package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.common.http.HttpCallExecutor;
import it.pagopa.pn.client.b2b.pa.service.IPnDowntimeLogsClient;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.PnDowntimeEntry;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.PnDowntimeHistoryResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.PnFunctionality;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;


@Slf4j
public class DowntimeLogsSteps {
    private final IPnDowntimeLogsClient downtimeLogsClient;
    private final LegalFactContentVerifySteps legalFactContentVerifySteps;
    private final HttpCallExecutor httpCallExecutor;
    private PnDowntimeEntry pnDowntimeEntry;
    private String sha256;

    @Autowired
    public DowntimeLogsSteps(IPnDowntimeLogsClient downtimeLogsClient, LegalFactContentVerifySteps legalFactContentVerifySteps,
                             HttpCallExecutor httpCallExecutor) {
        this.downtimeLogsClient = downtimeLogsClient;
        this.legalFactContentVerifySteps = legalFactContentVerifySteps;
        this.httpCallExecutor = httpCallExecutor;
    }

    @Given("vengono letti gli eventi di disservizio degli ultimi {int} giorni relativi al(la) {string}")
    public void vengonoLettiGliEventiDegliUltimiGiorniRelativiAlla(int time, String eventType) {
        PnFunctionality pnFunctionality = switch (eventType) {
            case "creazione notifiche" -> PnFunctionality.NOTIFICATION_CREATE;
            case "visualizzazione notifiche" -> PnFunctionality.NOTIFICATION_VISUALIZATION;
            case "workflow notifiche" -> PnFunctionality.NOTIFICATION_WORKFLOW;
            default -> null;
        };
        List<PnFunctionality> pnFunctionalities = Collections.singletonList(pnFunctionality);

        httpCallExecutor.callForEntity(() -> downtimeLogsClient.statusHistoryWithHttpInfo(
                OffsetDateTime.now().minusDays(time), OffsetDateTime.now(), pnFunctionalities, "0", "50"));
        getSuccessfulResponse(PnDowntimeHistoryResponse.class);
    }

    @When("viene individuato se presente l'evento più recente")
    public void vieneIndividuatoSePresenteLEventoPiùRecente() {
        PnDowntimeHistoryResponse pnDowntimeHistoryResponse = getSuccessfulResponse(PnDowntimeHistoryResponse.class);
        log.info("Elenco eventi {}", pnDowntimeHistoryResponse);

        Assertions.assertNotNull(pnDowntimeHistoryResponse);
        PnDowntimeEntry value = null;
        for (PnDowntimeEntry entry : pnDowntimeHistoryResponse.getResult()) {
            if (value == null && Boolean.TRUE.equals(entry.getFileAvailable())) {
                value = entry;
            }
            boolean valueNotNull = value != null && value.getEndDate() != null;
            boolean entryNotNull = entry != null && entry.getEndDate() != null && entry.getFileAvailable() != null;
            if (valueNotNull && entryNotNull && value.getEndDate().isBefore(entry.getEndDate()) && entry.getFileAvailable()) {
                value = entry;
            }
        }
        this.pnDowntimeEntry = value;
        log.info("evento {}", value);
    }


    @And("viene scaricata la relativa attestazione opponibile")
    public void vieneScaricataLaRelativaAttestazioneOpponibile() {
        if (pnDowntimeEntry != null) {
            httpCallExecutor.callForEntity(() -> downtimeLogsClient.getLegalFactWithHttpInfo(pnDowntimeEntry.getLegalFactId()));
            LegalFactDownloadMetadataResponse legalFact = getSuccessfulResponse(LegalFactDownloadMetadataResponse.class);
            byte[] content = Assertions.assertDoesNotThrow(() -> B2bUtils.downloadFile(legalFact.getUrl()));
            this.sha256 = B2bUtils.computeSha256(new ByteArrayInputStream(content));
        }
    }

    @Then("l'attestazione opponibile è stata correttamente scaricata")
    public void lAttestazioneOpponibileÈStataCorrettamenteScaricata() {
        if (pnDowntimeEntry != null) {
            Assertions.assertNotNull(sha256);
        }
    }

