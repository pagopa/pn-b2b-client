package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.domain.KeyPairPEM;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.authorization.service.utils.KeyPairGeneratorUtil;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

public class ClientKeyReadSteps {
    private static final long MAX_SAFE_INTEGER = 9007199254740991L;

    private final IAuthorizationClient authorizationClient;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final HttpCallExecutor httpCallExecutor;
    private final DataPreparationService dataPreparationService;

    public ClientKeyReadSteps(IAuthorizationClient authorizationClient,
                              SharedStepsContext sharedStepsContext,
                              DataPreparationService dataPreparationService) {
        this.authorizationClient = authorizationClient;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.dataPreparationService = dataPreparationService;
    }

    @Given("un {string} di {string} ha caricato una chiave pubblica nel client")
    public void clientPublicKeyUpload(String role, String tenantType) {
        identityService.setBearerToken(identityService.getToken(tenantType, role));
        KeyPairPEM keyPairPEM = KeyPairGeneratorUtil.createKeyPairPEM("RSA", 2048);
        String key = KeyPairGeneratorUtil.keyToBase64(keyPairPEM.getPublicKey(), true);
        sharedStepsContext.getClientCommonContext().setClientPublicKey(key);
        String keyId = dataPreparationService.addPublicKeyToClient(sharedStepsContext.getClientCommonContext().getFirstClient(), KeyPairGeneratorUtil.createKeySeed(
            key).get(0));
        sharedStepsContext.getClientCommonContext().setKeyId(keyId);
    }

    @When("l'utente richiede la lettura della chiave pubblica")
    public void userReadPublicKey() {
        identityService.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(() ->
                authorizationClient.getClientKeyById(sharedStepsContext.getXCorrelationId(),
                        sharedStepsContext.getClientCommonContext().getFirstClient(),
                        sharedStepsContext.getClientCommonContext().getKeyId()));
    }


}
