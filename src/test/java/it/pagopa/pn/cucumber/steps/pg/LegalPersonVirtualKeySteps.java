package it.pagopa.pn.cucumber.steps.pg;

import io.cucumber.java.en.Given;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.*;
import it.pagopa.pn.client.b2b.pa.service.IPnLegalPersonVirtualKeyServiceClient;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import org.junit.jupiter.api.Assertions;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

public class LegalPersonVirtualKeySteps {

    private BffNewVirtualKeyResponse responseNewVirtualKey;
    private List<VirtualKeyExpectedResponse> responseNewVirtualKeys;

    private IPnLegalPersonVirtualKeyServiceClient virtualKeyServiceClient;

    public LegalPersonVirtualKeySteps(IPnLegalPersonVirtualKeyServiceClient virtualKeyServiceClient) {
        this.virtualKeyServiceClient = virtualKeyServiceClient;
    }

    @Given("un utente {string} censisce una virtual key per se stesso")
    public void unUtenteAmministratoreCensisceUnaVirtualKeyPerSeStesso(String admin) {
        selectPGUser(admin);
        Assertions.assertDoesNotThrow(() -> executeOperation(admin, "crea", null));
    }

    @Given("un utente {string} censisce una virtual key per se stesso con una attiva gia presente")
    public void unUtenteAmministratoreCensisceUnaVirtualKeyPerSeStessoConUnaAttivaGiaPresente(String admin) {
        //cambiare eccezione con messaggio specifico
//        RestClientResponseException exception = Assertions.assertThrows(RestClientResponseException.class, ()-> executeOperation(admin, "crea", null));
//        Assertions.assertEquals(exception.getRawStatusCode(), "409");
        unUtenteAmministratoreCensisceUnaVirtualKeyPerSeStessoSenzaSuccesso(admin, "crea", null, 409);
    }

    @Given("un utente amministratore {string} {string} una virtual key in stato {string} per se stesso")
    public void unUtenteAmministratoreCensisceUnaVirtualKeyPerSeStesso(String admin, String operation, String startOperation) {
        selectPGUser(admin);
        Assertions.assertDoesNotThrow(() -> executeOperation(admin, operation, startOperation));
    }

    @Given("un utente amministratore {string} {string} una virtual key in stato {string} e riceve errore {int}")
    public void unUtenteAmministratoreCensisceUnaVirtualKeyPerSeStessoSenzaSuccesso(String admin, String operation, String startOperation, Integer errorCode) {
        selectPGUser(admin);
        RestClientResponseException exception = Assertions.assertThrows(RestClientResponseException.class, ()-> executeOperation(admin, operation, startOperation));
        Assertions.assertEquals(exception.getRawStatusCode(), errorCode);
    }

    @Given("un utente {string} controlla che la sua virtual key sia in stato {string}")
    public void unUtenteControllaCheLaSuaVirtualKeySiaInStato(String admin, String status) {
        selectPGUser(admin);
        VirtualKeyExpectedResponse virtualKeyExpectedResponse = responseNewVirtualKeys.get(responseNewVirtualKeys.size() - 1);
        BffVirtualKeysResponse response = Assertions.assertDoesNotThrow(()-> virtualKeyServiceClient.getVirtualKeys(10, virtualKeyExpectedResponse.response.getVirtualKey(), null, true));
        Assertions.assertFalse(responseNewVirtualKeys.isEmpty());
        VirtualKey virtualKeyFounded = response.getItems().stream()
                .filter(data -> data.getId().equals(virtualKeyExpectedResponse.response.getVirtualKey()))
                .findFirst().orElse(null);
        Assertions.assertNotNull(virtualKeyFounded);
        Assertions.assertEquals(virtualKeyExpectedResponse.response.getId(), virtualKeyFounded.getId());
        Assertions.assertEquals(status, virtualKeyExpectedResponse.state.getState());
        Assertions.assertEquals(status, virtualKeyFounded.getStatus().getValue());
    }

    public void executeOperation(String admin, String operation, String startOperation) {
        selectPGUser(admin);
        switch (operation) {
            case "crea" -> {
                BffNewVirtualKeyRequest requestNewVirtualKey = new BffNewVirtualKeyRequest();
                responseNewVirtualKey = virtualKeyServiceClient.createVirtualKey(requestNewVirtualKey);
                responseNewVirtualKeys.add(new VirtualKeyExpectedResponse(responseNewVirtualKey, VirtualKeyState.ACTIVE));
            }
            case "ruota", "blocca" -> {
                responseNewVirtualKeys.stream()
                        .filter(data -> data.state.equals(VirtualKeyState.valueOf(startOperation)))
                        .findFirst()
                        .ifPresent(data -> changeVirtualKey(operation, data));
            }
            case "elimina" -> {
                responseNewVirtualKeys.stream()
                        .filter(data -> data.state.equals(VirtualKeyState.valueOf(startOperation)))
                        .findFirst()
                        .ifPresent(this::virtualKeyCancellation);
            }
        }
    }

    private void changeVirtualKey(String status, VirtualKeyExpectedResponse response) {
        BffVirtualKeyStatusRequest requestNewVirtualKey = new BffVirtualKeyStatusRequest();
        requestNewVirtualKey.setStatus(BffVirtualKeyStatusRequest.StatusEnum.fromValue(status));
        Assertions.assertDoesNotThrow(() -> virtualKeyServiceClient.changeStatusVirtualKeys(response.response.getId(), requestNewVirtualKey));
        response.setState(VirtualKeyState.valueOf(status));
    }

    private void virtualKeyCancellation(VirtualKeyExpectedResponse response) {
        Assertions.assertDoesNotThrow(() -> virtualKeyServiceClient.deleteVirtualKey(response.response.getId()));
        response.setState(VirtualKeyState.CANCELLED);
    }

    void selectPGUser(String admin) {
        switch (admin.toLowerCase()) {
            case "amministratore con gruppi" -> {
                virtualKeyServiceClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
            }
            case "amministratore senza gruppi" -> {
                virtualKeyServiceClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_2);
            }
            case "pg" -> {
                virtualKeyServiceClient.setBearerToken(SettableBearerToken.BearerTokenType.valueOf("pg"));
            }
            default -> throw new IllegalArgumentException("ADMIN NOT VALID");
        }
    }

    private class VirtualKeyExpectedResponse {
        BffNewVirtualKeyResponse response;
        VirtualKeyState state;

        private VirtualKeyExpectedResponse(BffNewVirtualKeyResponse response, VirtualKeyState state) {
            this.response = response;
            this.state = state;
        }

        public void setResponse(BffNewVirtualKeyResponse response) {
            this.response = response;
        }

        public void setState(VirtualKeyState state) {
            this.state = state;
        }
    }
}
