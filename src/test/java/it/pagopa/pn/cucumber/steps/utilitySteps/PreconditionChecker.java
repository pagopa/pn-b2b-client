package it.pagopa.pn.cucumber.steps.utilitySteps;


import io.cucumber.java.Before;
import org.junit.jupiter.api.Assumptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


public class PreconditionChecker {

    @Value("${pn.interop.enable}")
    private boolean isInteropEnabled;

    @Value("${spring.profiles.active}")
    private String env;


    @Autowired
    public PreconditionChecker() {
    }


    @Before("@precondition")
    public void setup(){
        Assumptions.assumeTrue(preconditionForTest());
    }

    private boolean preconditionForTest(){
        System.out.println("ENV: "+env+" isInteropEnabled: "+isInteropEnabled);
        return switch (env) {
            case "test" -> !isInteropEnabled;
            case "uat" -> isInteropEnabled;
            default -> false;
        };
    }

    @Before("@uatEnvCondition")
    public void envCondition(){
        Assumptions.assumeFalse(env.equalsIgnoreCase("uat"));
    }


}
