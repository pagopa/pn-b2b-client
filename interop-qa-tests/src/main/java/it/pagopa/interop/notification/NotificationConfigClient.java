package it.pagopa.interop.notification;

import it.pagopa.interop.common.client.AbstractClient;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.NotificationConfigsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantNotificationConfig;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantNotificationConfigUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UserNotificationConfig;
import it.pagopa.interop.generated.openapi.clients.bff.model.UserNotificationConfigUpdateSeed;
import it.pagopa.interop.utils.HttpCallExecutor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@ToString
@EqualsAndHashCode(callSuper = true)
@Component
public class NotificationConfigClient extends AbstractClient implements INotificationConfigClient {

    private final NotificationConfigsApi configsApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public NotificationConfigClient(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs, HttpCallExecutor httpCallExecutor) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getBaseUrl();
        this.configsApi = new NotificationConfigsApi(createApiClient("dummyBearer"));
        super.httpCallExecutor = httpCallExecutor;
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.configsApi.setApiClient(createApiClient(bearerToken));
    }

    @Override
    public TenantNotificationConfig getTenantConfig(){
        return performOperation(
                configsApi::getTenantNotificationConfigWithHttpInfo
        ).orElseThrow(() -> new IllegalStateException(
                "Errore nel recupero configurazione notifiche per tenant (response non 2xx o body nullo)"
        ));
    }

    @Override
    public UserNotificationConfig getUserConfig(){
        return performOperation(
                configsApi::getUserNotificationConfigWithHttpInfo
        ).orElseThrow(() -> new IllegalStateException(
                "Errore nel recupero configurazione notifiche per tenant (response non 2xx o body nullo)"
        ));
    }

    @Override
    public void updateTenantNotificationConfig(TenantNotificationConfigUpdateSeed seed){
        performOperation(
                () -> configsApi.updateTenantNotificationConfigWithHttpInfo(seed)
        );
    }

    @Override
    public void updateUserNotificationConfig(UserNotificationConfigUpdateSeed seed){
        performOperation(
                () -> configsApi.updateUserNotificationConfigWithHttpInfo(seed)
        );
    }

}
