package it.pagopa.pn.interop.cucumber.steps.dev_tools;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.dev_tools.service.IDevToolsClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.TokenGenerationValidationEntry;
import it.pagopa.interop.generated.openapi.clients.bff.model.TokenGenerationValidationStepFailure;
import it.pagopa.interop.generated.openapi.clients.bff.model.TokenGenerationValidationSteps;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.authorization.ClientCreateStep;
import it.pagopa.pn.interop.cucumber.steps.authorization.model.VoucherContext;
import it.pagopa.pn.interop.cucumber.steps.dev_tools.config.DevToolsRequestConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
public class DevToolsSteps {

    private final static String CLIENT_ASSERTION_TYPE = "urn:ietf:params:oauth:client-assertion-type:jwt-bearer";
    private final static String GRANT_TYPE = "client_credentials";

    private final IDevToolsClient devToolsClient;
    private final ClientCreateStep clientCreateStep;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final VoucherContext voucherContext;
    private Boolean isAsync = false;

    public DevToolsSteps(
            ClientTokenConfigurator clientTokenConfigurator,
            SharedStepsContext sharedStepsContext,
            VoucherContext voucherContext,
            ClientCreateStep clientCreateStep
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.devToolsClient = clientTokenConfigurator.getDevToolsClient();
        devToolsClient.setHttpCallExecutor(sharedStepsContext.getHttpCallExecutor());
        this.sharedStepsContext = sharedStepsContext;
        this.voucherContext = voucherContext;
        this.clientCreateStep = clientCreateStep;
    }

    @When("l'utente sceglie la validazione asincrona")
    public void setDebugMode() {
        isAsync = true;
    }

    @When("l'erogatore {string} richiede la validazione della client assertion appena creata")
    public void verifyClientAssertionProducer(String tenant) {
        clientCreateStep.setRole("admin", tenant);
        String clientId = sharedStepsContext.getProducerKeychainCommonContext().getFirstProducerKeychainId().toString();
        runClientAssertionValidation(null, null, isAsync, clientId);
    }

    @When("un {string} di {string} richiede la validazione della client assertion appena creata")
    public void verifyClientAssertion(String role, String tenantType) {
        clientCreateStep.setRole(role, tenantType);
        UUID clientId = sharedStepsContext.getClientCommonContext().getLastClient();
        runClientAssertionValidation(null, null, isAsync, clientId.toString());
    }

    @When("{string} richiede la validazione della client assertion appena creata")
    @When("{string} richiede la validazione della client assertion e della DPoP Proof appena creata")
    public void verifyClientAssertion(String tenantType) {
        clientCreateStep.setRole("admin", tenantType);
        UUID clientId = sharedStepsContext.getClientCommonContext().getLastClient();
        runClientAssertionValidation(null, null, isAsync, clientId.toString());
    }

    @When("{string} richiede la validazione della client assertion appena creata con un token di autorizzazione non valido")
    public void verifyClientAssertionWithoutAuthorization(String tenantType) {
        clientTokenConfigurator.setBearerToken("invalidBearerToken");
        sharedStepsContext.setUserToken("invalidBearerToken");
        UUID clientId = sharedStepsContext.getClientCommonContext().getLastClient();
        runClientAssertionValidation(null, null, isAsync, clientId.toString());
    }

    @When("{string} richiede la validazione della client assertion appena creata specificando client_assertion_type={string} e grant_type={string}")
    public void verifyClientAssertion(String tenantType, String clientAssertionType, String grantType) {
        clientCreateStep.setRole("admin", tenantType);
        UUID clientId = sharedStepsContext.getClientCommonContext().getLastClient();
        runClientAssertionValidation(clientAssertionType, grantType, isAsync, clientId.toString());
    }

    @Then("i risultati di validazione sono:")
    public void assertValidations(List<DevToolsRequestConfig.ValidationRow> rows) {
        TokenGenerationValidationSteps expected = DevToolsRequestConfig.toTokenGenerationValidationSteps(rows);
        TokenGenerationValidationSteps actual = voucherContext.getLastValidationResult().getSteps();

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

    private void runClientAssertionValidation(String clientAssertionType, String grantType, Boolean isAsync, String clientId) {

        String clientAssertion = voucherContext.getActualClientAssertion();
        String dpopProof = voucherContext.getActualDpopProof();

        final String currentAssertionType = clientAssertionType == null ? CLIENT_ASSERTION_TYPE : clientAssertionType;
        final String currentGrantType = grantType == null ? GRANT_TYPE : grantType;

        try {
            var result = devToolsClient.validateTokenGeneration(clientAssertion, currentAssertionType, currentGrantType, clientId, isAsync, dpopProof);
            voucherContext.setLastValidationResult(result);
        } catch (Exception e) {
            log.error("Errore durante la validazione della client assertion: {}", e.getMessage());
        }
    }
}
