package it.pagopa.interop.attribute.service;

import it.pagopa.interop.common.client.IClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttributeSeed;

import java.util.UUID;

public interface IM2MCertifiedAttributeClient extends IClient<CertifiedAttribute, UUID> {
    CertifiedAttribute create(CertifiedAttributeSeed agreementPayload);
}
