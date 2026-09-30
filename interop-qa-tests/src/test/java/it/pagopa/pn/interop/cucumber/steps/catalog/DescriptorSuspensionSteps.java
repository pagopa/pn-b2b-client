package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

@Slf4j
public class DescriptorSuspensionSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;

    public DescriptorSuspensionSteps(ClientTokenConfigurator clientTokenConfigurator,
                                     SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente sospende quel descrittore")
    public void suspendDescriptor() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().suspendDescriptor(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        sharedStepsContext.getEServicesCommonContext().getDescriptorId()
                )
        );

        try {
            PollingService.makePolling(
                    () -> clientTokenConfigurator.getEServiceClient().getEServiceDescriptor(
                            sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                            sharedStepsContext.getEServicesCommonContext().getDescriptorId()
                    ),
                    res -> res.getState().equals(EServiceDescriptorState.SUSPENDED),
                    "Errore durante la sospensione del descrittore dell'e-service",
                    4, 2_500
            );
        } catch (PollingPredicateException e) {
            log.warn("Il descrittore non risulta nello stato SUSPENDED");
        }

    }

    @When("l'utente sospende quel descrittore in corso di archiviazione")
    public void suspendDescriptorInArchiving() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().suspendDescriptor(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        sharedStepsContext.getEServicesCommonContext().getDescriptorId()
                ),
                ResponseEntity::getStatusCode
        );
    }

    @When("l'utente sospende il vecchio descrittore in corso di archiviazione")
    public void suspendOldDescriptorInArchiving() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().suspendDescriptor(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        sharedStepsContext.getEServicesCommonContext().getOldDescriptorId()
                ),
                ResponseEntity::getStatusCode
        );
    }

    @When("l'utente {string} di {string} sospende quel descrittore")
    public void suspendDescriptor(String role, String tenant) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getIdentityService().getToken(tenant, role));
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().suspendDescriptor(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        sharedStepsContext.getEServicesCommonContext().getDescriptorId()
                )
        );
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
    }

    @When("l'utente {string} di {string} sospende quel descrittore con successo")
    public void successfullySuspendDescriptor(String role, String tenant) {
        suspendDescriptor(role, tenant);
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getIdentityService().getToken(tenant, role));
        sharedStepsContext.getPollingService().makePolling(
                () ->
                        clientTokenConfigurator.getEServiceClient().getEServiceDescriptor(
                                sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                                sharedStepsContext.getEServicesCommonContext().getDescriptorId()
                        ),
                res -> res.getState().equals(EServiceDescriptorState.SUSPENDED),
                "La sospensione del descrittore dell'e-service non ha avuto successo"
        );
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
    }
}
