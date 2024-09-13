package it.pagopa.pn.cucumber.steps.pg;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.BffPublicKeyRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.BffPublicKeyResponse;
import it.pagopa.pn.client.b2b.pa.service.IPnLegalPersonAuthClient;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.List;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class LegalPersonAuthSteps {

    private List<ExpectedPublicKey> publicKeysResponses;
    //private BffPublicKeyResponse bffPublicKeyResponse;

    private final IPnLegalPersonAuthClient pnLegalPersonAuthClient;

    public LegalPersonAuthSteps(IPnLegalPersonAuthClient pnLegalPersonAuthClient) {
        this.pnLegalPersonAuthClient = pnLegalPersonAuthClient;
    }

    @Given("l'amministratore {string} censisce una chiave pubblica per la PG")
    public void lAmministratoreCensisceUnaChiavePubblicaPerLaPG(String admin) {
        selectAdmin(admin);
        BffPublicKeyRequest request = new BffPublicKeyRequest().name("nome Chiave Pubblica");
        BffPublicKeyResponse bffPublicKeyResponse = Assertions.assertDoesNotThrow(() -> pnLegalPersonAuthClient.newPublicKeyV1(request));
        publicKeysResponses.add(new ExpectedPublicKey(bffPublicKeyResponse, "ACTIVE"));
    }

    @And("l'amministratore {string} blocca la chiave pubblica")
    public void lAmministratoreBloccaLaChiavePubblica(String admin) {
        selectAdmin(admin);
        String kid = publicKeysResponses.get(0).bffPublicKeyResponse.getKid();
        Assertions.assertDoesNotThrow(() -> pnLegalPersonAuthClient.changeStatusPublicKeyV1(kid, "BLOCKED"));
        publicKeysResponses.stream()
                .filter(data -> data.bffPublicKeyResponse.getKid().equals(kid))
                .findFirst()
                .map(data -> {
                    data.setExpectedStatus("BLOCKED");
                    return data;
                });
    }

    @And("l'amministratore {string} cancella la chiave pubblica")
    public void lAmministratoreCancellaLaChiavePubblica(String admin) {
        selectAdmin(admin);
        //get0 solo per evitare errori di intellij
        Assertions.assertDoesNotThrow(() -> pnLegalPersonAuthClient.deletePublicKeyV1(publicKeysResponses.get(0).bffPublicKeyResponse.getKid()));
    }

    void selectAdmin(String admin) {
        switch (admin.toLowerCase()) {
            case "amministratore" -> {
                pnLegalPersonAuthClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
            }
        }
    }

    @And("l'amministratore {string} {string} la chiave pubblica per la PG che è {string}")
    public void lAmministratoreLaChiavePubblicaPerLaPGConOriginalState(String admin, String operation, String originalState) {
        selectAdmin(admin);
        switch (operation.toLowerCase()) {
            case "CREA" -> {
                BffPublicKeyRequest request = new BffPublicKeyRequest().name("nome Chiave Pubblica");
                BffPublicKeyResponse bffPublicKeyResponse = Assertions.assertDoesNotThrow(() -> pnLegalPersonAuthClient.newPublicKeyV1(request));
                publicKeysResponses.add(new ExpectedPublicKey(bffPublicKeyResponse, "ACTIVE"));
            }
            case "BLOCCA" -> {
                String kid = publicKeysResponses.stream()
                        .filter(data -> data.expectedStatus.equals(originalState))
                        .findFirst()
                        .map(data -> data.bffPublicKeyResponse.getKid())
                        .orElse("");
                Assertions.assertDoesNotThrow(() -> pnLegalPersonAuthClient.changeStatusPublicKeyV1(kid, "BLOCKED"));
                publicKeysResponses.stream()
                        .filter(data -> data.bffPublicKeyResponse.getKid().equals(kid))
                        .findFirst()
                        .map(data -> {
                            data.setExpectedStatus("BLOCKED");
                            return data;
                        });
            }
        }
    }

    private class ExpectedPublicKey {
        private BffPublicKeyResponse bffPublicKeyResponse;
        private String expectedStatus;

        public ExpectedPublicKey(BffPublicKeyResponse bffPublicKeyResponse, String expectedStatus) {
            this.bffPublicKeyResponse = bffPublicKeyResponse;
            this.expectedStatus = expectedStatus;
        }

        public void setBffPublicKeyResponse(BffPublicKeyResponse bffPublicKeyResponse) {
            this.bffPublicKeyResponse = bffPublicKeyResponse;
        }

        public void setExpectedStatus(String expectedStatus) {
            this.expectedStatus = expectedStatus;
        }
    }
}
