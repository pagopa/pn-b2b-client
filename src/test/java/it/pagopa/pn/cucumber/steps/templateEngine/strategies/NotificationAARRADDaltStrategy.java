package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templates_engine.model.NotificationAARRADDalt;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.TemplateEngineResult;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class NotificationAARRADDaltStrategy implements ITemplateEngineStrategy {
    private ITemplateEngineClient templateEngineClient;

    public NotificationAARRADDaltStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }


    @Override
    public TemplateEngineResult retrieveTemplate(String language) {
        NotificationAARRADDalt legalFact = new  NotificationAARRADDalt();
        File file= templateEngineClient. notificationAARRADDalt(selectLanguage(language), legalFact);
        return new TemplateEngineResult(file);
    }
}
