package it.pagopa.interop.e_service_template;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.eservice.service.EServiceAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DeclaredAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.VerifiedAttribute;

import java.util.List;
import java.util.UUID;

public interface IM2MEServiceTemplateAttributeClient extends SettableBearerToken {
    void addCertifiedAttributes(UUID eServiceId, UUID descriptorId, int groupId, List<UUID> attributes);
    List<EServiceAttribute<CertifiedAttribute>> createCertifiedAttributesGroup(UUID eServiceId, UUID descriptorId, List<UUID> attributes);
    List<EServiceAttribute<CertifiedAttribute>> getCertifiedAttributes(UUID eServiceId, UUID descriptorId);
    void deleteCertifiedAttribute(UUID eServiceId, UUID descriptorId, int groupId, UUID attribute);

    void addDeclaredAttributes(UUID eServiceId, UUID descriptorId, int groupId, List<UUID> attributes);
    List<EServiceAttribute<DeclaredAttribute>> createDeclaredAttributesGroup(UUID eServiceId, UUID descriptorId, List<UUID> attributes);
    List<EServiceAttribute<DeclaredAttribute>> getDeclaredAttributes(UUID eServiceId, UUID descriptorId);
    void deleteDeclaredAttribute(UUID eServiceId, UUID descriptorId, int groupId, UUID attribute);


    void addVerifiedAttributes(UUID eServiceId, UUID descriptorId, int groupId, List<UUID> attributes);
    List<EServiceAttribute<VerifiedAttribute>> createVerifiedAttributesGroup(UUID eServiceId, UUID descriptorId, List<UUID> attributes);
    List<EServiceAttribute<VerifiedAttribute>> getVerifiedAttributes(UUID eServiceId, UUID descriptorId);
    void deleteVerifiedAttribute(UUID eServiceId, UUID descriptorId, int groupId, UUID attribute);



}
