package it.pagopa.pn.interop.cucumber.steps.m2m.eservice.assistant;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.eservice.service.IM2MEserviceClient.EServiceNamePatchRequest;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.mapper.EServiceNameMapper;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.stereotype.Component;

@ToString
@EqualsAndHashCode(callSuper = true)
@Component
@ScenarioScope
public class EServiceNamePatchOperationsAssistant extends EServiceGenericPatchOperationsAssistant<EServiceNamePatchRequest> {
    public EServiceNamePatchOperationsAssistant(
        EServiceNameMapper resourceMapper,
        SharedStepsContext sharedStepsContext,
        ClientTokenConfigurator tokenConfigurator,
        EServicePatchContext patchContext
    ) {
        super(resourceMapper, sharedStepsContext, tokenConfigurator.getM2meServiceClient(), patchContext);
    }

    // TODO 13/08/2025 destinato a essere modificato e ampliato non appena la specifica
    //  OpenAPI dell'API in oggetto sarà rilasciata
    @Override
    protected EServiceNamePatchRequest buildDefaultPatchRequest() {
        return EServiceNamePatchRequest.builder()
            .name("patched name-" + UUID.randomUUID())
            .build();
    }

    @Override
    protected void patchResource(UUID uuid, EServiceNamePatchRequest patchRequest) {
        this.client.patchEServiceName(uuid, patchRequest);
    }

}