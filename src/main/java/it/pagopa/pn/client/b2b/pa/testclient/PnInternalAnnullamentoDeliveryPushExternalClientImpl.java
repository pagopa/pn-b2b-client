package it.pagopa.pn.client.b2b.pa.testclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import java.util.Collections;
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
    private final String deliveryBasePath;
    private final String paId;
    private final String operatorId;

    private final ObjectMapper objMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();
    private final List<String> groups;


    public PnInternalAnnullamentoDeliveryPushExternalClientImpl(
            ApplicationContext ctx,
            RestTemplate restTemplate,
            @Value("${pn.internal.delivery-push-base-url}") String deliveryPushBasePath ,
            @Value("${pn.internal.delivery-base-url}") String deliveryBasePath,
            @Value("${pn.internal.pa-id}") String paId
    ) {
        this.paId = paId;
        this.operatorId = "TestMv";
        this.groups = Collections.emptyList();
        this.ctx = ctx;
        this.restTemplate = restTemplate;
        this.basePath = deliveryPushBasePath;
        this.deliveryBasePath = deliveryBasePath;
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
     * @param iun Identificativo Univoco Notifica (required)
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public void notificationCancellation(String iun) throws RestClientException {
        notificationCancellationApi.notificationCancellation(operatorId,  CxTypeAuthFleet.PA,  paId,  iun,  groups);
    }

    //Recupero degli atti opponibili a terzi generati per una notifica

    /**
     * Singolo atto opponibile a terzi
     * Permette di scaricare un atto opponibile a terzi &lt;br/&gt; L&#39;unico valore ammesso per il parametro _x-pagopa-pn-cx-type_ è &#x60;PA&#x60;
     * <p><b>200</b> - OK
     * <p><b>400</b> - Invalid input
     * <p><b>404</b> - Not found
     * <p><b>500</b> - Internal Server Error
     * @param iun Identificativo Univoco Notifica (required)
     * @param legalFactId Identificativo dell&#39;atto opponibile a terzi (required)
     * @param mandateId identificativo della delega (optional)
     * @return LegalFactDownloadMetadataResponse
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public LegalFactDownloadMetadataResponse getLegalFactById(String iun, String legalFactId, UUID mandateId) throws RestClientException {
        return legalFactsApi.getLegalFactById( operatorId,  CxTypeAuthFleet.PA,  paId,  iun,  legalFactId,  groups,  mandateId);
    }

    /**
     * Singolo atto opponibile a terzi
     * Permette di scaricare un atto opponibile a terzi &lt;br/&gt; L&#39;unico valore ammesso per il parametro _x-pagopa-pn-cx-type_ è &#x60;PA&#x60;
     * <p><b>200</b> - OK
     * <p><b>400</b> - Invalid input
     * <p><b>404</b> - Not found
     * <p><b>500</b> - Internal Server Error
     * @param iun Identificativo Univoco Notifica (required)
     * @param legalFactType Categoria dell&#39;atto opponibile a terzi (required)
     * @param legalFactId Identificativo dell&#39;atto opponibile a terzi (required)
     * @param mandateId identificativo della delega (optional)
     * @return LegalFactDownloadMetadataResponse
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public LegalFactDownloadMetadataResponse getLegalFact(String iun, LegalFactCategory legalFactType, String legalFactId,  UUID mandateId) throws RestClientException {
        return legalFactsApi.getLegalFact(operatorId,  CxTypeAuthFleet.PA,  paId, iun, legalFactType, legalFactId, groups, mandateId);
    }

    /**
     * Elenco atti opponibili a terzi
     * Elenca le informazione di categorizzazione di tutti gli atti opponibili a terzi  registrati durante il processo di notifica. &lt;br/&gt; L&#39;unico valore ammesso per il parametro _x-pagopa-pn-cx-type_ è &#x60;PA&#x60;
     * <p><b>200</b> - OK
     * <p><b>400</b> - Invalid input
     * <p><b>404</b> - Not found
     * <p><b>500</b> - Internal Server Error
     * @param iun Identificativo Univoco Notifica (required)
     * @param mandateId identificativo della delega (optional)
     * @return List&lt;LegalFactListElement&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public List<LegalFactListElement> getNotificationLegalFacts(String iun,  UUID mandateId) throws RestClientException {
        return legalFactsApi.getNotificationLegalFacts(operatorId,  CxTypeAuthFleet.PA,  paId, iun, groups, mandateId);
    }

    /**
     * Restituisce il singolo documento identificato dalla documentKey
     * Permette di scaricare un documento correlato alla notificazione
     * <p><b>200</b> - OK
     * <p><b>400</b> - Invalid input
     * <p><b>404</b> - Not found
     * <p><b>500</b> - Internal Server Error
     * @param iun Identificativo Univoco Notifica (required)
     * @param documentType Categoria documento (required)
     * @param documentId Identificativo del documento (required)
     * @param mandateId identificativo della delega (optional)
     * @return DocumentDownloadMetadataResponse
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public DocumentDownloadMetadataResponse getDocumentsWeb( String iun, DocumentCategory documentType, String documentId, UUID mandateId) throws RestClientException {
        return documentsWebApi.getDocumentsWeb(operatorId,  CxTypeAuthFleet.PA,  paId, iun, documentType, documentId, groups, mandateId);
    }
}
