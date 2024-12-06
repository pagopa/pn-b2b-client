package it.pagopa.pn.cucumber.steps.utilitySteps;


import io.cucumber.java.Before;
import it.pagopa.pn.client.b2b.pa.config.PnInteropConfig;
import org.junit.jupiter.api.Assumptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import static it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenSingleton.ENEBLED_INTEROP;


public class PreconditionChecker {

    private boolean isInteropEnabled;

    @Value("${spring.profiles.active}")
    private String env;


    @Autowired
    public PreconditionChecker(PnInteropConfig pnInteropConfig) {
        this.isInteropEnabled = pnInteropConfig.getEnableInterop().equalsIgnoreCase(ENEBLED_INTEROP);
    }


    @Before("@precondition")
    public void setup() {
        Assumptions.assumeTrue(preconditionForTest());
    }

    private boolean preconditionForTest() {
        System.out.println("ENV: " + env + " isInteropEnabled: " + isInteropEnabled);
        return switch (env) {
            case "test" -> !isInteropEnabled;
            case "uat", "hotfix" -> isInteropEnabled;
            default -> true;
        };
    }

    @Before("@uatEnvCondition")
    public void envCondition() {
        Assumptions.assumeFalse(env.equalsIgnoreCase("uat"));
    }

    @Before("@mockEnvCondition")
    public void mockCondition() {
        Assumptions.assumeTrue(env.equalsIgnoreCase("uat"));
    }
}
