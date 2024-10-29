package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.*;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.service.IPnDowntimeLogsClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestClientResponseException;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;


@Slf4j
public class DowntimeLogsSteps {
    private final PnPaB2bUtils b2bUtils;
    private final IPnDowntimeLogsClient downtimeLogsClient;
    private final LegalFactContentVerifySteps legalFactContentVerifySteps;
    private PnDowntimeHistoryResponse pnDowntimeHistoryResponse;
    private PnDowntimeEntry pnDowntimeEntry;
    private String sha256;
    private LegalFactDownloadMetadataResponse legalFact;
    private RestClientResponseException exception;

    @Autowired
    public DowntimeLogsSteps(IPnDowntimeLogsClient downtimeLogsClient, PnPaB2bUtils b2bUtils, LegalFactContentVerifySteps legalFactContentVerifySteps) {
        this.downtimeLogsClient = downtimeLogsClient;
        this.b2bUtils = b2bUtils;
        this.legalFactContentVerifySteps = legalFactContentVerifySteps;
    }

    @Given("vengono letti gli eventi di disservizio degli ultimi {int} giorni relativi al(la) {string}")
    public void vengonoLettiGliEventiDegliUltimiGiorniRelativiAlla(int time, String eventType) {
        PnFunctionality pnFunctionality = null;
        switch (eventType){
            case "creazione notifiche":
                pnFunctionality = PnFunctionality.NOTIFICATION_CREATE;
                break;
            case "visualizzazione notifiche":
                pnFunctionality = PnFunctionality.NOTIFICATION_VISUALIZATION;
                break;
            case "workflow notifiche":
                pnFunctionality = PnFunctionality.NOTIFICATION_WORKFLOW;
                break;
        }
        List<PnFunctionality> pnFunctionalities = Collections.singletonList(pnFunctionality);

        this.pnDowntimeHistoryResponse = downtimeLogsClient.statusHistory(OffsetDateTime.now().minusDays(time), OffsetDateTime.now(),
                pnFunctionalities, "0", "50");
    }

    @When("viene individuato se presente l'evento più recente")
    public void vieneIndividuatoSePresenteLEventoPiùRecente() {
        log.info("Elenco eventi {}",pnDowntimeHistoryResponse);

        Assertions.assertNotNull(pnDowntimeHistoryResponse);
        PnDowntimeEntry value = null;
        for(PnDowntimeEntry entry: pnDowntimeHistoryResponse.getResult()){
            if(value == null && Boolean.TRUE.equals(entry.getFileAvailable())){
                value = entry;
            }
            boolean valueNotNull = value != null && value.getEndDate() != null;
            boolean entryNotNull = entry != null && entry.getEndDate() != null && entry.getFileAvailable() != null;
            if( valueNotNull && entryNotNull && value.getEndDate().isBefore(entry.getEndDate()) && entry.getFileAvailable()){
                value = entry;
            }
        }
        this.pnDowntimeEntry = value;
        log.info("evento {}",value);
    }


    @And("viene scaricata la relativa attestazione opponibile")
    public void vieneScaricataLaRelativaAttestazioneOpponibile() {
        if(pnDowntimeEntry != null){
            this.legalFact = downtimeLogsClient.getLegalFact(pnDowntimeEntry.getLegalFactId());
            byte[] content = Assertions.assertDoesNotThrow(() -> b2bUtils.downloadFile(legalFact.getUrl()));
            this.sha256 = b2bUtils.computeSha256(new ByteArrayInputStream(content));
        }
    }

    @Then("l'attestazione opponibile è stata correttamente scaricata")
    public void lAttestazioneOpponibileÈStataCorrettamenteScaricata() {
        if(pnDowntimeEntry != null){
            Assertions.assertNotNull(sha256);
        }
    }

    @Then("si effettua download della relativa attestazione opponibile e si verifica se il legalFact è di tipo {string}")
    public void siEffettuaDownloadDellaRelativaAttestazioneOpponibileESiVerificaSeIlLegalFactEDiTipo(String legalFactType) {
        if(pnDowntimeEntry != null){
            this.legalFact = downtimeLogsClient.getLegalFact(pnDowntimeEntry.getLegalFactId());
            byte[] content = Assertions.assertDoesNotThrow(() -> b2bUtils.downloadFile(legalFact.getUrl()));
            legalFactContentVerifySteps.setLegalFactUrl(legalFact.getUrl());
            legalFactContentVerifySteps.checkLegalFactType(content, legalFactType);
        }
    }

    @Given("si chiama l'api di recupero elenco disservizi nell'anno {int} e mese {int}")
    public void siChiamaLApiDiRecuperoElencoDisserviziNellAnnoEMese(Integer year, Integer month) {
        try {
            pnDowntimeHistoryResponse = downtimeLogsClient.getResolved(year, month);
        } catch (RestClientResponseException e) {
            exception = e;
        }
    }

    @Given("si chiama l'api di recupero elenco disservizi con mese e anno vuoti")
    public void siChiamaLApiDiRecuperoElencoDisserviziConMeseEAnnoVuoti() {
        siChiamaLApiDiRecuperoElencoDisserviziNellAnnoEMese(null, null);
    }

