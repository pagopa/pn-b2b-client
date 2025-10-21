package it.pagopa.pn.interop.cucumber.steps.m2m.eservice;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.With;

@Data
@With
@Builder
public class EServiceAttributesGroupKey {
    private UUID eServiceId;
    private UUID descriptorId;
    private int groupIndex;
}
