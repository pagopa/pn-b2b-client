package it.pagopa.interop.eservice.service.enums;

import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EService;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptors;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServices;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public enum EserviceEntityType {
    ESERVICE(
            "eService",
            obj -> {
                var es = (EService) obj;
                return List.of(new EServiceDescriptor(es.getId(), null));
            }
    ),

    DESCRIPTORS(
            "descriptors",
            obj -> {
                var desc = (EServiceDescriptors) obj;
                return desc.getResults().stream()
                        .map(d -> new EServiceDescriptor(null, d.getId()))
                        .toList();
            }
    ),

    ESERVICES(
            "eServices",
            obj -> {
                var eservices = (EServices) obj;
                return eservices.getResults().stream()
                        .map(s -> new EServiceDescriptor(s.getId(), null))
                        .toList();
            }
    ),

    DESCRIPTOR(
            "descriptor",
            obj -> {
              var descriptor = (EServiceDescriptors) obj;
              return descriptor.getResults().stream()
                      .map(d -> new EServiceDescriptor(null, d.getId()))
                      .toList();
            }
    );

    private final String label;
    private final Function<Object, List<EServiceDescriptor>> eServiceDescriptorMapper;

    EserviceEntityType(String label,
                       Function<Object, List<EServiceDescriptor>> eServiceDescriptorMapper) {
        this.label = label;
        this.eServiceDescriptorMapper = eServiceDescriptorMapper;
    }

    public String getLabel() {
        return label;
    }

    public Function<Object, List<EServiceDescriptor>> geteServiceDescriptorMapper() {
        return eServiceDescriptorMapper;
    }

    public static EserviceEntityType fromString(String input) {
        return Arrays.stream(values())
                .filter(e -> e.label.equalsIgnoreCase(input))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tipo non supportato: " + input));
    }
}



