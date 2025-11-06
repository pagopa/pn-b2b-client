package it.pagopa.interop.attribute.service;

import it.pagopa.interop.common.client.IClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.VerifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.VerifiedAttributeSeed;
import java.util.UUID;

public interface IM2MVerifiedAttributeClient extends IClient<VerifiedAttribute, UUID> {
    VerifiedAttribute create(VerifiedAttributeSeed agreementPayload);
}
