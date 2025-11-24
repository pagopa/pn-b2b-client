package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AarForSmsNotificationDigital;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AarForSmsRecipientDigital;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AarForSmsSenderDigital;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.NotificationAarForSmsDigital;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateNotification;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class NotificationAARForSMSDigitalStrategy implements ITemplateEngineStrategy {
    private ITemplateEngineClient templateEngineClient;

    public NotificationAARForSMSDigitalStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        NotificationAarForSmsDigital notificationAarForSmsDigital = createRequest(body, context);
        String file = templateEngineClient.notificationAARForSMSDigital(selectLanguage(language), notificationAarForSmsDigital);
        return new TemplateEngineResult(file);
    }

//    @Override
//    public String getTextToCheckLanguage(String language) {
//        return "Hai ricevuto una notifica da string con Codice IUN string. Per leggerla, accedi con SPID o CIE al sito di SEND - Servizio Notifiche Digitali.";
//    }

        @Override
    public String getTextToCheckLanguage(String language, String recipientType) {
        return switch (recipientType.toUpperCase()) {
            case "PG" -> "La tua impresa ha ricevuto una notifica SEND da string con Codice IUN string. Per leggerla, accedi a SEND - Servizio Notifiche Digitali.\nLa notifica risulterà legalmente consegnata  dopo 7 giorni dalla ricezione.";
            default -> "Hai ricevuto una notifica SEND da string con Codice IUN string. Per leggerla, accedi a SEND - Servizio Notifiche Digitali.\nLa notifica risulterà legalmente consegnata a te dopo 7 giorni dalla ricezione.";
        };
    }

    private NotificationAarForSmsDigital createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new NotificationAarForSmsDigital()
                .recipient(createRecipient(context)) // todo t mc
                .notification(createNotification(context));
    }
    private AarForSmsRecipientDigital createRecipient(TemplateRequestContext context) {
        return new AarForSmsRecipientDigital()
                .recipientType(context.getRecipientType());   // todo t mc
    }

    private AarForSmsNotificationDigital createNotification(TemplateRequestContext context) {
        return Optional.ofNullable(context.getNotification())
                .map(data -> new AarForSmsNotificationDigital()
                        .iun(data.getIun())
                        .sender(createSender(data)))
                .orElse(null);
    }

    private AarForSmsSenderDigital createSender(TemplateNotification notification) {
        return Optional.ofNullable(notification.getSender())
                .map(data -> new AarForSmsSenderDigital()
                        .paDenomination(data.getPaDenomination()))
                .orElse(null);
    }
}
