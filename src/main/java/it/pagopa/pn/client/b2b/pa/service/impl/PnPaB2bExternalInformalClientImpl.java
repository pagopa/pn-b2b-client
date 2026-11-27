package it.pagopa.pn.client.b2b.pa.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.ApiClient;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.api.InformalNotificationTerminationApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.api.MessagesApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.api.NewInformalNotificationApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.api.SenderReadInformalNotificationB2BApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.model.*;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bExternalInformalClient;
import it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenInjector;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnPaB2bExternalInformalClientImpl implements IPnPaB2bExternalInformalClient {

    private final RestTemplate restTemplate;
    private final String basePath;
    private final InteropTokenInjector interopTokenInjector;
    private final String apiKeyMvp1;
    private final String apiKeyMvp2;
    private final String apiKeyGa;
    private final String apiKeySon;
    private final String apiKeyRoot;

    public PnPaB2bExternalInformalClientImpl(RestTemplate restTemplate, @Value("${pn.external.informal-base-url.pagopa}") String basePath, InteropTokenInjector interopTokenInjector, @Value("${pn.external.api-key}") String apiKeyMvp1, @Value("${pn.external.api-key-2}") String apiKeyMvp2, @Value("${pn.external.api-key-GA}") String apiKeyGa, @Value("${pn.external.api-key-SON}") String apiKeySon, @Value("${pn.external.api-key-ROOT}") String apiKeyRoot) {

        this.restTemplate = withIsoDateSerialization(restTemplate);
        this.basePath = basePath;
        this.interopTokenInjector = interopTokenInjector;
        this.apiKeyMvp1 = apiKeyMvp1;
        this.apiKeyMvp2 = apiKeyMvp2;
        this.apiKeyGa = apiKeyGa;
        this.apiKeySon = apiKeySon;
        this.apiKeyRoot = apiKeyRoot;
    }

    /**
     * Il RestTemplate condiviso serializza le date (es. PagoPaPaymentBase.dueDate) come timestamp numerici, rifiutati
     * dalla validazione OpenAPI dell'API esterna (string date-time): questa copia le serializza in ISO-8601 e mantiene
     * request factory ed error handler.
     */
    private static RestTemplate withIsoDateSerialization(RestTemplate sharedRestTemplate) {
        List<HttpMessageConverter<?>> messageConverters = sharedRestTemplate.getMessageConverters().stream()
                .<HttpMessageConverter<?>>map(converter -> converter instanceof MappingJackson2HttpMessageConverter jacksonConverter
                        ? new MappingJackson2HttpMessageConverter(jacksonConverter.getObjectMapper().copy()
                                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS))
                        : converter)
                .toList();
        RestTemplate informalRestTemplate = new RestTemplate(sharedRestTemplate.getRequestFactory());
        informalRestTemplate.setMessageConverters(messageConverters);
        informalRestTemplate.setErrorHandler(sharedRestTemplate.getErrorHandler());
        return informalRestTemplate;
    }

    // Body JSON effettivamente inviato, serializzato con lo stesso ObjectMapper del converter (utile per il debug)
    private String toJsonBody(Object body) {
        return restTemplate.getMessageConverters().stream()
                .filter(MappingJackson2HttpMessageConverter.class::isInstance)
                .map(converter -> ((MappingJackson2HttpMessageConverter) converter).getObjectMapper())
                .findFirst()
                .map(mapper -> {
                    try {
                        return mapper.writeValueAsString(body);
                    } catch (JsonProcessingException e) {
                        return "<serializzazione fallita: " + e.getMessage() + ">";
                    }
                })
                .orElseGet(() -> String.valueOf(body));
    }

    private MessagesApi messagesApi(SettableApiKey.ApiKeyType apiKeyType) {
        return new MessagesApi(newExternalInformalApiClient(restTemplate, basePath, resolveApiKey(apiKeyType)));
    }

    private SenderReadInformalNotificationB2BApi senderReadApi(SettableApiKey.ApiKeyType apiKeyType) {
        return new SenderReadInformalNotificationB2BApi(newExternalInformalApiClient(restTemplate, basePath, resolveApiKey(apiKeyType)));
    }

    private NewInformalNotificationApi notificationApi(SettableApiKey.ApiKeyType apiKeyType) {
        return new NewInformalNotificationApi(newExternalInformalApiClient(restTemplate, basePath, resolveApiKey(apiKeyType)));
    }

    private InformalNotificationTerminationApi terminationApi(SettableApiKey.ApiKeyType apiKeyType) {
        return new InformalNotificationTerminationApi(newExternalInformalApiClient(restTemplate, basePath, resolveApiKey(apiKeyType)));
    }

    private ApiClient newExternalInformalApiClient(RestTemplate restTemplate, String basePath, String apiKey) {

        var client = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.ApiClient(restTemplate);
        client.setBasePath(basePath);
        client.addDefaultHeader("x-api-key", apiKey);
        interopTokenInjector.applyTo(client::addDefaultHeader);
        client.setApiKey(apiKey);

        return client;
    }

    private String resolveApiKey(SettableApiKey.ApiKeyType apiKeyType) {

        return switch (apiKeyType) {
            case MVP_1 -> apiKeyMvp1;
            case MVP_2 -> apiKeyMvp2;
            case GA -> apiKeyGa;
            case SON -> apiKeySon;
            case ROOT -> apiKeyRoot;
            case SUPPORT_1 -> null;
        };
    }


    @Override
    public MessageResponse createMessage(NewMessageRequest request, SettableApiKey.ApiKeyType apiKeyType) {

        return messagesApi(apiKeyType).newMessage(request);
    }

    @Override
    public MessageResponse getMessage(UUID messageId, SettableApiKey.ApiKeyType apiKeyType) {
        return messagesApi(apiKeyType).messageById(messageId);

    }

    @Override
    public NewInformalNotificationResponse sendNewInformalNotificationV1(InformalNotificationRequestV1 request, SettableApiKey.ApiKeyType apiKeyType) throws RestClientException {
        log.info("sendNewInformalNotificationV1 apiKeyType={} body={}", apiKeyType, toJsonBody(request));
        return notificationApi(apiKeyType).sendNewInformalNotificationV1(request);
    }

    @Override
    public NewInformalNotificationRequestStatusResponseV1 getNotificationStatusByRequestId(String notificationRequestId, SettableApiKey.ApiKeyType apiKeyType) {
        return senderReadApi(apiKeyType).retrieveInformalNotificationRequestStatusV1(notificationRequestId, null, null);
    }

    @Override
    public NotificationAttachmentDownloadMetadataResponse getSentInformalNotificationDocument(String iun, int docIdx, SettableApiKey.ApiKeyType apiKeyType) {
        return senderReadApi(apiKeyType).retrieveSentInformalNotificationDocument(iun, docIdx);
    }

    @Override
    public NotificationAttachmentDownloadMetadataResponse getSentInformalNotificationAttachment(String iun, int recipientIdx, int attachmentIdx, SettableApiKey.ApiKeyType apiKeyType) {
        return senderReadApi(apiKeyType).retrieveSentInformalNotificationAttachment(iun, recipientIdx, "PAGOPA", attachmentIdx);
    }

    @Override
    public TerminationRequestStatus terminateInformalWorkflow(String iun, SettableApiKey.ApiKeyType apiKeyType) {
          return terminationApi(apiKeyType).terminateInformalWorkflow(iun);
    }

    @Override
    public FullSentInformalNotificationV1 getSentInformalNotificationSender(String iun, Boolean retrieveMessage, SettableApiKey.ApiKeyType apiKeyType) {
        return senderReadApi(apiKeyType).getSentInformalNotificationV1(iun, retrieveMessage);
    }

    @Override
    public List<InformalPreLoadResponse> informalPresignedUploadRequest(List<InformalPreLoadRequest> requests, SettableApiKey.ApiKeyType apiKeyType) {
        return notificationApi(apiKeyType).informalPresignedUploadRequest(requests);
    }
}
