package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.When;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.springframework.http.ResponseEntity;

public class DescriptorActivationSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;

    public DescriptorActivationSteps(ClientTokenConfigurator clientTokenConfigurator, SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
    }

    @When("l'utente attiva il descrittore di quell'e-service")
    public void activeEServiceDescriptor() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getEServiceClient().activateDescriptor(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(), sharedStepsContext.getEServicesCommonContext().getDescriptorId()),
                ResponseEntity::getStatusCode
        );
    }

    @When("l'utente attiva il vecchio descrittore in corso di archiviazione di quell'e-service")
    public void activeOldDescriptorInArchiving() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getEServiceClient().activateDescriptor(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(), sharedStepsContext.getEServicesCommonContext().getOldDescriptorId())
        );
    }

    @When("l'utente {string} di {string} attiva il descrittore di quell'e-service")
    public void activeEServiceDescriptor(String role, String tenant) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getIdentityService().getToken(tenant, role));
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getEServiceClient().activateDescriptor(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(), sharedStepsContext.getEServicesCommonContext().getDescriptorId())
        );
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
    }


    @When("l'utente {string} di {string} attiva il descrittore di quell'e-service con successo")
    public void successfullyActiveEServiceDescriptor(String role, String tenant) {
        activeEServiceDescriptor(role, tenant);
        if (sharedStepsContext.getHttpCallExecutor().getResponseStatus().isError()) {
            throw new IllegalStateException("L'attivazione del descrittore dell'e-service non ha avuto successo");
        }
    }
}
