package it.pagopa.pn.interop.cucumber.steps.probing;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.probing.model.*;
import it.pagopa.interop.probing.service.impl.ProbingClient;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.probing.model.ProbingContext;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static it.pagopa.pn.interop.cucumber.utility.StepParser.*;

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
                    HttpStatus::is2xxSuccessful,
                    "Il ms " + ms + " dovrebbe risultare attivo",
                    maxTry,
                    sleepMs
            );

            case "probing-statistics-api" -> PollingService.makePolling(
                    () -> {
                        probingClient.getStatisticsHealthStatus();
                        return httpCallExecutor.getResponseStatus();
                    },
                    HttpStatus::is2xxSuccessful,
                    "Il ms " + ms + " dovrebbe risultare attivo",
                    maxTry,
                    sleepMs
            );

            default -> throw new IllegalArgumentException("Il microservizio " + ms + " non esiste");
        }
    }

    @When("viene recuperato l'intero catalogo degli e-service relativo a probing")
    public void findProbingEservice() {
        probingContext.setActualResults(probingClient.getAllEservice());
    }

    @Then("l'eservice creato è presente nei risultati")
    public void probingEserviceIsPresent() {
        final String eserviceName = getEserviceName();
        Assertions.assertThat(eserviceName).as("Il nome dell'eservice non deve essere nullo").isNotNull();

        boolean found = probingContext.getActualResults().stream()
                .map(SearchEserviceContent::getEserviceName)
                .anyMatch(eserviceName::equals);

        Assertions.assertThat(found).as("L'eservice '" + eserviceName + "' deve essere presente").isTrue();
    }

    @When("viene recuperato nel catalogo di probing l'eservice creato filtrando per {string}")
    public void findProbingEserviceBy(String filter) {
        switch (filter) {
            case "name" -> {
                final String eserviceName = getEserviceName();
                probingContext.setActualResults(probingClient.findEserviceByName(eserviceName));
            }
            case "producer" -> {
                final String producer = getEserviceProducer();
                probingContext.setActualResults(probingClient.findEserviceByProducer(producer));
            }
            default -> throw new IllegalArgumentException("Filtro non supportato: " + filter);
        }
    }

    @When("recupero la lista dei producers con limit {int} e offset {int} e producerName {string}")
    public void getProducersWithProducerName(Integer limit, Integer offset, String producerName) {
        List<SearchProducerNameResponse> producer = probingClient.getEservicesProducers(limit, offset, producerName);
        Assertions.assertThat(producer).as("La lista dei producer non deve essere null").isNotNull();
    }

    @When("recupero la lista dei producers con limit {string} e offset {string}")
    public void getProducersWithPagination(String limit, String offset) {
        Integer limitValue = nullableInteger(limit);
        Integer offsetValue = nullableInteger(offset);

        List<SearchProducerNameResponse> producer = probingClient.getEservicesProducers(limitValue, offsetValue, null);
        Assertions.assertThat(producer).as("La lista dei producer non deve essere null").isNotNull();
    }

    @When("viene modificato lo stato di probing dell'e-service creato in {string}")
    public void updateProbingState(String probingEnabled) {
        UUID eserviceId = getEserviceId();
        UUID versionId = getEserviceVersion();

        ChangeProbingStateRequest probingState = new ChangeProbingStateRequest()
                .probingEnabled(nullableBoolean(probingEnabled));

        probingClient.updateEserviceProbingState(eserviceId, versionId, probingState);
    }

    @When("vengono recuperati dal catalogo gli e-service con valori di paginazione limit {string} e offset {string} e filtro di tipo {string} con valore {string}")
    public void getEServiceCatalogWithPaginationAndFilters(String limit, String offset, String filter, String filterValue) {
        Integer limitValue = nullableInteger(limit);
        Integer offsetValue = nullableInteger(offset);

        SearchEserviceResponse eservice = switch (filter) {
            case "null" -> probingClient.searchEservices(limitValue, offsetValue, null, null, null, null);
            case "eServiceName" -> probingClient.searchEservices(limitValue, offsetValue, filterValue, null, null, null);
            case "producerName" -> probingClient.searchEservices(limitValue, offsetValue, null, filterValue, null, null);
            case "versionNumber" -> probingClient.searchEservices(limitValue, offsetValue, null, null, nullableInteger(filterValue), null);
            case "state" -> probingClient.searchEservices(limitValue, offsetValue, null, null, null,
                    singletonListNullable(filterValue, EserviceStateFE::fromValue));
            default -> throw new IllegalArgumentException("Filtro non supportato: " + filter);
        };

        Assertions.assertThat(eservice).as("La lista degli e-service non deve essere null").isNotNull();
    }

    @When("viene modificato lo stato operativo dell'e-service creato in {string}")
    public void updateOperationalState(String eserviceState) {
        UUID eserviceUuid = getEserviceId();
        UUID versionUuid = getEserviceVersion();

        ChangeEserviceStateRequest operationalState = new ChangeEserviceStateRequest()
                .eServiceState(parseNullableSafe(eserviceState, EserviceStateBE::fromValue));

        probingClient.updateEserviceState(eserviceUuid, versionUuid, operationalState);
    }

    @When("viene modificato lo stato di probing dell'e-service con id {string} e id versione {string} in {string}")
    public void updateProbingState(String eserviceId, String versionId, String probingEnabled) {
        UUID eserviceUuid = resolveEserviceId(eserviceId);
        UUID versionUuid  = resolveVersionId(versionId);

        ChangeProbingStateRequest probingState = new ChangeProbingStateRequest()
                .probingEnabled(nullableBoolean(probingEnabled));

        probingClient.updateEserviceProbingState(eserviceUuid, versionUuid, probingState);
    }

    @When("viene modificato lo stato operativo dell'e-service con id {string} e id versione {string} in {string}")
    public void updateOperationalState(String eserviceId, String versionId, String eserviceState) {
        UUID eserviceUuid = resolveEserviceId(eserviceId);
        UUID versionUuid  = resolveVersionId(versionId);

        ChangeEserviceStateRequest operationalState = new ChangeEserviceStateRequest()
                .eServiceState(parseNullableSafe(eserviceState, EserviceStateBE::fromValue));

        probingClient.updateEserviceState(eserviceUuid, versionUuid, operationalState);
    }

    @And("vengono settati i parametri di probing di default per l'e-service")
    public void setDefaultProbingParamsForEservice() {
        UUID eserviceUuid = resolveEserviceId("corretto");
        UUID versionUuid = resolveVersionId("corretto");
        OffsetDateTime now = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        // Valori di default riconoscibili per i test
        Integer defaultFrequency = 1;
        OffsetDateTime defaultStartDate = now.plusHours(1);
        OffsetDateTime defaultEndDate = now.plusHours(2);

        probingContext.setExpectedStartDate(defaultStartDate);
        probingContext.setExpectedEndDate(defaultEndDate);
        probingContext.setExpectedFrequency(defaultFrequency);

        probingClient.updateEserviceFrequency(eserviceUuid, versionUuid, defaultFrequency, defaultStartDate, defaultEndDate);
        assertProbingParams(204);
    }

    @When("aggiorno i parametri di probing dell'e-service con eserviceId {string} e versionId {string} impostando frequency {string}, startDate {string}, endDate {string}")
    public void updateEserviceFrequency(String eserviceId, String versionId, String frequency, String startDate, String endDate) {
        UUID eserviceUuid = resolveEserviceId(eserviceId);
        UUID versionUuid = resolveVersionId(versionId);

        Integer frequencyValue = resolveFrequencyToken(frequency);
        OffsetDateTime startValue = resolveDateToken(startDate, probingContext.getExpectedStartDate());
        OffsetDateTime endValue = resolveDateToken(endDate, probingContext.getExpectedEndDate());

        probingClient.updateEserviceFrequency(eserviceUuid, versionUuid, frequencyValue, startValue, endValue);
    }

    @And("se lo status code è {int} verifica che i parametri di probing recuperati coincidano con quelli attesi")
    public void assertProbingParams(int expectedStatusCode) {
        int actualStatusCode = httpCallExecutor.getResponseStatus().value();
        if (actualStatusCode != expectedStatusCode) return;

        Long eserviceRecordId = getEserviceRecordId();

        // 1) se la finestra attesa parte nel futuro, aspetta fino allo start (con cap)
        waitUntilExpectedWindowStarts(probingContext.getExpectedStartDate());

        // 2) calcola policy di polling in base a finestra/frequenza
        PollingPolicy policy = computePollingPolicy(
                probingContext.getExpectedStartDate(),
                probingContext.getExpectedEndDate(),
                probingContext.getExpectedFrequency()
        );

        PollingService.makePolling(
                () -> probingClient.getEserviceMainData(eserviceRecordId),
                resp -> {
                    if (!isProbingStateUpdated(resp)) return false;
                    probingContext.setActualFrequency(resp.getPollingFrequency());
                    return true;
                },
                "Errore durante il setting di parametri di probing l'e-service con eserviceRecordId '" + eserviceRecordId + "'",
                policy.maxTry(),
                policy.sleepMs()
        );
    }

    private String getEserviceName() {
        return sharedStepsContext.getEServicesCommonContext().getName();
    }

    private String getEserviceProducer() {
        return sharedStepsContext.getTenantType();
    }

    private UUID getEserviceId() {
        return sharedStepsContext.getEServicesCommonContext().getEserviceId();
    }

    private UUID getEserviceVersion() {
        return sharedStepsContext.getEServicesCommonContext().getDescriptorId();
    }

    private Long getEserviceRecordId(){
        final String eserviceName = getEserviceName();
        List<SearchEserviceContent> results = probingClient.findEserviceByName(eserviceName);

        if(results.size() != 1) throw new RuntimeException("Errore durante il recupero dell'eserviceRecordId per l'eservice '" + eserviceName + "'");
        return results.get(0).getEserviceRecordId();
    }

    private UUID resolveEserviceId(String eserviceId) {
        if(eserviceId == null || eserviceId.equalsIgnoreCase("null"))
            return null;

        return (eserviceId.equalsIgnoreCase("corretto"))
                ? getEserviceId()
                : uuidOrRandomOrNull(eserviceId);
    }

    private UUID resolveVersionId(String versionId) {
        if(versionId == null || versionId.equalsIgnoreCase("null"))
            return null;

        return (versionId.equalsIgnoreCase("corretto"))
                ? getEserviceVersion()
                : uuidOrRandomOrNull(versionId);
    }

    private Integer resolveFrequencyToken(String token) {
        if (token == null) return null;
        if (token.equalsIgnoreCase("keep")) return probingContext.getActualFrequency();
        if (token.equalsIgnoreCase("null")) return null;
        return nullableInteger(token);
    }

    private OffsetDateTime resolveDateToken(String token, OffsetDateTime current) {
        if (token == null) return null;

        if (token.equalsIgnoreCase("keep")) return current;
        if (token.equalsIgnoreCase("null")) return null;

        OffsetDateTime now = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        if (token.equalsIgnoreCase("now")) return now;

        // now+Nh / now-Nh (solo ore)
        String lower = token.toLowerCase();
        if (lower.startsWith("now+") && lower.endsWith("h")) {
            long hours = Long.parseLong(lower.substring(4, lower.length() - 1));
            return now.plusHours(hours);
        }
        if (lower.startsWith("now-") && lower.endsWith("h")) {
            long hours = Long.parseLong(lower.substring(4, lower.length() - 1));
            return now.minusHours(hours);
        }

        return OffsetDateTime.parse(token);
    }

    private boolean isProbingStateUpdated(MainDataEserviceResponse resp){
        return resp != null
                && resp.getPollingFrequency() != null
                && isWithinExpectedWindow(OffsetDateTime.now(), probingContext.getExpectedStartDate(), probingContext.getExpectedEndDate())
                && resp.getPollingFrequency().equals(probingContext.getExpectedFrequency());
    }

    public static boolean isWithinExpectedWindow(OffsetDateTime now, OffsetDateTime expectedStartDate, OffsetDateTime expectedEndDate) {
        if (now == null) {
            throw new IllegalArgumentException("now must not be null");
        }

        if (expectedStartDate != null && now.isBefore(expectedStartDate)) {
            return false;
        }

        if (expectedEndDate != null && now.isAfter(expectedEndDate)) {
            return false;
        }

        return true;
    }

    private void waitUntilExpectedWindowStarts(OffsetDateTime expectedStart) {
        if (expectedStart == null) return;

        OffsetDateTime now = OffsetDateTime.now();
        if (!now.isBefore(expectedStart)) return;

        // Attesa “di allineamento” allo start: cap per non addormentare troppo il test
        Duration toWait = Duration.between(now, expectedStart);

        // cap: max 10s
        Duration capped = toWait.compareTo(Duration.ofSeconds(10)) > 0 ? Duration.ofSeconds(10) : toWait;

        sleepQuietly(capped);
    }

    private PollingPolicy computePollingPolicy(OffsetDateTime expectedStart, OffsetDateTime expectedEnd, Integer expectedFrequency) {
        OffsetDateTime now = OffsetDateTime.now();

        // Deadline: se ho endDate, uso quella; altrimenti uso un fallback ragionevole (es. 30s)
        OffsetDateTime deadline = (expectedEnd != null) ? expectedEnd : now.plusSeconds(30);

        // Se la deadline è già passata, comunque concedi un minimo di tempo (es. 5s) per non avere maxTry=0
        if (deadline.isBefore(now)) {
            deadline = now.plusSeconds(5);
        }

        long totalMs = Duration.between(now, deadline).toMillis();

        // Sleep: guidato dalla frequency, con limiti.
        // Assunzione: expectedFrequency espressa in secondi
        long sleepMs;
        if (expectedFrequency == null || expectedFrequency <= 0) {
            sleepMs = 1_000L; // default
        } else {
            long freqMs = TimeUnit.SECONDS.toMillis(expectedFrequency.longValue());
            // polling ~ ogni metà periodo, ma con min/max
            sleepMs = Math.max(500L, Math.min(2_000L, freqMs / 2));
        }

        int maxTry = (int) Math.max(1, Math.ceil(totalMs / (double) sleepMs));

        // cap di sicurezza per non avere loop infiniti in casi strani (es. endDate molto avanti)
        maxTry = Math.min(maxTry, 120); // max 120 tentativi

        return new PollingPolicy(maxTry, sleepMs);
    }

    private void sleepQuietly(Duration d) {
        try {
            Thread.sleep(Math.max(0L, d.toMillis()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private record PollingPolicy(int maxTry, long sleepMs) {}
}
