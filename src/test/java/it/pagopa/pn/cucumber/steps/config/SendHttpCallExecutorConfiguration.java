package it.pagopa.pn.cucumber.steps.config;

import it.pagopa.common.http.HttpCallExecutor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class SendHttpCallExecutorConfiguration {

    @Bean(name = "sendHttpCallExecutor")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public HttpCallExecutor sendHttpCallExecutor() {
        return new HttpCallExecutor();
    }
}
