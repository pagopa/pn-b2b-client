package it.pagopa.pn.cucumber.steps;

import io.cucumber.spring.CucumberContextConfiguration;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.config.PnB2bClientTimingConfigs;
import it.pagopa.pn.client.b2b.pa.config.springconfig.*;
import it.pagopa.pn.client.b2b.pa.interop.service.impl.PnPollingInteropTracing;
import it.pagopa.pn.client.b2b.pa.parsing.config.PnLegalFactTokenProperty;
import it.pagopa.pn.client.b2b.pa.parsing.config.PnLegalFactTokens;
import it.pagopa.pn.client.b2b.pa.parsing.parser.impl.PnParser;
import it.pagopa.pn.client.b2b.pa.parsing.service.impl.PnParserService;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingFactory;
import it.pagopa.pn.client.b2b.pa.polling.impl.*;
import it.pagopa.pn.client.b2b.pa.service.IBffMandateServiceApi;
import it.pagopa.pn.client.b2b.pa.service.IMandateReverseServiceClient;
import it.pagopa.pn.client.b2b.pa.service.impl.*;
import it.pagopa.pn.client.b2b.pa.interop.config.InteropClientConfigs;
import it.pagopa.pn.client.b2b.pa.interop.config.springconfig.InteropConfiguration;
import it.pagopa.pn.client.b2b.pa.interop.service.impl.InteropTracingClientImpl;
import it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenSingleton;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import it.pagopa.pn.cucumber.interop.utility.TracingFileUtils;
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
        PnPollingServiceTimelineRapidV25.class,
        PnPollingServiceTimelineRapidV23.class,
        PnPollingServiceTimelineRapidV21.class,
        PnPollingServiceTimelineRapidV20.class,
        PnPollingServiceTimelineRapidV1.class,
        PnPollingServiceStatusRapidV25.class,
        PnPollingServiceStatusRapidV23.class,
        PnPollingServiceStatusRapidV21.class,
        PnPollingServiceStatusRapidV20.class,
        PnPollingServiceStatusRapidV1.class,
        PnPollingServiceTimelineSlowV25.class,
        PnPollingServiceTimelineSlowV23.class,
        PnPollingServiceTimelineSlowE2eV25.class,
        PnPollingServiceTimelineSlowE2eV23.class,
        PnPollingServiceTimelineSlowV21.class,
        PnPollingServiceTimelineSlowV20.class,
        PnPollingServiceTimelineSlowV1.class,
        PnPollingServiceStatusSlowV25.class,
        PnPollingServiceStatusSlowV23.class,
        PnPollingServiceStatusSlowV21.class,
        PnPollingServiceStatusSlowV20.class,
        PnPollingServiceStatusSlowV1.class,
        PnPollingServiceValidationStatusV1.class,
        PnPollingServiceValidationStatusV20.class,
        PnPollingServiceValidationStatusV21.class,
        PnPollingServiceValidationStatusV23.class,
        PnPollingServiceValidationStatusV25.class,
        PnPollingServiceValidationStatusNoAcceptedV23.class,
        PnPollingServiceValidationStatusNoAcceptedV25.class,
        PnPollingServiceValidationStatusAcceptedShortV23.class,
        PnPollingServiceValidationStatusAcceptedShortV25.class,
        PnPollingServiceWebhookV20.class,
        PnPollingServiceWebhookV23.class,
        PnPollingServiceWebhookV24.class,
        PnPollingServiceWebhookV25.class,
        PnPollingServiceValidationStatusAcceptedExtraRapidV23.class,
        PnPollingServiceValidationStatusAcceptedExtraRapidV25.class,
        PnPollingServiceStatusExtraRapidV23.class,
        PnPollingServiceStatusExtraRapidV25.class,
        PnPollingServiceTimelineExtraRapidV23.class,
        PnPollingServiceTimelineExtraRapidV25.class,
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
        InteropConfiguration.class,
        InteropClientConfigs.class,
        InteropTracingClientImpl.class,
        PnPollingInteropTracing.class,
        TracingFileUtils.class,
        TemplateEngineClientImpl.class
})
@EnableScheduling
@EnableConfigurationProperties
public class CucumberSpringIntegration {
}