package it.pagopa.pn.cucumber.steps.pg;

import java.util.ArrayList;
import java.util.List;

import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.tos.privacy.BffConsent;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.tos.privacy.ConsentType;
import lombok.Setter;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.*;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.tos.privacy.BffTosPrivacyActionBody;
import it.pagopa.pn.client.b2b.pa.service.IPnLegalPersonVirtualKeyServiceClient;
import it.pagopa.pn.client.b2b.pa.service.IPnTosPrivacyClient;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.web.client.RestClientResponseException;

public class LegalPersonVirtualKeySteps {

    private static final String TOS_VERSION = "2";
    private static final String ADMIN_ROLE = "AMMINISTRATORE";
    private static final String ACCEPT_TOS = "ACCETTA";
    private static final String TOS_ACCEPTED = "POSITIVA";
    private static final String UNKNOWN = "UNKNOWN";
    private static final String VIRTUAL_KEY_UNKNOW = "123345";

    private List<VirtualKeyExpectedResponse> responseNewVirtualKeys;

    private final IPnLegalPersonVirtualKeyServiceClient virtualKeyServiceClient;
    private final IPnTosPrivacyClient tosPrivacyClient;

    public LegalPersonVirtualKeySteps(IPnLegalPersonVirtualKeyServiceClient virtualKeyServiceClient, IPnTosPrivacyClient tosPrivacyClient) {
        this.virtualKeyServiceClient = virtualKeyServiceClient;
        this.tosPrivacyClient = tosPrivacyClient;
        this.responseNewVirtualKeys = new ArrayList<>();
    }

    @Given("l'utente {string} censisce una virtual key per sè stesso")
    public void lUtenteAmministratoreCensisceUnaVirtualKeyPerSeStesso(String user) {
        Assertions.assertDoesNotThrow(() -> executeOperation(user, OperationStatus.REGISTER, null, user));
    }

    @Given("l'utente {string} censisce una virtual key per sè stesso senza successo con l'errore {int}")
    public void lUtenteCensisceUnaVirtualKeyPerSeStessoSenzaSuccesso(String admin, int errorCode) {
        lUtenteFaDelleOperazioniSUuUnaVirtualKeyPerSeStessoSenzaSuccesso(admin, "crea", null, errorCode);
    }

    @Given("l'utente {string} {string} una virtual key in stato {string} per sè stesso")
    public void lUtenteFaDelleOperazioniSUuUnaVirtualKeyPerSeStesso(String user, String operation, String startOperation) {
        lUtenteFaDelleOperazioniSUuUnaVirtualKeyPerLUtente(user, operation, startOperation, user);
    }

    @And("l'utente {string} {string} una virtual key in stato {string} per l'utente {string}")
    public void lUtenteFaDelleOperazioniSUuUnaVirtualKeyPerLUtente(String user, String operation, String startOperation, String userVirtualKeyToChange) {
        Assertions.assertDoesNotThrow(() -> executeOperation(user, OperationStatus.fromValue(operation), VirtualKeyState.valueOf(startOperation), userVirtualKeyToChange));
    }

    @Given("l'utente {string} {string} una virtual key in stato {string} per sè stesso e riceve errore {int}")
    public void lUtenteFaDelleOperazioniSUuUnaVirtualKeyPerSeStessoSenzaSuccesso(String user, String operation, String startOperation, Integer errorCode) {
        lUtenteFaDelleOperazioniSuUnaVirtualKeyPerLUtenteSenzaSuccesso(user, operation, startOperation, user, errorCode);
    }

    @Given("l'utente {string} {string} una virtual key in stato {string} per l'utente {string} e riceve errore {int}")
    public void lUtenteFaDelleOperazioniSuUnaVirtualKeyPerLUtenteSenzaSuccesso(String user, String operation, String startOperation, String userVirtualKeyToChange, Integer errorCode) {
        RestClientResponseException exception = Assertions.assertThrows(RestClientResponseException.class, ()-> executeOperation(user, OperationStatus.valueOf(operation), VirtualKeyState.valueOf(startOperation), userVirtualKeyToChange));
        Assertions.assertEquals(exception.getRawStatusCode(), errorCode);
    }

    @And("controllo che la rotazione è stata effettuata con successo per l'utente {string}")
    public void controlloCheLaVirtualKeySiaInStatoPerLUtente(String user) {
        BffVirtualKeysResponse response = Assertions.assertDoesNotThrow(()-> virtualKeyServiceClient.getVirtualKeys(null, null, null, null));
        Assertions.assertNotNull(response);
        VirtualKeyExpectedResponse expectedVirtualKey = retrieveExpectedVirtualKey(user, VirtualKeyState.ROTATED);
        Assertions.assertNotNull(expectedVirtualKey);
        VirtualKey actualVirtualKey = retrieveVirtualKeyWithStatus(response, expectedVirtualKey);
        Assertions.assertNotNull(actualVirtualKey);
        VirtualKey actualVirtualKeyActive = retrieveNewVirtualKey(user, response);
        Assertions.assertNotNull(actualVirtualKeyActive);
        responseNewVirtualKeys.add(new VirtualKeyExpectedResponse(new BffNewVirtualKeyResponse().id(actualVirtualKeyActive.getId()).virtualKey(response.getLastKey()), VirtualKeyState.ENABLE, user));
    }

