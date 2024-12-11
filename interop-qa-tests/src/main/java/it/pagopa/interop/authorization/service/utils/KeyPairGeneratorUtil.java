package it.pagopa.interop.authorization.service.utils;

import it.pagopa.interop.authorization.domain.KeyPairPEM;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeySeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeyUse;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class KeyPairGeneratorUtil {

    public static String createBase64PublicKey(String keyType, int keyLength) {
        return createBase64PublicKey(keyType, keyLength, true);
    }

    public static String createBase64PublicKey(String keyType, int keyLength, boolean withDelimitators) {
        KeyPairPEM keyPairPEM = createKeyPairPEM(keyType, keyLength);
        return keyToBase64(keyPairPEM.getPublicKey(), withDelimitators);
    }

    public static KeyPairPEM createKeyPairPEM(String keyType, int modulusLength) {
        KeyPair keyPair;
        KeyPairPEM keyPairPEM;
        try {
            if ("RSA".equalsIgnoreCase(keyType)) {
                // Generazione chiave RSA
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                keyPairGenerator.initialize(modulusLength);
                keyPair = keyPairGenerator.generateKeyPair();
            } else {
                // Generazione chiave NON-RSA (es. Ed25519)
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("Ed25519");
                keyPair = keyPairGenerator.generateKeyPair();
            }
            keyPairPEM = new KeyPairPEM(keyToPEM(keyPair.getPrivate(), keyType), keyToPEM(keyPair.getPublic(), keyType));
        } catch (Exception e) {
            throw new IllegalArgumentException("There was an error while crating the KeyPairPEM: " + e.getMessage(), e);
        }
        return keyPairPEM;
    }

    private static String keyToPEM(Key key, String keyType) {
        String header, footer;
        byte[] encoded = key.getEncoded();

        if ("RSA".equalsIgnoreCase(keyType)) {
            header = key instanceof PrivateKey
                    ? "-----BEGIN RSA PRIVATE KEY-----"
                    : "-----BEGIN RSA PUBLIC KEY-----";
            footer = key instanceof PrivateKey
                    ? "-----END RSA PRIVATE KEY-----"
                    : "-----END RSA PUBLIC KEY-----";
        } else {
            header = key instanceof PrivateKey
                    ? "-----BEGIN PRIVATE KEY-----"
                    : "-----BEGIN PUBLIC KEY-----";
            footer = key instanceof PrivateKey
                    ? "-----END PRIVATE KEY-----"
                    : "-----END PUBLIC KEY-----";
        }

        // Codifica in Base64
        String encodedKey = Base64.getEncoder().encodeToString(encoded);

        // Formatta in PEM
        return String.format("%s\n%s\n%s", header, encodedKey, footer);
    }

    public static String keyToBase64(String key, boolean withDelimitators) {
        // Se includere i delimitatori
        if (withDelimitators) {
            return Base64.getEncoder().encodeToString(key.getBytes());
        } else {
            // Rimuovi i delimitatori e poi codifica in Base64
            String cleanedKey = key
                    .replace("-----BEGIN RSA PUBLIC KEY-----", "")
                    .replace("-----END RSA PUBLIC KEY-----", "")
                    .trim();

            return Base64.getEncoder().encodeToString(cleanedKey.getBytes());
        }
    }

    public static List<KeySeed> createKeySeed(KeyUse use, String alg, String key) {
        KeySeed keySeed = new KeySeed();
        keySeed.setUse(KeyUse.SIG);
        keySeed.setAlg("RS256");
        keySeed.setName("key-" + getRandomInt());
        keySeed.setKey(key);
        return List.of(keySeed);
    }

    private static int getRandomInt() {
        return ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
    }

}
