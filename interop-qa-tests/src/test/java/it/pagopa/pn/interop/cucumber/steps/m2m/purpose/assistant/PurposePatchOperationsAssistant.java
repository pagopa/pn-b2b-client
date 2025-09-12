package it.pagopa.pn.interop.cucumber.steps.m2m.purpose.assistant;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.e_service_template.mapper.RiskAnalysisMapper;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.RiskAnalysisFormSeed;
import it.pagopa.interop.purpose.domain.RiskAnalysis;
import it.pagopa.interop.purpose.service.IM2MPurposeClient.PurposePatchRequest;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.m2m.purpose.mapper.PurposeMapper;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@ToString
@EqualsAndHashCode(callSuper = true)
@Component
@ScenarioScope
public class PurposePatchOperationsAssistant extends PurposeGenericPatchOperationsAssistant<PurposePatchRequest> {
    private final BFFDataPreparationService dataPreparationService;
    private final RiskAnalysisMapper riskAnalysisMapper;
    private final IdentityService identityService;
    private final SharedStepsContext sharedContext;
    private final ClientTokenConfigurator clientTokenConfigurator;

    public PurposePatchOperationsAssistant(
        PurposeMapper resourceMapper,
        SharedStepsContext sharedStepsContext,
        ClientTokenConfigurator tokenConfigurator,
        PurposePatchContext patchContext,
        BFFDataPreparationService dataPreparationService,
        RiskAnalysisMapper riskAnalysisMapper,
        @Qualifier("interopIdentityService") IdentityService identityService
    ) {
        super(resourceMapper, sharedStepsContext, tokenConfigurator.getM2mPurposeClient(), patchContext, tokenConfigurator);
        this.dataPreparationService = dataPreparationService;
        this.riskAnalysisMapper = riskAnalysisMapper;
        this.identityService = identityService;
        this.sharedContext = sharedStepsContext;
        this.clientTokenConfigurator = tokenConfigurator;
    }

    @Override
    public PurposePatchRequest buildDefaultPatchRequest() {
        UUID uuid = UUID.randomUUID();

        String lastToken = clientTokenConfigurator.getLastToken();
        String tenantType = sharedContext.getTenantType();
        String token = identityService.getToken(tenantType, null);
        clientTokenConfigurator.setBearerToken(token);
        RiskAnalysis riskAnalysis = dataPreparationService.getRiskAnalysis(tenantType, true);
        clientTokenConfigurator.setBearerToken(lastToken);

        RiskAnalysisFormSeed riskAnalysisFormSeed = riskAnalysisMapper.mapBFFToM2M(
            riskAnalysis.getRiskAnalysisForm());
        return PurposePatchRequest.builder()
            .title("patched title - " + uuid)
            .description("patched description - " + uuid)
            .dailyCalls(10)
            .isFreeOfCharge(true)
            .freeOfChargeReason("some reason - " + uuid)
            .riskAnalysisForm(riskAnalysisFormSeed)
            .build();
    }

    @Override
    protected Purpose patchResource(UUID uuid, PurposePatchRequest eServicePatchRequest) {
        return this.client.patchPurpose(uuid, eServicePatchRequest);
    }
}