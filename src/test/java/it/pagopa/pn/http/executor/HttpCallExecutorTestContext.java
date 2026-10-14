package it.pagopa.pn.http.executor;

import io.cucumber.spring.CucumberContextConfiguration;
import it.pagopa.pn.cucumber.steps.config.SendHttpCallExecutorConfiguration;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@ContextConfiguration(classes = {SendHttpCallExecutorConfiguration.class, HttpCallExecutorLifecycleProbe.class})
public class HttpCallExecutorTestContext {
}
