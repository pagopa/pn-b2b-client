package it.pagopa.pn.interop.cucumber.steps;

import io.cucumber.spring.CucumberContextConfiguration;
import it.pagopa.interop.service.IAuthorizationClientCreate;
import it.pagopa.interop.config.springconfig.springconfig.InteropRestTemplateConfiguration;
import it.pagopa.interop.service.impl.AuthorizationClientCreateImpl;
import it.pagopa.pn.interop.cucumber.steps.authorization.factory.SessionTokenFactory;
import it.pagopa.pn.interop.cucumber.steps.authorization.resolver.TokenResolver;
import it.pagopa.pn.interop.cucumber.steps.authorization.utils.CommonSteps;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;

@CucumberContextConfiguration
@SpringBootTest(classes = {
        TokenResolver.class,
        IAuthorizationClientCreate.class,
        AuthorizationClientCreateImpl.class,
        InteropRestTemplateConfiguration.class,
        CommonSteps.class,
        SessionTokenFactory.class,
        TokenResolver.class

})
@EnableScheduling
@EnableConfigurationProperties
public class CucumberSpringIntegration2 {
}