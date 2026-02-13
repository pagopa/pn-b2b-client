package it.pagopa.pn.interop.cucumber.steps.m2m.purpose_template.assistant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplate;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplateDraftUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.TargetTenantKind;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.purpose_template.mapper.PurposeTemplateMapper;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@ToString
@EqualsAndHashCode(callSuper = true)
@Component
@ScenarioScope
public class PurposeTemplatePatchOperationsAssistant extends
    PurposeTemplateGenericPatchOperationsAssistant<PurposeTemplateDraftUpdateSeed> {
    public PurposeTemplatePatchOperationsAssistant(
        PurposeTemplateMapper resourceMapper,
        SharedStepsContext sharedStepsContext,
        ClientTokenConfigurator tokenConfigurator,
        PurposeTemplatePatchContext patchContext
    ) {
        super(resourceMapper, sharedStepsContext, tokenConfigurator.getM2mPurposeTemplateClient(), patchContext, tokenConfigurator);
    }

    @Override
    public PurposeTemplateDraftUpdateSeed buildDefaultPatchRequest() {
        String reqId = RandomStringUtils.insecure().nextAlphanumeric(5);
        return new PurposeTemplateDraftUpdateSeed()
            .targetDescription("some patched targetDescription - " + reqId)
            .targetTenantKind(TargetTenantKind.PA)
            .purposeTitle("some patched purposeTitle - " + reqId)
            .purposeDescription("some patched purposeDescription - " + reqId)
            .purposeIsFreeOfCharge(true)
            .purposeFreeOfChargeReason("some patched purposeFreeOfChargeReason - " + reqId)
            .purposeDailyCalls(87)
            .handlesPersonalData(true);
    }

    @Override
    protected PurposeTemplate patchResource(UUID uuid, PurposeTemplateDraftUpdateSeed patchRequest) {
        return this.client.patchPurposeTemplate(uuid, patchRequest);
    }

    @Override
    protected void assertImpl(PurposeTemplate actual, PurposeTemplate expected, String assertDescription) {
        if(ObjectUtils.anyNull(actual, expected)) {
            super.assertImpl(actual, expected, assertDescription);
        } else {
            OffsetDateTime actualUpdatedAt = OffsetDateTime.parse(actual.getUpdatedAt());
            OffsetDateTime expectedUpdatedAt = context.getUpdatedAt();

            actual.setUpdatedAt(null);
            expected.setUpdatedAt(null);

            assertSoftly(softly -> {
                softly.assertThat(actual)
                    .as(assertDescription)
                    .isEqualTo(expected);
                softly.assertThat(actualUpdatedAt)
                    .as(assertDescription + " - Verifica coerenza campo updatedAt")
                    .isCloseTo(expectedUpdatedAt, within(30, SECONDS));
            });
        }
    }
}