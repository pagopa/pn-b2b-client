package it.pagopa.pn.interop.cucumber.steps.m2m.delegate;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.within;

import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.delegate.service.IM2MDelegationClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DelegationState;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.ConsumerDelegation;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.DelegationCommonContext;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.function.Consumer;
import org.assertj.core.api.SoftAssertions;

public class DelegationConsumerStep {
    private final SharedStepsContext sharedStepsContext;
    private final DelegationCommonContext delegationContext;
    private final IHttpExecutor httpCallExecutor;
    private final IM2MDelegationClient delegationClient;

    private ConsumerDelegation consumerDelegation;

    public DelegationConsumerStep(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext) {
        this.sharedStepsContext = sharedStepsContext;
        this.delegationContext = sharedStepsContext.getDelegationCommonContext();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.delegationClient = clientTokenConfigurator.getM2mDelegationClient();
    }

    @When("l'utente tenta di reperire i dettagli della delega in fruizione")
    public void getConsumerDelegation() {
        UUID delegationId = delegationContext.getDelegationId();
        getConsumerDelegation(delegationId);
    }

    @When("l'utente tenta di reperire i dettagli di una delega in fruizione inesistente")
    public void getUnexistentConsumerDelegation() {
        UUID delegationId = UUID.randomUUID();
        getConsumerDelegation(delegationId);
    }

    private void getConsumerDelegation(UUID delegationId) {
        httpCallExecutor.performCall(() -> delegationClient.getConsumerDelegation(delegationId));
        this.consumerDelegation = (ConsumerDelegation) httpCallExecutor.getResponse();
    }

    @When("i dettagli della delega in fruizione sono coerenti con quanto atteso da una delega in stato {delegationState}")
    public void checkProducedDelegation(DelegationState state) {
        Consumer<SoftAssertions> stateCheck = softly ->
            softly.assertThat(consumerDelegation.getState())
                .as("Verifica stato della delega")
                .isEqualTo(state);
        switch (state) {
            case WAITING_FOR_APPROVAL -> assertConsumerDelegation(stateCheck);
            case ACTIVE ->
                assertConsumerDelegation(stateCheck.andThen(softly -> {
                    softly.assertThat(OffsetDateTime.parse(consumerDelegation.getActivatedAt()))
                        .as("Verifica timestamp di attivazione della delega")
                        .isCloseTo(delegationContext.getActivatedAt(), within(5, SECONDS));
                    softly.assertThat(OffsetDateTime.parse(consumerDelegation.getUpdatedAt()))
                        .as("Verifica timestamp di aggiornamento della delega")
                        .isCloseTo(delegationContext.getActivatedAt(), within(5, SECONDS));
                }));
            case REJECTED ->
                assertConsumerDelegation(stateCheck.andThen(softly -> {
                    softly.assertThat(OffsetDateTime.parse(consumerDelegation.getRejectedAt()))
                        .as("Verifica timestamp di rifiuto della delega")
                        .isCloseTo(delegationContext.getRejectedAt(), within(5, SECONDS));
                    softly.assertThat(OffsetDateTime.parse(consumerDelegation.getUpdatedAt()))
                        .as("Verifica timestamp di aggiornamento della delega")
                        .isCloseTo(delegationContext.getRejectedAt(), within(5, SECONDS));
                    softly.assertThat(consumerDelegation.getRejectionReason())
                        .as("Verifica ragione del rifiuto della delega")
                        .isEqualTo(delegationContext.getRejectionReason());
            }));
            case REVOKED ->
                assertConsumerDelegation(stateCheck.andThen(softly -> {
                    softly.assertThat(OffsetDateTime.parse(consumerDelegation.getRevokedAt()))
                        .as("Verifica timestamp di revoca della delega")
                        .isCloseTo(delegationContext.getRevokedAt(), within(5, SECONDS));
                    softly.assertThat(OffsetDateTime.parse(consumerDelegation.getUpdatedAt()))
                        .as("Verifica timestamp di aggiornamento della delega")
                        .isCloseTo(delegationContext.getRevokedAt(), within(5, SECONDS));
            }));
            default -> throw new IllegalArgumentException("Lo stato '%s' non è supportato".formatted(state));
        }

    }

    private void assertConsumerDelegation(Consumer<SoftAssertions> specificAssertions) {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(consumerDelegation)
            .as("Verifica che si abbia a disposizione la delega in fruizione")
            .isNotNull();
        softAssertions.assertThat(consumerDelegation.getDelegatorId())
            .as("Verifica id del delegante")
            .isEqualTo(delegationContext.getDelegatorId());
        softAssertions.assertThat(consumerDelegation.getDelegateId())
            .as("Verifica id del delegato")
            .isEqualTo(delegationContext.getDelegateId());
        softAssertions.assertThat(consumerDelegation.getId())
            .as("Verifica id della delega")
            .isEqualTo(delegationContext.getDelegationId());
        softAssertions.assertThat(consumerDelegation.getEserviceId())
            .as("Verifica id dell'e-service")
            .isEqualTo(sharedStepsContext.getEServicesCommonContext().getEserviceId());
        softAssertions.assertThat(OffsetDateTime.parse(consumerDelegation.getCreatedAt()))
            .as("Verifica timestamp di creazione della delega")
            .isCloseTo(delegationContext.getCreatedAt(), within(5, SECONDS));
        softAssertions.assertThat(OffsetDateTime.parse(consumerDelegation.getSubmittedAt()))
            .as("Verifica timestamp di sottomissione della delega")
            .isCloseTo(delegationContext.getCreatedAt(), within(5, SECONDS));
        specificAssertions.accept(softAssertions);
        softAssertions.assertAll();
    }
}
