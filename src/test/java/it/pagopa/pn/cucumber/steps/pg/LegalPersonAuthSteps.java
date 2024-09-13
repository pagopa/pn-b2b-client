package it.pagopa.pn.cucumber.steps.pg;

import io.cucumber.java.en.Given;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.BffPublicKeyRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.BffPublicKeyResponse;
import it.pagopa.pn.client.b2b.pa.service.IPnLegalPersonAuthClient;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import it.pagopa.pn.cucumber.utils.LegalPersonsAuthStepsPojo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Arrays;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class LegalPersonAuthSteps {
    private LegalPersonsAuthStepsPojo pojo;
    private final IPnLegalPersonAuthClient pnLegalPersonAuthClient;

    public LegalPersonAuthSteps(IPnLegalPersonAuthClient pnLegalPersonAuthClient) {
        this.pnLegalPersonAuthClient = pnLegalPersonAuthClient;
    }

    /**
     * REMINDER CAMBIO STATI //TODO delete
     * attiva -> ruotata, bloccata
     * ruotata-> cancellata
     * bloccata -> attiva, cancellata
     */

    @Given("esiste una chiave pubblica creata da {string} in stato {string}")
    public void creaChiavePubblicaAndChangeStatus(String admin, String status) {
        selectAdmin(admin);
        creaChiavePubblica();
        String kid = pojo.getPublicKeysResponses().get(0).bffPublicKeyResponse.getKid();
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
        if (Arrays.asList("CREA", "RICREA").contains(operation.toUpperCase())) {
            creaChiavePubblica();
        } else {
            String kid = pojo.getPublicKeysResponses().get(0).bffPublicKeyResponse.getKid();
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
        pnLegalPersonAuthClient.getPublicKeysV1();//TODO aggiungere parametri
    }

    private void selectAdmin(String admin) {
        switch (admin.toUpperCase()) {
            case "AMMINISTRATORE" -> pnLegalPersonAuthClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
        }
    }

    private void creaChiavePubblica() {
        BffPublicKeyRequest request = new BffPublicKeyRequest().name("TEST");
        try {
            BffPublicKeyResponse bffPublicKeyResponse = pnLegalPersonAuthClient.newPublicKeyV1(request);
            pojo.getPublicKeysResponses().add(new ExpectedPublicKey(bffPublicKeyResponse, "ACTIVE"));
        } catch (RestClientException e) {
            pojo.setException(e);
        }
    }

    private void bloccaChiavePubblica(String kid) {
        try {
            pnLegalPersonAuthClient.changeStatusPublicKeyV1(kid, "BLOCKED");
            pojo.getPublicKeysResponses().stream()
                    .filter(data -> data.bffPublicKeyResponse.getKid().equals(kid))
                    .findFirst()
                    .map(data -> {
                        data.setExpectedStatus("BLOCKED");
                        return data;
                    });
        } catch (RestClientException e) {
            pojo.setException(e);
        }
    }

    private void ruotaChiavePubblica(String kid) {
        BffPublicKeyRequest bffPublicKeyRequest = new BffPublicKeyRequest();//TODO crea request
        try {
            pnLegalPersonAuthClient.rotatePublicKeyV1(kid, bffPublicKeyRequest);
        } catch (RestClientException e) {
            pojo.setException(e);
        }
    }

    private void riattivaChiavePubblica(String kid) {
        try {
            pnLegalPersonAuthClient.changeStatusPublicKeyV1(kid, "ACTIVE");
            pojo.getPublicKeysResponses().stream()
                    .filter(data -> data.bffPublicKeyResponse.getKid().equals(kid))
                    .findFirst()
                    .map(data -> {
                        data.setExpectedStatus("ACTIVE");
                        return data;
                    });
        } catch (RestClientException e) {
            pojo.setException(e);
        }
    }

    private void cancellaChiavePubblica(String kid) {
        try {
            pnLegalPersonAuthClient.deletePublicKeyV1(kid);
        } catch (RestClientException e) {
            pojo.setException(e);
        }
    }

    @After(todoScegliereNome)
    public void eliminaChiaviPubblicheCreate() {
        pojo.getPublicKeysResponses().stream().forEach(k -> {
            String kid = k.bffPublicKeyResponse.getKid();
            if (k.getExpectedStatus().equalsIgnoreCase("ACTIVE")) {
                bloccaChiavePubblica(kid);
            }
            if (!k.getExpectedStatus().equalsIgnoreCase("CANCELLED")) {
                pnLegalPersonAuthClient.deletePublicKeyV1(kid);
            }
        });
    }
}
