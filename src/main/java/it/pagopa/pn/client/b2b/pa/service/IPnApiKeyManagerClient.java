package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import it.pagopa.pn.client.web.generated.openapi.clients.externalApiKeyManager.model.ApiKeysResponse;
import it.pagopa.pn.client.web.generated.openapi.clients.externalApiKeyManager.model.RequestApiKeyStatus;
import it.pagopa.pn.client.web.generated.openapi.clients.externalApiKeyManager.model.RequestNewApiKey;
import it.pagopa.pn.client.web.generated.openapi.clients.externalApiKeyManager.model.ResponseNewApiKey;
import org.springframework.web.client.RestClientException;

public interface IPnApiKeyManagerClient extends SettableApiKey {

    void changeStatusApiKey(String id, RequestApiKeyStatus requestApiKeyStatus) throws RestClientException;

    void deleteApiKeys(String id) throws RestClientException;

    ApiKeysResponse getApiKeys(Integer limit, String lastKey, String lastUpdate, Boolean showVirtualKey) throws RestClientException;

    ResponseNewApiKey newApiKey(RequestNewApiKey requestNewApiKey) throws RestClientException;

}
