package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.eservices;

import io.cucumber.java.en.When;
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

    /**
     * Crea e aggiunge gli attributi al descrittore dell'e-service.
     *
     * @param attributesSpec Lista di attributi da aggiungere al descrittore dell'e-service. Il campo group è a base zero.
     */
    @When("l'utente crea e aggiunge i seguenti attributi al descrittore dell'e-service:")
    public void createAndAddAttributesToEServiceDescriptor(List<EServiceAttributeSpec> attributesSpec) {

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

            Assertions.assertTrue(httpExecutor.getResponseStatus().is2xxSuccessful());

            EServiceDescriptorCertifiedDiscreteAttributesGroup attributesGroup = (EServiceDescriptorCertifiedDiscreteAttributesGroup) httpExecutor.getResponse();
        });
    }

    @When("l'utente tenta di associare l'attributo certificato discreto creato all'e-service")
    public void associateCertifiedDiscreteAttribute() {

        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();

        associateLastCertifiedDiscreteAttributePublished(eServiceId, descriptorId, null);
    }

    /**
     *
     * @param groupIndex is a zero-based index of the group to associate the attribute to e-service descriptor
     */
    @When("l'utente tenta di associare l'attributo certificato discreto creato al gruppo {int} dell'e-service")
    public void associateCertifiedDiscreteAttributeToGroup(int groupIndex) {
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();

        this.associateLastCertifiedDiscreteAttributePublished(eServiceId, descriptorId, groupIndex);

        if (httpExecutor.getResponseStatus().is2xxSuccessful()) {
            // Update group in context
            List<CertifiedDiscreteAttribute> publishedAttributes = sharedStepsContext.getAttributeCommonContext().getCertifiedDiscretePublished();
            CertifiedDiscreteAttribute certifiedDiscreteAttribute = publishedAttributes.get(publishedAttributes.size() - 1);
            List<List<CertifiedDiscreteAttribute>> assignedAttributes = sharedStepsContext.getAttributeCommonContext().getCertifiedDiscreteAssigned();

            while (assignedAttributes.size() <= groupIndex) {
                assignedAttributes.add(new ArrayList<>());
            }
            assignedAttributes.get(groupIndex).add(certifiedDiscreteAttribute);
        }
    }

    @When("l'utente tenta di associare un attributo certificato discreto all'e-service senza specificare alcun parametro")
    public void associateCertifiedDiscreteAttributeWithoutParameters() {
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();

        EServiceDescriptorCertifiedDiscreteAttributesGroupSeed attributesGroupSeed = new EServiceDescriptorCertifiedDiscreteAttributesGroupSeed();

        httpExecutor.performCall(
                () -> this.eServiceAttributeClient.createEServiceDescriptorCertifiedDiscreteAttributesGroup(
                        eServiceId, descriptorId, attributesGroupSeed)
        );
    }

    @When("l'utente tenta di associare l'attributo certificato discreto creato all'e-service senza specificare i parametri necessari")
    public void associateCertifiedDiscreteAttributeWithMissingParameters() {
        List<CertifiedDiscreteAttribute> publishedAttributes = sharedStepsContext.getAttributeCommonContext().getCertifiedDiscretePublished();

        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        CertifiedDiscreteAttribute lastPublishedAttribute = publishedAttributes.get(publishedAttributes.size() - 1);

        EServiceDescriptorCertifiedDiscreteAttributesGroupSeed attributesGroupSeed = new EServiceDescriptorCertifiedDiscreteAttributesGroupSeed();
        EServiceDescriptorCertifiedDiscreteAttributesGroupSeedAttributesInner attributeSeed = new EServiceDescriptorCertifiedDiscreteAttributesGroupSeedAttributesInner();
        attributeSeed.setId(lastPublishedAttribute.getId());
        attributeSeed.setDiscreteConfig(new EServiceAttributeCertifiedDiscreteConfigSeed());
        attributesGroupSeed.addAttributesItem(attributeSeed);

        httpExecutor.performCall(
                () -> this.eServiceAttributeClient.createEServiceDescriptorCertifiedDiscreteAttributesGroup(
                        eServiceId, descriptorId, attributesGroupSeed)
        );
    }

    @When("l'utente tenta di associare un attributo certificato discreto specificando un ID {entityIdType} per l'attributo")
    public void associateCertifiedDiscreteAttributeWithInvalidAttributeId(EntityIdType entityIdType) {
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();

        EServiceDescriptorCertifiedDiscreteAttributesGroupSeed attributesGroupSeed = new EServiceDescriptorCertifiedDiscreteAttributesGroupSeed();
        EServiceDescriptorCertifiedDiscreteAttributesGroupSeedAttributesInner attributeSeed = new EServiceDescriptorCertifiedDiscreteAttributesGroupSeedAttributesInner();
        attributeSeed.setId(UUID.randomUUID());
        attributeSeed.setDiscreteConfig(new EServiceAttributeCertifiedDiscreteConfigSeed());
        attributesGroupSeed.addAttributesItem(attributeSeed);

        httpExecutor.performCall(
                () -> this.eServiceAttributeClient.createEServiceDescriptorCertifiedDiscreteAttributesGroup(
                        eServiceId, descriptorId, attributesGroupSeed)
        );
    }

    @When("la configurazione degli attributi certificati discreti del descrittore dell'e-service corrisponde a quella attesa")
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

    @When("l'utente tenta di associare l'attributo certificato discreto creato specificando un e-service ID {entityIdType}")
    public void associateCertifiedDiscreteAttributeWithInvalidEServiceId(EntityIdType entityIdType) {
        UUID eServiceId = generateId(entityIdType);
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();

        associateLastCertifiedDiscreteAttributePublished(eServiceId, descriptorId, null);
    }

    @When("l'utente tenta di associare l'attributo certificato discreto creato specificando un descriptor ID {entityIdType}")
    public void associateCertifiedDiscreteAttributeWithInvalidDescriptorId(EntityIdType entityIdType) {
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = generateId(entityIdType);

        associateLastCertifiedDiscreteAttributePublished(eServiceId, descriptorId, null);
    }

    @When("l'utente tenta di associare l'attributo certificato discreto creato al gruppo {int} dell'e-service specificando un ID {entityIdType} per il descrittore dell'e-service")
    public void associateCertifiedDiscreteAttributeToGroupWithInvalidEServiceId(int groupIndex, EntityIdType entityIdType) {
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = generateId(entityIdType);
        associateLastCertifiedDiscreteAttributePublished(eServiceId, descriptorId, groupIndex);
    }

    @When("l'utente tenta di recuperare gli attributi certificati discreti del descrittore dell'e-service")
    public void getCertifiedDiscreteAttributes() {
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();

        httpExecutor.performCall(() -> this.eServiceAttributeClient.getEServiceDescriptorCertifiedDiscreteAttributes(
                eServiceId, descriptorId, 0, 50
        ));
    }

    @When("l'utente tenta di recuperare gli attributi certificati discreti del descrittore dell'e-service specificando un ID {entityIdType} per l'e-service")
    public void getCertifiedDiscreteAttributesWithInvalidEServiceId(EntityIdType entityIdType) {
        UUID eServiceId = generateId(entityIdType);
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();

        httpExecutor.performCall(() -> this.eServiceAttributeClient.getEServiceDescriptorCertifiedDiscreteAttributes(
                eServiceId, descriptorId, 0, 50
        ));
    }

    @When("l'utente tenta di recuperare gli attributi certificati discreti del descrittore dell'e-service specificando un ID {entityIdType} per il descrittore dell'e-service")
    public void getCertifiedDiscreteAttributesWithInvalidDescriptorId(EntityIdType entityIdType) {
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = generateId(entityIdType);

        httpExecutor.performCall(() -> this.eServiceAttributeClient.getEServiceDescriptorCertifiedDiscreteAttributes(
                eServiceId, descriptorId, 0, 50
        ));
    }

    @When("l'utente tenta di rimuovere l'attributo certificato discreto {int} associato al gruppo {int} dell'e-service")
    public void removeCertifiedDiscreteAttributeFromGroup(int attributeIndex, int groupIndex) {
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        List<List<CertifiedDiscreteAttribute>> assignedAttributes = sharedStepsContext.getAttributeCommonContext().getCertifiedDiscreteAssigned();
        UUID attributeId = (groupIndex >= 0 && groupIndex < assignedAttributes.size()) ?
            assignedAttributes.get(groupIndex).get(attributeIndex).getId() :
            // This is a special case where the group index is out of bounds,
            // but we need to retrieve a valid attribute ID.
            assignedAttributes.get(0).get(0).getId();

        httpExecutor.performCall(
                () -> this.eServiceAttributeClient.deleteEServiceDescriptorCertifiedDiscreteAttributeFromGroup(
                        eServiceId, descriptorId, groupIndex, attributeId
                )
        );

        if (httpExecutor.getResponseStatus().is2xxSuccessful()) {
            assignedAttributes.get(groupIndex).remove(attributeIndex);
        }
    }

    @When("l'utente tenta di rimuovere l'attributo certificato discreto {int} associato al gruppo {int} dell'e-service specificando un ID {entityIdType} per l'e-service")
    public void removeCertifiedDiscreteAttributeFromGroupWithInvalidEServiceId(int attributeIndex, int groupIndex, EntityIdType entityIdType) {
        UUID eServiceId = generateId(entityIdType);
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        List<List<CertifiedDiscreteAttribute>> assignedAttributes = sharedStepsContext.getAttributeCommonContext().getCertifiedDiscreteAssigned();
        UUID attributeId = assignedAttributes.get(groupIndex).get(attributeIndex).getId();

        httpExecutor.performCall(
                () -> this.eServiceAttributeClient.deleteEServiceDescriptorCertifiedDiscreteAttributeFromGroup(
                        eServiceId, descriptorId, groupIndex, attributeId
                )
        );
    }

    @When("l'utente tenta di rimuovere l'attributo certificato discreto {int} associato al gruppo {int} dell'e-service specificando un ID {entityIdType} per il descrittore dell'e-service")
    public void removeCertifiedDiscreteAttributeFromGroupWithInvalidDescriptorId(int attributeIndex, int groupIndex, EntityIdType entityIdType) {
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = generateId(entityIdType);
        List<List<CertifiedDiscreteAttribute>> assignedAttributes = sharedStepsContext.getAttributeCommonContext().getCertifiedDiscreteAssigned();
        UUID attributeId = assignedAttributes.get(groupIndex).get(attributeIndex).getId();

        httpExecutor.performCall(
                () -> this.eServiceAttributeClient.deleteEServiceDescriptorCertifiedDiscreteAttributeFromGroup(
                        eServiceId, descriptorId, groupIndex, attributeId
                )
        );
    }

    @When("l'utente tenta di rimuovere l'attributo certificato discreto {int} associato al gruppo {int} dell'e-service specificando un ID {entityIdType} per l'attributo precedentemente associato")
    public void removeCertifiedDiscreteAttributeFromGroupWithInvalidEServiceAndDescriptorId(int attributeIndex, int groupIndex, EntityIdType entityIdType) {
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        UUID attributeId = generateId(entityIdType);

        httpExecutor.performCall(
                () -> this.eServiceAttributeClient.deleteEServiceDescriptorCertifiedDiscreteAttributeFromGroup(
                        eServiceId, descriptorId, groupIndex, attributeId
                )
        );
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

    private void associateLastCertifiedDiscreteAttributePublished(UUID eServiceId, UUID descriptorId, Integer groupIndex) {
        List<CertifiedDiscreteAttribute> publishedAttributes = sharedStepsContext.getAttributeCommonContext().getCertifiedDiscretePublished();
        CertifiedDiscreteAttribute lastPublishedAttribute = publishedAttributes.get(publishedAttributes.size() - 1);

        EServiceDescriptorCertifiedDiscreteAttributesGroupSeed attributesGroupSeed = new EServiceDescriptorCertifiedDiscreteAttributesGroupSeed();
        EServiceDescriptorCertifiedDiscreteAttributesGroupSeedAttributesInner attributeSeed = new EServiceDescriptorCertifiedDiscreteAttributesGroupSeedAttributesInner();
        attributeSeed.setId(lastPublishedAttribute.getId());
        EServiceAttributeCertifiedDiscreteConfigSeed configSeed = new EServiceAttributeCertifiedDiscreteConfigSeed();
        configSeed.setComparator(AttributeCertifiedDiscreteComparator.GT);
        configSeed.setThreshold(100);
        attributeSeed.setDiscreteConfig(configSeed);
        attributesGroupSeed.addAttributesItem(attributeSeed);

        if (groupIndex == null) {
            httpExecutor.performCall(
                    () -> this.eServiceAttributeClient.createEServiceDescriptorCertifiedDiscreteAttributesGroup(
                            eServiceId, descriptorId, attributesGroupSeed)
            );
        } else {
            httpExecutor.performCall(
                    () -> this.eServiceAttributeClient.assignEServiceDescriptorCertifiedDiscreteAttributesToGroup(
                            eServiceId, descriptorId, groupIndex, attributesGroupSeed)
            );
        }
    }

    private UUID generateId(EntityIdType entityIdType) {
        return switch (entityIdType){
            case INVALID_ID -> UUID.fromString("0-0-0-0-0");
            case NON_EXISTENT_ID -> UUID.randomUUID();
            default -> throw new IllegalStateException("Tipo di id non supportato: " + entityIdType.name());
        };
    }
}
