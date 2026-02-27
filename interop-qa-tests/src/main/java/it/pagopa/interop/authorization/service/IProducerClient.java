package it.pagopa.interop.authorization.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServices;

import java.util.List;
import java.util.UUID;

public interface IProducerClient extends SettableBearerToken {
    ProducerEServiceDescriptor getProducerEServiceDescriptor(UUID eserviceId, UUID descriptorId);
    ProducerEServiceDetails getProducerEServiceDetails(UUID eserviceId);
    ProducerEServices getProducerEServices(Integer offset, Integer limit, String q, List<UUID> consumersIds, Boolean delegated);
    ProducerEServices getProducerEServices(Integer offset, Integer limit, String q, List<UUID> consumersIds, Boolean delegated, Boolean personalData);
}
