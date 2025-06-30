package it.pagopa.pn.interop.cucumber.steps.authorization;

import static it.pagopa.interop.authorization.domain.KeyPairDecorator.of;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.domain.KeyPairDecorator;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.KeyPairGeneratorUtil;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

public class ClientKeyReadSteps {
    private static final long MAX_SAFE_INTEGER = 9007199254740991L;

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IAuthorizationClient authorizationClient;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final IHttpExecutor httpCallExecutor;
    private final BFFDataPreparationService dataPreparationService;

    public ClientKeyReadSteps(ClientTokenConfigurator clientTokenConfigurator,
                              SharedStepsContext sharedStepsContext,
                              BFFDataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.authorizationClient = clientTokenConfigurator.getAuthorizationClient();
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.dataPreparationService = dataPreparationService;
    }

    @Given("un {string} di {string} ha caricato una chiave pubblica nel client")
    public void clientPublicKeyUpload(String role, String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, role));
        String keyType = "RSA";
        KeyPairDecorator keyPair = of(keyType, 2048);
        String encodedPublicKey = keyPair.getDelimitedPublicKeyBase64();

        // TODO: memorizzare in contesto solo KeyPairDecorator e keyType
        sharedStepsContext.getClientCommonContext().setClientPublicKey(encodedPublicKey);
        sharedStepsContext.getClientCommonContext().setClientPublicKeyAsObj(keyPair.getPublic());
        sharedStepsContext.getClientCommonContext().setClientPrivateKey(keyPair.getPrivatePEM());
        sharedStepsContext.getClientCommonContext().setClientPrivateKeyAsObj(keyPair.getPrivate());
        sharedStepsContext.getClientCommonContext().setKeyType(keyType);
        String keyId = dataPreparationService.addPublicKeyToClient(
            sharedStepsContext.getClientCommonContext().getFirstClient(),
            KeyPairGeneratorUtil.createKeySeed(encodedPublicKey).get(0));
        sharedStepsContext.getClientCommonContext().setKeyId(keyId);
    }

    @When("l'utente richiede la lettura della chiave pubblica")
    public void userReadPublicKey() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(() ->
                authorizationClient.getClientKeyById(
                        sharedStepsContext.getClientCommonContext().getFirstClient(),
                        sharedStepsContext.getClientCommonContext().getKeyId()));
    }

}
