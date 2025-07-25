package it.pagopa.pari.cucumber.utils;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.pari.registrobeni.service.impl.RegisterPortalOperationClientImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
@ScenarioScope
@AllArgsConstructor
public class ApiClientContext {
    private RegisterPortalOperationClientImpl registerPortalOperationClient;

    public void setBearerToken(String role) {
        registerPortalOperationClient.setBearerToken(role);
    }
}
