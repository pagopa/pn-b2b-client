package it.pagopa.pn.cucumber.steps.pg;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.BffPublicKeyRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.BffPublicKeyResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.BffPublicKeysResponse;
import it.pagopa.pn.client.b2b.pa.service.IPnLegalPersonAuthClient;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import it.pagopa.pn.cucumber.utils.LegalPersonAuthExpectedResponseWithStatus;
import it.pagopa.pn.cucumber.utils.LegalPersonsAuthStepsPojo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestClientResponseException;

import java.util.Objects;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class LegalPersonAuthSteps {
    private final LegalPersonsAuthStepsPojo pojo;
    private final IPnLegalPersonAuthClient pnLegalPersonAuthClient;

    public LegalPersonAuthSteps(IPnLegalPersonAuthClient pnLegalPersonAuthClient, LegalPersonsAuthStepsPojo pojo) {
        this.pnLegalPersonAuthClient = pnLegalPersonAuthClient;
        this.pojo = pojo;
    }

    /**
     * REMINDER CAMBIO STATI //TODO delete
     * attiva -> ruotata, bloccata
     * ruotata-> cancellata
     * bloccata -> attiva, cancellata
     */

    private void selectAdmin(String utente) {
        switch (utente.toUpperCase()) {
            case "AMMINISTRATORE" -> pnLegalPersonAuthClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
            case "AMMINISTRATORE CON GRUPPO ASSOCIATO" -> System.out.println("TODO 1");
            case "NON AMMINISTRATORE" -> System.out.println("TODO 2");
        }
    }

    @Given("esiste una chiave pubblica creata da {string} in stato {string}")
    public void creaChiavePubblicaAndChangeStatus(String utente, String status) {
        selectAdmin(utente);
        BffPublicKeyResponse response = creaChiavePubblica();
        if (response != null) {
            pojo.getResponseWithStatusList().add(
                    LegalPersonAuthExpectedResponseWithStatus.builder()
                            .status("ACTIVE")
                            .response(response)
                            .build());
            String kid = response.getKid();
            switch (status.toUpperCase()) {
                case "BLOCCATA" -> bloccaChiavePubblica(kid);
                case "RUOTATA" -> ruotaChiavePubblica(kid);
                case "CANCELLATA" -> {
                    bloccaChiavePubblica(kid);
                    cancellaChiavePubblica(kid);
                }
            }
        }
    }

    @Given("l'utente {string} non ha censito alcuna chiave pubblica")
    public void checkForAbsenceOfPublicKey(String utente) {
        //TODO
    }

    @When("l'utente {string} {string} la suddetta chiave pubblica per la PG")
    public void changeStatusChiavePubblica(String utente, String operation) {
        selectAdmin(utente);
        if (operation.equalsIgnoreCase("RICREA")) {
            BffPublicKeyResponse response = creaChiavePubblica();
            if (response != null) {
                pojo.getResponseWithStatusList().add(
                        LegalPersonAuthExpectedResponseWithStatus.builder()
                                .status("ACTIVE")
                                .response(response)
                                .build());
            }
        } else {
            String kid = pojo.getResponseWithStatusList().get(0).getResponse().getKid();
            switch (operation.toUpperCase()) {
                case "BLOCCA" -> bloccaChiavePubblica(kid);
                case "RUOTA" -> ruotaChiavePubblica(kid);
                case "RIATTIVA" -> riattivaChiavePubblica(kid);
                case "CANCELLA" -> cancellaChiavePubblica(kid);
            }
        }
    }

    @When("l'utente {string} crea una seconda chiava pubblica")
    public BffPublicKeyResponse createSecondPublicKey(String utente) {
        selectAdmin(utente);
        BffPublicKeyRequest request = new BffPublicKeyRequest().name("TEST 2");
        BffPublicKeyResponse response = pnLegalPersonAuthClient.newPublicKeyV1(request);
        this.pojo.getResponseWithStatusList().add(
                LegalPersonAuthExpectedResponseWithStatus.builder()
                        .status("ACTIVE")
                        .response(response)
                        .build());
        return response;
    }

    @When("l'utente {string} crea una seconda chiava pubblica e prova a cambiarne lo stato in {string}")
    public void createSecondPublicKeyAndChangeItsStatus(String utente, String status) {
        BffPublicKeyResponse response = createSecondPublicKey(utente);
        try {
            switch (status.toUpperCase()) {
                case "BLOCCATA" -> bloccaChiavePubblica(response.getKid());
                case "RUOTATA" -> ruotaChiavePubblica(response.getKid());
                case "RIATTIVATA" -> riattivaChiavePubblica(response.getKid());
                case "CANCELLATA" -> cancellaChiavePubblica(response.getKid());
            }
        } catch (RestClientResponseException e) {
            this.pojo.setException(e);
        }
    }

    @When("l'utente {string} {string} la chiava pubblica generatasi dopo aver ruotato la prima")
    public void changeStatusOfPublicKeyCreatedAfterRotation(String utente, String operation) {
        selectAdmin(utente);
        String secondKid = this.pojo.getResponseWithStatusList().get(1).getResponse().getKid();
        try {
            switch (operation.toUpperCase()) {
                case "BLOCCA" -> bloccaChiavePubblica(secondKid);
                case "RUOTA" -> ruotaChiavePubblica(secondKid);
                case "RIATTIVA" -> riattivaChiavePubblica(secondKid);
                case "CANCELLA" -> cancellaChiavePubblica(secondKid);
            }
        } catch (RestClientResponseException e) {
            this.pojo.setException(e);
        }
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
            pnLegalPersonAuthClient.getPublicKeysV1(0, "", "", true);//TODO modificare i parametri
        } catch (RestClientResponseException e) {
            pojo.setException(e);
        }
    }


    @Then("la chiave pubblica viene correttamente visualizzata nell'elenco delle chiavi pubbliche per la PG")
    public void searchChiavePubblica() {
        BffPublicKeysResponse response = pnLegalPersonAuthClient.getPublicKeysV1(0, "", "", true);//TODO modificare i parametri
        Assertions.assertTrue(response.getItems().stream().map(x -> x.getKid()).toList().
                contains(pojo.getResponseWithStatusList().get(0).getResponse().getKid()));
    }

    @Then("la chiave pubblica non è più presente nell'elenco delle chiavi pubbliche per la PG")
    public void searchChiavePubblicaNegativeCase() {
        BffPublicKeysResponse response = pnLegalPersonAuthClient.getPublicKeysV1(0, "", "", true);//TODO modificare i parametri
        Assertions.assertFalse(response.getItems().stream().map(x -> x.getKid()).toList().
                contains(pojo.getResponseWithStatusList().get(0).getResponse().getKid()));
    }

    @Then("la chiamata restituisce un errore con status code {int} riportante il messaggio {string}")
    public void fdf(Integer errorCode, String errorMessage) {
        Assertions.assertNotNull(pojo.getException());
        Assertions.assertEquals(errorCode, pojo.getException().getRawStatusCode());
        Assertions.assertTrue(Objects.requireNonNull(pojo.getException().getMessage()).contains(errorMessage));
    }

    @When("l'utente {string} crea una chiave pubblica per la PG")
    public BffPublicKeyResponse creaChiavePubblica() {
        BffPublicKeyRequest request = new BffPublicKeyRequest().name("TEST");
        try {
            return pnLegalPersonAuthClient.newPublicKeyV1(request);
        } catch (RestClientResponseException e) {
            pojo.setException(e);
            return null;
        }
    }

    @When("l'utente {string} tenta di recuperare i dati dell'utente avente user id {string}")
    public void recuperaDatiUtente(String utente, String userId) {
        //TODO
    }

    @Then("i dati utente vengono correttamente recuperati")
    public void checkDatiUtente() {
        Assertions.assertNotNull(this.pojo.getUserListResponse());
    }

    @Then("la chiamata va in status 200 e restituisce una lista utenti vuota")
    public void checkEmptyUserList() {
        Assertions.assertTrue(pojo.getUserListResponse().isEmpty());
    }

    private void bloccaChiavePubblica(String kid) {
        try {
            pnLegalPersonAuthClient.changeStatusPublicKeyV1(kid, "BLOCKED");
            updateResponseStatus("BLOCKED", kid);
        } catch (RestClientResponseException e) {
            pojo.setException(e);
        }
    }

    private void ruotaChiavePubblica(String kid) {
        BffPublicKeyRequest bffPublicKeyRequest = new BffPublicKeyRequest();//TODO crea request
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
            pnLegalPersonAuthClient.changeStatusPublicKeyV1(kid, "ACTIVE");
            updateResponseStatus("ACTIVE", kid);
        } catch (RestClientResponseException e) {
            pojo.setException(e);
        }
    }

    private void cancellaChiavePubblica(String kid) {
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

    @After("publicKeyCreation")
    public void eliminaChiaviPubblicheCreate() {
        this.pojo.getResponseWithStatusList().forEach(pojo -> {
            if (pojo.getStatus().equalsIgnoreCase("ACTIVE")) {
                bloccaChiavePubblica(pojo.getResponse().getKid());
            }
            if (!pojo.getStatus().equalsIgnoreCase("CANCELLED")) {
                pnLegalPersonAuthClient.deletePublicKeyV1(pojo.getResponse().getKid());
            }
        });
    }
}
