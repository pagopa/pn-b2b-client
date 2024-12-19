package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.*;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateNotification;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class NotificationAARSubjectStrategy implements ITemplateEngineStrategy{
    private ITemplateEngineClient templateEngineClient;

    public NotificationAARSubjectStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        NotificationAarForSubject subject = createRequest(body, context);
        String file = templateEngineClient.notificationAARSubject(selectLanguage(language), subject);
        return new TemplateEngineResult(file);
    }

    @Override
    public String getTextToCheckLanguage(String language) {
        return "SEND - Nuova notifica da";
    }

    private NotificationAarForSubject createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new NotificationAarForSubject()
                .notification(createNotification(context));
    }

    private AarForSubjectNotification createNotification(TemplateRequestContext context) {
        return Optional.ofNullable(context.getNotification())
                .map(data -> new AarForSubjectNotification()
                        .sender(createSender(data)))
                .orElse(null);
    }

    private AarForSubjectSender createSender(TemplateNotification notification) {
        return Optional.ofNullable(notification.getSender())
                .map(data -> new AarForSubjectSender()
                        .paDenomination(data.getPaDenomination()))
                .orElse(null);
    }
}
