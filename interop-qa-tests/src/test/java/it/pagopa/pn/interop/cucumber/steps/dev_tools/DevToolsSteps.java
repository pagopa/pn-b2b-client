package it.pagopa.pn.interop.cucumber.steps.dev_tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import it.pagopa.interop.authorization.service.DPoPTokenService;
import it.pagopa.interop.authorization.service.utils.KeyPairGeneratorUtil;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions;
import it.pagopa.interop.dev_tools.service.IDevToolsClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.TokenGenerationValidationEntry;
import it.pagopa.interop.generated.openapi.clients.bff.model.TokenGenerationValidationStepFailure;
import it.pagopa.interop.generated.openapi.clients.bff.model.TokenGenerationValidationSteps;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.agreement.AgreementCommonSteps;
import it.pagopa.pn.interop.cucumber.steps.authorization.ClientCommonSteps;
import it.pagopa.pn.interop.cucumber.steps.authorization.ClientCreateStep;
import it.pagopa.pn.interop.cucumber.steps.authorization.ClientKeyReadSteps;
import it.pagopa.pn.interop.cucumber.steps.authorization.ClientPurposeRemoveStep;
import it.pagopa.pn.interop.cucumber.steps.dev_tools.config.DevToolsRequestConfig;
import it.pagopa.pn.interop.cucumber.steps.dev_tools.config.DevToolsRequestConfig.JwtClaimOverride;
import it.pagopa.pn.interop.cucumber.steps.dev_tools.model.DevToolsContext;
import it.pagopa.pn.interop.cucumber.steps.purpose.PurposeCommonStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;

import static it.pagopa.interop.authorization.service.utils.JWTUtils.*;
import static it.pagopa.interop.authorization.service.utils.voucher.VoucherService.calculateKidFromPublicKey;

@Slf4j
public class DevToolsSteps {

    private final static String CLIENT_ASSERTION_TYPE = "urn:ietf:params:oauth:client-assertion-type:jwt-bearer";
    private final static String GRANT_TYPE = "client_credentials";

    private final IDevToolsClient devToolsClient;
    private final ClientCommonSteps clientCommonSteps;
    private final ClientKeyReadSteps clientKeyReadSteps;
    private final AgreementCommonSteps agreementCommonSteps;
    private final PurposeCommonStep purposeCommonStep;
    private final ClientPurposeRemoveStep clientPurposeRemoveStep;
    private final ClientCreateStep clientCreateStep;
    private final SharedStepsContext sharedStepsContext;
    private final String clientAssertionJwtAudience;
    private final DevToolsContext devToolsContext = new DevToolsContext();
    private final DPoPTokenService dPoPTokenService;

    public DevToolsSteps(
            @Value("${client.assertion.jwt.audience}") String clientAssertionJwtAudience,
            ClientTokenConfigurator clientTokenConfigurator,
            SharedStepsContext sharedStepsContext,
            ClientCommonSteps clientCommonSteps,
            ClientKeyReadSteps clientKeyReadSteps,
            AgreementCommonSteps agreementCommonSteps,
            PurposeCommonStep purposeCommonStep,
            ClientPurposeRemoveStep clientPurposeRemoveStep,
            ClientCreateStep clientCreateStep,
            DPoPTokenService dPoPTokenService
    ) {
        this.clientCommonSteps = clientCommonSteps;
        this.clientKeyReadSteps = clientKeyReadSteps;
        this.devToolsClient = clientTokenConfigurator.getDevToolsClient();
        devToolsClient.setHttpCallExecutor(sharedStepsContext.getHttpCallExecutor());
        this.clientAssertionJwtAudience = clientAssertionJwtAudience;
        this.sharedStepsContext = sharedStepsContext;
        this.clientPurposeRemoveStep = clientPurposeRemoveStep;
        this.clientCreateStep = clientCreateStep;
        this.purposeCommonStep = purposeCommonStep;
        this.agreementCommonSteps = agreementCommonSteps;
        this.dPoPTokenService = dPoPTokenService;
    }

