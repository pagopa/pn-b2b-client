package it.pagopa.pn.interop.cucumber.steps.probing;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.generated.openapi.clients.probing.model.SearchEserviceContent;
import it.pagopa.interop.probing.service.IProbingClient;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.probing.model.ProbingContext;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class ProbingSteps {
    private final HttpCallExecutor httpCallExecutor;
    private final IProbingClient probingClient;
    private final ProbingContext probingContext;
    private final SharedStepsContext sharedStepsContext;

    @And("il microservizio {string} risulta attivo")
    public void getStatus(String ms) {
        final int maxTry = 30;          // ~30 secondi
        final long sleepMs = 1_000L;    // 1 secondo

        switch (ms) {
            case "probing-api" -> PollingService.makePolling(
                    () -> {
                        probingClient.getProbingApiHealthStatus();
                        return httpCallExecutor.getResponseStatus();
                    },
                    HttpStatus::is2xxSuccessful, // STOP quando è online
                    "Il ms " + ms + " dovrebbe risultare attivo",
                    maxTry,
                    sleepMs
            );

            case "probing-statistics-api" -> PollingService.makePolling(
                    () -> {
                        probingClient.getStatisticsHealthStatus();
                        return httpCallExecutor.getResponseStatus();
                    },
                    HttpStatus::is2xxSuccessful, // STOP quando è online
                    "Il ms " + ms + " dovrebbe risultare attivo",
                    maxTry,
                    sleepMs
            );

            default -> throw new IllegalArgumentException("Il microservizio " + ms + " non esiste");
        }
    }

    @When("viene recuperato l'intero catalogo degli e-service relativo a probing")
    public void findProbingEservice() {
        probingContext.setActual(probingClient.getAllEservice());
    }

    @Then("l'eservice creato è presente nei risultati")
    public void probingEserviceIsPresent() {
        final String eserviceName = getEserviceName();
        Assertions.assertThat(eserviceName).as("Il nome dell'eservice non deve essere nullo").isNotNull();

        boolean found = probingContext.getActual().stream().map(SearchEserviceContent::getEserviceName).anyMatch(eserviceName::equals);
        Assertions.assertThat(found).as("L'eservice '" + eserviceName + "' deve essere presente").isTrue();
    }

    @When("viene recuperato nel catalogo di probing l'eservice creato filtrando per {string}")
    public void findProbingEserviceBy(String filter) {
        switch (filter) {
            case "name" -> {
                final String eserviceName = getEserviceName();
                probingContext.setActual(probingClient.findEserviceByName(eserviceName));
            }
            case "producer" -> {
                final String producer = getEserviceProducer();
                probingContext.setActual(probingClient.findEserviceByProducer(producer));
            }
            default -> throw new IllegalArgumentException("Filtro non supportato: " + filter);
        }
    }

    private String getEserviceName() {
        return sharedStepsContext.getEServicesCommonContext().getName();
    }

    private String getEserviceProducer() {
        return sharedStepsContext.getTenantType();
    }
}

