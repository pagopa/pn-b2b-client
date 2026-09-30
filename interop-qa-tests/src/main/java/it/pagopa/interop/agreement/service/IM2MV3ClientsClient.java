package it.pagopa.interop.agreement.service;

import it.pagopa.interop.authorization.service.utils.Authenticable;
import it.pagopa.interop.authorization.service.utils.SettableHeaders;
import it.pagopa.interop.common.SettableHttpCallExecutor;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.*;

import java.util.UUID;

public interface IM2MV3ClientsClient extends IM2MClientsClient, SettableHeaders, Authenticable, SettableHttpCallExecutor {
    // OPERAZIONI ESCLUSIVE DEL SET M2M V3
    Key createClientKey(UUID clientId, KeySeed keySeed);
    JWKs getClientKeys(UUID clientId, Integer offset, Integer limit);
    Client createClient(ClientSeed clientSeed);
    void deleteClient(UUID clientId);
}