package it.pagopa.pn.interop.cucumber.steps.m2m.delegate;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.within;

import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.delegate.service.IM2MDelegationClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DelegationState;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.ProducerDelegation;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.DelegationCommonContext;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.function.Consumer;
import org.assertj.core.api.SoftAssertions;

public class DelegationProducerStep {
    private final SharedStepsContext sharedStepsContext;
    private final DelegationCommonContext delegationContext;
    private final IHttpExecutor httpCallExecutor;
    private final IM2MDelegationClient delegationClient;

    private ProducerDelegation producerDelegation;

    public DelegationProducerStep(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext) {
        this.sharedStepsContext = sharedStepsContext;
        this.delegationContext = sharedStepsContext.getDelegationCommonContext();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.delegationClient = clientTokenConfigurator.getM2mDelegationClient();
    }

    @When("l'utente tenta di reperire i dettagli della delega in erogazione")
    public void getPublisherDelegation() {
        UUID delegationId = delegationContext.getDelegationId();
        getPublisherDelegation(delegationId);
    }

    @When("l'utente tenta di reperire i dettagli di una delega in erogazione inesistente")
    public void getUnexistentPublisherDelegation() {
        UUID delegationId = UUID.randomUUID();
        getPublisherDelegation(delegationId);
    }

    private void getPublisherDelegation(UUID delegationId) {
        httpCallExecutor.performCall(() -> delegationClient.getProducerDelegation(delegationId));
        this.producerDelegation = (ProducerDelegation) httpCallExecutor.getResponse();
    }

    @When("i dettagli della delega in erogazione sono coerenti con quanto atteso da una delega in stato {delegationState}")
    public void checkProducedDelegation(DelegationState state) {
        Consumer<SoftAssertions> stateCheck = softly ->
            softly.assertThat(producerDelegation.getState())
                .as("Verifica stato della delega")
                .isEqualTo(state);
        switch (state) {
            case WAITING_FOR_APPROVAL -> assertProducerDelegation(stateCheck);
            case ACTIVE ->
                assertProducerDelegation(stateCheck.andThen(softly -> {
                    softly.assertThat(OffsetDateTime.parse(producerDelegation.getActivatedAt()))
                        .as("Verifica timestamp di attivazione della delega")
                        .isCloseTo(delegationContext.getActivatedAt(), within(5, SECONDS));
                    softly.assertThat(OffsetDateTime.parse(producerDelegation.getUpdatedAt()))
                        .as("Verifica timestamp di aggiornamento della delega")
                        .isCloseTo(delegationContext.getActivatedAt(), within(5, SECONDS));
                }));
            case REJECTED ->
                assertProducerDelegation(stateCheck.andThen(softly -> {
                    softly.assertThat(OffsetDateTime.parse(producerDelegation.getRejectedAt()))
                        .as("Verifica timestamp di rifiuto della delega")
                        .isCloseTo(delegationContext.getRejectedAt(), within(5, SECONDS));
                    softly.assertThat(OffsetDateTime.parse(producerDelegation.getUpdatedAt()))
                        .as("Verifica timestamp di aggiornamento della delega")
                        .isCloseTo(delegationContext.getRejectedAt(), within(5, SECONDS));
                    softly.assertThat(producerDelegation.getRejectionReason())
                        .as("Verifica ragione del rifiuto della delega")
                        .isEqualTo(delegationContext.getRejectionReason());
            }));
            case REVOKED ->
                assertProducerDelegation(stateCheck.andThen(softly -> {
                    softly.assertThat(OffsetDateTime.parse(producerDelegation.getRevokedAt()))
                        .as("Verifica timestamp di revoca della delega")
                        .isCloseTo(delegationContext.getRevokedAt(), within(5, SECONDS));
                    softly.assertThat(OffsetDateTime.parse(producerDelegation.getUpdatedAt()))
                        .as("Verifica timestamp di aggiornamento della delega")
                        .isCloseTo(delegationContext.getRevokedAt(), within(5, SECONDS));
            }));
            default -> throw new IllegalArgumentException("Lo stato '%s' non è supportato".formatted(state));
        }

    }

    private void assertProducerDelegation(Consumer<SoftAssertions> specificAssertions) {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(producerDelegation)
            .as("Verifica che si abbia a disposizione la delega in erogazione")
            .isNotNull();
        softAssertions.assertThat(producerDelegation.getDelegatorId())
            .as("Verifica id del delegante")
            .isEqualTo(delegationContext.getDelegatorId());
        softAssertions.assertThat(producerDelegation.getDelegateId())
            .as("Verifica id del delegato")
            .isEqualTo(delegationContext.getDelegateId());
        softAssertions.assertThat(producerDelegation.getId())
            .as("Verifica id della delega")
            .isEqualTo(delegationContext.getDelegationId());
        softAssertions.assertThat(producerDelegation.getEserviceId())
            .as("Verifica id dell'e-service")
            .isEqualTo(sharedStepsContext.getEServicesCommonContext().getEserviceId());
        softAssertions.assertThat(OffsetDateTime.parse(producerDelegation.getCreatedAt()))
            .as("Verifica timestamp di creazione della delega")
            .isCloseTo(delegationContext.getCreatedAt(), within(5, SECONDS));
        softAssertions.assertThat(OffsetDateTime.parse(producerDelegation.getSubmittedAt()))
            .as("Verifica timestamp di sottomissione della delega")
            .isCloseTo(delegationContext.getCreatedAt(), within(5, SECONDS));
        specificAssertions.accept(softAssertions);
        softAssertions.assertAll();
    }
}
