package it.pagopa.pn.interop.cucumber.steps.config;

import io.cucumber.spring.CucumberContextConfiguration;
import it.pagopa.interop.agreement.service.IAgreementClient;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.agreement.service.impl.AgreementClientImpl;
import it.pagopa.interop.agreement.service.impl.EServiceApiClientImpl;
import it.pagopa.interop.agreement.service.impl.M2MAgreementClientImpl;
import it.pagopa.interop.attribute.service.IAttributeApiClient;
import it.pagopa.interop.attribute.service.impl.AttributeApiClientImpl;
import it.pagopa.interop.attribute.service.impl.M2MAttributeClientImpl;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.impl.AuthorizationClientImpl;
import it.pagopa.interop.authorization.service.impl.ProducerClientImpl;
import it.pagopa.interop.authorization.service.utils.ConfigFileReader;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.authorization.service.utils.voucher.VoucherService;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.config.springconfig.springconfig.InteropRestTemplateConfiguration;
import it.pagopa.interop.config.springconfig.springconfig.JwtTokenServiceConfiguration;
import it.pagopa.interop.delegate.service.impl.ConsumerDelegationsApiClientImpl;
import it.pagopa.interop.delegate.service.impl.DelegationApiClientImpl;
import it.pagopa.interop.delegate.service.impl.ProducerDelegationsApiClientImpl;
import it.pagopa.interop.eservice.service.impl.M2MEserviceClientImpl;
import it.pagopa.interop.eservice.service.impl.M2MEserviceDescriptorClientImpl;
import it.pagopa.interop.eservice_template.impl.M2MEServiceTemplateClientImpl;
import it.pagopa.interop.purpose.RiskAnalysisDataInitializer;
import it.pagopa.interop.purpose.service.impl.M2MPurposeClientImpl;
import it.pagopa.interop.purpose.service.impl.PurposeApiClientImpl;
import it.pagopa.interop.tenant.service.ITenantsApi;
import it.pagopa.interop.tenant.service.impl.TenantsApiClientImpl;
import it.pagopa.interop.tracing.config.TracingClientConfigs;
import it.pagopa.interop.tracing.service.impl.DevAbstractInteropTracingClient;
import it.pagopa.interop.tracing.service.impl.QAAbstractInteropTracingClient;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.M2MDataPreparationService;
import it.pagopa.pn.interop.cucumber.utility.BlobFileCreator;
import it.pagopa.pn.interop.cucumber.utility.CommonUtils;
import it.pagopa.pn.interop.cucumber.utility.TracingFileUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;

@CucumberContextConfiguration
@SpringBootTest(classes = {
        IAuthorizationClient.class,
        AuthorizationClientImpl.class,
        InteropRestTemplateConfiguration.class,
        JwtTokenServiceConfiguration.class,
        PollingService.class,
        ProducerClientImpl.class,
        BFFDataPreparationService.class,
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
        ConsumerDelegationsApiClientImpl.class,
        DelegationApiClientImpl.class,
        ConfigFileReader.class,
        InteropClientConfigs.class,
        TracingFileUtils.class,
        BlobFileCreator.class,
        TracingClientConfigs.class,
        DevAbstractInteropTracingClient.class,
        QAAbstractInteropTracingClient.class,
        CommonUtils.class,
        VoucherService.class,
        it.pagopa.interop.authorization.service.DataPreparationService.class,
        M2MAgreementClientImpl.class,
        M2MAttributeClientImpl.class,
        M2MPurposeClientImpl.class,
        M2MEserviceClientImpl.class,
        M2MEServiceTemplateClientImpl.class,
        M2MDataPreparationService.class,
        M2MEserviceDescriptorClientImpl.class,
        CucumberScopedBeans.class
})
@EnableScheduling
@EnableConfigurationProperties
public class InteropCucumberSpringIntegration {

}