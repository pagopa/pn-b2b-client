package it.pagopa.pn.interop.cucumber.steps.m2m.eservice.assistant;

import it.pagopa.interop.eservice.service.IM2MEserviceClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.PatchOperationsAssistant;
import it.pagopa.pn.interop.cucumber.steps.m2m.ResourceMapper;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public abstract class EServiceGenericPatchOperationsAssistant<PATCH_REQUEST> extends PatchOperationsAssistant<PATCH_REQUEST, EService, UUID> {
    protected final EServicesCommonContext context;
    protected final IM2MEserviceClient client;

    public EServiceGenericPatchOperationsAssistant(
        ResourceMapper<PATCH_REQUEST, EService> resourceMapper,
        SharedStepsContext sharedStepsContext,
        IM2MEserviceClient client,
        EServicePatchContext patchContext
    ) {
        super(
            resourceMapper,
            sharedStepsContext.getHttpCallExecutor(),
            sharedStepsContext.getDelayService(),
            patchContext,
            "e-service");
        this.context = sharedStepsContext.getEServicesCommonContext();
        this.client = client;
    }

    @Override
    protected UUID getResourceId() {
        return this.context.getEserviceId();
    }

    @Override
    protected EService getResource(UUID uuid) {
        return client.get(uuid);
    }

    @Override
    protected UUID randomResourceId() {
        return UUID.randomUUID();
    }
}