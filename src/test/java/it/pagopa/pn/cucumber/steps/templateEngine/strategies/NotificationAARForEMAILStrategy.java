package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AarForEmailNotificationAnalog;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AarForEmailRecipientAnalog;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AarForEmailSenderAnalog;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.NotificationAarForEmailAnalog;
import it.pagopa.pn.client.b2b.pa.config.TemplateEngineMessageConfigs;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateNotification;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class NotificationAARForEMAILStrategy implements ITemplateEngineStrategy {
    private ITemplateEngineClient templateEngineClient;
    private final TemplateEngineMessageConfigs configs;

    public NotificationAARForEMAILStrategy(ITemplateEngineClient templateEngineClient, TemplateEngineMessageConfigs configs) {
        this.templateEngineClient = templateEngineClient;
        this.configs = configs;
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
            case "ITALIANA" -> {
                yield "Se accedi alla notifica SEND entro 5 giorni dall&#39;invio di questa email, eviterai una raccomandata cartacea e gli eventuali costi.";
            }
            case "TEDESCA" -> {
                yield "Wenn du innerhalb von 5 Tagen nach dem Versand dieser E-Mail auf die SEND-Zustellung zugreifst, vermeidest du ein Papier-Einschreiben und etwaige Kosten.";
            }
            case "SLOVENA" -> {
                yield "&#268;e do obvestila SEND dostopa&#353; v 5 dneh od po&#353;iljanja tega e-po&#353;tnega sporo&#269;ila, se izogne&#353; priporo&#269;enemu pismu v papirni obliki in morebitnim stro&#353;kom.";
            }
            case "FRANCESE" -> {
                yield "Tu as re&#xE7;u sur SEND - Servizio Notifiche Digitali une notification &#xE0; valeur juridique de la part de <strong>string</strong> avec le Code IUN string.";
            }
            case "INGLESE" -> {
                yield "If you access the SEND notification within 5 days of this email being sent, you will avoid registered mail and any related costs.";
            }
            default -> throw new IllegalArgumentException("NO VALID LANGUANGE");
        };
    }

    @Override
    public String getFullTextToCheck(String language, String recipientType) {
        return String.join(" ", getYamlText("avviso-cortesia-email", recipientType, language));
    }

    private List<String> getYamlText(String templateKey, String recipientType, String language) {
        TemplateEngineMessageConfigs.LocalizedText localizedText =
                Optional.ofNullable(configs.getMessages().get(templateKey))
                        .map(inner -> inner.get(recipientType.toLowerCase()))
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Template non trovato: " + templateKey + " " + recipientType));

        return switch (language.toUpperCase()) {
            case "ITALIANA" -> localizedText.getIt();
            case "TEDESCA" -> localizedText.getDe();
            case "FRANCESE" -> localizedText.getFr();
            case "SLOVENA" -> localizedText.getSl();
            case "INGLESE" -> localizedText.getEn();
            default -> throw new IllegalArgumentException("Lingua non valida: " + language);
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

