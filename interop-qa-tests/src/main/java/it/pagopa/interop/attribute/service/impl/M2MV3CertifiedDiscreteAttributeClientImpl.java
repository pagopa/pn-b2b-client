package it.pagopa.interop.attribute.service.impl;

import it.pagopa.interop.attribute.service.IM2MV3CertifiedDiscreteAttributeClient;
import it.pagopa.interop.common.client.AbstractDPoPClient;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.common.operation.SimpleOperation;
import it.pagopa.interop.common.rest_template.DpopRestTemplate;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.api.AttributesApi;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.CertifiedDiscreteAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.CertifiedDiscreteAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.CertifiedDiscreteAttributes;
import it.pagopa.interop.utils.ApiClientUtils;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static it.pagopa.interop.utils.ApiClientUtils.V3_UNSUPPORTED_BEARER_MSG;

@ToString
@EqualsAndHashCode(callSuper = true)
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class M2MV3CertifiedDiscreteAttributeClientImpl extends AbstractDPoPClient
        implements IM2MV3CertifiedDiscreteAttributeClient
{
    private final AttributesApi attributesApi;
    private final String basePath;

    public M2MV3CertifiedDiscreteAttributeClientImpl(
        DpopRestTemplate restTemplate,
        InteropClientConfigs interopClientConfigs
    ) {
        super(restTemplate);
        this.basePath = interopClientConfigs.getM2mV3BaseUrl();
        this.attributesApi = new AttributesApi(ApiClientUtils.createApiClient(restTemplate, basePath, Collections.emptyMap()));
    }

    @Override
    public void setBearerToken(String bearerToken) {
        throw new UnsupportedOperationException(V3_UNSUPPORTED_BEARER_MSG);
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        this.attributesApi.setApiClient(ApiClientUtils.createApiClient(super.getRestTemplate(), basePath, headers));
    }

    @Override
    public CertifiedDiscreteAttribute get(UUID id) {
        return this.performOperation(SimpleOperation.of(
                () -> this.attributesApi.getCertifiedDiscreteAttribute(id),
                res -> res
        )).orElse(null);
    }

    @Override
    public List<CertifiedDiscreteAttribute> getAll() {
        final int pageSize = 50;
        int offset = 0;
        List<CertifiedDiscreteAttribute> attributes = new ArrayList<>();

        while (true) {
            final int requestOffset = offset;
            var page = this.performOperation(SimpleOperation.of(
                () -> attributesApi.getCertifiedDiscreteAttributes(requestOffset, pageSize),
                    res -> res
            )).orElse(null);

            if (page == null || page.getResults().isEmpty()) {
                break;
            }

            attributes.addAll(page.getResults());

            if (attributes.size() >= page.getPagination().getTotalCount()) {
                break;
            }

            if (page.getResults().size() < pageSize) {
                break;
            }

            int nextOffset = offset + pageSize;
            if (nextOffset <= offset) {
                break;
            }
            offset = nextOffset;
        }

        return attributes;
    }

    @Override
    public List<CertifiedDiscreteAttribute> getPage(int page, int size) {
        var offset = (page - 1) * size;
        return this.performOperation(SimpleOperation.of(
                () -> attributesApi.getCertifiedDiscreteAttributes(offset, size),
                res -> res
        )).map(CertifiedDiscreteAttributes::getResults).orElse(Collections.emptyList());
    }

    @Override
    public UUID getId(CertifiedDiscreteAttribute entity) {
        return entity == null ? null : entity.getId();
    }

    @Override
    public UUID generateId(EntityIdType type) {
        return switch (type){
            case INVALID_ID -> UUID.fromString("0-0-0-0-0");
            case NON_EXISTENT_ID -> UUID.randomUUID();
            default -> throw new IllegalStateException("Tipo di id non supportato: " + type.name());
        };
    }

    @Override
    public CertifiedDiscreteAttribute create(CertifiedDiscreteAttributeSeed payload) {
        return this.performOperation(SimpleOperation.of(
            () -> this.attributesApi.createCertifiedDiscreteAttribute(payload),
            res -> res
        )).orElse(null);
    }

    @Override
    public void tryCreationWithMissingData() {

        var apiClient = this.attributesApi.getApiClient();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        String localVarPostBody = null;
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
        final String[] localVarAccepts = {
                "application/json", "application/problem+json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = {
            "application/json"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        String[] localVarAuthNames = new String[] { "DPoPAuth", "DPoPProofHeader" };
        ParameterizedTypeReference<CertifiedDiscreteAttribute> localReturnType = new ParameterizedTypeReference<CertifiedDiscreteAttribute>() {};

        apiClient.invokeAPI("/certifiedDiscreteAttributes", HttpMethod.POST, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
}
