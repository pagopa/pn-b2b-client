package it.pagopa.pn.interop.cucumber.steps.m2m.event;

import io.cucumber.java.Before;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.enums.M2MRole;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.event.domain.dto.M2MEvent;
import it.pagopa.interop.event.domain.request.M2MEventRequest;
import it.pagopa.interop.event.enums.InteropEvent;
import it.pagopa.interop.event.filter.EventFilter;
import it.pagopa.interop.event.filter.EventPredicate;
import it.pagopa.interop.event.service.IM2MEventClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.M2MAuthSteps;
import it.pagopa.pn.interop.cucumber.steps.m2m.event.model.EventContext;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.*;

@Slf4j
public class M2MEventsSteps {
    private static final long MIN_INVISIBILITY_MILLIS = 5_000L;
    private static final int POLLING_ATTEMPTS = 24;
    private static final int POLLING_INTERVAL_SECONDS = 2_500;

    private final M2MAuthSteps m2mAuthSteps;
    private final EventContext eventContext;
    private final IM2MEventClient m2mEventClient;

    public M2MEventsSteps(M2MAuthSteps m2mAuthSteps, EventContext eventContext, ClientTokenConfigurator clientTokenConfigurator, SharedStepsContext sharedStepsContext) {
        this.m2mAuthSteps = m2mAuthSteps;
        this.eventContext = eventContext;
        this.m2mEventClient = clientTokenConfigurator.getM2mEventClient();
        IHttpExecutor httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        m2mEventClient.setHttpCallExecutor(httpCallExecutor);
    }

    @Before("@m2m-events or @m2m-purpose-template-events")
    public void initEventClient() {
        m2mEventClient.setReferenceTime(Instant.now());
    }

    @When("{string} {visibilitaEvento} l'evento {interopEvent} con:")
    public void checkEventPresence(String tenantType, Boolean isVisible, InteropEvent event, List<EventPredicate> predicates) {
        m2mAuthSteps.authenticateM2MUser("admin", tenantType, M2MRole.M2M_ADMIN);
        EventPredicate eventPredicate = getPredicate(event, predicates);

        M2MEventRequest eventRequest = M2MEventRequest.builder()
                .tenantType(tenantType)
                .event(event)
                .filter(eventPredicate)
                .build();

        long startTime = System.currentTimeMillis();

        PollingService.makePolling(
                () -> {
                    Optional<M2MEvent> foundEvent = m2mEventClient.findEvent(eventRequest);
                    foundEvent.ifPresent(e -> {
                        log.info("Evento {} trovato: {}", event, e);
                        if (e.getId() == null) {
                            throw new AssertionError("Evento " + event + " trovato ma senza campo 'id' valorizzato");
                        }
                        if (e.getEventTimestamp() == null) {
                            throw new AssertionError("Evento " + event + " trovato ma senza campo 'eventTimestamp' valorizzato");
                        }
                        eventContext.setLastEventMatched(event, e);
                    });
                    return foundEvent;
                },
                optional -> {
                    if (isVisible) {
                        return optional.isPresent();
                    } else {
                        // deve NON comparire per almeno minWait ms
                        long elapsed = System.currentTimeMillis() - startTime;

                        if (optional.isPresent()) {
                            throw new AssertionError("Evento " + event + " non doveva comparire ma è stato trovato");
                        }

                        return elapsed >= MIN_INVISIBILITY_MILLIS;
                    }
                },
                "L'evento " + event + " doveva essere " + (isVisible ? "visibile" : "non visibile"),
                POLLING_ATTEMPTS,
                POLLING_INTERVAL_SECONDS
        );
    }

    @Nonnull
    private static EventPredicate getPredicate(InteropEvent event, List<EventPredicate> predicates) {
        List<EventPredicate> safePredicates = new ArrayList<>(predicates != null ? predicates : Collections.emptyList());

        EventPredicate eventTypePredicate = e -> e.getEventType().equals(event.name());
        safePredicates.add(eventTypePredicate);

        return EventPredicate.andAll(safePredicates);
    }

    @When("{string} {visibilitaEvento} l'evento {interopEvent} precedente")
    @When("{string} {visibilitaEvento} l'evento {interopEvent} appena trovato")
    public void checkBeforeEventPresence(String tenantType, Boolean isVisible, InteropEvent event) {
        M2MEvent lastEvent = Objects.requireNonNull(eventContext.getLastEventMatched(event), "Nessun evento precedente memorizzato");
        EventPredicate filter = EventFilter.builder().like(lastEvent).build();

        List<EventPredicate> predicates = new ArrayList<>();
        predicates.add(filter);

        checkEventPresence(tenantType, isVisible, event, predicates);
    }
}
