package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.*;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateNotification;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Optional;

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
                .notification(createNotification(context))
                .notificationCancelledDate(context.getNotificationCancelledDate());
    }

    private NotificationCancelledNotification createNotification(TemplateRequestContext context) {
        return Optional.ofNullable(context.getNotification())
                .map(data -> new NotificationCancelledNotification()
                        .iun(data.getIun())
                        .sender(createSender(data))
                        .recipients(createRecipients(data)))
                .orElse(null);
    }

    private NotificationCancelledSender createSender(TemplateNotification notification) {
        return Optional.ofNullable(notification.getSender())
                .map(data -> new NotificationCancelledSender()
                        .paDenomination(data.getPaDenomination()))
                .orElse(null);
    }

    private List<NotificationCancelledRecipient> createRecipients(TemplateNotification notification) {
        return Optional.ofNullable(notification.getRecipients())
                .map(data -> data.stream()
                        .map(d -> new NotificationCancelledRecipient()
                                .denomination(d.getDenomination())
                                .taxId(d.getTaxId()))
                        .toList())
                .orElse(null);
    }
}
