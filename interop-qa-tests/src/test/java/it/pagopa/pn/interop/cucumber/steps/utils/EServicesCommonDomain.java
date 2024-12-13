package it.pagopa.pn.interop.cucumber.steps.utils;

import io.cucumber.java.Before;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Data
public class EServicesCommonDomain {
    private List<EServiceDescriptor> publishedEservicesIds = new ArrayList<>();
    private UUID eserviceId;
    private UUID descriptorId;

    @Before
    public void resetEServicesCommonDomain() {
        publishedEservicesIds = new ArrayList<>();
        eserviceId = null;
        descriptorId = null;
    }

}
