package it.pagopa.pn.interop.cucumber.steps.m2m.agreement;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.ObjectUtils.allNull;
import static org.apache.commons.lang3.ObjectUtils.anyNull;
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
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Agreement;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementState;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Agreements;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DelegationRef;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Document;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Documents;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purposes;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.DocumentMetadata;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.M2MDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.agreement.utils.AgreementResolver;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

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
    private final AgreementResolver agreementResolver;

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
        this.agreementResolver = new AgreementResolver(sharedStepsContext);
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

        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            Agreements res = (Agreements) httpCallExecutor.getResponse();
            sharedStepsContext.getAgreementCommonContext().setAgreementIds(
                    res.getResults().stream()
                            .map(Agreement::getId)
                            .collect(toList()));
        }
    }

    @Then("sono stati visualizzati correttamente {int} agreements creati")
    public void agreementsSuccessfullyGot(int expectedSize) {
        HttpStatus clientResponse = httpCallExecutor.getResponseStatus();
        if (clientResponse.isError()) {
            Assertions.fail("Agreements list request failed: ", clientResponse);
        }

        Agreements agreements = (Agreements) httpCallExecutor.getResponse();
        List<UUID> visualizedIds = agreements.getResults().stream().map(Agreement::getId).toList();
        List<UUID> createdIds = sharedStepsContext.getAgreementCommonContext().getAgreementIds();
        assertSoftly(softly -> {
            softly.assertThat(visualizedIds).hasSize(expectedSize);
            softly.assertThat(createdIds).containsAll(visualizedIds);
        });
    }

    private void verificaStatoRecuperoAgreements(boolean successoAtteso) {
        List<UUID> agreementIds = this.sharedStepsContext.getAgreementCommonContext()
                .getAgreementIds();

        String assertDescription = "Check cardinalità agreements risultanti";
        if (successoAtteso) assertThat(agreementIds).as(assertDescription).isNotEmpty();
        else assertThat(agreementIds).as(assertDescription).isEmpty();
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

        sharedStepsContext.getAgreementCommonContext().setAgreementId(agreementId);
    }

    @When("l'utente m2m richiede una operazione di approvazione della richiesta di fruizione con id {string}")
    public void m2mUserRequiresAgreementApprovalWithId(String agreementId) {
        UUID resolvedAgreementId = agreementResolver.resolveAgreementId(agreementId);
        httpCallExecutor.performCall(() -> agreementClient.approveAgreement(resolvedAgreementId));
    }

    @When("l'utente m2m richiede una operazione di approvazione della richiesta di fruizione con id {string} e delegationId {string}")
    public void m2mUserRequiresAgreementApprovalWithIdAndDelegationId(String agreementId, String delegationId) {
        UUID resolvedAgreementId = agreementResolver.resolveAgreementId(agreementId);
        DelegationRef delegationRef = new DelegationRef().delegationId(agreementResolver.resolveDelegationId(delegationId));
        httpCallExecutor.performCall(() -> agreementClient.approveAgreement(resolvedAgreementId, delegationRef));
    }

    @When("l'utente m2m richiede una operazione di riattivazione della richiesta di fruizione con id {string}")
    public void m2mUserRequiresAgreementUnsuspensionWithId(String agreementId) {
        UUID resolvedAgreementId = agreementResolver.resolveAgreementId(agreementId);
        httpCallExecutor.performCall(() -> agreementClient.unsuspendAgreement(resolvedAgreementId));
    }

    @When("l'utente m2m richiede una operazione di riattivazione della richiesta di fruizione con id {string} e delegationId {string}")
    public void m2mUserRequiresAgreementUnsuspensionWithIdAndDelegationId(String agreementId, String delegationId) {
        UUID resolvedAgreementId = agreementResolver.resolveAgreementId(agreementId);
        DelegationRef delegationRef = new DelegationRef().delegationId(agreementResolver.resolveDelegationId(delegationId));
        httpCallExecutor.performCall(() -> agreementClient.unsuspendAgreement(resolvedAgreementId, delegationRef));
    }

    @Then("la richiesta di fruizione si trova in stato {string}")
    public void m2mAgreementStateCheck(String agreementState) {
        AgreementState expectedState = AgreementState.fromValue(agreementState);
        verifyAgreementState(
                expectedState,
                "La richiesta di fruizione m2m non si trova in stato " + agreementState
        );
    }

    private void verifyAgreementState(AgreementState expectedState, String errorMessage) {

        UUID agreementId = sharedStepsContext.getAgreementId();
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(() -> agreementClient.getAgreementById(agreementId)),
                status -> status.is2xxSuccessful() &&
                        ((Agreement) httpCallExecutor.getResponse()).getState().equals(expectedState) &&
                        ((Agreement) httpCallExecutor.getResponse()).getId().equals(agreementId),
                errorMessage
        );
    }

    @When("l'utente tenta di ottenere la lista delle finalità correlate alla richiesta di fruizione")
    public void agreementPurposes() {
        UUID agreementId = sharedStepsContext.getAgreementCommonContext().getAgreementId();
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(() -> agreementClient.getAgreementPurposes(agreementId)),
                status -> status.is2xxSuccessful() && !((Purposes) httpCallExecutor.getResponse()).getResults().isEmpty(),
                "GET Purposes related to agreements gone wrong: error response OR empty result");
    }

    @When("l'utente tenta di ottenere la lista delle finalità correlate a una richiesta di fruizione inesistente")
    public void nonExistentAgreementPurposes() {
        UUID agreementId = UUID.randomUUID();
        httpCallExecutor.performCall(() -> agreementClient.getAgreementPurposes(agreementId));
    }

    @Then("le finalità vengono correttamente visualizzate")
    public void agreementPurposedVisualized() {
        List<Purpose> returnedPurposes = ((Purposes) httpCallExecutor.getResponse()).getResults();
        List<PurposeSeed> createdPurposes = sharedStepsContext.getPurposeCommonContext().getCreatedPurposes();
        Predicate<Purpose> oneOfCreated = purpose -> createdPurposes.stream().anyMatch(created -> areConsistent(created, purpose));
        assertThat(returnedPurposes)
                .isNotEmpty()
                .allMatch(oneOfCreated, "each returned purpose match at least one created purpose");
    }

    private boolean areConsistent(PurposeSeed createdPurpose, Purpose returnedPurpose) {
        return allNull(createdPurpose, returnedPurpose) ||
                Objects.equals(createdPurpose.getIsFreeOfCharge(), returnedPurpose.getIsFreeOfCharge()) &&
                        Objects.equals(createdPurpose.getConsumerId(), returnedPurpose.getConsumerId()) &&
                        Objects.equals(createdPurpose.getDescription(), returnedPurpose.getDescription()) &&
                        Objects.equals(createdPurpose.getTitle(), returnedPurpose.getTitle()) &&
                        Objects.equals(createdPurpose.getEserviceId(), returnedPurpose.getEserviceId()) &&
                        Objects.equals(createdPurpose.getFreeOfChargeReason(), returnedPurpose.getFreeOfChargeReason());
    }

    @When("l'utente tenta di ottenere la lista dei documenti correlati alla richiesta di fruizione")
    public void agreementDocuments() {
        UUID agreementId = sharedStepsContext.getAgreementCommonContext().getAgreementId();
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(() -> agreementClient.getConsumerDocuments(agreementId)),
                status -> status.is2xxSuccessful() && !((Documents) httpCallExecutor.getResponse()).getResults().isEmpty(),
                "GET consumer documents related to agreements gone wrong: error response OR empty result");
    }

    @When("l'utente tenta di ottenere la lista dei documenti correlati a una richiesta di fruizione inesistente")
    public void nonExistentAgreementDocuments() {
        UUID agreementId = UUID.randomUUID();
        httpCallExecutor.performCall(() -> agreementClient.getConsumerDocuments(agreementId));
    }

    @Then("i documenti vengono correttamente visualizzati")
    public void agreementDocumentsVisualized() {
        List<Document> returnedDocuments = ((Documents) httpCallExecutor.getResponse()).getResults();
        List<DocumentMetadata> createdDocuments = sharedStepsContext.getAgreementCommonContext().getDocumentMetadata();
        Predicate<Document> oneOfCreated = document -> createdDocuments.stream().anyMatch(created -> areConsistent(created, document));
        assertSoftly(softly -> {
            softly.assertThat(returnedDocuments)
                    .as("Verifica coerenza tra metadati dei documenti ottenuti e quelli precedentemente caricati")
                    .isNotEmpty()
                    .allMatch(oneOfCreated, "each returned document match at least one created document");

            List<UUID> returnedIds = returnedDocuments.stream().map(Document::getId).toList();
            softly.assertThat(returnedIds)
                    .as("Verifica che tutti gli id di documenti ottenuti siano valorizzati")
                    .doesNotContainNull();

            List<String> returnedContentTypes = returnedDocuments.stream().map(Document::getContentType).toList();
            softly.assertThat(returnedContentTypes)
                    .as("Verifica che tutti i campi 'contentType' ottenuti siano valorizzati")
                    .doesNotContainNull();
        });
    }

    private boolean areConsistent(DocumentMetadata createdDocument, Document returnedDocument) {
        return allNull(createdDocument, returnedDocument) ||
                Objects.equals(createdDocument.getName(), returnedDocument.getName()) &&
                        Objects.equals(createdDocument.getPrettyName(), returnedDocument.getPrettyName()) &&
                        areInRange(createdDocument.getCreatedAt(), OffsetDateTime.parse(returnedDocument.getCreatedAt()), 1);
    }

    private static boolean areInRange(OffsetDateTime x, OffsetDateTime y, int minutes) {
        OffsetDateTime upperBound = x.plusMinutes(minutes);
        OffsetDateTime lowerBound = x.minusMinutes(minutes);
        return !anyNull(x, y) || x.equals(y) ||
                y.isAfter(lowerBound) && y.isBefore(upperBound);
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
        sharedStepsContext.getAgreementCommonContext().setAgreementId(agreementId);
    }*/

}
