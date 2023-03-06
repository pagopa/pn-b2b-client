package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.testclient.IPnDowntimeLogsClient;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.PnDowntimeEntry;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.PnDowntimeHistoryResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.PnFunctionality;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

public class DowntimeLogsSteps {

    private final SharedSteps sharedSteps;
    private final PnPaB2bUtils b2bUtils;
    private final IPnDowntimeLogsClient downtimeLogsClient;

    private PnDowntimeHistoryResponse pnDowntimeHistoryResponse;
    private PnDowntimeEntry pnDowntimeEntry;
    private String sha256;
    private LegalFactDownloadMetadataResponse legalFact;
    private static final Logger logger = LoggerFactory.getLogger(DowntimeLogsSteps.class);

    @Autowired
    public DowntimeLogsSteps(IPnDowntimeLogsClient downtimeLogsClient, SharedSteps sharedSteps) {
        this.downtimeLogsClient = downtimeLogsClient;
        this.sharedSteps = sharedSteps;
        this.b2bUtils = sharedSteps.getB2bUtils();
    }


    @Given("vengono letti gli eventi di disservizio degli ultimi {int} giorni relativi al(la) {string}")
    public void vengonoLettiGliEventiDegliUltimiGiorniRelativiAlla(int time, String eventType) {
        PnFunctionality pnFunctionality = null;
        switch (eventType) {
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
        List<PnFunctionality> pnFunctionalities = Arrays.asList(pnFunctionality);

        this.pnDowntimeHistoryResponse = downtimeLogsClient.statusHistory(OffsetDateTime.now().minusDays(time), OffsetDateTime.now(),
                pnFunctionalities, "0", "50");
    }


    @When("viene individuato se presente l'evento più recente")
    public void vieneIndividuatoSePresenteLEventoPiùRecente() {
        logger.info("Elenco eventi {}", pnDowntimeHistoryResponse);

        Assertions.assertNotNull(pnDowntimeHistoryResponse);
        PnDowntimeEntry value = null;
        for (PnDowntimeEntry entry : pnDowntimeHistoryResponse.getResult()) {
            if (value == null && entry.getFileAvailable()) {
                value = entry;
            }

            if (value != null && entry.getFileAvailable() && value.getEndDate().isBefore(entry.getEndDate())) {
                value = entry;
            }
        }
        this.pnDowntimeEntry = value;
        logger.info("evento {}", value);
    }


    @And("viene scaricata la relativa attestazione opponibile")
    public void vieneScaricataLaRelativaAttestazioneOpponibile() {
        if (pnDowntimeEntry != null) {
            this.legalFact = downtimeLogsClient.getLegalFact(pnDowntimeEntry.getLegalFactId());
            byte[] content = Assertions.assertDoesNotThrow(() -> b2bUtils.downloadFile(legalFact.getUrl()));
            this.sha256 = b2bUtils.computeSha256(new ByteArrayInputStream(content));
        }
    }

    @Then("l'attestazione opponibile è stata correttamente scaricata")
    public void lAttestazioneOpponibileÈStataCorrettamenteScaricata() {
        if (pnDowntimeEntry != null) {
            Assertions.assertNotNull(sha256);
        }
    }
}
