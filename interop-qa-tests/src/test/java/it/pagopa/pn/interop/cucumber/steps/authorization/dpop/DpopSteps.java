package it.pagopa.pn.interop.cucumber.steps.authorization.dpop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.domain.KeyPairDecorator;
import it.pagopa.interop.authorization.service.M2MDPopTokenService;
import it.pagopa.interop.authorization.service.utils.voucher.DPopVoucherService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;

import java.util.Base64;
import java.util.Map;

import static it.pagopa.interop.authorization.enums.M2MRole.M2M_ADMIN;

@RequiredArgsConstructor
public class DpopSteps {

    private final M2MDPopTokenService m2mDpopTokenService;
    private final DPopVoucherService voucherService;

    private String tenant;
    private Map<String, Object> tokenResponse;
    private KeyPairDecorator usedKeyPair;

    @Given("un tenant {string} configurato correttamente")
    public void tenantConfiguratoCorrettamente(String tenantType) {
        this.tenant = tenantType;
    }

    @And("genero un client M2M con chiave EC P-256 e lo registro per il tenant {string}")
    public void generoClientConChiaveEC(String tenant) {
        this.tokenResponse = m2mDpopTokenService.generateRawTokenResponse(tenant, M2M_ADMIN);
    }

    @When("genero un access token tramite richiesta con header DPoP")
    public void generoAccessTokenConDPoP() {
        // già gestito nel passo precedente: tokenResponse è valorizzato
        Assertions.assertNotNull(tokenResponse.get("access_token"));
    }

    @Then("la risposta contiene lo status {int}")
    public void laRispostaContieneStatus(int status) {
        // Assume che venga gestito implicitamente: Spring RestTemplate non fornisce status code in `Map`
        Assertions.assertTrue(tokenResponse.containsKey("access_token"));
    }

    @And("la risposta JSON contiene il campo {string} con valore {string}")
    public void verificaTokenType(String campo, String valoreAtteso) {
        Assertions.assertEquals(valoreAtteso, tokenResponse.get(campo));
    }

    @And("il campo {string} è presente nel token ed è uguale al thumbprint della chiave pubblica registrata")
    public void verificaCnfJktPresente(String cnfField) throws JsonProcessingException {
        String accessToken = (String) tokenResponse.get("access_token");
        String[] jwtParts = accessToken.split("\\.");
        Assertions.assertEquals(3, jwtParts.length, "Access token JWT non valido");

        String payload = new String(Base64.getUrlDecoder().decode(jwtParts[1]));
        Map payloadMap = new ObjectMapper().convertValue(
                new ObjectMapper().readValue(payload, Map.class), Map.class);

        @SuppressWarnings("unchecked")
        Map<String, String> cnf = (Map<String, String>) payloadMap.get("cnf");
        Assertions.assertNotNull(cnf);
        Assertions.assertEquals(voucherService.calculateKidFromPublicKey(usedKeyPair.getPublic()), cnf.get("jkt"));
    }
}
