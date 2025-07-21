package it.pagopa.pari.cucumber.steps.config;

import io.cucumber.spring.CucumberContextConfiguration;
import it.pagopa.pari.config.RestTemplateConfiguration;
import it.pagopa.pari.registrobeni.service.impl.RegisterPortalOperationClientImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;

@CucumberContextConfiguration
@SpringBootTest(classes = {
        RestTemplateConfiguration.class,
        RegisterPortalOperationClientImpl.class
})
@EnableScheduling
@EnableConfigurationProperties
public class PariCucumberSpringIntegration {
}