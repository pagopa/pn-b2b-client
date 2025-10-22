package it.pagopa.interop.eservice.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttribute;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.experimental.SuperBuilder;

public interface IM2MEServiceAttributeClient extends SettableBearerToken {
    /* TODO 09/10/2025 modelli di oggetti non ancora rilasciati. Aggiornare una volta ottenute le specifiche. */
    @Data
    @SuperBuilder
    class EServiceAttribute<T> {
        private Integer groupIndex;
        private T attribute;
    }
    /* ******************************/

    List<EServiceAttribute<CertifiedAttribute>> addCertifiedAttributes(UUID eServiceId, UUID descriptorId, int groupId, List<UUID> attributes);
    List<EServiceAttribute<CertifiedAttribute>> createCertifiedAttributesGroup(UUID eServiceId, UUID descriptorId, List<UUID> attributes);
    List<EServiceAttribute<CertifiedAttribute>> getCertifiedAttributes(UUID eServiceId, UUID descriptorId);
    void deleteCertifiedAttribute(UUID eServiceId, UUID descriptorId, int groupId, UUID attribute);
}
