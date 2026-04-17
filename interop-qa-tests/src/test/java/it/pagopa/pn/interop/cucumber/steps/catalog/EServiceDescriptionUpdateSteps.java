package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.When;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptionUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.EServiceState;
import org.jeasy.random.randomizers.text.StringRandomizer;

import java.util.Objects;

public class EServiceDescriptionUpdateSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final EServicesCommonContext eServicesCommonContext;

    public EServiceDescriptionUpdateSteps(ClientTokenConfigurator clientTokenConfigurator,
                               SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.eServicesCommonContext = sharedStepsContext.getEServicesCommonContext();
    }

    @When("l'utente aggiorna la descrizione di quell'e-service")
    public void userUpdateEServiceDescription() {
        userUpdateEServiceDescriptionInState(null, null);
    }

    @When("l'utente aggiorna la descrizione di quell'e-service in stato {string} con un valore di {int} caratteri")
    public void userUpdateEServiceDescriptionInState(String eServiceState, Integer descriptionLength) {

        String eServiceDescription = descriptionLength == null ?
                String.format("Nuova descrizione - %d", sharedStepsContext.getTestSeed()) :
                (new StringRandomizer(descriptionLength, descriptionLength, System.currentTimeMillis())).getRandomValue();;

        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        if (Objects.equals(eServiceState, EServiceState.PUBLISHED.name())) {
            sharedStepsContext.getHttpCallExecutor().performCall(
                    () -> clientTokenConfigurator.getEServiceClient().updateEServiceDescription(
                            eServicesCommonContext.getEserviceId(),
                            new EServiceDescriptionUpdateSeed().description(eServiceDescription)
                    )
            );
        } else {

            ProducerEServiceDetails createdEService = clientTokenConfigurator.getEServiceClient().getProducerEServiceDetailsWithHttpInfo(
                    eServicesCommonContext.getEserviceId()
            ).getBody();
            UpdateEServiceSeed seed = new UpdateEServiceSeed()
                    .description(eServiceDescription)
                    .name(createdEService.getName())
                    .mode(createdEService.getMode())
                    .isClientAccessDelegable(createdEService.getIsClientAccessDelegable())
                    .isConsumerDelegable(createdEService.getIsConsumerDelegable())
                    .isSignalHubEnabled(createdEService.getIsSignalHubEnabled())
                    .personalData(createdEService.getPersonalData())
                    .technology(createdEService.getTechnology());
            sharedStepsContext.getHttpCallExecutor().performCall(
                    () -> clientTokenConfigurator.getEServiceClient().updateEServiceById(eServicesCommonContext.getEserviceId(), seed)
            );
        }
    }

    @When("l'utente {string} di {string} aggiorna la descrizione di quell'e-service")
    public void userUpdateEServiceDescription(String role, String tenant) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getIdentityService().getToken(tenant, role));
        String newDescription = String.format("Nuova descrizione - %d", sharedStepsContext.getTestSeed());
        sharedStepsContext.getHttpCallExecutor().performCall(
            () -> clientTokenConfigurator.getEServiceClient().updateEServiceDescription(
                eServicesCommonContext.getEserviceId(),
                new EServiceDescriptionUpdateSeed().description(newDescription)
            )
        );
        if(sharedStepsContext.getHttpCallExecutor().getResponseStatus().is2xxSuccessful()) {
            sharedStepsContext.getEServicesCommonContext().setDescription(newDescription);
        }
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
    }

    @When("l'utente {string} di {string} aggiorna la descrizione di quell'e-service con successo")
    public void successfullyUserUpdateEServiceDescription(String role, String tenant) {
        userUpdateEServiceDescription(role, tenant);
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getIdentityService().getToken(tenant, role));
        EServicesCommonContext eServiceContext = sharedStepsContext.getEServicesCommonContext();
        sharedStepsContext.getPollingService().makePolling(
            () -> clientTokenConfigurator.getEServiceClient().getEServiceDescriptor(
                eServiceContext.getEserviceId(),
                eServiceContext.getDescriptorId()
            ),
            res -> res.getEservice().getDescription().equals(eServiceContext.getDescription()),
            "L'aggiornamento della descrizione non ha avuto successo"
        );
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
    }
}
