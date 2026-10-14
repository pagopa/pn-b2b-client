package it.pagopa.pn.cucumber.steps.config;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.common.http.HttpCallExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SendHttpCallExecutorConfiguration {

    @Bean(name = "sendHttpCallExecutor")
    @ScenarioScope
    public HttpCallExecutor sendHttpCallExecutor() {
        return new HttpCallExecutor();
    }
}
