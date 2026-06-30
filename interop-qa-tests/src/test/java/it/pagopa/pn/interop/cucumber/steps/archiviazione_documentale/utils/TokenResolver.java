package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils;

import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.utility.enums.ResolvableToken;
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
                    Map.entry(":clientId", ctx -> safe(c -> c.getClientCommonContext().getLastClient(), ctx)),
                    Map.entry(":userId", ctx -> safe(c -> c.getIdentityService().getUserId(c.getTenantType(), c.getRole().getValue()), ctx)),
                    Map.entry(":kid", ctx -> safe(c -> c.getClientCommonContext().getKeyId(), ctx)),
                    Map.entry(":agreementId", ctx -> safe(c -> c.getAgreementCommonContext().getAgreementId(), ctx)),
                    Map.entry(":consumerDelegationId", ctx -> safe(c -> c.getDelegationCommonContext().getDelegationId(), ctx)),
                    Map.entry(":producerDelegationId", ctx -> safe(c -> c.getDelegationCommonContext().getDelegationId(), ctx)),
                    Map.entry(":purposeId", ctx -> safe(c -> c.getPurposeCommonContext().getLastPurposeId(), ctx)),
                    Map.entry(":purposeVersionId", ctx -> safe(c -> c.getPurposeCommonContext().getCurrentVersionId(), ctx)),
                    Map.entry(":riskAnalysisId", ctx -> safe(c -> c.getRiskAnalysisCommonContext().getRiskAnalysisId(), ctx)),
                    Map.entry(":riskAnalysisDailyCalls", ctx -> safe(c -> c.getRiskAnalysisCommonContext().getDailyCalls(), ctx)),
                    Map.entry(":eServiceName", ctx -> safe(c -> c.getEServicesCommonContext().getName(), ctx)),
                    Map.entry(":eserviceId", ctx -> safe(c -> c.getEServicesCommonContext().getEserviceId(), ctx)),
                    Map.entry(":purposeTemplateId", ctx -> safe(c -> c.getPurposeTemplateContext().getPurposeTemplateId(), ctx)),
                    Map.entry(":descriptorId", ctx -> safe(c -> c.getEServicesCommonContext().getDescriptorId(), ctx)),
                    Map.entry(":eserviceTemplateId", ctx -> safe(c -> c.getEServiceTemplateStepContext().getLastTemplateManaged().getId(), ctx)),
                    Map.entry(":eserviceTemplateVersionId", ctx -> safe(c -> c.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId(), ctx))
                    );

    private static String safe(Function<SharedStepsContext, Object> extractor, SharedStepsContext ctx) {
        try {
            Object value = extractor.apply(ctx);
            return value != null ? value.toString() : null;
        } catch (NullPointerException e) {
            return null;
        }
    }


    private final SharedStepsContext sharedContext;

    public String resolve(String value) {
        if (value == null) return null;

        var resolvable = ResolvableToken.from(value);
        if (resolvable != null) {
            return switch (resolvable) {
                case BLANK -> "";
                case NULL -> null;
                default -> throw new IllegalArgumentException("Token non gestito: " + resolvable);
            };
        }

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

        return resolver.apply(sharedContext);
        /*
        return Objects.requireNonNull(
                resolver.apply(sharedContext),
                "Valore nullo per token: " + token
        );
         */
    }

    private boolean isToken(String value) {
        return value != null && value.startsWith(":");
    }
}
