package it.pagopa.pn.interop.cucumber.steps;

import io.cucumber.spring.CucumberContextConfiguration;
import it.pagopa.interop.agreement.service.IAgreementClient;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.agreement.service.impl.AgreementClientImpl;
import it.pagopa.interop.agreement.service.impl.EServiceApiClientImpl;
import it.pagopa.interop.attribute.service.IAttributeApiClient;
import it.pagopa.interop.attribute.service.impl.AttributeApiClientImpl;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.utils.ClientTokenConfigurator;
import it.pagopa.interop.conf.springconfig.InteropClientConfigs;
import it.pagopa.interop.config.springconfig.springconfig.InteropRestTemplateConfiguration;
import it.pagopa.interop.authorization.service.impl.AuthorizationClientImpl;
import it.pagopa.interop.authorization.service.factory.SessionTokenFactory;
import it.pagopa.interop.delegate.service.impl.DelegationApiClientImpl;
import it.pagopa.interop.delegate.service.impl.DelegationsApiClientImpl;
import it.pagopa.interop.purpose.RiskAnalysisDataInitializer;
import it.pagopa.interop.purpose.service.impl.PurposeApiClientImpl;
import it.pagopa.interop.authorization.service.impl.ProducerClientImpl;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.authorization.service.utils.KeyPairGeneratorUtil;
import it.pagopa.interop.tenant.service.ITenantsApi;
import it.pagopa.interop.tenant.service.impl.TenantsApiClientImpl;
import it.pagopa.interop.utils.HttpCallExecutor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

//@ComponentScan(basePackages = {"it.pagopa.pn.interop.cucumber"})
@CucumberContextConfiguration
@SpringBootTest(classes = {
        IAuthorizationClient.class,
        AuthorizationClientImpl.class,
        InteropRestTemplateConfiguration.class,
        CommonUtils.class,
        SessionTokenFactory.class,
        ProducerClientImpl.class,
        KeyPairGeneratorUtil.class,
        DataPreparationService.class,
        HttpCallExecutor.class,
        AgreementClientImpl.class,
        AttributeApiClientImpl.class,
        IAgreementClient.class,
        IAttributeApiClient.class,
        ITenantsApi.class,
        TenantsApiClientImpl.class,
        InteropClientConfigs.class,
        PurposeApiClientImpl.class,
        IEServiceClient.class,
        EServiceApiClientImpl.class,
        RiskAnalysisDataInitializer.class,
        ClientTokenConfigurator.class,
        DelegationsApiClientImpl.class,
        DelegationApiClientImpl.class

})
@EnableScheduling
@EnableConfigurationProperties
public class InteropCucumberSpringIntegration {
}