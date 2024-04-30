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

import java.io.ByteArrayInputStream;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
public class DowntimeLogsSteps {

    private final PnPaB2bUtils b2bUtils;
    private final IPnDowntimeLogsClient downtimeLogsClient;

    private PnDowntimeHistoryResponse pnDowntimeHistoryResponse;
    private PnDowntimeEntry pnDowntimeEntry;
    private String sha256;
    private LegalFactDownloadMetadataResponse legalFact;

    @Autowired
    public DowntimeLogsSteps(IPnDowntimeLogsClient downtimeLogsClient, PnPaB2bUtils b2bUtils) {
        this.downtimeLogsClient = downtimeLogsClient;
        this.b2bUtils = b2bUtils;
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
}
