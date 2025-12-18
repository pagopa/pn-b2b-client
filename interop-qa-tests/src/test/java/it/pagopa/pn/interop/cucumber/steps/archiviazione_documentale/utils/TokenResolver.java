package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils;

import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@RequiredArgsConstructor
public class TokenResolver {

    private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.ofPattern("dd");
    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("MM");
    private static final LocalDate TODAY = LocalDate.now();

    private static final Map<String, String> STATIC_TOKENS = Map.of(
            ":year", String.valueOf(TODAY.getYear()),
            ":onlyMonth", TODAY.format(MONTH_FORMAT),
            ":onlyDay", TODAY.format(DAY_FORMAT)
    );

    private static final Map<String, Function<SharedStepsContext, String>> DYNAMIC_TOKENS =
            Map.ofEntries(
                    Map.entry(":clientId", ctx -> ctx.getClientCommonContext().getLastClient().toString()),
                    Map.entry(":userId", ctx -> ctx.getIdentityService().getUserId(ctx.getTenantType(), ctx.getRole().getValue()).toString()),
                    Map.entry(":kid", ctx -> ctx.getClientCommonContext().getKeyId()),
                    Map.entry(":agreementId", ctx -> ctx.getAgreementId().toString()),
                    Map.entry(":consumerDelegationId", ctx -> ctx.getDelegationCommonContext().getDelegationId().toString()),
                    Map.entry(":producerDelegationId", ctx -> ctx.getDelegationCommonContext().getDelegationId().toString()),
                    Map.entry(":purposeId", ctx -> ctx.getPurposeCommonContext().getLastPurposeId().toString()),
                    Map.entry(":purposeVersionId", ctx -> ctx.getPurposeCommonContext().getCurrentVersionId()),
                    Map.entry(":riskAnalysisId", ctx -> ctx.getRiskAnalysisCommonContext().getRiskAnalysisId().toString()),
                    Map.entry(":riskAnalysisDailyCalls", ctx -> ctx.getRiskAnalysisCommonContext().getDailyCalls().toString()),
                    Map.entry(":eServiceName", ctx -> ctx.getEServicesCommonContext().getName())
            );


    private final SharedStepsContext sharedContext;

    public String resolve(String value) {
        if (value == null) return null;

        // Caso 1: key=:token
        if (value.contains("=")) {
            String[] parts = value.split("=", 2);
            if (parts.length == 2 && isToken(parts[1])) {
                return parts[0] + "=" + resolveSingleToken(parts[1]);
            }
            return value;
        }

        // Caso 2: token puro
        if (isToken(value)) {
            return resolveSingleToken(value);
        }

        return value;
    }

    public List<String> resolve(List<String> values) {
        return values.stream().map(this::resolve).toList();
    }


    private String resolveSingleToken(String token) {

        // static
        String staticValue = STATIC_TOKENS.get(token);
        if (staticValue != null) {
            return staticValue;
        }

        // dynamic
        Function<SharedStepsContext, String> resolver = DYNAMIC_TOKENS.get(token);
        if (resolver == null) {
            throw new IllegalArgumentException("Token sconosciuto: " + token);
        }

        return Objects.requireNonNull(
                resolver.apply(sharedContext),
                "Valore nullo per token: " + token
        );
    }

    private boolean isToken(String value) {
        return value != null && value.startsWith(":");
    }
}
