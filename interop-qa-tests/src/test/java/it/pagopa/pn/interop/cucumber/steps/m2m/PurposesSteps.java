package it.pagopa.pn.interop.cucumber.steps.m2m;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Agreement;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Agreements;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeVersion;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeVersionSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purposes;
import it.pagopa.interop.purpose.service.IM2MPurposeClient;
import it.pagopa.interop.purpose.service.IM2MPurposeClient.PurposesListRequest;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.PurposeCommonContext;
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
    private final int newDailyCalls = 50;

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

    @When("{string} tenta di creare una nuova versione della finalità aggiornando la stima di carico")
    public void createPurposeVersion(String tenant) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        PurposeCommonContext purposeCommonContext = sharedStepsContext.getPurposeCommonContext();
        PurposeVersionSeed purposeVersionSeed = new PurposeVersionSeed().dailyCalls(newDailyCalls);
        httpCallExecutor.performCall(
            () -> purposeClient.createPurposeVersion(
                UUID.fromString(purposeCommonContext.getPurposeId()),
                purposeVersionSeed
            )
        );

        if(httpCallExecutor.getClientResponse().is2xxSuccessful()) {
            PurposeVersion createdVersion = (PurposeVersion) httpCallExecutor.getResponse();
            purposeCommonContext.addCurrentVersionId(createdVersion.getId());
        }
    }

    @Then("la nuova versione della finalità è stata creata correttamente")
    public void purposeVersionSuccessfullyCreated() {
        httpCallExecutor.performCall(() -> purposeClient.getVersion(
            sharedStepsContext.getPurposeCommonContext().getPurposeIdAsUUID(),
            sharedStepsContext.getPurposeCommonContext().getCurrentVersionIdAsUUID()));

        assertThat(httpCallExecutor.getClientResponse().is2xxSuccessful())
            .as("Check GET created purpose response status")
            .withFailMessage("Non è stato possibile reperire la purpose version creata. "
                + "Visionare i log delle chiamate per maggiori dettagli.")
            .isTrue();

        PurposeVersion version = (PurposeVersion) httpCallExecutor.getResponse();
        assertThat(version.getDailyCalls())
            .as("Check purpose version created")
            .isEqualTo(this.newDailyCalls);
    }

}