    @And("controllo che l'utente {string} veda {string} virtual key nella PG")
    public void controlloCheLUtenteVirtualKeyNellaPG(String user, String condition) {
        List<VirtualKeyExpectedResponse> responses = user.equals(ADMIN_ROLE) ?
                responseNewVirtualKeys : responseNewVirtualKeys.stream()
                .filter(data -> data.user.equals(user))
                .toList();
        retrieveAndCheckVirtualKeyPresent(responses, user);
    }

    @Given("l'utente {string} {string} i tos")
    public void lUtenteAccettaITos(String user, String operation) {
        selectPGUser(user);
        BffTosPrivacyActionBody.ActionEnum actionEnum = operation.equals(ACCEPT_TOS) ? BffTosPrivacyActionBody.ActionEnum.ACCEPT : BffTosPrivacyActionBody.ActionEnum.DECLINE;
        //TO-DO controllare se la chiamata al tos va bene/funge in questo modo.
        BffTosPrivacyActionBody bffTosPrivacyBody = new BffTosPrivacyActionBody().action(actionEnum).version(TOS_VERSION).type(ConsentType.TOS_DEST_B2B);
        Assertions.assertDoesNotThrow(() -> tosPrivacyClient.acceptTosPrivacyV2(List.of(bffTosPrivacyBody)));
    }

    @Given("l'utente {string} controlla l'accettazione dei tos {string}")
    public void lUtenteControllaAccettazioneDeiTos(String user, String tosStatus) {
        selectPGUser(user);
        ConsentType consentType = ConsentType.TOS_DEST_B2B;
        List<BffConsent> privacyConsent = Assertions.assertDoesNotThrow(() -> tosPrivacyClient.getTosPrivacyV2(List.of(consentType)));
        Assertions.assertNotNull(privacyConsent);
        Assertions.assertFalse(privacyConsent.isEmpty());
        //devo capire come recuperare il recipientId dopo la creazione della public key
        privacyConsent.forEach(data -> {
            Assertions.assertNotNull(data.getConsentType());
            Assertions.assertNotNull(data.getConsentType().equals(ConsentType.TOS_DEST_B2B));
            Assertions.assertEquals(data.getAccepted(), tosStatus.equalsIgnoreCase("positiva"));
        });
    }

    @Then("controllo che la chiave sia in stato {string} per l'utente {string}")
    public void controlloCheLaChiaveSiaInStatoPerLUtente(String status, String user) {
        selectPGUser(user);
        BffVirtualKeysResponse response = Assertions.assertDoesNotThrow(()-> virtualKeyServiceClient.getVirtualKeys(null, null, null, null));
        Assertions.assertNotNull(response);
        VirtualKeyExpectedResponse expectedVirtualKey = retrieveExpectedVirtualKey(user, VirtualKeyState.valueOf(status));
        Assertions.assertNotNull(expectedVirtualKey);
        VirtualKey actualVirtualKey = retrieveVirtualKeyWithStatus(response, expectedVirtualKey);
        Assertions.assertNotNull(actualVirtualKey);
    }

    public void executeOperation(String user, OperationStatus operation, VirtualKeyState startOperation, String userToChange) {
        selectPGUser(user);
        switch (operation) {
            case REGISTER -> {
                BffNewVirtualKeyRequest requestNewVirtualKey = new BffNewVirtualKeyRequest();
                BffNewVirtualKeyResponse responseNewVirtualKey = virtualKeyServiceClient.createVirtualKey(requestNewVirtualKey);
                Assertions.assertNotNull(responseNewVirtualKey);
                responseNewVirtualKeys.add(new VirtualKeyExpectedResponse(responseNewVirtualKey, VirtualKeyState.ENABLE, userToChange));
            }
            case ROTATE -> chengeStatusFrom(operation, startOperation, userToChange, VirtualKeyState.ROTATED);
            case BLOCK -> chengeStatusFrom(operation, startOperation, userToChange, VirtualKeyState.BLOCKED);
            case REACTIVATE -> chengeStatusFrom(operation, startOperation, userToChange, VirtualKeyState.REACTIVE);
            case DELETE -> responseNewVirtualKeys.stream()
                .filter(data -> data.user.equals(userToChange))
                .filter(data -> data.state.equals(startOperation))
                .findFirst()
                .ifPresentOrElse(data -> {
                    virtualKeyCancellation(data.response.getId());
                    data.setState(VirtualKeyState.CANCELLED);},
                    () -> {
                        if (startOperation.equals(UNKNOWN)) {
                            virtualKeyCancellation(VIRTUAL_KEY_UNKNOW);
                        } else {
                            throw new AssertionFailedError("NOT FOUND VIRTUAL KEY TO DELETE");}});
        }
    }

