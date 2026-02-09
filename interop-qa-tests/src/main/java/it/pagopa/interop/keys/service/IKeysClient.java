package it.pagopa.interop.keys.service;

import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.Key;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.ProducerKey;

public interface IKeysClient {

    Key getJWKByKid(String kid);
    ProducerKey getProducerJWKByKid(String kid);
}
