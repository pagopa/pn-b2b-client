package it.pagopa.pn.interop.cucumber.steps;

import io.cucumber.spring.CucumberContextConfiguration;
import it.pagopa.interop.service.IAuthorizationClient;
import it.pagopa.interop.config.springconfig.springconfig.InteropRestTemplateConfiguration;
import it.pagopa.interop.service.impl.AuthorizationClientImpl;
import it.pagopa.interop.service.factory.SessionTokenFactory;
import it.pagopa.interop.resolver.TokenResolver;
import it.pagopa.interop.service.impl.ProducerClientImpl;
import it.pagopa.interop.service.utils.CommonUtils;
import it.pagopa.interop.service.utils.KeyPairGeneratorUtil;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;

@CucumberContextConfiguration
@SpringBootTest(classes = {
        TokenResolver.class,
        IAuthorizationClient.class,
        AuthorizationClientImpl.class,
        InteropRestTemplateConfiguration.class,
        CommonUtils.class,
        SessionTokenFactory.class,
        TokenResolver.class,
        ProducerClientImpl.class,
        CommonUtils.class,
        KeyPairGeneratorUtil.class,

})
@EnableScheduling
@EnableConfigurationProperties
public class CucumberSpringIntegration2 {
}