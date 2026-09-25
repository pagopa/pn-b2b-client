package it.pagopa.pn.interop.cucumber.steps.common;

import it.pagopa.interop.authorization.service.DPoPTokenService;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ClientCommonContext {
    private List<UUID> clients = new ArrayList<>();
    private List<UUID> users = new ArrayList<>();
    private List<DPoPTokenService.PreparedClient> preparedClients = new ArrayList<>();

    //Represents the public key uploaded to the client
    private String clientPublicKey;

    private PublicKey clientPublicKeyAsObj;

    private String clientPrivateKey;

    private PrivateKey clientPrivateKeyAsObj;

    //Represents the public key uploaded to the second client
    private String newClientPublicKey;

    private PublicKey newClientPublicKeyAsObj;

    private String newClientPrivateKey;

    private PrivateKey newClientPrivateKeyAsObj;

    private String keyType;

    //Represents the publicKey ID returned when the public key is associated with a client.
    private String keyId;

    private UUID adminId;

    // Mantiene il primo client di riferimento per scenari che creano ulteriori batch di client.
    private UUID trackedFirstClientId;

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

    public DPoPTokenService.PreparedClient getLastPreparedClient() {
        Assertions.assertFalse(preparedClients == null || preparedClients.isEmpty());
        return preparedClients.get(preparedClients.size() - 1);
    }

    public void addClient(UUID clientId) {
        if (clients == null) {
            clients = new ArrayList<>();
        }
        clients.add(clientId);
    }

    public void addClient(DPoPTokenService.PreparedClient client){
        preparedClients.add(client);
        if (!this.clients.contains(client.clientId())) {
            addClient(client.clientId());
        }
    }

}