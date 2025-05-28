package it.pagopa.interop.authorization.service.utils.voucher.domain;

import java.security.PrivateKey;
import java.security.PublicKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ClientAssertionOptions {
    public enum ClientType {
        API,
        CONSUMER
    }

    private ClientType clientType;
    private boolean digestIncluded;
    private String clientId;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private String purposeId;
}
