package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.domain.KeyPairPEM;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeyUse;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.authorization.service.utils.KeyPairGeneratorUtil;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

public class ClientKeyReadSteps {
    private static final long MAX_SAFE_INTEGER = 9007199254740991L;

    private final IAuthorizationClient authorizationClient;
    private final SharedStepsContext sharedStepsContext;
    private final CommonUtils commonUtils;
    private final HttpCallExecutor httpCallExecutor;
    private final DataPreparationService dataPreparationService;

    public ClientKeyReadSteps(IAuthorizationClient authorizationClient,
                              SharedStepsContext sharedStepsContext,
                              DataPreparationService dataPreparationService) {
        this.authorizationClient = authorizationClient;
        this.sharedStepsContext = sharedStepsContext;
        this.commonUtils = sharedStepsContext.getCommonUtils();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.dataPreparationService = dataPreparationService;
    }

    @Given("un {string} di {string} ha caricato una chiave pubblica nel client")
    public void clientPublicKeyUpload(String role, String tenantType) {
        commonUtils.setBearerToken(commonUtils.getToken(tenantType, role));
        KeyPairPEM keyPairPEM = KeyPairGeneratorUtil.createKeyPairPEM("RSA", 2048);
        String key = KeyPairGeneratorUtil.keyToBase64(keyPairPEM.getPublicKey(), true);
        sharedStepsContext.getClientCommonContext().setClientPublicKey(key);
        String keyId = dataPreparationService.addPublicKeyToClient(sharedStepsContext.getClientCommonContext().getFirstClient(), KeyPairGeneratorUtil.createKeySeed(KeyUse.SIG, "RS256", key).get(0));
        sharedStepsContext.getClientCommonContext().setKeyId(keyId);
    }

    @When("l'utente richiede la lettura della chiave pubblica")
    public void userReadPublicKey() {
        commonUtils.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(() ->
                authorizationClient.getClientKeyById(sharedStepsContext.getXCorrelationId(),
                        sharedStepsContext.getClientCommonContext().getFirstClient(),
                        sharedStepsContext.getClientCommonContext().getKeyId()));
    }


}
