package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.externalchannels.v1.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.externalchannels.v1.api.DigitalCourtesyMessagesApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.externalchannels.v1.model.CourtesyMessageProgressEvent;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.externalchannels.v1.model.DigitalCourtesyMailRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.externalchannels.v1.model.DigitalCourtesySmsRequest;
import it.pagopa.pn.client.b2b.pa.service.IPnExternalChannelsInternalClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnExternalChannelsInternalClientImpl implements IPnExternalChannelsInternalClient {
    private final DigitalCourtesyMessagesApi digitalCourtesyMessagesApi;

    public PnExternalChannelsInternalClientImpl(
            RestTemplate restTemplate,
            @Value("${pn.safeStorage.base-url}") String safeStorageBaseUrl) {
        this.digitalCourtesyMessagesApi = new DigitalCourtesyMessagesApi(newApiClient(restTemplate, safeStorageBaseUrl));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String baseUrl) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(baseUrl);
        return newApiClient;
    }

    @Override
    public List<CourtesyMessageProgressEvent> getCourtesyShortMessageStatus(String requestIdx, String xPagopaExtchCxId) throws RestClientException {
        return digitalCourtesyMessagesApi.getCourtesyShortMessageStatus(requestIdx, xPagopaExtchCxId);
    }

    @Override
    public List<CourtesyMessageProgressEvent> getDigitalCourtesyMessageStatus(String requestIdx, String xPagopaExtchCxId) throws RestClientException {
        return digitalCourtesyMessagesApi.getDigitalCourtesyMessageStatus(requestIdx, xPagopaExtchCxId);
    }

    @Override
    public ResponseEntity<Void> sendCourtesyShortMessage(String requestIdx, String xPagopaExtchCxId, DigitalCourtesySmsRequest digitalCourtesySmsRequest) throws RestClientException {
        return digitalCourtesyMessagesApi.sendCourtesyShortMessageWithHttpInfo(requestIdx, xPagopaExtchCxId, digitalCourtesySmsRequest);
    }

    @Override
    public ResponseEntity<Void> sendDigitalCourtesyMessage(String requestIdx, String xPagopaExtchCxId, DigitalCourtesyMailRequest digitalCourtesyMailRequest) throws RestClientException {
        return digitalCourtesyMessagesApi.sendDigitalCourtesyMessageWithHttpInfo(requestIdx, xPagopaExtchCxId, digitalCourtesyMailRequest);
    }
}
