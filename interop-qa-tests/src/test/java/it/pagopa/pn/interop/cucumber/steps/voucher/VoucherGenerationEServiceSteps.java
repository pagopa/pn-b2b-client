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

    @Then("la richiesta di generazione del Voucher non va a buon fine per il parametro grant_type")
    public void checkGrantTypeError() {
        InteropAPIErrorResponse possibleResponse1 = InteropAPIErrorResponse.builder()
                .errors(List.of(
                        InteropAPIError.builder()
                                .code("015-9000")
                                .detail(
                                        "Invalid parameter found - [Path '/grant_type'] Instance value (\"unknown\") not found in enum (possible values: [\"client_credentials\"])")
                                .build()))
                .status("400")
                .title("The request contains bad syntax or cannot be fulfilled.")
                .type("about:blank")
                .build();
        InteropAPIErrorResponse possibleResponse2 = InteropAPIErrorResponse.builder()
                .errors(List.of(
                        InteropAPIError.builder()
                                .code("007-9999")
                                .detail(
                                        "Validation error: Invalid literal value, expected \"client_credentials\" at \"grant_type\"")
                                .build()))
                .status("400")
                .title("Bad request")
                .type("about:blank")
                .detail("Incorrect value for body")
                .build();
        InteropAPIErrorResponse originalResponse = new ObjectMapper().convertValue(
                httpCallExecutor.getResponse(),
                InteropAPIErrorResponse.class);
        assertThat(originalResponse.toBuilder().correlationId(null)
                .build()) // perché il valore di correlationId è in questo caso irrilevante
                .as("Check che la error response abbia la valorizzazione prevista")
                .isInstanceOf(InteropAPIErrorResponse.class)
                .isNotNull()
                .satisfiesAnyOf(
                        response -> assertThat(response).isEqualTo(possibleResponse1),
                        response -> assertThat(response).isEqualTo(possibleResponse2)
                );
    }

    @Then("la richiesta di generazione del Voucher non va a buon fine")
    public void checkError() {
        InteropAPIErrorResponse expectedResponse = InteropAPIErrorResponse.builder()
                .errors(List.of(
                        InteropAPIError.builder()
                                .code("015-0008")
                                .detail("Unable to generate a token for the given request")
                                .build()))
                .status("400")
                .detail("Bad request") // ricavato sperimentalmente
                .title("The request contains bad syntax or cannot be fulfilled.")
                .type("about:blank")
                .build();

        log.info(expectedResponse.toString());

        InteropAPIErrorResponse actualResponse = new ObjectMapper().convertValue(
                httpCallExecutor.getResponse(),
                InteropAPIErrorResponse.class);

        log.info(actualResponse.toString());

        assertThat(actualResponse.getCorrelationId())
                .isNotNull()
                .asString()
                .isNotBlank();
        assertThat(actualResponse.toBuilder().correlationId(null)
                .build()) // perché il valore di correlationId è in questo caso irrilevante, basta sia un UUID (e qualora non lo fosse il parsing Jackson fallirebbe)
                .as("Check che la error response abbia la valorizzazione prevista")
                .isInstanceOf(InteropAPIErrorResponse.class)
                .isNotNull()
                .isEqualTo(expectedResponse);
    }
}