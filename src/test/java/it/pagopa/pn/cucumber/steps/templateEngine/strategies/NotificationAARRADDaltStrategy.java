package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.*;
import it.pagopa.pn.client.b2b.pa.config.TemplateEngineMessageConfigs;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateNotification;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class NotificationAARRADDaltStrategy implements ITemplateEngineStrategy {

    private final ITemplateEngineClient templateEngineClient;
    private final TemplateEngineMessageConfigs configs;

    public NotificationAARRADDaltStrategy(ITemplateEngineClient templateEngineClient, TemplateEngineMessageConfigs configs) {
        this.templateEngineClient = templateEngineClient;
        this.configs = configs;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        NotificationAarRaddAlt legalFact = createRequest(body, context);
        Resource file = templateEngineClient.notificationAARRADDalt(selectLanguage(language), legalFact);
        return new TemplateEngineResult(file);
    }

    @Override
    public String getTextToCheckLanguage(String language, String recipientType) {
        return String.join(" ", getYamlText("aar-radd", recipientType, language));
    }

    @Override
    public List<String> getTextsToCheckLanguage(String language, String recipientType) {
        return getYamlText("aar-radd", recipientType, language);
    }

    private NotificationAarRaddAlt createRequest(boolean body, TemplateRequestContext context) {
        if (!body) return null;

        return new NotificationAarRaddAlt()
                .recipient(createRecipient(context))
                .notification(createNotification(context))
                .qrCodeQuickAccessLink(context.getQrCodeQuickAccessLink());
               // .piattaformaNotificheURL(context.getPiattaformaNotificheURL())
               // .piattaformaNotificheURLLabel(context.getPiattaformaNotificheURLLabel())
               // .perfezionamentoURL(context.getPerfezionamentoURL())
               // .perfezionamentoURLLabel(context.getPerfezionamentoURLLabel())
               // .sendURL(context.getSendURL())
               // .sendURLLAbel(context.getSendURLLAbel())
               // .raddPhoneNumber(context.getRaddPhoneNumber());
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

}
