package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.service.IPnWebPaClient;
import it.pagopa.pn.client.web.generated.openapi.clients.webPa.ApiClient;
import it.pagopa.pn.client.web.generated.openapi.clients.webPa.api.SenderReadWebApi;
import it.pagopa.pn.client.web.generated.openapi.clients.webPa.model.NotificationSearchResponse;
import it.pagopa.pn.client.web.generated.openapi.clients.webPa.model.NotificationStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;

@Component
public class PnWebPaClientImpl implements IPnWebPaClient {


    private final ApplicationContext ctx;
    private final RestTemplate restTemplate;
    private final SenderReadWebApi senderReadWebApi;
    private final String basePath;
    private final String bearerToken;
    private final String userAgent;

    public PnWebPaClientImpl(
            ApplicationContext ctx,
            RestTemplate restTemplate,
            @Value("${pn.webapi.external.base-url}") String basePath,
            @Value("${pn.external.bearer-token-pa-1}") String bearerToken,
            @Value("${pn.webapi.external.user-agent}")String userAgent
    ) {
        this.ctx = ctx;
        this.restTemplate = restTemplate;
        this.basePath = basePath;
        this.bearerToken = bearerToken;
        this.userAgent = userAgent;
        this.senderReadWebApi = new SenderReadWebApi( newApiClient( restTemplate, basePath, bearerToken,userAgent) );
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String bearerToken, String userAgent ) {
        ApiClient newApiClient = new ApiClient( restTemplate );
        newApiClient.setBasePath( basePath );
        newApiClient.addDefaultHeader("user-agent",userAgent);
        newApiClient.addDefaultHeader("Authorization","Bearer "+bearerToken);
        return newApiClient;
    }

    @Override
    public NotificationSearchResponse searchSentNotification(OffsetDateTime startDate, OffsetDateTime endDate, String recipientId, NotificationStatus status, String subjectRegExp, String iunMatch, Integer size, String nextPagesKey) throws RestClientException {
        return senderReadWebApi.searchSentNotification(startDate, endDate, recipientId, status, subjectRegExp, iunMatch, size, nextPagesKey);
    }
}
