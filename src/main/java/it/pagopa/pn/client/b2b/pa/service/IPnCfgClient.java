package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.model.DocumentTypesConfigurations;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.model.UserConfiguration;
import org.springframework.web.client.RestClientException;

public interface IPnCfgClient {

    UserConfiguration getCurrentClientConfig(String clientId) throws RestClientException;

    DocumentTypesConfigurations getDocumentsConfigs() throws RestClientException;
}
