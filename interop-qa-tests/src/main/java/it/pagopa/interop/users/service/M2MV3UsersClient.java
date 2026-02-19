package it.pagopa.interop.users.service;

import it.pagopa.interop.common.client.AbstractClient;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.api.UsersApi;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.User;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.Users;
import it.pagopa.interop.users.IM2MV3UsersClient;
import it.pagopa.interop.utils.HttpCallExecutor;

import java.util.List;
import java.util.UUID;

import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@ToString
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class M2MV3UsersClient extends AbstractClient implements IM2MV3UsersClient {

    private final UsersApi usersApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public M2MV3UsersClient(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs, HttpCallExecutor httpCallExecutor) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getM2mV3BaseUrl();
        super.httpCallExecutor = httpCallExecutor;

        this.usersApi = new UsersApi(createUsersApiClient());
    }

    private ApiClient createUsersApiClient() {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        return apiClient;
    }

    public User getUser(UUID userId) {
        return performOperation((() -> usersApi.getUserWithHttpInfo(userId))).orElseThrow(() -> new IllegalStateException("Errore nel recupero dell'utente (response non 2xx o body nullo)"));
    }

    public Users getUsers(Integer limit, Integer offset, List<String> roles) {
        return performOperation((() -> usersApi.getUsersWithHttpInfo(limit, offset, roles))).orElseThrow(() -> new IllegalStateException("Errore nel recupero degli utenti (response non 2xx o body nullo)"));
    }
}