    @Then("viene restituito l'elenco dei disservizi del mese {int} dell'anno {int}")
    public void vieneRestituitoLElencoDeiDisserviziDelMeseMeseDellAnno(Integer month, Integer year) {
        Assertions.assertNotNull(pnDowntimeHistoryResponse);
        Assertions.assertNotNull(pnDowntimeHistoryResponse.getResult());
        pnDowntimeHistoryResponse.getResult()
                .forEach(data -> checkDataValue(year, month, data));

    }

    private void checkDataValue(Integer year, Integer month, PnDowntimeEntry data) {
        OffsetDateTime fileAvailableTimestamp = data.getFileAvailableTimestamp();
        OffsetDateTime endDate = data.getEndDate();
        OffsetDateTime releaseTime = OffsetDateTime.of(2024, 10, 23, 0, 0, 0, 0, ZoneOffset.UTC);
        Assertions.assertNotNull(endDate);
        if (data.getEndDate().isAfter(releaseTime)) {
            Assertions.assertNotNull(fileAvailableTimestamp);
            Assertions.assertTrue(fileAvailableTimestamp.isAfter(endDate));
        }
        Assertions.assertEquals(month, endDate.getMonthValue());
        Assertions.assertEquals(year, endDate.getYear());

    }

    @Then("viene restituito l'elenco dei disservizi del mese e dell'anno corrente")
    public void vieneRestituitoLElencoDeiDisserviziDelMeseCorrente() {
        LocalDate date = LocalDate.now();
        vieneRestituitoLElencoDeiDisserviziDelMeseMeseDellAnno(date.getMonthValue(), date.getYear());
    }

    @Then("si controlla che l'api restituisce un codice di errore {int}")
    public void siControllaCheLApiRestituisceUnCodiceDiErrore(Integer errorCode) {
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(errorCode, exception.getRawStatusCode());
    }

    @Then("viene restituito un elenco di disservizi vuoto")
    public void vieneRestituitoUnElencoDiDisserviziVuoto() {
        Assertions.assertNotNull(pnDowntimeHistoryResponse);
        Assertions.assertNotNull(pnDowntimeHistoryResponse.getResult());
        Assertions.assertTrue(pnDowntimeHistoryResponse.getResult().isEmpty());
    }

    @Given("viene chiamata l’API per il download dell'atto opponibile ai terzi con id {string}")
    public void vieneChiamataLAPIPerIlDownloadDellAttoOpponibileAiTerziConId(String idType) {
        try {
            siChiamaLApiDiRecuperoElencoDisserviziNellAnnoEMese(2024, 10);
            Assertions.assertNotNull(pnDowntimeHistoryResponse);
            Assertions.assertNotNull(pnDowntimeHistoryResponse.getResult());
            Assertions.assertFalse(pnDowntimeHistoryResponse.getResult().isEmpty());
            String legalFactId = getLegalFactId(idType, pnDowntimeHistoryResponse);
            legalFact = downtimeLogsClient.getLegalFact(idType.equals("null") ? null : legalFactId);
        } catch (RestClientResponseException e) {
            exception = e;
        }
    }

    @Then("viene scaricato l'atto opponibile ai terzi di malfunzionamento e ripristino")
    public void vieneScaricatoLAttoOpponibileAiTerziDiMalfunzionamentoERipristino() {
        Assertions.assertNotNull(legalFact);
        Assertions.assertNotNull(legalFact.getUrl());
    }

    @Then("la chiamata va con successo e la risposta contiene il campo retryAfter popolato")
    public void laChiamataVaConSuccessoELaRispostaContieneIlCampoPopolato() {
        Assertions.assertNotNull(legalFact);
        Assertions.assertNotNull(legalFact.getRetryAfter());
    }

    @Given("viene chiamata l’API per il download dell'atto opponibile prodotto piu di 365 giorni precedenti")
    public void vieneChiamataLAPIPerIlDownloadDellAttoOpponibileProdottoPiuDiGiorniPrecedenti() {
        try {
            LocalDate date = LocalDate.now();
            LocalDate before = date.minusDays(365);
            siChiamaLApiDiRecuperoElencoDisserviziNellAnnoEMese(before.getYear(), before.getMonthValue());
            Assertions.assertNotNull(pnDowntimeHistoryResponse.getResult());
            Assertions.assertFalse(pnDowntimeHistoryResponse.getResult().isEmpty());
            String legalFactId = pnDowntimeHistoryResponse.getResult()
                    .stream()
                    .filter(data -> data.getEndDate() != null && data.getEndDate().getDayOfMonth() <= before.getDayOfMonth())
                    .map(PnDowntimeEntry::getLegalFactId)
                    .filter(Objects::nonNull)
                    .findAny()
                    .orElse(null);
            Assertions.assertNotNull(legalFactId, "non è stato trovato nessun legal fact prodotto prima del giorno " + before.getDayOfMonth() + " " + before.getMonth().name() + " anno " + before.getYear());
            legalFact = downtimeLogsClient.getLegalFact(legalFactId);
        } catch (RestClientResponseException e) {
            exception = e;
        }
    }

    private String getLegalFactId(String type,  PnDowntimeHistoryResponse legalFact) {
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
                return pnDowntimeHistoryResponse.getResult().get(0).getLegalFactId();
            }
            default -> throw new IllegalArgumentException();
        }
    }
}