package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformalcampaign.model.CampaignDetail;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformalcampaign.model.CampaignSearchResponse;

import java.util.UUID;


public interface IPnPaB2bInternalInformalCampaignClient {

    CampaignSearchResponse listCampaigns(UUID senderId, Integer size, String nextPagesKey);

    CampaignDetail getCampaign(String campaignId, UUID senderId);
}

