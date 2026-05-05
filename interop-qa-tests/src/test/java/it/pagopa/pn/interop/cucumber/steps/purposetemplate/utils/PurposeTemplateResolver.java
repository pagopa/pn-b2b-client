package it.pagopa.pn.interop.cucumber.steps.purposetemplate.utils;

import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplateState;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.TargetTenantKind;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.utils.AbstractResolver;
import it.pagopa.pn.interop.cucumber.steps.purposetemplate.model.PurposeTemplateContext;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;


public class PurposeTemplateResolver extends AbstractResolver {

    private final SharedStepsContext sharedStepsContext;
    private final PurposeTemplateContext purposeTemplateContext;
    private final IdentityService identityService;

    public PurposeTemplateResolver(SharedStepsContext sharedStepsContext, PurposeTemplateContext purposeTemplateContext, IdentityService identityService) {
        super(sharedStepsContext);

        this.sharedStepsContext = sharedStepsContext;
        this.purposeTemplateContext = purposeTemplateContext;
        this.identityService = identityService;
    }

    public Integer resolveOffset(String raw) {
        return resolveOrParse(
                raw,
                Integer::valueOf,
                purposeTemplateContext::getActualOffset,
                purposeTemplateContext::getActualOffset,
                PurposeTemplateResolver::randomNonNegativeInt,
                () -> null
        );
    }

    public Integer resolveLimit(String raw) {
        return resolveOrParse(
                raw,
                Integer::valueOf,
                purposeTemplateContext::getActualLimit,
                purposeTemplateContext::getActualLimit,
                PurposeTemplateResolver::randomPositiveInt,
                () -> null
        );
    }

    private static int randomPositiveInt() {
        return 1 + (int) (Math.random() * Integer.MAX_VALUE);
    }

    private static int randomNonNegativeInt() {
        return (int) (Math.random() * Integer.MAX_VALUE);
    }

    public String resolvePurposeTitle(String raw) {
        return resolveOrParse(
                raw,
                String::valueOf,
                purposeTemplateContext::getActualPurposeTitle,
                purposeTemplateContext::getActualPurposeTitle,
                () -> "PT-" + UUID.randomUUID(),

                //() -> "" <-- vecchio comportamento, sconfessato da https://pagopaspa.slack.com/archives/C094Z08MXS6/p1776694759386899
                purposeTemplateContext::getActualPurposeTitle
        );
    }

    public Boolean resolveHandlesPersonalData(String raw) {
        return resolveOrParse(
                raw,
                Boolean::valueOf,
                purposeTemplateContext::getActualHandlesPersonalData,
                purposeTemplateContext::getActualHandlesPersonalData,
                () -> Math.random() < 0.5,
                null
        );
    }

    public TargetTenantKind resolveTargetTenantKind(String raw) {
        Supplier<TargetTenantKind> tks = () -> sharedStepsContext.getTenantType().equalsIgnoreCase("Privato")
                ? TargetTenantKind.PRIVATE
                : TargetTenantKind.PA;
        return resolveOrParse(
                raw,
                TargetTenantKind::valueOf,
                tks,
                tks,
                PurposeTemplateResolver::randomTargetTenantKind,
                null
        );
    }

    private static TargetTenantKind randomTargetTenantKind() {
        TargetTenantKind[] values = TargetTenantKind.values();
        return values[(int) (Math.random() * values.length)];
    }

    public List<UUID> resolveCreatorIds(String raw) {
        UUID organizationId = identityService.getOrganizationId(sharedStepsContext.getTenantType());

        return resolveOrParse(
                raw,
                (uuid) -> Collections.singletonList(UUID.fromString(uuid)),
                () -> organizationId == null
                        ? null
                        : Collections.singletonList(organizationId),
                () -> organizationId == null
                        ? null
                        : Collections.singletonList(organizationId),
                () -> Collections.singletonList(UUID.randomUUID()),
                Collections::emptyList
        );
    }

    public List<PurposeTemplateState> resolveStates(String raw) {
        List<PurposeTemplateState> singletonState = purposeTemplateContext.getActualState() == null
                ? null
                : Collections.singletonList(purposeTemplateContext.getActualState());

        return resolveOrParse(
                raw,
                (state) -> Collections.singletonList(PurposeTemplateState.valueOf(state)),
                () -> singletonState,
                () -> singletonState,
                () -> Collections.singletonList(randomPurposeTemplateState()),
                Collections::emptyList
        );
    }

    private static PurposeTemplateState randomPurposeTemplateState() {
        PurposeTemplateState[] values = PurposeTemplateState.values();
        return values[(int) (Math.random() * values.length)];
    }
}
