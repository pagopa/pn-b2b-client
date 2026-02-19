package it.pagopa.interop;

import it.pagopa.interop.eservice.service.EServiceAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Agreement;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementApprovalPolicy;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementState;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementSubmission;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Agreements;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttributes;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.ConsumerDelegation;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.ConsumerDelegations;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DeclaredAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DeclaredAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DeclaredAttributes;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DelegationRef;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DelegationSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Document;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Documents;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EService;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorCertifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorCertifiedAttributes;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorDeclaredAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorVerifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorVerifiedAttributes;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptors;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplate;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersionCertifiedAttributes;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersionCertifiedAttributesGroup;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersionDeclaredAttributes;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersionDeclaredAttributesGroup;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersionDraftUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersionQuotasUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersionSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersionState;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersionVerifiedAttributes;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersionVerifiedAttributesGroup;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersions;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServices;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.FileDownloadMultipart;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.ProducerDelegation;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplate;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplateDraftUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeVersion;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeVersionSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeVersions;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purposes;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.RiskAnalysisFormSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.TenantVerifiedAttributeRevokers;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.TenantVerifiedAttributeVerifiers;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.VerifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.VerifiedAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.VerifiedAttributes;
import java.util.List;
import java.util.function.Function;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.http.ResponseEntity;

/* 17/02/2026: raccolta di mappers introdotti durante il refactor volto a riutilizzare tutto
* il materiale possibile prodotto per API v2 anche per i test di API v3. Effettua il mapping di
* oggetti facenti parte di API v3 in oggetti facenti parte di API v2, allo scopo di minimizzare
* le modifiche dei client attualmente presenti. */
@Mapper(componentModel = "spring")
public interface M2MVersionsMapper {
    TenantVerifiedAttributeVerifiers mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.TenantVerifiedAttributeVerifiers bean);
    TenantVerifiedAttributeRevokers mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.TenantVerifiedAttributeRevokers bean);

    Purpose mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.Purpose bean);
    Purposes mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.Purposes bean);
    PurposeVersion mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.PurposeVersion bean);
    PurposeVersions mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.PurposeVersions bean);
    it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.PurposeVersionSeed mapToV3(PurposeVersionSeed bean);

    PurposeTemplate mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.PurposeTemplate bean);
    it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.PurposeTemplateDraftUpdateSeed mapToV3(PurposeTemplateDraftUpdateSeed bean);

    it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.RiskAnalysisFormSeed mapToV3(RiskAnalysisFormSeed bean);

    List<it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.AgreementState> mapToV3(List<AgreementState> bean);
    it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.AgreementSeed mapToV3(AgreementSeed bean);
    it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.AgreementSubmission mapToV3(AgreementSubmission bean);
    Agreement mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.Agreement bean);
    Agreements mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.Agreements bean);
    it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.AgreementApprovalPolicy mapToV3(AgreementApprovalPolicy bean);

    Document mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.Document bean);
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

    it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.DelegationSeed mapToV3(DelegationSeed bean);
    ConsumerDelegation mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.ConsumerDelegation bean);
    ConsumerDelegations mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.ConsumerDelegations bean);
    ProducerDelegation mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.ProducerDelegation bean);

    EServiceTemplate mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceTemplate bean);
    it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceTemplateVersionState mapToV3(EServiceTemplateVersionState bean);
    EServiceTemplateVersion mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceTemplateVersion bean);
    EServiceTemplateVersions mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceTemplateVersions bean);
    it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceTemplateVersionSeed mapToV3(EServiceTemplateVersionSeed bean);
    it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceTemplateVersionDraftUpdateSeed mapToV3(EServiceTemplateVersionDraftUpdateSeed bean);
    it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceTemplateVersionQuotasUpdateSeed mapToV3(EServiceTemplateVersionQuotasUpdateSeed bean);

    EService mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EService bean);
    EServices mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServices bean);
    it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceTechnology mapToV3(EServiceTechnology bean);
    it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceMode mapToV3(EServiceMode bean);
    EServiceDescriptor mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceDescriptor bean);
    EServiceDescriptors mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceDescriptors bean);
    it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceDescriptorState mapToV3(EServiceDescriptorState bean);

    EServiceDescriptorCertifiedAttribute mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceDescriptorCertifiedAttribute bean);
    EServiceDescriptorCertifiedAttributes mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceDescriptorCertifiedAttributes bean);
    EServiceDescriptorDeclaredAttribute mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceDescriptorDeclaredAttribute bean);
    EServiceDescriptorVerifiedAttribute mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceDescriptorVerifiedAttribute bean);
    EServiceDescriptorVerifiedAttributes mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceDescriptorVerifiedAttributes bean);

    EServiceTemplateVersionCertifiedAttributes mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceTemplateVersionCertifiedAttributes bean);
    EServiceTemplateVersionCertifiedAttributesGroup mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceTemplateVersionCertifiedAttributesGroup bean);
    EServiceTemplateVersionDeclaredAttributes mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceTemplateVersionDeclaredAttributes bean);
    EServiceTemplateVersionDeclaredAttributesGroup mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceTemplateVersionDeclaredAttributesGroup bean);
    EServiceTemplateVersionVerifiedAttributes mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceTemplateVersionVerifiedAttributes bean);
    EServiceTemplateVersionVerifiedAttributesGroup mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceTemplateVersionVerifiedAttributesGroup bean);

    it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.DelegationRef mapToV3(DelegationRef bean);

    @Mapping(target = "_file", ignore = true) // correttamente mappato attraverso getFile
    FileDownloadMultipart mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.FileDownloadMultipart bean);

    // mappers ausiliari
    default <T, U> ResponseEntity<T> map(ResponseEntity<U> bean, Function<U, T> mapper) {
        return new ResponseEntity<>(
            mapper.apply(bean.getBody()),
            bean.getHeaders(),
            bean.getStatusCode()
        );
    }

    default <T, U> EServiceAttribute<T> map(EServiceAttribute<U> bean, Function<U, T> mapper) {
        return new EServiceAttribute<>(bean.getGroupIndex(), mapper.apply(bean.getAttribute()));
    }
}
