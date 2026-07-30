package it.pagopa.interop.attribute.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;

import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface IAttributeApiClient extends SettableBearerToken {
    Attribute createCertifiedAttribute(CertifiedAttributeSeed certifiedAttributeSeed);
    Attribute createCertifiedDiscreteAttribute(AttributeSeed attributeSeed);
    Attribute createVerifiedAttribute(AttributeSeed attributeSeed);
    Attribute createDeclaredAttribute(AttributeSeed attributeSeed);
    Attributes getAttributes(Integer limit, Integer offset, List<AttributeKind> kinds, String q, String origin);
    Attribute getAttributeById(UUID attributeId);

    ResponseEntity<Attribute> getAttributeByIdRE(UUID attributeId);
    ResponseEntity<Attribute> createCertifiedAttributeRE(CertifiedAttributeSeed certifiedAttributeSeed);
    ResponseEntity<Attribute> createVerifiedAttributeRE(AttributeSeed certifiedAttributeSeed);
    ResponseEntity<Attribute> createDeclaredAttributeRE(AttributeSeed certifiedAttributeSeed);
}
