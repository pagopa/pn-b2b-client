package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.service.IPnRaddCapCoverageClient;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddcoverage.api.CoverageApi;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddcoverage.model.Coverage;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddcoverage.model.CreateCoverageRequest;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddcoverage.model.UpdateCoverageRequest;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.privateb2braddalt.api.CoveragePrivateApi;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.privateb2braddalt.model.CheckCoverageRequest;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.privateb2braddalt.model.CheckCoverageResponse;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.privateb2braddalt.model.SearchMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnRaddCapCoverageClientImpl implements IPnRaddCapCoverageClient {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private final CoverageApi apiCapCoverage;
    private final CoveragePrivateApi apiPrivateCoverage;

    public PnRaddCapCoverageClientImpl(RestTemplate restTemplate,
                                       @Value("${pn.radd.alt.external.base-url}") String basePath,
                                       @Value("${pn.externalChannels.base-url}") String basePathPrivate
    ) {
        this.apiCapCoverage = new CoverageApi(newApiClientExternal(restTemplate, basePath, null));
        this.apiPrivateCoverage = new CoveragePrivateApi(newApiClientPrivate(restTemplate, basePathPrivate, null));
    }


    private static it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddcoverage.ApiClient newApiClientExternal(RestTemplate restTemplate, String basePath, String token) {
        it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddcoverage.ApiClient newApiClient = new it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddcoverage.ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader(AUTHORIZATION, BEARER + token);
        return newApiClient;
    }

    private static it.pagopa.pn.client.b2b.radd.generated.openapi.clients.privateb2braddalt.ApiClient newApiClientPrivate(RestTemplate restTemplate, String basePathPrivate, String token) {
        it.pagopa.pn.client.b2b.radd.generated.openapi.clients.privateb2braddalt.ApiClient newApiClient = new it.pagopa.pn.client.b2b.radd.generated.openapi.clients.privateb2braddalt.ApiClient(restTemplate);
        newApiClient.setBasePath(basePathPrivate);
        return newApiClient;
    }


    public void selectRaddista(String token) {

        this.apiCapCoverage.getApiClient().addDefaultHeader("Authorization", "Bearer " + token);

    }

    @Override
    public Coverage addCoverage(CreateCoverageRequest createCoverageRequest) throws RestClientException {
        return this.apiCapCoverage.addCoverage(createCoverageRequest);
    }

    @Override
    public Coverage updateCoverage(String cap, String locality, UpdateCoverageRequest updateCoverageRequest) throws RestClientException {
        return this.apiCapCoverage.updateCoverage(cap, locality, updateCoverageRequest);
    }

    @Override
    public CheckCoverageResponse checkCoverage(SearchMode searchMode, CheckCoverageRequest checkCoverageRequest, LocalDate searchDate) throws RestClientException {
        return apiPrivateCoverage.checkCoverage(searchMode, checkCoverageRequest,searchDate);
    }

    @Override
    public ResponseEntity<Coverage> addCoverageWithHttpInfo(CreateCoverageRequest createCoverageRequest) throws RestClientException {
        return this.apiCapCoverage.addCoverageWithHttpInfo(createCoverageRequest);
    }

    @Override
    public ResponseEntity<Coverage> updateCoverageWithHttpInfo(String cap, String locality, UpdateCoverageRequest updateCoverageRequest) throws RestClientException {
        return this.apiCapCoverage.updateCoverageWithHttpInfo(cap, locality, updateCoverageRequest);
    }
}