package it.pagopa.pn.interop.cucumber.steps.purposetemplate.utils;

import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplateState;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.TargetTenantKind;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.utils.AbstractResolver;
import it.pagopa.pn.interop.cucumber.steps.purposetemplate.model.PurposeTemplateContext;
import it.pagopa.pn.interop.cucumber.utility.StepParser;
import it.pagopa.pn.interop.cucumber.utility.enums.ResolvableToken;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static it.pagopa.pn.interop.cucumber.utility.StepParser.intOrRandomOrNull;

@RequiredArgsConstructor
public class PurposeTemplateResolver extends AbstractResolver {

    private final SharedStepsContext sharedStepsContext;
    private final PurposeTemplateContext purposeTemplateContext;
    private final IdentityService identityService;

    public Integer resolveOffset(String raw) {
        ResolvableToken token = ResolvableToken.from(raw);
        if (token == null) return intOrRandomOrNull(raw);

        return resolveOrParse(
                raw,
                StepParser::nullableInteger,
                purposeTemplateContext::getActualOffset,
                PurposeTemplateResolver::randomNonNegativeInt,
                null,
                purposeTemplateContext::getActualOffset
        );
    }

    public Integer resolveLimit(String raw) {
        ResolvableToken token = ResolvableToken.from(raw);
        if (token == null) return intOrRandomOrNull(raw);

        return resolveOrParse(
                raw,
                StepParser::nullableInteger,
                purposeTemplateContext::getActualLimit,
                PurposeTemplateResolver::randomPositiveInt,
                null,
                purposeTemplateContext::getActualLimit
        );
    }

    private static int randomPositiveInt() {
        return 1 + (int) (Math.random() * Integer.MAX_VALUE);
    }

    private static int randomNonNegativeInt() {
        return (int) (Math.random() * Integer.MAX_VALUE);
    }

    public String resolvePurposeTitle(String raw) {
        ResolvableToken token = ResolvableToken.from(raw);
        if (token == null) return raw; // stringa “normale”

        return resolveOrParse(
                raw,
                StepParser::nullOrValue,
                purposeTemplateContext::getActualPurposeTitle,
                () -> "PT-" + UUID.randomUUID(),
                () -> "",
                purposeTemplateContext::getActualPurposeTitle
        );
    }

    public Boolean resolveHandlesPersonalData(String raw) {
        ResolvableToken token = ResolvableToken.from(raw);
        if (token == null) {
            if (raw == null) return null;
            String v = raw.trim().toLowerCase();
            if (v.equals("true")) return true;
            if (v.equals("false")) return false;
            // se arriva un valore “sporco”, lascia che fallisca più avanti (400 atteso)
            return null;
        }

        return resolveOrParse(
                raw,
                StepParser::nullableBoolean,
                purposeTemplateContext::getActualHandlesPersonalData,
                () -> Math.random() < 0.5,
                null,
                purposeTemplateContext::getActualHandlesPersonalData
        );
    }

    public TargetTenantKind resolveTargetTenantKind(String raw) {
        ResolvableToken token = ResolvableToken.from(raw);
        if (token == null) {
            if (raw == null) return null;
            try {
                return TargetTenantKind.valueOf(raw.trim().toUpperCase());
            } catch (Exception e) {
                return null; // input invalido -> nel test ti aspetti 400
            }
        }

        return resolveOrParse(
                raw,
                TargetTenantKind::valueOf,
                () -> TargetTenantKind.valueOf(sharedStepsContext.getTenantType()),
                PurposeTemplateResolver::randomTargetTenantKind,
                null,
                () -> TargetTenantKind.valueOf(sharedStepsContext.getTenantType())
        );
    }

    private static TargetTenantKind randomTargetTenantKind() {
        TargetTenantKind[] values = TargetTenantKind.values();
        return values[(int) (Math.random() * values.length)];
    }

    public List<UUID> resolveCreatorIds(String raw) {
        ResolvableToken token = ResolvableToken.from(raw);
        if (token == null) return parseUuidList(raw);
        UUID organizationId = identityService.getOrganizationId(sharedStepsContext.getTenantType());

        return resolveOrParse(
                raw,
                v -> Collections.singletonList(UUID.fromString(v)),
                () -> organizationId == null ? null : Collections.singletonList(organizationId), // ACTUAL
                () -> Collections.singletonList(UUID.randomUUID()),                               // EXPECTED (se davvero lo vuoi così)
                Collections::emptyList,                                                           // RANDOM
                () -> organizationId == null ? null : Collections.singletonList(organizationId)  // BLANK  ✅ qui era l’errore
        );

    }

    public List<UUID> resolveEserviceIds(String raw) {
        ResolvableToken token = ResolvableToken.from(raw);
        if (token == null) return parseUuidList(raw);

        UUID eserviceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();

        return resolveOrParse(
                raw,
                v -> Collections.singletonList(UUID.fromString(v)),
                () -> eserviceId == null ? Collections.emptyList() : List.of(eserviceId),
                () -> Collections.singletonList(UUID.randomUUID()),
                Collections::emptyList,
                () -> eserviceId == null ? Collections.emptyList() : List.of(eserviceId)
        );
    }

    public List<PurposeTemplateState> resolveStates(String raw) {
        ResolvableToken token = ResolvableToken.from(raw);
        if (token == null) return parseEnumList(raw, PurposeTemplateState.class);

        PurposeTemplateState actualState = purposeTemplateContext.getActualState();

        return resolveOrParse(
                raw,
                (v) -> Collections.emptyList(),
                () -> actualState == null ? null : Collections.singletonList(actualState),
                () -> Collections.singletonList(randomPurposeTemplateState()),
                Collections::emptyList,
                () -> actualState == null ? null : Collections.singletonList(actualState)
        );
    }

    private static PurposeTemplateState randomPurposeTemplateState() {
        PurposeTemplateState[] values = PurposeTemplateState.values();
        return values[(int) (Math.random() * values.length)];
    }

    private List<UUID> parseUuidList(String raw) {
        if (raw == null) return null;
        String v = raw.trim();
        if (v.isEmpty()) return Collections.emptyList();

        // supporta CSV: "uuid1,uuid2"
        return Arrays.stream(v.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                // riusa parser di progetto: gestisce anche "RANDOM"/"NULL" ecc.
                .map(StepParser::uuidOrRandomOrNull)
                .collect(Collectors.toList());
    }

    private <E extends Enum<E>> List<E> parseEnumList(String raw, Class<E> enumClass) {
        if (raw == null) return null;
        String v = raw.trim();
        if (v.isEmpty()) return Collections.emptyList();

        return Arrays.stream(v.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    try {
                        return Enum.valueOf(enumClass, s.toUpperCase());
                    } catch (Exception e) {
                        return null; // invalido -> 400
                    }
                })
                .collect(Collectors.toList());
    }
}
