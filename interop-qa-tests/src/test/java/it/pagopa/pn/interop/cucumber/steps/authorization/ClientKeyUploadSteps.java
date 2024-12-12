package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeyUse;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.utils.KeyPairGeneratorUtil;
import it.pagopa.pn.interop.cucumber.steps.utils.HttpCallExecutor;

public class ClientKeyUploadSteps {
    private final IAuthorizationClient authorizationClient;
    private final ClientCommonSteps clientCommonSteps;
    private final HttpCallExecutor httpCallExecutor;
    private final CommonUtils commonUtils;

    public ClientKeyUploadSteps(IAuthorizationClient authorizationClient,
                                ClientCommonSteps clientCommonSteps,
                                HttpCallExecutor httpCallExecutor,
                                CommonUtils commonUtils) {
        this.authorizationClient = authorizationClient;
        this.clientCommonSteps = clientCommonSteps;
        this.httpCallExecutor = httpCallExecutor;
        this.commonUtils = commonUtils;
    }

    @When("l'utente richiede il caricamento di una chiave pubblica di tipo {string}")
    public void userLoadsPublicKeyWithType(String keyType) {
        commonUtils.setBearerToken(commonUtils.getUserToken());
        httpCallExecutor.performCall(() -> authorizationClient.createKeys("", clientCommonSteps.getClients().get(0),
                KeyPairGeneratorUtil.createKeySeed(KeyUse.SIG, "RS256", KeyPairGeneratorUtil.createBase64PublicKey(keyType, 2048))));
    }

    @When("l'utente richiede il caricamento di una chiave pubblica di tipo {string} di lunghezza {int}")
    public void userLoadsPublicKeyWithTypeAndSize(String keyType, int keyLength) {
        httpCallExecutor.performCall(() -> authorizationClient.createKeys("", clientCommonSteps.getClients().get(0),
                KeyPairGeneratorUtil.createKeySeed(KeyUse.SIG, "RS256", KeyPairGeneratorUtil.createBase64PublicKey(keyType, keyLength))));
    }

    @When("l'utente richiede il caricamento di una chiave pubblica di tipo {string} di lunghezza {int} senza i delimitatori di inizio e fine")
    public void userLoadsPulicKeyWithoutDelimitators (String keyType, int keyLength) {
        httpCallExecutor.performCall(() -> authorizationClient.createKeys("", clientCommonSteps.getClients().get(0),
                KeyPairGeneratorUtil.createKeySeed(KeyUse.SIG, "RS256", KeyPairGeneratorUtil.createBase64PublicKey(keyType, keyLength, false))));
    }

    @When("l'utente richiede il caricamento di una chiave pubblica di tipo {string} di lunghezza {int} con lo stesso kid")
    public void userLoadsPublicKeyWithTypeAndSizeAndSameKid(String keyType, int keyLength) {
        httpCallExecutor.performCall(() -> authorizationClient.createKeys("", clientCommonSteps.getClients().get(0),
                KeyPairGeneratorUtil.createKeySeed(KeyUse.SIG, "RS256", clientCommonSteps.getClientPublicKey())));
    }
}
