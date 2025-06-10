package it.pagopa.pn.interop.cucumber.steps.m2m;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeVersion;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeVersionSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeVersions;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purposes;
import it.pagopa.interop.purpose.service.IM2MPurposeClient;
import it.pagopa.interop.purpose.service.IM2MPurposeClient.PurposeVersionsListRequest;
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
    }

    @Then("sono state visualizzate correttamente {int} finalità create")
    public void purposesSuccessfullyGot(int expectedSize) {
        HttpStatus clientResponse = httpCallExecutor.getClientResponse();
        if(clientResponse.isError()) {
            Assertions.fail("Agreements list request failed: ", clientResponse);
        }

        Purposes purposes = (Purposes) httpCallExecutor.getResponse();
        List<UUID> visualizedIds = purposes.getResults().stream().map(Purpose::getId).toList();
        List<UUID> createdIds = sharedStepsContext.getPurposeCommonContext().getPurposesIdsAsUUID();
        assertSoftly(softly -> {
            softly.assertThat(visualizedIds).hasSize(expectedSize);
            softly.assertThat(createdIds).containsAll(visualizedIds);
        }) ;
    }

    @When("l'utente tenta di creare una nuova versione della finalità aggiornando la stima di carico")
    public void createPurposeVersion() {
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

    @When("l'utente crea una nuova versione della finalità con successo aggiornando la stima di carico")
    public void successfullyCreateNewVersion() {
        createPurposeVersion();
        purposeVersionSuccessfullyCreated();
    }

    //@SuppressWarnings("java:S6204")
    @When("l'utente tenta di visualizzare la lista delle versioni della finalità")
    public void purposeVersionsListAttempt() {
        httpCallExecutor.performCall(() -> purposeClient.getVersions(
            PurposeVersionsListRequest.builder()
                .offset(0)
                .limit(20)
                .purposeId(sharedStepsContext.getPurposeCommonContext().getPurposeIdAsUUID())
                .build()
        ));
    }

    @Then("sono state visualizzate correttamente {int} versioni della finalità")
    public void purposeVersionsSuccessfullyGot(int expectedSize) {
        HttpStatus clientResponse = httpCallExecutor.getClientResponse();
        if(clientResponse.isError()) {
            Assertions.fail("Agreements list request failed: ", clientResponse);
        }

        PurposeVersions versions = (PurposeVersions) httpCallExecutor.getResponse();
        List<UUID> visualizedIds = versions.getResults().stream().map(PurposeVersion::getId).toList();
        List<UUID> createdIds = sharedStepsContext.getPurposeCommonContext().getPurposeCurrentVersionsIdsAsUUID();

        assertSoftly(softly -> {
            softly.assertThat(visualizedIds).hasSize(expectedSize);
            softly.assertThat(createdIds).containsAll(visualizedIds);
        }) ;
    }
}
