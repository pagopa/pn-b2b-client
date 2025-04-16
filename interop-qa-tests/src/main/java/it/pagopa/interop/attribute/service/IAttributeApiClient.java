package it.pagopa.interop.attribute.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;

import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface IAttributeApiClient extends SettableBearerToken {
    Attribute createCertifiedAttribute(String xCorrelationId, CertifiedAttributeSeed certifiedAttributeSeed);
    Attribute createVerifiedAttribute(String xCorrelationId, AttributeSeed attributeSeed);
    Attribute createDeclaredAttribute(String xCorrelationId, AttributeSeed attributeSeed);
    Attributes getAttributes(String xCorrelationId, Integer limit, Integer offset, List<AttributeKind> kinds, String q, String origin);

    ResponseEntity<Attribute> getAttributeByIdRE(String xCorrelationId, UUID attributeId);
    ResponseEntity<Attribute> createCertifiedAttributeRE(String xCorrelationId, CertifiedAttributeSeed certifiedAttributeSeed);
    ResponseEntity<Attribute> createVerifiedAttributeRE(String xCorrelationId, AttributeSeed certifiedAttributeSeed);
    ResponseEntity<Attribute> createDeclaredAttributeRE(String xCorrelationId, AttributeSeed certifiedAttributeSeed);
}
