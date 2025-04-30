package it.pagopa.pn.interop.cucumber.steps.voucher;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.interop.utils.InteropAPIErrorResponse;
import it.pagopa.interop.utils.InteropAPIErrorResponse.InteropAPIError;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.util.List;
import java.util.UUID;

public class VoucherGenerationEServiceSteps {

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final DataPreparationService dataPreparationService;
    private final HttpCallExecutor httpCallExecutor;

    public VoucherGenerationEServiceSteps(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        DataPreparationService dataPreparationService) {
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
                    .code("9999")
                    .detail(
                        "Validation error: Invalid literal value, expected \"client_credentials\" at \"grant_type\"")
                    .build()))
            .status("400")
            .title("Bad request")
            .type("about:blank")
            .build();
        assertThat(httpCallExecutor.getResponse())
            .as("Check che la error response abbia la valorizzazione prevista")
            .isInstanceOf(InteropAPIErrorResponse.class)
            .isNotNull()
            .satisfiesAnyOf(
                response -> assertThat(response).isEqualTo(possibleResponse1),
                response -> assertThat(response).isEqualTo(possibleResponse2)
            );
    }

    // TODO la classe non è finita, continua questa e anche VoucherGenerationParamsValidationSteps




    /*Then(
  "la richiesta di generazione del Voucher non va a buon fine per il parametro grant_type",
  async function () {
    assertContextSchema(this, {
      response: z.object({
        data: z
          .object({
            errors: z.tuple([
              z.object({
                code: z.literal("015-9000"),
                detail: z.literal(
                  'Invalid parameter found - [Path \'/grant_type\'] Instance value ("unknown") not found in enum (possible values: ["client_credentials"])'
                ),
              }),
            ]),
            status: z.literal(400),
            title: z.literal(
              "The request contains bad syntax or cannot be fulfilled."
            ),
            type: z.literal("about:blank"),
          })
          .or(
            z.object({
              errors: z.tuple([
                z.object({
                  code: z.literal("9999"),
                  detail: z.literal(
                    'Validation error: Invalid literal value, expected "client_credentials" at "grant_type"'
                  ),
                }),
              ]),
              status: z.literal(400),
              title: z.literal("Bad request"),
              type: z.literal("about:blank"),
            })
          ),
      }),
    });
  }
);*/
}
