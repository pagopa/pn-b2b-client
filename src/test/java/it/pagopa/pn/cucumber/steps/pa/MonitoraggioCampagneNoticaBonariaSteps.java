package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformalmonitorcampaign.model.CampaignStatisticsResponse;
import it.pagopa.pn.client.b2b.pa.provider.SenderInfoProvider;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaB2bInternalInformalClientImpl;
import it.pagopa.pn.cucumber.steps.SharedSteps;
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
    @Getter
    private final SharedSteps sharedSteps;
    private CampaignStatisticsResponse initialCampaignStatistics;
    private CampaignStatisticsResponse finalCampaignStatistics;
    private String currentCxId;

    @Autowired
    public MonitoraggioCampagneNoticaBonariaSteps(PnPaB2bInternalInformalClientImpl pnPaB2bInternalInformalClientImpl, SharedSteps sharedSteps) {
        this.pnPaB2bInternalInformalClientImpl = pnPaB2bInternalInformalClientImpl;
        this.sharedSteps = sharedSteps;

    }

    @Given("vengono salvati i dati statistici attuali della campagna {string} associata all' ente {string}")
    public void saveCampaignStatistics(String campaignId, String paName) {

        SenderInfoProvider.PaInfo paInfo = sharedSteps.getSenderInfoProvider().getPaInfo(paName);
        this.currentCxId = paInfo.getSenderId();

        initialCampaignStatistics = pnPaB2bInternalInformalClientImpl.getCampaignStatistics(currentCxId, campaignId);
        assertNotNull(initialCampaignStatistics);
    }

    @Then("il recupero dei dati statistici della campagna {string} fallisce con errore {int}")
    public void getCampaignStatisticsExpectError(String campaignId, int expectedStatus) {

        try {
            pnPaB2bInternalInformalClientImpl.getCampaignStatistics(currentCxId, campaignId);
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
            pnPaB2bInternalInformalClientImpl.getCampaignStatistics(currentCxId, campaignId);
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

                    finalCampaignStatistics = pnPaB2bInternalInformalClientImpl.getCampaignStatistics(currentCxId, campaignId);
                    int currentValue = counter.extract(finalCampaignStatistics.getStats());
                    return currentValue == initialValue + increment;
                });
    }
}



