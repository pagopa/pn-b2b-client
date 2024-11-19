package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templates_engine.model.NotificationAARForEMAIL;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.TemplateEngineResult;
import org.springframework.stereotype.Component;

@Component
public class NotificationAARForEMAILStrategy implements ITemplateEngineStrategy {
    private ITemplateEngineClient templateEngineClient;

    public NotificationAARForEMAILStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body) {
        NotificationAARForEMAIL legalFact = new  NotificationAARForEMAIL();
        String result = templateEngineClient.notificationAARForEMAIL(selectLanguage(language), legalFact);
        return new TemplateEngineResult(result);
    }
}
