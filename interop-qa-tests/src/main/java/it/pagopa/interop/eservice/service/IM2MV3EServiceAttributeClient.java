package it.pagopa.interop.eservice.service;

import it.pagopa.interop.authorization.service.utils.Authenticable;
import it.pagopa.interop.authorization.service.utils.SettableHeaders;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.*;

import java.util.UUID;

public interface IM2MV3EServiceAttributeClient extends IM2MEServiceAttributeClient,
    SettableHeaders, Authenticable {

    EServiceDescriptorCertifiedDiscreteAttributesGroup createEServiceDescriptorCertifiedDiscreteAttributesGroup(UUID eserviceId, UUID descriptorId, EServiceDescriptorCertifiedDiscreteAttributesGroupSeed eserviceDescriptorCertifiedDiscreteAttributesGroupSeed);
    EServiceDescriptorCertifiedDiscreteAttributes getEServiceDescriptorCertifiedDiscreteAttributes(UUID eserviceId, UUID descriptorId, Integer offset, Integer limit);
    Object assignEServiceDescriptorCertifiedDiscreteAttributesToGroup(UUID eserviceId, UUID descriptorId, Integer groupIndex, EServiceDescriptorCertifiedDiscreteAttributesGroupSeed eserviceDescriptorCertifiedDiscreteAttributesGroupSeed);
    Object deleteEServiceDescriptorCertifiedDiscreteAttributeFromGroup(UUID eserviceId, UUID descriptorId, Integer groupIndex, UUID attributeId);
}
