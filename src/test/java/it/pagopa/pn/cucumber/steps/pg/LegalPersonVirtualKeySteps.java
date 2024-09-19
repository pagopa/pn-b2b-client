package it.pagopa.pn.cucumber.steps.pg;

import java.util.ArrayList;
import java.util.List;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import lombok.Setter;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.*;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.tos.privacy.BffTosPrivacyActionBody;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.tos.privacy.BffTosPrivacyBody;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.tos.privacy.BffTosPrivacyConsent;
import it.pagopa.pn.client.b2b.pa.service.IPnLegalPersonVirtualKeyServiceClient;
import it.pagopa.pn.client.b2b.pa.service.IPnTosPrivacyClient;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import org.junit.jupiter.api.Assertions;
import org.springframework.web.client.RestClientResponseException;

public class LegalPersonVirtualKeySteps {

    private List<VirtualKeyExpectedResponse> responseNewVirtualKeys;

    private final IPnLegalPersonVirtualKeyServiceClient virtualKeyServiceClient;
    private final IPnTosPrivacyClient tosPrivacyClient;

    public LegalPersonVirtualKeySteps(IPnLegalPersonVirtualKeyServiceClient virtualKeyServiceClient, IPnTosPrivacyClient tosPrivacyClient) {
        this.virtualKeyServiceClient = virtualKeyServiceClient;
        this.tosPrivacyClient = tosPrivacyClient;
        this.responseNewVirtualKeys = new ArrayList<>();
    }

    @Given("un utente {string} censisce una virtual key per se stesso")
    public void unUtenteAmministratoreCensisceUnaVirtualKeyPerSeStesso(String user) {
        Assertions.assertDoesNotThrow(() -> executeOperation(user, "crea", null, user));
    }

    @Given("un utente {string} censisce una virtual key per se stesso senza successo con l'errore {int}")
    public void unUtenteCensisceUnaVirtualKeyPerSeStessoSenzaSuccesso(String admin, int errorCode) {
        unUtenteFaDelleOperazioniSUuUnaVirtualKeyPerSeStessoSenzaSuccesso(admin, "crea", null, errorCode);
    }

    @Given("un utente {string} {string} una virtual key in stato {string} per se stesso")
    public void unUtenteFaDelleOperazioniSUuUnaVirtualKeyPerSeStesso(String user, String operation, String startOperation) {
        unUtenteFaDelleOperazioniSUuUnaVirtualKeyPerLUtente(user, operation, startOperation, user);
    }

    @And("un utente {string} {string} una virtual key in stato {string} per l'utente {string}")
    public void unUtenteFaDelleOperazioniSUuUnaVirtualKeyPerLUtente(String user, String operation, String startOperation, String userVirtualKeyToChange) {
        Assertions.assertDoesNotThrow(() -> executeOperation(user, operation, startOperation, userVirtualKeyToChange));
    }

    @Given("un utente {string} {string} una virtual key in stato {string} per se stesso e riceve errore {int}")
    public void unUtenteFaDelleOperazioniSUuUnaVirtualKeyPerSeStessoSenzaSuccesso(String user, String operation, String startOperation, Integer errorCode) {
        unUtenteFaDelleOperazioniSuUnaVirtualKeyPerLUtenteSenzaSuccesso(user, operation, startOperation, user, errorCode);
    }

    @Given("un utente {string} {string} una virtual key in stato {string} per l'utente {string} e riceve errore {int}")
    public void unUtenteFaDelleOperazioniSuUnaVirtualKeyPerLUtenteSenzaSuccesso(String user, String operation, String startOperation, String userVirtualKeyToChange, Integer errorCode) {
        RestClientResponseException exception = Assertions.assertThrows(RestClientResponseException.class, ()-> executeOperation(user, operation, startOperation, userVirtualKeyToChange));
        Assertions.assertEquals(exception.getRawStatusCode(), errorCode);
    }

    @And("controllo che l'utente {string} veda {string} virtual key nella PG")
    public void controlloCheLUtenteVirtualKeyNellaPG(String user, String condition) {
        List<VirtualKeyExpectedResponse> responses = user.equals("AMMINISTRATORE") ?
                responseNewVirtualKeys : responseNewVirtualKeys.stream()
                .filter(data -> data.user.equals(user))
                .toList();
        retrieveAndCheckVirtualKeyPresent(responses, user);
    }

    private void retrieveAndCheckVirtualKeyPresent(List<VirtualKeyExpectedResponse> expectedResponses, String user) {
        selectPGUser(user);
        BffVirtualKeysResponse response = Assertions.assertDoesNotThrow(()-> virtualKeyServiceClient.getVirtualKeys(null, null, null, null));
        response.getItems()
            .forEach(virtualKey -> {
                boolean matches = expectedResponses.stream()
                    .anyMatch(data -> checkVirtualKeyPresent(data, virtualKey));
                Assertions.assertTrue(matches);
            });
    }

