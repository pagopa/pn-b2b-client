package it.pagopa.interop.event.service;

import it.pagopa.interop.common.client.AbstractDPoPClient;
import it.pagopa.interop.common.rest_template.DpopRestTemplate;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.event.domain.dto.M2MEvent;
import it.pagopa.interop.event.domain.dto.events.*;
import it.pagopa.interop.event.domain.request.M2MAgreementEventRequest;
import it.pagopa.interop.event.domain.request.M2MEserviceEventRequest;
import it.pagopa.interop.event.domain.request.M2MEventRequest;
import it.pagopa.interop.event.domain.request.M2MPurposeEventRequest;
import it.pagopa.interop.event.enums.InteropEvent;
import it.pagopa.interop.event.filter.EventPredicate;
import it.pagopa.interop.event.mapper.M2MV3EventMapper;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.api.EventsApi;
import it.pagopa.interop.utils.ApiClientUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static it.pagopa.interop.utils.ApiClientUtils.V3_UNSUPPORTED_BEARER_MSG;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class M2MV3EventClientImpl extends AbstractDPoPClient implements IM2MV3EventClient {
    private static final long EVENT_START_TOLERANCE_MINUTES = 5L;

    private final Map<String, Map<InteropEvent, M2MEvents>> tenantEventCache = new HashMap<>();

    private final EventsApi eventsApi;
    private final String basePath;
    private final M2MV3EventMapper mapper;
    private final Instant eventStartTime;

    public M2MV3EventClientImpl(DpopRestTemplate restTemplate, InteropClientConfigs interopClientConfigs, M2MV3EventMapper mapper) {
        super(restTemplate);
        this.basePath = interopClientConfigs.getM2mV3BaseUrl();
        this.eventsApi = new EventsApi(ApiClientUtils.createApiClient(restTemplate, basePath, Collections.emptyMap()));
        this.mapper = mapper;
        this.eventStartTime = Instant.now();
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
                .orElseThrow(() -> new IllegalStateException(httpCallExecutor.getErrorMessage()));
    }

    @Override
    public M2MEServiceEvents getAllEServicesEvents(M2MEserviceEventRequest request) throws RestClientException {
        return (M2MEServiceEvents) getM2MEvents(request, this::getEServicesEvents);
    }

    @Override
    public M2MEServiceTemplateEvents getEServiceTemplateEvents(M2MEventRequest request) throws RestClientException {
        return performOperation(
                () -> eventsApi.getEServiceTemplateEventsWithHttpInfo(
                        request.getLimit(),
                        request.getLastEventId()
                ))
                .map(mapper::map)
                .orElseThrow(() -> new IllegalStateException(httpCallExecutor.getErrorMessage()));
    }

    @Override
    public M2MEServiceTemplateEvents getAllEServiceTemplateEvents(M2MEventRequest request) throws RestClientException {
        return (M2MEServiceTemplateEvents) getM2MEvents(request, this::getEServiceTemplateEvents);
    }

    @Override
    public M2MConsumerDelegationEvents getConsumerDelegationEvents(M2MEventRequest request) throws RestClientException {
        return performOperation(
                () -> eventsApi.getConsumerDelegationEventsWithHttpInfo(
                        request.getLimit(),
                        request.getLastEventId()
                ))
                .map(mapper::map)
                .orElseThrow(() -> new IllegalStateException(httpCallExecutor.getErrorMessage()));
    }

    @Override
    public M2MConsumerDelegationEvents getAllConsumerDelegationEvents(M2MEventRequest request) throws RestClientException {
        return (M2MConsumerDelegationEvents) getM2MEvents(request, this::getConsumerDelegationEvents);
    }

    @Override
    public M2MClientEvents getClientEvents(M2MEventRequest request) throws RestClientException {
        return performOperation(
                () -> eventsApi.getClientEventsWithHttpInfo(
                        request.getLimit(),
                        request.getLastEventId()
                ))
                .map(mapper::map)
                .orElseThrow(() -> new IllegalStateException(httpCallExecutor.getErrorMessage()));
    }

    @Override
    public M2MClientEvents getAllClientEvents(M2MEventRequest request) throws RestClientException {
        return (M2MClientEvents) getM2MEvents(request, this::getClientEvents);
    }

    @Override
    public M2MAttributeEvents getAttributesEvents(M2MEventRequest request) throws RestClientException {
        return performOperation(
                () -> eventsApi.getAttributesEventsWithHttpInfo(
                        request.getLimit(),
                        request.getLastEventId()
                ))
                .map(mapper::map)
                .orElseThrow(() -> new IllegalStateException(httpCallExecutor.getErrorMessage()));
    }

    @Override
    public M2MAttributeEvents getAllAttributesEvents(M2MEventRequest request) throws RestClientException {
        return (M2MAttributeEvents) getM2MEvents(request, this::getAttributesEvents);
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
                .orElseThrow(() -> new IllegalStateException(httpCallExecutor.getErrorMessage()));
    }

    @Override
    public M2MAgreementEvents getAllAgreementsEvents(M2MAgreementEventRequest request) throws RestClientException {
        return (M2MAgreementEvents) getM2MEvents(request, this::getAgreementsEvents);
    }

    @Override
    public M2MKeyEvents getKeyEvents(M2MEventRequest request) throws RestClientException {
        return performOperation(
                () -> eventsApi.getKeyEventsWithHttpInfo(
                        request.getLimit(),
                        request.getLastEventId()
                ))
                .map(mapper::map)
                .orElseThrow(() -> new IllegalStateException(httpCallExecutor.getErrorMessage()));
    }

    @Override
    public M2MKeyEvents getAllKeyEvents(M2MEventRequest request) throws RestClientException {
        return (M2MKeyEvents) getM2MEvents(request, this::getKeyEvents);
    }

    @Override
    public M2MProducerDelegationEvents getProducerDelegationEvents(M2MEventRequest request) throws RestClientException {
        return performOperation(
                () -> eventsApi.getProducerDelegationEventsWithHttpInfo(
                        request.getLimit(),
                        request.getLastEventId()
                ))
                .map(mapper::map)
                .orElseThrow(() -> new IllegalStateException(httpCallExecutor.getErrorMessage()));
    }

    @Override
    public M2MProducerDelegationEvents getAllProducerDelegationEvents(M2MEventRequest request) throws RestClientException {
        return (M2MProducerDelegationEvents) getM2MEvents(request, this::getProducerDelegationEvents);
    }

    @Override
    public M2MProducerKeyEvents getProducerKeyEvents(M2MEventRequest request) throws RestClientException {
        return performOperation(
                () -> eventsApi.getProducerKeyEventsWithHttpInfo(
                        request.getLimit(),
                        request.getLastEventId()
                ))
                .map(mapper::map)
                .orElseThrow(() -> new IllegalStateException(httpCallExecutor.getErrorMessage()));
    }

    @Override
    public M2MProducerKeyEvents getAllProducerKeyEvents(M2MEventRequest request) throws RestClientException {
        return (M2MProducerKeyEvents) getM2MEvents(request, this::getProducerKeyEvents);
    }

    @Override
    public M2MProducerKeychainEvents getProducerKeychainEvents(M2MEventRequest request) throws RestClientException {
        return performOperation(
                () -> eventsApi.getProducerKeychainEventsWithHttpInfo(
                        request.getLimit(),
                        request.getLastEventId()
                ))
                .map(mapper::map)
                .orElseThrow(() -> new IllegalStateException(httpCallExecutor.getErrorMessage()));
    }

    @Override
    public M2MProducerKeychainEvents getAllProducerKeychainEvents(M2MEventRequest request) throws RestClientException {
        return (M2MProducerKeychainEvents) getM2MEvents(request, this::getProducerKeychainEvents);
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
                .orElseThrow(() -> new IllegalStateException(httpCallExecutor.getErrorMessage()));
    }

    @Override
    public M2MPurposeEvents getAllPurposeEvents(M2MPurposeEventRequest request) throws RestClientException {
        return (M2MPurposeEvents) getM2MEvents(request, this::getPurposeEvents);
    }

    @Override
    public M2MTenantEvents getTenantEvents(M2MEventRequest request) throws RestClientException {
        return performOperation(
                () -> eventsApi.getTenantEventsWithHttpInfo(
                        request.getLimit(),
                        request.getLastEventId()
                ))
                .map(mapper::map)
                .orElseThrow(() -> new IllegalStateException(httpCallExecutor.getErrorMessage()));
    }

    @Override
    public M2MTenantEvents getAllTenantEvents(M2MEventRequest request) throws RestClientException {
        return (M2MTenantEvents) getM2MEvents(request, this::getTenantEvents);
    }

    @Override
    public M2MPurposeTemplateEvents getPurposeTemplateEvents(M2MEventRequest request) throws RestClientException {
        return performOperation(
                () -> eventsApi.getPurposeTemplateEventsWithHttpInfo(
                        request.getLimit(),
                        request.getLastEventId()
                ))
                .map(mapper::map)
                .orElseThrow(() -> new IllegalStateException(httpCallExecutor.getErrorMessage()));
    }

    @Override
    public M2MPurposeTemplateEvents getAllPurposeTemplateEvents(M2MEventRequest request) throws RestClientException {
        return (M2MPurposeTemplateEvents) getM2MEvents(request, this::getPurposeTemplateEvents);
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
            case PURPOSE_TEMPLATE -> getAllPurposeTemplateEvents(request);
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

    private <Request extends M2MEventRequest> M2MEvents getM2MEvents(Request request, Function<Request, M2MEvents> fetchPage) throws RestClientException {
        Objects.requireNonNull(request, "request cannot be null");
        Objects.requireNonNull(fetchPage, "fetchPage cannot be null");

        if (request.getTenantType() == null) {
            throw new IllegalArgumentException("request.tenantType cannot be null");
        }
        if (request.getEvent() == null) {
            throw new IllegalArgumentException("request.eventFamily cannot be null");
        }

        return getAllCached(request, fetchPage);
    }

    private <Request extends M2MEventRequest> M2MEvents getAllCached(Request request, Function<Request, M2MEvents> fetchPage) {

        Integer limit = request.getLimit() != null ? request.getLimit() : M2MEventRequest.EVENTS_MAX_LIMIT;
        request.setLimit(limit);

        Map<InteropEvent, M2MEvents> tenantCache =
                tenantEventCache.computeIfAbsent(request.getTenantType(), t -> new HashMap<>());

        M2MEvents cachedEvents =
                tenantCache.computeIfAbsent(request.getEvent(), this::createEmptyEvents);

        UUID lastEventId = request.getLastEventId() != null
                ? request.getLastEventId()
                : ((cachedEvents.getLastEvent() != null && cachedEvents.getLastEvent().getId() != null) ? cachedEvents.getLastEvent().getId() : getFirstEventIdAfterStart(request, fetchPage, cachedEvents));

        request.setLastEventId(lastEventId);


        while (true) {
            M2MEvents page = fetchPage.apply(request);
            if (!hasEvents(page, request.getLimit())) {
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

    private <Request extends M2MEventRequest> UUID getFirstEventIdAfterStart(Request request, Function<Request, M2MEvents> fetchPage, M2MEvents cachedEvents) {
        M2MEvents page;
        UUID lastEventId;
        Instant threshold = eventStartTime.minusSeconds(EVENT_START_TOLERANCE_MINUTES * 60);

        do {
            page = fetchPage.apply(request);
            page.setEvents(
                    page.getEvents()
                            .stream()
                            .filter(e -> !e.getEventTimestamp().isBefore(threshold))
                            .collect(Collectors.toList())
            );

            cachedEvents.addEvents(page);

            lastEventId = page.getLastEvent() != null
                    ? page.getLastEvent().getId()
                    : null;

            request.setLastEventId(lastEventId);
        } while (hasEvents(page, request.getLimit()));

        return lastEventId;
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

    private boolean hasEvents(M2MEvents events, Integer pageSize) {
        if (events == null || events.getEvents() == null || events.getEvents().isEmpty()) {
            return false;
        }

        // Se pageSize non è valido, fallback: basta avere eventi.
        if (pageSize == null || pageSize <= 0) {
            return true;
        }

        // true solo se la pagina è piena: potrebbe esistere una pagina successiva.
        return events.getEvents().size() >= pageSize;
    }

    @Override
    public void setBearerToken(String bearerToken) {
        throw new UnsupportedOperationException(V3_UNSUPPORTED_BEARER_MSG);
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        this.eventsApi.setApiClient(
                ApiClientUtils.createApiClient(super.getRestTemplate(), basePath, headers));
    }
}