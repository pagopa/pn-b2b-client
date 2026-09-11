package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class DescriptorCreationSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final BFFDataPreparationService dataPreparationService;

    public DescriptorCreationSteps(ClientTokenConfigurator clientTokenConfigurator,
                                   SharedStepsContext sharedStepsContext,
                                   BFFDataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.dataPreparationService = dataPreparationService;
    }

    @Given("l'utente ha già pubblicato quel descrittore")
    public void userPublishDescriptor() {
        EServicesCommonContext eServicesCommonContext = sharedStepsContext.getEServicesCommonContext();
        dataPreparationService.bringDescriptorToGivenState(
                eServicesCommonContext.getEserviceId(),
                eServicesCommonContext.getDescriptorId(),
                EServiceDescriptorState.PUBLISHED,
                false
        );
    }

    @When("l'utente crea una versione in bozza per quell'e-service")
    public void userCreatesDraftDescriptor() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getEServiceClient().createDescriptor(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId()
                )
        );
    }

    @When("l'utente crea una versione in bozza per quell'e-service istanza di template")
    public void userCreatesDraftDescriptorForEServiceFromTemplate() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getEServiceClient().createDescriptor(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId()
                )
        );
        sharedStepsContext.getEServiceTemplateStepContext().setLastEServiceIdCreatedFromTemplate(
                sharedStepsContext.getEServicesCommonContext().getEserviceId()
        );
        UUID draftDescriptorId = ((CreatedResource)sharedStepsContext.getHttpCallExecutor().getResponse()).getId();
        sharedStepsContext.getEServiceTemplateStepContext().setLastEServiceDescriptorIdCreatedFromTemplate(draftDescriptorId);
        sharedStepsContext.getEServicesCommonContext().setOldDescriptorId(
                sharedStepsContext.getEServicesCommonContext().getDescriptorId()
        );
        sharedStepsContext.getEServicesCommonContext().setDescriptorId(draftDescriptorId);
    }

    @Then("si ottiene status code 200 e il descrittore contiene i campi del precedente")
    public void verifyStatusCodeAndDescriptor() {
        ProducerEServiceDescriptor descriptor = clientTokenConfigurator.getProducerClient().getProducerEServiceDescriptor(
                sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                sharedStepsContext.getEServicesCommonContext().getDescriptorId()
        );
        UUID newDescriptorId = ((CreatedResource) sharedStepsContext.getHttpCallExecutor().getResponse()).getId();

        sharedStepsContext.getPollingService().makePolling(
                () -> sharedStepsContext.getHttpCallExecutor().performCall(
                        () -> clientTokenConfigurator.getProducerClient().getProducerEServiceDescriptor(
                                sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                                newDescriptorId
                        )
                ),
                res -> res != HttpStatus.NOT_FOUND,
                String.format("The eservice descriptor search was not found: %s", sharedStepsContext.getEServicesCommonContext().getEserviceId())
        );

        ProducerEServiceDescriptor newDescriptor = clientTokenConfigurator.getProducerClient().getProducerEServiceDescriptor(
                sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                newDescriptorId
        );

        Assertions.assertEquals(HttpStatus.OK, sharedStepsContext.getHttpCallExecutor().getResponseStatus());
        Assertions.assertEquals(descriptor.getDescription(), newDescriptor.getDescription());
        Assertions.assertEquals(descriptor.getVoucherLifespan(), newDescriptor.getVoucherLifespan());
        Assertions.assertEquals(descriptor.getDailyCallsPerConsumer(), newDescriptor.getDailyCallsPerConsumer());
        Assertions.assertEquals(descriptor.getDailyCallsTotal(), newDescriptor.getDailyCallsTotal());
        Assertions.assertEquals(descriptor.getAgreementApprovalPolicy(), newDescriptor.getAgreementApprovalPolicy());
        Assertions.assertEquals(descriptor.getAttributes(), newDescriptor.getAttributes());
    }
}
