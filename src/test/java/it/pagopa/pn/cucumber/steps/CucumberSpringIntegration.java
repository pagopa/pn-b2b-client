package it.pagopa.pn.cucumber.steps;

import io.cucumber.spring.CucumberContextConfiguration;
import it.pagopa.common.config.AwsConfig;
import it.pagopa.pn.client.b2b.generated.openapi.clients.generate.api.externalregistry.selfcare.privateapi.AooUoIdsApi;
import it.pagopa.pn.client.b2b.pa.cache.CacheConfig;
import it.pagopa.pn.client.b2b.pa.config.PnB2bClientTimingConfigs;
import it.pagopa.pn.client.b2b.pa.config.TemplateEngineConfigBean;
import it.pagopa.pn.client.b2b.pa.config.springconfig.ApiKeysConfiguration;
import it.pagopa.pn.client.b2b.pa.config.springconfig.BearerTokenConfiguration;
import it.pagopa.pn.client.b2b.pa.config.springconfig.LegalFactTokenConfiguration;
import it.pagopa.pn.client.b2b.pa.config.springconfig.MailSenderConfig;
import it.pagopa.pn.client.b2b.pa.config.springconfig.RestTemplateConfiguration;
import it.pagopa.pn.client.b2b.pa.config.springconfig.TaxIdConfiguration;
import it.pagopa.pn.client.b2b.pa.config.springconfig.TimingConfiguration;
import it.pagopa.pn.client.b2b.pa.mapper.NotificationSearchParamMapper;
import it.pagopa.pn.client.b2b.pa.parsing.config.PnLegalFactTokenProperty;
import it.pagopa.pn.client.b2b.pa.parsing.config.PnLegalFactTokens;
import it.pagopa.pn.client.b2b.pa.parsing.parser.impl.PnParser;
import it.pagopa.pn.client.b2b.pa.parsing.service.impl.PnParserService;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingFactory;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServicePaymentInfo;
import it.pagopa.pn.client.b2b.pa.polling.impl.v1.PnPollingServiceStatusRapidV1;
import it.pagopa.pn.client.b2b.pa.polling.impl.v1.PnPollingServiceStatusSlowV1;
import it.pagopa.pn.client.b2b.pa.polling.impl.v1.PnPollingServiceTimelineRapidV1;
import it.pagopa.pn.client.b2b.pa.polling.impl.v1.PnPollingServiceTimelineSlowV1;
import it.pagopa.pn.client.b2b.pa.polling.impl.v1.PnPollingServiceValidationStatusV1;
import it.pagopa.pn.client.b2b.pa.polling.impl.v20.PnPollingServiceStatusRapidV20;
import it.pagopa.pn.client.b2b.pa.polling.impl.v20.PnPollingServiceStatusSlowV20;
import it.pagopa.pn.client.b2b.pa.polling.impl.v20.PnPollingServiceTimelineRapidV20;
import it.pagopa.pn.client.b2b.pa.polling.impl.v20.PnPollingServiceTimelineSlowV20;
import it.pagopa.pn.client.b2b.pa.polling.impl.v20.PnPollingServiceValidationStatusV20;
import it.pagopa.pn.client.b2b.pa.polling.impl.v20.PnPollingServiceWebhookV20;
import it.pagopa.pn.client.b2b.pa.polling.impl.v21.PnPollingServiceStatusRapidV21;
import it.pagopa.pn.client.b2b.pa.polling.impl.v21.PnPollingServiceStatusSlowV21;
import it.pagopa.pn.client.b2b.pa.polling.impl.v21.PnPollingServiceTimelineRapidV21;
import it.pagopa.pn.client.b2b.pa.polling.impl.v21.PnPollingServiceTimelineSlowV21;
import it.pagopa.pn.client.b2b.pa.polling.impl.v21.PnPollingServiceValidationStatusV21;
import it.pagopa.pn.client.b2b.pa.polling.impl.v23.PnPollingServiceStatusExtraRapidV23;
import it.pagopa.pn.client.b2b.pa.polling.impl.v23.PnPollingServiceStatusRapidV23;
import it.pagopa.pn.client.b2b.pa.polling.impl.v23.PnPollingServiceStatusSlowV23;
import it.pagopa.pn.client.b2b.pa.polling.impl.v23.PnPollingServiceTimelineExtraRapidV23;
import it.pagopa.pn.client.b2b.pa.polling.impl.v23.PnPollingServiceTimelineRapidV23;
import it.pagopa.pn.client.b2b.pa.polling.impl.v23.PnPollingServiceTimelineSlowE2eV23;
import it.pagopa.pn.client.b2b.pa.polling.impl.v23.PnPollingServiceTimelineSlowV23;
import it.pagopa.pn.client.b2b.pa.polling.impl.v23.PnPollingServiceValidationStatusAcceptedExtraRapidV23;
import it.pagopa.pn.client.b2b.pa.polling.impl.v23.PnPollingServiceValidationStatusAcceptedShortV23;
import it.pagopa.pn.client.b2b.pa.polling.impl.v23.PnPollingServiceValidationStatusNoAcceptedV23;
import it.pagopa.pn.client.b2b.pa.polling.impl.v23.PnPollingServiceValidationStatusV23;
import it.pagopa.pn.client.b2b.pa.polling.impl.v23.PnPollingServiceWebhookV23;
import it.pagopa.pn.client.b2b.pa.polling.impl.v24.PnPollingServiceWebhookV24;
import it.pagopa.pn.client.b2b.pa.polling.impl.v25.PnPollingServiceStatusExtraRapidV25;
import it.pagopa.pn.client.b2b.pa.polling.impl.v25.PnPollingServiceStatusRapidV25;
import it.pagopa.pn.client.b2b.pa.polling.impl.v25.PnPollingServiceStatusSlowV25;
import it.pagopa.pn.client.b2b.pa.polling.impl.v25.PnPollingServiceTimelineExtraRapidV25;
import it.pagopa.pn.client.b2b.pa.polling.impl.v25.PnPollingServiceTimelineRapidV25;
import it.pagopa.pn.client.b2b.pa.polling.impl.v25.PnPollingServiceTimelineSlowE2eV25;
import it.pagopa.pn.client.b2b.pa.polling.impl.v25.PnPollingServiceTimelineSlowV25;
import it.pagopa.pn.client.b2b.pa.polling.impl.v25.PnPollingServiceValidationStatusAcceptedExtraRapidV25;
import it.pagopa.pn.client.b2b.pa.polling.impl.v25.PnPollingServiceValidationStatusAcceptedShortV25;
import it.pagopa.pn.client.b2b.pa.polling.impl.v25.PnPollingServiceValidationStatusNoAcceptedV25;
import it.pagopa.pn.client.b2b.pa.polling.impl.v25.PnPollingServiceValidationStatusV25;
import it.pagopa.pn.client.b2b.pa.polling.impl.v25.PnPollingServiceWebhookV25;
import it.pagopa.pn.client.b2b.pa.polling.impl.v26.PnPollingServiceStatusExtraRapidV26;
import it.pagopa.pn.client.b2b.pa.polling.impl.v26.PnPollingServiceStatusRapidV26;
import it.pagopa.pn.client.b2b.pa.polling.impl.v26.PnPollingServiceStatusSlowV26;
import it.pagopa.pn.client.b2b.pa.polling.impl.v26.PnPollingServiceTimelineExtraRapidV26;
import it.pagopa.pn.client.b2b.pa.polling.impl.v26.PnPollingServiceTimelineRapidV26;
import it.pagopa.pn.client.b2b.pa.polling.impl.v26.PnPollingServiceTimelineSlowE2eV26;
import it.pagopa.pn.client.b2b.pa.polling.impl.v26.PnPollingServiceTimelineSlowV26;
import it.pagopa.pn.client.b2b.pa.polling.impl.v26.PnPollingServiceValidationStatusAcceptedExtraRapidV26;
import it.pagopa.pn.client.b2b.pa.polling.impl.v26.PnPollingServiceValidationStatusAcceptedShortV26;
import it.pagopa.pn.client.b2b.pa.polling.impl.v26.PnPollingServiceValidationStatusNoAcceptedV26;
import it.pagopa.pn.client.b2b.pa.polling.impl.v26.PnPollingServiceValidationStatusV26;
import it.pagopa.pn.client.b2b.pa.polling.impl.v26.PnPollingServiceWebhookV26;
import it.pagopa.pn.client.b2b.pa.polling.impl.v27.PnPollingServiceStatusExtraRapidV27;
import it.pagopa.pn.client.b2b.pa.polling.impl.v27.PnPollingServiceStatusRapidV27;
import it.pagopa.pn.client.b2b.pa.polling.impl.v27.PnPollingServiceStatusSlowV27;
import it.pagopa.pn.client.b2b.pa.polling.impl.v27.PnPollingServiceTimelineExtraRapidV27;
import it.pagopa.pn.client.b2b.pa.polling.impl.v27.PnPollingServiceTimelineRapidV27;
import it.pagopa.pn.client.b2b.pa.polling.impl.v27.PnPollingServiceTimelineSlowE2eV27;
import it.pagopa.pn.client.b2b.pa.polling.impl.v27.PnPollingServiceTimelineSlowV27;
import it.pagopa.pn.client.b2b.pa.polling.impl.v27.PnPollingServiceValidationStatusAcceptedExtraRapidV27;
import it.pagopa.pn.client.b2b.pa.polling.impl.v27.PnPollingServiceValidationStatusAcceptedShortV27;
import it.pagopa.pn.client.b2b.pa.polling.impl.v27.PnPollingServiceValidationStatusNoAcceptedV27;
import it.pagopa.pn.client.b2b.pa.polling.impl.v27.PnPollingServiceValidationStatusV27;
import it.pagopa.pn.client.b2b.pa.polling.impl.v27.PnPollingServiceWebhookV27;
import it.pagopa.pn.client.b2b.pa.polling.impl.v28.PnPollingServiceStatusExtraRapidV28;
import it.pagopa.pn.client.b2b.pa.polling.impl.v28.PnPollingServiceStatusRapidV28;
import it.pagopa.pn.client.b2b.pa.polling.impl.v28.PnPollingServiceStatusSlowV28;
import it.pagopa.pn.client.b2b.pa.polling.impl.v28.PnPollingServiceTimelineExtraRapidV28;
import it.pagopa.pn.client.b2b.pa.polling.impl.v28.PnPollingServiceTimelineRapidV28;
import it.pagopa.pn.client.b2b.pa.polling.impl.v28.PnPollingServiceTimelineSlowE2eV28;
import it.pagopa.pn.client.b2b.pa.polling.impl.v28.PnPollingServiceTimelineSlowV28;
import it.pagopa.pn.client.b2b.pa.polling.impl.v28.PnPollingServiceValidationStatusAcceptedExtraRapidV28;
import it.pagopa.pn.client.b2b.pa.polling.impl.v28.PnPollingServiceValidationStatusAcceptedShortV28;
import it.pagopa.pn.client.b2b.pa.polling.impl.v28.PnPollingServiceValidationStatusNoAcceptedV28;
import it.pagopa.pn.client.b2b.pa.polling.impl.v28.PnPollingServiceValidationStatusV28;
import it.pagopa.pn.client.b2b.pa.polling.impl.v28.PnPollingServiceWebhookV28;
import it.pagopa.pn.client.b2b.pa.polling.impl.v29.PnPollingServiceStatusExtraRapidV29;
import it.pagopa.pn.client.b2b.pa.polling.impl.v29.PnPollingServiceStatusRapidV29;
import it.pagopa.pn.client.b2b.pa.polling.impl.v29.PnPollingServiceStatusSlowV29;
import it.pagopa.pn.client.b2b.pa.polling.impl.v29.PnPollingServiceTimelineExtraRapidV29;
import it.pagopa.pn.client.b2b.pa.polling.impl.v29.PnPollingServiceTimelineRapidV29;
import it.pagopa.pn.client.b2b.pa.polling.impl.v29.PnPollingServiceTimelineSlowE2eV29;
import it.pagopa.pn.client.b2b.pa.polling.impl.v29.PnPollingServiceTimelineSlowV29;
import it.pagopa.pn.client.b2b.pa.polling.impl.v29.PnPollingServiceValidationStatusAcceptedExtraRapidV29;
import it.pagopa.pn.client.b2b.pa.polling.impl.v29.PnPollingServiceValidationStatusAcceptedShortV29;
import it.pagopa.pn.client.b2b.pa.polling.impl.v29.PnPollingServiceValidationStatusNoAcceptedV29;
import it.pagopa.pn.client.b2b.pa.polling.impl.v29.PnPollingServiceValidationStatusV29;
import it.pagopa.pn.client.b2b.pa.polling.impl.v29.PnPollingServiceWebhookV29;
import it.pagopa.pn.client.b2b.pa.provider.DestinatarioRegistry;
import it.pagopa.pn.client.b2b.pa.provider.SenderInfoProvider;
import it.pagopa.pn.client.b2b.pa.service.DynamoDbService;
import it.pagopa.pn.client.b2b.pa.service.IBffMandateServiceApi;
import it.pagopa.pn.client.b2b.pa.service.IMandateReverseServiceClient;
import it.pagopa.pn.client.b2b.pa.service.impl.AooUoIdsClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.B2BDeliveryPushServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.B2BRecipientExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.B2BSenderReadClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.B2BUserAttributesExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.B2bMandateServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.BffMandateServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.EmdIntegrationApiImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.IPnInteropProbingClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.IPnLegalPersonAuthClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.IPnLegalPersonVirtualKeyServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.IPnTosPrivacyClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.MandateB2BExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.MandateInternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.MandateReverseServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PaperCalculatorClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnApiKeyManagerExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnAppIOB2bExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnBFFRecipientNotificationClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnBffPaClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnCfgClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnDowntimeLogsExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnEcInternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnExternalChannelsInternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnExternalChannelsServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnExternalRegistryPrivateUserApiImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnExternalServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnGPDClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnIOConnectorClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnIoUserAttributerExternaClient;
import it.pagopa.pn.client.b2b.pa.service.impl.PnMandateAppIoClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnNotificationCostClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaB2bExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaB2bExternalInformalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaB2bInternalInformalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaperChannelClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaperTrackerClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaymentInfoClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPrivateDeliveryPushExternalClient;
import it.pagopa.pn.client.b2b.pa.service.impl.PnRaddAlternativeClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnRaddAlternativeV2ClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnRaddCapCoverageClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnRaddFsuClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnRaddNetVpceClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnRaddVpceAdapter;
import it.pagopa.pn.client.b2b.pa.service.impl.PnSafeStoragePrivateClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnServiceDeskClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnWebMandateExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnWebRecipientExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnWebUserAttributesInternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnWebhookB2bExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.RecipientB2BExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.RecipientInternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.ReworkTimelineClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.TemplateEngineClientImpl;
import it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenSingleton;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableAuthTokenRaddCognito;
import it.pagopa.pn.client.b2b.pa.utils.DataPreparationRaddVpceService;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import it.pagopa.pn.cucumber.steps.censimentoStimeMittenti.model.StimeMittentiContext;
import it.pagopa.pn.cucumber.steps.delayer.client.DelayerLambdaClient;
import it.pagopa.pn.cucumber.steps.delayer.client.PortfatLambdaClient;
import it.pagopa.pn.cucumber.steps.delayer.loader.DelayerCsvLoader;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerContext;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerSuiteContext;
import it.pagopa.pn.cucumber.steps.delayer.planner.DelayerPlanner;
import it.pagopa.pn.cucumber.steps.delayer.service.DelayerSevice;
import it.pagopa.pn.cucumber.steps.delayer.utils.DelayerPaperDeliveryUtils;
import it.pagopa.pn.cucumber.steps.delayer.validator.DelayerValidator;
import it.pagopa.pn.cucumber.steps.informalNotification.builders.InformalRecipientBuilder;
import it.pagopa.pn.cucumber.steps.informalNotification.mapper.InformalNotificationRequestMapper;
import it.pagopa.pn.cucumber.steps.informalNotification.provider.InformalMessageProvider;
import it.pagopa.pn.cucumber.steps.informalNotification.utils.NotificationInformalUtilsV1;
import it.pagopa.pn.cucumber.steps.paperTracker.parser.EventTimelineParser;
import it.pagopa.pn.cucumber.steps.paperTracker.proxy.PaperTrackerSchemaValidatorProxy;
import it.pagopa.pn.cucumber.steps.recipient.OtpCodeService;
import it.pagopa.pn.cucumber.steps.templateEngine.TemplateConfiguration;
import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateEngineContextFactory;
import it.pagopa.pn.cucumber.steps.ioMock.*;
import it.pagopa.pn.cucumber.steps.ioMock.context.IoMockScenarioContext;
import it.pagopa.pn.cucumber.steps.utilitySteps.CieGeneratorTool;
import it.pagopa.pn.cucumber.utils.LambdaInvoker;
import it.pagopa.pn.cucumber.utils.notificationsearch.NotificationSearchCriteriaMapper;
import it.pagopa.pn.cucumber.utils.validator.SchemaValidator;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;

