package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.model.mock.pec.ReceivedMessage;
import org.springframework.web.client.RestClientException;

import java.util.List;


public interface IPnExternalChannelsServiceClient {

    public ReceivedMessage getReceivedMessage(String requestId) throws RestClientException;
    public List<ReceivedMessage> getReceivedMessages(String iun, Integer recipientIndex) throws RestClientException;

}