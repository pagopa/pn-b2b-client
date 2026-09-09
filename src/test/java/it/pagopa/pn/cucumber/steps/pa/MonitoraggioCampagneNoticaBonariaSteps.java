package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformalmonitorcampaign.model.CampaignStatisticsResponse;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaB2bInternalInformalClientImpl;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class MonitoraggioCampagneNoticaBonariaSteps {

    @Getter
    private final PnPaB2bInternalInformalClientImpl pnPaB2bInternalInformalClientImpl;
    private CampaignStatisticsResponse campaignStatisticsResponse;

    @Autowired
    public MonitoraggioCampagneNoticaBonariaSteps(PnPaB2bInternalInformalClientImpl pnPaB2bInternalInformalClientImpl) {
        this.pnPaB2bInternalInformalClientImpl = pnPaB2bInternalInformalClientImpl;

    }

    @When("vengono recuperati i dati statistici della campagna {string}")
    public void getCampaignStatistics(String campaignId) {

        try {
            campaignStatisticsResponse = pnPaB2bInternalInformalClientImpl.getCampaignStatistics(campaignId);

        } catch (Exception e) {
            campaignStatisticsResponse = null;
            log.info("Errore durante il recupero delle statistiche della campagna", e);
        }
    }

}



