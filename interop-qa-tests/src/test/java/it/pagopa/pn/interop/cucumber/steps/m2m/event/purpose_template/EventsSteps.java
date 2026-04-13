package it.pagopa.pn.interop.cucumber.steps.m2m.event.purpose_template;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.enums.M2MRole;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.event.queue.M2MEventsQueue;
import it.pagopa.interop.event.queue.purpose_template.PurposeTemplateM2MEvent;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceEvent;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.InteropEvent;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils.TokenResolver;
import it.pagopa.pn.interop.cucumber.steps.m2m.M2MAuthSteps;
import it.pagopa.pn.interop.cucumber.steps.m2m.event.util.RequestMappingUtils;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class EventsSteps {
    private final M2MEventsQueue eventsQueue;
    private final SharedStepsContext sharedStepsContext;
    private final M2MAuthSteps m2mAuthSteps;
    private final TokenResolver tokenResolver;
    private final Map<InteropEvent, Object> eventCache;

    private enum EventFamily {
        PURPOSE_TEMPLATE,
        ESERVICE
    }

    public EventsSteps(M2MEventsQueue eventsQueue, SharedStepsContext sharedStepsContext, M2MAuthSteps m2mAuthSteps) {
        this.eventsQueue = eventsQueue;
        this.sharedStepsContext = sharedStepsContext;
        this.m2mAuthSteps = m2mAuthSteps;
        this.tokenResolver = new TokenResolver(sharedStepsContext);
        this.eventCache = new HashMap<>();
    }

    @When("{string} visualizza correttamente l'evento {interopEvent}")
    public void checkEventPresence(String tenantType, InteropEvent interopEvent) {
        var eventFilter = buildDefaultRequestByEvent(interopEvent);
        var foundEvent = checkEventVisibility(tenantType, eventFilter, true);
        eventCache.put(interopEvent, foundEvent);
    }

    @When("{string} visualizza correttamente l'evento {interopEvent} con:")
    public void checkEventPresenceWith(String tenantType, InteropEvent interopEvent, DataTable dataTable) {
        Map<String, String> customData = dataTable.asMap(String.class, String.class);
        Map<String, String> resolvedCustomData = customData.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> tokenResolver.resolve(entry.getValue())));

        var eventFilter = buildCustomRequestByEvent(interopEvent, resolvedCustomData);
        var foundEvent = checkEventVisibility(tenantType, eventFilter, true);
        eventCache.put(interopEvent, foundEvent);
    }

    @When("{string} visualizza ancora l'evento {interopEvent} precedente")
    @When("{string} visualizza correttamente l'evento {interopEvent} appena trovato")
    public void checkBeforeEventPresence(String tenantType, InteropEvent interopEvent) {
        var eventFilter = eventCache.get(interopEvent);
        if(eventFilter == null) throw new IllegalStateException("Nessun evento precedente memorizzato per " + interopEvent);

        checkEventVisibility(tenantType, eventFilter, true);
    }

    @When("{string} non ha visibilità dell'evento {interopEvent}")
    public void checkEventNotPresence(String tenantType, InteropEvent interopEvent) {
        var eventFilter = buildDefaultRequestByEvent(interopEvent);
        checkEventVisibility(tenantType, eventFilter, false);
    }

    @When("{string} non ha visibilità dell'evento {interopEvent} appena trovato")
    public void checkAfterEventNotPresence(String tenantType, InteropEvent interopEvent) {
        var eventFilter = eventCache.get(interopEvent);
        if(eventFilter == null) throw new IllegalStateException("Nessun evento precedente memorizzato per " + interopEvent);

        checkEventVisibility(tenantType, eventFilter, false);
    }

    private Object checkEventVisibility(String tenantType, Object eventFilter, boolean isVisible) {
        m2mAuthSteps.authenticateM2MUser("admin", tenantType, M2MRole.M2M_ADMIN);
        AtomicReference<Optional<Object>> event = new AtomicReference<>(Optional.empty());

        try {
            PollingService.makePolling(
                    () -> {
                        event.set(eventsQueue.find(eventFilter));
                        return event;
                    },
                    (optional) -> optional.get().isPresent(),
                    "Evento  non presente per il tenant " + tenantType,
                    500,
                    10000
            );
            return event.get().orElse(null);
        } catch (PollingPredicateException exception){
            if(!isVisible) {
                // Se l'evento non è visibile, ci aspettiamo un timeout, quindi ignoriamo l'eccezione
                return null;
            }
            // Se l'evento dovrebbe essere visibile, ma si è verificato un timeout, allora falliamo il test
            Assertions.fail("L'evento doveva essere visibile ma non è stato trovato entro il timeout previsto.");
            return null;
        }
    }

    private Object buildDefaultRequestByEvent(InteropEvent interopEvent) {
        return switch (resolveEventFamily(interopEvent)) {
            case PURPOSE_TEMPLATE -> buildPurposeTemplateRequest(interopEvent);
            case ESERVICE -> buildDefaultEServiceRequest(interopEvent);
        };
    }

    private EServiceEvent buildDefaultEServiceRequest(InteropEvent interopEvent) {
        EServiceEvent request = new EServiceEvent();
        request.setEventType(EServiceEvent.EventTypeEnum.fromValue(interopEvent.getValue()));
        request.setEserviceId(sharedStepsContext.getEServicesCommonContext().getEserviceId());
        request.setDescriptorId(sharedStepsContext.getEServicesCommonContext().getDescriptorId());
        return request;
    }

    private PurposeTemplateM2MEvent buildPurposeTemplateRequest(InteropEvent interopEvent) {
        UUID purposeTemplateId = sharedStepsContext.getPurposeTemplateContext().getPurposeTemplateId();

        PurposeTemplateM2MEvent request = new PurposeTemplateM2MEvent();
        request.setPurposeTemplateId(purposeTemplateId);
        request.setEventType(interopEvent.getValue());

        return request;
    }

    private Object buildCustomRequestByEvent(InteropEvent interopEvent, Map<String, String> customData) {
        return switch (resolveEventFamily(interopEvent)) {
            case PURPOSE_TEMPLATE -> RequestMappingUtils.mapToRequest(new PurposeTemplateM2MEvent(), customData, true);
            case ESERVICE -> RequestMappingUtils.mapToRequest(new EServiceEvent(), customData, true);
        };
    }

    private EventFamily resolveEventFamily(InteropEvent interopEvent) {
        return switch (interopEvent) {
            case PURPOSE_TEMPLATE_PUBLISHED,
                 PURPOSE_TEMPLATE_SUSPENDED,
                 RISK_ANALYSIS_TEMPLATE_DOCUMENT_GENERATED,
                 PURPOSE_TEMPLATE_UNSUSPENDED,
                 PURPOSE_TEMPLATE_ARCHIVED -> EventFamily.PURPOSE_TEMPLATE;

            case ESERVICE_ADDED,
                 ESERVICE_DESCRIPTOR_PUBLISHED,
                 DRAFT_ESERVICE_UPDATED -> EventFamily.ESERVICE;

            default -> throw new IllegalArgumentException("Evento non gestito: " + interopEvent);
        };
    }
}
