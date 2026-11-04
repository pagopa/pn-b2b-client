package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.external.registries.pt.model.PtDelegationStatus;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.external.registries.pt.model.PtInstitution;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.external.registries.pt.model.PtInstitutionsResponse;
import it.pagopa.pn.client.b2b.pa.service.IPnPtInstitutionsClient;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPtInstitutionsClientImpl;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@Slf4j
public class PtEntiDelegantiSteps {
    private static final String RAGIONE_SOCIALE = "ragioneSociale";
    private static final String CODICE_FISCALE = "codiceFiscale";
    private static final String TOKEN_RAGIONE_SOCIALE = "@ragioneSocialeSalvata";
    private static final String TOKEN_PARTE_RAGIONE_SOCIALE = "@parteRagioneSocialeSalvata";
    private static final String TOKEN_RAGIONE_SOCIALE_CASE_INVERTITO = "@ragioneSocialeSalvataCaseInvertito";
    private static final String TOKEN_CODICE_FISCALE = "@codiceFiscaleSalvato";
    private static final String TOKEN_VUOTO = "@vuoto";
    private static final String TOKEN_LUNGHEZZA = "@lunghezza_";
    private static final int MAX_LIMIT = 50;

    private final IPnPtInstitutionsClient ptInstitutionsClient;
    private final SharedSteps sharedSteps;

    private PtInstitutionsResponse lastResponse;
    private final List<PtInstitution> allReadInstitutions = new ArrayList<>();
    private final Map<String, String> savedValues = new HashMap<>();
    private String lastSearch;
    private PtDelegationStatus lastStatus;
    private Integer lastLimit;

    public PtEntiDelegantiSteps(PnPtInstitutionsClientImpl ptInstitutionsClient, SharedSteps sharedSteps) {
        this.ptInstitutionsClient = ptInstitutionsClient;
        this.sharedSteps = sharedSteps;
    }

    //TODO: funzionalità di creazione/revoca deleghe PA→PT non ancora disponibile. Gli step che le richiamano sono commentati nella feature
//    @Given("l'Ente PA {string} crea una delega per l'Ente PT {string}")
//    public void createPtDelegation(String paName, String ptName) {
//    }
//
//    @Given("l'Ente PA {string} revoca la delega per l'Ente PT {string}")
//    public void revokePtDelegation(String paName, String ptName) {
//    }

    @When("l'Ente PT {string} richiede l'elenco degli Enti deleganti")
    public void ptRequestsInstitutions(String ptName) {
        allReadInstitutions.clear();
        requestPtInstitutions(ptName, null, null, null, null);
    }

    @When("l'Ente PT {string} richiede l'elenco degli Enti deleganti con i seguenti parametri:")
    public void ptRequestsInstitutionsWithParams(String ptName, Map<String, String> params) {
        allReadInstitutions.clear();
        String search = params.containsKey("search") ? resolveValue(params.get("search")) : null;
        PtDelegationStatus status = params.containsKey("status") ? PtDelegationStatus.fromValue(params.get("status")) : null;
        Integer limit = params.containsKey("limit") ? Integer.valueOf(params.get("limit")) : null;
        requestPtInstitutions(ptName, search, status, limit, null);
    }

    @When("l'Ente PT {string} richiede l'elenco degli Enti deleganti con i seguenti parametri non validi:")
    public void ptRequestsInstitutionsWithInvalidParams(String ptName, Map<String, String> params) {
        ptInstitutionsClient.setPt(ptName);
        Map<String, String> queryParams = new LinkedHashMap<>();
        params.forEach((key, value) -> queryParams.put(key, resolveValue(value)));
        requestPtInstitutionsRaw(queryParams, true);
    }

    @When("viene richiesto l'elenco degli Enti deleganti senza indicare l'Ente PT")
    public void requestInstitutionsWithoutPt() {
        requestPtInstitutionsRaw(new LinkedHashMap<>(), false);
    }

    @When("l'Ente PT {string} richiede la pagina successiva")
    public void ptRequestsNextPage(String ptName) {
        assertThat(lastResponse)
                .as("Nessuna pagina letta in precedenza")
                .isNotNull();
        assertThat(lastResponse.getLastKey())
                .as("La pagina precedente non contiene il riferimento alla pagina successiva")
                .isNotBlank();
        requestPtInstitutions(ptName, lastSearch, lastStatus, lastLimit, lastResponse.getLastKey());
    }

    @When("l'Ente PT {string} legge tutte le pagine dell'elenco degli Enti deleganti con limit {int}")
    public void ptReadsAllPages(String ptName, Integer limit) {
        allReadInstitutions.clear();
        allReadInstitutions.addAll(readAllPages(ptName, null, null, limit));
    }

