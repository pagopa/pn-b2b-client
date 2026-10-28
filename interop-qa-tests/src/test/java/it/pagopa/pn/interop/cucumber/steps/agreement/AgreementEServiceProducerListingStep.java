package it.pagopa.pn.interop.cucumber.steps.agreement;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementState;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactEServiceLight;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactEServicesLight;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

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
        getProducerEServiceListWithName(operation, eServiceRef, null);
    }

    @Then("^l'utente recupera la lista di e-service in erogazione e (non |)contiene il (vecchio|nuovo) e-service creato con filtro:$")
    public void getProducerEServiceListWithName(String operation, String eServiceRef, DataTable data) {
        List<AgreementState> stateFilterList;
        String namePart;
        if (data != null) {
            Map<String, String> filterMap = data.asMap(String.class, String.class);
            stateFilterList = Arrays.stream(filterMap.get("agreementState").split(","))
                    .map(AgreementState::valueOf)
                    .toList();
            namePart = filterMap.getOrDefault("namePart", null);
        } else {
            stateFilterList = new ArrayList<>();
            namePart = null;
        }

        String checkName = ("vecchio".equals(eServiceRef)) ?
                sharedStepsContext.getEServicesCommonContext().getOldName() :
                sharedStepsContext.getEServicesCommonContext().getName();

        IHttpExecutor httpCallExecutor = sharedStepsContext.getHttpCallExecutor();

        // Recupera il conteggio degli e-service da controllare basato sul filtro scelto
        // offset avrà uno scarto di 10 e-service, tra max limit 50 e -40 al conteggio totale
        // perché facendo più tentativo, se viene creato qualche nuovo e-service, non si perderà
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getAgreementClient().getAgreementEServiceProducers(0, 1, namePart, stateFilterList)
        );
        Assertions.assertEquals(HttpStatus.valueOf(200), httpCallExecutor.getResponseStatus());
        int count = ((ResponseEntity<CompactEServicesLight>) httpCallExecutor.getResponse()).getBody().getPagination().getTotalCount();
        int offset = Math.max((count - 40), 0);

        PollingService.makePolling(
                () -> clientTokenConfigurator.getAgreementClient().getAgreementEServiceProducers(offset, 50, namePart, stateFilterList),
                res -> {
                    List<CompactEServiceLight> list = res.getBody().getResults();
                    boolean found = false;
                    for (CompactEServiceLight item: list) {
                        if (checkName.equals(item.getName())) {
                            found = true; break;
                        }
                    }
                    if (operation.startsWith("non")) {
                        return !found;
                    } else {
                        return found;
                    }
                },
                "Wrong visibility for the expected e-service: " + checkName,
                3,
                3000
        );
    }
}
