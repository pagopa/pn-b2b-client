package it.pagopa.interop.agreement.domain;

import lombok.Data;

import java.util.UUID;

@Data
public class EServiceDescriptor {
    private final UUID eServiceId;
    private final UUID descriptorId;
}
