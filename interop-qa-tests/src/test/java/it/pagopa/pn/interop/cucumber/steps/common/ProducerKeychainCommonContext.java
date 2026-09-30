package it.pagopa.pn.interop.cucumber.steps.common;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import it.pagopa.interop.authorization.domain.KeyPairDecorator;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProducerKeychainCommonContext {
    private List<UUID> producerKeychainIds = new ArrayList<>();
    private List<KeyPairDecorator> producerKeyPairs = new ArrayList<>();

    public void addProducerKeychainId(UUID producerKeychainId) {
        producerKeychainIds.add(producerKeychainId);
    }

    public void addProducerKeyPair(KeyPairDecorator keyPair) {
        producerKeyPairs.add(keyPair);
    }

    public UUID getFirstProducerKeychainId() {
        return producerKeychainIds.get(0);
    }
}
