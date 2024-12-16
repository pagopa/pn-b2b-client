package it.pagopa.pn.interop.cucumber.steps.common;

import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EServicesCommonContext {
    private List<EServiceDescriptor> publishedEservicesIds = new ArrayList<>();
    private UUID eserviceId;
    private UUID descriptorId;

}
