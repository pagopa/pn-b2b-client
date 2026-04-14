package it.pagopa.pn.interop.cucumber.steps.m2m.event;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.enums.M2MRole;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.event.domain.dto.M2MEvent;
import it.pagopa.interop.event.domain.request.M2MEventRequest;
import it.pagopa.interop.event.enums.InteropEvent;
import it.pagopa.interop.event.filter.EventFilter;
import it.pagopa.interop.event.filter.EventPredicate;
import it.pagopa.interop.event.service.IM2MV3EventClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.m2m.M2MAuthSteps;
import it.pagopa.pn.interop.cucumber.steps.m2m.event.model.EventContext;
import org.junit.jupiter.api.Assertions;

import java.util.Objects;
import java.util.Optional;

public class M2MEventsSteps {
    private final M2MAuthSteps m2mAuthSteps;
    private final EventContext eventContext;
    private final IM2MV3EventClient m2mEventClient;

    public M2MEventsSteps(M2MAuthSteps m2mAuthSteps, EventContext eventContext, ClientTokenConfigurator clientTokenConfigurator) {
        this.m2mAuthSteps = m2mAuthSteps;
        this.eventContext = eventContext;
        this.m2mEventClient = clientTokenConfigurator.getM2mV3EventClient();
    }

    @When("{string} {visibilitaEvento} l'evento {interopEvent} con:")
    public void checkEventPresence(String tenantType, Boolean isVisible, InteropEvent event, EventPredicate filter){
        m2mAuthSteps.authenticateM2MUser("admin", tenantType, M2MRole.M2M_ADMIN);

        M2MEventRequest eventRequest = M2MEventRequest.builder()
                .tenantType(tenantType)
                .eventFamily(event.getFamily())
                .build();

        try {
            PollingService.makePolling(
                    () -> {
                        Optional<M2MEvent> foundEvent = m2mEventClient.findEvent(eventRequest, filter);
                        foundEvent.ifPresent(e -> eventContext.setLastEventMatched(event, e));
                        return foundEvent;
                    },
                    optional -> optional.isPresent() == isVisible,
                    "L'evento " + event + " doveva essere " + (isVisible ? "visibile" : "non visibile") + " ma non è stato trovato entro il timeout previsto.",
                    500,
                    10000
            );
        } catch (PollingPredicateException e){
            // Se l'evento doveva essere visibile, ma si è verificato un timeout, allora fallisce il test
            if(isVisible)
                Assertions.fail("L'evento " + event + " doveva essere visibile ma non è stato trovato entro il timeout previsto.");
            // Altrimenti l'evento non doveva essere visibile, ma si è verificato un timeout, allora ignoro l'eccezione
        }
    }

    @When("{string} {visibilitaEvento} l'evento {interopEvent} precedente")
    @When("{string} {visibilitaEvento} l'evento {interopEvent} appena trovato")
    public void checkBeforeEventPresence(String tenantType, Boolean isVisible, InteropEvent event) {
        M2MEvent lastEvent = Objects.requireNonNull(eventContext.getLastEventMatched(event), "Nessun evento precedente memorizzato");
        EventPredicate filter = EventFilter.builder().like(lastEvent).build();

        checkEventPresence(tenantType, isVisible, event, filter);
    }
}