    @ParameterType("API|api|CONSUMER|consumer")
    public ClientAssertionOptions.ClientType interopClientType(String clientType) {
        return ClientAssertionOptions.ClientType.valueOf(clientType.toUpperCase());
    }

    @Given("l'admin del fruitore {string} ha già creato un client di tipo {interopClientType} aggiungendo se stesso come membro e caricando una coppia di chiavi")
    public void createClient(String tenantType, ClientAssertionOptions.ClientType clientType) {
        clientCommonSteps.createClientsForTenants(tenantType, 1, clientType.name());
        clientCommonSteps.tenantHasAlreadyAddUsersWithRole(tenantType, "admin");
        clientKeyReadSteps.clientPublicKeyUpload("admin", tenantType);
    }

    @When("{string} crea una client assertion valida per un client di tipo {interopClientType}")
    public void createValidClientAssertion(String tenantType, ClientAssertionOptions.ClientType clientType) {
        createCustomClientAssertion(clientType, Collections.emptyList());
    }

    @When("{string} crea una client assertion per un client di tipo {interopClientType} utilizzando una chiave {string} di lunghezza {int}")
    @When("{string} crea una client assertion per un client di tipo {interopClientType} utilizzando una chiave {string} di lunghezza {int}:")
    public void createClientAssertion(String tenantType, ClientAssertionOptions.ClientType clientType, String keyType, int keySize, List<JwtClaimOverride> overrides) {
        createCustomClientAssertionWithKey(clientType, overrides, keyType, keySize);
    }

    @When("{string} crea una client assertion per un client di tipo {interopClientType} con:")
    public void createClientAssertion(String tenantType, ClientAssertionOptions.ClientType clientType, List<JwtClaimOverride> overrides) {
        createCustomClientAssertion(clientType, overrides);
    }

    @When("{string} richiede la validazione della client assertion appena creata")
    @When("{string} richiede la validazione della client assertion e della DPoP Proof appena creata")
    public void verifyClientAssertion(String tenantType) {
        runClientAssertionValidation(tenantType, null, null);
    }

    @When("{string} richiede la validazione della client assertion appena creata specificando client_assertion_type={string} e grant_type={string}")
    public void verifyClientAssertion(String tenantType, String clientAssertionType, String grantType) {
        runClientAssertionValidation(tenantType, clientAssertionType, grantType);
    }

