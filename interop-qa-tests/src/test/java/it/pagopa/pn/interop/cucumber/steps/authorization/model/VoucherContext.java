package it.pagopa.pn.interop.cucumber.steps.authorization.model;

import it.pagopa.interop.generated.openapi.clients.bff.model.TokenGenerationValidationResult;
import lombok.Data;

@Data
public class VoucherContext {
    private String actualClientAssertion;
    private String actualDpopProof;
    private TokenGenerationValidationResult lastValidationResult;
    private String actualAsyncAccessToken;
    private String actualInteractionId;
}
