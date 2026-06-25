package it.pagopa.pn.interop.cucumber.steps.producer_keychains.model;

import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.KeySeed;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.ProducerKey;

import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import io.cucumber.spring.ScenarioScope;

@Getter
@Setter
@Component
@ScenarioScope
public class ProducerKeychainsContext {
    private UUID producerKeychainId;
    private ProducerKey producerKey;
    private KeySeed actualKeySeed;
    private String actualName;
    private String expectedName;
    private String actualDescription;
    private String expectedDescription;
    private List<UUID> actualMembers;
    private List<UUID> expectedMembers;
}
