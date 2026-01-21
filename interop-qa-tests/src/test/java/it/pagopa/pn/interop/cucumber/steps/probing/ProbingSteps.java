package it.pagopa.pn.interop.cucumber.steps.probing;

import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.generated.openapi.clients.probing.model.SearchProducerNameResponse;
import it.pagopa.interop.probing.service.IProbingClient;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;

import java.util.List;

@RequiredArgsConstructor
public class ProbingSteps {
    private final HttpCallExecutor httpCallExecutor;
    private final IProbingClient probingClient;
    private final SharedStepsContext  sharedStepsContext;

    @And("il microservizio {string} risulta attivo")
    public void getStatus(String ms) {
        final int maxTry = 30;          // ~30 secondi
        final long sleepMs = 1_000L;    // 1 secondo

        switch (ms) {
            case "probing-api" ->
                    PollingService.makePolling(
                            () -> {
                                probingClient.getProbingApiHealthStatus();
                                return httpCallExecutor.getResponseStatus();
                            },
                            HttpStatus::is2xxSuccessful, // STOP quando è online
                            "Il ms " + ms + " dovrebbe risultare attivo",
                            maxTry,
                            sleepMs
                    );

            case "probing-statistics-api" ->
                    PollingService.makePolling(
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

