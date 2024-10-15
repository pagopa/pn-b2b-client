package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.deliverypushb2b.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import org.springframework.web.client.RestClientException;

public interface IB2BDeliveryPushServiceClient extends SettableBearerToken {
    LegalFactDownloadMetadataResponse getDownloadLegalFact(String iun, String legalFactId) throws RestClientException;
}
