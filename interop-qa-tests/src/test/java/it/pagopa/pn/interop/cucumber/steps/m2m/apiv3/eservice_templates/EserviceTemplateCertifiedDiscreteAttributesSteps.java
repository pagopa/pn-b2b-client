package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.eservice_templates;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.attribute.service.IM2MV3CertifiedDiscreteAttributeClient;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.e_service_template.IM2MV3EServiceTemplateAttributeClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeKind;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.*;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.agreement.model.EServiceAttributeSpec;
import it.pagopa.pn.interop.cucumber.steps.common.AttributeCommonContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServiceTemplateInfo;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.*;

public class EserviceTemplateCertifiedDiscreteAttributesSteps {

    private final IHttpExecutor httpExecutor;
    private final SharedStepsContext sharedStepsContext;
    private final IM2MV3EServiceTemplateAttributeClient eServiceTemplateAttributeClient;
    private final IM2MV3CertifiedDiscreteAttributeClient certifiedDiscreteAttributeClient;

    public EserviceTemplateCertifiedDiscreteAttributesSteps(
        ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext
    ) {
        this.sharedStepsContext = sharedStepsContext;
        this.httpExecutor = sharedStepsContext.getHttpCallExecutor();
        this.eServiceTemplateAttributeClient = clientTokenConfigurator.getM2mV3EServiceTemplateAttributeClient();
        this.certifiedDiscreteAttributeClient = clientTokenConfigurator.getM2mV3CertifiedDiscreteAttributeClient();
        this.certifiedDiscreteAttributeClient.setHttpCallExecutor(httpExecutor);
    }

    /**
    * Crea e aggiunge gli attributi all'ultima versione del template e-service.
    *
    * @param attributesSpec Lista di attributi da aggiungere all'ultima versione del template e-service. Il campo group è a base zero.
    */
    @When("l'utente crea e aggiunge i seguenti attributi all'e-service template creato:")
    public void addAttributesToEServiceTemplate(List<EServiceAttributeSpec> attributesSpec) {

        List<List<CertifiedDiscreteAttribute>> assignedAttributes = sharedStepsContext.getAttributeCommonContext().getCertifiedDiscreteAssigned();

        Map<Integer, EServiceTemplateVersionCertifiedDiscreteAttributesGroupSeed> groups = new TreeMap<>();

        attributesSpec.stream()
            .sorted(Comparator.comparing(EServiceAttributeSpec::getGroup))
            .forEach(attributeSpec -> {

                // Questa implementazione considera soltanto gli attributi certificati discreti.
                // Potrebbe essere necessario supportare anche le altre tipologie di attributo.
                if (Objects.requireNonNull(attributeSpec.getKind()) != AttributeKind.CERTIFIED_DISCRETE) {
                    throw new UnsupportedOperationException("Unsupported attribute kind: %s".formatted(attributeSpec.getKind()));
                }

                CertifiedDiscreteAttribute certifiedDiscreteAttribute = this.createCertifiedDiscreteAttribute(attributeSpec);
                Assertions.assertNotNull(certifiedDiscreteAttribute, "Certified discrete attribute must be created before attaching it to the template e-service");

                EServiceAttributeCertifiedDiscreteConfigSeed configSeed = new EServiceAttributeCertifiedDiscreteConfigSeed();
                configSeed.setComparator(
                    AttributeCertifiedDiscreteComparator.fromValue(attributeSpec.getComparator().getValue())
                );
                configSeed.setThreshold(attributeSpec.getValue());

                EServiceDescriptorCertifiedDiscreteAttributesGroupSeedAttributesInner attributeSeed = new EServiceDescriptorCertifiedDiscreteAttributesGroupSeedAttributesInner();
                attributeSeed.setId(certifiedDiscreteAttribute.getId());
                attributeSeed.setDiscreteConfig(configSeed);

                groups.computeIfAbsent(attributeSpec.getGroup(), groupIndex -> new EServiceTemplateVersionCertifiedDiscreteAttributesGroupSeed())
                    .addAttributesItem(attributeSeed);

                // Store in context
                while (assignedAttributes.size() <= attributeSpec.getGroup()) {
                    assignedAttributes.add(new ArrayList<>());
                }
                assignedAttributes.get(attributeSpec.getGroup()).add(certifiedDiscreteAttribute);
            });

        EServiceTemplateInfo templateInfo = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged();
        UUID templateEServiceId = templateInfo.getId();
        UUID templateVersionId = templateInfo.getLastVersionId();

        groups.forEach((groupIndex, attributesGroupSeed) -> {
            httpExecutor.performCall(
                () -> this.eServiceTemplateAttributeClient.createEServiceTemplateVersionCertifiedDiscreteAttributesGroup(
                                templateEServiceId, templateVersionId, attributesGroupSeed)
            );

            Assertions.assertTrue(httpExecutor.getResponseStatus().is2xxSuccessful());

            EServiceTemplateVersionCertifiedDiscreteAttributesGroup attributesGroup = (EServiceTemplateVersionCertifiedDiscreteAttributesGroup) httpExecutor.getResponse();
        });
    }