    @Then("i risultati di validazione sono:")
    public void assertValidations(List<DevToolsRequestConfig.ValidationRow> rows) {
        TokenGenerationValidationSteps expected = DevToolsRequestConfig.toTokenGenerationValidationSteps(rows);
        TokenGenerationValidationSteps actual = devToolsContext.getLastValidationResult().getSteps();

        // Mappa nome-step -> getter del corrispondente campo dentro TokenGenerationValidationSteps.
        // Serve per evitare duplicazione e iterare in modo uniforme su tutti gli step di validazione.
        Map<String, Function<TokenGenerationValidationSteps, TokenGenerationValidationEntry>> validations = Map.of(
                "clientAssertionValidation", TokenGenerationValidationSteps::getClientAssertionValidation,
                "publicKeyRetrieve", TokenGenerationValidationSteps::getPublicKeyRetrieve,
                "clientAssertionSignatureVerification", TokenGenerationValidationSteps::getClientAssertionSignatureVerification,
                "platformStatesVerification", TokenGenerationValidationSteps::getPlatformStatesVerification,
                "dpopValidation", TokenGenerationValidationSteps::getDpopValidation
        );

        validations.forEach((stepName, getter) -> {
            TokenGenerationValidationEntry actualEntry = getter.apply(actual);
            TokenGenerationValidationEntry expectedEntry = getter.apply(expected);

            // Se lo step non è valorizzato nell'expected, significa che non vogliamo fare assert su quel campo.
            if (expectedEntry == null) {
                return;
            }

            // Se invece lo step è atteso ma manca nella risposta reale, il test deve fallire.
            if (actualEntry == null) {
                throw new AssertionError("Validation step assente nell'actual: " + stepName);
            }

            // Controllo di sicurezza: ogni step restituito deve avere un result valorizzato.
            if (actualEntry.getResult() == null) {
                throw new AssertionError("Validation result is null for step: " + stepName);
            }

            // Verifica del risultato complessivo dello step.
            if (!Objects.equals(expectedEntry.getResult(), actualEntry.getResult())) {
                throw new AssertionError(
                        "Result diverso per step '%s'. Expected=%s, actual=%s"
                                .formatted(stepName, expectedEntry.getResult(), actualEntry.getResult())
                );
            }

            // Prima confrontiamo il numero di failure attese e reali.
            if (!Objects.equals(expectedEntry.getFailures().size(), actualEntry.getFailures().size())) {
                throw new AssertionError(
                        "Failure diversa per step '%s'. Expected=%s, actual=%s"
                                .formatted(stepName, expectedEntry.getFailures(), actualEntry.getFailures())
                );
            }

            // Estraiamo solo i codici errore per fare un confronto più stabile e meno dipendente
            // dall'intero contenuto degli oggetti failure.
            var actualCodes = actualEntry.getFailures().stream()
                    .map(TokenGenerationValidationStepFailure::getCode)
                    .toList();

            var expectedCodes = expectedEntry.getFailures().stream()
                    .map(TokenGenerationValidationStepFailure::getCode)
                    .toList();

            // Ogni codice atteso deve essere presente tra quelli effettivamente restituiti.
            expectedCodes.forEach(expectedCode -> {
                if (!actualCodes.contains(expectedCode)) {
                    throw new AssertionError(
                            "Failure code '%s' non presente per step '%s'. Expected codes=%s, actual codes=%s"
                                    .formatted(expectedCode, stepName, expectedCodes, actualCodes)
                    );
                }
            });
        });
    }

    @And("l'admin dell'erogatore {string} ha creato un eservice e l'admin del fruitore {string} ha creato una richiesta di fruizione per quell'eservice e ha associato la finalità a quel client")
    public void createEserviceAndPurpose(String tenantErogatore, String tenantFruitore) {
        agreementCommonSteps.tenantHasAlreadyCreatedAndPublishedEService(tenantErogatore, 1);
        agreementCommonSteps.tenantAlreadyHasFruitionRequestWithState(tenantFruitore, "ACTIVE");
        purposeCommonStep.tenantHasAlreadyCreateFinalizationWithStatus(tenantFruitore, 1, "ACTIVE");
        clientPurposeRemoveStep.addPurposeToClient(tenantFruitore);
    }

    @And("l'admin dell'erogatore {string} ha creato un eservice e l'admin del fruitore {string} ha creato una richiesta di fruizione per quell'eservice e ha associato una finalità in stato {string} a quel client")
    public void createEserviceAndPurpose(String tenantErogatore, String tenantFruitore, String statoPurpose) {
        agreementCommonSteps.tenantHasAlreadyCreatedAndPublishedEService(tenantErogatore, 1);
        agreementCommonSteps.tenantAlreadyHasFruitionRequestWithState(tenantFruitore, "ACTIVE");
        purposeCommonStep.tenantHasAlreadyCreateFinalizationWithStatus(tenantFruitore, 1, statoPurpose);
        clientPurposeRemoveStep.addPurposeToClient(tenantFruitore);
    }

    private void createCustomClientAssertion(ClientAssertionOptions.ClientType clientType, List<JwtClaimOverride> overrides) {
        DPoPTokenService.PreparedClient preparedClient = sharedStepsContext.getClientCommonContext().getLastPreparedClient();
        JwtBuilder validClientAssertion = buildValidClientAssertion(clientType);

        if (!overrides.isEmpty()) applyOverrides(validClientAssertion, overrides);

        String clientAssertion = validClientAssertion.signWith(preparedClient.keyPair().getPrivate()).compact();
        // JwtBuilder::compact aggiorna "alg" nell'header per cui eventuali modifiche devono essere riapplicate
        try {
            clientAssertion = applyOverridesToEncodedAlg(clientAssertion, overrides);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing JSON for client assertion header: " + e.getMessage(), e);
        }

        logClientAssertion(clientAssertion);
        devToolsContext.setActualClientAssertion(clientAssertion);
    }

