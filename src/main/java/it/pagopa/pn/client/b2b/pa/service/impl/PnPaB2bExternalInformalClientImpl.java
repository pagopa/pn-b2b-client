package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.ApiClient;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.api.InformalNotificationTerminationApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.api.MessagesApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.api.NewInformalNotificationApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.api.SenderReadInformalNotificationB2BApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.model.*;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bExternalInformalClient;
import it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenSingleton;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

import static it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenSingleton.INTEROP_ENABLED;

@Component
@Slf4j
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnPaB2bExternalInformalClientImpl implements IPnPaB2bExternalInformalClient {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";

    private final RestTemplate restTemplate;
    private SettableApiKey.ApiKeyType apiKeySetted;
    private final String basePath;
    private final String enableInterop;
    private final InteropTokenSingleton interopTokenSingleton;
    private final String apiKeyMvp1;
    private final String apiKeyMvp2;
    private final String apiKeyGa;
    private final String apiKeySon;
    private final String apiKeyRoot;

    private MessagesApi messagesApi;
    private SenderReadInformalNotificationB2BApi senderReadApi;
    private NewInformalNotificationApi notificationApi;
    private InformalNotificationTerminationApi terminationApi;

    public PnPaB2bExternalInformalClientImpl(
            RestTemplate restTemplate,
            @Value("${pn.external.informal-base-url.pagopa}") String basePath,
            @Value("${pn.interop.enable}") String enableInterop,
            InteropTokenSingleton interopTokenSingleton,
            @Value("${pn.external.api-key}") String apiKeyMvp1,
            @Value("${pn.external.api-key-2}") String apiKeyMvp2,
            @Value("${pn.external.api-key-GA}") String apiKeyGa,
            @Value("${pn.external.api-key-SON}") String apiKeySon,
            @Value("${pn.external.api-key-ROOT}") String apiKeyRoot) {

        this.restTemplate = restTemplate;
        this.basePath = basePath;
        this.enableInterop = enableInterop;
        this.interopTokenSingleton = interopTokenSingleton;
        this.messagesApi = new MessagesApi();
        this.senderReadApi = new SenderReadInformalNotificationB2BApi();
        this.notificationApi = new NewInformalNotificationApi();
        this.terminationApi = new InformalNotificationTerminationApi();
        this.apiKeyMvp1 = apiKeyMvp1;
        this.apiKeyMvp2 = apiKeyMvp2;
        this.apiKeyGa = apiKeyGa;
        this.apiKeySon = apiKeySon;
        this.apiKeyRoot = apiKeyRoot;
    }

//    @Override
//    public List<InformalPreLoadResponse> informalPresignedUploadRequest(String apiKey, List<InformalPreLoadRequest> requests) {
//        NewInformalNotificationApi api = new NewInformalNotificationApi(newExternalInformalApiClient(apiKey));
//        return api.informalPresignedUploadRequest(requests);
//        this.apiKeyMvp1 = apiKeyMvp1;
//        this.apiKeyMvp2 = apiKeyMvp2;
//        this.apiKeyGa = apiKeyGa;
//        this.apiKeySon = apiKeySon;
//        this.apiKeyRoot = apiKeyRoot;
//        this.apiKeySetted = SettableApiKey.ApiKeyType.MVP_1;
//        this.messagesApi = new MessagesApi(newExternalInformalApiClient(restTemplate, basePath, apiKeyMvp1));
//        this.senderReadApi = new SenderReadInformalNotificationB2BApi(newExternalInformalApiClient(restTemplate, basePath, apiKeyMvp1));
//        this.notificationApi = new NewInformalNotificationApi(newExternalInformalApiClient(restTemplate, basePath, apiKeyMvp1));
//        this.terminationApi = new InformalNotificationTerminationApi(newExternalInformalApiClient(restTemplate, basePath, apiKeyMvp1));
//    }

    //private it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.ApiClient newExternalInformalApiClient(String apiKey) {

    private ApiClient newExternalInformalApiClient(RestTemplate restTemplate, String basePath, String apiKey) {

        var client = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.ApiClient(restTemplate);
        client.setBasePath(basePath);
        client.addDefaultHeader("x-api-key", apiKey);
        if (INTEROP_ENABLED.equalsIgnoreCase(enableInterop)) {
            client.addDefaultHeader(AUTHORIZATION, BEARER + interopTokenSingleton.getTokenInterop());
        }
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

        if (apiKeyType != null) {
            setApiKeys(apiKeyType);
        }
        checkApiKeyConfigured();
        return messagesApi.newMessage(request);
    }

    @Override
    public MessageResponse getMessage(UUID messageId, SettableApiKey.ApiKeyType apiKeyType) {
        if (apiKeyType != null) {
            setApiKeys(apiKeyType);
        }
        checkApiKeyConfigured();
        return messagesApi.messageById(messageId);
    }

    @Override
    public NewInformalNotificationResponse sendNewInformalNotificationV1(InformalNotificationRequestV1 request, SettableApiKey.ApiKeyType apiKeyType) throws RestClientException {
        if (apiKeyType != null) {
            setApiKeys(apiKeyType);
        }
        checkApiKeyConfigured();
        return notificationApi.sendNewInformalNotificationV1(request);
    }

    @Override
    public NewInformalNotificationRequestStatusResponseV1 getNotificationStatusByRequestId(String notificationRequestId, SettableApiKey.ApiKeyType apiKeyType) {

        if (apiKeyType != null) {
            setApiKeys(apiKeyType);
        }
        checkApiKeyConfigured();
        return senderReadApi.retrieveInformalNotificationRequestStatusV1(notificationRequestId, null, null);
    }

    @Override
    public NotificationAttachmentDownloadMetadataResponse getSentInformalNotificationDocument(String iun, int docIdx, SettableApiKey.ApiKeyType apiKeyType) {
        if (apiKeyType != null) {
            setApiKeys(apiKeyType);
        }
        checkApiKeyConfigured();
        return senderReadApi.retrieveSentInformalNotificationDocument(iun, docIdx);
    }

    @Override
    public NotificationAttachmentDownloadMetadataResponse getSentInformalNotificationAttachment(String iun, int recipientIdx, int attachmentIdx, SettableApiKey.ApiKeyType apiKeyType) {
        if (apiKeyType != null) {
            setApiKeys(apiKeyType);
        }
        checkApiKeyConfigured();
        return senderReadApi.retrieveSentInformalNotificationAttachment(iun, recipientIdx, "PAGOPA", attachmentIdx);
    }

    @Override
    public TerminationRequestStatus terminateInformalWorkflow(String iun, SettableApiKey.ApiKeyType apiKeyType) {
        if (apiKeyType != null) {
            setApiKeys(apiKeyType);
        }
        checkApiKeyConfigured();
        return terminationApi.terminateInformalWorkflow(iun);
    }

    @Override
    public FullSentInformalNotificationV1 getSentInformalNotificationSender(String iun, Boolean retrieveMessage, SettableApiKey.ApiKeyType apiKeyType) {

        if (apiKeyType != null) {
            setApiKeys(apiKeyType);
        }
        checkApiKeyConfigured();
        return senderReadApi.getSentInformalNotificationV1(iun, retrieveMessage);
    }


    @Override
    public List<InformalPreLoadResponse> informalPresignedUploadRequest(List<InformalPreLoadRequest> requests, SettableApiKey.ApiKeyType apiKeyType) {
        if (apiKeyType != null) {
            setApiKeys(apiKeyType);
        }
        checkApiKeyConfigured();
        return notificationApi.informalPresignedUploadRequest(requests);
    }


    public void setApiKey(String apiKey) {

        this.messagesApi.setApiClient(newExternalInformalApiClient(restTemplate, basePath, apiKey));
        this.senderReadApi.setApiClient(newExternalInformalApiClient(restTemplate, basePath, apiKey));
        this.notificationApi.setApiClient(newExternalInformalApiClient(restTemplate, basePath, apiKey));
        this.terminationApi.setApiClient(newExternalInformalApiClient(restTemplate, basePath, apiKey));
    }

    public boolean setApiKeys(SettableApiKey.ApiKeyType apiKey) {

        boolean beenSet = false;

        switch (apiKey) {

            case MVP_1 -> {
                if (this.apiKeySetted != SettableApiKey.ApiKeyType.MVP_1) {
                    setApiKey(apiKeyMvp1);
                    this.apiKeySetted = SettableApiKey.ApiKeyType.MVP_1;
                }
                beenSet = true;
            }
            case MVP_2 -> {
                if (this.apiKeySetted != SettableApiKey.ApiKeyType.MVP_2) {
                    setApiKey(apiKeyMvp2);
                    this.apiKeySetted = SettableApiKey.ApiKeyType.MVP_2;
                }
                beenSet = true;
            }
            case GA -> {
                if (this.apiKeySetted != SettableApiKey.ApiKeyType.GA) {
                    setApiKey(apiKeyGa);
                    this.apiKeySetted = SettableApiKey.ApiKeyType.GA;
                }
                beenSet = true;
            }
            case SON -> {
                if (this.apiKeySetted != SettableApiKey.ApiKeyType.SON) {
                    setApiKey(apiKeySon);
                    this.apiKeySetted = SettableApiKey.ApiKeyType.SON;
                }
                beenSet = true;
            }
            case ROOT -> {
                if (this.apiKeySetted != SettableApiKey.ApiKeyType.ROOT) {
                    setApiKey(apiKeyRoot);
                    this.apiKeySetted = SettableApiKey.ApiKeyType.ROOT;
                }
                beenSet = true;
            }
            case SUPPORT_1 -> {
                beenSet = true;
            }
        }
        return beenSet;
    }


    private void checkApiKeyConfigured() {
        if (apiKeySetted == null) {
            throw new IllegalStateException("ApiKeyType not configured");
        }
    }

    public SettableApiKey.ApiKeyType getApiKeySetted() {
        return apiKeySetted;
    }
}
