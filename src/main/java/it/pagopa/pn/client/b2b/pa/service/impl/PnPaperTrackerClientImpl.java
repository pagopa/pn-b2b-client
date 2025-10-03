package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.papertracker.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.papertracker.api.PaperTrackerErrorApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.papertracker.api.PaperTrackerOutputApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.papertracker.api.PaperTrackerTrackingApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.papertracker.model.PaperTrackerOutputsResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.papertracker.model.TrackingErrorsResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.papertracker.model.TrackingsRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.papertracker.model.TrackingsResponse;
import it.pagopa.pn.client.b2b.pa.service.IPnPaperTrackerClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component()
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnPaperTrackerClientImpl implements IPnPaperTrackerClient {

    private final PaperTrackerTrackingApi paperTrackerTrackingApi;
    private final PaperTrackerOutputApi paperTrackerOutputApi;
    private final PaperTrackerErrorApi paperTrackerErrorApi;

    private final RestTemplate restTemplate;
    private final String paperTrackerBaseUrl;

    @Autowired
    public PnPaperTrackerClientImpl(RestTemplate restTemplate, @Value("${pn.internal.delivery-push-base-url}") String paperTrackerBaseUrl) {
        this.restTemplate = restTemplate;
        this.paperTrackerBaseUrl = paperTrackerBaseUrl;
        this.paperTrackerTrackingApi = new PaperTrackerTrackingApi(newApiClient(restTemplate, paperTrackerBaseUrl));
        this.paperTrackerOutputApi = new PaperTrackerOutputApi(newApiClient(restTemplate, paperTrackerBaseUrl));
        this.paperTrackerErrorApi = new PaperTrackerErrorApi(newApiClient(restTemplate, paperTrackerBaseUrl));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        return newApiClient;
    }

    @Override
    public TrackingsResponse retrieveTrackerEvents(TrackingsRequest trackingsRequest) {
        return paperTrackerTrackingApi.retrieveTrackings(trackingsRequest);
    }

    @Override
    public PaperTrackerOutputsResponse retrieveTrackerOutputs(TrackingsRequest trackingsRequest) {
        return paperTrackerOutputApi.retrieveTrackingOutputs(trackingsRequest);
    }

    @Override
    public TrackingErrorsResponse retrieveTrackerErrors(TrackingsRequest trackingsRequest) {
        return paperTrackerErrorApi.retrieveTrackingErrors(trackingsRequest);
    }

    @Override
    public TrackingsResponse retrieveTrackingsByAttemptId(String attemptId, String pcRetry) {
        return paperTrackerTrackingApi.retrieveTrackingsByAttemptId(attemptId, pcRetry);
    }
}
