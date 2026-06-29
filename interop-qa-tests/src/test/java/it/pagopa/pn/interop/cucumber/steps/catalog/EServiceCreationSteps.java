package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;
import org.jeasy.random.randomizers.text.StringRandomizer;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class EServiceCreationSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final BFFDataPreparationService dataPreparationService;

    public EServiceCreationSteps(ClientTokenConfigurator clientTokenConfigurator,
                                 SharedStepsContext sharedStepsContext,
                                 BFFDataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.dataPreparationService = dataPreparationService;
    }

    @When("l'utente crea un e-service con lo stesso nome")
    public void createEServiceWithSameName() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getEServiceClient().createEService(
                        new EServiceSeed().name(sharedStepsContext.getEServicesCommonContext().getName())
                                .description("Questo è un e-service di test").technology(EServiceTechnology.REST)
                                .mode(EServiceMode.DELIVER)
                )
        );
    }

    @When("l'utente crea un e-service {isAsynchronous} {string} in modalità {eServiceMode}")
    public void userCreatesEserviceInSyncMode(Boolean isAsynchronous, String technology, EServiceMode eServiceMode) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        EServiceSeed eServiceSeed = new EServiceSeed()
                .name(String.format("e-service-%s", sharedStepsContext.getTestSeed()))
                .description("Questo è un e-service di test")
                .technology(EServiceTechnology.fromValue(technology))
                .mode(eServiceMode)
                .asyncExchange(isAsynchronous);
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getEServiceClient().createEService(eServiceSeed)
        );
    }

    @Given("l'utente ha già creato un e-service contenente anche il primo descrittore")
    public void userCreateEServiceWithDescriptor() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        String eserviceName = String.format("e-service-%s", sharedStepsContext.getTestSeed());
        EServiceDescriptor eServiceDescriptor = dataPreparationService.createEServiceAndDraftDescriptor(new EServiceSeed().name(eserviceName), new UpdateEServiceDescriptorSeed());
        EServicesCommonContext eServicesCommonContext = sharedStepsContext.getEServicesCommonContext();
        eServicesCommonContext.setName(eserviceName);
        eServicesCommonContext.setEserviceId(eServiceDescriptor.getEServiceId());
        eServicesCommonContext.setDescriptorId(eServiceDescriptor.getDescriptorId());
    }

    @When("l'utente crea un e-service")
    public void userCreatesEservice() {
        userCreatesEservice(null);
    }

    @When("l'utente crea un e-service con una descrizione di {int} caratteri")
    public void userCreatesEservice(Integer descriptionLength) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        String eserviceName = String.format("e-service-%s", sharedStepsContext.getTestSeed());

        String eServiceDescription = descriptionLength == null ?
                "Questo è un e-service di test" :
                (new StringRandomizer(descriptionLength, descriptionLength, System.currentTimeMillis())).getRandomValue();

        IHttpExecutor httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().createEService(new EServiceSeed().name(eserviceName)
                        .description(eServiceDescription).technology(EServiceTechnology.REST)
                        .mode(EServiceMode.DELIVER))
        );
        EServicesCommonContext eServicesCommonContext = sharedStepsContext.getEServicesCommonContext();
        if (sharedStepsContext.getHttpCallExecutor().getResponseStatus().is2xxSuccessful()) {
            CreatedEServiceDescriptor createdEServiceDescriptor = ((CreatedEServiceDescriptor) httpCallExecutor.getResponse());
            eServicesCommonContext.setEserviceId(createdEServiceDescriptor.getId());
            eServicesCommonContext.setDescriptorId(createdEServiceDescriptor.getDescriptorId());
            eServicesCommonContext.setName(eserviceName);
        }
    }

    @When("l'e-service creato ha una descrizione di {int} caratteri")
    public void eServiceCreatedWithDescription(Integer descriptionLength) {

        EServicesCommonContext eServicesCommonContext = sharedStepsContext.getEServicesCommonContext();

        sharedStepsContext.getPollingService().makePolling(
                () -> sharedStepsContext.getHttpCallExecutor().performCall(
                        () -> clientTokenConfigurator.getEServiceClient().getProducerEServiceDetailsWithHttpInfo(
                                eServicesCommonContext.getEserviceId()
                        )
                ),
                HttpStatus::is2xxSuccessful,
                "Non è stato possibile recuperare i dettagli dell'e-service creato"
        );

        ProducerEServiceDetails eServiceDetails = ((ResponseEntity<ProducerEServiceDetails>) sharedStepsContext.getHttpCallExecutor().getResponse()).getBody();

        Assertions.assertNotNull(eServiceDetails.getDescription());
        Assertions.assertEquals(descriptionLength, eServiceDetails.getDescription().length());
    }
}
