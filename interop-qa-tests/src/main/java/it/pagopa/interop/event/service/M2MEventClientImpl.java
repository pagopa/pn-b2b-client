package it.pagopa.interop.event.service;

import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.event.mapper.M2MEventMapper;
import it.pagopa.interop.event.domain.M2MEventRequest;
import it.pagopa.interop.event.domain.M2MEvents;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.api.EventsApi;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class M2MEventClientImpl implements IM2MEventClient {
    private final EventsApi eventsApi;
    private final RestTemplate restTemplate;
    private final String basePath;
    private final M2MEventMapper mapper;

    public M2MEventClientImpl(
        RestTemplate restTemplate,
        InteropClientConfigs interopClientConfigs,
        M2MEventMapper mapper
    ) {
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
    public M2MEvents getEServicesEvents(M2MEventRequest request)
        throws RestClientException {
        return mapper.map(eventsApi.getEServicesEvents(
            request.getLimit(),
            request.getDelegationId(),
            request.getLastEventId()));
    }

    @Override
    public M2MEvents getEServiceTemplateEvents(M2MEventRequest request)
        throws RestClientException {
        return mapper.map(eventsApi.getEServiceTemplateEvents(
            request.getLimit(),
            request.getLastEventId()));
    }

    @Override
    public M2MEvents getConsumerDelegationEvents(M2MEventRequest request)
        throws RestClientException {
        return mapper.map(eventsApi.getConsumerDelegationEvents(
            request.getLimit(),
            request.getLastEventId()));
    }

    @Override
    public M2MEvents getClientEvents(M2MEventRequest request)
        throws RestClientException {
        return mapper.map(eventsApi.getClientEvents(
            request.getLimit(),
            request.getLastEventId()));
    }

    @Override
    public M2MEvents getAttributesEvents(M2MEventRequest request)
        throws RestClientException {
        return mapper.map(eventsApi.getAttributesEvents(
            request.getLimit(),
            request.getLastEventId()));
    }

    @Override
    public M2MEvents getAgreementsEvents(M2MEventRequest request)
        throws RestClientException {
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
    public M2MEvents getProducerDelegationEvents(M2MEventRequest request)
        throws RestClientException {
        return mapper.map(eventsApi.getProducerDelegationEvents(
            request.getLimit(),
            request.getLastEventId()));
    }

    @Override
    public M2MEvents getProducerKeyEvents(M2MEventRequest request)
        throws RestClientException {
        return mapper.map(eventsApi.getProducerKeyEvents(
            request.getLimit(),
            request.getLastEventId()));
    }

    @Override
    public M2MEvents getProducerKeychainEvents(M2MEventRequest request)
        throws RestClientException {
        return mapper.map(eventsApi.getProducerKeychainEvents(
            request.getLimit(),
            request.getLastEventId()));
    }

    @Override
    public M2MEvents getPurposeEvents(M2MEventRequest request)
        throws RestClientException {
        return mapper.map(eventsApi.getPurposeEvents(
            request.getLimit(),
            request.getLastEventId(),
            request.getDelegationId()));
    }

    @Override
    public M2MEvents getTenantEvents(M2MEventRequest request)
        throws RestClientException {
        return mapper.map(eventsApi.getTenantEvents(
            request.getLimit(),
            request.getLastEventId()));
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.eventsApi.setApiClient(createApiClient(bearerToken));
    }
}