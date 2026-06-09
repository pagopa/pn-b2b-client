package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pa.BffApiKeyStatus;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pa.BffApiKeysResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pa.BffRequestApiKeyStatus;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pa.BffRequestNewApiKey;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pa.BffResponseNewApiKey;
import it.pagopa.pn.client.b2b.pa.service.IPnApiKeyManagerClient;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.utils.GroupPosition;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.List;

import static it.pagopa.pn.cucumber.steps.utilitySteps.Costanti.*;


public class ApikeyManagerSteps {
    private final IPnApiKeyManagerClient apiKeyManagerClient;
    private final SharedSteps sharedSteps;
    private BffApiKeysResponse apiKeys;
    private BffRequestNewApiKey requestNewApiKey;
    private BffResponseNewApiKey responseNewApiKey;
    private HttpStatusCodeException httpStatusCodeException;
    private String firstGroupUsed;
    private String responseNewApiKeyTaxId;

    @Autowired
    public ApikeyManagerSteps(IPnApiKeyManagerClient apiKeyManagerClient, SharedSteps sharedSteps) {
        this.sharedSteps = sharedSteps;
        this.apiKeyManagerClient = apiKeyManagerClient;
    }

    @Given("vengono lette le apiKey esistenti")
    public void vengonoLetteLeApiKeyPrecedentementeGenerate() {
        Assertions.assertDoesNotThrow(() ->
                apiKeys = this.apiKeyManagerClient.getApiKeys(null, null, null, true));
    }

    @Then("la lettura è avvenuta correttamente")
    public void letturaAvvenutaCorrettamente() {
        Assertions.assertNotNull(apiKeys);
    }

    @Given("Viene creata una nuova apiKey")
    public void vieneCreataUnaNuovaApiKey() {
        requestNewApiKey = new BffRequestNewApiKey().name("CUCUMBER TEST");
        Assertions.assertDoesNotThrow(() -> responseNewApiKey = this.apiKeyManagerClient.newApiKey(requestNewApiKey));
        Assertions.assertNotNull(responseNewApiKey);
        System.out.println("ApiKey: " + responseNewApiKey);
    }


    @And("l'apiKey creata è presente tra quelle lette")
    public void apiKeyCreataPresenteTraQuelleLette() {
        Assertions.assertNotNull(
                apiKeys.getItems().stream()
                        .filter(elem -> elem.getId().equals(responseNewApiKey.getId())).findAny().orElse(null));
    }

    @When("l'apiKey viene cancellata")
    public void apiKeyGetsDeleted() {
        Assertions.assertDoesNotThrow(() -> apiKeyManagerClient.deleteApiKeys(responseNewApiKey.getId()));
    }

    @Then("l'apiKey non è più presente")
    public void apiKeyIsNotPresentAnymore() {
        Assertions.assertNull(
                apiKeys.getItems().stream()
                        .filter(elem -> elem.getId().equals(responseNewApiKey.getId())).findAny().orElse(null));
    }

    @When("viene modificato lo stato dell'apiKey in {string}")
    public void vieneModificatoLoStatoDellApiKeyIn(String state) {
        BffRequestApiKeyStatus requestApiKeyStatus = getRequestApiKeyStatus(state);
        Assertions.assertDoesNotThrow(() ->
                apiKeyManagerClient.changeStatusApiKey(responseNewApiKey.getId(), requestApiKeyStatus));
    }

    @Then("l'operazione ha sollevato un errore con status code {string}")
    public void lOperazioneHaSollevatoUnErroreConStatusCode(String statusCode) {
        Assertions.assertTrue((httpStatusCodeException != null) &&
                (httpStatusCodeException.getStatusCode().toString().substring(0, 3).equals(statusCode)));
    }

    @And("si tenta la cancellazione dell'apiKey")
    public void siTentaLaCancellazioneDellApiKey() {
        try {
            apiKeyManagerClient.deleteApiKeys(responseNewApiKey.getId());
        } catch (HttpStatusCodeException codeException) {
            this.httpStatusCodeException = codeException;
        }
    }

