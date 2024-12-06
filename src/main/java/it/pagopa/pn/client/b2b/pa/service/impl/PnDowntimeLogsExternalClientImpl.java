package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.config.PnBaseUrlConfig;
import it.pagopa.pn.client.b2b.pa.config.PnExternalApiKeyConfig;
import it.pagopa.pn.client.b2b.pa.service.IPnDowntimeLogsClient;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.ApiClient;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.api.DowntimeApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.api.DowntimeInternalApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.api.StatusApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.List;


@Component
public class PnDowntimeLogsExternalClientImpl implements IPnDowntimeLogsClient {
    private final String basePath;
    private final DowntimeApi downtimeApi;
    private final DowntimeInternalApi internalApi;
    private final StatusApi statusApi;
    private final String userAgent;


    @Autowired
    public PnDowntimeLogsExternalClientImpl(RestTemplate restTemplate,
                                            PnBaseUrlConfig pnBaseUrlConfig,
                                            PnExternalApiKeyConfig pnExternalApiKeyConfig) {
        this.basePath = pnBaseUrlConfig.getDeliveryBaseUrl();
        this.userAgent = pnExternalApiKeyConfig.getUserAgent();
        this.statusApi = new StatusApi(newApiClient(restTemplate, basePath, userAgent));
        this.internalApi = new DowntimeInternalApi(newApiClient(restTemplate, basePath, userAgent));
        this.downtimeApi = new DowntimeApi(newApiClient(restTemplate, basePath, userAgent));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String userAgent) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("user-agent", userAgent);
        return newApiClient;
    }

    public PnStatusResponse currentStatus() throws RestClientException {
        return this.downtimeApi.currentStatus();
    }

    public LegalFactDownloadMetadataResponse getLegalFact(String legalFactId) throws RestClientException {
        return this.downtimeApi.getLegalFact(legalFactId);
    }

    public PnStatusResponse status() throws RestClientException {
        return this.statusApi.status();
    }

    public PnDowntimeHistoryResponse statusHistory(OffsetDateTime fromTime, OffsetDateTime toTime, List<PnFunctionality> functionality, String page, String size) throws RestClientException {
        return this.downtimeApi.statusHistory(fromTime, toTime, functionality, page, size);
    }

    public void addStatusChangeEvent(String xPagopaPnUid, List<PnStatusUpdateEvent> pnStatusUpdateEvent) throws RestClientException {
        this.internalApi.addStatusChangeEvent(xPagopaPnUid, pnStatusUpdateEvent);
    }

    @Override
    public PnDowntimeHistoryResponse getResolved(Integer year, Integer month) throws RestClientException {
        return downtimeApi.getResolved(year, month);
    }
}