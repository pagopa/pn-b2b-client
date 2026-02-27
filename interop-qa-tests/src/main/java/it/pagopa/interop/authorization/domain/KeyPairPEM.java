package it.pagopa.interop.authorization.domain;

import static it.pagopa.interop.authorization.service.utils.KeyPairUtil.stringToPrivateKey;
import static it.pagopa.interop.authorization.service.utils.KeyPairUtil.stringToPublicKey;

import java.security.PrivateKey;
import java.security.PublicKey;
import lombok.Value;

@Value
public class KeyPairPEM {
    String privateKey;
    String publicKey;

    public PublicKey getPublicKeyAsObj(String keyType) {
        return stringToPublicKey(this.getPublicKey(), keyType);
    }

    public PrivateKey getPrivateKeyAsObj(String keyType) {
        return stringToPrivateKey(this.getPrivateKey(), keyType);
    }
}
