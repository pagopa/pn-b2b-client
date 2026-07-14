package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.api.RecipientReadB2BApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.FullNotificationSearchResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.LegalNotificationSearchResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.NotificationStatusV26;
import it.pagopa.pn.client.b2b.generated.openapi.clients.deliverypushb2b.api.LegalFactsApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.deliverypushb2b.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffDocumentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffFullNotificationV1;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffLegalFactId;
import it.pagopa.pn.client.b2b.pa.domain.Destinatario;
import it.pagopa.pn.client.b2b.pa.domain.NotificationSearchParam;
import it.pagopa.pn.client.b2b.pa.service.IPnWebRecipientClient;
import it.pagopa.pn.client.b2b.pa.wrapper.BundleFullReceivedNotification;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.v25.model.LegalFactCategory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

import static it.pagopa.pn.client.b2b.pa.utils.JsonDeepCopyMapper.deepCopy;

/**
 * Implementazione di IPnWebRecipientClient sull'openapi di destinatari strutturati (b2b-pg-external):
 * riservata alle PG dedicate con token _B2B (pn.bearer-token-b2b.pg1/pg2). Selezionata a runtime
 * dal router {@link B2BRecipientExternalClientImpl}.
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RecipientB2BExternalClientImpl implements IPnWebRecipientClient {
    private final RestTemplate restTemplate;
    private final String b2bBasePath;
    private final String gherkinSrlBearerToken;
    private final String cucumberSpaBearerToken;
    private final RecipientReadB2BApi recipientReadB2BApi;
    private final LegalFactsApi legalFactsApi;

    private BearerTokenType bearerTokenSetted;

    public RecipientB2BExternalClientImpl(RestTemplate restTemplate,
                                          @Value("${pn.external.dest.base-url}") String b2bBasePath,
                                          @Value("${pn.bearer-token-b2b.pg1}") String gherkinSrlBearerToken,
                                          @Value("${pn.bearer-token-b2b.pg2}") String cucumberSpaBearerToken) {
        this.restTemplate = restTemplate;
        this.b2bBasePath = b2bBasePath;
        this.gherkinSrlBearerToken = gherkinSrlBearerToken;
        this.cucumberSpaBearerToken = cucumberSpaBearerToken;
        this.recipientReadB2BApi = new RecipientReadB2BApi(newApiClient(restTemplate, b2bBasePath, gherkinSrlBearerToken));
        this.legalFactsApi = new LegalFactsApi(newLegalFactApiClient(restTemplate, b2bBasePath, gherkinSrlBearerToken));
        setBearerToken(BearerTokenType.PG_B2B_1);
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    private static it.pagopa.pn.client.b2b.generated.openapi.clients.deliverypushb2b.ApiClient newLegalFactApiClient(RestTemplate restTemplate, String basePath, String bearerToken) {
        it.pagopa.pn.client.b2b.generated.openapi.clients.deliverypushb2b.ApiClient apiClient = new it.pagopa.pn.client.b2b.generated.openapi.clients.deliverypushb2b.ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        String token = switch (bearerToken) {
            case PG_B2B_1 -> gherkinSrlBearerToken;
            case PG_B2B_2 -> cucumberSpaBearerToken;
            default -> throw new IllegalStateException("Unexpected value: " + bearerToken);
        };
        this.recipientReadB2BApi.setApiClient(newApiClient(restTemplate, b2bBasePath, token));
        this.legalFactsApi.setApiClient(newLegalFactApiClient(restTemplate, b2bBasePath, token));
        this.bearerTokenSetted = bearerToken;
        return true;
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return bearerTokenSetted;
    }

    @Override
    public BundleFullReceivedNotification getFullReceivedNotification(String iun, String mandateId) throws RestClientException {
        return deepCopy(recipientReadB2BApi.getReceivedNotificationV28(iun, mandateId), BundleFullReceivedNotification.class);
    }

    @Override
    public BffFullNotificationV1 getBffFullNotification(String iun, String mandateId) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public NotificationAttachmentDownloadMetadataResponse getReceivedNotificationAttachment(String iun, String attachmentName, UUID mandateId, Integer attachmentIdx) throws RestClientException {
        return recipientReadB2BApi.getReceivedNotificationAttachment(iun, attachmentName, mandateId, null);
    }

    @Override
    public BffDocumentDownloadMetadataResponse getReceivedNotificationAttachment(String iun, String attachmentName, UUID mandateId) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public NotificationAttachmentDownloadMetadataResponse getReceivedNotificationDocument(String iun, Integer docIdx, UUID mandateId) throws RestClientException {
        return recipientReadB2BApi.getReceivedNotificationDocument(iun, docIdx, mandateId);
    }

    @Override
    public LegalNotificationSearchResponse searchReceivedDelegatedNotification(Destinatario destinatario, NotificationSearchParam param) throws RestClientException {
        return recipientReadB2BApi.searchReceivedDelegatedNotification(
                param.startDate.toString(), param.endDate.toString(), param.senderId, param.recipientId,
                param.group, param.iunMatch, convertStatus(it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.NotificationStatusV26.fromValue(param.status)),
                param.size, param.nextPagesKey);
    }

    @Override
    public FullNotificationSearchResponse searchReceivedNotification(Destinatario destinatario, NotificationSearchParam param) throws RestClientException {
        return recipientReadB2BApi.searchReceivedNotification(param.startDate.toString(), param.endDate.toString(), param.mandateId,
                param.senderId, param.subjectRegExp, param.iunMatch, param.size, param.nextPagesKey, param.communicationType);
    }

    private static NotificationStatusV26 convertStatus(it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.NotificationStatusV26 status) {
        return status == null ? null : NotificationStatusV26.fromValue(status.getValue());
    }

    @Override
    public LegalFactDownloadMetadataResponse getLegalFact(String iun, LegalFactCategory legalFactType, String legalFactId) throws RestClientException {
        return legalFactsApi.deliveryPushIunDownloadLegalFactsLegalFactIdGet(iun, legalFactId);
    }

    @Override
    public BffDocumentDownloadMetadataResponse getDocumentsWeb(String iun, String documentId, UUID mandateId) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<BffLegalFactId> getLegalFactsV20(String iun, UUID mandateId) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public BffDocumentDownloadMetadataResponse downloadLegalFactById(String iun, String legalFactId, UUID mandateId) throws RestClientException {
        throw new UnsupportedOperationException();
    }
}
