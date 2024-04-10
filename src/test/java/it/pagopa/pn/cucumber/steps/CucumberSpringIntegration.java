package it.pagopa.pn.cucumber.steps;

import io.cucumber.spring.CucumberContextConfiguration;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.config.PnB2bClientTimingConfigs;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingFactory;
import it.pagopa.pn.client.b2b.pa.polling.impl.*;
import it.pagopa.pn.client.b2b.pa.service.impl.*;
import it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenSingleton;
import it.pagopa.pn.client.b2b.pa.config.springconfig.ApiKeysConfiguration;
import it.pagopa.pn.client.b2b.pa.config.springconfig.BearerTokenConfiguration;
import it.pagopa.pn.client.b2b.pa.config.springconfig.RestTemplateConfiguration;
import it.pagopa.pn.client.b2b.pa.config.springconfig.TimingConfiguration;
import it.pagopa.pn.client.b2b.pa.utils.TimingForTimeline;
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
        PnServiceDeskClientImplNoApiKey.class,
        PnServiceDeskClientImplWrongApiKey.class,
        PnGPDClientImpl.class,
        PnPaymentInfoClientImpl.class,
        PnRaddFsuClientImpl.class,
        PnRaddAlternativeClientImpl.class,
        TimingForTimeline.class,
        PnB2bClientTimingConfigs.class,
        PnPollingFactory.class,
        PnPollingServiceTimelineRapidV23.class,
        PnPollingServiceTimelineRapidV21.class,
        PnPollingServiceTimelineRapidV20.class,
        PnPollingServiceTimelineRapidV1.class,
        PnPollingServiceStatusRapidV23.class,
        PnPollingServiceStatusRapidV21.class,
        PnPollingServiceStatusRapidV20.class,
        PnPollingServiceStatusRapidV1.class,
        PnPollingServiceTimelineSlowV23.class,
        PnPollingServiceTimelineSlowV21.class,
        PnPollingServiceTimelineSlowV20.class,
        PnPollingServiceTimelineSlowV1.class,
        PnPollingServiceStatusSlowV23.class,
        PnPollingServiceStatusSlowV21.class,
        PnPollingServiceStatusSlowV20.class,
        PnPollingServiceStatusSlowV1.class,
        PnPollingServiceValidationStatusV1.class,
        PnPollingServiceValidationStatusV20.class,
        PnPollingServiceValidationStatusV21.class,
        PnPollingServiceValidationStatusV23.class,
        PnPollingServiceValidationStatusNoAcceptedV23.class,
        PnPollingServiceValidationStatusAcceptedShortV23.class
})
@EnableScheduling
@EnableConfigurationProperties
public class CucumberSpringIntegration {
}