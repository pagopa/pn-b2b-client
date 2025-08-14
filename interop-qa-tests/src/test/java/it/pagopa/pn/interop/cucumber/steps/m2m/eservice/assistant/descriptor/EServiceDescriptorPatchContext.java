package it.pagopa.pn.interop.cucumber.steps.m2m.eservice.assistant.descriptor;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptor;
import it.pagopa.pn.interop.cucumber.steps.m2m.AbstractResourceContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Component
@ScenarioScope
public class EServiceDescriptorPatchContext extends AbstractResourceContext<EServiceDescriptor> {

}