    @When("la configurazione degli attributi certificati discreti del template e-service corrisponde a quella attesa")
    public void checkCertifiedDiscreteAttributesAgainstExpected() {
        EServiceTemplateInfo templateInfo = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged();
        UUID templateId = templateInfo.getId();
        UUID versionId = templateInfo.getLastVersionId();
        AttributeCommonContext context = sharedStepsContext.getAttributeCommonContext();

        List<EServiceTemplateVersionCertifiedDiscreteAttribute> actualAttributes = fetchAllCertifiedDiscreteAttributes(templateId, versionId);

        Map<Integer, List<UUID>> expectedIdsByGroup = new TreeMap<>();
            for (int groupIndex = 0; groupIndex < context.getCertifiedDiscreteAssigned().size(); groupIndex++) {
                List<CertifiedDiscreteAttribute> assignedGroup = context.getCertifiedDiscreteAssigned().get(groupIndex);
                expectedIdsByGroup.computeIfAbsent(groupIndex, ignored -> new ArrayList<>())
                    .addAll(assignedGroup.stream().map(CertifiedDiscreteAttribute::getId).toList());
            }

            Map<Integer, List<UUID>> actualIdsByGroup = new TreeMap<>();
            for (EServiceTemplateVersionCertifiedDiscreteAttribute item : actualAttributes) {
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

    @When("l'utente tenta di recuperare gli attributi certificati discreti del template e-service")
    public void getCertifiedDiscreteAttributes() {
        EServiceTemplateInfo templateInfo = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged();
        UUID templateId = templateInfo.getId();
        UUID versionId = templateInfo.getLastVersionId();

        httpExecutor.performCall(() -> this.eServiceTemplateAttributeClient.getEServiceTemplateVersionCertifiedDiscreteAttributes(
                templateId, versionId, 0, 50
        ));
    }

    @When("l'utente tenta di recuperare gli attributi certificati discreti del template e-service specificando un ID {entityIdType} per il template")
    public void getCertifiedDiscreteAttributesWithInvalidTemplateId(EntityIdType entityIdType) {
        EServiceTemplateInfo templateInfo = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged();
        UUID templateId = templateInfo.getId();
        UUID versionId = templateInfo.getLastVersionId();
        httpExecutor.performCall(() -> this.eServiceTemplateAttributeClient.getEServiceTemplateVersionCertifiedDiscreteAttributes(
                templateId, versionId, 0, 50
        ));
    }

    /**
     *
     * @param groupIndex is a zero-based index of the group to associate the attribute to e-service template
     */
    @When("l'utente tenta di associare l'attributo certificato discreto creato al gruppo {int} del template e-service")
    public void associateCertifiedDiscreteAttributeToGroup(int groupIndex) {
        EServiceTemplateInfo templateInfo = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged();
        UUID templateId = templateInfo.getId();
        UUID versionId = templateInfo.getLastVersionId();

        this.associateLastCertifiedDiscreteAttributePublished(templateId, versionId, groupIndex);

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

    private CertifiedDiscreteAttribute createCertifiedDiscreteAttribute(EServiceAttributeSpec attributeSpec) {
        CertifiedDiscreteAttributeSeed seed = new CertifiedDiscreteAttributeSeed();
        int millis = Instant.now().get(ChronoField.MILLI_OF_SECOND);
        String attrName = "attribute-%d-%s".formatted(sharedStepsContext.getTestSeed() + millis, attributeSpec.getKind());
        seed.setName(attrName);
        seed.setCode(attributeSpec.getCode());
        seed.setDescription("description of %s".formatted(attrName));
        return certifiedDiscreteAttributeClient.create(seed);
    }

    private List<EServiceTemplateVersionCertifiedDiscreteAttribute> fetchAllCertifiedDiscreteAttributes(UUID templateId, UUID versionId) {
        List<EServiceTemplateVersionCertifiedDiscreteAttribute> allAttributes = new ArrayList<>();
        int offset = 0;
        int limit = 50;

        while (true) {
            final int currentOffset = offset;
            final int currentLimit = limit;

            httpExecutor.performCall(() -> this.eServiceTemplateAttributeClient.getEServiceTemplateVersionCertifiedDiscreteAttributes(
                    templateId, versionId, currentOffset, currentLimit
            ));

            Assertions.assertTrue(
                    httpExecutor.getResponseStatus().is2xxSuccessful(),
                    "Expected successful retrieval of template e-service certified discrete attributes"
            );

            EServiceTemplateVersionCertifiedDiscreteAttributes response = (EServiceTemplateVersionCertifiedDiscreteAttributes) httpExecutor.getResponse();
            Assertions.assertNotNull(response, "Response must not be null");

            List<EServiceTemplateVersionCertifiedDiscreteAttribute> pageResults = response.getResults();
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

    private void associateLastCertifiedDiscreteAttributePublished(UUID templateId, UUID versionId, Integer groupIndex) {
        List<CertifiedDiscreteAttribute> publishedAttributes = sharedStepsContext.getAttributeCommonContext().getCertifiedDiscretePublished();
        CertifiedDiscreteAttribute lastPublishedAttribute = publishedAttributes.get(publishedAttributes.size() - 1);

        EServiceTemplateVersionCertifiedDiscreteAttributesGroupSeed attributesGroupSeed = new EServiceTemplateVersionCertifiedDiscreteAttributesGroupSeed();
        EServiceDescriptorCertifiedDiscreteAttributesGroupSeedAttributesInner attributeSeed = new EServiceDescriptorCertifiedDiscreteAttributesGroupSeedAttributesInner();
        attributeSeed.setId(lastPublishedAttribute.getId());
        EServiceAttributeCertifiedDiscreteConfigSeed configSeed = new EServiceAttributeCertifiedDiscreteConfigSeed();
        configSeed.setComparator(AttributeCertifiedDiscreteComparator.GT);
        configSeed.setThreshold(100);
        attributeSeed.setDiscreteConfig(configSeed);
        attributesGroupSeed.addAttributesItem(attributeSeed);

        if (groupIndex == null) {
            httpExecutor.performCall(
                    () -> this.eServiceTemplateAttributeClient.createEServiceTemplateVersionCertifiedDiscreteAttributesGroup(
                            templateId, versionId, attributesGroupSeed
                    )
            );
        } else {
            httpExecutor.performCall(
                    () -> this.eServiceTemplateAttributeClient.assignEServiceTemplateVersionCertifiedDiscreteAttributesToGroup(
                            templateId, versionId, groupIndex, attributesGroupSeed
                    )
            );
        }
    }
}
