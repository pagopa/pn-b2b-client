package it.pagopa.interop;

import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Agreement;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementState;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementSubmission;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Agreements;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttributes;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DeclaredAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DeclaredAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DeclaredAttributes;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Documents;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purposes;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.TenantVerifiedAttributeRevokers;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.TenantVerifiedAttributeVerifiers;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.VerifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.VerifiedAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.VerifiedAttributes;
import java.util.List;
import org.mapstruct.Mapper;

/* 17/02/2026: raccolta di mappers introdotti durante il refactor volto a riutilizzare tutto
* il materiale possibile prodotto per API v2 anche per i test di API v3. Effettua il mapping di
* oggetti facenti parte di API v3 in oggetti facenti parte di API v2, allo scopo di minimizzare
* le modifiche dei client attualmente presenti. */
@Mapper(componentModel = "spring")
public interface M2MVersionsMapper {
    TenantVerifiedAttributeVerifiers mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.TenantVerifiedAttributeVerifiers bean);
    TenantVerifiedAttributeRevokers mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.TenantVerifiedAttributeRevokers bean);

    Purposes mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.Purposes bean);

    List<it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.AgreementState> mapToV3(List<AgreementState> bean);
    it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.AgreementSeed mapToV3(AgreementSeed bean);
    it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.AgreementSubmission mapToV3(AgreementSubmission bean);
    Agreement mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.Agreement bean);
    Agreements mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.Agreements bean);

    Documents mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.Documents bean);

    CertifiedAttribute mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.CertifiedAttribute bean);
    CertifiedAttributes mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.CertifiedAttributes bean);
    it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.CertifiedAttributeSeed mapToV3(CertifiedAttributeSeed bean);

    DeclaredAttribute mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.DeclaredAttribute bean);
    DeclaredAttributes mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.DeclaredAttributes bean);
    it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.DeclaredAttributeSeed mapToV3(DeclaredAttributeSeed bean);

    VerifiedAttribute mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.VerifiedAttribute bean);
    VerifiedAttributes mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.VerifiedAttributes bean);
    it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.VerifiedAttributeSeed mapToV3(VerifiedAttributeSeed bean);
}
