package it.pagopa.interop.attribute.service;

import it.pagopa.interop.common.client.IClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DeclaredAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DeclaredAttributeSeed;
import java.util.UUID;

public interface IM2MDeclaredAttributeClient extends IClient<DeclaredAttribute, UUID> {
    DeclaredAttribute create(DeclaredAttributeSeed agreementPayload);
}
