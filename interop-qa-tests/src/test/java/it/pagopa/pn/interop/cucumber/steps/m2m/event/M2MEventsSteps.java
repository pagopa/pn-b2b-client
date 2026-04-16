package it.pagopa.pn.interop.cucumber.steps.m2m.event;

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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class M2MEventsSteps {
    private final M2MAuthSteps m2mAuthSteps;
    private final EventContext eventContext;
    private final IM2MEventClient m2mEventClient;
    private final IHttpExecutor httpCallExecutor;

    public M2MEventsSteps(M2MAuthSteps m2mAuthSteps, EventContext eventContext, ClientTokenConfigurator clientTokenConfigurator, SharedStepsContext sharedStepsContext) {
        this.m2mAuthSteps = m2mAuthSteps;
        this.eventContext = eventContext;
        this.m2mEventClient = clientTokenConfigurator.getM2mEventClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        m2mEventClient.setHttpCallExecutor(httpCallExecutor);
    }

    @When("{string} {visibilitaEvento} l'evento {interopEvent} con:")
    public void checkEventPresence(String tenantType, Boolean isVisible, InteropEvent event, List<EventPredicate> predicates) {
        m2mAuthSteps.authenticateM2MUser("admin", tenantType, M2MRole.M2M_ADMIN);

        EventPredicate combined = EventPredicate.andAll(predicates);

        M2MEventRequest eventRequest = M2MEventRequest.builder()
                .tenantType(tenantType)
                .event(event)
                .build();

        long startTime = System.currentTimeMillis();
        long minWait = 5000;

        PollingService.makePolling(
                () -> {
                    Optional<M2MEvent> foundEvent = m2mEventClient.findEvent(eventRequest, combined);
                    foundEvent.ifPresent(e -> {
                        log.info("Evento {} trovato: {}", event, e);
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

                        return elapsed >= minWait;
                    }
                },
                "L'evento " + event + " doveva essere " + (isVisible ? "visibile" : "non visibile"),
                10,
                30000
        );
    }

    @When("{string} {visibilitaEvento} l'evento {interopEvent} precedente")
    @When("{string} {visibilitaEvento} l'evento {interopEvent} appena trovato")
    public void checkBeforeEventPresence(String tenantType, Boolean isVisible, InteropEvent event) {
        M2MEvent lastEvent = Objects.requireNonNull(eventContext.getLastEventMatched(event), "Nessun evento precedente memorizzato");
        EventPredicate filter = EventFilter.builder().like(lastEvent).build();

        checkEventPresence(tenantType, isVisible, event, Collections.singletonList(filter));
    }
}
