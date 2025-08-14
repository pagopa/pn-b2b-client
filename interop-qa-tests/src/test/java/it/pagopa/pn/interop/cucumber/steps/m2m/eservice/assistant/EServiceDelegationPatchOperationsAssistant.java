package it.pagopa.pn.interop.cucumber.steps.m2m.eservice.assistant;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.eservice.service.IM2MEserviceClient.EServiceDelegationPatchRequest;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.mapper.EServiceDelegationMapper;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.stereotype.Component;

@ToString
@EqualsAndHashCode(callSuper = true)
@Component
@ScenarioScope
public class EServiceDelegationPatchOperationsAssistant extends EServiceGenericPatchOperationsAssistant<EServiceDelegationPatchRequest> {
    public EServiceDelegationPatchOperationsAssistant(
        EServiceDelegationMapper resourceMapper,
        SharedStepsContext sharedStepsContext,
        ClientTokenConfigurator tokenConfigurator,
        EServicePatchContext patchContext
    ) {
        super(resourceMapper, sharedStepsContext, tokenConfigurator.getM2meServiceClient(), patchContext);
    }

    // TODO 13/08/2025 destinato a essere modificato e ampliato non appena la specifica
    //  OpenAPI dell'API in oggetto sarà rilasciata
    @Override
    protected EServiceDelegationPatchRequest buildDefaultPatchRequest() {
        return EServiceDelegationPatchRequest.builder()
            .isConsumerDelegable(true)
            .isClientAccessDelegable(true)
            .build();
    }

    @Override
    protected void patchResource(UUID uuid, EServiceDelegationPatchRequest patchRequest) {
        this.client.patchEServiceDelegation(uuid, patchRequest);
    }

}