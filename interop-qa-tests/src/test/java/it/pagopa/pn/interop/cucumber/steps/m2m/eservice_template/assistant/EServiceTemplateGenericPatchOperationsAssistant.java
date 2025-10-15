package it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template.assistant;

import it.pagopa.interop.e_service_template.IM2MEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplate;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.PatchOperationsAssistant;
import it.pagopa.pn.interop.cucumber.steps.m2m.ResourceMapper;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public abstract class EServiceTemplateGenericPatchOperationsAssistant<PATCH_REQUEST> extends PatchOperationsAssistant<PATCH_REQUEST, EServiceTemplate, UUID> {
    protected final EServiceTemplateStepContext context;
    protected final IM2MEServiceTemplateClient client;

    public EServiceTemplateGenericPatchOperationsAssistant(
        ResourceMapper<PATCH_REQUEST, EServiceTemplate> resourceMapper,
        SharedStepsContext sharedStepsContext,
        IM2MEServiceTemplateClient client,
        EServiceTemplatePatchContext patchContext,
        ClientTokenConfigurator tokenConfigurator
    ) {
        super(
            resourceMapper,
            sharedStepsContext.getHttpCallExecutor(),
            sharedStepsContext.getDelayService(),
            patchContext,
            tokenConfigurator,
            "e-service template");
        this.context = sharedStepsContext.getEServiceTemplateStepContext();
        this.client = client;
    }

    @Override
    protected UUID getResourceId() {
        return this.context.getLastTemplateManaged().id();
    }

    @Override
    protected EServiceTemplate getResource(UUID uuid) {
        return client.getEserviceTemplate(uuid);
    }

    @Override
    protected UUID randomResourceId() {
        return UUID.randomUUID();
    }
}