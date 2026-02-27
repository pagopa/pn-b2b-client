package it.pagopa.pn.interop.cucumber.steps.selfcare.model;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.User;
import java.util.List;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
@Data
public class TenantContext {
    private List<User> m2mUsers;
    private List<it.pagopa.interop.generated.openapi.clients.bff.model.User> selfcareUsers;
}
