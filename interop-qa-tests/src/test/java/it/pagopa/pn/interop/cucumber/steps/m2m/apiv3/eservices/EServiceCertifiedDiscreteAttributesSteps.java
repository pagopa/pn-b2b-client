package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.eservices;

import io.cucumber.java.en.Given;
import it.pagopa.interop.attribute.service.IM2MV3CertifiedDiscreteAttributeClient;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.eservice.service.IM2MV3EServiceAttributeClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeKind;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.*;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.agreement.model.EServiceAttributeSpec;
import it.pagopa.pn.interop.cucumber.steps.common.AttributeCommonContext;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.*;

public class EServiceCertifiedDiscreteAttributesSteps {

    private final IHttpExecutor httpExecutor;
    private final SharedStepsContext sharedStepsContext;
    private final IM2MV3EServiceAttributeClient eServiceAttributeClient;
    private final IM2MV3CertifiedDiscreteAttributeClient certifiedDiscreteAttributeClient;

    public EServiceCertifiedDiscreteAttributesSteps(
        ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext
    ) {
        this.sharedStepsContext = sharedStepsContext;
        this.httpExecutor = sharedStepsContext.getHttpCallExecutor();
        this.eServiceAttributeClient = clientTokenConfigurator.getM2mV3EServiceAttributeClient();
        this.certifiedDiscreteAttributeClient = clientTokenConfigurator.getM2mV3CertifiedDiscreteAttributeClient();
        this.certifiedDiscreteAttributeClient.setHttpCallExecutor(httpExecutor);
    }

    @Given("l'utente aggiunge i seguenti attributi al descrittore dell'e-service:")
    public void addCertifiedDiscreteAttributesToEServiceDescriptor(List<EServiceAttributeSpec> attributesSpec) {

        List<List<CertifiedDiscreteAttribute>> assignedAttributes = sharedStepsContext.getAttributeCommonContext().getCertifiedDiscreteAssigned();

        Map<Integer, EServiceDescriptorCertifiedDiscreteAttributesGroupSeed> groups = new TreeMap<>();

        attributesSpec.stream()
                .sorted(Comparator.comparing(EServiceAttributeSpec::getGroup))
                .forEach(attributeSpec -> {

            // Questa implementazione considera soltanto gli attributi certificati discreti.
            // Potrebbe essere necessario supportare anche le altre tipologie di attributo.
            if (Objects.requireNonNull(attributeSpec.getKind()) != AttributeKind.CERTIFIED_DISCRETE) {
                throw new UnsupportedOperationException("Unsupported attribute kind: %s".formatted(attributeSpec.getKind()));
            }

            CertifiedDiscreteAttribute certifiedDiscreteAttribute = this.createCertifiedDiscreteAttribute(attributeSpec);
            Assertions.assertNotNull(certifiedDiscreteAttribute, "Certified discrete attribute must be created before attaching it to the e-service descriptor");

            EServiceAttributeCertifiedDiscreteConfigSeed configSeed = new EServiceAttributeCertifiedDiscreteConfigSeed();
            configSeed.setComparator(
                    AttributeCertifiedDiscreteComparator.fromValue(attributeSpec.getComparator().getValue())
            );
            configSeed.setThreshold(attributeSpec.getValue());

            EServiceDescriptorCertifiedDiscreteAttributesGroupSeedAttributesInner attributeSeed = new EServiceDescriptorCertifiedDiscreteAttributesGroupSeedAttributesInner();
            attributeSeed.setId(certifiedDiscreteAttribute.getId());
            attributeSeed.setDiscreteConfig(configSeed);

            groups.computeIfAbsent(attributeSpec.getGroup(), groupIndex -> new EServiceDescriptorCertifiedDiscreteAttributesGroupSeed())
                    .addAttributesItem(attributeSeed);

            // Store in context
            while (assignedAttributes.size() <= attributeSpec.getGroup()) {
                assignedAttributes.add(new ArrayList<>());
            }
            assignedAttributes.get(attributeSpec.getGroup()).add(certifiedDiscreteAttribute);
        });

        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();

        groups.forEach((groupIndex, attributesGroupSeed) -> {
            httpExecutor.performCall(
                    () -> this.eServiceAttributeClient.createEServiceDescriptorCertifiedDiscreteAttributesGroup(
                            eServiceId, descriptorId, attributesGroupSeed)
            );

            assert httpExecutor.getResponseStatus().is2xxSuccessful();

            EServiceDescriptorCertifiedDiscreteAttributesGroup attributesGroup = (EServiceDescriptorCertifiedDiscreteAttributesGroup) httpExecutor.getResponse();
        });
    }

