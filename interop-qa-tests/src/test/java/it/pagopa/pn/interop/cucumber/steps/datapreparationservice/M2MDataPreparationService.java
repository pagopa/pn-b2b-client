package it.pagopa.pn.interop.cucumber.steps.datapreparationservice;

import it.pagopa.interop.agreement.service.IM2MAgreementClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Agreement;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementState;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementSubmission;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.template.CreateAgreementOperation;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.template.CreateAndCheckAgreementOperation;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.template.DataPreparationServiceTemplate;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.template.SubmitAgreementOperation;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.template.UpperAgreementState;
import it.pagopa.pn.interop.cucumber.utility.CommonUtils;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Slf4j
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class M2MDataPreparationService {
    private final IM2MAgreementClient agreementClient;
    private final DataPreparationServiceTemplate templateService;

    public M2MDataPreparationService(ClientTokenConfigurator clientTokenConfigurator,
                                  SharedStepsContext sharedStepsContext,
                                  CommonUtils commonUtils) {
        this.agreementClient = clientTokenConfigurator.getM2mAgreementClient();
        this.templateService = new DataPreparationServiceTemplate(
            sharedStepsContext.getHttpCallExecutor(),
            sharedStepsContext.getPollingService(),
            commonUtils
        );
    }

    public Optional<UUID> createAgreement(UUID eServiceID, UUID descriptorId, @Nullable UUID delegationId) {
        CreateAgreementOperation operation = buildCreateAgreementOperation(
            eServiceID, descriptorId, delegationId);
        return templateService.createAgreement(operation);
    }

    private CreateAgreementOperation buildCreateAgreementOperation(UUID eServiceID, UUID descriptorId,
        UUID delegationId) {
        return CreateAgreementOperation.of(
            () -> agreementClient.createAgreement(new AgreementSeed()
                .eserviceId(eServiceID)
                .descriptorId(descriptorId)
                .delegationId(delegationId)),
            res -> ((it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Agreement) res).getId()
        );
    }

    public UUID createAndCheckAgreement(UUID eServiceID, UUID descriptorId) {
        return createAndCheckAgreement(eServiceID, descriptorId, null);
    }

    public UUID createAndCheckAgreement(UUID eServiceID, UUID descriptorId, UUID delegationId) {
        CreateAndCheckAgreementOperation operation = CreateAndCheckAgreementOperation.of(
            buildCreateAgreementOperation(eServiceID, descriptorId, delegationId),
            agreementClient::getAgreementById
        );
        return templateService.createAndCheckAgreement(operation);
    }

    public void submitAgreement(UUID agreementId, AgreementState expectedState) {
        SubmitAgreementOperation operation = SubmitAgreementOperation.of(
            () -> agreementClient.submitAgreement(agreementId, new AgreementSubmission()),
            () -> agreementClient.getAgreementById(agreementId),
            res -> UpperAgreementState.from(((Agreement) res).getState()));
        templateService.submitAgreement(operation, UpperAgreementState.from(expectedState));
    }
}