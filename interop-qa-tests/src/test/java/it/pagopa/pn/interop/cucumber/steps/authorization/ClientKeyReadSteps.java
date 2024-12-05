package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.domain.KeyPairPEM;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeySeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeyUse;
import it.pagopa.interop.service.IAuthorizationClient;
import it.pagopa.interop.service.utils.CommonUtils;
import it.pagopa.interop.service.utils.KeyPairGeneratorUtil;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Random;

public class ClientKeyReadSteps {
    private static final long MAX_SAFE_INTEGER = 9007199254740991L;

    private final IAuthorizationClient authorizationClient;
    private final ClientCommonSteps clientCommonSteps;
    private final CommonUtils commonUtils;

    public ClientKeyReadSteps(IAuthorizationClient authorizationClient,
                              ClientCommonSteps clientCommonSteps,
                              CommonUtils commonUtils) {
        this.authorizationClient = authorizationClient;
        this.clientCommonSteps = clientCommonSteps;
        this.commonUtils = commonUtils;
    }

    @Given("un {string} di {string} ha caricato una chiave pubblica nel client")
    public void clientPublicKeyUpload(String role, String tenantType) {
        KeyPairPEM keyPairPEM = KeyPairGeneratorUtil.createKeyPairPEM("RSA", 2048);
        String key = KeyPairGeneratorUtil.keyToBase64(keyPairPEM.getPublicKey(), true);

        commonUtils.makePolling(
                () -> (clientCommonSteps.performCall(() -> authorizationClient.createKeys("", clientCommonSteps.getClients().get(0),
                        KeyPairGeneratorUtil.createKeySeed(KeyUse.SIG, "RS256", key)))),
                status -> status != HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to create public key"
        );
    }

    @When("l'utente richiede la lettura della chiave pubblica")
    public void userReadPublicKey() {
        clientCommonSteps.performCall(() ->
                authorizationClient.getClientKeyById("", clientCommonSteps.getClients().get(0), clientCommonSteps.getClientPublicKey()));
    }


}
