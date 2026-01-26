package it.pagopa.pn.interop.cucumber.steps.m2m.purpose_template.assistant;

import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplate;
import it.pagopa.interop.purpose.service.IM2MPurposeTemplateClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.PurposeTemplateCommonContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.PatchOperationsAssistant;
import it.pagopa.pn.interop.cucumber.steps.m2m.ResourceMapper;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public abstract class PurposeTemplateGenericPatchOperationsAssistant<PATCH_REQUEST> extends PatchOperationsAssistant<PATCH_REQUEST, PurposeTemplate, UUID> {
    protected final PurposeTemplateCommonContext context;
    protected final IM2MPurposeTemplateClient client;

    public PurposeTemplateGenericPatchOperationsAssistant(
        ResourceMapper<PATCH_REQUEST, PurposeTemplate> resourceMapper,
        SharedStepsContext sharedStepsContext,
        IM2MPurposeTemplateClient client,
        PurposeTemplatePatchContext patchContext,
        ClientTokenConfigurator tokenConfigurator
    ) {
        super(
            resourceMapper,
            sharedStepsContext.getHttpCallExecutor(),
            sharedStepsContext.getDelayService(),
            patchContext,
            tokenConfigurator,
            "purpose template");
        this.client = client;
        this.context = sharedStepsContext.getPurposeTemplateContext();
    }

    @Override
    protected UUID getResourceId() {
        return this.context.getPurposeTemplateId();
    }

    @Override
    protected PurposeTemplate getResource(UUID uuid) {
        return client.getPurposeTemplate(uuid);
    }

    @Override
    protected UUID randomResourceId() {
        return UUID.randomUUID();
    }
}