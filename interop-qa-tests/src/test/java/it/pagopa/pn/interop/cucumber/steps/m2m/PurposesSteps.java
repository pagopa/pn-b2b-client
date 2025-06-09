package it.pagopa.pn.interop.cucumber.steps.m2m;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Agreement;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Agreements;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purposes;
import it.pagopa.interop.purpose.service.IM2MPurposeClient;
import it.pagopa.interop.purpose.service.IM2MPurposeClient.PurposesListRequest;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.M2MDataPreparationService;
import java.util.List;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;

public class PurposesSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final M2MDataPreparationService dataPreparationService;
    private final BFFDataPreparationService bffDataPreparationService;
    private final IM2MPurposeClient purposeClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;

    public PurposesSteps(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        M2MDataPreparationService dataPreparationService,
        BFFDataPreparationService bffDataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.dataPreparationService = dataPreparationService;
        this.bffDataPreparationService = bffDataPreparationService;
        this.purposeClient = clientTokenConfigurator.getM2mPurposeClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
    }

    @SuppressWarnings("java:S6204")
    @When("l'utente tenta di recuperare una lista di {int} finalità create")
    public void agreementsListAttempt(int agreementsQuantity) {
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        httpCallExecutor.performCall(() -> purposeClient.getPurposes(
            PurposesListRequest.builder()
                .offset(0)
                .limit(agreementsQuantity)
                .eservicesIds(List.of(eServiceId))
                .build()
        ));

        if (httpCallExecutor.getClientResponse().is2xxSuccessful()) {
            Purposes res = (Purposes) httpCallExecutor.getResponse();
            sharedStepsContext.getAgreementCommonContext().setAgreementIds(
                res.getResults().stream()
                    .map(Purpose::getId)
                    .collect(toList()));
        }
    }

    @Then("sono state visualizzate correttamente {int} finalità create")
    public void agreementsSuccessfullyGot(int expectedSize) {
        HttpStatus clientResponse = httpCallExecutor.getClientResponse();
        if(clientResponse.isError()) {
            Assertions.fail("Agreements list request failed: ", clientResponse);
        }

        Agreements agreements = (Agreements) httpCallExecutor.getResponse();
        List<UUID> visualizedIds = agreements.getResults().stream().map(Agreement::getId).toList();
        List<UUID> createdIds = sharedStepsContext.getAgreementCommonContext().getAgreementIds();
        assertSoftly(softly -> {
            softly.assertThat(visualizedIds).hasSize(expectedSize);
            softly.assertThat(createdIds).containsAll(visualizedIds);
        }) ;
    }

}
