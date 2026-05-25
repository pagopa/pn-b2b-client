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
import io.jsonwebtoken.*;
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
import java.security.Signature;
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
    private final ClientTokenConfigurator clientTokenConfigurator;
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
        this.clientTokenConfigurator = clientTokenConfigurator;
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
        createClient("admin", tenantType, clientType);
    }

    @Given("un {string} del fruitore {string} ha già creato un client di tipo {interopClientType} aggiungendo se stesso come membro e caricando una coppia di chiavi")
    public void createClient(String role, String tenantType, ClientAssertionOptions.ClientType clientType) {
        clientCommonSteps.createClientsForTenants(tenantType, 1, clientType.name());
        clientCommonSteps.tenantHasAlreadyAddUsersWithRole(tenantType, role);
        clientKeyReadSteps.clientPublicKeyUpload(role, tenantType);
    }

    @When("{string} crea una client assertion valida per un client di tipo {interopClientType}")
    public void createValidClientAssertion(String tenantType, ClientAssertionOptions.ClientType clientType) {
        createCustomClientAssertion(clientType, Collections.emptyList());
    }

    @When("{string} crea una client assertion per un client di tipo {interopClientType} utilizzando una chiave {string} di lunghezza {int}")
    public void createClientAssertion(String tenantType, ClientAssertionOptions.ClientType clientType, String keyType, int keySize) {
        createCustomClientAssertionWithKey(clientType, Collections.emptyList(), keyType, keySize);
    }

    @When("{string} crea una client assertion per un client di tipo {interopClientType} utilizzando una chiave {string} di lunghezza {int} con:")
    public void createClientAssertion(String tenantType, ClientAssertionOptions.ClientType clientType, String keyType, int keySize, List<JwtClaimOverride> overrides) {
        createCustomClientAssertionWithKey(clientType, overrides, keyType, keySize);
    }

    @When("{string} crea una client assertion per un client di tipo {interopClientType} con:")
    public void createClientAssertion(String tenantType, ClientAssertionOptions.ClientType clientType, List<JwtClaimOverride> overrides) {
        createCustomClientAssertion(clientType, overrides);
    }

    @When("{string} crea una DPoP proof per la client assertion")
    public void createDPoPProof(String tenantType) {
        createCustomDPoP(Collections.emptyList());
    }

    @When("{string} crea una DPoP proof per la client assertion con:")
    public void createDPoPProof(String tenantType, List<JwtClaimOverride> overrides) {
        createCustomDPoP(overrides);
    }

    @When("{string} crea una DPoP proof con firma non valida")
    public void createDPoPProofWithInvalidSignature(String tenantType) {
        KeyPair keyPair = KeyPairGeneratorUtil.createKeyPair("RSA", 2048);
        String dpopProof = this.dPoPTokenService.buildDpopProof(keyPair);
        String tamperedDpop = dpopProof.substring(0, dpopProof.lastIndexOf(".") + 1) + "bm90LWEtc2lnbmF0dXJl";
        logDPopProof(tamperedDpop);
        devToolsContext.setActualDpopProof(tamperedDpop);
    }

    @When("un {string} di {string} richiede la validazione della client assertion appena creata")
    public void verifyClientAssertion(String role, String tenantType) {
        clientCreateStep.setRole(role, tenantType);
        runClientAssertionValidation(null, null, null);
    }

    @When("{string} richiede la validazione della client assertion appena creata")
    @When("{string} richiede la validazione della client assertion e della DPoP Proof appena creata")
    public void verifyClientAssertion(String tenantType) {
        clientCreateStep.setRole("admin", tenantType);
        runClientAssertionValidation(null, null, null);
    }

    @When("{string} richiede la validazione della client assertion appena creata con un token di autorizzazione non valido")
    public void verifyClientAssertionWithoutAuthorization(String tenantType) {
        clientTokenConfigurator.setBearerToken("invalidBearerToken");
        sharedStepsContext.setUserToken("invalidBearerToken");
        runClientAssertionValidation(null, null, null);
    }

    @When("{string} richiede la validazione della client assertion appena creata specificando client_assertion_type={string} e grant_type={string}")
    public void verifyClientAssertion(String tenantType, String clientAssertionType, String grantType) {
        clientCreateStep.setRole("admin", tenantType);
        runClientAssertionValidation(clientAssertionType, grantType, null);
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

    @And("l'admin dell'erogatore {string} ha creato un eservice di tipo {isAsynchronous} e l'admin del fruitore {string} ha creato una richiesta di fruizione per quell'eservice e ha associato una finalità in stato {string} a quel client")
    public void createEserviceAndPurpose(String tenantErogatore, Boolean isAsync, String tenantFruitore, String statoPurpose) {
        agreementCommonSteps.tenantHasAlreadyCreatedAndPublishedEServiceWithAsyncExchange(tenantErogatore, 1, isAsync);
        agreementCommonSteps.tenantAlreadyHasFruitionRequestWithState(tenantFruitore, "ACTIVE");
        purposeCommonStep.tenantHasAlreadyCreateFinalizationWithStatus(tenantFruitore, 1, statoPurpose);
        clientPurposeRemoveStep.addPurposeToClient(tenantFruitore);
    }

    private void createCustomClientAssertion(ClientAssertionOptions.ClientType clientType, List<JwtClaimOverride> overrides) {
        DPoPTokenService.PreparedClient preparedClient = sharedStepsContext.getClientCommonContext().getLastPreparedClient();
        JwtBuilder validClientAssertion = buildValidClientAssertion(clientType);

        String clientAssertion = validClientAssertion.signWith(preparedClient.keyPair().getPrivate()).compact();

        try {
            clientAssertion = applyOverridesToEncodedJwt(clientAssertion, overrides, preparedClient.keyPair().getKeyPair());
        } catch (Exception e) {
            throw new RuntimeException("Error processing JSON for client assertion header: " + e.getMessage(), e);
        }

        logClientAssertion(clientAssertion);
        devToolsContext.setActualClientAssertion(clientAssertion);
    }

    private void createCustomClientAssertionWithKey(ClientAssertionOptions.ClientType clientType, List<JwtClaimOverride> overrides, String keyType, int keySize) {
        KeyPair keyPair = KeyPairGeneratorUtil.createKeyPair(keyType, keySize);
        JwtBuilder validClientAssertion = buildValidClientAssertion(clientType);

        String clientAssertion = validClientAssertion.signWith(keyPair.getPrivate()).compact();

        try {
            clientAssertion = applyOverridesToEncodedJwt(clientAssertion, overrides, keyPair);
        } catch (Exception e) {
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

    private void logDPopProof(String dpopProof) {
        log.info("DPoPProof header: '{}'", new String(Base64.getUrlDecoder().decode(dpopProof.split("\\.")[0])));
        log.info("DPoPProof payload: '{}'", new String(Base64.getUrlDecoder().decode(dpopProof.split("\\.")[1])));
        log.info("DPoPProof: '{}'", dpopProof);
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

            dpopProof = jwtBuilder.compact();

            try {
                dpopProof = applyOverridesToEncodedJwt(dpopProof, overrides, keyPair);
            } catch (Exception e) {
                throw new RuntimeException("Error processing JSON for client assertion header: " + e.getMessage(), e);
            }
        }

        logDPopProof(dpopProof);
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

    private String applyOverridesToEncodedJwt(
            String encodedJwt,
            List<JwtClaimOverride> overrides,
            KeyPair keyPair
    ) throws Exception {

        String[] jwtParts = encodedJwt.split("\\.", -1);
        if (jwtParts.length != 3) {
            throw new IllegalArgumentException("JWT non valido: attese 3 parti");
        }

        ObjectMapper mapper = new ObjectMapper();

        ObjectNode header = (ObjectNode) mapper.readTree(decodeBase64Url(jwtParts[0]));
        ObjectNode payload = (ObjectNode) mapper.readTree(decodeBase64Url(jwtParts[1]));

        String rawHeaderOverride = null;
        String rawPayloadOverride = null;

        for (JwtClaimOverride ov : overrides) {
            String claim = ov.claim();
            String raw = ov.value();

            switch (claim) {
                case "__rawHeader" -> rawHeaderOverride = raw;
                case "__rawPayload" -> rawPayloadOverride = raw;

                case "header.alg" -> header.put("alg", raw);
                case "header.kid" -> header.put("kid", raw);
                case "header.typ" -> header.put("typ", raw);
                case "__removeHeader" -> header.remove(raw);

                case "iss" -> payload.put("iss", raw);
                case "sub" -> payload.put("sub", raw);
                case "aud" -> setJsonClaim(mapper, payload, "aud", parseAud(raw));
                case "jti" -> payload.put("jti", raw);
                case "iat" -> setJsonClaim(mapper, payload, "iat", parseEpoch(raw));
                case "exp" -> setJsonClaim(mapper, payload, "exp", parseEpoch(raw));
                case "nbf" -> setJsonClaim(mapper, payload, "nbf", parseEpoch(raw));

                case "htm" -> payload.put("htm", raw);
                case "htu" -> payload.put("htu", raw);

                case "purposeId" -> setJsonClaim(mapper, payload, "purposeId", parseMaybeUuid(raw));
                case "digest" -> setJsonClaim(mapper, payload, "digest", parseMaybeJson(raw, mapper));
                case "algorithm" -> payload.put("algorithm", raw);
                case "assertionType" -> payload.put("client_assertion_type", raw);
                case "grantType" -> payload.put("grant_type", raw);

                case "urlCallback" -> payload.put("url_callback", raw);
                case "scope" -> payload.put("scope", raw);

                case "invalidClaim" -> payload.put("invalid_claim", raw);

                case "__remove" -> payload.remove(raw);

                default -> throw new IllegalArgumentException("Claim non supportato: " + claim);
            }
        }

        String newHeaderBase64Url = rawHeaderOverride != null
                ? encodeBase64Url(rawHeaderOverride)
                : encodeBase64Url(mapper.writeValueAsString(header));

        String newPayloadBase64Url = rawPayloadOverride != null
                ? encodeBase64Url(rawPayloadOverride)
                : encodeBase64Url(mapper.writeValueAsString(payload));

        if (keyPair != null) {
            String signingInput = newHeaderBase64Url + "." + newPayloadBase64Url;

            String keyAlg = keyPair.getPrivate().getAlgorithm();
            String sigAlg = switch (keyAlg) {
                case "RSA" -> "SHA256withRSA";
                case "EC"  -> "SHA256withECDSA";
                case "Ed25519" -> "Ed25519";
                default -> throw new IllegalArgumentException("Unsupported key");
            };

            Signature signature = Signature.getInstance(sigAlg);
            signature.initSign(keyPair.getPrivate());
            signature.update(signingInput.getBytes());
            String encodedSignature =Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(signature.sign());
            return newHeaderBase64Url + "." + newPayloadBase64Url + "." + encodedSignature;
        }

        return newHeaderBase64Url + "." + newPayloadBase64Url + "." + jwtParts[2];
    }

    private Object parseMaybeJson(String raw, ObjectMapper mapper) {
        try {
            return mapper.readTree(raw);
        } catch (Exception e) {
            return raw;
        }
    }

    private String decodeBase64Url(String value) {
        return new String(
                Base64.getUrlDecoder().decode(value),
                StandardCharsets.UTF_8
        );
    }

    private String encodeBase64Url(String value) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private void setJsonClaim(
            ObjectMapper mapper,
            ObjectNode node,
            String claimName,
            Object value
    ) {
        if (value == null) {
            node.putNull(claimName);
        } else {
            node.set(claimName, mapper.valueToTree(value));
        }
    }

    private void runClientAssertionValidation(String clientAssertionType, String grantType, Boolean isAsync) {

        String clientAssertion = devToolsContext.getActualClientAssertion();
        UUID clientId = sharedStepsContext.getClientCommonContext().getLastClient();
        String dpopProof = devToolsContext.getActualDpopProof();

        final String currentAssertionType = clientAssertionType == null ? CLIENT_ASSERTION_TYPE : clientAssertionType;
        final String currentGrantType = grantType == null ? GRANT_TYPE : grantType;

        try {
            var result = devToolsClient.validateTokenGeneration(clientAssertion, currentAssertionType, currentGrantType, clientId.toString(), isAsync, dpopProof);
            devToolsContext.setLastValidationResult(result);
        } catch (Exception e) {
            log.error("Errore durante la validazione della client assertion: {}", e.getMessage());
        }
    }
}
