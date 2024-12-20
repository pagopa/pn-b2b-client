package it.pagopa.pn.cucumber.steps.pg;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.BffPublicKeyRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.BffPublicKeyResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.BffPublicKeysResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.PublicKeyRow;
import it.pagopa.pn.client.b2b.generated.openapi.clients.generate.model.externalregistry.privateapi.PgUser;
import it.pagopa.pn.client.b2b.pa.service.IPnExternalRegistryPrivateUserApi;
import it.pagopa.pn.client.b2b.pa.service.IPnLegalPersonAuthClient;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import it.pagopa.pn.cucumber.utils.LegalPersonAuthExpectedResponseWithStatus;
import it.pagopa.pn.cucumber.utils.LegalPersonsAuthStepsPojo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LegalPersonAuthSteps {
    private final LegalPersonsAuthStepsPojo pojo;
    private final IPnLegalPersonAuthClient pnLegalPersonAuthClient;
    private final IPnExternalRegistryPrivateUserApi privateUserApi;

    @Value("${pn.authentication.pg.public.key}")
    private String publicKey;

    @Value("${pn.authentication.pg.public.key.rotation}")
    private String publicKeyRotation;

    @Value("${pn.authentication.pg.admin.uid}")
    private String adminUid;

    @Value("${pn.authentication.pg.admin.group.uid}")
    private String adminGroupUid;

    @Value("${pn.authentication.pg.operator.uid}")
    private String operatorUid;

    @Value("${pn.authentication.pg.organization.id}")
    private String organizationId;

    private final String UNKNOWN_KID = "4004309b-1bf6-789a-9582-721fe23653b6";

    public LegalPersonAuthSteps(IPnLegalPersonAuthClient pnLegalPersonAuthClient, IPnExternalRegistryPrivateUserApi privateUserApi) {
        this.pnLegalPersonAuthClient = pnLegalPersonAuthClient;
        this.privateUserApi = privateUserApi;
        this.pojo = new LegalPersonsAuthStepsPojo();
    }

    public void selectAdmin(String utente) {
        switch (utente.toUpperCase()) {
            case "AMMINISTRATORE" -> pnLegalPersonAuthClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_3);
            case "AMMINISTRATORE CON GRUPPO ASSOCIATO" -> pnLegalPersonAuthClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_4);
            case "NON AMMINISTRATORE" -> pnLegalPersonAuthClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_5);
            case "DI UNA PG DIVERSA" -> pnLegalPersonAuthClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_2);
        }
    }

    public void selectAdminForGetUser(String utente) {
        switch (utente.toUpperCase()) {
            case "AMMINISTRATORE" -> privateUserApi.setBearerToken(SettableBearerToken.BearerTokenType.PG_3);
            case "AMMINISTRATORE CON GRUPPO ASSOCIATO" -> privateUserApi.setBearerToken(SettableBearerToken.BearerTokenType.PG_4);
            case "NON AMMINISTRATORE" -> privateUserApi.setBearerToken(SettableBearerToken.BearerTokenType.PG_5);
        }
    }

    @When("l'utente {string} crea una chiave pubblica per la PG usando una chiave già presente in stato ruotato")
    public void lUtenteCreaUnaChiavePubblicaPerLaPGUsandoUnaChiaveGiàPresenteInStatoRuotato(String utente) {
        selectAdmin(utente);
        RestClientResponseException e = Assertions.assertThrows(RestClientResponseException.class, () -> creaChiavePubblica(utente));
        pojo.setException(e);
    }

    @When("l'utente {string} crea una chiave pubblica per la PG")
    public BffPublicKeyResponse creaChiavePubblica(String utente) {
        selectAdmin(utente);
        BffPublicKeyRequest request = new BffPublicKeyRequest()
                .publicKey(publicKey)
                .name("TEST PUBLIC KEY");
        try {
            BffPublicKeyResponse response = pnLegalPersonAuthClient.newPublicKeyV1(request);
            pojo.getResponseWithStatusList().add(
                    LegalPersonAuthExpectedResponseWithStatus.builder()
                            .status("ACTIVE")
                            .response(response)
                            .build());
            return response;
        } catch (RestClientResponseException e) {
            pojo.setException(e);
            return null;
        }
    }

    @Given("esiste una chiave pubblica creata da {string} in stato {string}")
    public void creaChiavePubblicaAndChangeStatus(String utente, String status) {
        BffPublicKeyResponse response = creaChiavePubblica(utente);
        String kid = response.getKid();
        switch (status.toUpperCase()) {
            case "BLOCKED" -> bloccaChiavePubblica(kid);
            case "ROTATED" -> ruotaChiavePubblica(kid);
            case "CANCELLED" -> {
                bloccaChiavePubblica(kid);
                cancellaChiavePubblica(kid);
            }
        }
    }

    @Given("non ci sono chiavi pubbliche per la PG")
    public void checkForAbsenceOfPublicKey() {
        boolean existPublicKey = existPublicKeyActive();
        Assertions.assertFalse(existPublicKey);
    }

    @When("l'utente {string} {string} la chiave pubblica per la PG che si trova in stato {string}")
    public void changeStatusChiavePubblica(String utente, String operation, String status) {
        selectAdmin(utente);
        if (operation.equalsIgnoreCase("RICREA")) {
            creaChiavePubblica(utente);
        } else {
            String kid = getPublicKeyKidByStatus(status);
            Assertions.assertNotNull(kid);
            switch (operation.toUpperCase()) {
                case "BLOCCA" -> bloccaChiavePubblica(kid);
                case "RUOTA" -> ruotaChiavePubblica(kid);
                case "RIATTIVA" -> riattivaChiavePubblica(kid);
                case "CANCELLA" -> cancellaChiavePubblica(kid);
            }
        }
    }

    @When("l'utente {string} {string} la chiave pubblica per la PG che si trova in stato {string} usando la chiave pubblica già usata per la chiave attiva")
    public void changeStatusChiavePubblicaConLaChiaveGiàUsata(String utente, String operation, String status) {
        this.publicKeyRotation = publicKey;
        changeStatusChiavePubblica(utente, operation, status);
    }

    @When("l'utente {string} {string} una chiave pubblica per la PG che non esiste")
    public void changeStatusChiavePubblicaInesistente(String utente, String operation) {
        selectAdmin(utente);
        String kid = "test_inesistente";
        switch (operation.toUpperCase()) {
            case "BLOCCA" -> bloccaChiavePubblica(kid);
            case "RUOTA" -> ruotaChiavePubblica(kid);
            case "RIATTIVA" -> riattivaChiavePubblica(kid);
            case "CANCELLA" -> cancellaChiavePubblica(kid);
        }
    }

    @When("l'utente {string} recupera la lista delle chiavi pubbliche")
    public void searchChiaviPubbliche(String utente) {
        selectAdmin(utente);
        try {
            pnLegalPersonAuthClient.getPublicKeysV1(null, null, null, null);
        } catch (RestClientResponseException e) {
            pojo.setException(e);
        }
    }

    @Then("la chiave pubblica in stato {string} viene correttamente visualizzata nell'elenco delle chiavi pubbliche per la PG")
    public void searchChiavePubblica(String status) {
        BffPublicKeysResponse response = pnLegalPersonAuthClient.getPublicKeysV1(null, null, null, null);
        String targetKid = getPublicKeyKidByStatus(status);
        Assertions.assertTrue(response.getItems().stream().map(PublicKeyRow::getKid).toList().contains(targetKid));
        Assertions.assertNull(pojo.getException());
    }

    @Then("la chiave pubblica in stato {string} non è più presente nell'elenco delle chiavi pubbliche per la PG")
    public void searchChiavePubblicaNegativeCase(String status) {
        BffPublicKeysResponse response = pnLegalPersonAuthClient.getPublicKeysV1(null, null, null, null);
        String targetKid = getPublicKeyKidByStatus(status);
        Assertions.assertFalse(response.getItems().stream().map(PublicKeyRow::getKid).toList().contains(targetKid));
        Assertions.assertNull(pojo.getException());
    }

    @Then("la chiamata restituisce un errore con status code {int} riportante il messaggio {string}")
    public void fdf(Integer errorCode, String errorMessage) {
        Assertions.assertNotNull(pojo.getException());
        Assertions.assertEquals(errorCode, pojo.getException().getRawStatusCode());
        Assertions.assertTrue(Objects.requireNonNull(pojo.getException().getMessage()).contains(errorMessage), "the error message is: " + pojo.getException().getMessage());
    }

    @Then("la chiamata restituisce un errore con status code {int}")
    public void laChiamataRestituisceUnErroreConStatusCodeConIlJwtDellUtenteNonValido(int errorCode) {
        Assertions.assertNotNull(pojo.getException());
        Assertions.assertEquals(errorCode, pojo.getException().getRawStatusCode());
    }

    @When("un utente tenta di recuperare i dati dell'utente {string}")
    public void recuperaDatiUtente(String userToSearch) {
        recuperaDatiUtente(userToSearch, "corretta");
    }

    @When("un utente tenta di recuperare i dati dell'utente {string} della pg {string}")
    public void recuperaDatiUtente( String userToSearch, String pg) {
        try {
            String uid = retrieveUID(userToSearch);
            String pgId = pg.equals("corretta") ? organizationId : null;
            PgUser user = privateUserApi.getPgUsersPrivate(uid, pgId);
            pojo.addUserToList(user);
        } catch (RestClientResponseException e) {
            pojo.setException(e);
        }
    }

    @Then("i dati utente vengono correttamente recuperati")
    public void checkDatiUtente() {
        Assertions.assertNotNull(this.pojo.getUserListResponse());
        Assertions.assertFalse(this.pojo.getUserListResponse().isEmpty());
    }

    @Then("la chiamata va in status 200 e restituisce una lista utenti vuota")
    public void checkEmptyUserList() {
        Assertions.assertTrue(pojo.getUserListResponse().isEmpty());
    }

    private String getPublicKeyKidByStatus(String status) {
        return status.equalsIgnoreCase("INESISTENTE") ? UNKNOWN_KID : this.pojo.getResponseWithStatusList()
                .stream()
                .filter(data -> data.getStatus().equalsIgnoreCase(status))
                .findFirst()
                .map(data -> data.getResponse().getKid())
                .orElse(null);
    }

    public void bloccaChiavePubblica(String kid) {
        try {
            pnLegalPersonAuthClient.changeStatusPublicKeyV1(kid, "BLOCK");
            updateResponseStatus("BLOCKED", kid);
        } catch (RestClientResponseException e) {
            pojo.setException(e);
        }
    }

    private void ruotaChiavePubblica(String kid) {
        BffPublicKeyRequest bffPublicKeyRequest = new BffPublicKeyRequest()
                .publicKey(publicKeyRotation)
                .name("PUBLIC KEY ROTATE")
                .exponent("AQAB");

        try {
            BffPublicKeyResponse response = pnLegalPersonAuthClient.rotatePublicKeyV1(kid, bffPublicKeyRequest);
            this.pojo.getResponseWithStatusList().add(
                    LegalPersonAuthExpectedResponseWithStatus.builder().
                            response(response).
                            status("ACTIVE").
                            build()
            );
            updateResponseStatus("ROTATED", kid);
        } catch (RestClientResponseException e) {
            pojo.setException(e);
        }
    }

    private void riattivaChiavePubblica(String kid) {
        try {
            pnLegalPersonAuthClient.changeStatusPublicKeyV1(kid, "ENABLE");
            updateResponseStatus("ACTIVE", kid);
        } catch (RestClientResponseException e) {
            pojo.setException(e);
        }
    }

    public void cancellaChiavePubblica(String kid) {
        try {
            pnLegalPersonAuthClient.deletePublicKeyV1(kid);
            updateResponseStatus("CANCELLED", kid);
        } catch (RestClientResponseException e) {
            pojo.setException(e);
        }
    }

    private void updateResponseStatus(String status, String kid) {
        if (kid == null) {
            this.pojo.getResponseWithStatusList().add(
                    LegalPersonAuthExpectedResponseWithStatus.builder()
                            .response(null)
                            .status(status)
                            .build());
        } else {
            this.pojo.getResponseWithStatusList().stream().filter(
                            x -> x.getResponse().getKid().equals(kid)).findFirst().
                    ifPresent(listElement -> listElement.setStatus(status));
        }
    }

    public boolean existPublicKeyActive() {
        selectAdmin("AMMINISTRATORE");
        BffPublicKeysResponse response = Assertions.assertDoesNotThrow(() -> pnLegalPersonAuthClient.getPublicKeysV1(null, null, null, null));
        return Optional.ofNullable(response)
                .map(BffPublicKeysResponse::getItems)
                .map(data -> data.stream()
                        .anyMatch(key -> key.getStatus().getValue().equals("ACTIVE")))
                .orElse(false);
    }

    @Before("@publicKeyCreation")
    public void eliminaChiaviPubblicheCreate() {
        selectAdmin("AMMINISTRATORE");
        BffPublicKeysResponse response = Assertions.assertDoesNotThrow(() -> pnLegalPersonAuthClient.getPublicKeysV1(null, null, null, null));
        List<String> blockedKids = response.getItems().stream()
                .filter(data -> data.getStatus() != null && data.getStatus().getValue().equalsIgnoreCase("BLOCKED"))
                .map(PublicKeyRow::getKid)
                .toList();

        blockedKids.forEach(this::cancellaChiavePubblica);
        List<PublicKeyRow> otherPublicKeys = response.getItems().stream()
                .filter(data -> !blockedKids.contains(data.getKid()))
                .toList();

        otherPublicKeys.forEach(pk -> {
            if (pk.getStatus() != null && pk.getStatus().getValue().equalsIgnoreCase("ACTIVE")) {
                pnLegalPersonAuthClient.changeStatusPublicKeyV1(pk.getKid(), "BLOCK");
            }
            if (pk.getStatus() != null && !pk.getStatus().getValue().equalsIgnoreCase("CANCELLED")) {
                pnLegalPersonAuthClient.deletePublicKeyV1(pk.getKid());
            }
        });
    }

    public String retrieveUID(String user) {
        switch (user.toLowerCase()) {
            case "alda merini" -> {
                return adminUid;
            }
            case "maria montessori" -> {
                return adminGroupUid;
            }
            case "nilde iotti" -> {
                return operatorUid;
            }
            case "unknown" -> {
                return "4a700ac1-4893-495e-9b51-bbf4c37f4bbc";
            }
            case "vuoto" -> {
                return null;
            }
            default -> {
                throw new IllegalArgumentException("user not allowed");
            }
        }
    }

}
