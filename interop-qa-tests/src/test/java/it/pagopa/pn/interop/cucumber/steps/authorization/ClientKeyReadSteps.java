package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.domain.KeyPairPEM;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeyUse;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.authorization.service.utils.KeyPairGeneratorUtil;
import it.pagopa.pn.interop.cucumber.steps.utils.HttpCallExecutor;
import org.springframework.http.HttpStatus;

public class ClientKeyReadSteps {
    private static final long MAX_SAFE_INTEGER = 9007199254740991L;

    private final IAuthorizationClient authorizationClient;
    private final ClientCommonSteps clientCommonSteps;
    private final CommonUtils commonUtils;
    private final HttpCallExecutor httpCallExecutor;

    public ClientKeyReadSteps(IAuthorizationClient authorizationClient,
                              ClientCommonSteps clientCommonSteps,
                              CommonUtils commonUtils,
                              HttpCallExecutor httpCallExecutor) {
        this.authorizationClient = authorizationClient;
        this.clientCommonSteps = clientCommonSteps;
        this.commonUtils = commonUtils;
        this.httpCallExecutor = httpCallExecutor;
    }

    @Given("un {string} di {string} ha caricato una chiave pubblica nel client")
    public void clientPublicKeyUpload(String role, String tenantType) {
        KeyPairPEM keyPairPEM = KeyPairGeneratorUtil.createKeyPairPEM("RSA", 2048);
        String key = KeyPairGeneratorUtil.keyToBase64(keyPairPEM.getPublicKey(), true);
        clientCommonSteps.setClientPublicKey(key);

        commonUtils.makePolling(
                () -> (httpCallExecutor.performCall(() -> authorizationClient.createKeys("", clientCommonSteps.getClients().get(0),
                        KeyPairGeneratorUtil.createKeySeed(KeyUse.SIG, "RS256", key)))),
                status -> status != HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to create public key"
        );
    }

    @When("l'utente richiede la lettura della chiave pubblica")
    public void userReadPublicKey() {
        httpCallExecutor.performCall(() ->
                authorizationClient.getClientKeyById("", clientCommonSteps.getClients().get(0), clientCommonSteps.getClientPublicKey()));
    }


}
