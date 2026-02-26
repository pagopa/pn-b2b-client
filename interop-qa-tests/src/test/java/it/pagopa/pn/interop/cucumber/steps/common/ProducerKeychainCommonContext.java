package it.pagopa.pn.interop.cucumber.steps.common;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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

    public void addProducerKeychainId(UUID producerKeychainId) {
        producerKeychainIds.add(producerKeychainId);
    }

    public UUID getFirstProducerKeychainId() {
        return producerKeychainIds.get(0);
    }
}
