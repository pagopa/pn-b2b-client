package it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template.version.assistant;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.e_service_template.IM2MEServiceTemplateClient.EServiceTemplateVersionQuotasPatchRequest;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersion;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template.version.mapper.EServiceTemplateVersionQuotasMapper;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.stereotype.Component;

@ToString
@EqualsAndHashCode(callSuper = true)
@Component
@ScenarioScope
public class EServiceTemplateVersionQuotasPatchOperationsAssistant extends
    EServiceTemplateVersionGenericPatchOperationsAssistant<EServiceTemplateVersionQuotasPatchRequest> {
    public EServiceTemplateVersionQuotasPatchOperationsAssistant(
        EServiceTemplateVersionQuotasMapper resourceMapper,
        SharedStepsContext sharedStepsContext,
        ClientTokenConfigurator tokenConfigurator,
        EServiceTemplateVersionPatchContext patchContext
    ) {
        super(resourceMapper, sharedStepsContext, tokenConfigurator.getM2mEServiceTemplateClient(), patchContext, tokenConfigurator);
    }

    @Override
    public EServiceTemplateVersionQuotasPatchRequest buildDefaultPatchRequest() {
        return EServiceTemplateVersionQuotasPatchRequest.builder()
            .dailyCallsTotal(40)
            .dailyCallsPerConsumer(4)
            .build();
    }

    @Override
    protected EServiceTemplateVersion patchResource(EServiceTemplateVersionId id, EServiceTemplateVersionQuotasPatchRequest eServicePatchRequest) {
        return this.client.patchEServiceTemplateVersionQuotas(id.getTemplateId(), id.getVersionId(), eServicePatchRequest);
    }
}