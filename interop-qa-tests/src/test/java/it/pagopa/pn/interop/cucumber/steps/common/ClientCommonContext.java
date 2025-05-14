package it.pagopa.pn.interop.cucumber.steps.common;

import static it.pagopa.interop.authorization.service.utils.KeyPairUtil.stringToPrivateKey;
import static it.pagopa.interop.authorization.service.utils.KeyPairUtil.stringToPublicKey;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
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

    //Represents the public key uploaded to the second client
    private String newClientPublicKey;

    private String newClientPrivateKey;

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

    public PublicKey getClientPublicKeyAsObj() {
        return stringToPublicKey(this.getClientPublicKey(), this.getKeyType());
    }

    public PublicKey getNewClientPublicKeyAsObj() {
        return stringToPublicKey(this.getNewClientPublicKey(), this.getKeyType());
    }

    public PrivateKey getClientPrivateKeyAsObj() {
        return stringToPrivateKey(this.getClientPrivateKey(), this.getKeyType());
    }

    public PrivateKey getNewClientPrivateKeyAsObj() {
        return stringToPrivateKey(this.getNewClientPrivateKey(), this.getKeyType());
    }

}