package it.pagopa.interop.attribute.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttributeSeed;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

public interface IM2MAttributeClient extends SettableBearerToken {
    @Data
    @Builder
    class AttributeListRequest {

    }

    CertifiedAttribute createCertifiedAttribute(CertifiedAttributeSeed attributePayload);
    CertifiedAttribute getCertifiedAttribute(UUID id);
}
