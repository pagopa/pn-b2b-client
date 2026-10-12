package it.pagopa.pn.interop.cucumber.steps.agreement;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactEServiceLight;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactEServicesLight;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class AgreementEServiceProducerListingStep {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;

    public AgreementEServiceProducerListingStep(ClientTokenConfigurator clientTokenConfigurator,
                                                SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
    }

    @When("l'utente richiede una operazione di listing degli e-services che hanno una richiesta di fruizione attiva")
    public void requireEServiceListingOperation() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient().getAgreementEServiceProducers(0, 50, null, List.of())
        );
    }

    @When("l'utente richiede una operazione di listing degli e-services che hanno una richiesta di fruizione attiva limitata ai primi {int} e-services")
    public void requireEServiceListingOperationWithLimit(int limit) {
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient().getAgreementEServiceProducers(0, limit, null, List.of())
        );
    }

    @When("l'utente richiede una operazione di listing degli e-services che hanno una richiesta di fruizione attiva con offset {int}")
    public void requireEServiceListingOperationWithOffset(int offset) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient().getAgreementEServiceProducers(
                        offset, 10, String.valueOf(sharedStepsContext.getTestSeed()), List.of()
                )
        );
    }

    @When("l'utente richiede una operazione di listing degli e-services che hanno una richiesta di fruizione attiva filtrando per la keyword {string}")
    public void requireEServiceListingOperationWithOffset(String keyword) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        String query = String.format("%s-%s", sharedStepsContext.getTestSeed(), keyword);
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient().getAgreementEServiceProducers(0, 10, query, List.of())
        );
    }

    @Then("^l'utente recupera la lista di e-service in erogazione e (non |)contiene il (vecchio|nuovo) e-service creato$")
    public void getProducerEServiceListWithName(String operation, String eServiceRef) {
        String expectedName = ("vecchio".equals(eServiceRef)) ?
                sharedStepsContext.getEServicesCommonContext().getOldName() :
                sharedStepsContext.getEServicesCommonContext().getName();

        IHttpExecutor httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getAgreementClient().getAgreementEServiceProducers(0, 10, expectedName, List.of())
        );
        Assertions.assertEquals(HttpStatus.valueOf(200), httpCallExecutor.getResponseStatus());
        List<CompactEServiceLight> list =
                ((ResponseEntity<CompactEServicesLight>) httpCallExecutor.getResponse()).getBody().getResults();

        boolean found = false;
        for (CompactEServiceLight item: list) {
            if (expectedName.equals(item.getName())) {
                found = true;
                break;
            }
        }
        if (operation.startsWith("non")) {
            Assertions.assertFalse(found, "Found not expected e-service: " + expectedName);
        } else {
            Assertions.assertTrue(found, "Not found expected e-service: " + expectedName);
        }
    }
}