@CucumberContextConfiguration
@SpringBootTest(classes = {
        JacksonAutoConfiguration.class,
        ApiKeysConfiguration.class,
        BearerTokenConfiguration.class,
        TimingConfiguration.class,
        RestTemplateConfiguration.class,
        AwsConfig.class,
        PnPaB2bExternalClientImpl.class,
        PnWebRecipientExternalClientImpl.class,
        PnWebhookB2bExternalClientImpl.class,
        PnWebMandateExternalClientImpl.class,
        B2bMandateServiceClientImpl.class,
        MandateInternalClientImpl.class,
        MandateB2BExternalClientImpl.class,
        PnExternalServiceClientImpl.class,
        PnWebUserAttributesInternalClientImpl.class,
        PnAppIOB2bExternalClientImpl.class,
        PnApiKeyManagerExternalClientImpl.class,
        PnDowntimeLogsExternalClientImpl.class,
        PnIoUserAttributerExternaClient.class,
        PnPaperTrackerClientImpl.class,
        PnBffPaClientImpl.class,
        PnPrivateDeliveryPushExternalClient.class,
        InteropTokenSingleton.class,
        PnServiceDeskClientImpl.class,
        PnGPDClientImpl.class,
        PnPaymentInfoClientImpl.class,
        PnRaddFsuClientImpl.class,
        PnRaddAlternativeClientImpl.class,
        PnRaddAlternativeV2ClientImpl.class,
        PnRaddCapCoverageClientImpl.class,
        PnCfgClientImpl.class,
        TimingForPolling.class,
        PnB2bClientTimingConfigs.class,
        PnIOConnectorClientImpl.class,
        PnPollingFactory.class,
        PnPollingServicePaymentInfo.class,
        CieGeneratorTool.class,
        TaxIdConfiguration.class,

        PnPollingServiceTimelineRapidV1.class,
        PnPollingServiceStatusRapidV1.class,
        PnPollingServiceTimelineSlowV1.class,
        PnPollingServiceStatusSlowV1.class,
        PnPollingServiceValidationStatusV1.class,

        PnPollingServiceTimelineRapidV20.class,
        PnPollingServiceStatusRapidV20.class,
        PnPollingServiceTimelineSlowV20.class,
        PnPollingServiceStatusSlowV20.class,
        PnPollingServiceValidationStatusV20.class,
        PnPollingServiceWebhookV20.class,

        PnPollingServiceTimelineRapidV21.class,
        PnPollingServiceStatusRapidV21.class,
        PnPollingServiceTimelineSlowV21.class,
        PnPollingServiceStatusSlowV21.class,
        PnPollingServiceValidationStatusV21.class,

        PnPollingServiceTimelineRapidV23.class,
        PnPollingServiceStatusRapidV23.class,
        PnPollingServiceTimelineSlowV23.class,
        PnPollingServiceTimelineSlowE2eV23.class,
        PnPollingServiceStatusSlowV23.class,
        PnPollingServiceValidationStatusV23.class,
        PnPollingServiceValidationStatusNoAcceptedV23.class,
        PnPollingServiceValidationStatusAcceptedShortV23.class,
        PnPollingServiceWebhookV23.class,
        PnPollingServiceValidationStatusAcceptedExtraRapidV23.class,
        PnPollingServiceStatusExtraRapidV23.class,
        PnPollingServiceTimelineExtraRapidV23.class,

        PnPollingServiceWebhookV24.class,

        PnPollingServiceTimelineRapidV25.class,
        PnPollingServiceStatusRapidV25.class,
        PnPollingServiceTimelineSlowV25.class,
        PnPollingServiceTimelineSlowE2eV25.class,
        PnPollingServiceStatusSlowV25.class,
        PnPollingServiceValidationStatusV25.class,
        PnPollingServiceValidationStatusNoAcceptedV25.class,
        PnPollingServiceValidationStatusAcceptedShortV25.class,
        PnPollingServiceWebhookV25.class,
        PnPollingServiceValidationStatusAcceptedExtraRapidV25.class,
        PnPollingServiceStatusExtraRapidV25.class,
        PnPollingServiceTimelineExtraRapidV25.class,

        PnPollingServiceTimelineRapidV26.class,
        PnPollingServiceStatusRapidV26.class,
        PnPollingServiceTimelineSlowV26.class,
        PnPollingServiceTimelineSlowE2eV26.class,
        PnPollingServiceStatusSlowV26.class,
        PnPollingServiceValidationStatusV26.class,
        PnPollingServiceValidationStatusNoAcceptedV26.class,
        PnPollingServiceValidationStatusAcceptedShortV26.class,
        PnPollingServiceWebhookV26.class,
        PnPollingServiceValidationStatusAcceptedExtraRapidV26.class,
        PnPollingServiceStatusExtraRapidV26.class,
        PnPollingServiceTimelineExtraRapidV26.class,

        PnPollingServiceTimelineRapidV27.class,
        PnPollingServiceStatusRapidV27.class,
        PnPollingServiceTimelineSlowV27.class,
        PnPollingServiceTimelineSlowE2eV27.class,
        PnPollingServiceStatusSlowV27.class,
        PnPollingServiceValidationStatusV27.class,
        PnPollingServiceValidationStatusNoAcceptedV27.class,
        PnPollingServiceValidationStatusAcceptedShortV27.class,
        PnPollingServiceWebhookV27.class,
        PnPollingServiceValidationStatusAcceptedExtraRapidV27.class,
        PnPollingServiceStatusExtraRapidV27.class,
        PnPollingServiceTimelineExtraRapidV27.class,

        PnPollingServiceTimelineRapidV28.class,
        PnPollingServiceStatusRapidV28.class,
        PnPollingServiceTimelineSlowV28.class,
        PnPollingServiceTimelineSlowE2eV28.class,
        PnPollingServiceStatusSlowV28.class,
        PnPollingServiceValidationStatusV28.class,
        PnPollingServiceValidationStatusNoAcceptedV28.class,
        PnPollingServiceValidationStatusAcceptedShortV28.class,
        PnPollingServiceWebhookV28.class,
        PnPollingServiceValidationStatusAcceptedExtraRapidV28.class,
        PnPollingServiceStatusExtraRapidV28.class,
        PnPollingServiceTimelineExtraRapidV28.class,

        PnPollingServiceTimelineRapidV29.class,
        PnPollingServiceStatusRapidV29.class,
        PnPollingServiceTimelineSlowV29.class,
        PnPollingServiceTimelineSlowE2eV29.class,
        PnPollingServiceStatusSlowV29.class,
        PnPollingServiceValidationStatusV29.class,
        PnPollingServiceValidationStatusNoAcceptedV29.class,
        PnPollingServiceValidationStatusAcceptedShortV29.class,
        PnPollingServiceWebhookV29.class,
        PnPollingServiceValidationStatusAcceptedExtraRapidV29.class,
        PnPollingServiceStatusExtraRapidV29.class,
        PnPollingServiceTimelineExtraRapidV29.class,
        PortfatLambdaClient.class,
        MailSenderConfig.class,
        PnParserService.class,
        LegalFactTokenConfiguration.class,
        PnLegalFactTokenProperty.class,
        PnLegalFactTokens.class,
        PnParser.class,
        PnExternalChannelsServiceClientImpl.class,
        PnSafeStoragePrivateClientImpl.class,
        PnBFFRecipientNotificationClientImpl.class,
        IPnInteropProbingClientImpl.class,
        PaperCalculatorClientImpl.class,
        PnExternalRegistryPrivateUserApiImpl.class,
        IMandateReverseServiceClient.class,
        MandateReverseServiceClientImpl.class,
        B2BRecipientExternalClientImpl.class,
        RecipientInternalClientImpl.class,
        RecipientB2BExternalClientImpl.class,
        IBffMandateServiceApi.class,
        BffMandateServiceClientImpl.class,
        B2BDeliveryPushServiceClientImpl.class,
        B2BUserAttributesExternalClientImpl.class,
        IPnLegalPersonAuthClientImpl.class,
        IPnLegalPersonVirtualKeyServiceClientImpl.class,
        IPnTosPrivacyClientImpl.class,
        TemplateEngineClientImpl.class,
        PnPaperChannelClientImpl.class,
        TemplateConfiguration.class,
        TemplateEngineContextFactory.class,
        EmdIntegrationApiImpl.class,
        SettableAuthTokenRaddCognito.class,
        EventTimelineParser.class,
        PnMandateAppIoClientImpl.class,
        ReworkTimelineClientImpl.class,
        PnPaB2bInternalInformalClientImpl.class,
        LambdaInvoker.class,
        TemplateEngineConfigBean.class,
        SchemaValidator.class,
        PaperTrackerSchemaValidatorProxy.class,
        DelayerLambdaClient.class,
        DelayerSevice.class,
        DelayerValidator.class,
        DelayerContext.class,
        DelayerSuiteContext.class,
        DelayerPaperDeliveryUtils.class,
        DelayerCsvLoader.class,
        DelayerPlanner.class,
        PnExternalChannelsInternalClientImpl.class,
        PnEcInternalClientImpl.class,
        DynamoDbService.class,
        AwsConfig.class,
        SenderInfoProvider.class,
        DestinatarioRegistry.class,
        CacheConfig.class,
        AooUoIdsClientImpl.class,
        AooUoIdsApi.class,
        OtpCodeService.class,
        InformalRecipientBuilder.class,
        InformalNotificationRequestMapper.class,
        InformalMessageProvider.class,
        PnRaddVpceAdapter.class,
        PnRaddNetVpceClientImpl.class,
        DataPreparationRaddVpceService.class,
        PnNotificationCostClientImpl.class,
        NotificationInformalUtilsV1.class,
        PnPaB2bExternalInformalClientImpl.class,
        StimeMittentiContext.class,
        NotificationSearchParamMapper.class,
        NotificationSearchCriteriaMapper.class,
        B2BSenderReadClientImpl.class,
        NotificationInformalUtilsV1.class,
        PnPaB2bExternalInformalClientImpl.class,
        IoMockScenarioContext.class,
        IOMockCommonSteps.class,
})
@EnableScheduling
@EnableConfigurationProperties
public class CucumberSpringIntegration {
}