    private void createCustomClientAssertionWithKey(ClientAssertionOptions.ClientType clientType, List<JwtClaimOverride> overrides, String keyType, int keySize) {
        KeyPair keyPair = KeyPairGeneratorUtil.createKeyPair(keyType, keySize);
        JwtBuilder validClientAssertion = buildValidClientAssertion(clientType);

        if (!overrides.isEmpty()) applyOverrides(validClientAssertion, overrides);

        String clientAssertion = validClientAssertion.signWith(keyPair.getPrivate()).compact();

        // JwtBuilder::compact aggiorna "alg" nell'header per cui eventuali modifiche devono essere riapplicate
        try {
            clientAssertion = applyOverridesToEncodedAlg(clientAssertion, overrides);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing JSON for client assertion header: " + e.getMessage(), e);
        }

        logClientAssertion(clientAssertion);
        devToolsContext.setActualClientAssertion(clientAssertion);
    }

    private void logClientAssertion(String clientAssertion) {
        log.info("Client assertion header: '{}'", new String(Base64.getUrlDecoder().decode(clientAssertion.split("\\.")[0])));
        log.info("Client assertion payload: '{}'", new String(Base64.getUrlDecoder().decode(clientAssertion.split("\\.")[1])));
        log.info("Client assertion: '{}'", clientAssertion);
    }

    private void createCustomDPoP(List<JwtClaimOverride> overrides) {
        KeyPair keyPair = KeyPairGeneratorUtil.createKeyPair("RSA", 2048);
        String dpopProof = this.dPoPTokenService.buildDpopProof(keyPair);

        if (!overrides.isEmpty()) {
            Jws<Claims> existingJws = Jwts.parser()
                    .verifyWith(keyPair.getPublic())
                    .build()
                    .parseSignedClaims(dpopProof);

            JwtBuilder jwtBuilder = Jwts.builder()
                    .header()
                    .add(existingJws.getHeader())
                    .and()
                    .claims(existingJws.getPayload())
                    .signWith(keyPair.getPrivate());

            applyOverrides(jwtBuilder, overrides);
            dpopProof = jwtBuilder.compact();
        }

        log.info("DPoP: '{}'", dpopProof);
        devToolsContext.setActualDpopProof(dpopProof);
    }

