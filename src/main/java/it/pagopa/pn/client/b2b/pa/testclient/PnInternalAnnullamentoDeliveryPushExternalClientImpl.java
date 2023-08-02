package it.pagopa.pn.client.b2b.pa.testclient;

import it.pagopa.pn.client.b2b.web.generated.openapi.clients.internalAnnullamentoDeliveryPush.ApiClient;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.internalAnnullamentoDeliveryPush.api.DocumentsWebApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.internalAnnullamentoDeliveryPush.api.HealthCheckApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.internalAnnullamentoDeliveryPush.api.LegalFactsApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.internalAnnullamentoDeliveryPush.api.NotificationCancellationApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.internalAnnullamentoDeliveryPush.model.*;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.api.PaperNotificationFailedApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.api.TimelineAndStatusApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model.NotificationHistoryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class PnInternalAnnullamentoDeliveryPushExternalClientImpl implements IPnInternalAnnullamentoDeliveryPushExternalClientImpl {
    private final ApplicationContext ctx;
    private final RestTemplate restTemplate;

    private final HealthCheckApi healthCheckApi;
    private final DocumentsWebApi documentsWebApi;
    private final LegalFactsApi legalFactsApi;
    private final NotificationCancellationApi notificationCancellationApi;
    private final String basePath;

    public PnInternalAnnullamentoDeliveryPushExternalClientImpl(
            ApplicationContext ctx,
            RestTemplate restTemplate,
            @Value("${pn.internal.delivery-push-base-url}") String deliveryPushBasePath
    ) {
        this.ctx = ctx;
        this.restTemplate = restTemplate;
        this.basePath = deliveryPushBasePath;
        this.healthCheckApi = new HealthCheckApi(newApiClient( restTemplate, basePath));
        this.documentsWebApi = new DocumentsWebApi(newApiClient( restTemplate, basePath));
        this.legalFactsApi = new LegalFactsApi(newApiClient( restTemplate, basePath));
        this.notificationCancellationApi = new NotificationCancellationApi(newApiClient( restTemplate, basePath));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath ) {
        ApiClient newApiClient = new ApiClient( restTemplate );
        newApiClient.setBasePath( basePath );
        // newApiClient.addDefaultHeader("x-api-key", apiKey );
        return newApiClient;
    }


    /**
     * Annullamento notifica
     * Permette l&#39;annullamento di una notifica restituendo una presa in carico, il processo di annullamento si completa poi in maniera asincrona
     * <p><b>204</b> - Accepted - Richiesta annullamento presa in carico
     * <p><b>400</b> - Bad request
     * <p><b>404</b> - Not found
     * <p><b>409</b> - Conflict - La notifica risulta essere già annullata
     * <p><b>500</b> - Internal Server Error
     * @param xPagopaPnUid User Identifier (required)
     * @param xPagopaPnCxType Customer/Receiver Type (required)
     * @param xPagopaPnCxId Customer/Receiver Identifier (required)
     * @param iun Identificativo Univoco Notifica (required)
     * @param xPagopaPnCxGroups Customer Groups (optional)
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public void notificationCancellation(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String iun, List<String> xPagopaPnCxGroups) throws RestClientException {
        notificationCancellationApi.notificationCancellation(xPagopaPnUid,  xPagopaPnCxType,  xPagopaPnCxId,  iun,  xPagopaPnCxGroups);
    }

    //Recupero degli atti opponibili a terzi generati per una notifica

    /**
     * Singolo atto opponibile a terzi
     * Permette di scaricare un atto opponibile a terzi &lt;br/&gt; L&#39;unico valore ammesso per il parametro _x-pagopa-pn-cx-type_ è &#x60;PA&#x60;
     * <p><b>200</b> - OK
     * <p><b>400</b> - Invalid input
     * <p><b>404</b> - Not found
     * <p><b>500</b> - Internal Server Error
     * @param xPagopaPnUid User Identifier (required)
     * @param xPagopaPnCxType Customer/Receiver Type (required)
     * @param xPagopaPnCxId Customer/Receiver Identifier (required)
     * @param iun Identificativo Univoco Notifica (required)
     * @param legalFactId Identificativo dell&#39;atto opponibile a terzi (required)
     * @param xPagopaPnCxGroups Customer Groups (optional)
     * @param mandateId identificativo della delega (optional)
     * @return LegalFactDownloadMetadataResponse
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public LegalFactDownloadMetadataResponse getLegalFactById(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String iun, String legalFactId, List<String> xPagopaPnCxGroups, UUID mandateId) throws RestClientException {
        return legalFactsApi.getLegalFactById( xPagopaPnUid,  xPagopaPnCxType,  xPagopaPnCxId,  iun,  legalFactId,  xPagopaPnCxGroups,  mandateId);
    }

    /**
     * Singolo atto opponibile a terzi
     * Permette di scaricare un atto opponibile a terzi &lt;br/&gt; L&#39;unico valore ammesso per il parametro _x-pagopa-pn-cx-type_ è &#x60;PA&#x60;
     * <p><b>200</b> - OK
     * <p><b>400</b> - Invalid input
     * <p><b>404</b> - Not found
     * <p><b>500</b> - Internal Server Error
     * @param xPagopaPnUid User Identifier (required)
     * @param xPagopaPnCxType Customer/Receiver Type (required)
     * @param xPagopaPnCxId Customer/Receiver Identifier (required)
     * @param iun Identificativo Univoco Notifica (required)
     * @param legalFactType Categoria dell&#39;atto opponibile a terzi (required)
     * @param legalFactId Identificativo dell&#39;atto opponibile a terzi (required)
     * @param xPagopaPnCxGroups Customer Groups (optional)
     * @param mandateId identificativo della delega (optional)
     * @return LegalFactDownloadMetadataResponse
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public LegalFactDownloadMetadataResponse getLegalFact(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String iun, LegalFactCategory legalFactType, String legalFactId, List<String> xPagopaPnCxGroups, UUID mandateId) throws RestClientException {
        return legalFactsApi.getLegalFact(xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, iun, legalFactType, legalFactId, xPagopaPnCxGroups, mandateId);
    }

    /**
     * Elenco atti opponibili a terzi
     * Elenca le informazione di categorizzazione di tutti gli atti opponibili a terzi  registrati durante il processo di notifica. &lt;br/&gt; L&#39;unico valore ammesso per il parametro _x-pagopa-pn-cx-type_ è &#x60;PA&#x60;
     * <p><b>200</b> - OK
     * <p><b>400</b> - Invalid input
     * <p><b>404</b> - Not found
     * <p><b>500</b> - Internal Server Error
     * @param xPagopaPnUid User Identifier (required)
     * @param xPagopaPnCxType Customer/Receiver Type (required)
     * @param xPagopaPnCxId Customer/Receiver Identifier (required)
     * @param iun Identificativo Univoco Notifica (required)
     * @param xPagopaPnCxGroups Customer Groups (optional)
     * @param mandateId identificativo della delega (optional)
     * @return List&lt;LegalFactListElement&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public List<LegalFactListElement> getNotificationLegalFacts(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String iun, List<String> xPagopaPnCxGroups, UUID mandateId) throws RestClientException {
        return legalFactsApi.getNotificationLegalFacts(xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, iun, xPagopaPnCxGroups, mandateId);
    }

    /**
     * Restituisce il singolo documento identificato dalla documentKey
     * Permette di scaricare un documento correlato alla notificazione
     * <p><b>200</b> - OK
     * <p><b>400</b> - Invalid input
     * <p><b>404</b> - Not found
     * <p><b>500</b> - Internal Server Error
     * @param xPagopaPnUid User Identifier (required)
     * @param xPagopaPnCxType Customer/Receiver Type (required)
     * @param xPagopaPnCxId Customer/Receiver Identifier (required)
     * @param iun Identificativo Univoco Notifica (required)
     * @param documentType Categoria documento (required)
     * @param documentId Identificativo del documento (required)
     * @param xPagopaPnCxGroups Customer Groups (optional)
     * @param mandateId identificativo della delega (optional)
     * @return DocumentDownloadMetadataResponse
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public DocumentDownloadMetadataResponse getDocumentsWeb(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String iun, DocumentCategory documentType, String documentId, List<String> xPagopaPnCxGroups, UUID mandateId) throws RestClientException {
        return documentsWebApi.getDocumentsWeb(xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, iun, documentType, documentId, xPagopaPnCxGroups, mandateId);
    }
}
