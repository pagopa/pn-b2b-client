package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.io.connector.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.io.connector.api.IoConnectorApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.io.connector.model.GetMessageResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.io.connector.model.GetProfileRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.io.connector.model.GetProfileResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.io.connector.model.MessageRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.io.connector.model.MessageResponse;
import it.pagopa.pn.client.b2b.pa.service.IPnIOConnectorClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnIOConnectorClientImpl implements IPnIOConnectorClient {

    private final IoConnectorApi apiIOConnector;


    public PnIOConnectorClientImpl(RestTemplate restTemplate,
                                   @Value("${pn.delivery.base-url}") String basePath
                                   
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