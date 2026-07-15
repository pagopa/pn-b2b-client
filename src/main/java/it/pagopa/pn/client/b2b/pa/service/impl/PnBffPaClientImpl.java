package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.pa.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.pa.info.InfoPaApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.pa.recipient.NotificationSentApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.sender.dashboard.SenderDashboardApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.info.BffAdditionalLanguages;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffDocumentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffDocumentType;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffFullNotificationV1;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffNewNotificationRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffNewNotificationResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffLegalNotificationsResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffRequestStatus;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.NotificationStatusV26;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.sender.dashboard.BffSenderDashboardDataResponse;
import it.pagopa.pn.client.b2b.pa.service.IPnWebPaClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Component
public class PnBffPaClientImpl implements IPnWebPaClient {

    private final NotificationSentApi notificationSentApi;
    private final InfoPaApi infoPaApi;
    private final SenderDashboardApi senderDashboardApi;
    private final RestTemplate restTemplate;
    private final String basePath;
    private final String userAgent;

    private final String bearerTokenCom1;
    private final String bearerTokenCom2;
    private final String bearerTokenSON;
    private final String bearerTokenROOT;
    private final String bearerTokenGA;
    private final String bearerTokenSupport1;
    private BearerTokenType bearerTokenSetted;
    private final String apiKeySupport;


    public PnBffPaClientImpl(RestTemplate restTemplate,
                             @Value("${pn.webapi.external.base-url}") String basePath,
                             @Value("${pn.external.bearer-token-pa-1}") String bearerTokenCom1,
                             @Value("${pn.external.bearer-token-pa-2}") String bearerTokenCom2,
                             @Value("${pn.external.bearer-token-pa-SON}") String bearerTokenSON,
                             @Value("${pn.external.bearer-token-pa-ROOT}") String bearerTokenROOT,
                             @Value("${pn.external.bearer-token-pa-GA}") String bearerTokenGA,
                             @Value("${pn.external.bearer-token-pa-support-1}") String bearerTokenSupport1,
                             @Value("${pn.webapi.external.user-agent}") String userAgent,
                             @Value("${pn.external.senderId-GA}") String senderIdSupport) {
        this.bearerTokenCom1 = bearerTokenCom1;
        this.bearerTokenCom2 = bearerTokenCom2;
        this.bearerTokenSON = bearerTokenSON;
        this.bearerTokenROOT = bearerTokenROOT;
        this.bearerTokenGA = bearerTokenGA;
        this.bearerTokenSupport1 = bearerTokenSupport1;
        this.restTemplate = restTemplate;
        this.basePath = basePath;
        this.userAgent = userAgent;
        this.apiKeySupport = senderIdSupport;
        this.notificationSentApi = new NotificationSentApi(newBffApiClient(restTemplate, basePath, bearerTokenCom1, userAgent));
        this.infoPaApi = new InfoPaApi(newBffApiClient(restTemplate, basePath, bearerTokenCom1, userAgent));
        this.senderDashboardApi = new SenderDashboardApi(newApiClient(restTemplate, basePath, bearerTokenCom1, userAgent));
    }

