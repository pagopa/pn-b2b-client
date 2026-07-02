package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.io.connector.model.*;
import org.springframework.web.client.RestClientException;

public interface IPnIOConnectorClient {

    MessageResponse sendIOMessage(String xPagopaIoconCxId, MessageRequest messageRequest) throws RestClientException;

    GetProfileResponse getIOProfile(String xPagopaIoconCxId, GetProfileRequest getProfileRequest) throws RestClientException;

    GetMessageResponse getMessage(String id, String recipientTaxId) throws RestClientException;

}
