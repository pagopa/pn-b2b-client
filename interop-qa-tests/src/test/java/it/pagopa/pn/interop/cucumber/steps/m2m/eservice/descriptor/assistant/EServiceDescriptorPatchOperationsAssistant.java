package it.pagopa.pn.interop.cucumber.steps.m2m.eservice.descriptor.assistant;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.eservice.service.IM2MEserviceDescriptorClient.EServiceDescriptorPatchRequest;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementApprovalPolicy;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.descriptor.mapper.EServiceDescriptorMapper;
import java.util.List;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

@ToString
@EqualsAndHashCode(callSuper = true)
@Component
@ScenarioScope
public class EServiceDescriptorPatchOperationsAssistant extends
    EServiceDescriptorGenericPatchOperationsAssistant<EServiceDescriptorPatchRequest> {
    public EServiceDescriptorPatchOperationsAssistant(
        EServiceDescriptorMapper resourceMapper,
        SharedStepsContext sharedStepsContext,
        ClientTokenConfigurator tokenConfigurator,
        EServiceDescriptorPatchContext patchContext
    ) {
        super(resourceMapper, sharedStepsContext, tokenConfigurator.getM2mEServiceDescriptorClient(), patchContext, tokenConfigurator);
    }

    @Override
    public EServiceDescriptorPatchRequest buildDefaultPatchRequest() {
        return EServiceDescriptorPatchRequest.builder()
            .dailyCallsTotal(10)
            .dailyCallsPerConsumer(5)
            .voucherLifespan(1000)
            .description("patched description")
            .audience(List.of("patched audience"))
            .agreementApprovalPolicy(AgreementApprovalPolicy.MANUAL)
            .build();
    }

    @Override
    protected EServiceDescriptor patchResource(Pair<UUID, UUID> uuid, EServiceDescriptorPatchRequest eServicePatchRequest) {
        return this.client.patchEServiceDescriptor(uuid.getLeft(), uuid.getRight(), eServicePatchRequest);
    }
}