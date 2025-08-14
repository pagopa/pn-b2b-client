package it.pagopa.pn.interop.cucumber.steps.m2m.purpose.assistant;

import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purpose;
import it.pagopa.interop.purpose.service.IM2MPurposeClient;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.PurposeCommonContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.PatchOperationsAssistant;
import it.pagopa.pn.interop.cucumber.steps.m2m.ResourceMapper;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public abstract class PurposeGenericPatchOperationsAssistant<PATCH_REQUEST> extends PatchOperationsAssistant<PATCH_REQUEST, Purpose, UUID> {
    protected final PurposeCommonContext context;
    protected final IM2MPurposeClient client;

    public PurposeGenericPatchOperationsAssistant(
        ResourceMapper<PATCH_REQUEST, Purpose> resourceMapper,
        SharedStepsContext sharedStepsContext,
        IM2MPurposeClient client,
        PurposePatchContext patchContext
    ) {
        super(
            resourceMapper,
            sharedStepsContext.getHttpCallExecutor(),
            sharedStepsContext.getDelayService(),
            patchContext,
            "e-service descriptor");
        this.context = sharedStepsContext.getPurposeCommonContext();
        this.client = client;
    }

    @Override
    protected UUID getResourceId() {
        return this.context.getPurposeIdAsUUID();
    }

    @Override
    protected Purpose getResource(UUID uuid) {
        return client.getPurpose(uuid);
    }

    @Override
    protected UUID randomResourceId() {
        return UUID.randomUUID();
    }
}