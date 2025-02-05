package it.pagopa.pn.interop.cucumber.steps;

import io.cucumber.spring.CucumberContextConfiguration;
import it.pagopa.interop.agreement.service.IAgreementClient;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.agreement.service.impl.AgreementClientImpl;
import it.pagopa.interop.agreement.service.impl.EServiceApiClientImpl;
import it.pagopa.interop.attribute.service.IAttributeApiClient;
import it.pagopa.interop.attribute.service.impl.AttributeApiClientImpl;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.factory.SessionTokenFactory;
import it.pagopa.interop.authorization.service.impl.AuthorizationClientImpl;
import it.pagopa.interop.authorization.service.impl.ProducerClientImpl;
import it.pagopa.interop.authorization.service.utils.ConfigFileReader;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.config.springconfig.springconfig.InteropRestTemplateConfiguration;
import it.pagopa.interop.delegate.service.impl.DelegationApiClientImpl;
import it.pagopa.interop.delegate.service.impl.ProducerDelegationsApiClientImpl;
import it.pagopa.interop.purpose.RiskAnalysisDataInitializer;
import it.pagopa.interop.purpose.service.impl.PurposeApiClientImpl;
import it.pagopa.interop.tenant.service.ITenantsApi;
import it.pagopa.interop.tenant.service.impl.TenantsApiClientImpl;
import it.pagopa.interop.tracing.config.TracingClientConfigs;
import it.pagopa.interop.tracing.service.TracingRetriever;
import it.pagopa.interop.tracing.service.impl.InteropTracingClientImpl;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.utility.TracingFileUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;

//@ComponentScan(basePackages = {"it.pagopa.pn.interop.cucumber"})
@CucumberContextConfiguration
@SpringBootTest(classes = {
    IAuthorizationClient.class,
    AuthorizationClientImpl.class,
    InteropRestTemplateConfiguration.class,
    IdentityService.class,
    PollingService.class,
    SessionTokenFactory.class,
    ProducerClientImpl.class,
    DataPreparationService.class,
    HttpCallExecutor.class,
    AgreementClientImpl.class,
    AttributeApiClientImpl.class,
    IAgreementClient.class,
    IAttributeApiClient.class,
    ITenantsApi.class,
    TenantsApiClientImpl.class,
    PurposeApiClientImpl.class,
    IEServiceClient.class,
    EServiceApiClientImpl.class,
    RiskAnalysisDataInitializer.class,
    ClientTokenConfigurator.class,
    ProducerDelegationsApiClientImpl.class,
    DelegationApiClientImpl.class,
    ConfigFileReader.class,
    InteropClientConfigs.class,
    InteropTracingClientImpl.class,
    TracingFileUtils.class,
    TracingClientConfigs.class,
    TracingRetriever.class
})
@EnableScheduling
@EnableConfigurationProperties
public class InteropCucumberSpringIntegration {
}