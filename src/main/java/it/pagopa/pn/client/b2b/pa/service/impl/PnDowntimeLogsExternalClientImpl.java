package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.service.IPnDowntimeLogsClient;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.ApiClient;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.api.DowntimeApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.api.DowntimeInternalApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.api.StatusApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.PnDowntimeHistoryResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.PnFunctionality;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.PnStatusResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.PnStatusUpdateEvent;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


@Component
public class PnDowntimeLogsExternalClientImpl implements IPnDowntimeLogsClient {
    private final DowntimeApi downtimeApi;
    private final DowntimeInternalApi internalApi;
    private final StatusApi statusApi;


    public PnDowntimeLogsExternalClientImpl(RestTemplate restTemplate,
                                            @Value("${pn.delivery.base-url}") String basePath,
                                            @Value("${pn.external.bearer-token-pa-1}") String bearerToken,
                                            @Value("${pn.webapi.external.user-agent}")String userAgent) {
        this.statusApi = new StatusApi( newApiClient(restTemplate, basePath, userAgent));
        this.internalApi = new DowntimeInternalApi(newApiClient(restTemplate, basePath, userAgent));
        this.downtimeApi = new DowntimeApi(newApiClient(restTemplate, basePath, userAgent));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String userAgent ) {
        ApiClient newApiClient = new ApiClient( restTemplate );
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