package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformalmonitorcampaign.model.CampaignStatisticsResponse;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaB2bInternalInformalClientImpl;
import it.pagopa.pn.cucumber.steps.informalNotification.data.CampaignCounter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.jsoup.helper.Validate.fail;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class MonitoraggioCampagneNoticaBonariaSteps {

    @Getter
    private final PnPaB2bInternalInformalClientImpl pnPaB2bInternalInformalClientImpl;
    private CampaignStatisticsResponse initialCampaignStatistics;
    private CampaignStatisticsResponse finalCampaignStatistics;

    @Autowired
    public MonitoraggioCampagneNoticaBonariaSteps(PnPaB2bInternalInformalClientImpl pnPaB2bInternalInformalClientImpl) {
        this.pnPaB2bInternalInformalClientImpl = pnPaB2bInternalInformalClientImpl;

    }

    @Given("vengono salvati i dati statistici attuali della campagna {string}")
    public void saveCampaignStatistics(String campaignId) {

        initialCampaignStatistics = pnPaB2bInternalInformalClientImpl.getCampaignStatistics(campaignId);
        assertNotNull(initialCampaignStatistics);
    }

//    @Then("il contatore {string} della campagna {string} risulta incrementato di {int}")
//    public void verifyCounterIncrement(String counterName, String campaignId, int increment) {
//
//        int initialValue = getCounter(initialCampaignStatistics.getStats(), counterName);
//
//        await().atMost(Duration.ofMinutes(2)).pollInterval(Duration.ofSeconds(5)).until(() -> {
//            finalCampaignStatistics = pnPaB2bInternalInformalClientImpl.getCampaignStatistics(campaignId);
//            int currentValue = getCounter(finalCampaignStatistics.getStats(), counterName);
//            return currentValue == initialValue + increment;
//        });
//    }


    @Then("il recupero dei dati statistici della campagna {string} fallisce con errore {int}")
    public void getCampaignStatisticsExpectError(String campaignId, int expectedStatus) {

        try {
            pnPaB2bInternalInformalClientImpl.getCampaignStatistics(campaignId);
            fail("Atteso errore " + expectedStatus + " ma la richiesta è andata a buon fine");

        } catch (HttpClientErrorException ex) {
            assertEquals(expectedStatus, ex.getStatusCode().value());

        } catch (Exception e) {
            fail("Eccezione inattesa: " + e.getClass().getName());
        }
    }

    @Then("il recupero dei dati statistici della campagna {string} fallisce con errore {int} {string}")
    public void getCampaignStatisticsExpectError(String campaignId, int expectedStatus, String expectedErrorCode) {

        try {
            pnPaB2bInternalInformalClientImpl.getCampaignStatistics(campaignId);
            fail("Atteso errore ma la richiesta è andata a buon fine");

        } catch (HttpClientErrorException ex) {
            assertEquals(expectedStatus, ex.getStatusCode().value());
            assertTrue(ex.getResponseBodyAsString().contains(expectedErrorCode), "Codice errore atteso non trovato");
        }
    }

    @Then("il contatore {string} della campagna {string} risulta incrementato di {int}")
    public void verifyCounterIncrement(String counterName, String campaignId, int increment) {

        CampaignCounter counter = CampaignCounter.valueOf(counterName);
        int initialValue = counter.extract(initialCampaignStatistics.getStats());

        await()
                .atMost(Duration.ofMinutes(2))
                .pollInterval(Duration.ofSeconds(5))
                .until(() -> {

                    finalCampaignStatistics = pnPaB2bInternalInformalClientImpl.getCampaignStatistics(campaignId);
                    int currentValue = counter.extract(finalCampaignStatistics.getStats());
                    return currentValue == initialValue + increment;
                });
    }




//    private Integer getCounter(CampaignStats stats, String counterName) {
//
//        CampaignCounter counter = CampaignCounter.valueOf(counterName);
//
//        return switch (counter) {
//
//            case NOTIFICHE_TOTALI -> stats.getTotalCount();
//
//            case NOTIFICHE_RIFIUTATE -> stats.getTotalRefusedCount();
//
//            case NOTIFICHE_INVIATE_SU_CANALE -> stats.getSentOnChannelCount();
//
//            case NOTIFICHE_CONSEGNATE -> stats.getDeliveredCount();
//
//            case NOTIFICHE_NON_CONSEGNABILI -> stats.getUndeliverableCount();
//
//            case WORKFLOW_COMPLETATI -> stats.getWorkflowDoneCount();
//
//            case NOTIFICHE_VISUALIZZATE -> stats.getViewedCount();
//
//            case NOTIFICHE_PAGATE -> stats.getPaidCount();
//
//            case NOTIFICHE_INVIATE_IO -> stats.getSentOnChannel().getDigital().getIO();
//
//            case NOTIFICHE_INVIATE_EMAIL -> stats.getSentOnChannel().getDigital().getEMAIL();
//
//            case NOTIFICHE_INVIATE_PEC -> stats.getSentOnChannel().getDigital().getPEC();
//
//            case NOTIFICHE_INVIATE_SMS -> stats.getSentOnChannel().getDigital().getSMS();
//
//            case NOTIFICHE_INVIATE_RS -> stats.getSentOnChannel().getAnalog().getRS();
//
//            case NOTIFICHE_CONSEGNATE_IO -> stats.getDelivered().getIO();
//
//            case NOTIFICHE_CONSEGNATE_EMAIL -> stats.getDelivered().getEMAIL();
//
//            case NOTIFICHE_CONSEGNATE_PEC -> stats.getDelivered().getPEC();
//
//            case NOTIFICHE_CONSEGNATE_RS -> stats.getDelivered().getRS();
//
//            case NOTIFICHE_VISUALIZZATE_IO -> stats.getViewed().getIO();
//
//            case NOTIFICHE_VISUALIZZATE_SEND -> stats.getViewed().getSEND();
//
//            case PRIMA_VISUALIZZAZIONE -> stats.getViewed().getFirstViewedCount();
//        };
//    }

}



