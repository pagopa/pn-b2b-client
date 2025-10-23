package it.pagopa.pn.interop.cucumber.steps.m2m;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.With;

@Data
@With
@Builder
public class EServiceAttributesKey {
    private UUID eServiceId;
    private UUID descriptorId;

    public static EServiceAttributesKey from(EServiceAttributesGroupKey groupKey) {
        return EServiceAttributesKey.builder()
            .eServiceId(groupKey.getEServiceId())
            .descriptorId(groupKey.getDescriptorId())
            .build();
    }
}