    @And("si memorizzano ragione sociale e codice fiscale del primo Ente restituito")
    public void saveFirstInstitution() {
        assertThat(lastResponse.getItems())
                .as("L'elenco degli Enti deleganti è vuoto, impossibile memorizzare il primo Ente")
                .isNotEmpty();
        PtInstitution first = lastResponse.getItems().get(0);
        savedValues.put(RAGIONE_SOCIALE, first.getInstitutionName());
        savedValues.put(CODICE_FISCALE, first.getInstitutionTaxCode());
        log.info("Ente memorizzato: {} - {}", first.getInstitutionName(), first.getInstitutionTaxCode());
    }

    @Then("la risposta contiene al massimo {int} Enti")
    public void responseContainsAtMost(Integer max) {
        assertThat(lastResponse.getItems())
                .as("La risposta contiene più di %s Enti", max)
                .hasSizeLessThanOrEqualTo(max);
    }

    @Then("la risposta contiene esattamente {int} Enti")
    public void responseContainsExactly(Integer size) {
        assertThat(lastResponse.getItems())
                .as("La risposta non contiene esattamente %s Enti", size)
                .hasSize(size);
    }

    @Then("la risposta contiene il riferimento alla pagina successiva")
    public void responseContainsLastKey() {
        assertThat(lastResponse.getLastKey())
                .as("La risposta non contiene il riferimento alla pagina successiva")
                .isNotBlank();
    }

    @Then("la risposta non contiene il riferimento alla pagina successiva")
    public void responseNotContainsLastKey() {
        assertThat(lastResponse.getLastKey())
                .as("La risposta contiene il riferimento alla pagina successiva")
                .isNull();
    }

    @Then("l'elenco degli Enti deleganti è vuoto")
    public void institutionsListIsEmpty() {
        assertThat(lastResponse)
                .as("La richiesta non ha prodotto una risposta valida")
                .isNotNull();
        assertThat(lastResponse.getItems())
                .as("L'elenco degli Enti deleganti non è vuoto")
                .isEmpty();
    }

    @Then("sono stati letti in totale {int} Enti")
    public void totalReadInstitutions(Integer total) {
        assertThat(allReadInstitutions)
                .as("Il numero totale di Enti letti non corrisponde")
                .hasSize(total);
    }

    @Then("ogni Ente compare una sola volta in tutte le pagine lette")
    public void eachInstitutionAppearsOnce() {
        List<String> readDelegationIds = allReadInstitutions.stream().map(PtInstitution::getDelegationId).toList();
        // riferimento indipendente dalla paginazione usata: lettura completa con il limit massimo
        Set<String> expectedDelegationIds = readAllPages(ptInstitutionsClient.getPtSetted(), lastSearch, lastStatus, MAX_LIMIT).stream()
                .map(PtInstitution::getDelegationId)
                .collect(Collectors.toSet());

        assertSoftly(softly -> {
            softly.assertThat(readDelegationIds)
                    .as("Sono presenti Enti duplicati tra le pagine lette")
                    .doesNotHaveDuplicates();
            softly.assertThat(readDelegationIds)
                    .as("Gli Enti letti pagina per pagina non corrispondono all'elenco completo")
                    .containsExactlyInAnyOrderElementsOf(expectedDelegationIds);
        });
    }

    @Then("ogni Ente restituito ha valorizzati ragione sociale, codice fiscale, id delega, stato e data")
    public void eachInstitutionHasAllFields() {
        assertSoftly(softly -> allReadInstitutions.forEach(institution -> {
            softly.assertThat(institution.getInstitutionId()).as("institutionId non valorizzato").isNotBlank();
            softly.assertThat(institution.getInstitutionName()).as("institutionName non valorizzato").isNotBlank();
            softly.assertThat(institution.getInstitutionTaxCode()).as("institutionTaxCode non valorizzato").isNotBlank();
            softly.assertThat(institution.getDelegationId()).as("delegationId non valorizzato").isNotBlank();
            softly.assertThat(institution.getStatus()).as("status non valorizzato").isNotNull();
            softly.assertThat(institution.getStatusChangedAt()).as("statusChangedAt non valorizzato").isNotBlank();
        }));
    }

    @Then("l'elenco degli Enti deleganti contiene almeno un Ente con stato delega {string}")
    public void listContainsStatus(String status) {
        assertThat(allReadInstitutions)
                .as("Nessun Ente con stato delega %s", status)
                .anyMatch(institution -> PtDelegationStatus.fromValue(status).equals(institution.getStatus()));
    }

    @Then("tutti gli Enti restituiti hanno stato delega {string}")
    public void allInstitutionsHaveStatus(String status) {
        assertThat(allReadInstitutions)
                .as("Sono presenti Enti con stato delega diverso da %s", status)
                .isNotEmpty()
                .allMatch(institution -> PtDelegationStatus.fromValue(status).equals(institution.getStatus()));
    }

    @Then("tutti gli Enti restituiti hanno ragione sociale contenente {string} senza distinzione tra maiuscole e minuscole")
    public void allInstitutionsNameContains(String value) {
        String expected = resolveValue(value).toLowerCase();
        assertThat(allReadInstitutions)
                .as("Sono presenti Enti la cui ragione sociale non contiene '%s'", expected)
                .isNotEmpty()
                .allMatch(institution -> institution.getInstitutionName().toLowerCase().contains(expected));
    }

