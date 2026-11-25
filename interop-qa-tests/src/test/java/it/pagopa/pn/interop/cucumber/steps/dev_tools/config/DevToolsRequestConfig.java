package it.pagopa.pn.interop.cucumber.steps.dev_tools.config;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.DataTableType;
import io.cucumber.java.ParameterType;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.utility.StepParser;
import lombok.RequiredArgsConstructor;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class DevToolsRequestConfig {

    private final SharedStepsContext sharedStepsContext;
    private final ExpressionParser parser = new SpelExpressionParser();

    public record JwtClaimOverride(String claim, String value) {}
    public record ValidationRow(String step, TokenGenerationValidationEntry entry) {}

    @ParameterType("asincrono|sincrono")
    public boolean isAsynchronous(String value) {
        return switch (value) {
            case "asincrono" -> true;
            case "sincrono" -> false;
            default -> throw new IllegalArgumentException("Invalid async mode: " + value);
        };
    }

    @DataTableType
    public EServiceSeed toEServiceSeed(DataTable dataTable) {
        EServiceSeed seed = new EServiceSeed();

        Map<String, String> rows = new HashMap<>();
        dataTable.cells().forEach(row -> {
            if (row.size() >= 2) {
                rows.put(
                        row.get(0) != null ? row.get(0).trim() : "",
                        row.get(1) != null ? row.get(1).trim() : ""
                );
            }
        });

        rows.forEach((key, value) -> {
            switch (key) {
                case "name" -> seed.setName(value);
                case "description" -> seed.setDescription(value);
                case "asyncExchange" -> seed.setAsyncExchange(Boolean.valueOf(value));
                case "technology" -> seed.setTechnology(EServiceTechnology.fromValue(value));
                case "mode" -> seed.setMode(EServiceMode.fromValue(value));

                case "isSignalHubEnabled" ->
                        seed.setIsSignalHubEnabled(Boolean.valueOf(value));

                case "isConsumerDelegable" ->
                        seed.setIsConsumerDelegable(Boolean.valueOf(value));

                case "isClientAccessDelegable" ->
                        seed.setIsClientAccessDelegable(Boolean.valueOf(value));

                case "personalData" ->
                        seed.setPersonalData(Boolean.valueOf(value));

                default -> throw new IllegalArgumentException(
                        "Campo non supportato per EServiceSeed: " + key
                );
            }
        });

        return seed;
    }

    @DataTableType
    public UpdateEServiceSeed toUpdateEServiceSeed(DataTable dataTable) {
        UpdateEServiceSeed seed = new UpdateEServiceSeed();

        Map<String, String> rows = new HashMap<>();
        dataTable.cells().forEach(row -> {
            if (row.size() >= 2) {
                rows.put(
                        row.get(0) != null ? row.get(0).trim() : "",
                        row.get(1) != null ? row.get(1).trim() : ""
                );
            }
        });

        rows.forEach((key, value) -> {
            switch (key) {
                case "name" -> seed.setName(value);
                case "description" -> seed.setDescription(value);
                case "asyncExchange" -> seed.setAsyncExchange(Boolean.valueOf(value));
                case "technology" -> seed.setTechnology(EServiceTechnology.fromValue(value));
                case "mode" -> seed.setMode(EServiceMode.fromValue(value));

                case "isSignalHubEnabled" ->
                        seed.setIsSignalHubEnabled(Boolean.valueOf(value));

                case "isConsumerDelegable" ->
                        seed.setIsConsumerDelegable(Boolean.valueOf(value));

                case "isClientAccessDelegable" ->
                        seed.setIsClientAccessDelegable(Boolean.valueOf(value));

                case "personalData" ->
                        seed.setPersonalData(Boolean.valueOf(value));

                default -> throw new IllegalArgumentException(
                        "Campo non supportato per EServiceSeed: " + key
                );
            }
        });

        if (!rows.containsKey("name")) {
            seed.setName(sharedStepsContext.getEServicesCommonContext().getName());
        }
        if (!rows.containsKey("description")) {
            seed.setDescription(sharedStepsContext.getEServicesCommonContext().getDescription());
        }

        return seed;
    }

    @DataTableType
    public UpdateEServiceTemplateVersionSeed toUpdateEServiceTemplateSeed(DataTable dataTable) {
        UpdateEServiceTemplateVersionSeed seed = new UpdateEServiceTemplateVersionSeed();
        seed.setAttributes(new EServiceTemplateAttributesSeed());

        Map<String, String> rows = new HashMap<>();
        dataTable.cells().forEach(row -> {
            if (row.size() >= 2) {
                rows.put(
                    row.get(0) != null ? row.get(0).trim() : "",
                    row.get(1) != null ? row.get(1).trim() : ""
                );
            }
        });

        StandardEvaluationContext context = new StandardEvaluationContext(seed);

        rows.forEach((key, value) -> {

            if (value.equals(":null")) {
                value = null;
            }

            if (key.contains(".") && key.startsWith("asyncExchangeProperties")) {
                if (seed.getAsyncExchangeProperties() == null) {
                    seed.setAsyncExchangeProperties(new AsyncExchangeProperties());
                }
                value = StepParser.normalize(value);

                // SpEL automatically handles String -> Integer, String -> Boolean, etc.
                parser.parseExpression(key).setValue(context, value);
                return;
            }

            switch (key) {
                case "description" -> seed.setDescription(value);
                case "voucherLifespan" -> seed.setVoucherLifespan(Integer.valueOf(value));
                case "dailyCallsPerConsumer" -> seed.setDailyCallsPerConsumer(Integer.valueOf(value));
                case "dailyCallsTotal" -> seed.setDailyCallsTotal(Integer.valueOf(value));
                case "agreementApprovalPolicy" -> seed.setAgreementApprovalPolicy(AgreementApprovalPolicy.fromValue(value));
                default -> throw new IllegalArgumentException(
                        "Campo non supportato per EServiceSeed: " + key
                );
            }
        });

        return seed;
    }

    @DataTableType
    public UpdateEServiceDescriptorSeed toUpdateEServiceDescriptorSeed(DataTable dataTable) {
        UpdateEServiceDescriptorSeed seed = new UpdateEServiceDescriptorSeed();
        seed.setAttributes(new DescriptorAttributesSeed());

        Map<String, String> rows = new HashMap<>();
        dataTable.cells().forEach(row -> {
            if (row.size() >= 2) {
                rows.put(row.get(0) != null ? row.get(0).trim() : "", row.get(1) != null ? row.get(1).trim() : "");
            }
        });

        StandardEvaluationContext context = new StandardEvaluationContext(seed);

        rows.forEach((key, value) -> {
            if (key.contains(".") && key.startsWith("asyncExchangeProperties")) {
                if (seed.getAsyncExchangeProperties() == null) {
                    seed.setAsyncExchangeProperties(new AsyncExchangeProperties());
                }
            }

            value = StepParser.normalize(value);

            if ("audience".equals(key)) {
                seed.setAudience(Arrays.stream(value.split(",")).map(String::trim).toList());
            } else if ("agreementApprovalPolicy".equals(key)) {
                seed.setAgreementApprovalPolicy(AgreementApprovalPolicy.fromValue(value));
            } else {
                // SpEL automatically handles String -> Integer, String -> Boolean, etc.
                parser.parseExpression(key).setValue(context, value);
            }
        });

        return seed;
    }

    @DataTableType
    public AsyncExchangePropertiesInstanceSeed toAsyncExchangePropertiesInstanceSeed(DataTable dataTable) {
        AsyncExchangePropertiesInstanceSeed seed = new AsyncExchangePropertiesInstanceSeed();

        Map<String, String> rows = new HashMap<>();
        dataTable.cells().forEach(row -> {
            if (row.size() >= 2) {
                rows.put(row.get(0) != null ? row.get(0).trim() : "", row.get(1) != null ? row.get(1).trim() : "");
            }
        });

        rows.forEach((key, value) -> {
            if (value.equals(":null")) {
                value = null;
            }
            switch (key) {
                case "responseTime" -> seed.responseTime(value == null ? null : Integer.valueOf(value));
                case "resourceAvailableTime" -> seed.resourceAvailableTime(value == null ? null : Integer.valueOf(value));
                case "maxResultSet" -> seed.maxResultSet(value == null ? null : Integer.valueOf(value));
            }
        });

        return seed;
    }

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
