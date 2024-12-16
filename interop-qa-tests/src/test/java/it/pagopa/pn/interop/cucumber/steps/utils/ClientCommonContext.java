package it.pagopa.pn.interop.cucumber.steps.utils;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
    //Represents the public key uploaded to the client
    private String clientPublicKey;
    //Represents the publicKey ID returned when the public key is associated with a client.
    private String keyId;

    public UUID getFirstUser() {
        Assertions.assertFalse(users == null || users.isEmpty());
        return users.get(0);
    }

    public UUID getFirstClient() {
        Assertions.assertFalse(clients == null || clients.isEmpty());
        return clients.get(0);
    }
}
