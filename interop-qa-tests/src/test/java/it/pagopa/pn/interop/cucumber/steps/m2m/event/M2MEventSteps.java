package it.pagopa.pn.interop.cucumber.steps.m2m.event;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.enums.M2MRole;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.event.domain.M2MEvent;
import it.pagopa.interop.event.domain.M2MEventRequest;
import it.pagopa.interop.event.domain.M2MEvents;
import it.pagopa.interop.event.service.IM2MEventClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.InteropEntityKind;
import it.pagopa.pn.interop.cucumber.utility.delay_service.DelayService;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;
import org.springframework.http.HttpStatus;

@Slf4j
public class M2MEventSteps {
    public static final String E_SERVICE_CREATION_EVENT_TYPE = "ESERVICE_ADDED";

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final IM2MEventClient eventsClient;
    private final IHttpExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final DelayService delayService;

    private UUID lastEventId;

    public M2MEventSteps(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.eventsClient = clientTokenConfigurator.getM2mEventClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.delayService = sharedStepsContext.getDelayService();
    }

    @Given("[{string} prende nota dell'ultimo evento presente di tipo {interopEntityKind}]")
    public void getLastEventId(String tenantType, InteropEntityKind entityKind) {
        String lastToken = clientTokenConfigurator.getLastToken();
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, M2MRole.M2M_ADMIN.toString()));

        M2MEventRequest startingRequest = M2MEventRequest.minimal();
        Function<M2MEventRequest, M2MEvents> eventRetriever = getM2MEventsFunction(entityKind);

        M2MEvent lastEvent = eventRetriever.apply(startingRequest).getLastEvent();
        this.lastEventId = isNull(lastEvent) ? null : lastEvent.getId();

        clientTokenConfigurator.setBearerToken(lastToken);
    }

    @When("{string} visualizza correttamente sia l'evento di creazione che quello di pubblicazione {m2mEventDelegationConfig}")
    public void eServiceCreationAndPublishingEventPresent(String tenantType, M2MDelegationEventConfig delegationConfig) {
        checkCreationEvent(tenantType, delegationConfig);
        checkPublicationEvent(tenantType, delegationConfig);
    }

    @When("{string} visualizza correttamente l'evento di pubblicazione {m2mEventDelegationConfig}")
    public void eServicePublishingEventPresent(String tenantType, M2MDelegationEventConfig delegationConfig) {
        checkPublicationEvent(tenantType, delegationConfig);
    }

    @When("{string} non visualizza l'evento di creazione")
    public void eServiceCreationEventAbsent(String tenantType) {
        String lastToken = clientTokenConfigurator.getLastToken();
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, M2MRole.M2M_ADMIN.toString()));

        Function<M2MEventRequest, M2MEvents> eServiceEventsRetriever = getM2MEventsFunction(InteropEntityKind.E_SERVICE);
        M2MEventRequest eventRequest = M2MEventRequest.of(this.lastEventId);
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();

        delayService.delayForSeconds(10);

        HttpStatus httpStatus = httpCallExecutor.performCall(
            () -> eServiceEventsRetriever.apply(eventRequest));
        assertThat(httpStatus.is2xxSuccessful())
            .as("Verifica che la call al canale di eventi degli e-service sia andata a buon fine")
            .isTrue();

        M2MEvents events = (M2MEvents) httpCallExecutor.getResponse();
        M2MEvents filteredEvents = events.find(E_SERVICE_CREATION_EVENT_TYPE, eServiceId);
        assertThat(filteredEvents.getEvents())
            .as("Verifica che non sia stato restituito alcun evento inerente l'e-service")
            .isEmpty();

        clientTokenConfigurator.setBearerToken(lastToken);
    }

    private void checkCreationEvent(String tenantType, M2MDelegationEventConfig delegationConfig) {
        OffsetDateTime creationTimestamp = sharedStepsContext.getEServicesCommonContext()
            .getCreationTimestamp();

        checkEServiceEvent(tenantType, E_SERVICE_CREATION_EVENT_TYPE, creationTimestamp, delegationConfig);
    }

    private void checkPublicationEvent(String tenantType, M2MDelegationEventConfig delegationConfig) {
        OffsetDateTime publicationTimestamp = sharedStepsContext.getEServicesCommonContext()
            .getPublicationTimestamp();
        String eventType = "ESERVICE_DESCRIPTOR_PUBLISHED";

        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        M2MEventAssertionBuilder eServiceAssertionsBuilder = M2MEventAssertionBuilder.builder()
            .subResourceId(descriptorId);
        checkEServiceEvent(tenantType, eventType, publicationTimestamp, eServiceAssertionsBuilder, delegationConfig);
    }

    private void checkEServiceEvent(String tenantType, String eventType,
        OffsetDateTime publicationTimestamp, M2MDelegationEventConfig delegationConfig) {
        M2MEventAssertionBuilder eServiceAssertionsBuilder = M2MEventAssertionBuilder.builder();
        checkEServiceEvent(tenantType, eventType, publicationTimestamp, eServiceAssertionsBuilder, delegationConfig);
    }

    private void checkEServiceEvent(String tenantType, String eventType, OffsetDateTime publicationTimestamp,
        M2MEventAssertionBuilder eServiceAssertionsBuilder, M2MDelegationEventConfig delegationConfig) {
        String lastToken = clientTokenConfigurator.getLastToken();
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, M2MRole.M2M_ADMIN.toString()));

        Function<M2MEventRequest, M2MEvents> eServiceEventsRetriever = getM2MEventsFunction(InteropEntityKind.E_SERVICE);
        M2MEventRequest eventRequest = M2MEventRequest.of(this.lastEventId);
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        M2MEvents events = this.makePolling(eServiceEventsRetriever, eventRequest, eServiceId,
            eventType);

        M2MEvents filteredEvents = events.find(eventType, eServiceId);
        M2MEventAssertionBuilder builder = eServiceAssertionsBuilder
            .actual(filteredEvents)
            .eventType(eventType)
            .resourceId(eServiceId)
            .creationTimestamp(publicationTimestamp);

        if(delegationConfig.isProducerDelegationActivated()) {
            UUID delegationId = sharedStepsContext.getDelegationCommonContext().getDelegationId();
            builder.producerDelegationId(delegationId);
        }

        SoftAssertions eServiceEventsAssertions = builder
            .build();
        eServiceEventsAssertions.assertAll();

        clientTokenConfigurator.setBearerToken(lastToken);
    }

    private M2MEvents makePolling(Function<M2MEventRequest, M2MEvents> eventRetriever, M2MEventRequest request, UUID resourceId, String eventType) {
        pollingService.makePolling(
            () -> httpCallExecutor.performCall(() -> eventRetriever.apply(request)),
            status -> status.is2xxSuccessful() && ((M2MEvents) httpCallExecutor.getResponse()).getEvents().stream().anyMatch(event-> event.getResourceId().equals(resourceId) && event.getEventType().equals(eventType)),
            "Non è stato rilevato un evento di tipo %s per la risorsa con id %s".formatted(eventType, resourceId));
        return (M2MEvents) httpCallExecutor.getResponse();
    }

    private Function<M2MEventRequest, M2MEvents> getM2MEventsFunction(InteropEntityKind entityKind) {
        return switch (entityKind) {
            case E_SERVICE -> eventsClient::getEServicesEvents;
            case AGREEMENT -> eventsClient::getAgreementsEvents;
            case ATTRIBUTE -> eventsClient::getAttributesEvents;
            case PURPOSE -> eventsClient::getPurposeEvents;
            case TENANT -> eventsClient::getTenantEvents;
            case E_SERVICE_TEMPLATE -> eventsClient::getEServiceTemplateEvents;
            case KEY -> eventsClient::getKeyEvents;
            case CLIENT -> eventsClient::getClientEvents;
            case PRODUCER_KEY -> eventsClient::getProducerKeyEvents;
            case PRODUCER_KEYCHAIN -> eventsClient::getProducerKeychainEvents;
            case PRODUCER_DELEGATION -> eventsClient::getProducerDelegationEvents;
            case CONSUMER_DELEGATION -> eventsClient::getConsumerDelegationEvents;
            default -> throw new IllegalArgumentException("Tipo di evento non supportato");
        };
    }
}
