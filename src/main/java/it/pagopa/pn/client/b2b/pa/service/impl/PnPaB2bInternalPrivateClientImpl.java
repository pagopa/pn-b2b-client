package it.pagopa.pn.client.b2b.pa.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalprivate.api.InternalOnlyApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalprivate.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bPrivateClient;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.api_v27.LegalFactsPrivateApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v27.CxTypeAuthFleet;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v27.LegalFactDownloadMetadataWithContentTypeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

//todo t frontespizio, code refactore e clean

@Component()
public class PnPaB2bInternalPrivateClientImpl implements IPnPaB2bPrivateClient {

    private final InternalOnlyApi internalOnlyApi;
    private final LegalFactsPrivateApi legalFactsPrivateApi;

    private final ObjectMapper objMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    public PnPaB2bInternalPrivateClientImpl(
            RestTemplate restTemplate,
            @Value("${pn.delivery.base-url}") String deliveryBasePath,
            @Value("${pn.internal.delivery-push-base-url}") String deliveryPushBasePath) {

        this.internalOnlyApi = new InternalOnlyApi(newInternalPrivateApiClient(restTemplate, deliveryBasePath));
        this.legalFactsPrivateApi = new LegalFactsPrivateApi(newPrivateDeliveryPushClient(restTemplate, deliveryPushBasePath));
    }
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

    @Override
    public LegalFactDownloadMetadataWithContentTypeResponse getLegalFactByIdPrivate(String recipientInternalId, String iun, String legalFactId, String mandateId, CxTypeAuthFleet xPagopaPnCxType, List<String> xPagopaPnCxGroups) {
        return legalFactsPrivateApi.getLegalFactByIdPrivate(recipientInternalId, iun, legalFactId, mandateId, xPagopaPnCxType, xPagopaPnCxGroups);
    }

    @Override
    public NotificationAttachmentDownloadMetadataResponse getReceivedNotificationDocumentPrivate(String iun, Integer docIdx, String recipientInternalId, String mandateId) {
        return internalOnlyApi.getReceivedNotificationDocumentPrivate(iun, docIdx, recipientInternalId, mandateId);
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
