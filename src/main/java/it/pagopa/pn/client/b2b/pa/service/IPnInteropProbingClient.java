package it.pagopa.pn.client.b2b.pa.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;


public interface IPnInteropProbingClient {

  public ResponseEntity<Void> getEserviceStatus() throws RestClientException;
}