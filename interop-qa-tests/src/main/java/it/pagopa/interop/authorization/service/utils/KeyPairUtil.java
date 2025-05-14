package it.pagopa.interop.authorization.service.utils;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.apache.tomcat.util.codec.binary.Base64;

public class KeyPairUtil {

    private KeyPairUtil() {
        throw new AssertionError("This class is an utility class and should not be instantiated");
    }

    /* DEV. NOTE 29/04/2025: oltre a "RSA" un altro key type previsto in alcuni flussi potrebbe
     * essere "Ed25519". Poiché le funzionalità che usano i seguenti metodi al momento prevedono
     * soltanto "RSA" si sceglie di non implementare la decodifica per key type "Ed25519" per
     * ragioni di tempo. */
    public static PublicKey stringToPublicKey(String clientPublicKey, String keyType) {
        try {
            keyTypeCheck(keyType);
            byte[] publicBytes = Base64.decodeBase64(clientPublicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(keyType);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new KeyParsingException(
                "Errore durante la conversione della public key da tipo %s a tipo %s".formatted(String.class.getName(), PublicKey.class.getName()),
                e);
        }
    }

    public static PrivateKey stringToPrivateKey(String clientPrivateKey, String keyType) {
        try {
            keyTypeCheck(keyType);
            byte[] privateBytes = Base64.decodeBase64(clientPrivateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(keyType);
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new KeyParsingException(
                "Errore durante la conversione della private key da tipo %s a tipo %s".formatted(String.class.getName(), PublicKey.class.getName()),
                e);
        }
    }
    /* ****************************************************************************************/

    private static void keyTypeCheck(String keyType) {
        if (!keyType.equals("RSA")) {
            throw new IllegalArgumentException("Tipo di chiave non supportato: " + keyType);
        }
    }
}
