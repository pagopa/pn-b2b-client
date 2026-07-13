package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.api.RecipientReadB2BApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.FullNotificationSearchResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.LegalNotificationSearchResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.deliverypushb2b.api.LegalFactsApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.deliverypushb2b.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffDocumentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffFullNotificationV1;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffLegalFactId;
import it.pagopa.pn.client.b2b.pa.domain.Destinatario;
import it.pagopa.pn.client.b2b.pa.domain.NotificationSearchParam;
import it.pagopa.pn.client.b2b.pa.service.IPnWebRecipientClient;
import it.pagopa.pn.client.b2b.pa.wrapper.BundleFullReceivedNotification;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.api.RecipientReadApi;
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
 * Instrada le due ricerche notifiche (searchReceivedNotification/searchReceivedDelegatedNotification)
 * su {@link B2BExternalRecipientSearchDelegate} o {@link InternalRecipientSearchDelegate} a seconda
 * che l'utenza attiva sia una PG dedicata _B2B oppure no; la scelta avviene una sola volta in
 * {@link #setBearerToken(BearerTokenType)}. Le altre operazioni (notifica completa, allegati,
 * documenti, atti legali) non hanno un equivalente sull'openapi internal e usano sempre
 * recipientReadB2BApi/legalFactsApi.
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class B2BRecipientExternalClientImpl implements IPnWebRecipientClient {

    private final String marioCucumberBearerToken;
    private final String marioGherkinBearerToken;
    private final String leonardoBearerToken;
    private final String gherkinSrlBearerToken;
    private final String cucumberSpaBearerToken;
    private final RestTemplate restTemplate;
    private final String webBasePath;
    private final String b2bBasePath;
    private final RecipientReadB2BApi recipientReadB2BApi;
    private final RecipientReadApi recipientReadApi;
    private final LegalFactsApi legalFactsApi;
    private BearerTokenType bearerTokenSetted;
    private final String pg1ClassicToken;
    private final String pg2ClassicToken;

    private final RecipientSearchDelegate b2bExternalSearchDelegate;
    private final RecipientSearchDelegate internalSearchDelegate;
    private RecipientSearchDelegate activeSearchDelegate;

    public B2BRecipientExternalClientImpl(RestTemplate restTemplate,
                                          @Value("${pn.delivery.base-url}") String webBasePath,
                                          @Value("${pn.external.dest.base-url}") String b2bBasePath,
                                          @Value("${pn.bearer-token.user1}") String marioCucumberBearerToken,
                                          @Value("${pn.bearer-token.user2}") String marioGherkinBearerToken,
                                          @Value("${pn.bearer-token.user3}") String leonardoBearerToken,
                                          @Value("${pn.bearer-token-b2b.pg1}") String gherkinSrlBearerToken,
                                          @Value("${pn.bearer-token-b2b.pg2}") String cucumberSpaBearerToken,
                                          @Value("${pn.bearer-token.pg1}") String pg1ClassicToken,
                                          @Value("${pn.bearer-token.pg2}") String pg2ClassicToken) {
        this.marioCucumberBearerToken = marioCucumberBearerToken;
        this.marioGherkinBearerToken = marioGherkinBearerToken;
        this.leonardoBearerToken = leonardoBearerToken;
        this.gherkinSrlBearerToken = gherkinSrlBearerToken;
        this.cucumberSpaBearerToken = cucumberSpaBearerToken;
        this.restTemplate = restTemplate;
        this.webBasePath = webBasePath;
        this.b2bBasePath = b2bBasePath;
        this.pg1ClassicToken = pg1ClassicToken;
        this.pg2ClassicToken = pg2ClassicToken;
        this.recipientReadB2BApi = new RecipientReadB2BApi(newApiClient(restTemplate, webBasePath, gherkinSrlBearerToken));
        this.recipientReadApi = new RecipientReadApi(createApiClient(restTemplate, webBasePath, gherkinSrlBearerToken));
        this.legalFactsApi = new LegalFactsApi(newLegalFactApiClient(restTemplate, b2bBasePath, gherkinSrlBearerToken));
        this.b2bExternalSearchDelegate = new B2BExternalRecipientSearchDelegate(recipientReadB2BApi);
        this.internalSearchDelegate = new InternalRecipientSearchDelegate(recipientReadApi);
        setBearerToken(BearerTokenType.PG_1);
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String bearerToken) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.setBearerToken(bearerToken);
        return newApiClient;
    }

    private static it.pagopa.pn.client.b2b.generated.openapi.clients.deliverypushb2b.ApiClient newLegalFactApiClient(RestTemplate restTemplate, String basePath, String bearerToken) {
        it.pagopa.pn.client.b2b.generated.openapi.clients.deliverypushb2b.ApiClient newApiClient = new it.pagopa.pn.client.b2b.generated.openapi.clients.deliverypushb2b.ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.setBearerToken(bearerToken);
        return newApiClient;
    }

    private it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.ApiClient createApiClient(RestTemplate restTemplate, String basePath, String bearerToken) {
        it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.ApiClient newApiClient = new it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        return newApiClient;
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
        return activeSearchDelegate.searchReceivedDelegatedNotification(destinatario, param);
    }

    @Override
    public FullNotificationSearchResponse searchReceivedNotification(Destinatario destinatario, NotificationSearchParam param) throws RestClientException {
        return activeSearchDelegate.searchReceivedNotification(destinatario, param);
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

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        switch (bearerToken) {
            case USER_1 -> {
                this.recipientReadB2BApi.setApiClient(newApiClient(restTemplate, webBasePath, marioCucumberBearerToken));
                activateInternal(marioCucumberBearerToken, bearerToken);
            }
            case USER_2 -> {
                this.recipientReadB2BApi.setApiClient(newApiClient(restTemplate, webBasePath, marioGherkinBearerToken));
                activateInternal(marioGherkinBearerToken, bearerToken);
            }
            case USER_3 -> {
                this.recipientReadB2BApi.setApiClient(newApiClient(restTemplate, webBasePath, leonardoBearerToken));
                activateInternal(leonardoBearerToken, bearerToken);
            }
            case PG_1 -> activateInternal(pg1ClassicToken, bearerToken);
            case PG_2 -> {
                this.legalFactsApi.setApiClient(newLegalFactApiClient(restTemplate, webBasePath, pg2ClassicToken));
                activateInternal(pg2ClassicToken, bearerToken);
            }
            case PG_B2B_1 -> activateB2BExternal(gherkinSrlBearerToken, bearerToken);
            case PG_B2B_2 -> {
                this.legalFactsApi.setApiClient(newLegalFactApiClient(restTemplate, b2bBasePath, cucumberSpaBearerToken));
                activateB2BExternal(cucumberSpaBearerToken, bearerToken);
            }
            default -> throw new IllegalStateException("Unexpected value: " + bearerToken);
        }
        return true;
    }

    // utenze non _B2B (PF o PG classiche): ricerca notifiche sull'API internal
    private void activateInternal(String token, BearerTokenType bearerToken) {
        this.recipientReadApi.setApiClient(createApiClient(restTemplate, webBasePath, token));
        this.activeSearchDelegate = internalSearchDelegate;
        this.bearerTokenSetted = bearerToken;
    }

    // utenze _B2B (PG dedicate): ricerca notifiche sull'API di destinatari strutturati (b2b)
    private void activateB2BExternal(String token, BearerTokenType bearerToken) {
        this.recipientReadB2BApi.setApiClient(newApiClient(restTemplate, b2bBasePath, token));
        this.activeSearchDelegate = b2bExternalSearchDelegate;
        this.bearerTokenSetted = bearerToken;
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return this.bearerTokenSetted;
    }
}