    @Then("si effettua download della relativa attestazione opponibile e si verifica se il legalFact è di tipo {string}")
    public void siEffettuaDownloadDellaRelativaAttestazioneOpponibileESiVerificaSeIlLegalFactEDiTipo(String legalFactType) {
        if (pnDowntimeEntry != null) {
            httpCallExecutor.callForEntity(() -> downtimeLogsClient.getLegalFactWithHttpInfo(pnDowntimeEntry.getLegalFactId()));
            LegalFactDownloadMetadataResponse legalFact = getSuccessfulResponse(LegalFactDownloadMetadataResponse.class);
            byte[] content = Assertions.assertDoesNotThrow(() -> B2bUtils.downloadFile(legalFact.getUrl()));
            legalFactContentVerifySteps.setLegalFactUrl(legalFact.getUrl());
            legalFactContentVerifySteps.checkLegalFactType(content, legalFactType);
        }
    }

    @Given("si chiama l'api di recupero elenco disservizi nell'anno e mese corrente")
    public void siChiamaLApiDiRecuperoElencoDisserviziNellAnnoEMeseCorrente() {
        LocalDate now = LocalDate.now();
        siChiamaLApiDiRecuperoElencoDisserviziNellAnnoEMese(now.getYear(), now.getMonthValue());
    }

    @Given("si chiama l'api di recupero elenco disservizi nell'anno {int} e mese {int}")
    public void siChiamaLApiDiRecuperoElencoDisserviziNellAnnoEMese(Integer year, Integer month) {
        httpCallExecutor.callForEntity(() -> downtimeLogsClient.getResolvedWithHttpInfo(year, month));
    }

    @Given("si chiama l'api di recupero elenco disservizi con mese e anno vuoti")
    public void siChiamaLApiDiRecuperoElencoDisserviziConMeseEAnnoVuoti() {
        siChiamaLApiDiRecuperoElencoDisserviziNellAnnoEMese(null, null);
    }

    //check fileAvailable solo quello del giorno corrente
    @Then("viene restituito l'elenco dei disservizi del mese {int} dell'anno {int}")
    public void vieneRestituitoLElencoDeiDisserviziDelMeseMeseDellAnno(Integer month, Integer year) {
        PnDowntimeHistoryResponse pnDowntimeHistoryResponse = getSuccessfulResponse(PnDowntimeHistoryResponse.class);
        Assertions.assertNotNull(pnDowntimeHistoryResponse);
        Assertions.assertNotNull(pnDowntimeHistoryResponse.getResult());
        pnDowntimeHistoryResponse.getResult()
                .forEach(data -> checkDataValue(year, month, data));
    }

    private void checkDataValue(Integer year, Integer month, PnDowntimeEntry data) {
        OffsetDateTime endDate = data.getEndDate();
        Assertions.assertNotNull(endDate);
        Assertions.assertEquals(month, endDate.getMonthValue());
        Assertions.assertEquals(year, endDate.getYear());
        checkFileAvailableTimestamp(data);
    }

    private void checkFileAvailableTimestamp(PnDowntimeEntry data) {
        LocalDate now = LocalDate.now();
        if (now.getYear() == data.getEndDate().getYear() && now.getMonthValue() == data.getEndDate().getMonth().getValue() && now.getDayOfMonth() == data.getEndDate().getDayOfMonth()) {
            OffsetDateTime fileAvailableTimestamp = data.getFileAvailableTimestamp();
            Assertions.assertNotNull(fileAvailableTimestamp);
        }
    }

    @Then("viene restituito l'elenco dei disservizi del mese e dell'anno corrente")
    public void vieneRestituitoLElencoDeiDisserviziDelMeseCorrente() {
        LocalDate date = LocalDate.now();
        vieneRestituitoLElencoDeiDisserviziDelMeseMeseDellAnno(date.getMonthValue(), date.getYear());
    }

    @Then("si controlla che l'api restituisce un codice di errore {int}")
    public void siControllaCheLApiRestituisceUnCodiceDiErrore(Integer errorCode) {
        Assertions.assertTrue(httpCallExecutor.isHttpError(), "La chiamata disservizi non ha prodotto un errore HTTP");
        Assertions.assertEquals(errorCode, httpCallExecutor.getStatusCode());
    }

