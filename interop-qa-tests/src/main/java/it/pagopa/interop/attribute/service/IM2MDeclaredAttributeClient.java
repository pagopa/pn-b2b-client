package it.pagopa.interop.attribute.service;

import it.pagopa.interop.attribute.service.IM2MDeclaredAttributeClient.DeclaredAttribute;
import it.pagopa.interop.common.client.IClient;
import java.util.UUID;
import lombok.Data;

public interface IM2MDeclaredAttributeClient extends IClient<DeclaredAttribute, UUID> {

    // TODO 09/07/2025: in QA non sono ancora state rilasciate le API in oggetto, dunque mancano
    //  gli oggetti concreti da poter usare. Si usano i seguenti come placeholders temporanei.
    @Data
    class DeclaredAttribute { private UUID id; }
    @Data
    class DeclaredAttributeSeed {
        private String name;
        private String description;
        private String code;

        public DeclaredAttributeSeed name(String name) {
            this.name = name;
            return this;
        }

        public DeclaredAttributeSeed description(String description) {
            this.description = description;
            return this;
        }

        public DeclaredAttributeSeed code(String code) {
            this.code = code;
            return this;
        }
    }

    DeclaredAttribute create(DeclaredAttributeSeed agreementPayload);
}
