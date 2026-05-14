package it.pagopa.interop;

import it.pagopa.interop.eservice.service.EServiceAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.function.Function;

/* 17/02/2026: raccolta di mappers introdotti durante il refactor volto a riutilizzare tutto
* il materiale possibile prodotto per API v2 anche per i test di API v3. Effettua il mapping di
* oggetti facenti parte di API v3 in oggetti facenti parte di API v2, allo scopo di minimizzare
* le modifiche dei client attualmente presenti. */
@Mapper(componentModel = "spring")
public interface M2MVersionsMapper {
    TenantVerifiedAttributeVerifiers mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.TenantVerifiedAttributeVerifiers bean);
    TenantVerifiedAttributeRevokers mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.TenantVerifiedAttributeRevokers bean);

    Client mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.Client bean);
    Purpose mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.Purpose bean);
    Purposes mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.Purposes bean);
    PurposeVersion mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.PurposeVersion bean);
    PurposeVersions mapToV2(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.PurposeVersions bean);
    it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.PurposeVersionSeed mapToV3(PurposeVersionSeed bean);
    it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.PurposeVersionState mapToV3(PurposeVersionState bean);
    List<it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.PurposeVersionState> mapToPStateV3(List<PurposeVersionState> bean);

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
    it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceTemplateSeed mapToV3(EServiceTemplateSeed bean);
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
    it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.DescriptorSeedForEServiceCreation mapToV3(DescriptorSeedForEServiceCreation bean);

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

    @Mapping(target = "_file", ignore = true) // correttamente mappato attraverso getFile
    it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.FileDownloadMultipart mapToV3(FileDownloadMultipart bean);

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
