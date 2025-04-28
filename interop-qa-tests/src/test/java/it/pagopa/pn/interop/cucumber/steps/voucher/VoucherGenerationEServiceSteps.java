package it.pagopa.pn.interop.cucumber.steps.voucher;

import io.cucumber.java.en.Given;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.util.UUID;

public class VoucherGenerationEServiceSteps {

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final DataPreparationService dataPreparationService;

    public VoucherGenerationEServiceSteps(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        DataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.dataPreparationService = dataPreparationService;
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

    //@Then("la richiesta di generazione del Voucher non va a buon fine per il parametro grant_type")




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
