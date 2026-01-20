package it.pagopa.pn.interop.cucumber.steps.probing;

import io.cucumber.java.en.And;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.probing.service.IProbingClient;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

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

}

