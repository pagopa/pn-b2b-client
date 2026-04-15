package it.pagopa.interop.event.service;

import it.pagopa.interop.common.client.AbstractClient;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.event.domain.dto.*;
import it.pagopa.interop.event.domain.dto.events.*;
import it.pagopa.interop.event.domain.request.M2MAgreementEventRequest;
import it.pagopa.interop.event.domain.request.M2MEserviceEventRequest;
import it.pagopa.interop.event.domain.request.M2MEventRequest;
import it.pagopa.interop.event.domain.request.M2MPurposeEventRequest;
import it.pagopa.interop.event.enums.InteropEvent;
import it.pagopa.interop.event.filter.EventPredicate;
import it.pagopa.interop.event.mapper.M2MEventMapper;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.api.EventsApi;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Primary
public class M2MEventClientImpl extends AbstractClient implements IM2MEventClient{
    private final Map<String, Map<InteropEvent, M2MEvents>> tenantEventCache = new HashMap<>();

    private final EventsApi eventsApi;
    private final RestTemplate restTemplate;
    private final String basePath;
    private final M2MEventMapper mapper;

    public M2MEventClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs, M2MEventMapper mapper) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getM2mBaseUrl();
        this.eventsApi = new EventsApi(createApiClient("dummyBearer"));
        this.mapper = mapper;
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public M2MEServiceEvents getEServicesEvents(M2MEserviceEventRequest request) {
        return performOperation(
                () -> eventsApi.getEServicesEventsWithHttpInfo(
                        request.getLimit(),
                        request.getDelegationId(),
                        request.getLastEventId()
                ))
                .map(mapper::map)
                .map(events -> {
                    List<M2MEserviceEvent> filtered = events.getEvents().stream()
                            .filter(event -> request.getEvent() != null &&
                                    event.getEventType().equals(request.getEvent().name()))
                            .toList();

                    events.setEvents(filtered);
                    return events;
                })
                .orElseThrow(() -> new IllegalStateException(httpCallExecutor.getErrorMessage()));
    }

    @Override
    public M2MEServiceEvents getAllEServicesEvents(M2MEserviceEventRequest request) throws RestClientException {
        return (M2MEServiceEvents) getAllCached(request, this::getEServicesEvents);
    }

    @Override
    public M2MEServiceTemplateEvents getEServiceTemplateEvents(M2MEventRequest request) throws RestClientException {
        return performOperation(
                () -> eventsApi.getEServiceTemplateEventsWithHttpInfo(
                        request.getLimit(),
                        request.getLastEventId()
                ))
                .map(mapper::map)
                .map(events -> {
                    List<M2MEServiceTemplateEvent> filtered = events.getEvents().stream()
                            .filter(event -> request.getEvent() != null &&
                                    event.getEventType().equals(request.getEvent().name()))
                            .toList();

                    events.setEvents(filtered);
                    return events;
                })
                .orElseThrow(() -> new IllegalStateException(httpCallExecutor.getErrorMessage()));
    }

    @Override
    public M2MEServiceTemplateEvents getAllEServiceTemplateEvents(M2MEventRequest request) throws RestClientException {
        return (M2MEServiceTemplateEvents) getAllCached(request, this::getEServiceTemplateEvents);
    }

    @Override
    public M2MConsumerDelegationEvents getConsumerDelegationEvents(M2MEventRequest request) throws RestClientException {
        return performOperation(
                () -> eventsApi.getConsumerDelegationEventsWithHttpInfo(
                        request.getLimit(),
                        request.getLastEventId()
                ))
                .map(mapper::map)
                .map(events -> {
                    List<M2MConsumerDelegationEvent> filtered = events.getEvents().stream()
                            .filter(event -> request.getEvent() != null &&
                                    event.getEventType().equals(request.getEvent().name()))
                            .toList();

                    events.setEvents(filtered);
                    return events;
                })
                .orElseThrow(() -> new IllegalStateException(httpCallExecutor.getErrorMessage()));
    }

    @Override
    public M2MConsumerDelegationEvents getAllConsumerDelegationEvents(M2MEventRequest request) throws RestClientException {
        return (M2MConsumerDelegationEvents) getAllCached(request, this::getConsumerDelegationEvents);
    }

    @Override
    public M2MClientEvents getClientEvents(M2MEventRequest request) throws RestClientException {
        return performOperation(
                () -> eventsApi.getClientEventsWithHttpInfo(
                        request.getLimit(),
                        request.getLastEventId()
                ))
                .map(mapper::map)
                .map(events -> {
                    List<M2MClientEvent> filtered = events.getEvents().stream()
                            .filter(event -> request.getEvent() != null &&
                                    event.getEventType().equals(request.getEvent().name()))
                            .toList();

                    events.setEvents(filtered);
                    return events;
                })
                .orElseThrow(() -> new IllegalStateException(httpCallExecutor.getErrorMessage()));
    }

    @Override
    public M2MClientEvents getAllClientEvents(M2MEventRequest request) throws RestClientException {
        return (M2MClientEvents) getAllCached(request, this::getClientEvents);
    }

    @Override
    public M2MAttributeEvents getAttributesEvents(M2MEventRequest request) throws RestClientException {
        return performOperation(
                () -> eventsApi.getAttributesEventsWithHttpInfo(
                        request.getLimit(),
                        request.getLastEventId()
                ))
                .map(mapper::map)
                .map(events -> {
                    List<M2MAttributeEvent> filtered = events.getEvents().stream()
                            .filter(event -> request.getEvent() != null &&
                                    event.getEventType().equals(request.getEvent().name()))
                            .toList();

                    events.setEvents(filtered);
                    return events;
                })
                .orElseThrow(() -> new IllegalStateException(httpCallExecutor.getErrorMessage()));
    }

    @Override
    public M2MAttributeEvents getAllAttributesEvents(M2MEventRequest request) throws RestClientException {
        return (M2MAttributeEvents) getAllCached(request, this::getAttributesEvents);
    }


    @Override
    public M2MAgreementEvents getAgreementsEvents(M2MAgreementEventRequest request) throws RestClientException {
        return performOperation(
                () -> eventsApi.getAgreementsEventsWithHttpInfo(
                        request.getLimit(),
                        request.getLastEventId(),
                        request.getDelegationId()
                ))
                .map(mapper::map)
                .map(events -> {
                    List<M2MAgreementEvent> filtered = events.getEvents().stream()
                            .filter(event -> request.getEvent() != null &&
                                    event.getEventType().equals(request.getEvent().name()))
                            .toList();

                    events.setEvents(filtered);
                    return events;
                })
                .orElseThrow(() -> new IllegalStateException(httpCallExecutor.getErrorMessage()));
    }

    @Override
    public M2MAgreementEvents getAllAgreementsEvents(M2MAgreementEventRequest request) throws RestClientException {
        return (M2MAgreementEvents) getAllCached(request, this::getAgreementsEvents);
    }

    @Override
    public M2MKeyEvents getKeyEvents(M2MEventRequest request) throws RestClientException {
        return performOperation(
                () -> eventsApi.getKeyEventsWithHttpInfo(
                        request.getLimit(),
                        request.getLastEventId()
                ))
                .map(mapper::map)
                .map(events -> {
                    List<M2MKeyEvent> filtered = events.getEvents().stream()
                            .filter(event -> request.getEvent() != null &&
                                    event.getEventType().equals(request.getEvent().name()))
                            .toList();

                    events.setEvents(filtered);
                    return events;
                })
                .orElseThrow(() -> new IllegalStateException(httpCallExecutor.getErrorMessage()));
    }

    @Override
    public M2MKeyEvents getAllKeyEvents(M2MEventRequest request) throws RestClientException {
        return (M2MKeyEvents) getAllCached(request, this::getKeyEvents);
    }

    @Override
    public M2MProducerDelegationEvents getProducerDelegationEvents(M2MEventRequest request) throws RestClientException {
        return performOperation(
                () -> eventsApi.getProducerDelegationEventsWithHttpInfo(
                        request.getLimit(),
                        request.getLastEventId()
                ))
                .map(mapper::map)
                .map(events -> {
                    List<M2MProducerDelegationEvent> filtered = events.getEvents().stream()
                            .filter(event -> request.getEvent() != null &&
                                    event.getEventType().equals(request.getEvent().name()))
                            .toList();

                    events.setEvents(filtered);
                    return events;
                })
                .orElseThrow(() -> new IllegalStateException(httpCallExecutor.getErrorMessage()));
    }

    @Override
    public M2MProducerDelegationEvents getAllProducerDelegationEvents(M2MEventRequest request) throws RestClientException {
        return (M2MProducerDelegationEvents) getAllCached(request, this::getProducerDelegationEvents);
    }

    @Override
    public M2MProducerKeyEvents getProducerKeyEvents(M2MEventRequest request) throws RestClientException {
        return performOperation(
                () -> eventsApi.getProducerKeyEventsWithHttpInfo(
                        request.getLimit(),
                        request.getLastEventId()
                ))
                .map(mapper::map)
                .map(events -> {
                    List<M2MProducerKeyEvent> filtered = events.getEvents().stream()
                            .filter(event -> request.getEvent() != null &&
                                    event.getEventType().equals(request.getEvent().name()))
                            .toList();

                    events.setEvents(filtered);
                    return events;
                })
                .orElseThrow(() -> new IllegalStateException(httpCallExecutor.getErrorMessage()));
    }

    @Override
    public M2MProducerKeyEvents getAllProducerKeyEvents(M2MEventRequest request) throws RestClientException {
        return (M2MProducerKeyEvents) getAllCached(request, this::getProducerKeyEvents);
    }

    @Override
    public M2MProducerKeychainEvents getProducerKeychainEvents(M2MEventRequest request) throws RestClientException {
        return performOperation(
                () -> eventsApi.getProducerKeychainEventsWithHttpInfo(
                        request.getLimit(),
                        request.getLastEventId()
                ))
                .map(mapper::map)
                .map(events -> {
                    List<M2MProducerKeychainEvent> filtered = events.getEvents().stream()
                            .filter(event -> request.getEvent() != null &&
                                    event.getEventType().equals(request.getEvent().name()))
                            .toList();

                    events.setEvents(filtered);
                    return events;
                })
                .orElseThrow(() -> new IllegalStateException(httpCallExecutor.getErrorMessage()));
    }

    @Override
    public M2MProducerKeychainEvents getAllProducerKeychainEvents(M2MEventRequest request) throws RestClientException {
        return (M2MProducerKeychainEvents) getAllCached(request, this::getProducerKeychainEvents);
    }

    @Override
    public M2MPurposeEvents getPurposeEvents(M2MPurposeEventRequest request) throws RestClientException {
        return performOperation(
                () -> eventsApi.getPurposeEventsWithHttpInfo(
                        request.getLimit(),
                        request.getLastEventId(),
                        request.getDelegationId()
                ))
                .map(mapper::map)
                .map(events -> {
                    List<M2MPurposeEvent> filtered = events.getEvents().stream()
                            .filter(event -> request.getEvent() != null &&
                                    event.getEventType().equals(request.getEvent().name()))
                            .toList();

                    events.setEvents(filtered);
                    return events;
                })
                .orElseThrow(() -> new IllegalStateException(httpCallExecutor.getErrorMessage()));
    }

    @Override
    public M2MPurposeEvents getAllPurposeEvents(M2MPurposeEventRequest request) throws RestClientException {
        return (M2MPurposeEvents) getAllCached(request, this::getPurposeEvents);
    }

    @Override
    public M2MTenantEvents getTenantEvents(M2MEventRequest request) throws RestClientException {
        return performOperation(
                () -> eventsApi.getTenantEventsWithHttpInfo(
                        request.getLimit(),
                        request.getLastEventId()
                ))
                .map(mapper::map)
                .map(events -> {
                    List<M2MTenantEvent> filtered = events.getEvents().stream()
                            .filter(event -> request.getEvent() != null &&
                                    event.getEventType().equals(request.getEvent().name()))
                            .toList();

                    events.setEvents(filtered);
                    return events;
                })
                .orElseThrow(() -> new IllegalStateException(httpCallExecutor.getErrorMessage()));
    }

    @Override
    public M2MTenantEvents getAllTenantEvents(M2MEventRequest request) throws RestClientException {
        return (M2MTenantEvents) getAllCached(request, this::getTenantEvents);
    }

    @Override
    public M2MPurposeTemplateEvents getPurposeTemplateEvents(M2MEventRequest request) {
        throw new RestClientException("Gli eventi PurposeTemplate non sono gestite dal gateway M2M V2");
    }

    @Override
    public M2MPurposeTemplateEvents getAllPurposeTemplateEvents(M2MEventRequest request) {
        throw new RestClientException("Gli eventi PurposeTemplate non sono gestite dal gateway M2M V2");
    }

    @Override
    public Optional<M2MEvent> findEvent(M2MEventRequest request, EventPredicate filter) {
        M2MEvents events = getEvents(request);
        return Optional.ofNullable(events.filter(filter));
    }

    @Override
    public M2MEvents findEvents(M2MEventRequest request, EventPredicate filter) {
        M2MEvents events = getEvents(request);
        var filtered = events.getEvents().stream().filter(filter).collect(Collectors.toCollection(ArrayList::new));
        events.setEvents(filtered);
        return events;
    }

    @Override
    public M2MEvents getEvents(M2MEventRequest request) throws RestClientException {
        return switch (request.getEvent().getFamily()) {
            case PURPOSE_TEMPLATE -> throw new RestClientException("Gli eventi PurposeTemplate non sono gestite dal gateway M2M V2");
            case ESERVICE -> getAllEServicesEvents(M2MEserviceEventRequest.from(request));
            case ESERVICE_TEMPLATE -> getAllEServiceTemplateEvents(request);
            case CONSUMER_DELEGATION -> getAllConsumerDelegationEvents(request);
            case CLIENT -> getAllClientEvents(request);
            case ATTRIBUTE -> getAllAttributesEvents(request);
            case AGREEMENT -> getAllAgreementsEvents(M2MAgreementEventRequest.from(request));
            case KEY -> getAllKeyEvents(request);
            case PRODUCER_DELEGATION -> getAllProducerDelegationEvents(request);
            case PRODUCER_KEY -> getAllProducerKeyEvents(request);
            case PRODUCER_KEYCHAIN -> getAllProducerKeychainEvents(request);
            case PURPOSE -> getAllPurposeEvents(M2MPurposeEventRequest.from(request));
            case TENANT -> getAllTenantEvents(request);
        };
    }

    private <Request extends M2MEventRequest> M2MEvents getAllCached(Request request, Function<Request, M2MEvents> fetchPage) throws RestClientException {
        Objects.requireNonNull(request, "request cannot be null");
        Objects.requireNonNull(fetchPage, "fetchPage cannot be null");

        if (request.getTenantType() == null) {
            throw new IllegalArgumentException("request.tenantType cannot be null");
        }
        if (request.getEvent() == null) {
            throw new IllegalArgumentException("request.eventFamily cannot be null");
        }

        Map<InteropEvent, M2MEvents> tenantCache =
                tenantEventCache.computeIfAbsent(request.getTenantType(), t -> new HashMap<>());

        M2MEvents cachedEvents =
                tenantCache.computeIfAbsent(request.getEvent(), this::createEmptyEvents);

        UUID lastEventId = cachedEvents.getLastEvent() != null
                ? cachedEvents.getLastEvent().getId()
                : request.getLastEventId();
        Integer limit = request.getLimit() != null ?  request.getLimit() : M2MEventRequest.EVENTS_MAX_LIMIT;

        request.setLastEventId(lastEventId);
        request.setLimit(limit);

        while (true) {
            M2MEvents page = fetchPage.apply(request);
            if (!hasEvents(page)) {
                return cachedEvents;
            }

            cachedEvents.addEvents(page);

            UUID nextLastEventId = page.getLastEvent() != null
                    ? page.getLastEvent().getId()
                    : null;

            if (nextLastEventId == null) {
                return cachedEvents;
            }

            request.setLastEventId(nextLastEventId);
        }
    }

    private M2MEvents createEmptyEvents(InteropEvent event) {
        return switch (event.getFamily()) {
            case PURPOSE_TEMPLATE -> new M2MPurposeTemplateEvents();
            case ESERVICE -> new M2MEServiceEvents();
            case ESERVICE_TEMPLATE -> new M2MEServiceTemplateEvents();
            case CONSUMER_DELEGATION -> new M2MConsumerDelegationEvents();
            case CLIENT -> new M2MClientEvents();
            case ATTRIBUTE -> new M2MAttributeEvents();
            case AGREEMENT -> new M2MAgreementEvents();
            case KEY -> new M2MKeyEvents();
            case PRODUCER_DELEGATION -> new M2MProducerDelegationEvents();
            case PRODUCER_KEY -> new M2MProducerKeyEvents();
            case PRODUCER_KEYCHAIN -> new M2MProducerKeychainEvents();
            case PURPOSE -> new M2MPurposeEvents();
            case TENANT -> new M2MTenantEvents();
        };
    }

    private boolean hasEvents(M2MEvents events) {
        return events != null && events.getEvents() != null && !events.getEvents().isEmpty();
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.eventsApi.setApiClient(createApiClient(bearerToken));
    }


}