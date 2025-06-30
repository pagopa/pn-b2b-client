package it.pagopa.pn.interop.cucumber.steps.m2m;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.service.IM2MAgreementClient;
import it.pagopa.interop.agreement.service.IM2MAgreementClient.AgreementsListRequest;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Agreement;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementState;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Agreements;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.M2MDataPreparationService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;

public class AgreementSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final M2MDataPreparationService dataPreparationService;
    private final IM2MAgreementClient agreementClient;
    private final IHttpExecutor httpCallExecutor;
    private final PollingService pollingService;

    public AgreementSteps(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        M2MDataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.dataPreparationService = dataPreparationService;
        this.agreementClient = clientTokenConfigurator.getM2mAgreementClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
    }

    @Given("{string} ha un agreement m2m attivo per ciascun e-service di {string}")
    public void tenantAlreadyHasActiveAgreementForEachEService(String consumer, String producer) {
        List<UUID> agreementsIds = sharedStepsContext.getEServicesCommonContext().getPublishedEservicesIds().stream()
            .map(eServiceDescriptor -> dataPreparationService.createAgreement(eServiceDescriptor.getEServiceId(), eServiceDescriptor.getDescriptorId(), null))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();

        agreementsIds.forEach(agreementId -> dataPreparationService.submitAgreement(agreementId, AgreementState.ACTIVE));
        sharedStepsContext.getAgreementCommonContext().setAgreementIds(agreementsIds);
    }

    @SuppressWarnings("java:S6204")
    @When("l'utente tenta di recuperare una lista di {int} agreements creati")
    public void agreementsListAttempt(int agreementsQuantity) {
        String tenant = sharedStepsContext.getTenantType();
        UUID producerId = identityService.getOrganizationId(tenant);
        httpCallExecutor.performCall(() -> agreementClient.getAgreements(
            AgreementsListRequest.builder()
                .offset(0)
                .limit(agreementsQuantity)
                .producersIds(List.of(producerId))
                .build()
        ));

        if (httpCallExecutor.getClientResponse().is2xxSuccessful()) {
            Agreements res = (Agreements) httpCallExecutor.getResponse();
            sharedStepsContext.getAgreementCommonContext().setAgreementIds(
                res.getResults().stream()
                    .map(Agreement::getId)
                    .collect(toList()));
        }
    }

    @Then("sono stati visualizzati correttamente {int} agreements creati")
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

    private void verificaStatoRecuperoAgreements(boolean successoAtteso) {
        List<UUID> agreementIds = this.sharedStepsContext.getAgreementCommonContext()
            .getAgreementIds();

        String assertDescription = "Check cardinalità agreements risultanti";
        if (successoAtteso) assertThat(agreementIds).as(assertDescription).isNotEmpty();
        else                assertThat(agreementIds).as(assertDescription).isEmpty();
    }

    @And("gli agreements sono stati recuperati correttamente")
    public void agreements_sono_stati_recuperati_correttamente() {
        verificaStatoRecuperoAgreements(true);
    }

    @And("gli agreements non sono stati recuperati correttamente")
    public void agreements_non_sono_stati_recuperati_correttamente() {
        verificaStatoRecuperoAgreements(false);
    }

    @And("viene effettuato la creazione di un agreement con successo")
    public void agreementCreationSuccess() {
        UUID eserviceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();

        UUID agreementId = dataPreparationService.createAndCheckAgreement(eserviceId, descriptorId);

        sharedStepsContext.setAgreementId(agreementId);
    }
        // FIXME
/*
    @Given("{string} ha una richiesta di fruizione in stato {string} per quell'e-service")
    public void tenantAlreadyHasFruitionRequestWithState(String consumer, String agreementState) {
        String token = identityService.getToken(consumer, null);
        tenantAlreadyHasFruitionRequestWithState(agreementState, token, null);
    }

    private void tenantAlreadyHasFruitionRequestWithState(String agreementState, String token, UUID delegationId) {
        clientTokenConfigurator.setBearerToken(token);

        UUID agreementId = dataPreparationService.createAgreementWithGivenState(
            it.pagopa.interop.generated.openapi.clients.bff.model.AgreementState.fromValue(agreementState),
            sharedStepsContext.getEServicesCommonContext().getEserviceId(),
            sharedStepsContext.getEServicesCommonContext().getDescriptorId(),
            delegationId,
            null);
        sharedStepsContext.setAgreementId(agreementId);
    }*/

}
