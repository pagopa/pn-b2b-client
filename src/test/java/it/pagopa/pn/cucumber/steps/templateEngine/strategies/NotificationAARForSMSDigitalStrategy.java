package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AarForSmsNotification;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AarForSmsSender;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.NotificationAarForSms;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateNotification;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

import java.util.Optional;
//todo t cm
@Component
public class NotificationAARForSMSDigitalStrategy implements ITemplateEngineStrategy {
    private ITemplateEngineClient templateEngineClient;

    public NotificationAARForSMSDigitalStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        NotificationAarForSms notificationAARForSMS = createRequest(body, context);
        String file = templateEngineClient.notificationAARForSMSDigital(selectLanguage(language), notificationAARForSMS);
        return new TemplateEngineResult(file);
    }

//    @Override
//    public String getTextToCheckLanguage(String language) {
//        return "Hai ricevuto una notifica da string con Codice IUN string. Per leggerla, accedi con SPID o CIE al sito di SEND - Servizio Notifiche Digitali.";
//    }

        @Override
    public String getTextToCheckLanguage(String language) {
        return "Hai ricevuto una notifica SEND da string con Codice IUN string. Per leggerla, accedi a SEND - Servizio Notifiche Digitali. <br>\n" +
                "La notifica risulterà legalmente consegnata a te dopo 7 giorni dalla ricezione.";
    }

    private NotificationAarForSms createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new NotificationAarForSms()
                .notification(createNotification(context));
    }

    private AarForSmsNotification createNotification(TemplateRequestContext context) {
        return Optional.ofNullable(context.getNotification())
                .map(data -> new AarForSmsNotification()
                        .iun(data.getIun())
                        .sender(createSender(data)))
                .orElse(null);
    }

    private AarForSmsSender createSender(TemplateNotification notification) {
        return Optional.ofNullable(notification.getSender())
                .map(data -> new AarForSmsSender()
                        .paDenomination(data.getPaDenomination()))
                .orElse(null);
    }
}
