package it.pagopa.pn.cucumber.steps;

import io.cucumber.spring.CucumberContextConfiguration;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.config.PnB2bClientTimingConfigs;
import it.pagopa.pn.client.b2b.pa.config.springconfig.ApiKeysConfiguration;
import it.pagopa.pn.client.b2b.pa.config.springconfig.BearerTokenConfiguration;
import it.pagopa.pn.client.b2b.pa.config.springconfig.LegalFactTokenConfiguration;
import it.pagopa.pn.client.b2b.pa.config.springconfig.MailSenderConfig;
import it.pagopa.pn.client.b2b.pa.config.springconfig.RestTemplateConfiguration;
import it.pagopa.pn.client.b2b.pa.config.springconfig.TimingConfiguration;
import it.pagopa.pn.client.b2b.pa.parsing.config.PnLegalFactTokenProperty;
import it.pagopa.pn.client.b2b.pa.parsing.config.PnLegalFactTokens;
import it.pagopa.pn.client.b2b.pa.parsing.parser.impl.PnParser;
import it.pagopa.pn.client.b2b.pa.parsing.service.impl.PnParserService;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingFactory;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceStatusExtraRapidV23;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceStatusExtraRapidV25;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceStatusExtraRapidV26;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceStatusRapidV1;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceStatusRapidV20;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceStatusRapidV21;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceStatusRapidV23;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceStatusRapidV25;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceStatusRapidV26;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceStatusSlowV1;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceStatusSlowV20;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceStatusSlowV21;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceStatusSlowV23;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceStatusSlowV25;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceStatusSlowV26;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceTimelineExtraRapidV23;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceTimelineExtraRapidV25;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceTimelineExtraRapidV26;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceTimelineRapidV1;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceTimelineRapidV20;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceTimelineRapidV21;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceTimelineRapidV23;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceTimelineRapidV25;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceTimelineRapidV26;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceTimelineSlowE2eV23;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceTimelineSlowE2eV25;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceTimelineSlowE2eV26;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceTimelineSlowV1;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceTimelineSlowV20;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceTimelineSlowV21;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceTimelineSlowV23;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceTimelineSlowV25;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceTimelineSlowV26;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceValidationStatusAcceptedExtraRapidV23;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceValidationStatusAcceptedExtraRapidV25;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceValidationStatusAcceptedExtraRapidV26;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceValidationStatusAcceptedShortV23;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceValidationStatusAcceptedShortV25;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceValidationStatusAcceptedShortV26;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceValidationStatusNoAcceptedV23;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceValidationStatusNoAcceptedV25;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceValidationStatusNoAcceptedV26;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceValidationStatusV1;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceValidationStatusV20;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceValidationStatusV21;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceValidationStatusV23;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceValidationStatusV25;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceValidationStatusV26;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceWebhookV20;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceWebhookV23;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceWebhookV24;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceWebhookV25;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceWebhookV26;
import it.pagopa.pn.client.b2b.pa.service.IBffMandateServiceApi;
import it.pagopa.pn.client.b2b.pa.service.IMandateReverseServiceClient;
import it.pagopa.pn.client.b2b.pa.service.impl.B2BDeliveryPushServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.B2BRecipientExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.B2BUserAttributesExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.B2bMandateServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.BffMandateServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.IPnInteropProbingClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.IPnLegalPersonAuthClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.IPnLegalPersonVirtualKeyServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.IPnTosPrivacyClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.MandateReverseServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PaperCalculatorClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnApiKeyManagerExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnAppIOB2bExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnBFFRecipientNotificationClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnDowntimeLogsExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnExternalChannelsServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnExternalRegistryPrivateUserApiImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnExternalServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnGPDClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnIoUserAttributerExternaClient;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaB2bExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaymentInfoClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPrivateDeliveryPushExternalClient;
import it.pagopa.pn.client.b2b.pa.service.impl.PnRaddAlternativeClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnRaddFsuClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnSafeStoragePrivateClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnServiceDeskClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnWebMandateExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnWebPaClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnWebRecipientExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnWebUserAttributesExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnWebhookB2bExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.TemplateEngineClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.EmdMessageApiImpl;
import it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenSingleton;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import it.pagopa.pn.cucumber.steps.templateEngine.TemplateConfiguration;
import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateEngineContextFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;

