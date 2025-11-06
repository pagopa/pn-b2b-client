package it.pagopa.pn.interop.cucumber.steps.authorization;

import com.nimbusds.jose.jwk.KeyType;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.utils.KeyPairGeneratorUtil;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

public class ClientKeyUploadSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IAuthorizationClient authorizationClient;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;

    public ClientKeyUploadSteps(ClientTokenConfigurator clientTokenConfigurator,
                                SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.authorizationClient = clientTokenConfigurator.getAuthorizationClient();
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente richiede il caricamento di una chiave pubblica di tipo {string}")
    public void userLoadsPublicKeyWithType(String keyType) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(() -> authorizationClient.createKeys(sharedStepsContext.getClientCommonContext().getFirstClient(),
                KeyPairGeneratorUtil.createKeySeed(
                    KeyPairGeneratorUtil.createBase64PublicKey(keyType, 2048), KeyType.parse(keyType))));
    }

    @When("l'utente richiede il caricamento di una chiave pubblica di tipo {string} di lunghezza {int}")
    public void userLoadsPublicKeyWithTypeAndSize(String keyType, int keyLength) {
        httpCallExecutor.performCall(() -> authorizationClient.createKeys(sharedStepsContext.getClientCommonContext().getFirstClient(),
                KeyPairGeneratorUtil.createKeySeed(
                    KeyPairGeneratorUtil.createBase64PublicKey(keyType, keyLength), KeyType.parse(keyType))));
    }

    @When("l'utente richiede il caricamento di una chiave pubblica di tipo {string} di lunghezza {int} senza i delimitatori di inizio e fine")
    public void userLoadsPulicKeyWithoutDelimitators (String keyType, int keyLength) {
        httpCallExecutor.performCall(() -> authorizationClient.createKeys(sharedStepsContext.getClientCommonContext().getFirstClient(),
                KeyPairGeneratorUtil.createKeySeed(
                    KeyPairGeneratorUtil.createBase64PublicKey(keyType, keyLength, false), KeyType.parse(keyType))));
    }

    @When("l'utente richiede il caricamento di una chiave pubblica di tipo RSA di lunghezza 2048 con lo stesso kid")
    public void userLoadsPublicKeyWithTypeAndSizeAndSameKid() {
        httpCallExecutor.performCall(() -> authorizationClient.createKeys(sharedStepsContext.getClientCommonContext().getFirstClient(),
                KeyPairGeneratorUtil.createKeySeed(
                    sharedStepsContext.getClientCommonContext().getClientPublicKey(), KeyType.RSA)));
    }
}
