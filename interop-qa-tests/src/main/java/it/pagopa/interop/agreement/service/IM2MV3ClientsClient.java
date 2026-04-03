package it.pagopa.interop.agreement.service;

import it.pagopa.interop.authorization.service.utils.Authenticable;
import it.pagopa.interop.authorization.service.utils.SettableHeaders;
import it.pagopa.interop.common.SettableHttpCallExecutor;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.JWKs;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.Key;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.KeySeed;

import java.util.UUID;

public interface IM2MV3ClientsClient extends IM2MClientsClient, SettableHeaders, Authenticable, SettableHttpCallExecutor {
    Key createClientKey(UUID clientId, KeySeed keySeed);
    JWKs getClientKeys(UUID clientId, Integer offset, Integer limit);
}