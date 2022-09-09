package it.pagopa.pn.client.b2b.pa.cucumber.test;

import io.cucumber.spring.CucumberContextConfiguration;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.impl.PnPaB2bExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.impl.PnPaB2bInternalClientImpl;
import it.pagopa.pn.client.b2b.pa.impl.PnWebRecipientExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.impl.PnWebhookB2bExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.springconfig.ApiKeysConfiguration;
import it.pagopa.pn.client.b2b.pa.springconfig.BearerTokenConfiguration;
import it.pagopa.pn.client.b2b.pa.springconfig.RestTemplateConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = {
        PnPaB2bExternalClientImpl.class,
        PnPaB2bInternalClientImpl.class,
        ApiKeysConfiguration.class,
        BearerTokenConfiguration.class,
        RestTemplateConfiguration.class,
        PnPaB2bUtils.class,
        PnWebRecipientExternalClientImpl.class,
        PnWebhookB2bExternalClientImpl.class,
})
public class CucumberSpringIntegration {
}