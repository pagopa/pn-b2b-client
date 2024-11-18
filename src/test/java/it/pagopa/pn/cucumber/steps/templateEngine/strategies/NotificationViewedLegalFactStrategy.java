package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templates_engine.model.NotificationViewedLegalFact;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.TemplateEngineResult;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class NotificationViewedLegalFactStrategy  implements ITemplateEngineStrategy{
    private ITemplateEngineClient templateEngineClient;

    public NotificationViewedLegalFactStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language) {
        NotificationViewedLegalFact legalFact = new  NotificationViewedLegalFact();
        File file = templateEngineClient.notificationViewedLegalFact(selectLanguage(language), legalFact);
        return new TemplateEngineResult(file);
    }
}