    private void chengeStatusFrom(OperationStatus operation, VirtualKeyState startOperation, String userToChange, VirtualKeyState statusToSeet) {
        responseNewVirtualKeys.stream()
            .filter(data -> data.user.equals(userToChange) && data.state.equals(startOperation))
            .findFirst()
            .ifPresentOrElse(response -> {
                    changeVirtualKey(operation, response);
                    response.setState(statusToSeet);
                },
                () -> {
                    throw new AssertionFailedError("No virtual key found for REACTIVATE operation");
                });
    }

    private void changeVirtualKey(OperationStatus status, VirtualKeyExpectedResponse response) {
        BffVirtualKeyStatusRequest requestNewVirtualKey = new BffVirtualKeyStatusRequest();
        requestNewVirtualKey.setStatus(BffVirtualKeyStatusRequest.StatusEnum.fromValue(status.getStatus().getState()));
        virtualKeyServiceClient.changeStatusVirtualKeys(response.response.getId(), requestNewVirtualKey);
    }

    private void virtualKeyCancellation(String virtualKeyId) {
        virtualKeyServiceClient.deleteVirtualKey(virtualKeyId);
    }

    private void retrieveAndCheckVirtualKeyPresent(List<VirtualKeyExpectedResponse> expectedResponses, String user) {
        selectPGUser(user);
        BffVirtualKeysResponse response = Assertions.assertDoesNotThrow(()-> virtualKeyServiceClient.getVirtualKeys(null, null, null, null));
        response.getItems()
                .forEach(virtualKey -> {
                    boolean matches = expectedResponses.stream()
                            .filter(data -> filterVirtualKeyCheck(data, virtualKey))
                            .anyMatch(data -> checkVirtualKeyPresent(data, virtualKey));
                    Assertions.assertTrue(matches);
                });
    }

    private boolean filterVirtualKeyCheck(VirtualKeyExpectedResponse expected, VirtualKey actual) {
        return expected != null && expected.response != null && expected.state != null &&
                actual != null && actual.getStatus() != null && actual.getId() != null;
    }

    private boolean checkVirtualKeyPresent(VirtualKeyExpectedResponse expected, VirtualKey actual) {
        return expected.state.getState().equals(actual.getStatus().getValue()) && expected.response.getId().equals(actual.getId());
    }

    private VirtualKeyExpectedResponse retrieveExpectedVirtualKey(String user, VirtualKeyState status) {
        return responseNewVirtualKeys.stream()
                .filter(data -> data.user.equals(user))
                .filter(data -> data.state.equals(status))
                .findFirst()
                .orElse(null);
    }

    private VirtualKey retrieveNewVirtualKey(String user, BffVirtualKeysResponse response) {
        return response.getItems()
                .stream()
                .filter(data -> data.getUser() != null && data.getUser().getDenomination() != null)
                .filter(data -> data.getUser().getDenomination().equals(user))
                .filter(data -> data.getStatus().getValue().equals(VirtualKeyState.ENABLE.getState()))
                .filter(data -> notAlreadyPresent(user, data))
                .findFirst()
                .orElse(null);
    }

    private boolean notAlreadyPresent(String user, VirtualKey virtualKey) {
        return responseNewVirtualKeys.stream()
                .filter(t -> t.user.equals(user))
                .noneMatch(t -> t.response.getId().equals(virtualKey.getId()) && t.state.getState().equals(VirtualKeyState.ENABLE.getState()));
    }

    private VirtualKey retrieveVirtualKeyWithStatus(BffVirtualKeysResponse response, VirtualKeyExpectedResponse expectedVirtualKey) {
        return response.getItems()
                .stream()
                .filter(data -> data.getUser() != null && data.getUser().getDenomination() != null)
                .filter(data -> data.getUser().getDenomination().equals(expectedVirtualKey.user))
                .filter(data -> filterVirtualKeyCheck(expectedVirtualKey, data))
                .filter(data -> data.getId().equals(expectedVirtualKey.response.getId()))
                .filter(data -> data.getStatus().getValue().equals(expectedVirtualKey.state.getState()))
                .findFirst()
                .orElse(null);
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

    @After("@removeAllVirtualKey")
    public void deleteZip() {
        selectPGUser("amministratore");
        responseNewVirtualKeys.stream()
            .filter(data -> data.state.getState().equals(VirtualKeyState.BLOCKED.getState()))
            .findFirst()
            .ifPresent(data -> virtualKeyCancellation(data.response.getId()));

        responseNewVirtualKeys.forEach(data -> {
            if (data.state.getState().equals(VirtualKeyState.ENABLE.getState())) {
                Assertions.assertDoesNotThrow(() -> changeVirtualKey(OperationStatus.BLOCK, data));
            }
            if (!data.state.getState().equals(VirtualKeyState.CANCELLED.getState())) {
                Assertions.assertDoesNotThrow(() -> virtualKeyCancellation(data.response.getId()));
            }
        });
    }
}
