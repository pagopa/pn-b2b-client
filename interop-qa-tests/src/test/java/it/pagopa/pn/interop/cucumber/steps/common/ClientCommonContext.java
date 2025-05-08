package it.pagopa.pn.interop.cucumber.steps.common;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ClientCommonContext {
    private List<UUID> clients = new ArrayList<>();
    private List<UUID> users = new ArrayList<>();

    //Represents the public key uploaded to the client
    private String clientPublicKey;

    private String clientPrivateKey;

    private String keyType;

    //Represents the publicKey ID returned when the public key is associated with a client.
    private String keyId;

    private String newKeyId;

    public UUID getFirstUser() {
        Assertions.assertFalse(users == null || users.isEmpty());
        return users.get(0);
    }

    public UUID getFirstClient() {
        Assertions.assertFalse(clients == null || clients.isEmpty());
        return clients.get(0);
    }

    public UUID getLastClient() {
        Assertions.assertFalse(clients == null || clients.isEmpty());
        return clients.get(clients.size() - 1);
    }

    public void addClient(UUID clientId) {
        if (clients == null) {
            clients = new ArrayList<>();
        }
        clients.add(clientId);
    }

    /* DEV. NOTE 29/04/2025: oltre a "RSA" un altro key type previsto in alcuni flussi potrebbe
    * essere "Ed25519". Poiché le funzionalità che usano i seguenti metodi al momento prevedono
    * soltanto "RSA" si sceglie di non implementare la decodifica per key type "Ed25519" per
    * ragioni di tempo. */
    public PublicKey getClientPublicKeyAsObj() {
        try {
            keyTypeCheck(this.getKeyType());
            byte[] publicBytes = Base64.decodeBase64(this.getClientPublicKey());
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(this.getKeyType());
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new KeyParsingException(
                "Errore durante la conversione della public key da tipo %s a tipo %s".formatted(String.class.getName(), PublicKey.class.getName()),
                e);
        }
    }

    public PrivateKey getClientPrivateKeyAsObj() {
        try {
            keyTypeCheck(this.getKeyType());
            byte[] privateBytes = Base64.decodeBase64(this.getClientPrivateKey());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(this.getKeyType());
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new KeyParsingException(
                "Errore durante la conversione della public key da tipo %s a tipo %s".formatted(String.class.getName(), PublicKey.class.getName()),
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