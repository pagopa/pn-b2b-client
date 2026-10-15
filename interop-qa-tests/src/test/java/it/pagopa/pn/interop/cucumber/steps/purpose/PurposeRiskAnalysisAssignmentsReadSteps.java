package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.bff.model.Purposes;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisSigningState;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.RiskAnalysisCommonContext.AssignedReviewerActorRef;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

public class PurposeRiskAnalysisAssignmentsReadSteps {
    private final SharedStepsContext sharedStepsContext;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IHttpExecutor httpCallExecutor;
    private List<Purpose> lastAssignmentsResults;

    public PurposeRiskAnalysisAssignmentsReadSteps(
            SharedStepsContext sharedStepsContext,
            ClientTokenConfigurator clientTokenConfigurator
    ) {
        this.sharedStepsContext = sharedStepsContext;
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("interroga l'endpoint delle assegnazioni del valutatore con filtro stato {string}")
    public void queryAssignmentsByState(String state) {
        RiskAnalysisSigningState stateEnum = RiskAnalysisSigningState.valueOf(state);
        queryAssignments(null, null, null, List.of(stateEnum));
    }

    @When("interroga l'endpoint delle assegnazioni del valutatore con parametro offset {int}")
    public void queryAssignmentsByOffset(int offset) {
        queryAssignments(offset, null, null, null);
    }

    @When("interroga l'endpoint delle assegnazioni del valutatore con parametro limit {int}")
    public void queryAssignmentsByLimit(int limit) {
        queryAssignments(null, limit, null, null);
    }

    @When("interroga l'endpoint delle assegnazioni del valutatore filtrando per il primo e-service creato")
    public void queryAssignmentsByFirstEService() {
        UUID firstEServiceId = getScenarioEServiceIds().get(0);
        queryAssignments(null, null, List.of(firstEServiceId), null);
    }

    @When("tenta di interrogare l'endpoint delle assegnazioni del valutatore senza filtri")
    public void tryQueryAssignmentsWithoutFilters() {
        queryAssignments(null, null, null, null);
    }

    @Then("vengono restituite {int} finalità attese nelle assegnazioni")
    public void verifyExpectedPurposesReturned(int expectedCount) {
        assertThat(lastAssignmentsResults)
            .as("La risposta delle assegnazioni non deve essere nulla")
            .isNotNull();

        Set<UUID> expectedPurposeIds = new HashSet<>(getScenarioPurposeIds());
        assertThat(expectedPurposeIds)
                .as("Nel contesto dello scenario devono esistere esattamente %s finalità", expectedCount)
                .hasSize(expectedCount);

        Set<UUID> returnedPurposeIds = lastAssignmentsResults.stream()
                .map(Purpose::getId)
                .collect(Collectors.toSet());

        assertThat(returnedPurposeIds)
                .as("Le finalità restituite devono essere esattamente %s e corrispondere a quelle attese nello scenario", expectedCount)
                .hasSize(expectedCount)
                .containsExactlyInAnyOrderElementsOf(expectedPurposeIds);
    }

    @Then("viene restituita una sola finalità in stato {string} nelle assegnazioni")
    public void verifyOnePurposeByState(String state) {
        assertThat(lastAssignmentsResults)
            .as("La risposta delle assegnazioni non deve essere nulla")
            .isNotNull();

        assertThat(lastAssignmentsResults)
            .as("Deve essere restituita esattamente 1 assegnazione")
            .hasSize(1);

        Purpose returnedPurpose = lastAssignmentsResults.get(0);
        assertThat(returnedPurpose.getReviewerWorkflow())
                .as("La finalità restituita deve contenere il reviewer workflow")
                .isNotNull();
        assertThat(returnedPurpose.getReviewerWorkflow().getSigningState())
                .as("La finalità restituita deve contenere lo stato del reviewer workflow")
                .isNotNull();

        assertThat(returnedPurpose.getReviewerWorkflow().getSigningState().getValue())
                .as("Lo stato del reviewer workflow deve essere %s", state)
                .isEqualTo(state);
    }

    @Then("viene restituita una sola finalità nelle assegnazioni")
    public void verifyOnePurposeReturned() {
        assertThat(lastAssignmentsResults)
            .as("La risposta delle assegnazioni non deve essere nulla")
            .isNotNull();

        assertThat(lastAssignmentsResults)
            .as("Deve essere restituita esattamente 1 assegnazione")
            .hasSize(1);

        Set<UUID> expectedPurposeIds = Set.copyOf(getScenarioPurposeIds());
        assertThat(lastAssignmentsResults.get(0).getId())
                .as("La finalità restituita deve appartenere alle finalità create nello scenario")
                .isIn(expectedPurposeIds);
    }

    @Then("viene restituita una sola finalità associata al primo e-service creato nelle assegnazioni")
    public void verifyOnePurposeByFirstEService() {
        assertThat(lastAssignmentsResults)
            .as("La risposta delle assegnazioni non deve essere nulla")
            .isNotNull();

        assertThat(lastAssignmentsResults)
            .as("Deve essere restituita esattamente 1 assegnazione")
            .hasSize(1);

        UUID firstEServiceId = getScenarioEServiceIds().get(0);

        assertThat(lastAssignmentsResults.get(0).getEservice().getId())
                .as("La finalità restituita deve essere associata al primo e-service creato")
                .isEqualTo(firstEServiceId);
    }

    @Then("la risposta contiene 0 risultati nelle assegnazioni")
    public void verifyEmptyResponse() {
        assertThat(lastAssignmentsResults)
                .as("La risposta delle assegnazioni deve contenere 0 risultati")
                .isNotNull()
                .isEmpty();
    }

    @Then("ai valutatori assegnati risulta correttamente assegnata la finalità")
    public void verifyPurposeAssignedToAllReviewers() {
        List<AssignedReviewerActorRef> reviewers = sharedStepsContext.getRiskAnalysisCommonContext().getAssignedReviewerActors();
        assertThat(reviewers)
                .as("Nel contesto deve essere inizializzato l'elenco dei valutatori assegnati")
                .isNotNull();
        if (reviewers.isEmpty()) {
            return;
        }

        UUID purposeId = UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId());
        String previousToken = sharedStepsContext.getUserToken();
        try {
            clientTokenConfigurator.setBearerToken(previousToken);
            UUID eserviceId = clientTokenConfigurator.getPurposeApiClient().getPurpose(purposeId).getEservice().getId();
            for (AssignedReviewerActorRef reviewer : reviewers) {
                clientTokenConfigurator.setBearerToken(sharedStepsContext.getIdentityService().getToken(
                        reviewer.tenantType(), reviewer.role(), reviewer.index()));
                sharedStepsContext.getPollingService().makePolling(
                        () -> isPurposeAssigned(purposeId, eserviceId),
                        Boolean.TRUE::equals,
                        String.format("La finalità %s dell'e-service %s non risulta assegnata al valutatore (%s,%s,%d)",
                                purposeId, eserviceId, reviewer.tenantType(), reviewer.role(), reviewer.index())
                );
            }
        } finally {
            clientTokenConfigurator.setBearerToken(previousToken);
        }
    }

