package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils;

import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import lombok.RequiredArgsConstructor;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@RequiredArgsConstructor
public class TokenResolver {


    private static final ExpressionParser PARSER = new SpelExpressionParser();

    private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.ofPattern("dd");
    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("MM");
    private static final LocalDate today = LocalDate.now();

    private static final Map<String, String> STATIC_TOKENS = Map.of(
            ":year", String.valueOf(today.getYear()),
            ":onlyMonth", today.format(MONTH_FORMAT),
            ":onlyDay", today.format(DAY_FORMAT)
    );


    private static final Map<String, String> DYNAMIC_TOKENS = Map.of(
            ":clientId", "clientCommonContext.getLastClient()",
            ":userId", "identityService.getUserId(tenantType, bucketRole)",
            ":kid", "clientCommonContext.keyId",
            ":agreementId","agreementId",
            ":consumerDelegationId", "delegationCommonContext.getDelegationId()",
            ":producerDelegationId", "delegationCommonContext.getDelegationId()",
            ":purposeId", "purposeCommonContext.getLastPurposeId()",
            ":purposeVersionId", "purposeCommonContext.getCurrentVersionId()",
            ":riskAnalysisId", "riskAnalysisCommonContext.getRiskAnalysisId()"
    );

    private final SharedStepsContext sharedContext;

    public String resolve(String value) {

        if (value == null) return null;

        // Caso 1: stringa tipo key=:fileToken
        if (value.contains("=")) {
            String[] parts = value.split("=", 2);

            if (parts.length == 2) {
                String key = parts[0];
                String right = parts[1];

                // solo la parte destra può essere un fileToken
                if (isToken(right)) {
                    return key + "=" + resolveSingleToken(right);
                }
            }
            return value; // non è un fileToken e non contiene fileToken risolvibili
        }

        // Caso 2: fileToken puro come ":year" o ":userId"
        if (isToken(value)) {
            return resolveSingleToken(value);
        }

        return value;
    }

    private String resolveSingleToken(String token) {

        if (STATIC_TOKENS.containsKey(token)) {
            return STATIC_TOKENS.get(token);
        }

        String expression = DYNAMIC_TOKENS.get(token);
        if (expression == null) {
            throw new IllegalArgumentException("Token sconosciuto: " + token);
        }

        StandardEvaluationContext ctx = new StandardEvaluationContext(sharedContext);
        return Objects.requireNonNull(PARSER.parseExpression(expression).getValue(ctx)).toString();
    }

    public List<String> resolve(List<String> tokens) {
        return tokens.stream().map(this::resolve).toList();
    }

    private boolean isToken(String value) {
        return value != null && (value.startsWith(":") || value.contains(":"));
    }

}
