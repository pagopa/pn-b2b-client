package it.pagopa.interop.dev_tools.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.common.SettableHttpCallExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.TokenGenerationValidationResult;

public interface IDevToolsClient extends SettableBearerToken, SettableHttpCallExecutor {
    TokenGenerationValidationResult validateTokenGeneration(String clientAssertion, String clientAssertionType, String grantType, String clientId, Boolean isAsync, String dpopProof);
}
