package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templates_engine.model.NotificationReceiverLegalFact;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.TemplateEngineResult;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class NotificationReceiverLegalFactStrategy implements ITemplateEngineStrategy {

    private ITemplateEngineClient templateEngineClient;

    public NotificationReceiverLegalFactStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body) {
        NotificationReceiverLegalFact legalFact = body ? new NotificationReceiverLegalFact() : null;
        File file = templateEngineClient.notificationReceivedLegalFact(selectLanguage(language), legalFact);
        return new TemplateEngineResult(file);
    }
}
