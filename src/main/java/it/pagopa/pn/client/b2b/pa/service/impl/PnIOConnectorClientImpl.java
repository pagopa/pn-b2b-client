package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.io.connector.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.io.connector.api.IoConnectorApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.io.connector.model.*;
import it.pagopa.pn.client.b2b.pa.service.IPnIOConnectorClient;
import it.pagopa.pn.client.b2b.pa.service.IPnRaddAlternativeV2Client;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.api_AnagraficaCRUD_V2.RegistryV2Api;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD_V2.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnIOConnectorClientImpl implements IPnIOConnectorClient {

    private final IoConnectorApi apiIOConnector;


    public PnIOConnectorClientImpl(RestTemplate restTemplate,
                                   @Value("http://localhost:8080") String basePath
                                   //TODO mettere base path a config
    ) {
        this.apiIOConnector = new IoConnectorApi(newApiClientExternal(restTemplate, basePath));
    }

    private static ApiClient newApiClientExternal(RestTemplate restTemplate, String basePath) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        return newApiClient;
    }


    @Override
    public MessageResponse sendIOMessage(String xPagopaIoconCxId, MessageRequest messageRequest) throws RestClientException {
        return apiIOConnector.sendIOMessage(xPagopaIoconCxId, messageRequest);
    }

    @Override
    public GetProfileResponse getIOProfile(String xPagopaIoconCxId, GetProfileRequest getProfileRequest) throws RestClientException {
        return apiIOConnector.getIOProfile(xPagopaIoconCxId, getProfileRequest);
    }

    @Override
    public GetMessageResponse getMessage(String id, String recipientTaxid) throws RestClientException {
        return apiIOConnector.getMessage(id, recipientTaxid);
    }
}