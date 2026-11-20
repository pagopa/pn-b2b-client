package it.pagopa.pn.interop.cucumber.steps.m2m.producer_keychain;

import io.cucumber.java.en.And;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.ProducerKeychainAddEService;
import it.pagopa.interop.producer_keychains.IM2MProducerKeychainsClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.UUID;

public class ProducerKeychainSteps {
    private final SharedStepsContext context;
    private final IM2MProducerKeychainsClient producerKeychainClient;

    public ProducerKeychainSteps(SharedStepsContext context, ClientTokenConfigurator clientTokenConfigurator) {
        this.context = context;
        this.producerKeychainClient = clientTokenConfigurator.getM2mProducerKeychainsClient();
        this.producerKeychainClient.setHttpCallExecutor(context.getHttpCallExecutor());
    }

    @And("l'utente associa il portachiavi erogatore all'e-service")
    public void linkProducerKeychainToEService() {
        UUID eServiceId = context.getEServicesCommonContext().getEserviceId();
        linkProducerKeychainToEservice(eServiceId);
    }

    @And("l'utente associa il portachiavi erogatore all'e-service con id {string}")
    public void linkProducerKeychainToEServiceById(String rawEserviceId) {
        UUID eServiceId = UUID.fromString(rawEserviceId);
        context.getEServicesCommonContext().setEserviceId(eServiceId);
        linkProducerKeychainToEservice(eServiceId);
    }

    private void linkProducerKeychainToEservice(UUID eServiceId) {
        UUID producerKeychainId = context.getProducerKeychainCommonContext().getFirstProducerKeychainId();
        producerKeychainClient.linkEservice(
            producerKeychainId,
            new ProducerKeychainAddEService().eserviceId(eServiceId)
        );
    }
}
