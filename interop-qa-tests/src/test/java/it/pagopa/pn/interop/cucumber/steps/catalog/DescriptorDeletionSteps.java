package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;

public class DescriptorDeletionSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;

    public DescriptorDeletionSteps(ClientTokenConfigurator clientTokenConfigurator,
                                   SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente cancella il descrittore di quell'e-service")
    public void userDeleteEserviceDescriptor() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(() -> clientTokenConfigurator.getEServiceClient()
                .deleteDraft(sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        sharedStepsContext.getEServicesCommonContext().getDescriptorId()));
    }

    @Then("il descrittore è stato cancellato, e anche l'eservice")
    public void descriptorAndEserviceCancelled() {
        sharedStepsContext.getPollingService().makePolling(
                () -> httpCallExecutor.performCall(() -> clientTokenConfigurator.getProducerClient().getProducerEServiceDescriptor(
                                sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                                sharedStepsContext.getEServicesCommonContext().getDescriptorId()
                        )
                ),
                res -> res == HttpStatus.NOT_FOUND,
                "There was an error while retrieving the e-service descriptor"
        );
        sharedStepsContext.getPollingService().makePolling(
                () -> httpCallExecutor.performCall(() -> clientTokenConfigurator.getProducerClient().getProducerEServiceDetails(
                                sharedStepsContext.getEServicesCommonContext().getEserviceId()
                        )
                ),
                res -> res == HttpStatus.NOT_FOUND,
                "There was an error while retrieving e-service details"
        );
    }

    @Then("l'ultimo descrittore in stato DRAFT è stato cancellato")
    @Then("l'ultimo descrittore in stato WAITING_FOR_APPROVAL è stato cancellato")
    public void lastDraftDescriptorCancelled() {
        sharedStepsContext.getPollingService().makePolling(
                () -> httpCallExecutor.performCall(() -> clientTokenConfigurator.getProducerClient().getProducerEServiceDescriptor(
                                sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                                sharedStepsContext.getEServicesCommonContext().getDescriptorId()
                        )
                ),
                res -> res == HttpStatus.NOT_FOUND,
                "There was an error while retrieving the e-service descriptor"
        );
        sharedStepsContext.getEServicesCommonContext()
                .setDescriptorId(sharedStepsContext.getEServicesCommonContext().getOldDescriptorId());
    }

    @Then("quell'e-service non è stato cancellato")
    public void verifyEserviceNotCancelled() throws InterruptedException {
        // We don't have an exact way to assert that the eService "doesn't get deleted".
        // We can only check if it still exists after a reasonable time.
        Thread.sleep(3000);
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getProducerClient().getProducerEServiceDetails(sharedStepsContext.getEServicesCommonContext().getEserviceId())
        );
        Assertions.assertNotEquals(HttpStatus.NOT_FOUND, httpCallExecutor.getResponseStatus());
    }
}
