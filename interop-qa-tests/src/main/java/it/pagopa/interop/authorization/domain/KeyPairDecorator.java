package it.pagopa.interop.authorization.domain;

import it.pagopa.interop.authorization.service.utils.KeyPairGeneratorUtil;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;


@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(staticName = "of")
public class KeyPairDecorator {
    @Getter
    @NonNull
    private final KeyPair keyPair;

    private KeyPairPEM keyPairPEM;
    private String publicKeyBase64WithDelimitators;
    private String publicKeyBase64WithoutDelimitators;
    private String privateKeyBase64WithDelimitators;
    private String privateKeyBase64WithoutDelimitators;

    public static KeyPairDecorator of(@NonNull String keyType, @NonNull int modulusLength) {
        return of(KeyPairGeneratorUtil.createKeyPair(keyType, modulusLength));
    }

    public PublicKey getPublic() {
        return keyPair.getPublic();
    }

    public PrivateKey getPrivate() {
        return keyPair.getPrivate();
    }

    public String getPublicPEM() {
        return getKeyPairPEM().getPublicKey();
    }

    public String getPrivatePEM() {
        return getKeyPairPEM().getPrivateKey();
    }

    public KeyPairPEM getKeyPairPEM() {
        if (this.keyPairPEM == null) {
            this.keyPairPEM = KeyPairGeneratorUtil.keyPairToPEM(keyPair);
        }
        return this.keyPairPEM;
    }

    public String getDelimitedPublicKeyBase64() {
        if (this.publicKeyBase64WithDelimitators == null) {
            this.publicKeyBase64WithDelimitators = KeyPairGeneratorUtil.keyToBase64(getKeyPairPEM().getPublicKey(), true);
        }
        return this.publicKeyBase64WithDelimitators;
    }

    public String getUndelimitedPublicKeyBase64() {
        if (this.publicKeyBase64WithoutDelimitators == null) {
            this.publicKeyBase64WithoutDelimitators = KeyPairGeneratorUtil.keyToBase64(getKeyPairPEM().getPublicKey(), false);
        }
        return this.publicKeyBase64WithoutDelimitators;
    }

    public String getDelimitedPrivateKeyBase64() {
        if (this.privateKeyBase64WithDelimitators == null) {
            this.privateKeyBase64WithDelimitators = KeyPairGeneratorUtil.keyToBase64(getKeyPairPEM().getPrivateKey(), true);
        }
        return this.privateKeyBase64WithDelimitators;
    }

    public String getUndelimitedPrivateKeyBase64() {
        if (this.privateKeyBase64WithoutDelimitators == null) {
            this.privateKeyBase64WithoutDelimitators = KeyPairGeneratorUtil.keyToBase64(getKeyPairPEM().getPrivateKey(), false);
        }
        return this.privateKeyBase64WithoutDelimitators;
    }
}