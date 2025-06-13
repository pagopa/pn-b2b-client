package it.pagopa.interop.eservice.service.mapper;

import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EService;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptors;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EserviceDescriptorDomainMapper {
    private EserviceDescriptorDomainMapper() {}
    
    public static EServiceDescriptor mapTo(EService eService) {
        return EserviceDescriptorDomainMapper.mapTo(eService.getId(), (UUID) null);
    }

    public static EService mapTo(EServiceDescriptor descriptor) {
        final EService eService = new EService();
        eService.setId(descriptor.getEServiceId());
        return eService;
    }

    public static EServiceDescriptor mapTo(UUID eserviceId, UUID descriptorId) {
        return new EServiceDescriptor(eserviceId, descriptorId);
    }

    public static List<EServiceDescriptor> mapTo(UUID eserviceId, EServiceDescriptors descriptors) {
        return descriptors.getResults().stream()
                .map(descriptor -> {
                    EServiceDescriptor d = mapTo(descriptor);
                    d.setEServiceId(eserviceId);
                    return d;
                })
                .collect(Collectors.toList());
    }


    private static EServiceDescriptor mapTo(it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptor descriptor) {
        return new EServiceDescriptor(null, descriptor.getId());
    }
}
