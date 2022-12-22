package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.testclient.IPnApiKeyManagerClient;
import it.pagopa.pn.client.web.generated.openapi.clients.externalApiKeyManager.model.*;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpStatusCodeException;

public class ApikeyManagerSteps {

    private final IPnApiKeyManagerClient apiKeyManagerClient;
    private final SharedSteps sharedSteps;

    private ApiKeysResponse apiKeys;
    private RequestNewApiKey requestNewApiKey;
    private ResponseNewApiKey responseNewApiKey;
    private HttpStatusCodeException httpStatusCodeException;

    @Autowired
    public ApikeyManagerSteps(IPnApiKeyManagerClient apiKeyManagerClient, SharedSteps sharedSteps) {
        this.sharedSteps = sharedSteps;
        this.apiKeyManagerClient = apiKeyManagerClient;
    }


    @Given("vengono lette le apiKey esistenti")
    public void vengonoLetteLeApiKeyPrecedentementeGenerate() {
        Assertions.assertDoesNotThrow(()->
                apiKeys = this.apiKeyManagerClient.getApiKeys(null, null, null, true));
    }

    @Then("la lettura è avvenuta correttamente")
    public void laLetturaÈAvvenutaCorrettamente() {
        Assertions.assertNotNull(apiKeys);
    }

    @Given("Viene creata una nuova apiKey")
    public void vieneCreataUnaNuovaApiKey() {
        requestNewApiKey = new RequestNewApiKey().name("CUCUMBER TEST");
        Assertions.assertDoesNotThrow(()-> responseNewApiKey = this.apiKeyManagerClient.newApiKey(requestNewApiKey));
        Assertions.assertNotNull(responseNewApiKey);
        System.out.println("ApiKey: "+responseNewApiKey);
    }


    @And("l'apiKey creata è presente tra quelle lette")
    public void lApiKeyCreataÈPresenteTraQuelleLette() {
        Assertions.assertNotNull(
                apiKeys.getItems().stream()
                        .filter(elem -> elem.getId().equals(responseNewApiKey.getId())).findAny().orElse(null));
    }

    @When("l'apiKey viene cancellata")
    public void lApiKeyVieneCancellata() {
        Assertions.assertDoesNotThrow(()->apiKeyManagerClient.deleteApiKeys(responseNewApiKey.getId()));
    }

    @Then("l'apiKey non è più presente")
    public void lApiKeyNonÈPiùPresente() {
        Assertions.assertNull(
                apiKeys.getItems().stream()
                        .filter(elem -> elem.getId().equals(responseNewApiKey.getId())).findAny().orElse(null));
    }

    @When("viene modificato lo stato dell'apiKey in {string}")
    public void vieneModificatoLoStatoDellApiKeyIn(String state) {
        RequestApiKeyStatus requestApiKeyStatus = getRequestApiKeyStatus(state);
        Assertions.assertDoesNotThrow(()->
                apiKeyManagerClient.changeStatusApiKey(responseNewApiKey.getId(),requestApiKeyStatus));
    }

    @Then("l'operazione ha sollevato un errore con status code {string}")
    public void lOperazioneHaSollevatoUnErroreConStatusCode(String statusCode) {
        Assertions.assertTrue((httpStatusCodeException != null) &&
                (httpStatusCodeException.getStatusCode().toString().substring(0,3).equals(statusCode)));
    }

    @And("si tenta la cancellazione dell'apiKey")
    public void siTentaLaCancellazioneDellApiKey() {
        try{
            apiKeyManagerClient.deleteApiKeys(responseNewApiKey.getId());
        }catch (HttpStatusCodeException httpStatusCodeException){
            this.httpStatusCodeException = httpStatusCodeException;
        }
    }

    @Then("si verifica lo stato dell'apikey {string}")
    public void siVerificaLoStatoDellApikey(String state) {
        ApiKeyStatus apiKeyStatus;
        switch (state){
            case "BLOCKED":
                apiKeyStatus = ApiKeyStatus.BLOCKED;
                break;
            case "ENABLED":
                apiKeyStatus = ApiKeyStatus.ENABLED;
                break;
            case "ROTATED":
                apiKeyStatus = ApiKeyStatus.ROTATED;
                break;
            case "CREATED":
                apiKeyStatus = ApiKeyStatus.CREATED;
                break;
            default:
                throw new IllegalArgumentException();
        }
        Assertions.assertNotNull(
                apiKeys.getItems().stream()
                        .filter(elem -> (elem.getId().equals(responseNewApiKey.getId()))
                                &&(elem.getStatus().equals(apiKeyStatus))).findAny().orElse(null));
    }



    private RequestApiKeyStatus getRequestApiKeyStatus(String state){
        RequestApiKeyStatus requestApiKeyStatus = new RequestApiKeyStatus();
        switch (state){
            case "BLOCK":
                requestApiKeyStatus.setStatus(RequestApiKeyStatus.StatusEnum.BLOCK);
                break;
            case "ENABLE":
                requestApiKeyStatus.setStatus(RequestApiKeyStatus.StatusEnum.ENABLE);
                break;
            case "ROTATE":
                requestApiKeyStatus.setStatus(RequestApiKeyStatus.StatusEnum.ROTATE);
                break;
            default:
                throw new IllegalArgumentException();
        }
        return requestApiKeyStatus;
    }

    @When("viene impostata l'apikey appena generata")
    public void vieneImpostataLApikeyAppenaGenerataPerIl() {
        sharedSteps.getB2bClient().setApiKey(responseNewApiKey.getApiKey());
        sharedSteps.getB2bUtils().setClient(sharedSteps.getB2bClient());
    }

    @Then("l'invio della notifica non ha prodotto errori")
    public void lInvioDellaNotificaNonHaProdottoErrori() {
        HttpStatusCodeException httpStatusCodeException = sharedSteps.consumeNotificationError();
        Assertions.assertNull(httpStatusCodeException);
    }

    @Then("l'invio della notifica ha sollevato un errore di autenticazione {string}")
    public void lInvioDellaNotificaHaSollevatoUnErroreDiAutenticazione(String statusCode) {
        HttpStatusCodeException httpStatusCodeException = this.sharedSteps.consumeNotificationError();
        Assertions.assertTrue((httpStatusCodeException != null) &&
                (httpStatusCodeException.getStatusCode().toString().substring(0,3).equals(statusCode)));
    }
}
