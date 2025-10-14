package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddcoverage.model.Coverage;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddcoverage.model.CreateCoverageRequest;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddcoverage.model.UpdateCoverageRequest;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.privateb2braddalt.model.CheckCoverageRequest;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.privateb2braddalt.model.CheckCoverageResponse;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.privateb2braddalt.model.SearchMode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

public interface IPnRaddCapCoverageClient {

    //todo t cap new api

    Coverage addCoverage(CreateCoverageRequest createCoverageRequest) throws RestClientException;

    Coverage updateCoverage(String cap, String locality, UpdateCoverageRequest updateCoverageRequest) throws RestClientException;

    CheckCoverageResponse checkCoverage(SearchMode searchMode, CheckCoverageRequest checkCoverageRequest) throws RestClientException;

    ResponseEntity<Coverage> addCoverageWithHttpInfo(CreateCoverageRequest createCoverageRequest) throws RestClientException;

    ResponseEntity<Coverage> updateCoverageWithHttpInfo(String cap, String locality, UpdateCoverageRequest updateCoverageRequest) throws RestClientException;

}
