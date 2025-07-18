package it.pagopa.pn.interop.cucumber.steps.m2m.eservice;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.eservice.service.IM2MEserviceClient;
import it.pagopa.interop.eservice.service.mapper.EserviceDescriptorDomainMapper;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EService;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorState;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.AbstractCommonSteps;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EserviceSteps extends AbstractCommonSteps<EService, UUID> {
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpExecutor;
    private final PollingService pollingService;
    private final IM2MEserviceClient client;

    public EserviceSteps(SharedStepsContext sharedStepsContext, ClientTokenConfigurator clientTokenConfigurator) {
        super("eService", clientTokenConfigurator.getM2meServiceClient(), sharedStepsContext);
        this.sharedStepsContext = sharedStepsContext;
        this.httpExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.client  = clientTokenConfigurator.getM2meServiceClient();
        client.setHttpCallExecutor(sharedStepsContext.getHttpCallExecutor());
    }

    @Given("l'utente effettua la cancellazione dell'e-service con successo")
    public void successfullyDeleteEService() {
        deleteEService();
        UUID eserviceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        pollingService.makePolling(
            () -> httpExecutor.performCall(() -> this.client.get(eserviceId)),
            status -> status.equals(NOT_FOUND),
            "Non è stato possibile eliminare l'e-service. Consultare i log per maggiori dettagli.");
    }

    @When("l'utente tenta di effettuare la cancellazione di un e-service inesistente")
    public void deleteNonExistentEService() {
        UUID eserviceId = UUID.randomUUID();
        httpExecutor.performCall(() -> this.client.delete(eserviceId));
    }

    @When("l'utente tenta di effettuare la cancellazione dell'e-service")
    public void deleteEService() {
        UUID eserviceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        httpExecutor.performCall(() -> this.client.delete(eserviceId));
    }

    @When("l'utente tenta di effettuare la riattivazione dell'e-service")
    public void unsuspendEService() {
        UUID eserviceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        httpExecutor.performCall(() -> client.unsuspendEService(eserviceId, descriptorId));
    }

    @When("l'utente tenta di effettuare la riattivazione di un e-service inesistente")
    public void unsuspendNonExistentEService() {
        UUID eserviceId = UUID.randomUUID();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        httpExecutor.performCall(() -> client.unsuspendEService(eserviceId, descriptorId));
    }

    @When("l'utente tenta di effettuare la riattivazione di un descriptor dell'e-service inesistente")
    public void unsuspendNonExistentDescriptor() {
        UUID eserviceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = UUID.randomUUID();
        httpExecutor.performCall(() -> client.unsuspendEService(eserviceId, descriptorId));
    }

    @Then("l'e-service è stato riattivato con successo")
    public void successfullyUnsuspendedEService() {
        UUID eserviceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        pollingService.makePolling(() -> httpExecutor.performCall(
            () ->client.getDescriptor(eserviceId, descriptorId)),
            status -> status.is2xxSuccessful() && ((it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptor) httpExecutor.getResponse()).getState().equals(EServiceDescriptorState.PUBLISHED),
            "Il servizio non è stato riattivato come previsto.");
    }

    @Override
    public void bindActual(SharedStepsContext context, List<EService> actualEntities) {
        var eserviceContext = context.getEServicesCommonContext();
        eserviceContext.setRetrievedEservicesIds(actualEntities.stream().map(this::mapTo).collect(Collectors.toList()));
    }

    @Override
    public List<EService> bindExpected(SharedStepsContext context) {
        return context.getEServicesCommonContext().getPublishedEservicesIds().stream().map(this::mapTo).collect(Collectors.toList());
    }

    @Override
    protected boolean isEqual(EService a, EService b) {
        return a.getId().equals(b.getId());
    }

    private EServiceDescriptor mapTo(EService eService) {
        return EserviceDescriptorDomainMapper.mapTo(eService);
    }

    private EService mapTo(EServiceDescriptor descriptor) {
        return EserviceDescriptorDomainMapper.mapTo(descriptor);
    }
}
