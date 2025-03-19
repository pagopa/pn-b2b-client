package it.pagopa.interop.attribute.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;

import java.util.List;

public interface IAttributeApiClient extends SettableBearerToken {
    Attribute createCertifiedAttribute(String xCorrelationId, CertifiedAttributeSeed certifiedAttributeSeed);
    Attribute createVerifiedAttribute(String xCorrelationId, AttributeSeed attributeSeed);
    Attribute createDeclaredAttribute(String xCorrelationId, AttributeSeed attributeSeed);
    Attributes getAttributes(String xCorrelationId, Integer limit, Integer offset, List<AttributeKind> kinds, String q, String origin);

}
