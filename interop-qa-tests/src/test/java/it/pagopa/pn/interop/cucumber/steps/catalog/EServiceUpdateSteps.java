package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.assertj.core.api.Assertions;

import java.time.OffsetDateTime;

public class EServiceUpdateSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;

    public EServiceUpdateSteps(ClientTokenConfigurator clientTokenConfigurator,
                               SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
    }

    @When("l'utente aggiorna quell'e-service")
    public void userUpdateEService() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        userUpdateEServiceImpl();
    }

    @When("{string} aggiorna quell'e-service")
    public void userUpdateEService(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        userUpdateEServiceImpl();
    }

    @When("l'utente {string} la delega amministrativa e {string} la delega tecnica per la fruizione dell'e-service")
    public void updateEServiceDelegationAvailability(String consumerDelegationAction, String clientAccessDelegationAction) {

        boolean isConsumerDelegable = this.getAction(consumerDelegationAction);
        boolean isClientAccessDelegable = this.getAction(clientAccessDelegationAction);

        this.updateEServiceDelegationFlags(isConsumerDelegable, isClientAccessDelegable);
    }

    @When("la delega amministrativa è {string} e la delega tecnica è {string} per la fruizione dell'e-service")
    public void checkEServiceDelegationAvailability(String consumerDelegationAction, String clientAccessDelegationAction) {
        boolean isConsumerDelegable = this.getAction(consumerDelegationAction);
        boolean isClientAccessDelegable = this.getAction(clientAccessDelegationAction);

        IHttpExecutor httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        sharedStepsContext.getPollingService().makePolling(
                () -> httpCallExecutor.performCall(
                        () -> clientTokenConfigurator.getProducerClient().getProducerEServiceDetails(
                                sharedStepsContext.getEServicesCommonContext().getEserviceId()
                        )
                ),
                res -> {
                    ProducerEServiceDetails eServiceDetails = (ProducerEServiceDetails) httpCallExecutor.getResponse();
                    return eServiceDetails.getIsConsumerDelegable() != null && eServiceDetails.getIsConsumerDelegable().equals(isConsumerDelegable) &&
                            eServiceDetails.getIsClientAccessDelegable() != null && eServiceDetails.getIsClientAccessDelegable().equals(isClientAccessDelegable);
                },
                "Impossibile aggiornare i flag di delega dell'e-service"
        );

        ProducerEServiceDetails eServiceDetails = (ProducerEServiceDetails) httpCallExecutor.getResponse();
        Assertions.assertThat(eServiceDetails.getIsConsumerDelegable()).isEqualTo(isConsumerDelegable);
        Assertions.assertThat(eServiceDetails.getIsClientAccessDelegable()).isEqualTo(isClientAccessDelegable);
    }

    private boolean getAction(String action) {
        return switch (action) {
            case "abilita", "attiva" -> true;
            case "disabilita", "disattiva" -> false;
            default -> throw new RuntimeException("Invalid action");
        };
    }

    private void userUpdateEServiceImpl() {
        IHttpExecutor httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().updateEServiceById(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        new UpdateEServiceSeed()
                                .name(String.format("e-service - %d", sharedStepsContext.getTestSeed()))
                                .description("Nuova descrizione")
                                .mode(EServiceMode.DELIVER)
                                .technology(EServiceTechnology.SOAP)
                )
        );
        if(httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            sharedStepsContext.getEServicesCommonContext().setEServiceEditTimestamp(OffsetDateTime.now());
        }
    }

    private void updateEServiceDelegationFlags(boolean isConsumerDelegable, boolean isClientAccessDelegable) {
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getEServiceClient().updateEServiceDelegationFlags(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        new EServiceDelegationFlagsUpdateSeed()
                                .isConsumerDelegable(isConsumerDelegable)
                                .isClientAccessDelegable(isClientAccessDelegable)
                )
        );
    }
}
