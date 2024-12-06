package it.pagopa.interop.authorization.domain;

public class KeyPairPEM {
    private final String privateKey;
    private final String publicKey;

    public KeyPairPEM(String privateKey, String publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }
}