    private boolean checkVirtualKeyPresent(VirtualKeyExpectedResponse expected, VirtualKey actual) {
        return expected != null && expected.response != null && expected.state != null && expected.response.getId() != null &&
                actual != null && actual.getStatus() != null && actual.getId() != null &&
                expected.state.getState().equals(actual.getStatus().getValue()) && expected.response.getId().equals(actual.getId());
    }

    @Given("un utente {string} {string} i tos")
    public void unUtenteAccettaITos(String admin, String operation) {
        selectPGUser(admin);
        BffTosPrivacyActionBody.ActionEnum actionEnum = operation.equals("ACCETTA") ? BffTosPrivacyActionBody.ActionEnum.ACCEPT : BffTosPrivacyActionBody.ActionEnum.DECLINE;
        //TO-DO controllare come valorizzare la request nel modo corretto (il valore della version se serve)
        BffTosPrivacyBody bffTosPrivacyBody = new BffTosPrivacyBody().privacy(new BffTosPrivacyActionBody().version("1.1.1").action(actionEnum));
        Assertions.assertDoesNotThrow(() -> tosPrivacyClient.acceptTosPrivacyV1(bffTosPrivacyBody));
    }

    @Given("un utente {string} controlla l'accettazione dei tos {string}")
    public void unUtenteControllaAccettazioneDeiTos(String admin, String tosStatus) {
        selectPGUser(admin);
        BffTosPrivacyConsent privacyConsent = Assertions.assertDoesNotThrow(() -> tosPrivacyClient.getTosPrivacyV1());
        boolean checkStatus = tosStatus.equalsIgnoreCase("POSITIVA");
        Assertions.assertEquals(checkStatus, privacyConsent.getPrivacy().getAccepted());
    }

    public void executeOperation(String user, String operation, String startOperation, String userToChange) {
        selectPGUser(user);
        switch (operation) {
            case "crea" -> {
                BffNewVirtualKeyRequest requestNewVirtualKey = new BffNewVirtualKeyRequest();
                BffNewVirtualKeyResponse responseNewVirtualKey = virtualKeyServiceClient.createVirtualKey(requestNewVirtualKey);
                responseNewVirtualKeys.add(new VirtualKeyExpectedResponse(responseNewVirtualKey, VirtualKeyState.ENABLE, userToChange));
            }
            case "ruota", "blocca", "riattiva" -> responseNewVirtualKeys.stream()
                        .filter(data -> data.user.equals(userToChange))
                        .filter(data -> data.state.equals(VirtualKeyState.valueOf(startOperation)))
                        .findFirst()
                        .ifPresent(data -> changeVirtualKey(operation, data));
            case "elimina" -> responseNewVirtualKeys.stream()
                        .filter(data -> data.user.equals(userToChange))
                        .filter(data -> data.state.equals(VirtualKeyState.valueOf(startOperation)))
                        .findFirst()
                        .ifPresent(data -> virtualKeyCancellation(data, startOperation));
        }
    }

    private void changeVirtualKey(String status, VirtualKeyExpectedResponse response) {
        BffVirtualKeyStatusRequest requestNewVirtualKey = new BffVirtualKeyStatusRequest();
        requestNewVirtualKey.setStatus(BffVirtualKeyStatusRequest.StatusEnum.fromValue(status));
        virtualKeyServiceClient.changeStatusVirtualKeys(response.response.getId(), requestNewVirtualKey);
        response.setState(VirtualKeyState.valueOf(status));
    }

    private void virtualKeyCancellation(VirtualKeyExpectedResponse response, String startOperation) {
        virtualKeyServiceClient.deleteVirtualKey(startOperation.equals("UNKNOWN") ? "123345" : response.response.getId());
        response.setState(VirtualKeyState.CANCELLED);
    }

    void selectPGUser(String admin) {
        switch (admin.toLowerCase()) {
            case "amministratore con gruppi" -> {
                virtualKeyServiceClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
                tosPrivacyClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
            }
            case "amministratore" -> {
                virtualKeyServiceClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_2);
                tosPrivacyClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_2);
            }
            case "pg" -> {
                virtualKeyServiceClient.setBearerToken(SettableBearerToken.BearerTokenType.valueOf("pg"));
                tosPrivacyClient.setBearerToken(SettableBearerToken.BearerTokenType.valueOf("pg"));
            }
            default -> throw new IllegalArgumentException("ADMIN NOT VALID");
        }
    }

    private class VirtualKeyExpectedResponse {
        BffNewVirtualKeyResponse response;
        @Setter
        VirtualKeyState state;
        String user;

        private VirtualKeyExpectedResponse(BffNewVirtualKeyResponse response, VirtualKeyState state, String user) {
            this.response = response;
            this.state = state;
            this.user = user;
        }
    }
}
