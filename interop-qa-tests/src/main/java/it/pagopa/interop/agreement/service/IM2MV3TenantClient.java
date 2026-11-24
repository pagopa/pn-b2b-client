package it.pagopa.interop.agreement.service;

import it.pagopa.interop.authorization.service.utils.Authenticable;
import it.pagopa.interop.authorization.service.utils.SettableHeaders;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.TenantCertifiedDiscreteAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.TenantCertifiedDiscreteAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.TenantCertifiedDiscreteAttributes;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.UpdateTenantCertifiedDiscreteAttributeSeed;

import java.util.UUID;

public interface IM2MV3TenantClient extends IM2MTenantClient, SettableHeaders, Authenticable {

    TenantCertifiedDiscreteAttribute assignTenantCertifiedDiscreteAttribute(UUID tenantId, TenantCertifiedDiscreteAttributeSeed tenantCertifiedDiscreteAttributeSeed);

    TenantCertifiedDiscreteAttributes getTenantCertifiedDiscreteAttributes(UUID tenantId, Integer offset, Integer limit);

    TenantCertifiedDiscreteAttribute revokeTenantCertifiedDiscreteAttribute(UUID tenantId, UUID attributeId);

    TenantCertifiedDiscreteAttribute replaceTenantCertifiedDiscreteAttribute(UUID tenantId, UUID attributeId, UpdateTenantCertifiedDiscreteAttributeSeed updateTenantCertifiedDiscreteAttributeSeed);
}