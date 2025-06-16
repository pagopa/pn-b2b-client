package it.pagopa.pn.interop.cucumber.steps.m2m.eservice;


import io.cucumber.java.Before;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.eservice.service.IM2MEserviceDescriptorClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.AbstractCommonSteps;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.api.Assertions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EserviceDescriptorSteps extends AbstractCommonSteps<EServiceDescriptor, Pair<UUID, UUID>> {

    private final IM2MEserviceDescriptorClient client;
    private final SharedStepsContext sharedStepsContext;

    public EserviceDescriptorSteps(SharedStepsContext sharedStepsContext, ClientTokenConfigurator clientTokenConfigurator) {
        super("descriptor", clientTokenConfigurator.getM2mEServiceDescriptorClient(), sharedStepsContext);
        this.client = clientTokenConfigurator.getM2mEServiceDescriptorClient();
        this.sharedStepsContext = sharedStepsContext;
        this.client.setHttpCallExecutor(sharedStepsContext.getHttpCallExecutor());
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