    private JwtBuilder buildValidClientAssertion(ClientAssertionOptions.ClientType clientType) {
        DPoPTokenService.PreparedClient preparedClient = sharedStepsContext.getClientCommonContext().getLastPreparedClient();
        String rawClientId = preparedClient != null ? preparedClient.clientId().toString() : null;
        String rawKid = preparedClient != null ? calculateKidFromPublicKey(preparedClient.keyPair().getPublic()) : null;

        // Builder base "valido", poi alterato con DataTable
        JwtBuilder jwtBuilder = Jwts.builder()
                .issuer(rawClientId)
                .subject(rawClientId)
                .audience().add(this.clientAssertionJwtAudience).and()
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plusSeconds(43200)))
                .header().add("kid", rawKid).and();

        if (clientType == ClientAssertionOptions.ClientType.CONSUMER) {
            UUID purposeId = sharedStepsContext.getPurposeCommonContext().getLastPurposeId();
            jwtBuilder.claim("purposeId", purposeId);
        }

        return jwtBuilder;
    }

    private void applyOverrides(JwtBuilder builder, List<JwtClaimOverride> overrides) {
        for (JwtClaimOverride ov : overrides) {
            String claim = ov.claim();
            String raw = ov.value();

            switch (claim) {
                // HEADER
                case "header.alg" -> setHeader(builder, "alg", raw);
                case "header.kid" -> setHeader(builder, "kid", raw);

                // CLAIM STANDARD
                case "iss" -> setClaim(builder, "iss", raw);
                case "sub" -> setClaim(builder, "sub", raw);
                case "aud" -> setClaim(builder, "aud", parseAud(raw));
                case "jti" -> setClaim(builder, "jti", raw);
                case "iat" -> setClaim(builder, "iat", parseEpoch(raw));
                case "exp" -> setClaim(builder, "exp", parseEpoch(raw));
                case "nbf" -> setClaim(builder, "nbf", parseEpoch(raw));

                // CLAIM DPOP
                case "htm" -> setClaim(builder, "htm", raw);
                case "htu" -> setClaim(builder, "htu", raw);

                // CLAIM CUSTOM
                case "purposeId" -> setClaim(builder, "purposeId", parseMaybeUuid(raw));
                case "digest" -> setClaim(builder, "digest", raw);
                case "algorithm" -> setClaim(builder, "algorithm", raw);
                case "assertionType" -> setClaim(builder, "client_assertion_type", raw);
                case "grantType" -> setClaim(builder, "grant_type", raw);

                // Comandi speciali utili per test negativi
                case "__remove" -> removeClaim(builder, raw); // raw = nome claim da rimuovere
                case "__removeHeader" -> removeHeader(builder, raw); // raw = nome header da rimuovere
                case "__rawPayload" -> setRawPayload(builder, raw);

                default -> throw new IllegalArgumentException("Claim non supportato: " + claim);
            }
        }
    }

    private String applyOverridesToEncodedAlg(String encodedClientAssertion, List<JwtClaimOverride> overrides) throws JsonProcessingException {
        String[] jwtParts = encodedClientAssertion.split("\\.");
        String headerBase64Url = jwtParts[0];
        String payloadBase64Url = jwtParts[1];
        String signatureBase64Url = jwtParts[2];

        byte[] decodedHeaderBytes = Base64.getUrlDecoder().decode(headerBase64Url);
        String headerJson = new String(decodedHeaderBytes, StandardCharsets.UTF_8);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode headerNode = mapper.readTree(headerJson);

        String overrideAlg = overrides.stream()
            .filter(ov -> "header.alg".equals(ov.claim()))
            .map(JwtClaimOverride::value)
            .findFirst()
            .orElse(null);
        if (overrideAlg != null) {
            ((ObjectNode) headerNode).put("alg", overrideAlg);
        }

        boolean removeAlg = overrides.stream()
                .filter(ov -> "__remove".equals(ov.claim()) && "header.alg".equals(ov.value()))
                .count() == 1;
        if (removeAlg) {
            ((ObjectNode) headerNode).remove("alg");
        }

        String modifiedHeaderJson = mapper.writeValueAsString(headerNode);
        String newHeaderBase64Url = Base64.getUrlEncoder().withoutPadding().encodeToString(modifiedHeaderJson.getBytes(StandardCharsets.UTF_8));

        return newHeaderBase64Url + "." + payloadBase64Url + "." + signatureBase64Url;
    }

    private String getRawClientAssertion() {
        return devToolsContext.getActualClientAssertion();
    }

    private void runClientAssertionValidation(String tenantType, String clientAssertionType, String grantType) {
        clientCreateStep.setRole("admin", tenantType);

        String clientAssertion = devToolsContext.getActualClientAssertion();
        UUID clientId = sharedStepsContext.getClientCommonContext().getLastClient();
        String dpopProof = devToolsContext.getActualDpopProof();

        final String currentAssertionType = clientAssertionType == null ? CLIENT_ASSERTION_TYPE : clientAssertionType;
        final String currentGrantType = grantType == null ? GRANT_TYPE : grantType;

        var result = devToolsClient.validateTokenGeneration(clientAssertion, currentAssertionType, currentGrantType, clientId.toString(), dpopProof);
        devToolsContext.setLastValidationResult(result);
    }

}
