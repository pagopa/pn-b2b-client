package it.pagopa.pn.client.b2b.pa.cucumber.test;

import io.cucumber.spring.CucumberContextConfiguration;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.testclient.PnSafeStorageInfoExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.impl.*;
import it.pagopa.pn.client.b2b.pa.springconfig.ApiKeysConfiguration;
import it.pagopa.pn.client.b2b.pa.springconfig.BearerTokenConfiguration;
import it.pagopa.pn.client.b2b.pa.springconfig.RestTemplateConfiguration;
import it.pagopa.pn.client.b2b.pa.testclient.PnWebMandateExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.testclient.PnWebRecipientExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.testclient.PnWebhookB2bExternalClientImpl;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = {
        ApiKeysConfiguration.class,
        BearerTokenConfiguration.class,
        RestTemplateConfiguration.class,
        PnPaB2bUtils.class,
        PnPaB2bExternalClientImpl.class,
        PnWebRecipientExternalClientImpl.class,
        PnWebhookB2bExternalClientImpl.class,
        PnWebMandateExternalClientImpl.class,
        PnSafeStorageInfoExternalClientImpl.class
})
public class CucumberSpringIntegration {

}
