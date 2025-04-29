package it.pagopa.interop.attribute.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;

import java.util.List;

public interface IAttributeApiClient extends SettableBearerToken {
    Attribute createCertifiedAttribute(CertifiedAttributeSeed certifiedAttributeSeed);
    Attribute createVerifiedAttribute(AttributeSeed attributeSeed);
    Attribute createDeclaredAttribute(AttributeSeed attributeSeed);
    Attributes getAttributes(Integer limit, Integer offset, List<AttributeKind> kinds, String q, String origin);

}
