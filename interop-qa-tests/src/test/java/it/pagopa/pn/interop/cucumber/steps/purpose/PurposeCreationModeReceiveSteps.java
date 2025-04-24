package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementApprovalPolicy;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributesSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeEServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class PurposeCreationModeReceiveSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final DataPreparationService dataPreparationService;

    public PurposeCreationModeReceiveSteps(ClientTokenConfigurator clientTokenConfigurator,
                                           SharedStepsContext sharedStepsContext,
                                           DataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.dataPreparationService = dataPreparationService;
    }

    @When("l'utente crea una nuova finalità con tutti i campi richiesti correttamente formattati per quell'e-service associando quella analisi del rischio creata dall'erogatore")
    public void createPurposeWithRiskAnalysisCreatedFromProducer() {
        UUID consumerId = identityService.getOrganizationId(sharedStepsContext.getTenantType());
        String title = String.format("purpose title - QA - %d - %d", sharedStepsContext.getTestSeed(), new Random().nextInt());
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().createPurposeForReceiveEservice(
                        new PurposeEServiceSeed()
                                .eserviceId(sharedStepsContext.getEServicesCommonContext().getEserviceId())
                                .consumerId(consumerId)
                                .riskAnalysisId(sharedStepsContext.getRiskAnalysisCommonContext().getRiskAnalysisId())
                                .title(title)
                                .description("description of the purpose - QA")
                                .isFreeOfCharge(true)
                                .freeOfChargeReason("free of charge - QA")
                                .dailyCalls(49)
                )
        );
    }

    @Given("{string} ha già creato un e-service in modalità RECEIVE in stato DRAFT che richiede quell'attributo certificato con approvazione {string}")
    public void tenantHasAlreadyCreatedEServiceWithModeReceiveAndDraftState(String tenantType, String approvalPolicy) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        EServiceDescriptor eServiceDescriptor = dataPreparationService.createEServiceAndDraftDescriptor(
                new EServiceSeed().mode(EServiceMode.RECEIVE),
                new UpdateEServiceDescriptorSeed()
                        .agreementApprovalPolicy(AgreementApprovalPolicy.valueOf(approvalPolicy))
                        .attributes(
                                new DescriptorAttributesSeed()
                                        .certified(List.of(List.of(new DescriptorAttributeSeed().id(sharedStepsContext.getAttributeCommonContext().getAttributeId())
                                                .explicitAttributeVerification(true))))
                        )
        );
        sharedStepsContext.getEServicesCommonContext().setEserviceId(eServiceDescriptor.getEServiceId());
        sharedStepsContext.getEServicesCommonContext().setDescriptorId(eServiceDescriptor.getDescriptorId());
    }

    @Given("{string} ha già creato un e-service in stato DRAFT in modalità RECEIVE con approvazione {string}")
    public void tenantHasAlreadyCreatedEServiceWithDraftStateAndModeReceiveWithApproval(String tenantType, String approvalPolicy) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        EServiceDescriptor eServiceDescriptor = dataPreparationService.createEServiceAndDraftDescriptor(
                new EServiceSeed().mode(EServiceMode.RECEIVE),
                new UpdateEServiceDescriptorSeed().agreementApprovalPolicy(AgreementApprovalPolicy.valueOf(approvalPolicy))
        );
        sharedStepsContext.getEServicesCommonContext().setEserviceId(eServiceDescriptor.getEServiceId());
        sharedStepsContext.getEServicesCommonContext().setDescriptorId(eServiceDescriptor.getDescriptorId());
    }

    @When("l'utente crea una nuova finalità per quell'e-service associando quella analisi del rischio creata dall'erogatore con tutti i campi richiesti correttamente formattati, in modalità gratuita senza specificare una ragione")
    public void userCreateNewPurposeWithRiskAnalysisCreatedByProducerWithoutFreeOfChargeReason() {
        UUID consumerId = identityService.getOrganizationId(sharedStepsContext.getTenantType());
        String title = String.format("purpose title - QA - %d - %d", sharedStepsContext.getTestSeed(), new Random().nextInt());
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().createPurposeForReceiveEservice(
                        new PurposeEServiceSeed()
                                .eserviceId(sharedStepsContext.getEServicesCommonContext().getEserviceId())
                                .consumerId(consumerId)
                                .riskAnalysisId(sharedStepsContext.getRiskAnalysisCommonContext().getRiskAnalysisId())
                                .title(title)
                                .description("description of the purpose - QA")
                                .isFreeOfCharge(true)
                                .freeOfChargeReason(null)
                                .dailyCalls(49)
                )
        );
    }

    @When("l'utente crea una nuova finalità per quell'e-service con tutti i campi richiesti correttamente formattati senza passare l'identificativo dell'analisi del rischio")
    public void userCreateNewPurposeWithRiskAnalysisCreatedByProducerWithoutRiskAnalysisId() {
        UUID consumerId = identityService.getOrganizationId(sharedStepsContext.getTenantType());
        String title = String.format("purpose title - QA - %d - %d", sharedStepsContext.getTestSeed(), new Random().nextInt());
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().createPurposeForReceiveEservice(
                        new PurposeEServiceSeed()
                                .eserviceId(sharedStepsContext.getEServicesCommonContext().getEserviceId())
                                .consumerId(consumerId)
                                .riskAnalysisId(null)
                                .title(title)
                                .description("description of the purpose - QA")
                                .isFreeOfCharge(true)
                                .freeOfChargeReason("free of charge - QA")
                                .dailyCalls(49)
                )
        );
    }

    @When("l'utente crea una nuova finalità per quell'e-service associando una analisi del rischio diversa da quelle create dall'erogatore")
    public void userCreateNewPurposeWithRiskAnalysisIdNotCreatedFromProducer() {
        UUID consumerId = identityService.getOrganizationId(sharedStepsContext.getTenantType());
        String title = String.format("purpose title - QA - %d - %d", sharedStepsContext.getTestSeed(), new Random().nextInt());
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().createPurposeForReceiveEservice(
                        new PurposeEServiceSeed()
                                .eserviceId(sharedStepsContext.getEServicesCommonContext().getEserviceId())
                                .consumerId(consumerId)
                                .riskAnalysisId(UUID.randomUUID())
                                .title(title)
                                .description("description of the purpose - QA")
                                .isFreeOfCharge(true)
                                .freeOfChargeReason("free of charge - QA")
                                .dailyCalls(49)
                )
        );
    }

}
