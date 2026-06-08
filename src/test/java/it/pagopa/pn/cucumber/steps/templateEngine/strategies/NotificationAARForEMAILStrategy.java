package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AarForEmailNotificationAnalog;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AarForEmailRecipientAnalog;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AarForEmailSenderAnalog;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.NotificationAarForEmailAnalog;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateNotification;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class NotificationAARForEMAILStrategy implements ITemplateEngineStrategy {
    private ITemplateEngineClient templateEngineClient;

    public NotificationAARForEMAILStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        NotificationAarForEmailAnalog legalFact = createRequestAnalog(body, context);
        String result = templateEngineClient.notificationAARForEMAILAnalog(selectLanguage(language), legalFact);
        return new TemplateEngineResult(result);
    }

    @Override
    public String getTextToCheckLanguage(String language, String recipientType) {
        return switch (language.toUpperCase()) {
            case  "ITALIANA" -> {
                yield "Se accedi alla comunicazione entro 5 giorni dall&#39;invio di questa email, eviterai una raccomandata cartacea e gli eventuali costi.";
            }
            case "TEDESCA" -> {
                yield "Wenn Sie innerhalb von 5 Tagen nach dem Versand dieser E-Mail auf die Mitteilung zugreifen, vermeiden Sie ein Papier-Einschreiben und die entsprechenden Kosten.";
            }
            case "SLOVENA" -> {
                yield "e do sporo&#269;ila dostopate v 5 dneh od po&#353;iljanja tega e-po&#353;tnega sporo&#269;ila, se boste izognili priporo&#269;eni po&#353;ti v papirni obliki in morebitnim stro&#353;kom";
            }
            case "FRANCESE" -> {
                yield "Vous avez re&#xE7;u sur SEND - Servizio Notifiche Digitali une notification de la part de <strong>string</strong> avec le Code IUN string";
            }
            case "INGLESE" -> {
                yield "If you access the communication within 5 days from the sending of this email, you will avoid a registered mail and any related costs";
            }
            default -> throw new IllegalArgumentException("NO VALID LANGUANGE");
        };
    }



    private NotificationAarForEmailAnalog createRequestAnalog(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new NotificationAarForEmailAnalog()
                .notification(createAarForEmailNotificationAnalog(context))
                //.pnFaqSendURL(context.getPnFaqSendURL())
                .quickAccessLink(context.getQrCodeQuickAccessLink())
                //.piattaformaNotificheURL(context.getPiattaformaNotificheURL())
               // .perfezionamentoURL(context.getPerfezionamentoURL())
                .recipient(new AarForEmailRecipientAnalog().recipientType(context.getRecipientType()));
    }


    private AarForEmailNotificationAnalog createAarForEmailNotificationAnalog(TemplateRequestContext context) {
        return Optional.ofNullable(context.getNotification())
                .map(data -> new AarForEmailNotificationAnalog()
                        .iun(data.getIun())
                        .sender(createSenderAnalog(data)))
                .orElse(null);
    }


    private AarForEmailSenderAnalog createSenderAnalog(TemplateNotification notification) {
        return Optional.ofNullable(notification.getSender())
                .map(data -> new AarForEmailSenderAnalog()
                        .paDenomination(data.getPaDenomination()))
                .orElse(null);
    }
}