    @Then("si verifica lo stato dell'apiKey {string}")
    public void siVerificaLoStatoDellApikey(String state) {
        BffApiKeyStatus apiKeyStatus = switch (state) {
            case "BLOCKED" -> BffApiKeyStatus.BLOCKED;
            case "ENABLED" -> BffApiKeyStatus.ENABLED;
            case "ROTATED" -> BffApiKeyStatus.ROTATED;
            case "CREATED" -> BffApiKeyStatus.CREATED;
            default -> throw new IllegalArgumentException("Invalid status for ApiKey:" + state);
        };
        Assertions.assertNotNull(
                apiKeys.getItems().stream().filter(elem -> (
                        elem.getId().equals(responseNewApiKey.getId()))
                        && (elem.getStatus().equals(apiKeyStatus))).findAny().orElse(null));
    }

    private BffRequestApiKeyStatus getRequestApiKeyStatus(String state) {
        BffRequestApiKeyStatus requestApiKeyStatus = new BffRequestApiKeyStatus();
        switch (state) {
            case "BLOCK" -> requestApiKeyStatus.setStatus(BffRequestApiKeyStatus.StatusEnum.BLOCK);
            case "ENABLE" -> requestApiKeyStatus.setStatus(BffRequestApiKeyStatus.StatusEnum.ENABLE);
            case "ROTATE" -> requestApiKeyStatus.setStatus(BffRequestApiKeyStatus.StatusEnum.ROTATE);
            default -> throw new IllegalArgumentException("Invalid status for ApiKey: " + state);
        }
        return requestApiKeyStatus;
    }

    @When("viene impostata l'apikey appena generata")
    public void vieneImpostataLApikeyAppenaGenerataPerIl() {
        sharedSteps.getB2bClient().setApiKey(responseNewApiKey.getApiKey());
        sharedSteps.setRequestNewApiKey(requestNewApiKey);
        sharedSteps.setResponseNewApiKey(responseNewApiKey);
    }

    @Then("l'invio della notifica non ha prodotto errori")
    public void lInvioDellaNotificaNonHaProdottoErrori() {
        HttpStatusCodeException codeException = sharedSteps.consumeNotificationError();
        Assertions.assertNull(codeException);
    }

    @Then("l'invio della notifica ha sollevato un errore di autenticazione {string}")
    public void lInvioDellaNotificaHaSollevatoUnErroreDiAutenticazione(String statusCode) {
        HttpStatusCodeException codeException = this.sharedSteps.consumeNotificationError();
        Assertions.assertTrue((codeException != null) &&
                (codeException.getStatusCode().toString().substring(0, 3).equals(statusCode)));
    }

    @Then("(l'invio)(il recupero) della notifica ha sollevato un errore {string}")
    public void lInvioDellaNotificaHaSollevatoUnErrore(String statusCode) {
        HttpStatusCodeException codeException = this.sharedSteps.consumeNotificationError();
        Assertions.assertTrue((codeException != null) &&
                (codeException.getStatusCode().toString().substring(0, 3).equals(statusCode)));
    }

    @Given("Viene generata una nuova apiKey con il gruppo {string}")
    public void vieneGenerataUnaNuovaApiKeyConIlGruppo(String group) {
        requestNewApiKey = new BffRequestNewApiKey().name("CUCUMBER GROUP TEST");
        requestNewApiKey.setGroups(List.of(group));
        try {
            this.apiKeyManagerClient.newApiKey(requestNewApiKey);
            sharedSteps.setRequestNewApiKey(requestNewApiKey);
        } catch (HttpStatusCodeException codeException) {
            this.httpStatusCodeException = codeException;
        }
    }

    @Given("Viene creata una nuova apiKey per il comune {string} con il primo gruppo disponibile")
    public void viene_creata_una_nuova_api_key_per_il_comune_con_il_primo_gruppo_disponibile(String paName) {
        setBearerToken(paName);
        requestNewApiKey = new BffRequestNewApiKey().name("CUCUMBER GROUP TEST");

        responseNewApiKeyTaxId = sharedSteps.getDestinatarioRegistry().getSenderTaxIdFromProperties(paName);
        firstGroupUsed = this.sharedSteps.getGroupIdByPa(paName, GroupPosition.FIRST);
        requestNewApiKey.setGroups(List.of(firstGroupUsed));
        Assertions.assertDoesNotThrow(() -> responseNewApiKey = this.apiKeyManagerClient.newApiKey(requestNewApiKey));
        Assertions.assertNotNull(responseNewApiKey);
        sharedSteps.setRequestNewApiKey(requestNewApiKey);
        sharedSteps.setResponseNewApiKey(responseNewApiKey);
        System.out.println("New ApiKey: " + responseNewApiKey);
    }

