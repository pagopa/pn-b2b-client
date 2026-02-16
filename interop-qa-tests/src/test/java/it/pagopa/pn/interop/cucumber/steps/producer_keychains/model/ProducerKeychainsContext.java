package it.pagopa.pn.interop.cucumber.steps.producer_keychains.model;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.ProducerKey;
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
    private ProducerKey producerKey;
}

