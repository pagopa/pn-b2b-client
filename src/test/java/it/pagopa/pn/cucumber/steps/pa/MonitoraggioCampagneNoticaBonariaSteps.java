package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformalmonitorcampaign.model.CampaignStatisticsResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformalmonitorcampaign.model.CampaignStats;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaB2bInternalInformalClientImpl;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    @Then("il contatore {string} della campagna {string} risulta incrementato di {int}")
    public void verifyCounterIncrement(String counterName, String campaignId, int increment) {

        int initialValue = getCounter(initialCampaignStatistics.getStats(), counterName);

        await()
                .atMost(Duration.ofMinutes(2))
                .pollInterval(Duration.ofSeconds(5))
                .until(() -> {
                    finalCampaignStatistics = pnPaB2bInternalInformalClientImpl.getCampaignStatistics(campaignId);
                    int currentValue = getCounter(finalCampaignStatistics.getStats(), counterName);
                    return currentValue == initialValue + increment;
                });
    }

    private Integer getCounter(CampaignStats stats, String counterName) {

        return switch (counterName) {

            case "totalCount" -> stats.getTotalCount();
            case "totalRefusedCount" -> stats.getTotalRefusedCount();
            case "sentOnChannelCount" -> stats.getSentOnChannelCount();
            case "deliveredCount" -> stats.getDeliveredCount();
            case "undeliverableCount" -> stats.getUndeliverableCount();
            case "workflowDoneCount" -> stats.getWorkflowDoneCount();
            case "viewedCount" -> stats.getViewedCount();
            case "paidCount" -> stats.getPaidCount();
            default -> throw new IllegalArgumentException("Counter non supportato: " + counterName);
        };
    }

}



