package it.pagopa.interop.agreement.service.impl;

import static it.pagopa.interop.utils.ApiClientUtils.V3_UNSUPPORTED_BEARER_MSG;

import it.pagopa.interop.M2MVersionsMapper;
import it.pagopa.interop.agreement.service.IM2MV3TenantClient;
import it.pagopa.interop.common.client.AbstractDPoPClient;
import it.pagopa.interop.common.rest_template.DpopRestTemplate;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.TenantVerifiedAttributeRevokers;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.TenantVerifiedAttributeVerifiers;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.api.TenantsApi;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.TenantCertifiedDiscreteAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.TenantCertifiedDiscreteAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.TenantCertifiedDiscreteAttributes;
import it.pagopa.interop.utils.ApiClientUtils;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

@ToString
@EqualsAndHashCode
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class M2MV3TenantClientImpl extends AbstractDPoPClient implements IM2MV3TenantClient {
    private final TenantsApi tenantsApi;
    private final String basePath;
    private final M2MVersionsMapper mapper;

    public M2MV3TenantClientImpl(
        DpopRestTemplate restTemplate,
        InteropClientConfigs interopClientConfigs,
        M2MVersionsMapper mapper
    ) {
        super(restTemplate);
        this.basePath = interopClientConfigs.getM2mV3BaseUrl();
        this.tenantsApi = new TenantsApi(ApiClientUtils.createApiClient(restTemplate, basePath,
            Collections.emptyMap()));
        this.mapper = mapper;
    }

    @Override
    public TenantVerifiedAttributeVerifiers getVerifiers(UUID tenantId, UUID attributeId) {
        it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.TenantVerifiedAttributeVerifiers v3Bean = this.tenantsApi.getTenantVerifiedAttributeVerifiers(
            tenantId, attributeId, 0, 30);
        return this.mapper.mapToV2(v3Bean);
    }

    @Override
    public TenantVerifiedAttributeRevokers getRevokers(UUID tenantId, UUID attributeId) {
        it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.TenantVerifiedAttributeRevokers v3Bean = this.tenantsApi.getTenantVerifiedAttributeRevokers(
            tenantId, attributeId, 0, 30);
        return this.mapper.mapToV2(v3Bean);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        throw new UnsupportedOperationException(V3_UNSUPPORTED_BEARER_MSG);
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        this.tenantsApi.setApiClient(ApiClientUtils.createApiClient(super.getRestTemplate(), basePath, headers));
    }

    @Override
    public TenantCertifiedDiscreteAttribute assignTenantCertifiedDiscreteAttribute(UUID tenantId, TenantCertifiedDiscreteAttributeSeed tenantCertifiedDiscreteAttributeSeed) {

        return this.tenantsApi.assignTenantCertifiedDiscreteAttribute(tenantId, tenantCertifiedDiscreteAttributeSeed);
    }

    @Override
    public TenantCertifiedDiscreteAttributes getTenantCertifiedDiscreteAttributes(UUID tenantId, Integer offset, Integer limit) {
        return this.tenantsApi.getTenantCertifiedDiscreteAttributes(tenantId, offset, limit);
    }

    @Override
    public TenantCertifiedDiscreteAttribute revokeTenantCertifiedDiscreteAttribute(UUID tenantId, UUID attributeId) {
        return this.tenantsApi.revokeTenantCertifiedDiscreteAttribute(tenantId, attributeId);
    }
}
