package it.pagopa.interop.eservice.service.mapper;

import it.pagopa.interop.eservice.service.EServiceAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorCertifiedAttribute;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EServiceAttributeMapper {
    EServiceAttribute<CertifiedAttribute> map(EServiceDescriptorCertifiedAttribute attributesGroup);
}
