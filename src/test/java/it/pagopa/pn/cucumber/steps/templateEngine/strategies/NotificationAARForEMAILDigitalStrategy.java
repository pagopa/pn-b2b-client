package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AarForEmailNotificationDigital;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AarForEmailRecipientDigital;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AarForEmailSenderDigital;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.NotificationAarForEmailDigital;
import it.pagopa.pn.client.b2b.pa.config.TemplateEngineMessageConfigs;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateNotification;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class NotificationAARForEMAILDigitalStrategy implements ITemplateEngineStrategy {
    private ITemplateEngineClient templateEngineClient;
    private final TemplateEngineMessageConfigs configs;

    public NotificationAARForEMAILDigitalStrategy(ITemplateEngineClient templateEngineClient, TemplateEngineMessageConfigs configs) {
        this.templateEngineClient = templateEngineClient;
        this.configs = configs;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        NotificationAarForEmailDigital legalFact = createRequest(body, context);
        String result = templateEngineClient.notificationAARForEMAILDigital(selectLanguage(language), legalFact);
        return new TemplateEngineResult(result);
    }

    @Override
    public String getTextToCheckLanguage(String language, String recipientType) {
        return switch (language.toUpperCase()) {
            case  "ITALIANA" -> {
                yield "Il termine per il pagamento, se previsto, e per eventuali impugnazioni &egrave; indicato nei documenti.";
            }
            case "TEDESCA" -> {
                yield "Sie haben ab dem Zeitpunkt der Zustellung der Mitteilung 120 Tage Zeit, um die Dokumente online einzusehen.";
            }
            case "SLOVENA" -> {
                yield "Od trenutka, ko se obvestilo &scaron;teje za vro&ccaron;eno, imate na voljo 120 dni za ogled dokumentov na spletu. Kasneje ne bodo ve&ccaron;";
            }
            case "FRANCESE" -> {
                yield "Vous disposez de 120 jours &agrave; compter du moment où la communication est consid&eacute;r&eacute;e d&eacute;livr&eacute;e pour consulter les documents en ligne.";
            }
            case "INGLESE" -> {
                yield "The deadline for payment, if applicable, and for any appeals is indicated in the documents.";
            }
            default -> throw new IllegalArgumentException("NO VALID LANGUANGE");
        };
    }

    @Override
    public String getFullTextToCheck(String language, String recipientType) {
        return String.join(" ", getYamlText("avviso-cortesia-email-digitale", recipientType, language));
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

    private NotificationAarForEmailDigital createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new NotificationAarForEmailDigital()
                .notification(createAarForEmailNotification(context))
                //.pnFaqSendURL(context.getPnFaqSendURL())
                .quickAccessLink(context.getQrCodeQuickAccessLink())
               // .piattaformaNotificheURL(context.getPiattaformaNotificheURL())
               // .perfezionamentoURL(context.getPerfezionamentoURL())
                .recipient(new AarForEmailRecipientDigital().recipientType(context.getRecipientType()));
    }

    private AarForEmailNotificationDigital createAarForEmailNotification(TemplateRequestContext context) {
        return Optional.ofNullable(context.getNotification())
                .map(data -> new AarForEmailNotificationDigital()
                        .iun(data.getIun())
                        .sender(createSender(data)))
                .orElse(null);
    }

    private AarForEmailSenderDigital createSender(TemplateNotification notification) {
        return Optional.ofNullable(notification.getSender())
                .map(data -> new AarForEmailSenderDigital()
                        .paDenomination(data.getPaDenomination()))
                .orElse(null);
    }
}