    @Given("la configurazione degli attributi certificati discreti del descrittore dell'eservice corrisponde a quella attesa")
    public void checkCertifiedDiscreteAttributesAgainstExpected() {
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        AttributeCommonContext context = sharedStepsContext.getAttributeCommonContext();

        List<EServiceDescriptorCertifiedDiscreteAttribute> actualAttributes = fetchAllCertifiedDiscreteAttributes(eServiceId, descriptorId);

        Map<Integer, List<UUID>> expectedIdsByGroup = new TreeMap<>();
        for (int groupIndex = 0; groupIndex < context.getCertifiedDiscreteAssigned().size(); groupIndex++) {
            List<CertifiedDiscreteAttribute> assignedGroup = context.getCertifiedDiscreteAssigned().get(groupIndex);
            expectedIdsByGroup.computeIfAbsent(groupIndex, ignored -> new ArrayList<>())
                    .addAll(assignedGroup.stream().map(CertifiedDiscreteAttribute::getId).toList());
        }

        Map<Integer, List<UUID>> actualIdsByGroup = new TreeMap<>();
        for (EServiceDescriptorCertifiedDiscreteAttribute item : actualAttributes) {
            Assertions.assertNotNull(item.getAttribute(), "Attribute payload must not be null");
            actualIdsByGroup.computeIfAbsent(item.getGroupIndex(), ignored -> new ArrayList<>())
                    .add(item.getAttribute().getId());
        }

        Assertions.assertEquals(
                expectedIdsByGroup.keySet(),
                actualIdsByGroup.keySet(),
                "Groups returned by API should match the expected configured groups"
        );

        expectedIdsByGroup.forEach((groupIndex, expectedIds) -> {
            List<UUID> actualIds = actualIdsByGroup.get(groupIndex);
            Assertions.assertNotNull(actualIds, "Missing group " + groupIndex + " in API response");
            Assertions.assertEquals(expectedIds.size(), actualIds.size(),
                    "Number of attributes in group " + groupIndex + " does not match expected value");
            Assertions.assertTrue(actualIds.containsAll(expectedIds),
                    "Attributes in group " + groupIndex + " do not match the expected ones");
        });
    }

    @Given("l'utente tenta di recuperare gli attributi certificati discreti del descrittore dell'eservice")
    public void getCertifiedDiscreteAttributes() {
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();

        httpExecutor.performCall(() -> this.eServiceAttributeClient.getEServiceDescriptorCertifiedDiscreteAttributes(
                eServiceId, descriptorId, 0, 50
        ));
    }

    @Given("l'utente tenta di recuperare gli attributi certificati discreti del descrittore dell'eservice specificando un ID {entityIdType} per l'e-service")
    public void getCertifiedDiscreteAttributesWithInvalidEServiceId(EntityIdType entityIdType) {
        UUID eServiceId = generateId(entityIdType);
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();

        httpExecutor.performCall(() -> this.eServiceAttributeClient.getEServiceDescriptorCertifiedDiscreteAttributes(
                eServiceId, descriptorId, 0, 50
        ));
    }

    @Given("l'utente tenta di recuperare gli attributi certificati discreti del descrittore dell'eservice specificando un ID {entityIdType} per il descrittore dell'e-service")
    public void getCertifiedDiscreteAttributesWithInvalidDescriptorId(EntityIdType entityIdType) {
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = generateId(entityIdType);

        httpExecutor.performCall(() -> this.eServiceAttributeClient.getEServiceDescriptorCertifiedDiscreteAttributes(
                eServiceId, descriptorId, 0, 50
        ));
    }

    private List<EServiceDescriptorCertifiedDiscreteAttribute> fetchAllCertifiedDiscreteAttributes(UUID eServiceId, UUID descriptorId) {
        List<EServiceDescriptorCertifiedDiscreteAttribute> allAttributes = new ArrayList<>();
        int offset = 0;
        int limit = 50;

        while (true) {
            final int currentOffset = offset;
            final int currentLimit = limit;

            httpExecutor.performCall(() -> this.eServiceAttributeClient.getEServiceDescriptorCertifiedDiscreteAttributes(
                    eServiceId,
                    descriptorId,
                    currentOffset,
                    currentLimit
            ));

            Assertions.assertTrue(
                    httpExecutor.getResponseStatus().is2xxSuccessful(),
                    "Expected successful retrieval of e-service certified discrete attributes"
            );

            EServiceDescriptorCertifiedDiscreteAttributes response = (EServiceDescriptorCertifiedDiscreteAttributes) httpExecutor.getResponse();
            Assertions.assertNotNull(response, "Response must not be null");

            List<EServiceDescriptorCertifiedDiscreteAttribute> pageResults = response.getResults();
            Assertions.assertNotNull(pageResults, "Attributes list in response must not be null");
            allAttributes.addAll(pageResults);

            Pagination pagination = response.getPagination();
            if (allAttributes.size() >= pagination.getTotalCount() || pageResults.size() < limit) {
                break;
            }

            offset += pageResults.size();
        }

        return allAttributes;
    }

    private CertifiedDiscreteAttribute createCertifiedDiscreteAttribute(EServiceAttributeSpec attributeSpec) {
        CertifiedDiscreteAttributeSeed seed = new CertifiedDiscreteAttributeSeed();
        int millis = Instant.now().get(ChronoField.MILLI_OF_SECOND);
        String attrName = "attribute-%d-%s".formatted(sharedStepsContext.getTestSeed() + millis, attributeSpec.getKind());
        seed.setName(attrName);
        seed.setCode(attributeSpec.getCode());
        seed.setDescription("description of %s".formatted(attrName));
        return certifiedDiscreteAttributeClient.create(seed);
    }

    private UUID generateId(EntityIdType entityIdType) {
        return switch (entityIdType){
            case INVALID_ID -> UUID.fromString("0-0-0-0-0");
            case NON_EXISTENT_ID -> UUID.randomUUID();
            default -> throw new IllegalStateException("Tipo di id non supportato: " + entityIdType.name());
        };
    }
}
