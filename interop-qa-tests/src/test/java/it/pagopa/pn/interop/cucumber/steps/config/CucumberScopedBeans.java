package it.pagopa.pn.interop.cucumber.steps.config;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.attribute.AttributeSteps;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.EserviceDescriptorSteps;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.EserviceSteps;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class CucumberScopedBeans {

    @Bean
    @ScenarioScope
    public AttributeSteps attributeSteps(SharedStepsContext ctx, ClientTokenConfigurator conf) {
        return new AttributeSteps(ctx, conf);
    }

    @Bean(name = "eserviceDescriptorSteps")
    @ScenarioScope
    public EserviceDescriptorSteps eserviceDescriptorSteps(SharedStepsContext ctx, ClientTokenConfigurator conf) {
        return new EserviceDescriptorSteps(ctx, conf);
    }

    @Bean
    @ScenarioScope
    public EserviceSteps eserviceSteps(SharedStepsContext sharedStepsContext,
                                       ClientTokenConfigurator clientTokenConfigurator) {
        return new EserviceSteps(sharedStepsContext, clientTokenConfigurator);
    }
}