    private static ApiClient newBffApiClient(RestTemplate restTemplate, String basePath, String bearerToken, String userAgent) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("user-agent", userAgent);
        newApiClient.addDefaultHeader("Authorization", "Bearer " + bearerToken);
        return newApiClient;
    }

    private static it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.sender.ApiClient newApiClient(RestTemplate restTemplate, String basePath, String bearerToken, String userAgent) {
        it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.sender.ApiClient newApiClient = new it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.sender.ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("user-agent", userAgent);
        newApiClient.addDefaultHeader("Authorization", "Bearer " + bearerToken);
        return newApiClient;
    }

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        switch (bearerToken) {
            case MVP_1 -> {
                    this.notificationSentApi.setApiClient(newBffApiClient(restTemplate, basePath, bearerTokenCom1, userAgent));
                    this.infoPaApi.setApiClient(newBffApiClient(restTemplate, basePath, bearerTokenCom1, userAgent));
                    this.senderDashboardApi.setApiClient(newApiClient(restTemplate, basePath, bearerTokenCom1, userAgent));
            }
            case MVP_2 -> {
                    this.notificationSentApi.setApiClient(newBffApiClient(restTemplate, basePath, bearerTokenCom2, userAgent));
                    this.infoPaApi.setApiClient(newBffApiClient(restTemplate, basePath, bearerTokenCom2, userAgent));
                    this.senderDashboardApi.setApiClient(newApiClient(restTemplate, basePath, bearerTokenCom2, userAgent));
            }
            case GA -> {
                    this.notificationSentApi.setApiClient(newBffApiClient(restTemplate, basePath, bearerTokenGA, userAgent));
                    this.infoPaApi.setApiClient(newBffApiClient(restTemplate, basePath, bearerTokenGA, userAgent));
                    this.senderDashboardApi.setApiClient(newApiClient(restTemplate, basePath, bearerTokenGA, userAgent));
            }
            case SON -> {
                    this.notificationSentApi.setApiClient(newBffApiClient(restTemplate, basePath, bearerTokenSON, userAgent));
                    this.infoPaApi.setApiClient(newBffApiClient(restTemplate, basePath, bearerTokenSON, userAgent));
                    this.senderDashboardApi.setApiClient(newApiClient(restTemplate, basePath, bearerTokenSON, userAgent));
            }
            case ROOT -> {
                    this.notificationSentApi.setApiClient(newBffApiClient(restTemplate, basePath, bearerTokenROOT, userAgent));
                    this.infoPaApi.setApiClient(newBffApiClient(restTemplate, basePath, bearerTokenROOT, userAgent));
                    this.senderDashboardApi.setApiClient(newApiClient(restTemplate, basePath, bearerTokenROOT, userAgent));
            }
            case SUPPORT_1 -> {
                    this.notificationSentApi.setApiClient(newBffApiClient(restTemplate, basePath, bearerTokenSupport1, userAgent));
                    this.infoPaApi.setApiClient(newBffApiClient(restTemplate, basePath, bearerTokenSupport1, userAgent));
                    this.senderDashboardApi.setApiClient(newApiClient(restTemplate, basePath, bearerTokenSupport1, userAgent));
            }
            default -> throw new IllegalStateException("Unexpected value: " + bearerToken);
        }
        return true;
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return this.bearerTokenSetted;
    }

    @Override
    public BffLegalNotificationsResponse searchSentNotification(OffsetDateTime startDate, OffsetDateTime endDate, String recipientId, NotificationStatusV26 status, String subjectRegExp, String iunMatch, Integer size, String nextPagesKey) throws RestClientException {
        BffLegalNotificationsResponse response = this.notificationSentApi.searchSentNotificationsV1(startDate, endDate, recipientId, status, subjectRegExp, iunMatch, size, nextPagesKey);
        return response;
    }

    @Override
    public BffNewNotificationResponse newSentNotificationV1(BffNewNotificationRequest notificationRequest) throws RestClientException {
        return this.notificationSentApi.newSentNotificationV1(notificationRequest);
    }

    @Override
    public BffFullNotificationV1 getSentNotificationV1(String iun) throws RestClientException {
        return this.notificationSentApi.getSentNotificationV1(iun);
    }

    public BffRequestStatus notificationCancellationV1(String iun) throws RestClientException {
        return this.notificationSentApi.notificationCancellationV1(iun);
    }

    public BffAdditionalLanguages changeAdditionalLang(BffAdditionalLanguages bffAdditionalLanguages) throws RestClientException {
        return infoPaApi.changeAdditionalLang(bffAdditionalLanguages);
    }

    public BffDocumentDownloadMetadataResponse getSentNotificationDocumentV1(String iun, BffDocumentType documentType, Integer documentIdx, String documentId) {
        return this.notificationSentApi.getSentNotificationDocumentV1(iun, documentType, documentIdx, documentId);
    }

    public BffDocumentDownloadMetadataResponse getSentNotificationPaymentV1(String iun, Integer recipientIdx, String attachmentName, Integer attachmentIdx) {
        return this.notificationSentApi.getSentNotificationPaymentV1(iun, recipientIdx, attachmentName, attachmentIdx);
    }

    public BffSenderDashboardDataResponse getDashboardDataV1(String cxType, LocalDate startDate, LocalDate endDate) {
        return this.senderDashboardApi.getDashboardDataV1(cxType, apiKeySupport, startDate, endDate);
    }

}
