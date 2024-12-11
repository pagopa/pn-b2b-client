package it.pagopa.pn.interop.cucumber.steps.agreement;

import io.cucumber.java.en.Given;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.pn.interop.cucumber.steps.utils.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.utils.EServicesCommonDomain;
import org.junit.jupiter.api.Assertions;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class AgreementCommonSteps {
    private DataPreparationService dataPreparationService;
    private CommonUtils commonUtils;
    private EServicesCommonDomain eServicesCommonDomain;

    public AgreementCommonSteps(DataPreparationService dataPreparationService,
            CommonUtils commonUtils,
            EServicesCommonDomain eServicesCommonDomain) {
        this.dataPreparationService = dataPreparationService;
        this.commonUtils = commonUtils;
        this.eServicesCommonDomain = eServicesCommonDomain;
    }

    @Given("{string} ha una richiesta di fruizione in stato {string} per quell'e-service")
    public void tenantAlreadyHasFruitionRequestWithState(String consumer, String agreementState) {
        dataPreparationService.createAgreementWithGivenState(
                AgreementState.fromValue(agreementState),
                eServicesCommonDomain.getEserviceId(),
                eServicesCommonDomain.getDescriptorId(),
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
        // Crea gli e-service e pubblica i descrittori
        for (int i = 0; i < totalEservices; i++) {
            // Crea e-service e descrittore
            int randomInt = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
            int TEST_SEED = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
            String eserviceName = String.format("eservice-%d-%d-%d", i, TEST_SEED, randomInt);
            EServiceDescriptor eServiceDescriptor = dataPreparationService.createEServiceAndDraftDescriptor(
                    new EServiceSeed().name(eserviceName), new UpdateEServiceDescriptorSeed());
            // Porta il descrittore allo stato "PUBLISHED"
            dataPreparationService.bringDescriptorToGivenState(eServiceDescriptor.getEServiceId(),
                    eServiceDescriptor.getDescriptorId(), EServiceDescriptorState.PUBLISHED, false);
            // Aggiungi l'e-service alla lista dei pubblicati
            eServicesCommonDomain.getPublishedEservicesIds().add(eServiceDescriptor);
        }
        // Imposta il primo e-service e descrittore
        if (!eServicesCommonDomain.getPublishedEservicesIds().isEmpty()) {
            EServiceDescriptor firstDescriptor = eServicesCommonDomain.getPublishedEservicesIds().get(0);
            eServicesCommonDomain.setEserviceId(firstDescriptor.getEServiceId());
            eServicesCommonDomain.setDescriptorId(firstDescriptor.getDescriptorId());
        }

    }
}
