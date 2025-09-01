package it.pagopa.pn.interop.cucumber.steps.m2m.eservice.assistant.descriptor;

import it.pagopa.interop.eservice.service.IM2MEserviceDescriptorClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.PatchOperationsAssistant;
import it.pagopa.pn.interop.cucumber.steps.m2m.ResourceMapper;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.tuple.Pair;

@ToString
@EqualsAndHashCode(callSuper = true)
public abstract class EServiceDescriptorGenericPatchOperationsAssistant<PATCH_REQUEST> extends PatchOperationsAssistant<PATCH_REQUEST, EServiceDescriptor, Pair<UUID, UUID>> {
    protected final EServicesCommonContext context;
    protected final IM2MEserviceDescriptorClient client;

    public EServiceDescriptorGenericPatchOperationsAssistant(
        ResourceMapper<PATCH_REQUEST, EServiceDescriptor> resourceMapper,
        SharedStepsContext sharedStepsContext,
        IM2MEserviceDescriptorClient client,
        EServiceDescriptorPatchContext patchContext,
        ClientTokenConfigurator tokenConfigurator
    ) {
        super(
            resourceMapper,
            sharedStepsContext.getHttpCallExecutor(),
            sharedStepsContext.getDelayService(),
            patchContext,
            tokenConfigurator,
            "e-service descriptor");
        this.context = sharedStepsContext.getEServicesCommonContext();
        this.client = client;
    }

    @Override
    protected Pair<UUID, UUID> getResourceId() {
        return Pair.of(this.context.getEserviceId(), this.context.getDescriptorId());
    }

    @Override
    protected EServiceDescriptor getResource(Pair<UUID, UUID> uuid) {
        return client.getCompleteResource(uuid.getLeft(), uuid.getRight());
    }

    @Override
    protected Pair<UUID, UUID> randomResourceId() {
        return Pair.of(UUID.randomUUID(), UUID.randomUUID());
    }
}