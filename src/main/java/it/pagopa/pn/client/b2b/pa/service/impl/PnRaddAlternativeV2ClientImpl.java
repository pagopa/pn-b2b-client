package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.service.IPnRaddAlternativeV2Client;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.api_AnagraficaCRUD_V2.RegistryV2Api;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD_V2.CreateRegistryRequestV2;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD_V2.GetRegistryResponseV2;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD_V2.RegistryV2;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD_V2.UpdateRegistryRequestV2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnRaddAlternativeV2ClientImpl implements IPnRaddAlternativeV2Client {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private final RegistryV2Api apiAnagraficaCRUDV2;

    private String tokenCognito;

    public PnRaddAlternativeV2ClientImpl(RestTemplate restTemplate,
                                         @Value("${pn.radd.alt.external.base-url}") String basePath

    ) {
        this.apiAnagraficaCRUDV2 = new RegistryV2Api(newApiClientExternal(restTemplate, basePath, null));
    }

    @Override
    public void deleteRegistry(String partnerId, String locationId) throws RestClientException {
        this.apiAnagraficaCRUDV2.deleteRegistry(partnerId, locationId);
    }

    @Override
    public GetRegistryResponseV2 retrieveRegistries(String xPagopaPnCxId, Integer limit, String lastKey) throws RestClientException {
        return this.apiAnagraficaCRUDV2.retrieveRegistries(xPagopaPnCxId, limit, lastKey);
    }

    @Override
    public RegistryV2 updateRegistry(String partnerId, String locationId, UpdateRegistryRequestV2 updateRegistryRequestV2) throws RestClientException {
        return this.apiAnagraficaCRUDV2.updateRegistry(partnerId, locationId, updateRegistryRequestV2);
    }

    @Override
    public ResponseEntity<Void> deleteRegistryWithHttpInfo(String partnerId, String locationId) {
        return this.apiAnagraficaCRUDV2.deleteRegistryWithHttpInfo(partnerId, locationId);
    }

    private static it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.ApiClient newApiClientExternal(RestTemplate restTemplate, String basePath, String token) {
        it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.ApiClient newApiClient = new it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader(AUTHORIZATION, BEARER + token);
        return newApiClient;
    }

    public void selectRaddista(String token) {

        this.apiAnagraficaCRUDV2.getApiClient().addDefaultHeader("Authorization", "Bearer " + token);

    }

    @Override
    public RegistryV2 addRegistry(String partnerId, CreateRegistryRequestV2 createRegistryRequestV2) throws RestClientException {
        return this.apiAnagraficaCRUDV2.addRegistry(partnerId, createRegistryRequestV2);
    }
}