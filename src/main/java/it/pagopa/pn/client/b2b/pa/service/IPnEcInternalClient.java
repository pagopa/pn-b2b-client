package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.externalchannel.model.MessageIdRequestMetadataDto;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.externalchannel.model.PatchDto;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.externalchannel.model.RequestDto;
import org.springframework.web.client.RestClientException;

public interface IPnEcInternalClient {

    void deleteRequest(String xPagopaExtchCxId, String requestIdx) throws RestClientException;

    RequestDto getRequest(String xPagopaExtchCxId, String requestIdx) throws RestClientException;

    RequestDto getRequestByMessageId(String messageId) throws RestClientException;

    RequestDto getRequestMetadataByMessageId(String messageId) throws RestClientException;

    RequestDto insertRequest(RequestDto requestDto) throws RestClientException;

    RequestDto patchRequest(String xPagopaExtchCxId, String requestIdx, PatchDto patchDto) throws RestClientException;

    RequestDto setMessageIdInRequestMetadata(String xPagopaExtchCxId, String requestIdx) throws RestClientException;

    RequestDto setRequestMetadataMessageId(String xPagopaExtchCxId, String requestIdx, MessageIdRequestMetadataDto messageIdRequestMetadataDto) throws RestClientException;
}
