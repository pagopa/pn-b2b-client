package it.pagopa.pn.interop.cucumber.steps.m2m.purpose.assistant;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.e_service_template.mapper.RiskAnalysisMapper;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purpose;
import it.pagopa.interop.purpose.service.IM2MPurposeClient.ReversePurposePatchRequest;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.m2m.purpose.mapper.ReversePurposeMapper;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.UUID;

@ToString
@EqualsAndHashCode(callSuper = true)
@Component
@ScenarioScope
public class ReversePurposePatchOperationsAssistant extends PurposeGenericPatchOperationsAssistant<ReversePurposePatchRequest> {
    private final BFFDataPreparationService dataPreparationService;
    private final IdentityService identityService;
    private final SharedStepsContext sharedContext;
    private final ClientTokenConfigurator clientTokenConfigurator;

    public ReversePurposePatchOperationsAssistant(
        ReversePurposeMapper resourceMapper,
        SharedStepsContext sharedStepsContext,
        ClientTokenConfigurator tokenConfigurator,
        PurposePatchContext patchContext,
        BFFDataPreparationService dataPreparationService,
        RiskAnalysisMapper riskAnalysisMapper,
        @Qualifier("interopIdentityService") IdentityService identityService
    ) {
        super(resourceMapper, sharedStepsContext, tokenConfigurator.getM2mPurposeClient(), patchContext, tokenConfigurator);
        this.dataPreparationService = dataPreparationService;
        this.identityService = identityService;
        this.sharedContext = sharedStepsContext;
        this.clientTokenConfigurator = tokenConfigurator;
    }

    @Override
    public ReversePurposePatchRequest buildDefaultPatchRequest() {
        UUID uuid = UUID.randomUUID();

        String lastToken = clientTokenConfigurator.getLastToken();
        String tenantType = sharedContext.getTenantType();
        String token = identityService.getToken(tenantType, null);
        clientTokenConfigurator.setBearerToken(token);
        clientTokenConfigurator.setBearerToken(lastToken);

        return ReversePurposePatchRequest.builder()
            .title("patched title - " + uuid)
            .description("patched description - " + uuid)
            .dailyCalls(10)
            .isFreeOfCharge(true)
            .freeOfChargeReason("some reason - " + uuid)
            .build();
    }


    public ReversePurposePatchRequest buildActualPatchRequest() {
        UUID uuid = UUID.fromString(this.sharedContext.getPurposeCommonContext().getPurposeId());

        String lastToken = clientTokenConfigurator.getLastToken();
        String tenantType = sharedContext.getTenantType();
        String token = identityService.getToken(tenantType, null);
        clientTokenConfigurator.setBearerToken(token);
        clientTokenConfigurator.setBearerToken(lastToken);

        return ReversePurposePatchRequest.builder()
                .title("patched title - " + uuid)
                .description("patched description - " + uuid)
                .dailyCalls(10)
                .isFreeOfCharge(true)
                .freeOfChargeReason("some reason - " + uuid)
                .build();
    }

    @Override
    protected Purpose patchResource(UUID uuid, ReversePurposePatchRequest patchRequest) {
        return this.client.patchReversePurpose(uuid, patchRequest);
    }
}