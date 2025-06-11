package it.pagopa.pn.interop.cucumber.steps.m2m.eservice;


import io.cucumber.java.en.When;
import it.pagopa.interop.eservice.service.IM2MEserviceDescriptorClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptors;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.AbstractCommonSteps;
import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EserviceDescriptorSteps extends AbstractCommonSteps<EServiceDescriptor, UUID> {

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
                .as("")
                .hasSize(1);

        EServiceDescriptors descriptors = client.getAll(eservices.get(0).getEServiceId());
        this.bindActual(sharedStepsContext, descriptors.getResults());
    }

    @Override
    public void bindActual(SharedStepsContext context, List<EServiceDescriptor> actualEntities) {
        var eserviceContext = this.sharedStepsContext.getEServicesCommonContext();
        eserviceContext.setRetrievedEservicesIds(actualEntities.stream().map(this::mapTo).collect(Collectors.toList()));
    }

    @Override
    public List<EServiceDescriptor> bindExpected(SharedStepsContext context) {
        return context.getEServicesCommonContext().getPublishedEservicesIds().stream().map(this::mapTo).collect(Collectors.toList());
    }

    @Override
    protected boolean isEqual(EServiceDescriptor a, EServiceDescriptor b) {
        return a.getId().equals(b.getId());
    }

    private it.pagopa.interop.agreement.domain.EServiceDescriptor mapTo(EServiceDescriptor eServiceDescriptor) {
        return new it.pagopa.interop.agreement.domain.EServiceDescriptor(null, eServiceDescriptor.getId());
    }

    private EServiceDescriptor mapTo(it.pagopa.interop.agreement.domain.EServiceDescriptor eServiceDescriptor) {
        EServiceDescriptor result = new EServiceDescriptor();
        result.setId(eServiceDescriptor.getDescriptorId());
        return result;
    }
}
