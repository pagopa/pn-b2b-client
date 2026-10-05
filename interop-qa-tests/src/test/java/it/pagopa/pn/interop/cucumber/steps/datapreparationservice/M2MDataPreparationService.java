package it.pagopa.pn.interop.cucumber.steps.datapreparationservice;

import it.pagopa.interop.agreement.service.IM2MAgreementClient;
import it.pagopa.interop.attribute.service.IM2MCertifiedAttributeClient;
import it.pagopa.interop.e_service_template.IM2MEServiceTemplateClient;
import it.pagopa.interop.e_service_template.mapper.RiskAnalysisMapper;
import it.pagopa.interop.eservice.service.IM2MEserviceClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.*;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.RiskAnalysisFormTemplateSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.template.DataPreparationServiceTemplate.RiskAnalysisExample;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.template.*;
import it.pagopa.pn.interop.cucumber.utility.CommonUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

import static it.pagopa.pn.interop.cucumber.steps.datapreparationservice.template.UpperAgreement.from;

@Slf4j
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class M2MDataPreparationService {
    private final IM2MAgreementClient agreementClient;
    private final IM2MCertifiedAttributeClient attributeClient;
    private final IM2MEserviceClient eserviceClient;
    private final IM2MEServiceTemplateClient eserviceTemplateClient;
    private final DataPreparationServiceTemplate templateService;
    private final SharedStepsContext sharedStepsContext;
    private final RiskAnalysisMapper riskAnalysisMapper;

    public M2MDataPreparationService(ClientTokenConfigurator clientTokenConfigurator,
                                  SharedStepsContext sharedStepsContext,
                                  CommonUtils commonUtils,
                                  RiskAnalysisMapper riskAnalysisMapper) {
        this.agreementClient = clientTokenConfigurator.getM2mAgreementClient();
        this.attributeClient = clientTokenConfigurator.getM2mCertifiedAttributeClient();
        this.eserviceClient = clientTokenConfigurator.getM2meServiceClient();
        this.eserviceTemplateClient = clientTokenConfigurator.getM2mEServiceTemplateClient();
        this.templateService = new DataPreparationServiceTemplate(
            sharedStepsContext.getHttpCallExecutor(),
            sharedStepsContext.getPollingService(),
            commonUtils
        );
        this.sharedStepsContext = sharedStepsContext;
        this.riskAnalysisMapper = riskAnalysisMapper;
    }

    public Optional<UUID> createAgreement(UUID eServiceID, UUID descriptorId, @Nullable UUID delegationId) {
        CreateAgreementOperation operation = buildCreateAgreementOperation();
        return templateService.createAgreement(operation, eServiceID, descriptorId, delegationId);
    }

    private CreateAgreementOperation buildCreateAgreementOperation() {
        return CreateAgreementOperation.of(
            params -> {
                Agreement agreement = agreementClient.createAgreement(new AgreementSeed()
                    .eserviceId(params.getEServiceID())
                    .descriptorId(params.getDescriptorId())
                    .delegationId(params.getDelegationId()));
                sharedStepsContext.getAgreementCommonContext().setCreatedAgreement(agreement);
                return agreement.getId();
            }
        );
    }

    public UUID createAndCheckAgreement(UUID eServiceID, UUID descriptorId) {
        return createAndCheckAgreement(eServiceID, descriptorId, null);
    }

    public UUID createAndCheckAgreement(UUID eServiceID, UUID descriptorId, UUID delegationId) {
        CreateAndCheckAgreementOperation operation = CreateAndCheckAgreementOperation.of(
            buildCreateAgreementOperation(),
            agreementClient::getAgreementById
        );
        return templateService.createAndCheckAgreement(operation, eServiceID, descriptorId, delegationId);
    }

    public void submitAgreement(UUID agreementId, AgreementState expectedState) {
        SubmitAgreementOperation operation = buildSubmitAgreementOperation();
        templateService.submitAgreement(operation, agreementId, UpperAgreementState.from(expectedState));
    }

    private SubmitAgreementOperation buildSubmitAgreementOperation() {
        return SubmitAgreementOperation.of(
            id -> from(agreementClient.submitAgreement(id, new AgreementSubmission())),
            id -> from(agreementClient.getAgreementById(id)));
    }

    // --ESERVICE TEMPLATE--
    public EServiceTemplate createEServiceTemplate(EServiceTemplateSeed payload){
        return eserviceTemplateClient.createEServiceTemplate(payload);
    }

    public EServiceTemplateVersions getEServiceTemplateVersions(UUID eServiceTemplateId) {
        return eserviceTemplateClient.getEserviceTemplateVersions(eServiceTemplateId);
    }

    public RiskAnalysisFormSeed getRiskAnalysisByExample(@NonNull String tenantKind, @NonNull RiskAnalysisExample example) {
        return riskAnalysisMapper.mapBFFToM2M(templateService.getRiskAnalysisByExample(tenantKind, example));
    }

    public RiskAnalysisFormTemplateSeed getRiskAnalysisTemplateByExample(@NonNull String suffix, @NonNull RiskAnalysisExample example) {
        return riskAnalysisMapper.mapBFFToM2M(templateService.getRiskAnalysisTemplateByExample(suffix, example));
    }
}