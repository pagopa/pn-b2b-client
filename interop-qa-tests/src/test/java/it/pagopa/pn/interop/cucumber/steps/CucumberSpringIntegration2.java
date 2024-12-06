package it.pagopa.pn.interop.cucumber.steps;

import io.cucumber.spring.CucumberContextConfiguration;
import it.pagopa.interop.agreement.service.IAgreementClient;
import it.pagopa.interop.agreement.service.impl.AgreementClientImpl;
import it.pagopa.interop.attribute.service.IAttributeApiClient;
import it.pagopa.interop.attribute.service.impl.AttributeApiClientImpl;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.config.springconfig.springconfig.InteropRestTemplateConfiguration;
import it.pagopa.interop.authorization.service.impl.AuthorizationClientImpl;
import it.pagopa.interop.authorization.service.factory.SessionTokenFactory;
import it.pagopa.interop.resolver.TokenResolver;
import it.pagopa.interop.authorization.service.impl.ProducerClientImpl;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.authorization.service.utils.KeyPairGeneratorUtil;
import it.pagopa.interop.tenant.service.ITenantsApi;
import it.pagopa.interop.tenant.service.impl.TenantsApiClientImpl;
import it.pagopa.pn.interop.cucumber.steps.authorization.ClientCommonSteps;
import it.pagopa.pn.interop.cucumber.steps.utils.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.utils.HttpCallExecutor;
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
        ClientCommonSteps.class,
        KeyPairGeneratorUtil.class,
        DataPreparationService.class,
        HttpCallExecutor.class,
        AgreementClientImpl.class,
        AttributeApiClientImpl.class,
        IAgreementClient.class,
        IAttributeApiClient.class,
        ITenantsApi.class,
        TenantsApiClientImpl.class

})
@EnableScheduling
@EnableConfigurationProperties
public class CucumberSpringIntegration2 {
}