    @Then("viene restituito un elenco di disservizi vuoto")
    public void vieneRestituitoUnElencoDiDisserviziVuoto() {
        PnDowntimeHistoryResponse pnDowntimeHistoryResponse = getSuccessfulResponse(PnDowntimeHistoryResponse.class);
        Assertions.assertNotNull(pnDowntimeHistoryResponse);
        Assertions.assertNotNull(pnDowntimeHistoryResponse.getResult());
        Assertions.assertTrue(pnDowntimeHistoryResponse.getResult().isEmpty());
    }

    @Given("viene chiamata l’API per il download dell'atto opponibile ai terzi con id {string}")
    public void vieneChiamataLAPIPerIlDownloadDellAttoOpponibileAiTerziConId(String idType) {
        PnDowntimeHistoryResponse history = null;
        if ("CORRETTO".equalsIgnoreCase(idType)) {
            siChiamaLApiDiRecuperoElencoDisserviziNellAnnoEMeseCorrente();
            history = getSuccessfulResponse(PnDowntimeHistoryResponse.class);
            Assertions.assertNotNull(history.getResult());
            Assertions.assertFalse(history.getResult().isEmpty());
        }
        PnDowntimeHistoryResponse resolvedHistory = history;
        httpCallExecutor.callForEntity(() -> downtimeLogsClient.getLegalFactWithHttpInfo(getLegalFactId(idType, resolvedHistory)));
    }

    @Then("viene scaricato l'atto opponibile ai terzi di malfunzionamento e ripristino")
    public void vieneScaricatoLAttoOpponibileAiTerziDiMalfunzionamentoERipristino() {
        LegalFactDownloadMetadataResponse legalFact = getSuccessfulResponse(LegalFactDownloadMetadataResponse.class);
        Assertions.assertNotNull(legalFact);
        Assertions.assertNotNull(legalFact.getUrl());
    }

    @Then("la chiamata va con successo e la risposta contiene il campo retryAfter popolato")
    public void laChiamataVaConSuccessoELaRispostaContieneIlCampoPopolato() {
        LegalFactDownloadMetadataResponse legalFact = getSuccessfulResponse(LegalFactDownloadMetadataResponse.class);
        Assertions.assertNotNull(legalFact);
        Assertions.assertNotNull(legalFact.getRetryAfter());
    }

    @Given("viene chiamata l’API per il download dell'atto opponibile prodotto piu di 365 giorni precedenti")
    public void vieneChiamataLAPIPerIlDownloadDellAttoOpponibileProdottoPiuDiGiorniPrecedenti() {
        LocalDate before = LocalDate.of(2024, 01, 27);
        siChiamaLApiDiRecuperoElencoDisserviziNellAnnoEMese(before.getYear(), before.getMonthValue());
        PnDowntimeHistoryResponse history = getSuccessfulResponse(PnDowntimeHistoryResponse.class);
        Assertions.assertNotNull(history.getResult());
        Assertions.assertFalse(history.getResult().isEmpty());
        List<PnDowntimeEntry> validResponse = history.getResult()
                .stream()
                .filter(data -> data.getEndDate() != null && data.getEndDate().getDayOfMonth() <= before.getDayOfMonth())
                .toList();
        Assertions.assertFalse(validResponse.isEmpty(), "nella data " + before.getMonth().name() + " anno " + before.getYear() + " non è stato trovato nessun disservizio risolto");
        double index = Math.random() * validResponse.size();
        String legalFactId = validResponse.get((int) index).getLegalFactId();
        Assertions.assertNotNull(legalFactId, "non è stato trovato nessun legal fact prodotto prima del giorno " + before.getDayOfMonth() + " " + before.getMonth().name() + " anno " + before.getYear());
        httpCallExecutor.callForEntity(() -> downtimeLogsClient.getLegalFactWithHttpInfo(legalFactId));
    }

    private <T> T getSuccessfulResponse(Class<T> responseType) {
        Assertions.assertTrue(httpCallExecutor.isSuccessful(), "La chiamata disservizi non ha prodotto un esito HTTP di successo");
        return httpCallExecutor.getResponseBody(responseType);
    }

    private String getLegalFactId(String type, PnDowntimeHistoryResponse history) {
        switch (type) {
            case "ERRATO" -> {
                return "1234567890";
            }
            case "null" -> {
                return null;
            }
            case "" -> {
                return "";
            }
            case "CORRETTO" -> {
                return history.getResult().get(0).getLegalFactId();
            }
            default -> throw new IllegalArgumentException();
        }
    }
}
