package it.pagopa.interop.event.service;

import static it.pagopa.interop.utils.ApiClientUtils.V3_UNSUPPORTED_BEARER_MSG;

import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.event.domain.M2MEventRequest;
import it.pagopa.interop.event.domain.M2MEvents;
import it.pagopa.interop.event.mapper.M2MV3EventMapper;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.api.EventsApi;
import it.pagopa.interop.utils.ApiClientUtils;
import java.util.Collections;
import java.util.Map;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class M2MV3EventClientImpl implements IM2MV3EventClient {
    private final EventsApi eventsApi;
    private final RestTemplate restTemplate;
    private final String basePath;
    private final M2MV3EventMapper mapper;

    public M2MV3EventClientImpl(
        RestTemplate restTemplate,
        InteropClientConfigs interopClientConfigs,
        M2MV3EventMapper mapper
    ) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getM2mV3BaseUrl();
        this.eventsApi = new EventsApi(
            ApiClientUtils.createApiClient(restTemplate, basePath,
                Collections.emptyMap()));
        this.mapper = mapper;
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
        throw new UnsupportedOperationException(V3_UNSUPPORTED_BEARER_MSG);
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        this.eventsApi.setApiClient(
            ApiClientUtils.createApiClient(restTemplate, basePath, headers));
    }
}