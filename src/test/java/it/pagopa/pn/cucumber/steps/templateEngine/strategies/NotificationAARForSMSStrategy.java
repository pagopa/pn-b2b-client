package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AarForSmsNotificationAnalog;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AarForSmsSenderAnalog;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.NotificationAarForSmsAnalog;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateNotification;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class NotificationAARForSMSStrategy implements ITemplateEngineStrategy {
    private ITemplateEngineClient templateEngineClient;

    public NotificationAARForSMSStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        NotificationAarForSmsAnalog notificationAARForSMS = createRequest(body, context);
        String file = templateEngineClient.notificationAARForSMSAnalog(selectLanguage(language), notificationAARForSMS);
        return new TemplateEngineResult(file);
    }

    @Override
    public String getTextToCheckLanguage(String language) {
        return "Hai ricevuto una notifica da string con Codice IUN string. Per leggerla, accedi con SPID o CIE al sito di SEND - Servizio Notifiche Digitali.";
    }

    private NotificationAarForSmsAnalog createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new NotificationAarForSmsAnalog()
                .notification(createNotification(context));
    }

    private AarForSmsNotificationAnalog createNotification(TemplateRequestContext context) {
        return Optional.ofNullable(context.getNotification())
                .map(data -> new AarForSmsNotificationAnalog()
                        .iun(data.getIun())
                        .sender(createSender(data)))
                .orElse(null);
    }

    private AarForSmsSenderAnalog createSender(TemplateNotification notification) {
        return Optional.ofNullable(notification.getSender())
                .map(data -> new AarForSmsSenderAnalog()
                        .paDenomination(data.getPaDenomination()))
                .orElse(null);
    }
}
