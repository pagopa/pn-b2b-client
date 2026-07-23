package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.FullNotificationSearchResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.LegalNotificationSearchResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.deliverypushb2b.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffDocumentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffFullNotificationV1;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffLegalFactId;
import it.pagopa.pn.client.b2b.pa.domain.Destinatario;
import it.pagopa.pn.client.b2b.pa.domain.NotificationSearchParam;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaldeliveryPushb2bpa.api.LegalFactsApi;
import it.pagopa.pn.client.b2b.pa.provider.DestinatarioRegistry;
import it.pagopa.pn.client.b2b.pa.service.IPnWebRecipientClient;
import it.pagopa.pn.client.b2b.pa.wrapper.BundleFullReceivedNotification;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.ApiClient;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.api.RecipientReadApi;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.CxTypeAuthFleet;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.v25.model.LegalFactCategory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static it.pagopa.pn.client.b2b.pa.domain.Costanti.CUCUMBER_SPA;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.GHERKIN_SRL;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.LEONARDO_DA_VINCI;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.MARIO_CUCUMBER;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.MARIO_GHERKIN;
import static it.pagopa.pn.client.b2b.pa.utils.JsonDeepCopyMapper.deepCopy;

/**
 * Implementazione di IPnWebRecipientClient sull'openapi internal: notifiche e ricerca via
 * RecipientReadApi, atti legali via la LegalFactsApi internal di pn-delivery-push. Usata dal
 * flusso @useB2B per tutte le utenze che NON sono PG dedicate _B2B (PF e PG classiche). Selezionata
 * a runtime dal router {@link B2BRecipientExternalClientImpl}.
 * <p>
 * Le operazioni puramente BFF (mai implementate neanche prima di questo refactor) restano non supportate.
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RecipientInternalClientImpl implements IPnWebRecipientClient {
    private static final String X_PAGOPA_PN_UID = "TestAutomation";
    private static final String SRC_CH_B2B = "B2B";

    private final DestinatarioRegistry destinatarioRegistry;
    private final RecipientReadApi recipientReadApi;
    private final LegalFactsApi legalFactsApi;

    private BearerTokenType bearerTokenSetted;
    private CxTypeAuthFleet xPagopaPnCxType;
    private String xPagopaPnCxId;

    public RecipientInternalClientImpl(RestTemplate restTemplate,
                                       @Value("${pn.delivery.base-url}") String webBasePath,
                                       @Value("${pn.internal.delivery-push-base-url}") String deliveryPushBasePath,
                                       DestinatarioRegistry destinatarioRegistry) {
        this.destinatarioRegistry = destinatarioRegistry;
        this.recipientReadApi = new RecipientReadApi(newApiClient(restTemplate, webBasePath));
        this.legalFactsApi = new LegalFactsApi(newLegalFactsApiClient(restTemplate, deliveryPushBasePath));
        setBearerToken(BearerTokenType.PG_1);
    }

    // le API internal non richiedono bearer token: l'identita' e' veicolata dagli header cx-* risolti sotto
    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        return apiClient;
    }

    private static it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaldeliveryPushb2bpa.ApiClient newLegalFactsApiClient(RestTemplate restTemplate, String basePath) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaldeliveryPushb2bpa.ApiClient apiClient = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaldeliveryPushb2bpa.ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        return apiClient;
    }

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        CxTypeAuthFleet cxType;
        String uid;
        switch (bearerToken) {
            case USER_1 -> {
                cxType = CxTypeAuthFleet.PF;
                uid = destinatarioRegistry.destinatario(MARIO_CUCUMBER).getUid();
            }
            case USER_2 -> {
                cxType = CxTypeAuthFleet.PF;
                uid = destinatarioRegistry.destinatario(MARIO_GHERKIN).getUid();
            }
            case USER_3 -> {
                cxType = CxTypeAuthFleet.PF;
                uid = destinatarioRegistry.destinatario(LEONARDO_DA_VINCI).getUid();
            }
            case PG_1 -> {
                cxType = CxTypeAuthFleet.PG;
                uid = destinatarioRegistry.destinatario(GHERKIN_SRL).getUid();
            }
            case PG_2 -> {
                cxType = CxTypeAuthFleet.PG;
                uid = destinatarioRegistry.destinatario(CUCUMBER_SPA).getUid();
            }
            default -> throw new IllegalStateException("Unexpected value: " + bearerToken);
        }
        this.xPagopaPnCxType = cxType;
        this.xPagopaPnCxId = cxType.getValue() + "-" + uid;
        this.bearerTokenSetted = bearerToken;
        return true;
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return bearerTokenSetted;
    }

    @Override
    public BundleFullReceivedNotification getFullReceivedNotification(String iun, String mandateId) throws RestClientException {
        return deepCopy(
                recipientReadApi.getReceivedNotificationV28(X_PAGOPA_PN_UID, xPagopaPnCxType, xPagopaPnCxId, SRC_CH_B2B, iun, null, null, mandateId),
                BundleFullReceivedNotification.class);
    }

    @Override
    public BffFullNotificationV1 getBffFullNotification(String iun, String mandateId) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public NotificationAttachmentDownloadMetadataResponse getReceivedNotificationAttachment(String iun, String attachmentName, UUID mandateId, Integer attachmentIdx) throws RestClientException {
        return deepCopy(
                recipientReadApi.getReceivedNotificationAttachment(X_PAGOPA_PN_UID, xPagopaPnCxType, xPagopaPnCxId, SRC_CH_B2B, iun, attachmentName, null, null, mandateId, attachmentIdx),
                NotificationAttachmentDownloadMetadataResponse.class);
    }

    @Override
    public BffDocumentDownloadMetadataResponse getReceivedNotificationAttachment(String iun, String attachmentName, UUID mandateId) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public NotificationAttachmentDownloadMetadataResponse getReceivedNotificationDocument(String iun, Integer docIdx, UUID mandateId) throws RestClientException {
        return deepCopy(
                recipientReadApi.getReceivedNotificationDocument(X_PAGOPA_PN_UID, xPagopaPnCxType, xPagopaPnCxId, SRC_CH_B2B, iun, docIdx, null, null, mandateId),
                NotificationAttachmentDownloadMetadataResponse.class);
    }

    @Override
    public LegalNotificationSearchResponse searchReceivedDelegatedNotification(Destinatario destinatario, NotificationSearchParam param) throws RestClientException {
        CxTypeAuthFleet cxType = Optional.ofNullable(resolveActual(param.xPagopaPnCxType, destinatario.getRecipientType()))
                .map(CxTypeAuthFleet::fromValue)
                .orElse(null);
        String cxId = resolveActual(param.xPagopaPnCxId, String.format("%s-%s", destinatario.getRecipientType(), destinatario.getUid()));
        it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.NotificationStatusV26 statusV26 = Optional.ofNullable(param.status)
                .map(it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.NotificationStatusV26::fromValue)
                .orElse(null);
        it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.LegalNotificationSearchResponse response = recipientReadApi.searchReceivedDelegatedNotification(
                param.xPagopaPnUid, cxType, cxId,
                param.startDate, param.endDate, param.xPagopaPnCxGroups, param.senderId, param.recipientId,
                param.group, param.iunMatch, statusV26, param.size, param.nextPagesKey);
        return deepCopy(response, LegalNotificationSearchResponse.class);
    }

    @Override
    public FullNotificationSearchResponse searchReceivedNotification(Destinatario destinatario, NotificationSearchParam param) throws RestClientException {
        CxTypeAuthFleet cxType = Optional.ofNullable(resolveActual(param.xPagopaPnCxType, destinatario.getRecipientType()))
                .map(CxTypeAuthFleet::fromValue)
                .orElse(null);
        String cxId = resolveActual(param.xPagopaPnCxId, String.format("%s-%s", destinatario.getRecipientType(), destinatario.getUid()));
        it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.FullNotificationSearchResponse response = recipientReadApi.searchReceivedNotification(
                param.xPagopaPnUid, cxType, cxId,
                param.startDate, param.endDate, param.xPagopaPnCxGroups, param.mandateId,
                param.senderId, param.iunMatch, param.size, param.nextPagesKey, param.communicationType);
        return deepCopy(response, FullNotificationSearchResponse.class);
    }

    // NotificationSearchParam.ACTUAL (default quando il campo non è specificato in tabella) -> valore derivato dal destinatario;
    // qualunque altro valore, incluso null esplicito (per simulare un campo obbligatorio mancante), passa invariato
    private static String resolveActual(String value, String actualValue) {
        return NotificationSearchParam.ACTUAL.equals(value) ? actualValue : value;
    }

    @Override
    public LegalFactDownloadMetadataResponse getLegalFact(String iun, LegalFactCategory legalFactType, String legalFactId) throws RestClientException {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaldeliveryPushb2bpa.model.CxTypeAuthFleet cxType =
                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaldeliveryPushb2bpa.model.CxTypeAuthFleet.fromValue(xPagopaPnCxType.getValue());
        return deepCopy(
                legalFactsApi.getLegalFactById(X_PAGOPA_PN_UID, cxType, xPagopaPnCxId, iun, legalFactId, null, null),
                LegalFactDownloadMetadataResponse.class);
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
