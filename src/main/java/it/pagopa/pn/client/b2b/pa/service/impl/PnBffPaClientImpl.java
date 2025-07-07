package it.pagopa.pn.client.b2b.pa.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.pa.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.pa.recipient.NotificationSentApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffNotificationsResponse;
import it.pagopa.pn.client.b2b.pa.exception.PnB2bException;
import it.pagopa.pn.client.b2b.pa.service.IPnWebPaClient;
import it.pagopa.pn.client.web.generated.openapi.clients.webPa.model.NotificationSearchResponse;
import it.pagopa.pn.client.web.generated.openapi.clients.webPa.model.NotificationStatusV26;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;

@Component
public class PnBffPaClientImpl implements IPnWebPaClient {

    private final NotificationSentApi notificationSentApi;
    private final RestTemplate restTemplate;
    private final String basePath;
    private final String userAgent;

    private final String bearerTokenCom1;
    private final String bearerTokenCom2;
    private final String bearerTokenSON;
    private final String bearerTokenROOT;
    private final String bearerTokenGA;
    private BearerTokenType bearerTokenSetted;


    public PnBffPaClientImpl(RestTemplate restTemplate,
                             @Value("${pn.webapi.external.base-url}") String basePath,
                             @Value("${pn.external.bearer-token-pa-1}") String bearerTokenCom1,
                             @Value("${pn.external.bearer-token-pa-2}") String bearerTokenCom2,
                             @Value("${pn.external.bearer-token-pa-SON}") String bearerTokenSON,
                             @Value("${pn.external.bearer-token-pa-ROOT}") String bearerTokenROOT,
                             @Value("${pn.external.bearer-token-pa-GA}") String bearerTokenGA,
                             @Value("${pn.webapi.external.user-agent}") String userAgent) {
        this.bearerTokenCom1 = bearerTokenCom1;
        this.bearerTokenCom2 = bearerTokenCom2;
        this.bearerTokenSON = bearerTokenSON;
        this.bearerTokenROOT = bearerTokenROOT;
        this.bearerTokenGA = bearerTokenGA;
        this.restTemplate = restTemplate;
        this.basePath = basePath;
        this.userAgent = userAgent;
        this.notificationSentApi = new NotificationSentApi(newApiClient(restTemplate, basePath, bearerTokenCom1, userAgent));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String bearerToken, String userAgent) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("user-agent", userAgent);
        newApiClient.addDefaultHeader("Authorization", "Bearer " + bearerToken);
        return newApiClient;
    }

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        switch (bearerToken) {
            case MVP_1 ->
                    this.notificationSentApi.setApiClient(newApiClient(restTemplate, basePath, bearerTokenCom1, userAgent));
            case MVP_2 ->
                    this.notificationSentApi.setApiClient(newApiClient(restTemplate, basePath, bearerTokenCom2, userAgent));
            case GA ->
                    this.notificationSentApi.setApiClient(newApiClient(restTemplate, basePath, bearerTokenGA, userAgent));
            case SON ->
                    this.notificationSentApi.setApiClient(newApiClient(restTemplate, basePath, bearerTokenSON, userAgent));
            case ROOT ->
                    this.notificationSentApi.setApiClient(newApiClient(restTemplate, basePath, bearerTokenROOT, userAgent));
            default -> throw new IllegalStateException("Unexpected value: " + bearerToken);
        }
        return true;
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return this.bearerTokenSetted;
    }

    @Override
    public NotificationSearchResponse searchSentNotification(OffsetDateTime startDate, OffsetDateTime endDate, String recipientId, NotificationStatusV26 status, String subjectRegExp, String iunMatch, Integer size, String nextPagesKey) throws RestClientException {
        it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.NotificationStatusV26 convertedStatus;
        convertedStatus = deepCopy(status, it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.NotificationStatusV26.class);
        BffNotificationsResponse response = this.notificationSentApi.searchSentNotificationsV1(startDate, endDate, recipientId, convertedStatus, subjectRegExp, iunMatch, size, nextPagesKey);
        return deepCopy(response, NotificationSearchResponse.class);
    }

    private <T> T deepCopy(Object obj, Class<T> toClass) {
        ObjectMapper objMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
        try {
            String json = objMapper.writeValueAsString(obj);
            return objMapper.readValue(json, toClass);
        } catch (JsonProcessingException exc) {
            throw new PnB2bException(exc.getMessage());
        }
    }
}
