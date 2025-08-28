package it.pagopa.pn.interop.cucumber.steps.m2m.purpose.assistant;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purpose;
import it.pagopa.interop.purpose.service.IM2MPurposeClient;
import it.pagopa.interop.purpose.service.IM2MPurposeClient.PurposePatchRequest;
import it.pagopa.interop.purpose.service.impl.M2MPurposeClientImpl;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.purpose.PurposeMapper;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.stereotype.Component;

@ToString
@EqualsAndHashCode(callSuper = true)
@Component
@ScenarioScope
public class PurposePatchOperationsAssistant extends PurposeGenericPatchOperationsAssistant<PurposePatchRequest> {
    public PurposePatchOperationsAssistant(
        PurposeMapper resourceMapper,
        SharedStepsContext sharedStepsContext,
        ClientTokenConfigurator tokenConfigurator,
        PurposePatchContext patchContext
    ) {
        super(resourceMapper, sharedStepsContext, tokenConfigurator.getM2mPurposeClient(), patchContext);
    }

    // TODO 14/08/2025 destinato a essere modificato e ampliato non appena la specifica
    //  OpenAPI dell'API in oggetto sarà rilasciata
    @Override
    protected PurposePatchRequest buildDefaultPatchRequest() {
        return PurposePatchRequest.builder()
            .title("patched title - " + UUID.randomUUID())
            .description("patched description - " + UUID.randomUUID())
            .build();
    }

    @Override
    protected Purpose patchResource(UUID uuid, PurposePatchRequest eServicePatchRequest) {
        return this.client.patchPurpose(uuid, eServicePatchRequest);
    }

    protected Purpose patchResourceWithNotValidToken(UUID uuid, PurposePatchRequest eServicePatchRequest) {
        String expiredToken = "c29tZQ==.aW52YWxpZA==.dG9rZW4=";
        this.client.setBearerToken(expiredToken);
        return this.client.patchPurpose(uuid, eServicePatchRequest);
    }
}