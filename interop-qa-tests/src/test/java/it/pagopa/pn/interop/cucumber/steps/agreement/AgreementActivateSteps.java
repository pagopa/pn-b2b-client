package it.pagopa.pn.interop.cucumber.steps.agreement;

import io.cucumber.java.en.Given;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AgreementActivateSteps {
    private final DataPreparationService dataPreparationService;
    private final SharedStepsContext sharedStepsContext;
    private final CommonUtils commonUtils;

    public AgreementActivateSteps(DataPreparationService dataPreparationService,
                                  SharedStepsContext sharedStepsContext) {
        this.dataPreparationService = dataPreparationService;
        this.sharedStepsContext = sharedStepsContext;
        this.commonUtils = sharedStepsContext.getCommonUtils();
    }

    @Given("{string} ha già approvato quella richiesta di fruizione")
    public void tenantHasAlreadyAcceptedThatRequest(String tenantType) {
        commonUtils.setBearerToken(commonUtils.getToken(tenantType, null));
        dataPreparationService.activateAgreement(sharedStepsContext.getAgreementId(), null);
    }

    @Given("{string} ha già creato un e-service in stato {string} che richiede quegli attributi con approvazione {string}")
    public void tenantHasAlreadyCreateEservice(String tenantType, String descriptorState, String approvalAgreementPolicy) {

        //TODO save this list in a shared context and refer to it, not necessary in this first phase
        List<List<String>> requiredCertifiedAttributes = new ArrayList<>();
        List<List<String>> requiredDeclaredAttributes = new ArrayList<>();
        List<List<String>> requiredVerifiedAttributes = new ArrayList<>();

        EServiceDescriptor result = dataPreparationService.createEServiceAndDraftDescriptor(
                new EServiceSeed(),
                new UpdateEServiceDescriptorSeed().attributes(new DescriptorAttributesSeed()
                                .addVerifiedItem(List.of())
                                .addDeclaredItem(List.of())
                                .addCertifiedItem(List.of()))
                        .agreementApprovalPolicy(AgreementApprovalPolicy.valueOf(approvalAgreementPolicy))
        );
        UUID eserviceId = result.getEServiceId();
        UUID descriptorId = result.getDescriptorId();
        dataPreparationService.bringDescriptorToGivenState(eserviceId, descriptorId, EServiceDescriptorState.valueOf(descriptorState), false);
        sharedStepsContext.getEServicesCommonContext().setEserviceId(eserviceId);
        sharedStepsContext.getEServicesCommonContext().setDescriptorId(descriptorId);
    }
}
