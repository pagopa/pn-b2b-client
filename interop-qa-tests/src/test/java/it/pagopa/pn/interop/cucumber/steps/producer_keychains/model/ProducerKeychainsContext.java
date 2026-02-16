package it.pagopa.pn.interop.cucumber.steps.producer_keychains.model;

import java.util.UUID;
import io.cucumber.spring.ScenarioScope;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Getter
@Setter
@Component
@ScenarioScope
public class ProducerKeychainsContext {
    private UUID producerKeychainId;
}

