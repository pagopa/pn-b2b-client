package it.pagopa.pn.interop.cucumber.steps.m2m.eservice;


import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.eservice.service.IM2MEserviceDescriptorClient;
import it.pagopa.interop.eservice.service.IM2MEserviceDescriptorClient.EServiceDescriptorPatchRequest;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.AbstractCommonSteps;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.assistant.descriptor.EServiceDescriptorPatchOperationsAssistant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.api.Assertions;

public class EserviceDescriptorSteps extends AbstractCommonSteps<EServiceDescriptor, Pair<UUID, UUID>> {

    private final IM2MEserviceDescriptorClient client;
    private final SharedStepsContext sharedStepsContext;

    private final EServiceDescriptorPatchOperationsAssistant eServiceDescriptorPatchAssistant;

    public EserviceDescriptorSteps(
        SharedStepsContext sharedStepsContext,
        ClientTokenConfigurator clientTokenConfigurator,
        EServiceDescriptorPatchOperationsAssistant eServiceDescriptorPatchAssistant
    ) {
        super("descriptor", clientTokenConfigurator.getM2mEServiceDescriptorClient(), sharedStepsContext);
        this.client = clientTokenConfigurator.getM2mEServiceDescriptorClient();
        this.sharedStepsContext = sharedStepsContext;
        this.client.setHttpCallExecutor(sharedStepsContext.getHttpCallExecutor());
        this.eServiceDescriptorPatchAssistant = eServiceDescriptorPatchAssistant;
    }


    @When("l'utente tenta di recuperare la lista di descriptor usando l'eserviceId creato")
    @Override
    public void getAll() {
        var eserviceContext = this.sharedStepsContext.getEServicesCommonContext();
        List<it.pagopa.interop.agreement.domain.EServiceDescriptor> eservices = eserviceContext.getPublishedEservicesIds();

        Assertions.assertThat(eservices)
                .as("Verifica che ci sia un solo e-service pubblicato")
                .hasSize(1);

        UUID eserviceId = eservices.get(0).getEServiceId();
        retrieveDescriptors(eserviceId);
    }

    @When("l'utente tenta di recuperare la lista di descriptor con un eserviceId {entityIdType}")
    public void getAll(EntityIdType entityIdType) {
        UUID eserviceId = this.generateId(entityIdType).getLeft();
        retrieveDescriptors(eserviceId);
    }

    @When("l'utente tenta di effettuare la modifica parziale del descriptor dell'e-service")
    public void patchEServiceDescription() {
        eServiceDescriptorPatchAssistant.patchResource(EServiceDescriptorPatchRequest.builder()
            .voucherLifespan(100)
            .dailyCallsTotal(10)
            .dailyCallsPerConsumer(5)
            .build());
    }

    @When("l'utente tenta di effettuare la modifica parziale del descriptor dell'e-service specificando un sottoinsieme di informazioni")
    public void patchEServiceDescriptionSubset() {
        eServiceDescriptorPatchAssistant.patchResource(EServiceDescriptorPatchRequest.builder()
            .voucherLifespan(200)
            .build());
    }

    @When("l'utente tenta di effettuare la modifica parziale del descriptor di un e-service inesistente")
    public void patchNonExistentEServiceDescription() {
        eServiceDescriptorPatchAssistant.patchNonExistentResource();
    }

    @When("l'utente tenta di effettuare la modifica parziale del descriptor dell'e-service senza apportare cambiamenti")
    public void patchEServiceWithSameDescription() {
        eServiceDescriptorPatchAssistant.patchResourceWithSameInfo();
    }

    @Then("l'e-service descriptor è stato parzialmente modificato correttamente")
    public void verificaPatchedEService() {
        eServiceDescriptorPatchAssistant.checkPatchedResource();
    }

    @Then("l'e-service descriptor non ha subito modifiche")
    public void verificaUnpatchedEService() {
        eServiceDescriptorPatchAssistant.checkUnpatchedResource();
    }

    @Override
    public void bindActual(SharedStepsContext context, List<EServiceDescriptor> actualEntities) {
        var eserviceContext = this.sharedStepsContext.getEServicesCommonContext();
        eserviceContext.setRetrievedEservicesIds(new ArrayList<>(actualEntities));
    }

    @Override
    public List<EServiceDescriptor> bindExpected(SharedStepsContext context) {
        return new ArrayList<>(context.getEServicesCommonContext().getPublishedEservicesIds());
    }

    @Override
    protected boolean isEqual(EServiceDescriptor a, EServiceDescriptor b) {
        return a.getDescriptorId().equals(b.getDescriptorId());
    }

    private void retrieveDescriptors(UUID eserviceId) {
        List<EServiceDescriptor> descriptors = client.getAll(eserviceId);
        List<EServiceDescriptor> actualDescriptors = descriptors != null ? descriptors : List.of();

        this.actualEntities.clear();
        this.actualEntities.addAll(actualDescriptors);
        this.bindActual(sharedStepsContext, actualDescriptors);
    }
}
