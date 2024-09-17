package it.pagopa.pn.cucumber.steps.pg;

import io.cucumber.java.en.Given;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.BffNewVirtualKeyRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.BffNewVirtualKeyResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.BffVirtualKeyStatusRequest;
import it.pagopa.pn.client.b2b.pa.service.IPnLegalPersonVirtualKeyServiceClient;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import org.junit.jupiter.api.Assertions;

public class LegalPersonVirtualKeySteps {

    private BffNewVirtualKeyResponse responseNewVirtualKey;

    private IPnLegalPersonVirtualKeyServiceClient virtualKeyServiceClient;

    public LegalPersonVirtualKeySteps(IPnLegalPersonVirtualKeyServiceClient virtualKeyServiceClient) {
        this.virtualKeyServiceClient = virtualKeyServiceClient;
    }

    @Given("un utente amministratore {string} censisce una virtual key per se stesso")
    public void unUtenteAmministratoreCensisceUnaVirtualKeyPerSeStesso(String admin) {
        selectPGUser(admin);
        BffNewVirtualKeyRequest requestNewVirtualKey = new BffNewVirtualKeyRequest();
        responseNewVirtualKey = Assertions.assertDoesNotThrow(() -> virtualKeyServiceClient.createVirtualKey(requestNewVirtualKey));
    }

    @Given("un utente {string} {string} la virtual key per se stesso")
    public void unUtenteBloccaVirtualKeyPerSeStesso(String user, String operation) {
        unUtenteAmministratoreBloccaVirtualKeyPerIlPGUtente(user, operation, user);
    }

    @Given("un utente amministratore {string} {string} la virtual key per l'utente {string}")
    public void unUtenteAmministratoreBloccaVirtualKeyPerIlPGUtente(String admin, String operation, String pGUser) {
        selectPGUser(admin);
        executeOperation(admin, operation);
    }

    public void executeOperation(String admin,String operation) {
        selectPGUser(admin);
        switch (operation) {
            case "crea" -> {
                BffNewVirtualKeyRequest requestNewVirtualKey = new BffNewVirtualKeyRequest();
                responseNewVirtualKey = Assertions.assertDoesNotThrow(() -> virtualKeyServiceClient.createVirtualKey(requestNewVirtualKey));
            }
            case "ruota" -> {
                BffVirtualKeyStatusRequest requestNewVirtualKey = new BffVirtualKeyStatusRequest();
                requestNewVirtualKey.setStatus(BffVirtualKeyStatusRequest.StatusEnum.ROTATE);
                Assertions.assertDoesNotThrow(() -> virtualKeyServiceClient.changeStatusVirtualKeys(responseNewVirtualKey.getId(), requestNewVirtualKey));
            }
            case "blocca" -> {
                BffVirtualKeyStatusRequest requestNewVirtualKey = new BffVirtualKeyStatusRequest();
                requestNewVirtualKey.setStatus(BffVirtualKeyStatusRequest.StatusEnum.BLOCK);
                Assertions.assertDoesNotThrow(() -> virtualKeyServiceClient.changeStatusVirtualKeys(responseNewVirtualKey.getId(), requestNewVirtualKey));
            }
            case "elimina" -> {
                Assertions.assertDoesNotThrow(() -> virtualKeyServiceClient.deleteVirtualKey(responseNewVirtualKey.getId()));
            }
        }
    }

    void selectPGUser(String admin) {
        switch (admin.toLowerCase()) {
            case "amministratore" -> {
                virtualKeyServiceClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
            }
        }
    }
}
