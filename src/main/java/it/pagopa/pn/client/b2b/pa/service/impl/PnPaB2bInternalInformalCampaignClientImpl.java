package it.pagopa.pn.client.b2b.pa.service.impl;


import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformalcampaign.api.CampaignsApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformalcampaign.model.CampaignDetail;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformalcampaign.model.CampaignSearchResponse;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bInternalInformalCampaignClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
public class PnPaB2bInternalInformalCampaignClientImpl implements IPnPaB2bInternalInformalCampaignClient {

    private final CampaignsApi campaignsApi;

    public PnPaB2bInternalInformalCampaignClientImpl(RestTemplate restTemplate, @Value("${pn.delivery.base-url}") String deliveryBasePath) {

        this.campaignsApi = new CampaignsApi(newCampaignApiClient(restTemplate, deliveryBasePath));
    }

    private static it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformalcampaign.ApiClient newCampaignApiClient(RestTemplate restTemplate, String basePath) {

        var client = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformalcampaign.ApiClient(restTemplate);

        client.setBasePath(basePath);

        return client;
    }

    @Override
    public CampaignSearchResponse listCampaigns(UUID senderId, Integer size, String nextPagesKey) {

        return campaignsApi.listCampaigns(senderId, size, nextPagesKey);
    }

    @Override
    public CampaignDetail getCampaign(String campaignId, UUID senderId) {

        return campaignsApi.getCampaign(campaignId, senderId);
    }
}
