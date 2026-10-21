package it.pagopa.pn.interop.cucumber.steps.journey;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import it.pagopa.interop.authorization.domain.TenantType;
import it.pagopa.pn.interop.cucumber.steps.agreement.AgreementActivateSteps;
import it.pagopa.pn.interop.cucumber.steps.agreement.AgreementCommonSteps;
import it.pagopa.pn.interop.cucumber.steps.authorization.ClientPurposeAddSteps;
import it.pagopa.pn.interop.cucumber.steps.authorization.ClientPurposeRemoveStep;
import it.pagopa.pn.interop.cucumber.steps.purpose.PurposeCommonStep;

public class EServiceJourneySteps {

    private final AgreementCommonSteps agreementCommonSteps;
    private final AgreementActivateSteps agreementActivateSteps;
    private final PurposeCommonStep purposeCommonStep;
    private final ClientPurposeAddSteps clientPurposeAddSteps;
    private final ClientPurposeRemoveStep clientPurposeRemoveStep;

    public EServiceJourneySteps(
            AgreementCommonSteps agreementCommonSteps,
            AgreementActivateSteps agreementActivateSteps,
            PurposeCommonStep purposeCommonStep,
            ClientPurposeAddSteps clientPurposeAddSteps,
            ClientPurposeRemoveStep clientPurposeRemoveStep
    ) {
        this.clientPurposeRemoveStep = clientPurposeRemoveStep;
        this.agreementActivateSteps = agreementActivateSteps;
        this.purposeCommonStep = purposeCommonStep;
        this.clientPurposeAddSteps = clientPurposeAddSteps;
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

    @Given("{tenantType} pubblica un e-service e {tenantType} inoltra una finalità che associa al client e si ripete il flusso {int} volte")
    public void journeyEServiceAndPurposeAndClientAssociation(TenantType producer, TenantType consumer, int times) {
        for (int i = 0; i < times; i++) {
            agreementActivateSteps.tenantHasAlreadyCreateEservice(producer.name(), "PUBLISHED", "AUTOMATIC", 400, 800);
            agreementCommonSteps.tenantAlreadyHasFruitionRequestWithState(consumer.name(), "ACTIVE");
            purposeCommonStep.tenantHasAlreadyCreateFinalizationWithStatus(consumer.name(), 1, "ACTIVE");
            clientPurposeAddSteps.userRetrievesFinalization();
        }
    }
}
