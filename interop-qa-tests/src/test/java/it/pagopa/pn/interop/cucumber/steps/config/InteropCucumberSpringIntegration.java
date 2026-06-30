package it.pagopa.pn.interop.cucumber.steps.config;

import io.cucumber.spring.CucumberContextConfiguration;
import it.pagopa.interop.M2MVersionsMapper;
import it.pagopa.interop.M2MVersionsMapperImpl;
import it.pagopa.interop.agreement.service.IAgreementClient;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.agreement.service.impl.*;
import it.pagopa.interop.attribute.service.IAttributeApiClient;
import it.pagopa.interop.attribute.service.impl.*;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.impl.AuthorizationClientImpl;
import it.pagopa.interop.authorization.service.impl.ProducerClientImpl;
import it.pagopa.interop.authorization.service.utils.ConfigFileReader;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.authorization.service.utils.voucher.AsyncVoucherService;
import it.pagopa.interop.authorization.service.utils.voucher.VoucherService;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.config.springconfig.InteropRestTemplateConfiguration;
import it.pagopa.interop.config.springconfig.JwtTokenServiceConfiguration;
import it.pagopa.interop.delegate.service.impl.*;
import it.pagopa.interop.dev_tools.service.impl.DevToolsClientImpl;
import it.pagopa.interop.e_service_template.impl.*;
import it.pagopa.interop.e_service_template.mapper.DescriptorAttributesMapperImpl;
import it.pagopa.interop.e_service_template.mapper.RiskAnalysisMapperImpl;
import it.pagopa.interop.eservice.service.impl.*;
import it.pagopa.interop.eservice.service.mapper.EServiceAttributeMapperImpl;
import it.pagopa.interop.event.mapper.M2MEventMapperImpl;
import it.pagopa.interop.event.mapper.M2MV3EventMapperImpl;
import it.pagopa.interop.event.service.M2MEventClientImpl;
import it.pagopa.interop.event.service.M2MV3EventClientImpl;
import it.pagopa.interop.maintenance.EnvDebugLogger;
import it.pagopa.interop.maintenance.InteropMaintenanceServiceImpl;
import it.pagopa.interop.maintenance.TenantMapperImpl;
import it.pagopa.interop.notification.NotificationClientImpl;
import it.pagopa.interop.notification.NotificationConfigClient;
import it.pagopa.interop.probing.config.ProbingClientConfigs;
import it.pagopa.interop.probing.service.impl.ProbingClient;
import it.pagopa.interop.producer_keychains.service.M2MV3ProducerKeychainsClient;
import it.pagopa.interop.producerkeychain.ProducerKeychainClientImpl;
import it.pagopa.interop.purpose.RiskAnalysisDataInitializer;
import it.pagopa.interop.purpose.service.IPurposeTemplateClient;
import it.pagopa.interop.purpose.service.impl.*;
import it.pagopa.interop.selfcare.service.ISelfcareClient;
import it.pagopa.interop.selfcare.service.impl.SelfcareClientImpl;
import it.pagopa.interop.tenant.service.ITenantsApi;
import it.pagopa.interop.tenant.service.impl.TenantsApiClientImpl;
import it.pagopa.interop.tenant.service.impl.TenantsProcessApiClientImpl;
import it.pagopa.interop.tracing.config.TracingClientConfigs;
import it.pagopa.interop.tracing.service.impl.DevAbstractInteropTracingClient;
import it.pagopa.interop.tracing.service.impl.ExtraQaAbstractInteropTracingClient;
import it.pagopa.interop.users.service.M2MV3UsersClient;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.common.PurposeTemplateCommonContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.M2MDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateTestAssistant;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.assistant.*;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.descriptor.assistant.EServiceDescriptorPatchContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.descriptor.assistant.EServiceDescriptorPatchOperationsAssistant;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.descriptor.assistant.EServiceDescriptorQuotasPatchOperationsAssistant;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.descriptor.mapper.EServiceDescriptorMapperImpl;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.descriptor.mapper.EServiceDescriptorQuotasMapperImpl;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.helpers.EServiceSeedFactory;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.mapper.*;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template.assistant.EServiceTemplatePatchContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template.assistant.EServiceTemplatePatchOperationsAssistant;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template.helpers.EServiceTemplateSeedFactory;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template.mapper.EServiceTemplateMapperImpl;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template.version.assistant.EServiceTemplateVersionPatchContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template.version.assistant.EServiceTemplateVersionPatchOperationsAssistant;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template.version.assistant.EServiceTemplateVersionQuotasPatchOperationsAssistant;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template.version.mapper.EServiceTemplateVersionMapperImpl;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template.version.mapper.EServiceTemplateVersionQuotasMapperImpl;
import it.pagopa.pn.interop.cucumber.steps.m2m.purpose.assistant.PurposePatchContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.purpose.assistant.PurposePatchOperationsAssistant;
import it.pagopa.pn.interop.cucumber.steps.m2m.purpose.assistant.ReversePurposePatchOperationsAssistant;
import it.pagopa.pn.interop.cucumber.steps.m2m.purpose.mapper.PurposeMapperImpl;
import it.pagopa.pn.interop.cucumber.steps.m2m.purpose.mapper.ReversePurposeMapperImpl;
import it.pagopa.pn.interop.cucumber.steps.m2m.purpose_template.assistant.PurposeTemplatePatchContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.purpose_template.assistant.PurposeTemplatePatchOperationsAssistant;
import it.pagopa.pn.interop.cucumber.steps.m2m.purpose_template.mapper.PurposeTemplateMapperImpl;
import it.pagopa.pn.interop.cucumber.steps.maintenance.TenantSetupState;
import it.pagopa.pn.interop.cucumber.utility.BlobFileCreator;
import it.pagopa.pn.interop.cucumber.utility.CommonUtils;
import it.pagopa.pn.interop.cucumber.utility.NotificationStore;
import it.pagopa.pn.interop.cucumber.utility.TracingFileUtils;
import it.pagopa.pn.interop.cucumber.utility.delay_service.DelayServiceImpl;
import it.pagopa.pn.interop.cucumber.utility.property_resolver.PropertyResolver;
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
        ProbingClientConfigs.class,
        DevAbstractInteropTracingClient.class,
        ExtraQaAbstractInteropTracingClient.class,
        CommonUtils.class,
        VoucherService.class,
        AsyncVoucherService.class,
        EServiceTemplateApiClientImpl.class,
        DescriptorAttributesMapperImpl.class,
        EServiceTemplateTestAssistant.class,
        EServiceTemplateStepContext.class,
        RiskAnalysisMapperImpl.class,
        it.pagopa.interop.authorization.service.DataPreparationService.class,
        M2MAgreementClientImpl.class,
        M2MCertifiedAttributeClientImpl.class,
        M2MPurposeClientImpl.class,
        M2MEserviceClientImpl.class,
        M2MEServiceTemplateClientImpl.class,
        M2MDataPreparationService.class,
        M2MEserviceDescriptorClientImpl.class,
        CucumberScopedBeans.class,
        M2MDelegationClient.class,
        M2MDeclaredAttributeClientImpl.class,
        M2MVerifiedAttributeClientImpl.class,
        M2MClientsClientImpl.class,
        M2MTenantClientImpl.class,
        M2MEserviceAttributeClientImpl.class,
        M2MEserviceTemplateAttributeClientImpl.class,
        M2MEventClientImpl.class,
        DelayServiceImpl.class,
        EServiceMapperImpl.class,
        EServiceNameMapperImpl.class,
        EServiceDelegationMapperImpl.class,
        EServiceDescriptionMapperImpl.class,
        EServiceDescriptorMapperImpl.class,
        EServiceTemplateMapperImpl.class,
        EServiceTemplateVersionQuotasMapperImpl.class,
        EServiceSeedFactory.class,
        DocumentMapperImpl.class,
        PurposeMapperImpl.class,
        ReversePurposeMapperImpl.class,
        EServiceTemplateVersionMapperImpl.class,
        EServiceTemplateMainMapperImpl.class,
        EServiceDescriptorQuotasMapperImpl.class,
        EServiceAttributeMapperImpl.class,
        M2MEventMapperImpl.class,
        EServicePatchContext.class,
        EServiceDescriptorPatchContext.class,
        PurposePatchContext.class,
        EServiceTemplatePatchContext.class,
        EServiceTemplateVersionPatchContext.class,
        EServicePatchOperationsAssistant.class,
        EServiceNamePatchOperationsAssistant.class,
        EServiceDelegationPatchOperationsAssistant.class,
        EServiceDescriptionPatchOperationsAssistant.class,
        EServiceDescriptorPatchOperationsAssistant.class,
        EServiceTemplateVersionPatchOperationsAssistant.class,
        EServiceTemplateSeedFactory.class,
        PurposePatchOperationsAssistant.class,
        ReversePurposePatchOperationsAssistant.class,
        EServiceTemplatePatchOperationsAssistant.class,
        EServiceTemplateVersionQuotasPatchOperationsAssistant.class,
        EServiceDescriptorQuotasPatchOperationsAssistant.class,
        ISelfcareClient.class,
        SelfcareClientImpl.class,
        PurposeTemplateClientImpl.class,
        M2MPurposeTemplateClientImpl.class,
        PurposeTemplateCommonContext.class,
        PurposeTemplatePatchContext.class,
        PurposeTemplatePatchOperationsAssistant.class,
        PurposeTemplateMapperImpl.class,
        NotificationClientImpl.class,
        NotificationConfigClient.class,
        PropertyResolver.class,
        NotificationStore.class,
        ProducerKeychainClientImpl.class,
        PurposeTemplateMapperImpl.class,
        M2MVersionsMapper.class,
        M2MVersionsMapperImpl.class,
        M2MV3EventMapperImpl.class,
        M2MV3AgreementClientImpl.class,
        M2MV3CertifiedAttributeClientImpl.class,
        M2MV3PurposeClientImpl.class,
        M2MV3EserviceClientImpl.class,
        M2MV3EServiceTemplateClientImpl.class,
        M2MV3EserviceDescriptorClientImpl.class,
        M2MV3DelegationClient.class,
        M2MV3DeclaredAttributeClientImpl.class,
        M2MV3VerifiedAttributeClientImpl.class,
        M2MV3ClientsClientImpl.class,
        M2MV3TenantClientImpl.class,
        M2MV3EserviceAttributeClientImpl.class,
        M2MV3EserviceTemplateAttributeClientImpl.class,
        M2MV3EventClientImpl.class,
        M2MV3PurposeTemplateClientImpl.class,
        M2MV3ProducerKeychainsClient.class,
        M2MV3UsersClient.class,
        IPurposeTemplateClient.class,
        ProbingClient.class,
        DevToolsClientImpl.class,
        InteropMaintenanceServiceImpl.class,
        EnvDebugLogger.class,
        TenantMapperImpl.class,
        TenantsProcessApiClientImpl.class,
        TenantSetupState.class
})
@EnableScheduling
@EnableConfigurationProperties
public class InteropCucumberSpringIntegration {

}