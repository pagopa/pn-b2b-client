package it.pagopa.pn.interop.cucumber.steps.m2m.purpose_template.assistant;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplate;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplateDraftUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.TargetTenantKind;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.purpose_template.mapper.PurposeTemplateMapper;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
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
        UUID uuid = UUID.randomUUID();
        return new PurposeTemplateDraftUpdateSeed()
            .targetDescription("some patched targetDescription - " + uuid)
            .targetTenantKind(TargetTenantKind.PA)
            .purposeTitle("some patched purposeTitle - " + uuid)
            .purposeDescription("some patched purposeDescription - " + uuid)
            .purposeIsFreeOfCharge(true)
            .purposeFreeOfChargeReason("some patched purposeFreeOfChargeReason - " + uuid)
            .purposeDailyCalls(87)
            .handlesPersonalData(true);
    }

    @Override
    protected PurposeTemplate patchResource(UUID uuid, PurposeTemplateDraftUpdateSeed patchRequest) {
        return this.client.patchPurposeTemplate(uuid, patchRequest);
    }
}