    @Given("Viene creata una nuova apiKey per il comune {string} con due gruppi")
    public void viene_creata_una_nuova_api_key_per_il_comune_con_due_gruppi(String paName) {
        setBearerToken(paName);
        requestNewApiKey = new BffRequestNewApiKey().name("CUCUMBER GROUP TEST");

        responseNewApiKeyTaxId = sharedSteps.getDestinatarioRegistry().getSenderTaxIdFromProperties(paName);
        firstGroupUsed = this.sharedSteps.getGroupIdByPa(paName, GroupPosition.FIRST);
        String lastGroupUsed = this.sharedSteps.getGroupIdByPa(paName, GroupPosition.LAST);

        requestNewApiKey.setGroups(List.of(firstGroupUsed, lastGroupUsed));
        Assertions.assertDoesNotThrow(() -> responseNewApiKey = this.apiKeyManagerClient.newApiKey(requestNewApiKey));
        Assertions.assertNotNull(responseNewApiKey);
        sharedSteps.setRequestNewApiKey(requestNewApiKey);
        sharedSteps.setResponseNewApiKey(responseNewApiKey);
        System.out.println("New ApiKey: " + responseNewApiKey);
    }

    @Given("Viene creata una nuova apiKey per il comune {string} senza gruppo")
    public void viene_creata_una_nuova_api_key_per_il_comune_senza_gruppo(String paName) {
        setBearerToken(paName);

        requestNewApiKey = new BffRequestNewApiKey().name("CUCUMBER GROUP TEST");
        responseNewApiKeyTaxId = sharedSteps.getDestinatarioRegistry().getSenderTaxIdFromProperties(paName);
        Assertions.assertDoesNotThrow(() -> responseNewApiKey = this.apiKeyManagerClient.newApiKey(requestNewApiKey));
        Assertions.assertNotNull(responseNewApiKey);
        sharedSteps.setRequestNewApiKey(requestNewApiKey);
        sharedSteps.setResponseNewApiKey(responseNewApiKey);
        System.out.println("New ApiKey: " + responseNewApiKey);
    }

    private void setBearerToken(String paName) {
        switch (paName) {
            case COMUNE_1 -> apiKeyManagerClient.setApiKeys(SettableApiKey.ApiKeyType.MVP_1);
            case COMUNE_2 -> apiKeyManagerClient.setApiKeys(SettableApiKey.ApiKeyType.MVP_2);
            case COMUNE_MULTI -> apiKeyManagerClient.setApiKeys(SettableApiKey.ApiKeyType.GA);
            case COMUNE_SON -> apiKeyManagerClient.setApiKeys(SettableApiKey.ApiKeyType.SON);
            case COMUNE_ROOT -> apiKeyManagerClient.setApiKeys(SettableApiKey.ApiKeyType.ROOT);
            default -> throw new IllegalArgumentException("Invalid paName: " + paName);
        }
    }

    @Given("viene settato il gruppo della notifica con quello dell'apikey")
    public void vieneSettatoIlGruppoDellaNotificaConQuelloDellApikey() {
        this.sharedSteps.setGroup(requestNewApiKey.getGroups().get(0));
    }

    @Given("viene settato il taxId della notifica con quello dell'apikey")
    public void vieneSettatoIlTaxIdDellaNotificaConQuelloDellApikey() {
        this.sharedSteps.setSenderTaxId(this.responseNewApiKeyTaxId);
    }

    @When("viene modificato lo stato dell'apiKey in {string} per il {string}")
    public void vieneModificatoLoStatoDellApiKeyIn(String state, String paName) {
        setBearerToken(paName);
        BffRequestApiKeyStatus requestApiKeyStatus = getRequestApiKeyStatus(state);
        Assertions.assertDoesNotThrow(() ->
                apiKeyManagerClient.changeStatusApiKey(responseNewApiKey.getId(), requestApiKeyStatus));
    }

    @Given("viene settato per la notifica corrente il primo gruppo valido del comune {string}")
    public void vieneSettatoIlPrimoGruppoValidoPerIlComune(String paName) {
        setBearerToken(paName);
        firstGroupUsed = this.sharedSteps.getGroupIdByPa(paName, GroupPosition.FIRST);
        this.sharedSteps.setGroup(firstGroupUsed);
    }

