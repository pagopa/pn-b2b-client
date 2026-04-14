package it.pagopa.interop.event.service;

import it.pagopa.interop.common.client.AbstractClient;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.event.domain.M2MEvent;
import it.pagopa.interop.event.domain.M2MEventRequest;
import it.pagopa.interop.event.domain.M2MEvents;
import it.pagopa.interop.event.mapper.M2MEventMapper;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.api.EventsApi;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static it.pagopa.interop.event.enums.InteropEvent.Family;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Primary
public class M2MEventClientImpl extends AbstractClient implements IM2MEventClient{
    private final Map<String, Map<Family, M2MEvents>> tenantEventCache = new HashMap<>();

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
    public M2MEvents getEServicesEvents(M2MEventRequest request) {
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
    public M2MEvents getEServiceTemplateEvents(M2MEventRequest request) throws RestClientException {
        return mapper.map(eventsApi.getEServiceTemplateEvents(
                request.getLimit(),
                request.getLastEventId()));
    }

    @Override
    public M2MEvents getConsumerDelegationEvents(M2MEventRequest request) throws RestClientException {
        return mapper.map(eventsApi.getConsumerDelegationEvents(
                request.getLimit(),
                request.getLastEventId()));
    }

    @Override
    public M2MEvents getClientEvents(M2MEventRequest request) throws RestClientException {
        return mapper.map(eventsApi.getClientEvents(
                request.getLimit(),
                request.getLastEventId()));
    }

    @Override
    public M2MEvents getAttributesEvents(M2MEventRequest request) throws RestClientException {
        return mapper.map(eventsApi.getAttributesEvents(
                request.getLimit(),
                request.getLastEventId()));
    }

    @Override
    public M2MEvents getAgreementsEvents(M2MEventRequest request) throws RestClientException {
        return mapper.map(eventsApi.getAgreementsEvents(
                request.getLimit(),
                request.getLastEventId(),
                request.getDelegationId()));
    }

    @Override
    public M2MEvents getKeyEvents(M2MEventRequest request) throws RestClientException {
        return mapper.map(eventsApi.getKeyEvents(
                request.getLimit(),
                request.getLastEventId()));
    }

    @Override
    public M2MEvents getProducerDelegationEvents(M2MEventRequest request) throws RestClientException {
        return mapper.map(eventsApi.getProducerDelegationEvents(
                request.getLimit(),
                request.getLastEventId()));
    }

    @Override
    public M2MEvents getProducerKeyEvents(M2MEventRequest request) throws RestClientException {
        return mapper.map(eventsApi.getProducerKeyEvents(
                request.getLimit(),
                request.getLastEventId()));
    }

    @Override
    public M2MEvents getProducerKeychainEvents(M2MEventRequest request) throws RestClientException {
        return mapper.map(eventsApi.getProducerKeychainEvents(
                request.getLimit(),
                request.getLastEventId()));
    }

    @Override
    public M2MEvents getPurposeEvents(M2MEventRequest request) throws RestClientException {
        return mapper.map(eventsApi.getPurposeEvents(
                request.getLimit(),
                request.getLastEventId(),
                request.getDelegationId()));
    }

    @Override
    public M2MEvents getTenantEvents(M2MEventRequest request) throws RestClientException {
        return mapper.map(eventsApi.getTenantEvents(
                request.getLimit(),
                request.getLastEventId()));
    }

    @Override
    public M2MEvents getPurposeTemplateEvents(M2MEventRequest request) throws RestClientException {
        throw new UnsupportedOperationException("Purpose Template events are not supported by M2MEventClient");
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.eventsApi.setApiClient(createApiClient(bearerToken));
    }

    private M2MEvents getEvents(M2MEventRequest request) throws RestClientException {
        return switch (request.getEventFamily()) {
            case PURPOSE_TEMPLATE -> getAllCached(request.getTenantType(), Family.PURPOSE_TEMPLATE, this::getPurposeTemplateEvents);
            case ESERVICE -> getAllCached(request.getTenantType(), Family.ESERVICE, this::getEServicesEvents);
            case ESERVICE_TEMPLATE -> getAllCached(request.getTenantType(), Family.ESERVICE_TEMPLATE, this::getEServiceTemplateEvents);
            case CONSUMER_DELEGATION -> getAllCached(request.getTenantType(), Family.CONSUMER_DELEGATION, this::getConsumerDelegationEvents);
            case CLIENT -> getAllCached(request.getTenantType(), Family.CLIENT, this::getClientEvents);
            case ATTRIBUTE -> getAllCached(request.getTenantType(), Family.ATTRIBUTE, this::getAttributesEvents);
            case AGREEMENT -> getAllCached(request.getTenantType(), Family.AGREEMENT, this::getAgreementsEvents);
            case KEY -> getAllCached(request.getTenantType(), Family.KEY, this::getKeyEvents);
            case PRODUCER_DELEGATION -> getAllCached(request.getTenantType(), Family.PRODUCER_DELEGATION, this::getProducerDelegationEvents);
            case PRODUCER_KEY -> getAllCached(request.getTenantType(), Family.PRODUCER_KEY, this::getProducerKeyEvents);
            case PRODUCER_KEYCHAIN -> getAllCached(request.getTenantType(), Family.PRODUCER_KEYCHAIN, this::getProducerKeychainEvents);
            case PURPOSE -> getAllCached(request.getTenantType(), Family.PURPOSE, this::getPurposeEvents);
            case TENANT -> getAllCached(request.getTenantType(), Family.TENANT, this::getTenantEvents);
        };
    }

   private M2MEvents getAllCached(String currentTenant, Family eventFamily, Function<M2MEventRequest, M2MEvents> funz) throws RestClientException {
        Map<Family, M2MEvents> cache = tenantEventCache.get(currentTenant);
        M2MEvent lastEvent = cache.get(eventFamily).getLastEvent();
        M2MEvents result;

        do {
            result = funz.apply(M2MEventRequest.of(lastEvent.getId()));
            if (!result.getEvents().isEmpty()) {
                cache.get(eventFamily).addEvents(result);
                lastEvent = cache.get(eventFamily).getLastEvent();
            }
        } while (result.getEvents().isEmpty());

        return cache.get(eventFamily);
    }
}