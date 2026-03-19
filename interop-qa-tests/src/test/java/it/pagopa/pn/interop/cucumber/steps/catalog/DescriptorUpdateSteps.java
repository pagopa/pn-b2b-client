package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.utility.EServiceDescriptorUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class DescriptorUpdateSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;
    private final EServiceDescriptorUtils eServiceDescriptorUtils;

    public DescriptorUpdateSteps(ClientTokenConfigurator clientTokenConfigurator,
                                     SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.eServiceDescriptorUtils = new EServiceDescriptorUtils(clientTokenConfigurator, sharedStepsContext);
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

        final int attributesGroup = 0;

        List<List<UUID>> requiredCertifiedAttributes = sharedStepsContext.getAttributeCommonContext().getRequiredCertifiedAttributes();
        List<List<UUID>> requiredDeclaredAttributes = sharedStepsContext.getAttributeCommonContext().getRequiredDeclaredAttributes();
        List<List<UUID>> requiredVerifiedAttributes = sharedStepsContext.getAttributeCommonContext().getRequiredVerifiedAttributes();

        UUID attributeId = switch (attributeType) {
            case CERTIFIED -> requiredCertifiedAttributes.get(attributesGroup).get(attributeIndex);
            case DECLARED -> requiredDeclaredAttributes.get(attributesGroup).get(attributeIndex);
            case VERIFIED -> requiredVerifiedAttributes.get(attributesGroup).get(attributeIndex);
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

        Optional<DescriptorAttribute> certAttr = eServiceDescriptorUtils.getDescriptorAttribute(eServiceId, descriptorId, attributeId);

        Assertions.assertTrue(certAttr.isPresent());
        Assertions.assertEquals(attributeId, certAttr.get().getId());
        // TODO Threshold
        // Assertions.assertEquals(certAttr.get().getDailyCallsPerConsumer(), dailyCallsPerConsumer);
    }
}
