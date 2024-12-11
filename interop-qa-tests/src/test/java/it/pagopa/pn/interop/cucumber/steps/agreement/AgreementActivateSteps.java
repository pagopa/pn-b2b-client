package it.pagopa.pn.interop.cucumber.steps.agreement;

import io.cucumber.java.en.Given;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.pn.interop.cucumber.steps.utils.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.utils.EServicesCommonDomain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AgreementActivateSteps {
    private final EServicesCommonDomain eServicesCommonDomain;
    private final DataPreparationService dataPreparationService;

    public AgreementActivateSteps(DataPreparationService dataPreparationService, EServicesCommonDomain eServicesCommonDomain) {
        this.dataPreparationService = dataPreparationService;
        this.eServicesCommonDomain = eServicesCommonDomain;
    }

    @Given("{string} ha già creato un e-service in stato {string} che richiede quegli attributi con approvazione {string}")
    public void tenantHasAlreadyCreateEservice(String tenantType, String descriptorState, String approvalAgreementPolicy) {

        //TODO salvare questa lista in un contesto comune e fare riferimento a quella
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
        eServicesCommonDomain.setEserviceId(eserviceId);
        eServicesCommonDomain.setDescriptorId(descriptorId);
    }
}
