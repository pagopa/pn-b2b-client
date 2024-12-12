package it.pagopa.pn.interop.cucumber.steps.utils;

import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class EServicesCommonDomain {
    private List<EServiceDescriptor> publishedEservicesIds = new ArrayList<>();
    private UUID eserviceId;
    private UUID descriptorId;

}
