package it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template.version.assistant;

import it.pagopa.interop.e_service_template.IM2MEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersion;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.PatchOperationsAssistant;
import it.pagopa.pn.interop.cucumber.steps.m2m.ResourceMapper;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template.version.assistant.EServiceTemplateVersionGenericPatchOperationsAssistant.EServiceTemplateVersionId;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public abstract class EServiceTemplateVersionGenericPatchOperationsAssistant<PATCH_REQUEST> extends PatchOperationsAssistant<PATCH_REQUEST, EServiceTemplateVersion, EServiceTemplateVersionId> {
    @Data
    @AllArgsConstructor(staticName = "of")
    public static class EServiceTemplateVersionId {
        private UUID templateId;
        private UUID versionId;
    }

    protected final EServiceTemplateStepContext context;
    protected final IM2MEServiceTemplateClient client;

    public EServiceTemplateVersionGenericPatchOperationsAssistant(
        ResourceMapper<PATCH_REQUEST, EServiceTemplateVersion> resourceMapper,
        SharedStepsContext sharedStepsContext,
        IM2MEServiceTemplateClient client,
        EServiceTemplateVersionPatchContext patchContext,
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
    protected EServiceTemplateVersionId getResourceId() {
        UUID templateId = this.context.getLastTemplateManaged().id();
        UUID versionId = this.context.getLastTemplateManaged().lastVersionId();
        return EServiceTemplateVersionId.of(templateId, versionId);
    }

    @Override
    protected EServiceTemplateVersion getResource(EServiceTemplateVersionId id) {
        return client.getEserviceTemplateVersion(id.getTemplateId(), id.getVersionId());
    }

    @Override
    protected EServiceTemplateVersionId randomResourceId() {
        return EServiceTemplateVersionId.of(UUID.randomUUID(), UUID.randomUUID());
    }
}