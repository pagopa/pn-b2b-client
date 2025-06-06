package it.pagopa.pn.interop.cucumber.steps.m2m;

import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.service.IM2MEserviceClient;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.M2MDataPreparationService;

public class EserviceSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final M2MDataPreparationService dataPreparationService;
    private IM2MEserviceClient eServiceClient;
    private final HttpCallExecutor httpCallExecutor;


    public EserviceSteps(ClientTokenConfigurator clientTokenConfigurator,
                         SharedStepsContext sharedStepsContext,
                         M2MDataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.dataPreparationService = dataPreparationService;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.eServiceClient = clientTokenConfigurator.getM2meServiceClient();
    }

    @When("l'utente tenta di recuperare la lista completa degli eServices")
    public void getEservices(){

        // Recupero la lista
        dataPreparationService.getEServices(IM2MEserviceClient.EserviceListRequest.builder()
                .offset(0)
                .limit(30)
                .build()
        );

        // Verifiche sulla lista (nei common utils)
        // Aggiorno il contesto
    }
}
