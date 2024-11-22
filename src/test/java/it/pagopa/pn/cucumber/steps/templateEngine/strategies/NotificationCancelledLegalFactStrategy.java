package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templates_engine.model.NotificationCancelledLegalFact;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class NotificationCancelledLegalFactStrategy implements ITemplateEngineStrategy {

    private ITemplateEngineClient templateEngineClient;

    public NotificationCancelledLegalFactStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        NotificationCancelledLegalFact legalFact = createRequest(body, context);
        File file = templateEngineClient.notificationCancelledLegalFact(selectLanguage(language), legalFact);
        return new TemplateEngineResult(file);
    }

    private NotificationCancelledLegalFact createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new NotificationCancelledLegalFact()
                .notification(context.getNotification())
                .recipient(context.getRecipient())
                .notificationCancelledDate(context.getNotificationCancelledDate());
    }
}
