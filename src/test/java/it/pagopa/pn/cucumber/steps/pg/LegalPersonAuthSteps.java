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
import it.pagopa.pn.cucumber.utils.LegalPersonAuthExpectedResponsePojo;
import it.pagopa.pn.cucumber.utils.LegalPersonsAuthStepsPojo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestClientException;

import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class LegalPersonAuthSteps {
    private final LegalPersonsAuthStepsPojo pojo;
    private final List<LegalPersonAuthExpectedResponsePojo> responsePojoList;
    private final IPnLegalPersonAuthClient pnLegalPersonAuthClient;

    public LegalPersonAuthSteps(IPnLegalPersonAuthClient pnLegalPersonAuthClient, LegalPersonsAuthStepsPojo pojo) {
        this.pnLegalPersonAuthClient = pnLegalPersonAuthClient;
        this.pojo = pojo;
        this.responsePojoList = new LinkedList<>();
    }

    /**
     * REMINDER CAMBIO STATI //TODO delete
     * attiva -> ruotata, bloccata
     * ruotata-> cancellata
     * bloccata -> attiva, cancellata
     */

    private void selectAdmin(String admin) {
        switch (admin.toUpperCase()) {
            case "AMMINISTRATORE" -> pnLegalPersonAuthClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
        }
    }

    @Given("esiste una chiave pubblica creata da {string} in stato {string}")
    public void creaChiavePubblicaAndChangeStatus(String admin, String status) {
        selectAdmin(admin);
        creaChiavePubblica();
        String kid = pojo.getPublicKeysResponses().get(0).getKid();
        switch (status.toUpperCase()) {
            case "BLOCCATA" -> bloccaChiavePubblica(kid);
            case "RUOTATA" -> ruotaChiavePubblica(kid);
            case "CANCELLATA" -> {
                bloccaChiavePubblica(kid);
                cancellaChiavePubblica(kid);
            }
        }
    }

    @When("l'amministratore {string} {string} la chiave pubblica per la PG")
    public void changeStatusChiavePubblica(String admin, String operation) {
        selectAdmin(admin);
        if (asList("CREA", "RICREA").contains(operation.toUpperCase())) {
            creaChiavePubblica();
        } else {
            String kid = pojo.getPublicKeysResponses().get(0).getKid();
            switch (operation.toUpperCase()) {
                case "BLOCCA" -> bloccaChiavePubblica(kid);
                case "RUOTA" -> ruotaChiavePubblica(kid);
                case "RIATTIVA" -> riattivaChiavePubblica(kid);
                case "CANCELLA" -> cancellaChiavePubblica(kid);
            }
        }
    }

    @Then("la chiave pubblica viene correttamente visualizzata nell'elenco delle chiavi pubbliche per la PG")
    public void searchChiavePubblica() {
        BffPublicKeysResponse response = pnLegalPersonAuthClient.getPublicKeysV1(0, "", "", true);//TODO modificare i parametri
        Assertions.assertTrue(response.getItems().stream().map(x -> x.getKid()).toList().
                contains(pojo.getPublicKeysResponses().get(0).getKid()));
    }

    @Then("la chiave pubblica non è più presente nell'elenco delle chiavi pubbliche per la PG")
    public void searchChiavePubblicaNegativeCase() {
        BffPublicKeysResponse response = pnLegalPersonAuthClient.getPublicKeysV1(0, "", "", true);//TODO modificare i parametri
        Assertions.assertFalse(response.getItems().stream().map(x -> x.getKid()).toList().
                contains(pojo.getPublicKeysResponses().get(0).getKid()));
    }

    private void creaChiavePubblica() {
        BffPublicKeyRequest request = new BffPublicKeyRequest().name("TEST");
        try {
            BffPublicKeyResponse response = pnLegalPersonAuthClient.newPublicKeyV1(request);
            pojo.getPublicKeysResponses().add(response);
            updateResponseStatus(response, "ACTIVE", null);
            pojo.getPublicKeysResponses().add(pnLegalPersonAuthClient.newPublicKeyV1(request));
        } catch (RestClientException e) {
            pojo.setException(e);
        }
    }

    private void bloccaChiavePubblica(String kid) {
        try {
            pnLegalPersonAuthClient.changeStatusPublicKeyV1(kid, "BLOCKED");
            updateResponseStatus(null, "BLOCKED", kid);
        } catch (RestClientException e) {
            pojo.setException(e);
        }
    }

    private void ruotaChiavePubblica(String kid) {
        BffPublicKeyRequest bffPublicKeyRequest = new BffPublicKeyRequest();//TODO crea request
        try {
            pnLegalPersonAuthClient.rotatePublicKeyV1(kid, bffPublicKeyRequest);
            updateResponseStatus(null, "ROTATED", kid);
        } catch (RestClientException e) {
            pojo.setException(e);
        }
    }

    private void riattivaChiavePubblica(String kid) {
        try {
            pnLegalPersonAuthClient.changeStatusPublicKeyV1(kid, "ACTIVE");
            updateResponseStatus(null, "ACTIVE", kid);
        } catch (RestClientException e) {
            pojo.setException(e);
        }
    }

    private void cancellaChiavePubblica(String kid) {
        try {
            pnLegalPersonAuthClient.deletePublicKeyV1(kid);
            updateResponseStatus(null, "CANCELLED", kid);
        } catch (RestClientException e) {
            pojo.setException(e);
        }
    }

    private void updateResponseStatus(BffPublicKeyResponse response, String status, String kid) {
        if (kid == null) {
            this.responsePojoList.add(LegalPersonAuthExpectedResponsePojo.builder()
                    .response(response)
                    .status(status)
                    .build());
        } else {
            this.responsePojoList.stream().filter(
                            x -> x.getResponse().getKid().equals(kid)).findFirst().
                    ifPresent(listElement -> listElement.setStatus(status));
        }
    }

    @After("todoScegliereNome")
    public void eliminaChiaviPubblicheCreate() {
        responsePojoList.forEach(pojo -> {
            if (pojo.getStatus().equalsIgnoreCase("ACTIVE")) {
                bloccaChiavePubblica(pojo.getResponse().getKid());
            }
            if (!pojo.getStatus().equalsIgnoreCase("CANCELLED")) {
                pnLegalPersonAuthClient.deletePublicKeyV1(pojo.getResponse().getKid());
            }
        });
    }
}
