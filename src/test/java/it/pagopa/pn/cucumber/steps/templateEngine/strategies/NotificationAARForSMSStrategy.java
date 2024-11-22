package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templates_engine.model.NotificationAARForSMS;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

@Component
public class NotificationAARForSMSStrategy implements ITemplateEngineStrategy {
    private ITemplateEngineClient templateEngineClient;

    public NotificationAARForSMSStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        NotificationAARForSMS notificationAARForSMS = createRequest(body, context);
        String file = templateEngineClient.notificationAARForSMS(selectLanguage(language), notificationAARForSMS);
        return new TemplateEngineResult(file);
    }

    private NotificationAARForSMS createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new NotificationAARForSMS()
                .notification(context.getNotification());
    }
}
