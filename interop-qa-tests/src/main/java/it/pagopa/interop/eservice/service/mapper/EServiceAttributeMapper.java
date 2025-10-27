package it.pagopa.interop.eservice.service.mapper;

import it.pagopa.interop.eservice.service.EServiceAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DeclaredAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorCertifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorDeclaredAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorVerifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersionCertifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.VerifiedAttribute;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EServiceAttributeMapper {
    EServiceAttribute<CertifiedAttribute> map(EServiceDescriptorCertifiedAttribute attributesGroup);
    EServiceAttribute<DeclaredAttribute> map(EServiceDescriptorDeclaredAttribute attributesGroup);
    EServiceAttribute<VerifiedAttribute> map(EServiceDescriptorVerifiedAttribute attributesGroup);

    EServiceAttribute<CertifiedAttribute> map(
        EServiceTemplateVersionCertifiedAttribute attributesGroup);
}
