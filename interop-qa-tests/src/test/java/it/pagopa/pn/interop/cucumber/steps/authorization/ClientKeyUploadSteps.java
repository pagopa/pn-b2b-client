package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.When;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.utils.KeyPairGeneratorUtil;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

public class ClientKeyUploadSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IAuthorizationClient authorizationClient;
    private final SharedStepsContext sharedStepsContext;
    private final HttpCallExecutor httpCallExecutor;
    private final IdentityService identityService;

    public ClientKeyUploadSteps(ClientTokenConfigurator clientTokenConfigurator,
                                SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.authorizationClient = clientTokenConfigurator.getAuthorizationClient();
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.identityService = sharedStepsContext.getIdentityService();
    }

    @When("l'utente richiede il caricamento di una chiave pubblica di tipo {string}")
    public void userLoadsPublicKeyWithType(String keyType) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(() -> authorizationClient.createKeys(sharedStepsContext.getXCorrelationId(), sharedStepsContext.getClientCommonContext().getFirstClient(),
                KeyPairGeneratorUtil.createKeySeed(
                    KeyPairGeneratorUtil.createBase64PublicKey(keyType, 2048))));
    }

    @When("l'utente richiede il caricamento di una chiave pubblica di tipo {string} di lunghezza {int}")
    public void userLoadsPublicKeyWithTypeAndSize(String keyType, int keyLength) {
        httpCallExecutor.performCall(() -> authorizationClient.createKeys(sharedStepsContext.getXCorrelationId(), sharedStepsContext.getClientCommonContext().getFirstClient(),
                KeyPairGeneratorUtil.createKeySeed(
                    KeyPairGeneratorUtil.createBase64PublicKey(keyType, keyLength))));
    }

    @When("l'utente richiede il caricamento di una chiave pubblica di tipo {string} di lunghezza {int} senza i delimitatori di inizio e fine")
    public void userLoadsPulicKeyWithoutDelimitators (String keyType, int keyLength) {
        httpCallExecutor.performCall(() -> authorizationClient.createKeys(sharedStepsContext.getXCorrelationId(), sharedStepsContext.getClientCommonContext().getFirstClient(),
                KeyPairGeneratorUtil.createKeySeed(
                    KeyPairGeneratorUtil.createBase64PublicKey(keyType, keyLength, false))));
    }

    @When("l'utente richiede il caricamento di una chiave pubblica di tipo {string} di lunghezza {int} con lo stesso kid")
    public void userLoadsPublicKeyWithTypeAndSizeAndSameKid(String keyType, int keyLength) {
        httpCallExecutor.performCall(() -> authorizationClient.createKeys(sharedStepsContext.getXCorrelationId(), sharedStepsContext.getClientCommonContext().getFirstClient(),
                KeyPairGeneratorUtil.createKeySeed(
                    sharedStepsContext.getClientCommonContext().getClientPublicKey())));
    }
}
