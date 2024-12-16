package it.pagopa.pn.interop.cucumber.steps.utils;

import io.cucumber.java.Before;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import lombok.Data;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EServicesCommonContext {
    private List<EServiceDescriptor> publishedEservicesIds = new ArrayList<>();
    private UUID eserviceId;
    private UUID descriptorId;

}
