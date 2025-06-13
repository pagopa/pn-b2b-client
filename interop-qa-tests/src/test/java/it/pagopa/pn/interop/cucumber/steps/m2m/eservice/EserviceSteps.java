package it.pagopa.pn.interop.cucumber.steps.m2m.eservice;

import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.eservice.service.IM2MEserviceClient;
import it.pagopa.interop.eservice.service.mapper.EserviceDescriptorDomainMapper;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EService;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.AbstractCommonSteps;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EserviceSteps extends AbstractCommonSteps<EService, UUID> {

    private final IM2MEserviceClient client;

    public EserviceSteps(SharedStepsContext sharedStepsContext, ClientTokenConfigurator clientTokenConfigurator) {
        super("eService", clientTokenConfigurator.getM2meServiceClient(), sharedStepsContext);
        this.client = clientTokenConfigurator.getM2meServiceClient();
        this.client.setHttpCallExecutor(sharedStepsContext.getHttpCallExecutor());
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
