package it.pagopa.pn.interop.cucumber.steps.authorization.dpop;

import com.nimbusds.jose.jwk.KeyType;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.domain.KeyPairDecorator;
import it.pagopa.interop.authorization.service.DPoPTokenService;
import it.pagopa.interop.authorization.service.utils.DpopProofService;
import it.pagopa.interop.authorization.service.utils.voucher.domain.VoucherResponse;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;

import java.lang.reflect.Field;
import java.security.KeyPair;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class DPoPSteps {

    private final DPoPTokenService dPoPTokenService;
    private final SharedStepsContext context;

    private VoucherResponse voucherResponse = new VoucherResponse();
    private String dpopProofJwt;

    private final HttpMethod DEFAULT_HTTP_METHOD = HttpMethod.POST;
    private final String DEFAULT_TYP = "dpop+jwt";
    @Value("${authorization.server.token.creation.url}") private String DEFAULT_OAUTH_SERVER_URL;

    public DPoPSteps(DPoPTokenService tokenService, SharedStepsContext sharedStepsContext) {
        this.dPoPTokenService = tokenService;
        this.context = sharedStepsContext;
        this.dPoPTokenService.setHttpCallExecutor(context.getHttpCallExecutor());
        this.dPoPTokenService.setIdentityService(context.getIdentityService());
    }

    @When("{string} genera una dpop proof con una chiave {string}")
    @When("{string} genera una dpop proof con una chiave {string} e verifica i campi HTU,HTM")
    public void getDpopProof(String tenantType, String keyAlgorithm) {
        this.dpopProofJwt = generateDpopProofWith(keyAlgorithm, DEFAULT_TYP, DEFAULT_HTTP_METHOD, DEFAULT_OAUTH_SERVER_URL);
    }

    @When("{string} genera una dpop proof con una chiave {string} e campo typ errato")
    public void getDpopProofWithTyp(String tenantType, String keyAlgorithm) {
        this.dpopProofJwt = generateDpopProofWith(keyAlgorithm, "wrong+dpop", DEFAULT_HTTP_METHOD, DEFAULT_OAUTH_SERVER_URL);
    }

    @When("{string} genera una dpop proof con una chiave {string} e metodo errato {string}")
    public void getDpopProofWithHtm(String tenantType, String keyAlgorithm, String httpMethod) {
        this.dpopProofJwt = generateDpopProofWith(keyAlgorithm, DEFAULT_TYP, HttpMethod.valueOf(httpMethod), DEFAULT_OAUTH_SERVER_URL);
    }

    @When("{string} genera una dpop proof con una chiave {string} e campo HTU errato")
    public void getDpopProofWithHtu(String tenantType, String keyAlgorithm) {
        this.dpopProofJwt = generateDpopProofWith(keyAlgorithm, DEFAULT_TYP, DEFAULT_HTTP_METHOD, "https://auth.interop.pagopa.it/invalid-token-endpoint");
    }

    @When("{string} genera una dpop proof con una chiave {string} scaduto rispetto il campo IAT")
    public void getDpopExpired(String tenantType, String keyAlgorithm) {
        this.dpopProofJwt = generateDpopProofWith(keyAlgorithm, DEFAULT_TYP, DEFAULT_HTTP_METHOD, DEFAULT_OAUTH_SERVER_URL);

        try {
            log.info("Attesa di 61 secondi per far scadere il campo 'iat' della DPoP proof...");
            Thread.sleep(61000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @When("{string} genera una dpop proof usando la chiave pubblica {string} di una key pair legittima ma firmando con una chiave privata diversa")
    public void getMaliciousDPoP(String tenantType, String keyType) {
        this.dpopProofJwt = this.generateMaliciousDpopProof(keyType);
    }

    @And("{string} cerca di ottenere un access token usando il dpop proof creato")
    public void getAccessToken(String tenantType) {
        var client = resolvePreparedClient();
        var purposeId = resolvePurposeId();

        Pair<String, VoucherResponse> proofWithToken = dPoPTokenService.getAccessTokenWithoutCache(dpopProofJwt, client, tenantType, purposeId);
        this.voucherResponse = proofWithToken.getRight();
    }

    @When("{string} tenta di ottenere un access token usando il dpop proof creato e inviando due header DPoP nella richiesta")
    public void getAccessTokenWithDuplicateDpop(String tenantType) {
        var client = resolvePreparedClient();
        var purposeId = resolvePurposeId();

        Pair<Integer, String> response = dPoPTokenService.sendRequestWithDuplicateDpopHeaders(client, purposeId, dpopProofJwt);
        context.getHttpCallExecutor().setRawResponse(response.getLeft(), response.getRight());
    }

    @When("{string} tenta di ottenere un access token senza includere l'header DPoP")
    public void getAccessTokenWithoutDPoP(String tenantType) {
        this.getAccessToken(tenantType);
    }

    @Then("la response contiene:")
    public void checkResponse(DataTable table) {
        Map<String, String> expectedValues = table.asMap(String.class, String.class);
        Assertions.assertThat(voucherResponse).isNotNull();

        expectedValues.forEach((fieldName, expectedValue) -> {
            try {
                Field field = VoucherResponse.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object actualValue = field.get(voucherResponse);
                assertFieldMatches(fieldName, actualValue, expectedValue);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Errore sull'accesso al campo '" + fieldName + "': " + e.getMessage(), e);
            }
        });
    }

    @Then("il campo cnf.jkt è presente e corretto")
    public void verifyCnfJktField() {
        String accessToken = Optional.ofNullable(voucherResponse)
                .map(VoucherResponse::getAccessToken)
                .orElseThrow(() -> new IllegalStateException("Access token non disponibile"));

        DpopProofService.ValidationResult result = dPoPTokenService.validateCnfJkt(accessToken, dpopProofJwt);

        Assertions.assertThat(result.isValid())
                .as("Errore nella validazione del campo cnf.jkt: " + result.getMessage())
                .isTrue();
    }

    private DPoPTokenService.PreparedClient resolvePreparedClient() {
        return Optional.ofNullable(context.getClientCommonContext().getLastPreparedClient())
                .orElseThrow(() -> new IllegalStateException("Nessun client disponibile nel contesto."));
    }

    private String resolvePurposeId() {
        return Optional.ofNullable(context.getPurposeCommonContext().getPurposesIds())
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(list.size() - 1))
                .orElseThrow(() -> new IllegalStateException("Nessuna finalità disponibile nel contesto."));
    }

    private void assertFieldMatches(String fieldName, Object actualValue, String expectedDirective) {
        switch (expectedDirective.toLowerCase()) {
            case "ignore":
                return;
            case "non_null":
                Assertions.assertThat(actualValue)
                        .as(fieldName + " deve essere valorizzato")
                        .isNotNull();
                break;
            case "null":
                Assertions.assertThat(actualValue)
                        .as(fieldName + " deve essere null")
                        .isNull();
                break;
            default:
                if (actualValue instanceof Long) {
                    Assertions.assertThat(actualValue)
                            .as(fieldName + " non corrisponde")
                            .isEqualTo(Long.valueOf(expectedDirective));
                } else {
                    Assertions.assertThat(String.valueOf(actualValue))
                            .as(fieldName + " non corrisponde")
                            .isEqualTo(expectedDirective);
                }
        }
    }

    private String generateDpopProofWith(String keyType, String typValue, HttpMethod httpMethod, String oAuthServerUrl) {
       var keyPair = dPoPTokenService.generateKeyPair(keyType);

        boolean shouldOverride = !Objects.equals(typValue, DEFAULT_TYP)
                || !Objects.equals(httpMethod, DEFAULT_HTTP_METHOD)
                || !Objects.equals(oAuthServerUrl, DEFAULT_OAUTH_SERVER_URL);

        return shouldOverride
                ? dPoPTokenService.buildProofWith(keyPair, typValue, httpMethod, oAuthServerUrl)
                : dPoPTokenService.buildDpopProof(keyPair);
    }

    private String generateMaliciousDpopProof(String keyType){
        var legittimPair = dPoPTokenService.generateKeyPair(keyType);
        var maliciousPair = dPoPTokenService.generateKeyPair(keyType);

        var keyPair = new KeyPair(legittimPair.getKeyPair().getPublic(), maliciousPair.getKeyPair().getPrivate());
        var keyPairDecorator = KeyPairDecorator.of(keyPair);

        return dPoPTokenService.buildDpopProof(keyPairDecorator);
    }
}
