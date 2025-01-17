package it.pagopa.interop.authorization.domain;

import lombok.Value;

@Value
public class KeyPairPEM {
    String privateKey;
    String publicKey;
}
