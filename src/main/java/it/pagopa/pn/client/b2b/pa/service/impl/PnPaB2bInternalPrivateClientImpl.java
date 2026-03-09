package it.pagopa.pn.client.b2b.pa.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.pn.client.b2b.pa.exception.PnB2bException;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.ApiClient;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.api.NewNotificationApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalprivate.api.InternalOnlyApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalprivate.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bPrivateClient;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.api_v26.LegalFactsPrivateApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v26.CxTypeAuthFleet;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v26.LegalFactDownloadMetadataWithContentTypeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

//todo t frontespizio, code refactore e clean

@Component()
//@ConditionalOnProperty(name = IPnPaB2bClient.IMPLEMENTATION_TYPE_PROPERTY, havingValue = "internal")
public class PnPaB2bInternalPrivateClientImpl implements IPnPaB2bPrivateClient {
    private final NewNotificationApi newNotificationApi;

    private final InternalOnlyApi internalOnlyApi;
    private final LegalFactsPrivateApi legalFactsPrivateApi;

    private final ObjectMapper objMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();
    private final List<String> groups;

    public PnPaB2bInternalPrivateClientImpl(
            RestTemplate restTemplate,
            @Value("${pn.internal.delivery-base-url}") String deliveryBasePath,
            @Value("${pn.internal.delivery-push-base-url}") String deliveryPushBasePath) {

        this.groups = Collections.emptyList();

        this.newNotificationApi = new NewNotificationApi(newApiClient(restTemplate, deliveryBasePath));
        this.internalOnlyApi = new InternalOnlyApi(newInternalPrivateApiClient(restTemplate, deliveryBasePath));
        this.legalFactsPrivateApi = new LegalFactsPrivateApi(newPrivateDeliveryPushClient(restTemplate, deliveryPushBasePath));
    }

    //    private static it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaldeliveryPushb2bpa.ApiClient
//    newApiClient(RestTemplate restTemplate, String basePath, Boolean isDeliveryPushApi) {
//        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaldeliveryPushb2bpa.ApiClient newApiClient = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaldeliveryPushb2bpa.ApiClient(restTemplate);
//        newApiClient.setBasePath(basePath);
//        return newApiClient;
//    }
    private static it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.ApiClient newPrivateDeliveryPushClient(
            RestTemplate restTemplate, String deliveryPushBasePath) {

        it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.ApiClient apiClient =
                new it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.ApiClient(restTemplate);

        apiClient.setBasePath(deliveryPushBasePath);
        return apiClient;
    }

    private static it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalprivate.ApiClient newInternalPrivateApiClient(
            RestTemplate restTemplate, String deliveryBasePath) {

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalprivate.ApiClient apiClient =
                new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalprivate.ApiClient(restTemplate);

        apiClient.setBasePath(deliveryBasePath);
        return apiClient;
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        return newApiClient;
    }

    private <T> T deepCopy(Object obj, Class<T> toClass) {
        try {
            String json = objMapper.writeValueAsString(obj);
            return objMapper.readValue(json, toClass);
        } catch (JsonProcessingException exc) {
            throw new PnB2bException(exc.getMessage());
        }
    }

    @Override
    public LegalFactDownloadMetadataWithContentTypeResponse getLegalFactByIdPrivate(String recipientInternalId, String iun, String legalFactId, String mandateId, CxTypeAuthFleet xPagopaPnCxType, List<String> xPagopaPnCxGroups) {
        return legalFactsPrivateApi.getLegalFactByIdPrivate(recipientInternalId, iun, legalFactId, mandateId, xPagopaPnCxType, xPagopaPnCxGroups);
    }

    @Override
    public NotificationAttachmentDownloadMetadataResponse getReceivedNotificationDocumentPrivate(String iun, Integer docIdx, String recipientInternalId, String mandateId) {
        return internalOnlyApi.getReceivedNotificationDocumentPrivate(iun, docIdx, recipientInternalId, mandateId);
    }

    @Override
    public NotificationAttachmentDownloadMetadataResponse getReceivedNotificationAttachmentPrivate(String iun, String attachmentName, String recipientInternalId, String mandateId, Integer attachmentIdx) {
        return internalOnlyApi.getReceivedNotificationAttachmentPrivate(iun, attachmentName, recipientInternalId, mandateId, attachmentIdx);
    }

    @Override
    public boolean setApiKeys(ApiKeyType apiKey) {
        return false;
    }

    @Override
    public void setApiKey(String apiKey) {

    }

    @Override
    public ApiKeyType getApiKeySetted() {
        return null;
    }
}
