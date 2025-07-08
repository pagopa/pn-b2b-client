package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pa.BffApiKeysResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pa.BffRequestApiKeyStatus;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pa.BffRequestNewApiKey;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pa.BffResponseNewApiKey;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import org.springframework.web.client.RestClientException;


public interface IPnApiKeyManagerClient extends SettableApiKey {
    void changeStatusApiKey(String id, BffRequestApiKeyStatus requestApiKeyStatus) throws RestClientException;
    void deleteApiKeys(String id) throws RestClientException;
    BffApiKeysResponse getApiKeys(Integer limit, String lastKey, String lastUpdate, Boolean showVirtualKey) throws RestClientException;
    BffResponseNewApiKey newApiKey(BffRequestNewApiKey requestNewApiKey) throws RestClientException;
}