package it.pagopa.pn.interop.cucumber.steps.utils;

import lombok.Data;

import java.util.UUID;

@Data
public class EServicesCommonDomain {
    private UUID eserviceId;
    private UUID descriptorId;
}
