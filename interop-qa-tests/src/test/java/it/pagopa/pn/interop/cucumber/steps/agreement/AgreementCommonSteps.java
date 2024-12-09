package it.pagopa.pn.interop.cucumber.steps.agreement;

import io.cucumber.java.en.Given;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementState;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeKind;
import it.pagopa.pn.interop.cucumber.steps.utils.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.utils.EServicesCommonDomain;
import org.junit.jupiter.api.Assertions;

import java.util.UUID;

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
}
