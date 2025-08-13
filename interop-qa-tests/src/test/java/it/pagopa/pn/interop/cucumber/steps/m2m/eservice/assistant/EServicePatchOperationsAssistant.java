package it.pagopa.pn.interop.cucumber.steps.m2m.eservice.assistant;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.eservice.service.IM2MEserviceClient.EServicePatchRequest;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.EServiceMapper;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class EServicePatchOperationsAssistant extends EServiceGenericPatchOperationsAssistant<EServicePatchRequest> {
    public EServicePatchOperationsAssistant(
        EServiceMapper resourceMapper,
        SharedStepsContext sharedStepsContext,
        ClientTokenConfigurator tokenConfigurator
    ) {
        super(resourceMapper, sharedStepsContext, tokenConfigurator.getM2meServiceClient());
    }

    // TODO 05/08/2025 destinato a essere modificato e ampliato non appena la specifica
    //  OpenAPI dell'API in oggetto sarà rilasciata
    @Override
    protected EServicePatchRequest buildPatchRequest() {
        return new EServicePatchRequest()
            .description("patched description")
            .name("patched name");
    }

    @Override
    protected void patchResource(UUID uuid, EServicePatchRequest eServicePatchRequest) {
        this.client.patchEService(uuid, eServicePatchRequest);
    }

}