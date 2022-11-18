package it.pagopa.pn.client.b2b.pa.testclient;


import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.ApiClient;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.api.RecipientReadApi;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.FullReceivedNotification;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.NotificationSearchResponse;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.NotificationStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnWebRecipientExternalClientImpl implements IPnWebRecipientClient {

    private final ApplicationContext ctx;
    private final RestTemplate restTemplate;
    private final RecipientReadApi recipientReadApi;

    private BearerTokenType bearerTokenSetted = BearerTokenType.USER_1;
    private final String fieramoscaEBearerToken;
    private final String cristoforoCBearerToken;
    private final String basePath;
    private final String userAgent;

    public PnWebRecipientExternalClientImpl(
            ApplicationContext ctx,
            RestTemplate restTemplate,
            @Value("${pn.webapi.external.base-url}") String basePath,
            @Value("${pn.bearer-token.FieramoscaE}") String fieramoscaEBearerToken,
            @Value("${pn.bearer-token.CristoforoC}") String cristoforoCBearerToken,
            @Value("${pn.webapi.external.user-agent}")String userAgent
    ) {
        this.ctx = ctx;
        this.restTemplate = restTemplate;
        this.fieramoscaEBearerToken = fieramoscaEBearerToken;
        this.cristoforoCBearerToken = cristoforoCBearerToken;
        this.basePath = basePath;
        this.userAgent = userAgent;
        this.recipientReadApi = new RecipientReadApi( newApiClient( restTemplate, basePath, cristoforoCBearerToken,userAgent) );
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String bearerToken, String userAgent ) {
        ApiClient newApiClient = new ApiClient( restTemplate );
        newApiClient.setBasePath( basePath );
        newApiClient.addDefaultHeader("user-agent",userAgent);
        newApiClient.setBearerToken(bearerToken);
        return newApiClient;
    }

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        boolean beenSet = false;
        switch (bearerToken){
            case USER_1:
                this.recipientReadApi.setApiClient(newApiClient( restTemplate, basePath, fieramoscaEBearerToken,userAgent));
                this.bearerTokenSetted = BearerTokenType.USER_1;
                beenSet = true;
                break;
            case USER_2:
                this.recipientReadApi.setApiClient(newApiClient( restTemplate, basePath, cristoforoCBearerToken,userAgent));
                this.bearerTokenSetted = BearerTokenType.USER_1;
                beenSet = true;
                break;
        }
        return beenSet;
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return this.bearerTokenSetted;
    }



    public FullReceivedNotification getReceivedNotification(String iun, String mandateId) throws RestClientException {
        return recipientReadApi.getReceivedNotification(iun, mandateId);
    }

    public NotificationAttachmentDownloadMetadataResponse getReceivedNotificationAttachment(String iun, String attachmentName, String mandateId) throws RestClientException {
        return recipientReadApi.getReceivedNotificationAttachment(iun, attachmentName, mandateId);
    }


    public NotificationAttachmentDownloadMetadataResponse getReceivedNotificationDocument(String iun, Integer docIdx, String mandateId) throws RestClientException {
        return recipientReadApi.getReceivedNotificationDocument(iun, docIdx, mandateId);
    }


    public NotificationSearchResponse searchReceivedNotification(OffsetDateTime startDate, OffsetDateTime endDate, String mandateId, String senderId, NotificationStatus status, String subjectRegExp, String iunMatch, Integer size, String nextPagesKey) throws RestClientException {
        return recipientReadApi.searchReceivedNotification(startDate, endDate, mandateId, senderId, status, subjectRegExp, iunMatch, size, nextPagesKey);
    }




}