@CucumberContextConfiguration
@SpringBootTest(classes = {
        ApiKeysConfiguration.class,
        BearerTokenConfiguration.class,
        TimingConfiguration.class,
        RestTemplateConfiguration.class,
        PnPaB2bUtils.class,
        PnPaB2bExternalClientImpl.class,
        PnWebRecipientExternalClientImpl.class,
        PnWebhookB2bExternalClientImpl.class,
        PnWebMandateExternalClientImpl.class,
        B2bMandateServiceClientImpl.class,
        PnExternalServiceClientImpl.class,
        PnWebUserAttributesExternalClientImpl.class,
        PnAppIOB2bExternalClientImpl.class,
        PnApiKeyManagerExternalClientImpl.class,
        PnDowntimeLogsExternalClientImpl.class,
        PnIoUserAttributerExternaClient.class,
        PnWebPaClientImpl.class,
        PnPrivateDeliveryPushExternalClient.class,
        InteropTokenSingleton.class,
        PnServiceDeskClientImpl.class,
        PnGPDClientImpl.class,
        PnPaymentInfoClientImpl.class,
        PnRaddFsuClientImpl.class,
        PnRaddAlternativeClientImpl.class,
        TimingForPolling.class,
        PnB2bClientTimingConfigs.class,
        PnPollingFactory.class,
        PnPollingServiceTimelineRapidV26.class,
        PnPollingServiceTimelineRapidV25.class,
        PnPollingServiceTimelineRapidV23.class,
        PnPollingServiceTimelineRapidV21.class,
        PnPollingServiceTimelineRapidV20.class,
        PnPollingServiceTimelineRapidV1.class,
        PnPollingServiceStatusRapidV26.class,
        PnPollingServiceStatusRapidV25.class,
        PnPollingServiceStatusRapidV23.class,
        PnPollingServiceStatusRapidV21.class,
        PnPollingServiceStatusRapidV20.class,
        PnPollingServiceStatusRapidV1.class,
        PnPollingServiceTimelineSlowV26.class,
        PnPollingServiceTimelineSlowV25.class,
        PnPollingServiceTimelineSlowV23.class,
        PnPollingServiceTimelineSlowE2eV26.class,
        PnPollingServiceTimelineSlowE2eV25.class,
        PnPollingServiceTimelineSlowE2eV23.class,
        PnPollingServiceTimelineSlowV21.class,
        PnPollingServiceTimelineSlowV20.class,
        PnPollingServiceTimelineSlowV1.class,
        PnPollingServiceStatusSlowV26.class,
        PnPollingServiceStatusSlowV25.class,
        PnPollingServiceStatusSlowV23.class,
        PnPollingServiceStatusSlowV21.class,
        PnPollingServiceStatusSlowV20.class,
        PnPollingServiceStatusSlowV1.class,
        PnPollingServiceValidationStatusV1.class,
        PnPollingServiceValidationStatusV20.class,
        PnPollingServiceValidationStatusV21.class,
        PnPollingServiceValidationStatusV23.class,
        PnPollingServiceValidationStatusV26.class,
        PnPollingServiceValidationStatusV25.class,
        PnPollingServiceValidationStatusNoAcceptedV23.class,
        PnPollingServiceValidationStatusNoAcceptedV25.class,
        PnPollingServiceValidationStatusNoAcceptedV26.class,
        PnPollingServiceValidationStatusAcceptedShortV23.class,
        PnPollingServiceValidationStatusAcceptedShortV25.class,
        PnPollingServiceValidationStatusAcceptedShortV26.class,
        PnPollingServiceWebhookV20.class,
        PnPollingServiceWebhookV23.class,
        PnPollingServiceWebhookV24.class,
        PnPollingServiceWebhookV25.class,
        PnPollingServiceWebhookV26.class,
        PnPollingServiceValidationStatusAcceptedExtraRapidV23.class,
        PnPollingServiceValidationStatusAcceptedExtraRapidV25.class,
        PnPollingServiceValidationStatusAcceptedExtraRapidV26.class,
        PnPollingServiceStatusExtraRapidV23.class,
        PnPollingServiceStatusExtraRapidV25.class,
        PnPollingServiceStatusExtraRapidV26.class,
        PnPollingServiceTimelineExtraRapidV23.class,
        PnPollingServiceTimelineExtraRapidV25.class,
        PnPollingServiceTimelineExtraRapidV26.class,
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
        IBffMandateServiceApi.class,
        BffMandateServiceClientImpl.class,
        B2BDeliveryPushServiceClientImpl.class,
        B2BUserAttributesExternalClientImpl.class,
        IPnLegalPersonAuthClientImpl.class,
        IPnLegalPersonVirtualKeyServiceClientImpl.class,
        IPnTosPrivacyClientImpl.class,
        TemplateEngineClientImpl.class,
        TemplateConfiguration.class,
        TemplateEngineContextFactory.class,
        EmdMessageApiImpl.class
})
@EnableScheduling
@EnableConfigurationProperties
public class CucumberSpringIntegration {
}