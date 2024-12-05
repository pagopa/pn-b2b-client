package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.When;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeyUse;
import it.pagopa.interop.service.IAuthorizationClient;
import it.pagopa.interop.service.utils.CommonUtils;
import it.pagopa.interop.service.utils.KeyPairGeneratorUtil;

public class ClientKeyUploadSteps {
    private final IAuthorizationClient authorizationClient;
    private final ClientCommonSteps clientCommonSteps;

    public ClientKeyUploadSteps(IAuthorizationClient authorizationClient, ClientCommonSteps clientCommonSteps) {
        this.authorizationClient = authorizationClient;
        this.clientCommonSteps = clientCommonSteps;
    }

    @When("l'utente richiede il caricamento di una chiave pubblica di tipo {string}")
    public void userLoadsPublicKeyWithType(String keyType) {
        clientCommonSteps.performCall(() -> authorizationClient.createKeys("", clientCommonSteps.getClients().get(0),
                KeyPairGeneratorUtil.createKeySeed(KeyUse.SIG, "RS256", KeyPairGeneratorUtil.createBase64PublicKey(keyType, 2048))));
    }

    @When("l'utente richiede il caricamento di una chiave pubblica di tipo {string} di lunghezza {int}")
    public void userLoadsPublicKeyWithTypeAndSize(String keyType, int keyLength) {
        clientCommonSteps.performCall(() -> authorizationClient.createKeys("", clientCommonSteps.getClients().get(0),
                KeyPairGeneratorUtil.createKeySeed(KeyUse.SIG, "RS256", KeyPairGeneratorUtil.createBase64PublicKey(keyType, keyLength))));
    }

    @When("l'utente richiede il caricamento di una chiave pubblica di tipo {string} di lunghezza {int} con lo stesso kid")
    public void userLoadsPublicKeyWithTypeAndSizeAndSameKid(String keyType, int keyLength) {
        clientCommonSteps.performCall(() -> authorizationClient.createKeys("", clientCommonSteps.getClients().get(0),
                KeyPairGeneratorUtil.createKeySeed(KeyUse.SIG, "RS256", clientCommonSteps.getClientPublicKey())));
    }
}
