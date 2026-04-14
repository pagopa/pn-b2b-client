package it.pagopa.pn.interop.cucumber.steps.m2m.event.purpose_template;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.enums.M2MRole;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.event.queue.M2MEventsQueue;
import it.pagopa.interop.event.queue.purpose_template.PurposeTemplateM2MEvent;
import it.pagopa.pn.interop.cucumber.steps.m2m.M2MAuthSteps;
import it.pagopa.pn.interop.cucumber.steps.m2m.event.model.EventContext;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;

@RequiredArgsConstructor
public class PurposeTemplateEventsSteps {
    private final M2MEventsQueue eventsQueue;
    private final M2MAuthSteps m2mAuthSteps;
    private final EventContext eventContext;

    @When("{string} {visibilitaEvento} l'evento {purposeTemplateInteropEvent} con:")
    public void checkPurposeTemplateEventPresence(String tenantType, Boolean isVisible, PurposeTemplateM2MEvent purposeTemplateEvent){
       m2mAuthSteps.authenticateM2MUser("admin", tenantType, M2MRole.M2M_ADMIN);

       try {
           PollingService.makePolling(
                   () -> {
                       var foundEvent = eventsQueue.find(purposeTemplateEvent);
                       foundEvent.ifPresent(eventContext::setLastPurposeTemplateEventMatched);
                       return foundEvent;
                   },
                   optional -> optional.isPresent() == isVisible,
                   "L'evento " + purposeTemplateEvent.getEventType() + " doveva essere " + (isVisible ? "visibile" : "non visibile") + " ma non è stato trovato entro il timeout previsto.",
                   500,
                   10000
           );
       } catch (PollingPredicateException e){
              // Se l'evento doveva essere visibile, ma si è verificato un timeout, allora fallisce il test
              if(isVisible)
                Assertions.fail("L'evento " + purposeTemplateEvent.getEventType() + " doveva essere visibile ma non è stato trovato entro il timeout previsto.");
              // Altrimenti l'evento non doveva essere visibile, ma si è verificato un timeout, allora ignoro l'eccezione
       }
    }

    @When("{string} {visibilitaEvento} ancora l'evento {purposeTemplateInteropEvent} precedente")
    @When("{string} {visibilitaEvento} ancora l'evento {purposeTemplateInteropEvent} appena trovato")
    public void checkBeforePurposeTemplateEventPresence(String tenantType, Boolean isVisible, PurposeTemplateM2MEvent purposeTemplateEvent) {
        PurposeTemplateM2MEvent eventFilter = eventContext.getLastPurposeTemplateEventMatched();
        if(eventFilter == null) throw new IllegalStateException("Nessun evento precedente memorizzato per " + purposeTemplateEvent.getEventType());

        checkPurposeTemplateEventPresence(tenantType, isVisible, eventFilter);
    }
}
