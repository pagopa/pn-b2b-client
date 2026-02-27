package it.pagopa.pn.interop.cucumber.steps.purposetemplate.utils;

import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplateState;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.TargetTenantKind;
import it.pagopa.pn.interop.cucumber.enums.ResolvableToken;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.purposetemplate.model.PurposeTemplateContext;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static it.pagopa.pn.interop.cucumber.utility.StepParser.intOrRandomOrNull;
import static it.pagopa.pn.interop.cucumber.utility.StepParser.uuidOrRandomOrNull;

@RequiredArgsConstructor
public class PurposeTemplateResolver {

    private final SharedStepsContext sharedStepsContext;
    private final PurposeTemplateContext purposeTemplateContext;
    private final IdentityService identityService;

    // ========== helper generico token ==========
    private <T> T resolveToken(
            String raw,
            Supplier<T> actualSupplier,
            Supplier<T> randomSupplier,
            Supplier<T> blankSupplier,
            T currentValueForKeep
    ) {
        ResolvableToken token = ResolvableToken.from(raw);
        if (token == null) return null;

        return switch (token) {
            case ACTUAL -> actualSupplier.get();
            case NULL -> null;
            case RANDOM -> randomSupplier.get();
            case KEEP -> currentValueForKeep;
            case BLANK -> blankSupplier == null ? null : blankSupplier.get();
        };
    }

    // ========== offset / limit ==========
    public Integer resolveOffset(String raw) {
        ResolvableToken token = ResolvableToken.from(raw);
        if (token == null) return intOrRandomOrNull(raw);

        return resolveToken(
                raw,
                () -> purposeTemplateContext.getActualOffset(),
                PurposeTemplateResolver::randomNonNegativeInt,
                null,
                purposeTemplateContext.getActualOffset()
        );
    }

    public Integer resolveLimit(String raw) {
        ResolvableToken token = ResolvableToken.from(raw);
        if (token == null) return intOrRandomOrNull(raw);

        return resolveToken(
                raw,
                () -> purposeTemplateContext.getActualLimit(),
                PurposeTemplateResolver::randomPositiveInt,
                null,
                purposeTemplateContext.getActualLimit()
        );
    }

    private static int randomPositiveInt() {
        return 1 + (int) (Math.random() * Integer.MAX_VALUE);
    }

    private static int randomNonNegativeInt() {
        return (int) (Math.random() * Integer.MAX_VALUE);
    }

    // ========== purposeTitle ==========
    public String resolvePurposeTitle(String raw) {
        ResolvableToken token = ResolvableToken.from(raw);
        if (token == null) return raw; // stringa “normale”

        return resolveToken(
                raw,
                () -> purposeTemplateContext.getActualPurposeTitle(),
                () -> "PT-" + UUID.randomUUID(),
                () -> "",
                purposeTemplateContext.getActualPurposeTitle()
        );
    }

    // ========== handlesPersonalData ==========
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

        return resolveToken(
                raw,
                () -> purposeTemplateContext.getActualHandlesPersonalData(),
                () -> Math.random() < 0.5,
                null,
                purposeTemplateContext.getActualHandlesPersonalData()
        );
    }

    // ========== targetTenantKind ==========
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

        return resolveToken(
                raw,
                () -> TargetTenantKind.valueOf(sharedStepsContext.getTenantType()),
                PurposeTemplateResolver::randomTargetTenantKind,
                null,
                TargetTenantKind.valueOf(sharedStepsContext.getTenantType())
        );
    }

    private static TargetTenantKind randomTargetTenantKind() {
        TargetTenantKind[] values = TargetTenantKind.values();
        return values[(int) (Math.random() * values.length)];
    }

    // ========== creatorIds (List<UUID>) ==========
    public List<UUID> resolveCreatorIds(String raw) {
        ResolvableToken token = ResolvableToken.from(raw);
        if (token == null) return parseUuidList(raw);
        UUID organizationId = identityService.getOrganizationId(sharedStepsContext.getTenantType());

        return resolveToken(
                raw,
                () -> organizationId == null
                        ? null
                        : Collections.singletonList(organizationId),
                () -> Collections.singletonList(UUID.randomUUID()),
                Collections::emptyList,
                organizationId == null
                        ? null
                        : Collections.singletonList(organizationId)
        );
    }

    // ========== eserviceIds (List<UUID>) ==========
    public List<UUID> resolveEserviceIds(String raw) {
        ResolvableToken token = ResolvableToken.from(raw);
        if (token == null) return parseUuidList(raw);

        return resolveToken(
                raw,
                () -> sharedStepsContext.getEServicesCommonContext().getEserviceId() == null
                        ? Collections.emptyList()
                        : List.of(sharedStepsContext.getEServicesCommonContext().getEserviceId()),
                () -> Collections.singletonList(UUID.randomUUID()),
                Collections::emptyList,
                sharedStepsContext.getEServicesCommonContext().getEserviceId() == null
                        ? Collections.emptyList()
                        : List.of(sharedStepsContext.getEServicesCommonContext().getEserviceId())
        );
    }

    // ========== states (List<PurposeTemplateState>) ==========
    public List<PurposeTemplateState> resolveStates(String raw) {
        ResolvableToken token = ResolvableToken.from(raw);
        if (token == null) return parseEnumList(raw, PurposeTemplateState.class);

        return resolveToken(
                raw,
                () -> purposeTemplateContext.getActualState() == null
                        ? null
                        : Collections.singletonList(purposeTemplateContext.getActualState()),
                () -> Collections.singletonList(randomPurposeTemplateState()),
                Collections::emptyList,
                purposeTemplateContext.getActualState() == null
                        ? null
                        : Collections.singletonList(purposeTemplateContext.getActualState())
        );
    }

    private static PurposeTemplateState randomPurposeTemplateState() {
        PurposeTemplateState[] values = PurposeTemplateState.values();
        return values[(int) (Math.random() * values.length)];
    }

    // ========== parsing helpers ==========
    private List<UUID> parseUuidList(String raw) {
        if (raw == null) return null;
        String v = raw.trim();
        if (v.isEmpty()) return Collections.emptyList();

        // supporta CSV: "uuid1,uuid2"
        return Arrays.stream(v.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                // riusa parser di progetto: gestisce anche "RANDOM"/"NULL" ecc.
                .map(s -> uuidOrRandomOrNull(s))
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
