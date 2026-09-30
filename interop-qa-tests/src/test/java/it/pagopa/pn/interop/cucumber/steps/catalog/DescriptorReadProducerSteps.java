package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

public class DescriptorReadProducerSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;

    public DescriptorReadProducerSteps(ClientTokenConfigurator clientTokenConfigurator,
                                       SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente richiede la lettura di quel descrittore")
    public void producerRequiresDescriptorRead() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getProducerClient().getProducerEServiceDescriptor(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        sharedStepsContext.getEServicesCommonContext().getDescriptorId()
                )
        );
    }

    @When("il descrittore risulta in stato {string}")
    public void producerRequiresDescriptorRead(String descriptorState) {
        EServiceDescriptorState descriptorStateEn = EServiceDescriptorState.valueOf(descriptorState);
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        sharedStepsContext.getPollingService().makePolling(
            () -> httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getProducerClient().getProducerEServiceDescriptor(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        sharedStepsContext.getEServicesCommonContext().getDescriptorId()
                )),
                res -> res.is2xxSuccessful() && ((ProducerEServiceDescriptor) httpCallExecutor.getResponse()).getState().equals(descriptorStateEn),
                "Il descrittore non è risultato in stato %s entro il tempo limite".formatted(descriptorState)
        );
    }
}
