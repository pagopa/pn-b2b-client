package it.pagopa.pn.interop.cucumber.steps.probing;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.probing.model.SearchEserviceContent;
import it.pagopa.interop.generated.openapi.clients.probing.model.SearchProducerNameResponse;
import it.pagopa.interop.probing.service.impl.ProbingClient;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.probing.model.ProbingContext;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;

import java.util.List;

public class ProbingSteps {
    private final IHttpExecutor httpCallExecutor;
    private final ProbingClient probingClient;
    private final ProbingContext probingContext;
    private final SharedStepsContext sharedStepsContext;

    public ProbingSteps(ProbingClient probingClient, SharedStepsContext sharedStepsContext) {
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.probingClient = probingClient;
        probingClient.setHttpCallExecutor(httpCallExecutor);
        this.probingContext = new ProbingContext();
    }

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
    @When("recupero la lista dei producers con limit {int} e offset {int} e producerName {string}")
    public void getProducersWithProducerName(Integer limit, Integer offset, String producerName) {
        List<SearchProducerNameResponse> producer = probingClient.getEservicesProducers(limit, offset, producerName);
        Assertions.assertThat(producer).as("La lista dei producer non deve essere null").isNotNull();
    }

    @When("recupero la lista dei producers con limit {string} e offset {string}")
    public void getProducersWith(String limit, String offset) {
        Integer limitValue = parseNullableInteger(limit);
        Integer offsetValue = parseNullableInteger(offset);
        List<SearchProducerNameResponse> producer = probingClient.getEservicesProducers(limitValue, offsetValue, null);
        Assertions.assertThat(producer).as("La lista dei producer non deve essere null").isNotNull();
    }

    private Integer parseNullableInteger(String value) {
        if (value == null || value.equalsIgnoreCase("null")) {
            return null;
        }
        return Integer.valueOf(value);
    }

}

