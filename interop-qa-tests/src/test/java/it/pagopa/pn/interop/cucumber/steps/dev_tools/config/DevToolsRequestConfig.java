package it.pagopa.pn.interop.cucumber.steps.dev_tools.config;

import io.cucumber.java.DataTableType;
import it.pagopa.interop.generated.openapi.clients.bff.model.TokenGenerationValidationEntry;
import it.pagopa.interop.generated.openapi.clients.bff.model.TokenGenerationValidationStepFailure;
import it.pagopa.interop.generated.openapi.clients.bff.model.TokenGenerationValidationStepResult;
import it.pagopa.interop.generated.openapi.clients.bff.model.TokenGenerationValidationSteps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DevToolsRequestConfig {

    public record JwtClaimOverride(String claim, String value) {}
    public record ValidationRow(String step, TokenGenerationValidationEntry entry) {}

    @DataTableType
    public JwtClaimOverride jwtBuilder(Map<String, String> row) {
        String claim = row.get("claim");
        String value = row.get("value");

        if (claim == null || claim.isBlank()) {
            throw new IllegalArgumentException("Il campo 'claim' è obbligatorio");
        }

        // value può essere vuoto/null: utile per simulare claim mancanti o header non valorizzati
        return new JwtClaimOverride(claim.trim(), value);
    }

    @DataTableType
    public ValidationRow validationRow(Map<String, String> row) {
        String step = row.get("step");
        String resultRaw = row.get("result");
        String errorsRaw = row.get("errors");

        if (step == null || step.isBlank()) {
            throw new IllegalArgumentException("Il campo 'step' è obbligatorio");
        }
        if (resultRaw == null || resultRaw.isBlank()) {
            throw new IllegalArgumentException("Il campo 'result' è obbligatorio");
        }

        TokenGenerationValidationEntry entry = new TokenGenerationValidationEntry();
        entry.setResult(TokenGenerationValidationStepResult.valueOf(resultRaw.trim().toUpperCase()));
        entry.setFailures(parseFailures(errorsRaw));

        return new ValidationRow(step.trim(), entry);
    }

    public static TokenGenerationValidationSteps toTokenGenerationValidationSteps(List<ValidationRow> rows) {
        TokenGenerationValidationSteps steps = new TokenGenerationValidationSteps();

        for (ValidationRow row : rows) {
            switch (row.step()) {
                case "clientAssertionValidation" -> steps.setClientAssertionValidation(row.entry());
                case "publicKeyRetrieve" -> steps.setPublicKeyRetrieve(row.entry());
                case "clientAssertionSignatureVerification" -> steps.setClientAssertionSignatureVerification(row.entry());
                case "platformStatesVerification" -> steps.setPlatformStatesVerification(row.entry());
                case "dpopValidation" -> steps.setDpopValidation(row.entry());
                default -> throw new IllegalArgumentException("Step non supportato: " + row.step());
            }
        }

        return steps;
    }

    private static List<TokenGenerationValidationStepFailure> parseFailures(String errorsRaw) {
        if (errorsRaw == null || errorsRaw.isBlank() || "[]".equals(errorsRaw.trim())) {
            return List.of();
        }

        String normalized = errorsRaw.trim();
        if (!normalized.startsWith("[") || !normalized.endsWith("]")) {
            throw new IllegalArgumentException("Formato errors non valido. Atteso [ERR1,ERR2], trovato: " + errorsRaw);
        }

        String content = normalized.substring(1, normalized.length() - 1).trim();
        if (content.isBlank()) {
            return List.of();
        }

        String[] codes = content.split(",");
        List<TokenGenerationValidationStepFailure> failures = new ArrayList<>();

        for (String code : codes) {
            String c = code.trim();
            if (c.isEmpty()) {
                continue;
            }
            TokenGenerationValidationStepFailure failure = new TokenGenerationValidationStepFailure();
            failure.setCode(c);
            failures.add(failure);
        }

        return failures;
    }
}
