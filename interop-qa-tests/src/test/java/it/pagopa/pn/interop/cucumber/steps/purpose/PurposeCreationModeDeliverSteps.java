package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionState;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormSeed;
import it.pagopa.interop.purpose.domain.RiskAnalysis;
import it.pagopa.interop.purpose.domain.TEServiceMode;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.PurposeCommonContext;

import java.util.Random;
import java.util.UUID;

public class PurposeCreationModeDeliverSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final DataPreparationService dataPreparationService;

    private String purposeTitle;

    public PurposeCreationModeDeliverSteps(ClientTokenConfigurator clientTokenConfigurator,
                                           SharedStepsContext sharedStepsContext,
                                           DataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.dataPreparationService = dataPreparationService;
    }

    @When("l'utente crea una nuova finalità per quell'e-service con tutti i campi richiesti correttamente formattati")
    public void createPurposeWithAllRequiredFields() {
        UUID consumerId = identityService.getOrganizationId(sharedStepsContext.getTenantType());
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().createPurpose(
                        new PurposeSeed()
                                .eserviceId(sharedStepsContext.getEServicesCommonContext().getEserviceId())
                                .consumerId(consumerId)
                                .title(String.format("purpose title - QA - %d -%d", sharedStepsContext.getTestSeed(), new Random().nextInt()))
                                .description("description of the purpose - QA")
                                .isFreeOfCharge(true)
                                .freeOfChargeReason("free of charge - QA")
                                .dailyCalls(49)
                )
        );
    }

    @Given("{string} ha già creato una finalità per quell'e-service con tutti i campi richiesti correttamente formattati")
    public void tenantHasAlreadyCreatedPurposeWithAllRequiredFields() {
        clientTokenConfigurator.setBearerToken(identityService.getToken(sharedStepsContext.getTenantType(), null));
        UUID consumerId = identityService.getOrganizationId(sharedStepsContext.getTenantType());

        String title = String.format("purpose title - QA - %d - %d", sharedStepsContext.getTestSeed(), new Random().nextInt());
        dataPreparationService.createPurposeWithGivenState(
                sharedStepsContext.getTestSeed(),
                EServiceMode.DELIVER,
                PurposeVersionState.DRAFT,
                TEServiceMode.builder()
                        .title(title)
                        .eserviceId(sharedStepsContext.getEServicesCommonContext().getEserviceId())
                        .consumerId(consumerId)
                        .build()
        );
        purposeTitle = title;
    }

    @When("l'utente crea una nuova finalità per quell'e-service con tutti i campi richiesti correttamente formattati, in modalità gratuita senza specificare una ragione")
    public void userCreatePurposeWithAllRequiredFieldsAndSameName() {
        UUID consumerId = identityService.getOrganizationId(sharedStepsContext.getTenantType());
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().createPurpose(
                        new PurposeSeed()
                                .eserviceId(sharedStepsContext.getEServicesCommonContext().getEserviceId())
                                .consumerId(consumerId)
                                .title(purposeTitle)
                                .description("description of the purpose - QA")
                                .isFreeOfCharge(true)
                                .freeOfChargeReason("free of charge - QA")
                                .dailyCalls(49)
                )
        );
    }

    @When("l'utente crea una nuova finalità per quell'e-service con tutti i campi richiesti correttamente formattati, in modalità gratuita senza specificare una ragione")
    public void userCreatePurposeInFreeModeWithoutReason() {
        UUID consumerId = identityService.getOrganizationId(sharedStepsContext.getTenantType());
        String title = String.format("purpose title - QA - %d - %d", sharedStepsContext.getTestSeed(), new Random().nextInt());

        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().createPurpose(
                        new PurposeSeed()
                                .eserviceId(sharedStepsContext.getEServicesCommonContext().getEserviceId())
                                .consumerId(consumerId)
                                .title(title)
                                .description("description of the purpose - QA")
                                .isFreeOfCharge(true)
                                .freeOfChargeReason(null)
                                .dailyCalls(49)
                )
        );
    }

    @When("l'utente crea una nuova finalità per quell'e-service con tutti i campi richiesti correttamente formattati, con un'analisi del rischio parzialmente compilata ma formattata correttamente")
    public void userCreatePurposeWithPartialRiskAnalysis() {
        UUID consumerId = identityService.getOrganizationId(sharedStepsContext.getTenantType());
        RiskAnalysis riskAnalysis = dataPreparationService.getRiskAnalysis("PA1", false);
        String title = String.format("purpose title - QA - %d - %d", sharedStepsContext.getTestSeed(), new Random().nextInt());

        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().createPurpose(
                        new PurposeSeed()
                                .eserviceId(sharedStepsContext.getEServicesCommonContext().getEserviceId())
                                .consumerId(consumerId)
                                .title(title)
                                .description("description of the purpose - QA")
                                .isFreeOfCharge(true)
                                .freeOfChargeReason("free of charge - QA")
                                .dailyCalls(49)
                                .riskAnalysisForm(new RiskAnalysisFormSeed()
                                        .answers(riskAnalysis.getRiskAnalysisForm().getAnswers())
                                        .version(riskAnalysis.getRiskAnalysisForm().getVersion())
                                )
                )
        );
    }

    @When("l'utente crea una nuova finalità per quell'e-service con tutti i campi richiesti correttamente formattati, con un'analisi del rischio parzialmente compilata, formattata correttamente, ma con un template datato")
    public void userCreatePurposeWihtPartialRiskAnalysisAndOutdatedTemplate() {
        String tenantType = sharedStepsContext.getTenantType();
        UUID consumerId = identityService.getOrganizationId(tenantType);
        RiskAnalysis riskAnalysis = dataPreparationService.getRiskAnalysis(tenantType, false);

        int outdatedVersion = Integer.parseInt(riskAnalysis.getRiskAnalysisForm().getVersion()) - 1;
        String title = String.format("purpose title - QA - %d - %d", sharedStepsContext.getTestSeed(), new Random().nextInt());

        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().createPurpose(
                        new PurposeSeed()
                                .eserviceId(sharedStepsContext.getEServicesCommonContext().getEserviceId())
                                .consumerId(consumerId)
                                .title(title)
                                .description("description of the purpose - QA")
                                .isFreeOfCharge(true)
                                .freeOfChargeReason("free of charge - QA")
                                .dailyCalls(49)
                                .riskAnalysisForm(new RiskAnalysisFormSeed()
                                        .answers(riskAnalysis.getRiskAnalysisForm().getAnswers())
                                        .version(String.valueOf(outdatedVersion))
                                )
                )
        );
    }

}
