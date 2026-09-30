package it.pagopa.pn.interop.cucumber.steps.journey;

import io.cucumber.java.en.And;
import it.pagopa.pn.interop.cucumber.steps.agreement.AgreementCommonSteps;
import it.pagopa.pn.interop.cucumber.steps.authorization.ClientPurposeRemoveStep;
import it.pagopa.pn.interop.cucumber.steps.purpose.PurposeCommonStep;

public class EServiceJourneySteps {

    private final AgreementCommonSteps agreementCommonSteps;
    private final PurposeCommonStep purposeCommonStep;
    private final ClientPurposeRemoveStep clientPurposeRemoveStep;

    public EServiceJourneySteps(
            AgreementCommonSteps agreementCommonSteps,
            PurposeCommonStep purposeCommonStep,
            ClientPurposeRemoveStep clientPurposeRemoveStep
    ) {
        this.clientPurposeRemoveStep = clientPurposeRemoveStep;
        this.purposeCommonStep = purposeCommonStep;
        this.agreementCommonSteps = agreementCommonSteps;
    }

    @And("l'admin dell'erogatore {string} ha creato un eservice e l'admin del fruitore {string} ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client")
    public void createEserviceAndPurpose(String tenantErogatore, String tenantFruitore) {
        agreementCommonSteps.tenantHasAlreadyCreatedAndPublishedEService(tenantErogatore, 1);
        agreementCommonSteps.tenantAlreadyHasFruitionRequestWithState(tenantFruitore, "ACTIVE");
        purposeCommonStep.tenantHasAlreadyCreateFinalizationWithStatus(tenantFruitore, 1, "ACTIVE");
        clientPurposeRemoveStep.addPurposeToClient(tenantFruitore);
    }

    @And("l'admin dell'erogatore {string} ha creato un eservice e l'admin del fruitore {string} ha creato una richiesta di fruizione per quell'eservice e ha associato una finalità in stato {string} a quel client")
    public void createEserviceAndPurpose(String tenantErogatore, String tenantFruitore, String statoPurpose) {
        agreementCommonSteps.tenantHasAlreadyCreatedAndPublishedEService(tenantErogatore, 1);
        agreementCommonSteps.tenantAlreadyHasFruitionRequestWithState(tenantFruitore, "ACTIVE");
        purposeCommonStep.tenantHasAlreadyCreateFinalizationWithStatus(tenantFruitore, 1, statoPurpose);
        clientPurposeRemoveStep.addPurposeToClient(tenantFruitore);
    }

    @And("l'admin dell'erogatore {string} ha creato un eservice di tipo {isAsynchronous} e l'admin del fruitore {string} ha creato una richiesta di fruizione per quell'eservice e ha associato una finalità in stato {string} a quel client")
    public void createEserviceAndPurpose(String tenantErogatore, Boolean isAsync, String tenantFruitore, String statoPurpose) {
        agreementCommonSteps.tenantHasAlreadyCreatedAndPublishedEServiceWithAsyncExchange(tenantErogatore, 1, isAsync);
        agreementCommonSteps.tenantAlreadyHasFruitionRequestWithState(tenantFruitore, "ACTIVE");
        purposeCommonStep.tenantHasAlreadyCreateFinalizationWithStatus(tenantFruitore, 1, statoPurpose);
        clientPurposeRemoveStep.addPurposeToClient(tenantFruitore);
    }
}