    @Given("viene settato un gruppo differente da quello utilizzato nell'apikey per il comune {string}")
    public void vieneSettatoUnGruppoDifferenteDaQuelloUtilizzatoNellApikey(String paName) {
        setBearerToken(paName);
        String group = this.sharedSteps.getGroupIdByPa(paName, GroupPosition.LAST);
        Assertions.assertNotNull(firstGroupUsed);
        Assertions.assertNotEquals(firstGroupUsed, group);
        this.sharedSteps.setGroup(group);
    }

    @Given("Viene creata una nuova apiKey per il comune {string} con gruppo differente (del invio notifica)(dallo stream)")
    public void viene_creata_una_nuova_api_key_per_il_comune_con_gruppo_differente_del_invio_notifica(String paName) {
        setBearerToken(paName);
        String group = this.sharedSteps.getGroupIdByPa(paName, GroupPosition.LAST);
        Assertions.assertNotNull(firstGroupUsed);
        Assertions.assertNotEquals(firstGroupUsed, group);

        requestNewApiKey = new BffRequestNewApiKey().name("CUCUMBER GROUP TEST");
        responseNewApiKeyTaxId = sharedSteps.getDestinatarioRegistry().getSenderTaxIdFromProperties(paName);

        requestNewApiKey.setGroups(List.of(group));
        Assertions.assertDoesNotThrow(() -> responseNewApiKey = this.apiKeyManagerClient.newApiKey(requestNewApiKey));
        Assertions.assertNotNull(responseNewApiKey);
        sharedSteps.setRequestNewApiKey(requestNewApiKey);
        sharedSteps.setResponseNewApiKey(responseNewApiKey);
        System.out.println("New ApiKey: " + responseNewApiKey);
    }


    @Given("Viene creata una nuova apiKey per il comune {string} con gruppo uguale del invio notifica")
    public void viene_creata_una_nuova_api_key_per_il_comune_con_gruppo_uguale_del_invio_notifica(String paName) {
        setBearerToken(paName);
        String group = this.sharedSteps.getGroupIdByPa(paName, GroupPosition.FIRST);
        Assertions.assertNotNull(firstGroupUsed);
        Assertions.assertEquals(firstGroupUsed, group);

        requestNewApiKey = new BffRequestNewApiKey().name("CUCUMBER GROUP TEST");
        responseNewApiKeyTaxId = sharedSteps.getDestinatarioRegistry().getSenderTaxIdFromProperties(paName);

        requestNewApiKey.setGroups(List.of(group));
        Assertions.assertDoesNotThrow(() -> responseNewApiKey = this.apiKeyManagerClient.newApiKey(requestNewApiKey));
        Assertions.assertNotNull(responseNewApiKey);
        System.out.println("New ApiKey: " + responseNewApiKey);
    }

    @Then("si tenta il recupero dal sistema tramite codice IUN")
    public void siTentaIlRecuperoDalSistemaTramiteCodiceIUN() {
        try {
            sharedSteps.getSentNotificationLastVersion();
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @Then("si tenta il recupero dal sistema tramite codice IUN con api v1")
    public void siTentaIlRecuperoDalSistemaTramiteCodiceIUNV1() {
        try {
            sharedSteps.getB2bClient().getSentNotificationV1(sharedSteps.getNotificationIun());
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @And("Si cambia al comune {string}")
    public void lApiKeyNonÈPresenteDalComune(String paName) {
        sharedSteps.setPA(paName);
        switch (paName) {
            case COMUNE_1 -> apiKeyManagerClient.setApiKeys(SettableApiKey.ApiKeyType.MVP_1);
            case COMUNE_2 -> apiKeyManagerClient.setApiKeys(SettableApiKey.ApiKeyType.MVP_2);
            case COMUNE_MULTI -> apiKeyManagerClient.setApiKeys(SettableApiKey.ApiKeyType.GA);
            case COMUNE_SON -> apiKeyManagerClient.setApiKeys(SettableApiKey.ApiKeyType.SON);
            case COMUNE_ROOT -> apiKeyManagerClient.setApiKeys(SettableApiKey.ApiKeyType.ROOT);
            default -> throw new IllegalArgumentException("Invalid paName: " + paName);
        }
    }
}