    @Then("la risposta contiene solo l'Ente con codice fiscale {string}")
    public void responseContainsOnlyTaxCode(String taxCode) {
        String expected = resolveValue(taxCode);
        assertThat(allReadInstitutions)
                .as("La risposta non contiene solo l'Ente con codice fiscale %s", expected)
                .isNotEmpty()
                .allMatch(institution -> expected.equalsIgnoreCase(institution.getInstitutionTaxCode()));
    }

    @Then("l'Ente con codice fiscale {string} compare una sola volta con stato {string}")
    public void institutionAppearsOnceWithStatus(String taxCode, String status) {
        String expected = resolveValue(taxCode);
        List<PtInstitution> found = allReadInstitutions.stream()
                .filter(institution -> expected.equalsIgnoreCase(institution.getInstitutionTaxCode()))
                .toList();
        assertThat(found)
                .as("L'Ente con codice fiscale %s non compare una sola volta", expected)
                .hasSize(1);
        assertThat(found.get(0).getStatus())
                .as("Lo stato della delega dell'Ente con codice fiscale %s non corrisponde", expected)
                .isEqualTo(PtDelegationStatus.fromValue(status));
        assertThat(found.get(0).getStatusChangedAt())
                .as("La data dello stato della delega non è valorizzata")
                .isNotBlank();
    }

    @Then("nessuno degli Enti restituiti è presente nell'elenco dell'Ente PT {string}")
    public void noInstitutionInOtherPtList(String otherPtName) {
        String currentPt = ptInstitutionsClient.getPtSetted();
        Set<String> otherPtInstitutionIds = readAllPages(otherPtName, null, null, MAX_LIMIT).stream()
                .map(PtInstitution::getInstitutionId)
                .collect(Collectors.toSet());
        ptInstitutionsClient.setPt(currentPt);

        assertThat(allReadInstitutions)
                .as("L'elenco dell'Ente PT %s non contiene Enti", currentPt)
                .isNotEmpty()
                .noneMatch(institution -> otherPtInstitutionIds.contains(institution.getInstitutionId()));
    }

    private void requestPtInstitutions(String ptName, String search, PtDelegationStatus status, Integer limit, String lastKey) {
        ptInstitutionsClient.setPt(ptName);
        this.lastSearch = search;
        this.lastStatus = status;
        this.lastLimit = limit;
        try {
            this.lastResponse = ptInstitutionsClient.getPtInstitutions(search, status, limit, lastKey);
            allReadInstitutions.addAll(lastResponse.getItems());
        } catch (HttpStatusCodeException httpStatusCodeException) {
            this.lastResponse = null;
            sharedSteps.setNotificationError(httpStatusCodeException);
        }
    }

    private void requestPtInstitutionsRaw(Map<String, String> queryParams, boolean sendCxId) {
        try {
            ptInstitutionsClient.getPtInstitutionsRaw(queryParams, sendCxId);
        } catch (HttpStatusCodeException httpStatusCodeException) {
            sharedSteps.setNotificationError(httpStatusCodeException);
        }
    }

    private List<PtInstitution> readAllPages(String ptName, String search, PtDelegationStatus status, Integer limit) {
        ptInstitutionsClient.setPt(ptName);
        List<PtInstitution> institutions = new ArrayList<>();
        String lastKey = null;
        do {
            PtInstitutionsResponse response = ptInstitutionsClient.getPtInstitutions(search, status, limit, lastKey);
            institutions.addAll(response.getItems());
            lastKey = response.getLastKey();
            this.lastResponse = response;
        } while (lastKey != null);
        return institutions;
    }

    private String resolveValue(String value) {
        if (value == null) {
            return null;
        }
        String ragioneSociale = savedValues.get(RAGIONE_SOCIALE);
        return switch (value) {
            case TOKEN_RAGIONE_SOCIALE -> ragioneSociale;
            case TOKEN_PARTE_RAGIONE_SOCIALE -> ragioneSociale.substring(0, Math.max(3, ragioneSociale.length() / 2));
            case TOKEN_RAGIONE_SOCIALE_CASE_INVERTITO -> invertCase(ragioneSociale);
            case TOKEN_CODICE_FISCALE -> savedValues.get(CODICE_FISCALE);
            case TOKEN_VUOTO -> "";
            default -> value.startsWith(TOKEN_LUNGHEZZA)
                    ? "a".repeat(Integer.parseInt(value.substring(TOKEN_LUNGHEZZA.length())))
                    : value;
        };
    }

    private static String invertCase(String value) {
        StringBuilder inverted = new StringBuilder();
        for (char c : value.toCharArray()) {
            inverted.append(Character.isUpperCase(c) ? Character.toLowerCase(c) : Character.toUpperCase(c));
        }
        return inverted.toString();
    }
}