    @Then("i valutatori rimossi rispetto all'assegnazione iniziale non vedono più la finalità nelle assegnazioni")
    public void verifyRemovedInitialReviewers() {
        List<AssignedReviewerActorRef> initialActors = sharedStepsContext.getRiskAnalysisCommonContext().getInitialReviewerActors();
        assertThat(initialActors).as("Il setup deve memorizzare i reviewer iniziali").isNotNull();
        List<AssignedReviewerActorRef> finalActors = sharedStepsContext.getRiskAnalysisCommonContext().getAssignedReviewerActors();
        assertThat(finalActors).isNotNull();
        verifyPurposeNotAssignedToRemovedReviewers(initialActors.stream()
                .filter(actor -> !finalActors.contains(actor)).toList());
    }

    private void verifyPurposeNotAssignedToRemovedReviewers(List<AssignedReviewerActorRef> removedReviewers) {
        if (removedReviewers.isEmpty()) {
            return; // Nei casi di sola aggiunta non ci sono valutatori rimossi.
        }

        UUID purposeId = UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId());
        String previousToken = sharedStepsContext.getUserToken();
        try {
            clientTokenConfigurator.setBearerToken(previousToken);
            UUID eserviceId = clientTokenConfigurator.getPurposeApiClient().getPurpose(purposeId).getEservice().getId();
            for (AssignedReviewerActorRef reviewer : removedReviewers) {
                clientTokenConfigurator.setBearerToken(sharedStepsContext.getIdentityService().getToken(
                        reviewer.tenantType(), reviewer.role(), reviewer.index()));
                sharedStepsContext.getPollingService().makePolling(
                        () -> isPurposeAssigned(purposeId, eserviceId),
                        Boolean.FALSE::equals,
                        String.format("La finalità %s risulta ancora assegnata al valutatore rimosso %s di %s",
                                purposeId, reviewer.index() + 1, reviewer.tenantType())
                );
            }
        } finally {
            clientTokenConfigurator.setBearerToken(previousToken);
        }
    }

    private boolean isPurposeAssigned(UUID purposeId, UUID eserviceId) {
        int limit = 50;
        for (int offset = 0; ; offset += limit) {
            Purposes assignments = clientTokenConfigurator.getPurposeApiClient().getRiskAnalysisAssignments(
                    offset, limit, List.of(eserviceId), null);
            assertThat(assignments).as("La risposta delle assegnazioni non deve essere nulla").isNotNull();
            List<Purpose> results = assignments.getResults();
            assertThat(results).as("I risultati delle assegnazioni non devono essere nulli").isNotNull();
            if (results.stream().anyMatch(purpose -> purposeId.equals(purpose.getId()))) {
                return true;
            }
            if (results.size() < limit) {
                return false;
            }
        }
    }

    private void queryAssignments(Integer offset, Integer limit, List<UUID> eservicesIds, List<RiskAnalysisSigningState> states) {
        Integer finalOffset = isNull(offset) ? 0 : offset;
        Integer finalLimit = isNull(limit) ? 50 : limit;
        List<UUID> finalEservices = nonNull(eservicesIds) ? eservicesIds : sharedStepsContext.getEServicesCommonContext().getTotalPublishedEServicesIds()
                .stream()
                .map(EServiceDescriptor::getEServiceId)
                .toList();
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(() -> clientTokenConfigurator.getPurposeApiClient().getRiskAnalysisAssignments(
                finalOffset,
                finalLimit,
                finalEservices,
                states
        ));

        Object response = httpCallExecutor.getResponse();
        if (response instanceof Purposes purposesResponse) {
            lastAssignmentsResults = purposesResponse.getResults();
            return;
        }

        lastAssignmentsResults = List.of();
    }


    private List<UUID> getScenarioPurposeIds() {
        List<String> createdPurposeIds = sharedStepsContext.getPurposeCommonContext().getPurposesIds();
        assertThat(createdPurposeIds)
                .as("Nello scenario deve esistere almeno una finalità")
                .isNotEmpty();

        return createdPurposeIds.stream()
                .map(UUID::fromString)
                .toList();
    }

    private List<UUID> getScenarioEServiceIds() {
        List<EServiceDescriptor> createdEServices = sharedStepsContext.getEServicesCommonContext().getPublishedEservicesIds();
        assertThat(createdEServices)
                .as("Nello scenario deve esistere almeno un e-service")
                .isNotEmpty();

        return createdEServices.stream()
                .map(EServiceDescriptor::getEServiceId)
                .collect(Collectors.toList());
    }

}

