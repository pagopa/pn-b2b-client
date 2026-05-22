package it.pagopa.pn.interop.cucumber.steps.dev_tools.model;

import it.pagopa.interop.generated.openapi.clients.bff.model.TokenGenerationValidationResult;
import lombok.Data;

@Data
public class DevToolsContext {
    private String actualClientAssertion;
    private String actualDpopProof;
    private TokenGenerationValidationResult lastValidationResult;
}
