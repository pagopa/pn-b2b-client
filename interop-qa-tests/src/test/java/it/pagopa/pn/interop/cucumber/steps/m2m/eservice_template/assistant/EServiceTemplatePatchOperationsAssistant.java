package it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template.assistant;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.e_service_template.IM2MEServiceTemplateClient.EServiceTemplatePatchRequest;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplate;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template.mapper.EServiceTemplateMapper;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.stereotype.Component;

@ToString
@EqualsAndHashCode(callSuper = true)
@Component
@ScenarioScope
public class EServiceTemplatePatchOperationsAssistant extends
    EServiceTemplateGenericPatchOperationsAssistant<EServiceTemplatePatchRequest> {
    public EServiceTemplatePatchOperationsAssistant(
        EServiceTemplateMapper resourceMapper,
        SharedStepsContext sharedStepsContext,
        ClientTokenConfigurator tokenConfigurator,
        EServiceTemplatePatchContext patchContext
    ) {
        super(resourceMapper, sharedStepsContext, tokenConfigurator.getM2mEServiceTemplateClient(), patchContext, tokenConfigurator);
    }

    @Override
    public EServiceTemplatePatchRequest buildDefaultPatchRequest() {
        UUID uuid = UUID.randomUUID();
        return EServiceTemplatePatchRequest.builder()
            .name("some patched name - " + uuid)
            .description("some patched description - " + uuid)
            .technology(EServiceTechnology.SOAP)
            .mode(EServiceMode.DELIVER)
            .intendedTarget("some patched intended target - " + uuid)
            .isSignalHubEnabled(true)
            .build();
    }

    @Override
    protected EServiceTemplate patchResource(UUID uuid, EServiceTemplatePatchRequest eServicePatchRequest) {
        return this.client.patchEServiceTemplate(uuid, eServicePatchRequest);
    }
}