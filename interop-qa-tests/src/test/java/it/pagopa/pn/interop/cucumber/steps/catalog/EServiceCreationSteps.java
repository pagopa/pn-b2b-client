package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;

public class EServiceCreationSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final DataPreparationService dataPreparationService;

    public EServiceCreationSteps(ClientTokenConfigurator clientTokenConfigurator,
                                 SharedStepsContext sharedStepsContext,
                                 DataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.dataPreparationService = dataPreparationService;
    }

    @When("l'utente crea un e-service con lo stesso nome")
    public void createEServiceWithSameName() {
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getEServiceClient().createEService(
                        sharedStepsContext.getXCorrelationId(),
                        new EServiceSeed().name(sharedStepsContext.getEServicesCommonContext().getName())
                                .description("Questo è un e-service di test").technology(EServiceTechnology.REST)
                                .mode(EServiceMode.DELIVER)
                )
        );
    }

    @Given("l'utente ha già creato un e-service contenente anche il primo descrittore")
    public void userCreateEServiceWithDescriptor() {
        String eserviceName = String.format("e-service-%s", sharedStepsContext.getXCorrelationId());
        EServiceDescriptor eServiceDescriptor = dataPreparationService.createEServiceAndDraftDescriptor(new EServiceSeed().name(eserviceName), new UpdateEServiceDescriptorSeed());
        EServicesCommonContext eServicesCommonContext = sharedStepsContext.getEServicesCommonContext();
        eServicesCommonContext.setName(eserviceName);
        eServicesCommonContext.setEserviceId(eServiceDescriptor.getEServiceId());
        eServicesCommonContext.setDescriptorId(eServiceDescriptor.getDescriptorId());
    }

    @When("l'utente crea un e-service")
    public void userCreatesEservice() {
        String eserviceName = String.format("e-service-%s", sharedStepsContext.getXCorrelationId());
        CreatedEServiceDescriptor createdEServiceDescriptor = clientTokenConfigurator.getEServiceClient().createEService(sharedStepsContext.getXCorrelationId(),
                new EServiceSeed().name(eserviceName)
                        .description("Questo è un e-service di test").technology(EServiceTechnology.REST)
                        .mode(EServiceMode.DELIVER));
        EServicesCommonContext eServicesCommonContext = sharedStepsContext.getEServicesCommonContext();
        eServicesCommonContext.setName(eserviceName);
        //TODO da controllare se bisogna salvare direttamente createdEServiceDescriptor dentro eServiceCommonContext
        eServicesCommonContext.setEserviceId(createdEServiceDescriptor.getId());
        eServicesCommonContext.setDescriptorId(createdEServiceDescriptor.getDescriptorId());
    }


}
