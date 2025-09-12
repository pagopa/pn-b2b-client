package it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template.version.assistant;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.e_service_template.IM2MEServiceTemplateClient.EServiceTemplateVersionPatchRequest;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementApprovalPolicy;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersion;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template.version.mapper.EServiceTemplateVersionMapper;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.stereotype.Component;

@ToString
@EqualsAndHashCode(callSuper = true)
@Component
@ScenarioScope
public class EServiceTemplateVersionPatchOperationsAssistant extends
    EServiceTemplateVersionGenericPatchOperationsAssistant<EServiceTemplateVersionPatchRequest> {
    public EServiceTemplateVersionPatchOperationsAssistant(
        EServiceTemplateVersionMapper resourceMapper,
        SharedStepsContext sharedStepsContext,
        ClientTokenConfigurator tokenConfigurator,
        EServiceTemplateVersionPatchContext patchContext
    ) {
        super(resourceMapper, sharedStepsContext, tokenConfigurator.getM2mEServiceTemplateClient(), patchContext, tokenConfigurator);
    }

    @Override
    public EServiceTemplateVersionPatchRequest buildDefaultPatchRequest() {
        UUID uuid = UUID.randomUUID();
        return EServiceTemplateVersionPatchRequest.builder()
            .description("some patched description - " + uuid)
            .voucherLifespan(100000)
            .agreementApprovalPolicy(AgreementApprovalPolicy.MANUAL)
            .dailyCallsTotal(40)
            .dailyCallsPerConsumer(4)
            .build();
    }

    @Override
    protected EServiceTemplateVersion patchResource(EServiceTemplateVersionId id, EServiceTemplateVersionPatchRequest eServicePatchRequest) {
        return this.client.patchEServiceTemplateVersion(id.getTemplateId(), id.getVersionId(), eServicePatchRequest);
    }
}