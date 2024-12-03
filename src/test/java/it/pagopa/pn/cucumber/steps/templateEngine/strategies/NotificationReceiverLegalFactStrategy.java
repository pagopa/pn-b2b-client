package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.*;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateNotification;
import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateRecipient;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Component
public class NotificationReceiverLegalFactStrategy implements ITemplateEngineStrategy {

    private ITemplateEngineClient templateEngineClient;

    public NotificationReceiverLegalFactStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        NotificationReceivedLegalFact legalFact = createRequest(body, context);
        File file = templateEngineClient.notificationReceivedLegalFact(selectLanguage(language), legalFact);
        return new TemplateEngineResult(file);
    }

    private NotificationReceivedLegalFact createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new NotificationReceivedLegalFact()
                .notification(createNotification(context))
                .subject(context.getSubject())
                .digests(context.getDigests())
                .sendDate(context.getSendDate());
    }

    private NotificationReceivedNotification createNotification(TemplateRequestContext context) {
        return Optional.ofNullable(context.getNotification())
                .map(data -> new NotificationReceivedNotification()
                            .iun(data.getIun())
                            .sender(createSender(data))
                            .recipients(createRecipients(data)))
                .orElse(null);
    }

    private NotificationReceivedSender createSender(TemplateNotification notification) {
        return Optional.ofNullable(notification.getSender())
                .map(data -> new NotificationReceivedSender()
                        .paDenomination(data.getPaDenomination())
                        .paTaxId(data.getPaTaxId()))
                .orElse(null);
    }

    private List<NotificationReceivedRecipient> createRecipients(TemplateNotification notification) {
        return Optional.ofNullable(notification.getRecipients())
                .map(data -> data.stream()
                        .map(d -> new NotificationReceivedRecipient()
                                .denomination(d.getDenomination())
                            .taxId(d.getTaxId())
                            .physicalAddressAndDenomination(d.getPhysicalAddressAndDenomination())
                            .digitalDomicile(createDigitalDomicile(d)))
                        .toList())
                .orElse(null);
    }

    private NotificationReceivedDigitalDomicile createDigitalDomicile(TemplateRecipient recipient) {
        return Optional.ofNullable(recipient.getDigitalDomicile())
                .map(data -> new NotificationReceivedDigitalDomicile()
                        .address(data.getAddress()))
                .orElse(null);
    }
}
