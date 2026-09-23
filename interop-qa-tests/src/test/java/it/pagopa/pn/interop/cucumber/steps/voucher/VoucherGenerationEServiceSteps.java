package it.pagopa.pn.interop.cucumber.steps.voucher;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.utils.InteropAPIErrorResponse;
import it.pagopa.interop.utils.InteropAPIErrorResponse.InteropAPIError;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.UUID;

@Slf4j
public class VoucherGenerationEServiceSteps {

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final BFFDataPreparationService dataPreparationService;
    private final IHttpExecutor httpCallExecutor;

    public VoucherGenerationEServiceSteps(ClientTokenConfigurator clientTokenConfigurator,
                                          SharedStepsContext sharedStepsContext,
                                          BFFDataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.dataPreparationService = dataPreparationService;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @Given("{string} ha già una nuova versione in stato DRAFT per quell'e-service")
    public void createDraftVersion(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        UUID eserviceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();

        UUID descriptorId = dataPreparationService.createNextDraftDescriptor(
                eserviceId);
        sharedStepsContext.getEServicesCommonContext().setDescriptorId(descriptorId);
        dataPreparationService.bringDescriptorToGivenState(
                eserviceId,
                descriptorId,
                EServiceDescriptorState.DRAFT,
                false);
    }

    @Given("{string} ha già attivato nuovamente quell'e-service")
    public void activateEService(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        dataPreparationService.activateDescriptor(eServiceId, descriptorId);
    }

    @Given("{string} ha già sospeso la vecchia versione di quell'e-service")
    public void suspendOldVersion(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getOldDescriptorId();
        dataPreparationService.suspendDescriptor(eServiceId, descriptorId);
    }

    @Given("{string} ha già attivato nuovamente la vecchia versione quell'e-service")
    public void activateOldVersion(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID oldDescriptorId = sharedStepsContext.getEServicesCommonContext().getOldDescriptorId();
        dataPreparationService.activateDescriptor(eServiceId, oldDescriptorId);
    }
}