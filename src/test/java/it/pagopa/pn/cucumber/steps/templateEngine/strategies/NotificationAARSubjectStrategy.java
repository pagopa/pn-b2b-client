package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.NotificationAARForSubject;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.NotificationReceivedLegalFact;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

@Component
public class NotificationAARSubjectStrategy implements ITemplateEngineStrategy{
    private ITemplateEngineClient templateEngineClient;

    public NotificationAARSubjectStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        NotificationReceivedLegalFact subject = createRequest(body, context);
        String file = templateEngineClient.notificationAARSubject(selectLanguage(language), subject);
        return new TemplateEngineResult(file);
    }

    private NotificationReceivedLegalFact createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new NotificationReceivedLegalFact()
                .notification(context.getNotification());
    }
}
