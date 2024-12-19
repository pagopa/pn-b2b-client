package it.pagopa.pn.interop.cucumber.steps.agreement;

import io.cucumber.java.en.Given;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class AgreementCommonSteps {
    private DataPreparationService dataPreparationService;
    private CommonUtils commonUtils;
    private SharedStepsContext sharedStepsContext;

    public AgreementCommonSteps(DataPreparationService dataPreparationService,
            CommonUtils commonUtils,
            SharedStepsContext sharedStepsContext) {
        this.dataPreparationService = dataPreparationService;
        this.commonUtils = commonUtils;
        this.sharedStepsContext = sharedStepsContext;
    }

    @Given("{string} ha una richiesta di fruizione in stato {string} per quell'e-service")
    public void tenantAlreadyHasFruitionRequestWithState(String tenant, String agreementState) {
        commonUtils.setBearerToken(commonUtils.getToken(tenant, null));
        dataPreparationService.createAgreementWithGivenState(
                AgreementState.fromValue(agreementState),
                sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                sharedStepsContext.getEServicesCommonContext().getDescriptorId(),
                null);
    }

    @Given("{string} ha creato un attributo certificato e lo ha assegnato a {string}")
    public void tenantHasCreatedCertifiedAttribute(String certifier, String tenantType) {
        UUID tenantId = commonUtils.getOrganizationId(tenantType);

        UUID attributeId = dataPreparationService.createAttribute(AttributeKind.CERTIFIED, null);
        dataPreparationService.assignCertifiedAttributeToTenant(tenantId, attributeId);
    }

    @Given("{string} ha già creato e pubblicato {int} e-service(s)")
    public void tenantHasAlreadyCreatedAndPublishedEService(String tenantType, int totalEservices) {
        commonUtils.setBearerToken(commonUtils.getToken(tenantType, null));
        // Create e-services and publish descriptors
        EServicesCommonContext eServicesCommonContext = sharedStepsContext.getEServicesCommonContext();
        for (int i = 0; i < totalEservices; i++) {
            // Create e-service and descriptor
            int randomInt = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
            int TEST_SEED = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
            String eserviceName = String.format("eservice-%d-%d-%d", i, TEST_SEED, randomInt);
            EServiceDescriptor eServiceDescriptor = dataPreparationService.createEServiceAndDraftDescriptor(
                    new EServiceSeed().name(eserviceName), new UpdateEServiceDescriptorSeed());
            // Set the descriptor to "PUBLISHED" state
            dataPreparationService.bringDescriptorToGivenState(eServiceDescriptor.getEServiceId(),
                    eServiceDescriptor.getDescriptorId(), EServiceDescriptorState.PUBLISHED, false);
            // Add the e-service to the list of published ones
            eServicesCommonContext.getPublishedEservicesIds().add(eServiceDescriptor);
        }
        // Set the first e-service and descriptor
        if (!eServicesCommonContext.getPublishedEservicesIds().isEmpty()) {
            EServiceDescriptor firstDescriptor = eServicesCommonContext.getPublishedEservicesIds().get(0);
            eServicesCommonContext.setEserviceId(firstDescriptor.getEServiceId());
            eServicesCommonContext.setDescriptorId(firstDescriptor.getDescriptorId());
        }

    }
}
