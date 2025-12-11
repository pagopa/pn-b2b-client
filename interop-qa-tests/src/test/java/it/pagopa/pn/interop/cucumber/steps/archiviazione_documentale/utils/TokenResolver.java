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
            ":userId", "identityService.getUserId(tenantType, role)",
            ":kid", "clientCommonContext.keyId",
            ":agreementId","agreementId",
            ":consumerDelegationId", "delegationCommonContext.getDelegationId()"
    );

    private final SharedStepsContext sharedContext;

    public String resolve(String token) {

        if(!isToken(token)) return token;

        if (STATIC_TOKENS.containsKey(token)) return STATIC_TOKENS.get(token);

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
        return value != null && value.startsWith(":");
    }

}
