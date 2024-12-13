package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.*;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateNotification;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Optional;

@Component
public class NotificationAARRADDaltStrategy implements ITemplateEngineStrategy {
    private ITemplateEngineClient templateEngineClient;

    public NotificationAARRADDaltStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        NotificationAarRaddAlt legalFact = createRequest(body, context);
        Resource file = templateEngineClient. notificationAARRADDalt(selectLanguage(language), legalFact);
        return new TemplateEngineResult(file);
    }

    private NotificationAarRaddAlt createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new NotificationAarRaddAlt()
                .recipient(createRecipient(context))
                .notification(createNotification(context))
                .qrCodeQuickAccessLink(context.getQrCodeQuickAccessLink())
                .piattaformaNotificheURL(context.getPiattaformaNotificheURL())
                .piattaformaNotificheURLLabel(context.getPiattaformaNotificheURLLabel())
                .perfezionamentoURL(context.getPerfezionamentoURL())
                .perfezionamentoURLLabel(context.getPerfezionamentoURLLabel())
                .sendURL(context.getSendURL())
                .sendURLLAbel(context.getSendURLLAbel())
                .raddPhoneNumber(context.getRaddPhoneNumber());
    }

    private AarRaddAltNotification createNotification(TemplateRequestContext context) {
        return Optional.ofNullable(context.getNotification())
                .map(data -> new AarRaddAltNotification()
                        .iun(data.getIun())
                        .subject(data.getSubject())
                        .sender(createSender(data)))
                .orElse(null);
    }

    private AarRaddAltRecipient createRecipient(TemplateRequestContext context) {
        return Optional.ofNullable(context.getRecipient())
                .map(data -> new AarRaddAltRecipient()
                        .denomination(data.getDenomination())
                        .taxId(data.getTaxId())
                        .recipientType(data.getRecipientType()))
                .orElse(null);
    }

    private AarRaddAltSender createSender(TemplateNotification notification) {
        return Optional.ofNullable(notification.getSender())
                .map(data -> new AarRaddAltSender()
                        .paDenomination(data.getPaDenomination()))
                .orElse(null);
    }
}
