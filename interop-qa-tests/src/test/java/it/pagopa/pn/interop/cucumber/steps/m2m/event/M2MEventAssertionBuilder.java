package it.pagopa.pn.interop.cucumber.steps.m2m.event;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.within;

import it.pagopa.interop.event.domain.M2MEvent;
import it.pagopa.interop.event.domain.M2MEvents;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.assertj.core.api.SoftAssertions;

@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@Accessors(fluent = true)
@Setter
public class M2MEventAssertionBuilder {
    private M2MEvents actual;

    private String eventType;
    private UUID resourceId;
    private UUID subResourceId;
    private OffsetDateTime creationTimestamp;
    private UUID producerDelegationId;
    private UUID consumerDelegationId;

    public static M2MEventAssertionBuilder builder() {
        return new M2MEventAssertionBuilder();
    }

    public SoftAssertions build() {
        M2MEvents nullSafeEvents = requireNonNull(this.actual);
        SoftAssertions softAssertions = new SoftAssertions();

        int sizeExpected = 1;
        softAssertions.assertThat(actual.getEvents())
            .as("Verifica che l'evento di tipo '%s' con resource id '%s' sia presente tra gli eventi forniti")
            .hasSize(sizeExpected);
        if (nullSafeEvents.getEvents().size() == sizeExpected) {
            // Check dei campi sempre presenti
            M2MEvent actualEvent = nullSafeEvents.getLastEvent();
            softAssertions.assertThat(actualEvent.getId())
                .as("Verifica che l'evento abbia un id valorizzato")
                .isNotNull();
            softAssertions.assertThat(actualEvent.getEventType())
                .as("Verifica che l'evento sia del tipo previsto")
                .isEqualTo(eventType);
            softAssertions.assertThat(actualEvent.getResourceId())
                .as("Verifica che l'evento abbia un resource-id correttamente valorizzato")
                .isEqualTo(resourceId);
            softAssertions.assertThat(actualEvent.getCreationTimestamp())
                .as("Verifica che l'evento abbia un timestamp coerente con la creazione dell'evento")
                .isCloseTo(creationTimestamp, within(5, SECONDS));

            // Check dei campi potenzialmente assenti
            if (nonNull(subResourceId)) {
                softAssertions.assertThat(actualEvent.getSubResourceId())
                    .as("Verifica che l'evento abbia un sub-resource-id correttamente valorizzato")
                    .isEqualTo(subResourceId);
            } else {
                softAssertions.assertThat(actualEvent.getSubResourceId())
                    .as("Verifica che l'evento NON abbia un sub-resource-id valorizzato")
                    .isNull();
            }

            if (nonNull(producerDelegationId)) {
                softAssertions.assertThat(actualEvent.getProducerDelegationId())
                    .as("Verifica che l'evento abbia un producer-delegation-id correttamente valorizzato")
                    .isEqualTo(producerDelegationId);
            } else {
                softAssertions.assertThat(actualEvent.getProducerDelegationId())
                    .as("Verifica che l'evento NON abbia un producer-delegation-id valorizzato")
                    .isNull();
            }

            if (nonNull(consumerDelegationId)) {
                softAssertions.assertThat(actualEvent.getConsumerDelegationId())
                    .as("Verifica che l'evento abbia un consumer-delegation-id correttamente valorizzato")
                    .isEqualTo(consumerDelegationId);
            } else {
                softAssertions.assertThat(actualEvent.getConsumerDelegationId())
                    .as("Verifica che l'evento NON abbia un consumer-delegation-id valorizzato")
                    .isNull();
            }
        }

        return softAssertions;
    }
}
