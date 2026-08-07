package it.pagopa.interop.attribute.service;

import it.pagopa.interop.authorization.service.utils.Authenticable;
import it.pagopa.interop.authorization.service.utils.SettableHeaders;
import it.pagopa.interop.common.client.IClient;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.CertifiedDiscreteAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.CertifiedDiscreteAttributeSeed;

import java.util.UUID;

public interface IM2MV3CertifiedDiscreteAttributeClient extends IClient<CertifiedDiscreteAttribute, UUID>,
    SettableHeaders, Authenticable {

    CertifiedDiscreteAttribute create(CertifiedDiscreteAttributeSeed agreementPayload);

    void tryCreationWithMissingData();
}
