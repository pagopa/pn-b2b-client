package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.eservice.service.IM2MV3EserviceDescriptorClient;
import it.pagopa.interop.eservice.service.IM2MV3EserviceDescriptorClient.EServiceDescriptorAttributePatchRequest;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.agreement.AgreementActivateSteps;
import it.pagopa.pn.interop.cucumber.steps.agreement.model.EServiceAttributeSpec;
import it.pagopa.pn.interop.cucumber.steps.common.AttributeCommonContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.utility.EServiceDescriptorUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.*;

@Slf4j
public class DescriptorUpdateSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    BFFDataPreparationService dataPreparationService;
    private final IHttpExecutor httpCallExecutor;
    private final EServiceDescriptorUtils eServiceDescriptorUtils;
    private final IM2MV3EserviceDescriptorClient eserviceDescriptorClient;

    public DescriptorUpdateSteps(ClientTokenConfigurator clientTokenConfigurator,
                                 SharedStepsContext sharedStepsContext,
                                 BFFDataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.dataPreparationService = dataPreparationService;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.eServiceDescriptorUtils = new EServiceDescriptorUtils(clientTokenConfigurator, sharedStepsContext);
        eserviceDescriptorClient = clientTokenConfigurator.getM2mV3EserviceDescriptorClient();
        eserviceDescriptorClient.setHttpCallExecutor(httpCallExecutor);
    }

    @When("l'utente aggiorna alcuni parametri di quel descrittore")
    public void updateSomeDescriptorParams() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UpdateEServiceDescriptorSeed seed = new UpdateEServiceDescriptorSeed()
                .description("Questo è un e-service di test")
                .audience(List.of("api/v1"))
                .voucherLifespan(60)
                .dailyCallsPerConsumer(50)
                .dailyCallsTotal(2000)
                .agreementApprovalPolicy(AgreementApprovalPolicy.AUTOMATIC)
                .attributes(new DescriptorAttributesSeed());

        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().updateDraftDescriptor(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        sharedStepsContext.getEServicesCommonContext().getDescriptorId(),
                        seed
                )
        );
    }

    @When("l'utente aggiorna alcuni parametri di quel descrittore con:")
    public void updateSomeDescriptorParams(UpdateEServiceDescriptorSeed updateEServiceDescriptorSeed) throws InterruptedException {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().updateDraftDescriptor(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        sharedStepsContext.getEServicesCommonContext().getDescriptorId(),
                        updateEServiceDescriptorSeed
                )
        );
    }

    @When("l'utente aggiorna il descrittore dell'e-service con i seguenti attributi:")
    public void updateDescriptorWithAttributes(List<EServiceAttributeSpec> attributesSpec) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UpdateEServiceDescriptorSeed updateEServiceDescriptorSeed = createUpdateEServiceDescriptorSeedAndUpdateContext(
                sharedStepsContext, dataPreparationService, attributesSpec
        );

        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().updateDraftDescriptor(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        sharedStepsContext.getEServicesCommonContext().getDescriptorId(),
                        updateEServiceDescriptorSeed
                )
        );
    }

    @When("l'utente pubblica il descrittore dell'e-service con i seguenti attributi:")
    public void publishDescriptorWithAttributes(List<EServiceAttributeSpec> attributesSpec) {
        DescriptorAttributesSeed descriptorAttributesSeed = createDescriptorAttributesSeedAndUpdateContext(
                sharedStepsContext, dataPreparationService, attributesSpec
        );
        UpdateEServiceDescriptorQuotas seed = new UpdateEServiceDescriptorQuotas()
                .attributes(descriptorAttributesSeed)
                .voucherLifespan(60)
                .dailyCallsPerConsumer(50)
                .dailyCallsTotal(2000);
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().updateDescriptor(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        sharedStepsContext.getEServicesCommonContext().getDescriptorId(),
                        seed
                )
        );
    }

    @When("l'utente tenta di aggiornare l'attributo certificato discreto {int}-esimo del gruppo {int}-esimo con discrete comparator {string} e il discrete threshhold {int}")
    public void updatePublishedDescriptorWithAttributes(int attributeIndex, int groupIndex, String comparator, int threshold) {

        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();

        ProducerEServiceDescriptor eServiceDescriptor = clientTokenConfigurator.getProducerClient().getProducerEServiceDescriptor(eServiceId, descriptorId);

        List<List<DescriptorAttributeSeed>> certifiedAttributesSeed = sharedStepsContext.getAttributeCommonContext().mapAttributes(eServiceDescriptor.getAttributes().getCertified());
        List<List<DescriptorAttributeSeed>> declaredAttributesSeed = sharedStepsContext.getAttributeCommonContext().mapAttributes(eServiceDescriptor.getAttributes().getDeclared());
        List<List<DescriptorAttributeSeed>> verifiedAttributesSeed = sharedStepsContext.getAttributeCommonContext().mapAttributes(eServiceDescriptor.getAttributes().getVerified());

        certifiedAttributesSeed.get(groupIndex).get(attributeIndex).getDiscreteConfig().setComparator(AttributeCertifiedDiscreteComparator.valueOf(comparator));
        certifiedAttributesSeed.get(groupIndex).get(attributeIndex).getDiscreteConfig().setThreshold(threshold);

        DescriptorAttributesSeed attributesSeed = new DescriptorAttributesSeed()
            .certified(certifiedAttributesSeed)
            .declared(declaredAttributesSeed)
            .verified(verifiedAttributesSeed);

        eServiceDescriptorUtils.updateEServiceDescriptor(eServiceDescriptor, attributesSeed);
    }

    @When("l'utente aggiorna la durata del voucher e le soglie di carico di quel descrittore")
    public void updateVoucherLifespanAndCallsLimit() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UpdateEServiceDescriptorQuotas seed = new UpdateEServiceDescriptorQuotas()
                .voucherLifespan(60)
                .dailyCallsPerConsumer(50)
                .dailyCallsTotal(2000);
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().updateDescriptor(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        sharedStepsContext.getEServicesCommonContext().getDescriptorId(),
                        seed
                )
        );
    }

    @When("l'utente tenta di aggiungere una soglia differenziata di {int} per l'attributo {attributeKind} {int}-esimo creato")
    public void updateDailyCallsPerConsumer(int dailyCallsPerConsumer, AttributeKind attributeType, int attributeIndex) {
        updateDailyCallsPerConsumer(dailyCallsPerConsumer, attributeType, attributeIndex, 0);
    }

    @When("l'utente tenta di aggiungere una soglia differenziata di {int} per l'attributo {attributeKind} {int}-esimo creato nel gruppo {int}-esimo con m2m")
    public void updateDailyCallsPerConsumerWithM2M(int dailyCallsPerConsumer, AttributeKind attributeType, int attributeIndex, int groupIndex) {

        if (attributeType != AttributeKind.CERTIFIED) {
            throw new UnsupportedOperationException("L'attributo deve essere certificato");
        }

        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        UUID certifiedAttributeId = sharedStepsContext.getAttributeCommonContext().getRequiredCertifiedAttributes()
                .get(groupIndex).get(attributeIndex);
        EServiceDescriptorAttributePatchRequest seed = EServiceDescriptorAttributePatchRequest.builder()
                .dailyCallsPerConsumer(dailyCallsPerConsumer)
                .build();

        eserviceDescriptorClient.patchEServiceDescriptorCertifiedAttribute(
            eServiceId, descriptorId, groupIndex, certifiedAttributeId, seed
        );
    }

    @When("l'utente tenta di aggiungere una soglia differenziata di {int} per l'attributo {attributeKind} {int}-esimo creato nel gruppo {int}-esimo")
    public void updateDailyCallsPerConsumer(int dailyCallsPerConsumer, AttributeKind attributeType, int attributeIndex, int groupIndex) {

        List<List<UUID>> requiredCertifiedAttributes = sharedStepsContext.getAttributeCommonContext().getRequiredCertifiedAttributes();
        List<List<UUID>> requiredDeclaredAttributes = sharedStepsContext.getAttributeCommonContext().getRequiredDeclaredAttributes();
        List<List<UUID>> requiredVerifiedAttributes = sharedStepsContext.getAttributeCommonContext().getRequiredVerifiedAttributes();

        UUID attributeId = switch (attributeType) {
            case CERTIFIED -> requiredCertifiedAttributes.get(groupIndex).get(attributeIndex);
            case DECLARED -> requiredDeclaredAttributes.get(groupIndex).get(attributeIndex);
            case VERIFIED -> requiredVerifiedAttributes.get(groupIndex).get(attributeIndex);
            default -> throw new UnsupportedOperationException("Unsupported attribute kind: %s".formatted(attributeType));
        };

        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();

        ProducerEServiceDescriptor eServiceDescriptor = clientTokenConfigurator.getProducerClient().getProducerEServiceDescriptor(eServiceId, descriptorId);

        List<List<DescriptorAttributeSeed>> certifiedAttributesSeed = sharedStepsContext.getAttributeCommonContext().mapAttributes(eServiceDescriptor.getAttributes().getCertified());
        List<List<DescriptorAttributeSeed>> declaredAttributesSeed = sharedStepsContext.getAttributeCommonContext().mapAttributes(eServiceDescriptor.getAttributes().getDeclared());
        List<List<DescriptorAttributeSeed>> verifiedAttributesSeed = sharedStepsContext.getAttributeCommonContext().mapAttributes(eServiceDescriptor.getAttributes().getVerified());

        switch (attributeType) {
            case CERTIFIED -> sharedStepsContext.getAttributeCommonContext().setDailyPerConsumer(certifiedAttributesSeed, attributeId, dailyCallsPerConsumer);
            case DECLARED -> sharedStepsContext.getAttributeCommonContext().setDailyPerConsumer(declaredAttributesSeed, attributeId, dailyCallsPerConsumer);
            case VERIFIED -> sharedStepsContext.getAttributeCommonContext().setDailyPerConsumer(verifiedAttributesSeed, attributeId, dailyCallsPerConsumer);
        }

        DescriptorAttributesSeed attributesSeed = new DescriptorAttributesSeed()
                .certified(certifiedAttributesSeed)
                .declared(declaredAttributesSeed)
                .verified(verifiedAttributesSeed);

        eServiceDescriptorUtils.updateEServiceDescriptor(eServiceDescriptor, attributesSeed);
    }

    @When("la soglia differenziata per l'attributo {attributeKind} {int}-esimo creato nel gruppo {int}-esimo è uguale a {string}")
    public void checkDailyCallsPerConsumer(AttributeKind attributeType, Integer attributeIndex, Integer groupIndex, String dailyCallsPerConsumer) {
        checkDailyCallsPerConsumer(attributeType, attributeIndex, groupIndex, dailyCallsPerConsumer, null, null);
    }

    @When("la soglia differenziata per l'attributo {attributeKind} {int}-esimo creato nel gruppo {int}-esimo è uguale a {string}, mentre il discrete comparator è {string} e il discrete threshhold è uguale a {int}")
    public void checkDailyCallsPerConsumer(AttributeKind attributeType, Integer attributeIndex, Integer groupIndex, String dailyCallsPerConsumer, String discreteComparator, Integer discreteThreshold) {

        Integer expectedDailyCallsPerConsumer = dailyCallsPerConsumer.equals("%null") ? null : Integer.parseInt(dailyCallsPerConsumer);

        List<List<UUID>> requiredCertifiedAttributes = sharedStepsContext.getAttributeCommonContext().getRequiredCertifiedAttributes();
        List<List<UUID>> requiredDeclaredAttributes = sharedStepsContext.getAttributeCommonContext().getRequiredDeclaredAttributes();
        List<List<UUID>> requiredVerifiedAttributes = sharedStepsContext.getAttributeCommonContext().getRequiredVerifiedAttributes();

        UUID attributeId = switch (attributeType) {
            case CERTIFIED, CERTIFIED_DISCRETE -> requiredCertifiedAttributes.get(groupIndex).get(attributeIndex);
            case DECLARED -> requiredDeclaredAttributes.get(groupIndex).get(attributeIndex);
            case VERIFIED -> requiredVerifiedAttributes.get(groupIndex).get(attributeIndex);
            default -> throw new UnsupportedOperationException("Unsupported attribute kind: %s".formatted(attributeType));
        };

        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();

        DescriptorAttribute certAttr = eServiceDescriptorUtils.getDescriptorCertifiedAttribute(eServiceId, descriptorId, attributeId, expectedDailyCallsPerConsumer, groupIndex).orElse(null);

        Assertions.assertNotNull(certAttr);
        Assertions.assertEquals(attributeId, certAttr.getId());
        Assertions.assertEquals(certAttr.getDailyCallsPerConsumer(), expectedDailyCallsPerConsumer);

        if (discreteComparator != null || discreteThreshold != null) {
            Assertions.assertNotNull(certAttr.getDiscreteConfig());
            Assertions.assertEquals(certAttr.getDiscreteConfig().getComparator().getValue(), discreteComparator);
            Assertions.assertEquals(certAttr.getDiscreteConfig().getThreshold(), discreteThreshold);
        }
    }

    public static UpdateEServiceDescriptorSeed createUpdateEServiceDescriptorSeedAndUpdateContext(
            SharedStepsContext sharedStepsContext,
            BFFDataPreparationService dataPreparationService,
            List<EServiceAttributeSpec> attributesSpec
    ) {
        return new UpdateEServiceDescriptorSeed()
                .voucherLifespan(3600)
                .attributes(
                        createDescriptorAttributesSeedAndUpdateContext(sharedStepsContext, dataPreparationService, attributesSpec)
                )
                .agreementApprovalPolicy(AgreementApprovalPolicy.AUTOMATIC)
                .dailyCallsPerConsumer(100)
                .dailyCallsTotal(1000);
    }


    public static DescriptorAttributesSeed createDescriptorAttributesSeedAndUpdateContext(
            SharedStepsContext sharedStepsContext,
            BFFDataPreparationService dataPreparationService,
            List<EServiceAttributeSpec> attributesSpec
    ) {

        DescriptorAttributesSeed descriptorAttributesSeed = new DescriptorAttributesSeed();

        AttributeCommonContext attributeCommonContext = sharedStepsContext.getAttributeCommonContext();

        attributeCommonContext.getRequiredCertifiedAttributes().clear();
        attributeCommonContext.getRequiredDeclaredAttributes().clear();
        attributeCommonContext.getRequiredVerifiedAttributes().clear();

        for (int i = 0; i < attributesSpec.size(); i++) {
            EServiceAttributeSpec attributeSpec = attributesSpec.get(i);
            Attribute attribute;

            if (attributeSpec.getKind() != AttributeKind.CERTIFIED_DISCRETE) {
                int millis = Instant.now().get(ChronoField.MILLI_OF_SECOND);
                String attrName = "attribute-%d-%d-%s".formatted(2 * i, sharedStepsContext.getTestSeed() + millis, attributeSpec.getKind());
                attribute = dataPreparationService.createAttribute(attributeSpec.getKind(), attrName);
            } else {
                CertifiedDiscreteTenantAttribute ownedCertifiedDiscreteAttr = sharedStepsContext.getAttributeCommonContext().getOwnedCertifiedDiscreteAttributes().get(0);
                attribute = new Attribute();
                attribute.setId(ownedCertifiedDiscreteAttr.getId());
            }

            DescriptorAttributeSeed seed = new DescriptorAttributeSeed()
                    .explicitAttributeVerification(true)
                    .id(attribute.getId());

            int group = attributeSpec.getGroup();

            switch (attributeSpec.getKind()) {
                case CERTIFIED, CERTIFIED_DISCRETE -> {
                    if (attributeSpec.getDailyCallsPerConsumer() != null) {
                        seed.dailyCallsPerConsumer(attributeSpec.getDailyCallsPerConsumer());
                    }
                    if (attributeSpec.getKind() == AttributeKind.CERTIFIED_DISCRETE) {
                        seed.setDiscreteConfig(new EServiceAttributeCertifiedDiscreteConfig()
                                .comparator(attributeSpec.getComparator())
                                .threshold(attributeSpec.getValue()));
                    }
                    addAttributeSeedToGroup(descriptorAttributesSeed.getCertified(), group, seed);
                    addAttributeToGroup(attributeCommonContext.getRequiredCertifiedAttributes(), group, attribute.getId());
                }
                case DECLARED -> {
                    addAttributeSeedToGroup(descriptorAttributesSeed.getDeclared(), group, seed);
                    addAttributeToGroup(attributeCommonContext.getRequiredDeclaredAttributes(), group, attribute.getId());
                }
                case VERIFIED -> {
                    addAttributeSeedToGroup(descriptorAttributesSeed.getVerified(), group, seed);
                    addAttributeToGroup(attributeCommonContext.getRequiredVerifiedAttributes(), group, attribute.getId());
                }
            }
        }

        return descriptorAttributesSeed;
    }

    private static void addAttributeSeedToGroup(List<List<DescriptorAttributeSeed>> groups, int groupIndex, DescriptorAttributeSeed seed) {
        while (groups.size() <= groupIndex) {
            groups.add(new ArrayList<>());
        }
        groups.get(groupIndex).add(seed);
    }

    private static void addAttributeToGroup(List<List<UUID>> groups, int groupIndex, UUID uuid) {
        while (groups.size() <= groupIndex) {
            groups.add(new ArrayList<>());
        }
        groups.get(groupIndex).add(uuid);
    }
}
