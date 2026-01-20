package it.pagopa.pn.interop.cucumber.steps.probing;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.probing.model.ChangeEserviceStateRequest;
import it.pagopa.interop.generated.openapi.clients.probing.model.ChangeProbingStateRequest;
import it.pagopa.interop.generated.openapi.clients.probing.model.EserviceStateBE;
import it.pagopa.interop.generated.openapi.clients.probing.model.EserviceStateFE;
import it.pagopa.interop.generated.openapi.clients.probing.model.SearchEserviceContent;
import it.pagopa.interop.generated.openapi.clients.probing.model.SearchEserviceResponse;
import it.pagopa.interop.generated.openapi.clients.probing.model.SearchProducerNameResponse;
import it.pagopa.interop.probing.service.impl.ProbingClient;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.probing.model.ProbingContext;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static it.pagopa.pn.interop.cucumber.utility.StepParser.nullableBoolean;
import static it.pagopa.pn.interop.cucumber.utility.StepParser.nullableInteger;
import static it.pagopa.pn.interop.cucumber.utility.StepParser.parseNullableSafe;
import static it.pagopa.pn.interop.cucumber.utility.StepParser.singletonListNullable;
import static it.pagopa.pn.interop.cucumber.utility.StepParser.uuidOrRandomOrNull;

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

    @When("viene modificato lo stato di probing dell'e-service con id versione {string} in {string}")
    public void updateProbingStateWithEServiceVersionId(String versionId, String probingEnabled) {
        UUID eserviceUuid = getEserviceId();
        UUID versionUuid = uuidOrRandomOrNull(versionId);

        ChangeProbingStateRequest probingState = new ChangeProbingStateRequest()
                .probingEnabled(nullableBoolean(probingEnabled));

        probingClient.updateEserviceProbingState(eserviceUuid, versionUuid, probingState);
    }

    @When("viene modificato lo stato di probing dell'e-service con id {string} e versione valida in {string}")
    public void updateProbingStateWithEServiceId(String eserviceId, String probingEnabled) {
        UUID eserviceUuid = uuidOrRandomOrNull(eserviceId);
        UUID versionUuid = getEserviceVersion();

        ChangeProbingStateRequest probingState = new ChangeProbingStateRequest()
                .probingEnabled(nullableBoolean(probingEnabled));

        probingClient.updateEserviceProbingState(eserviceUuid, versionUuid, probingState);
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

    @When("viene modificato lo stato operativo dell'e-service con id versione {string} in {string}")
    public void updateOperationalStateWithEserviceVersionId(String versionId, String eserviceState) {
        UUID eserviceUuid = getEserviceId();
        UUID versionUuid = uuidOrRandomOrNull(versionId);

        ChangeEserviceStateRequest operationalState = new ChangeEserviceStateRequest()
                .eServiceState(parseNullableSafe(eserviceState, EserviceStateBE::fromValue));

        probingClient.updateEserviceState(eserviceUuid, versionUuid, operationalState);
    }

    @When("viene modificato lo stato operativo dell'e-service con id {string} e versione valida in {string}")
    public void updateOperationalStateWithEserviceId(String eserviceId, String eserviceState) {
        UUID eserviceUuid = uuidOrRandomOrNull(eserviceId);
        UUID versionUuid = getEserviceVersion();

        ChangeEserviceStateRequest operationalState = new ChangeEserviceStateRequest()
                .eServiceState(parseNullableSafe(eserviceState, EserviceStateBE::fromValue));

        probingClient.updateEserviceState(eserviceUuid, versionUuid, operationalState);
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
}
