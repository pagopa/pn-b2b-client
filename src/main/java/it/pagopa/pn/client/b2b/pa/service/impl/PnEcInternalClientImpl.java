package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.externalchannel.api.GestoreRequestApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.externalchannel.model.MessageIdRequestMetadataDto;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.externalchannel.model.PatchDto;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.externalchannel.model.RequestDto;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.externalchannel.ApiClient;
import it.pagopa.pn.client.b2b.pa.service.IPnEcInternalClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnEcInternalClientImpl implements IPnEcInternalClient {

    private final GestoreRequestApi gestoreRequestApi;

    public PnEcInternalClientImpl(
            RestTemplate restTemplate,
            @Value("${pn.safeStorage.base-url}") String safeStorageBaseUrl) {
        this.gestoreRequestApi = new GestoreRequestApi(newApiClient(restTemplate, safeStorageBaseUrl));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String baseUrl) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(baseUrl);
        return newApiClient;
    }

    @Override
    public void deleteRequest(String xPagopaExtchCxId, String requestIdx) throws RestClientException {
        gestoreRequestApi.deleteRequest(xPagopaExtchCxId, requestIdx);
    }

    @Override
    public RequestDto getRequest(String xPagopaExtchCxId, String requestIdx) throws RestClientException {
        return gestoreRequestApi.getRequest(xPagopaExtchCxId, requestIdx);
    }

    @Override
    public RequestDto getRequestByMessageId(String messageId) throws RestClientException {
        return gestoreRequestApi.getRequestByMessageId(messageId);
    }

    @Override
    public RequestDto getRequestMetadataByMessageId(String messageId) throws RestClientException {
        return gestoreRequestApi.getRequestMetadataByMessageId(messageId);
    }

    @Override
    public RequestDto insertRequest(RequestDto requestDto) throws RestClientException {
        return gestoreRequestApi.insertRequest(requestDto);
    }

    @Override
    public RequestDto patchRequest(String xPagopaExtchCxId, String requestIdx, PatchDto patchDto) throws RestClientException {
        return gestoreRequestApi.patchRequest(xPagopaExtchCxId, requestIdx, patchDto);
    }

    @Override
    public RequestDto setMessageIdInRequestMetadata(String xPagopaExtchCxId, String requestIdx) throws RestClientException {
        return gestoreRequestApi.setMessageIdInRequestMetadata(xPagopaExtchCxId, requestIdx);
    }

    @Override
    public RequestDto setRequestMetadataMessageId(String xPagopaExtchCxId, String requestIdx, MessageIdRequestMetadataDto messageIdRequestMetadataDto) throws RestClientException {
        return gestoreRequestApi.setRequestMetadataMessageId(xPagopaExtchCxId, requestIdx, messageIdRequestMetadataDto);
    }
}
