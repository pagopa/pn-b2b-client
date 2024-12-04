package it.pagopa.interop.service;

import it.pagopa.interop.generated.openapi.clients.bff.model.ClientSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.service.utils.SettableBearerToken;

public interface IAuthorizationClientCreate extends SettableBearerToken {

    CreatedResource createConsumerClient(String xCorrelationId, ClientSeed clientSeed);
    CreatedResource createApiClient(String xCorrelationId, ClientSeed clientSeed);

}
