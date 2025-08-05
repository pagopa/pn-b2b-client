package it.pagopa.interop.attribute.service;

import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.VerifiedAttribute;
import it.pagopa.interop.common.client.IClient;
import java.util.List;
import java.util.UUID;
import lombok.Data;

public interface IM2MVerifiedAttributeClient extends IClient<VerifiedAttribute, UUID> {
    // TODO 09/07/2025: in QA non sono ancora state rilasciate le API in oggetto, dunque mancano
    //  gli oggetti concreti da poter usare. Si usano i seguenti come placeholders temporanei.
    @Data
    class VerifiedAttributeSeed {
        private String name;
        private String description;
        private String code;

        public VerifiedAttributeSeed name(String name) {
            this.name = name;
            return this;
        }

        public VerifiedAttributeSeed description(String description) {
            this.description = description;
            return this;
        }

        public VerifiedAttributeSeed code(String code) {
            this.code = code;
            return this;
        }
    }

    VerifiedAttribute create(VerifiedAttributeSeed agreementPayload);
    List<UUID> getVerifiers(UUID tenantId, UUID attributeId);
    List<UUID> getRevokers(UUID organizationId, UUID verifiedAttributeId);
}
