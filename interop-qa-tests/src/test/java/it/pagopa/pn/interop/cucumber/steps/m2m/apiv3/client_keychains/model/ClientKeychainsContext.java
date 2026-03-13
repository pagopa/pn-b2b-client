package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.client_keychains.model;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.Key;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.KeySeed;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ScenarioScope
public class ClientKeychainsContext {
    private KeySeed actualKeySeed;
    private Key key;
}
