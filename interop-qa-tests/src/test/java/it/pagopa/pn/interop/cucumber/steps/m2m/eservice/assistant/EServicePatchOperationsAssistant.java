package it.pagopa.pn.interop.cucumber.steps.m2m.eservice.assistant;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.eservice.service.IM2MEserviceClient.EServicePatchRequest;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EService;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTechnology;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.mapper.EServiceMapper;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.util.UUID;

@ToString
@EqualsAndHashCode(callSuper = true)
@Component
@ScenarioScope
public class EServicePatchOperationsAssistant extends EServiceGenericPatchOperationsAssistant<EServicePatchRequest> {
    public EServicePatchOperationsAssistant(
        EServiceMapper resourceMapper,
        SharedStepsContext sharedStepsContext,
        ClientTokenConfigurator tokenConfigurator,
        EServicePatchContext patchContext
    ) {
        super(resourceMapper, sharedStepsContext, tokenConfigurator.getM2meServiceClient(), patchContext, tokenConfigurator);
    }

    @Override
    public EServicePatchRequest buildDefaultPatchRequest() {
        String id = RandomStringUtils.insecure().nextAlphanumeric(5);
        return EServicePatchRequest.builder()
            .name("some patched name - " + id)
            .description("some patched description - " + id)
            .technology(EServiceTechnology.SOAP)
            .mode(EServiceMode.RECEIVE)
            .isClientAccessDelegable(true)
            .isConsumerDelegable(true)
            .isSignalHubEnabled(true)
            .build();
    }

    @Override
    protected EService patchResource(UUID uuid, EServicePatchRequest eServicePatchRequest) {
        return this.client.patchEService(uuid, eServicePatchRequest);
    }
}