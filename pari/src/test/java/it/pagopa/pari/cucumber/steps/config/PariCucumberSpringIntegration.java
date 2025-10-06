package it.pagopa.pari.cucumber.steps.config;

import io.cucumber.spring.CucumberContextConfiguration;
import it.pagopa.pari.config.RestTemplateConfiguration;
import it.pagopa.pari.cucumber.config.RdbUserRoleConfiguration;
import it.pagopa.pari.cucumber.domain.JWTUserDataRegistry;
import it.pagopa.pari.cucumber.utils.ApiClientContext;
import it.pagopa.pari.cucumber.utils.SharedCommonContext;
import it.pagopa.pari.merchant.service.impl.MerchantOperationClientImpl;
import it.pagopa.pari.registrobeni.service.impl.RegisterPortalOperationClientImpl;
import it.pagopa.pari.utils.RdBJWTProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;

@CucumberContextConfiguration
@SpringBootTest(classes = {
        RestTemplateConfiguration.class,
        RdBJWTProvider.class,
        JWTUserDataRegistry.class,
        ApiClientContext.class,
        SharedCommonContext.class,
        RegisterPortalOperationClientImpl.class,
        JWTUserDataRegistry.class,
        RdbUserRoleConfiguration.class,
        MerchantOperationClientImpl.class
})
@EnableScheduling
@EnableConfigurationProperties
public class PariCucumberSpringIntegration {
}