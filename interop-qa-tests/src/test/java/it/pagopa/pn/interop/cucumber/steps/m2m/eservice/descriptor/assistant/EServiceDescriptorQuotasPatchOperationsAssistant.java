package it.pagopa.pn.interop.cucumber.steps.m2m.eservice.descriptor.assistant;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.eservice.service.IM2MEserviceDescriptorClient.EServiceDescriptorQuotasPatchRequest;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.descriptor.mapper.EServiceDescriptorQuotasMapper;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

@ToString
@EqualsAndHashCode(callSuper = true)
@Component
@ScenarioScope
public class EServiceDescriptorQuotasPatchOperationsAssistant extends
    EServiceDescriptorGenericPatchOperationsAssistant<EServiceDescriptorQuotasPatchRequest> {
    public EServiceDescriptorQuotasPatchOperationsAssistant(
        EServiceDescriptorQuotasMapper resourceMapper,
        SharedStepsContext sharedStepsContext,
        ClientTokenConfigurator tokenConfigurator,
        EServiceDescriptorPatchContext patchContext
    ) {
        super(resourceMapper, sharedStepsContext, tokenConfigurator.getM2mEServiceDescriptorClient(), patchContext, tokenConfigurator);
    }

    @Override
    public EServiceDescriptorQuotasPatchRequest buildDefaultPatchRequest() {
        return EServiceDescriptorQuotasPatchRequest.builder()
            .dailyCallsTotal(10)
            .dailyCallsPerConsumer(5)
            .build();
    }

    @Override
    protected EServiceDescriptor patchResource(Pair<UUID, UUID> uuid, EServiceDescriptorQuotasPatchRequest patchRequest) {
        return this.client.patchEServiceDescriptorQuotas(uuid.getLeft(), uuid.getRight(), patchRequest);
    }
}