package it.pagopa.interop.agreement.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class EServiceDescriptor {
    private UUID eServiceId;
    private UUID descriptorId;
}
