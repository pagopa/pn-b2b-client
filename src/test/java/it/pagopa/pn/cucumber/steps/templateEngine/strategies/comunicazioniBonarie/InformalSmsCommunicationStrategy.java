package it.pagopa.pn.cucumber.steps.templateEngine.strategies.comunicazioniBonarie;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AarNotification;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AarRecipient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AarSender;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.InformalSmsCommunication;
import it.pagopa.pn.client.b2b.pa.config.TemplateEngineMessageConfigs;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateNotification;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import it.pagopa.pn.cucumber.steps.templateEngine.strategies.ITemplateEngineStrategy;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class InformalSmsCommunicationStrategy implements ITemplateEngineStrategy {

    private final ITemplateEngineClient templateEngineClient;
    private final TemplateEngineMessageConfigs configs;

    public InformalSmsCommunicationStrategy(ITemplateEngineClient templateEngineClient, TemplateEngineMessageConfigs configs) {
        this.templateEngineClient = templateEngineClient;
        this.configs = configs;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        InformalSmsCommunication legalFact = createRequest(body, context);
        String file = templateEngineClient.informalSmsCommunication(selectLanguage(language), legalFact);
        return new TemplateEngineResult(file);
    }

    @Override
    public String getTextToCheckLanguage(String language, String recipientType) {
        return getYamlText("aar-no-radd", recipientType, language);
    }

    private InformalSmsCommunication createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new InformalSmsCommunication();
    }

    private AarRecipient createRecipient(TemplateRequestContext context) {
        return Optional.ofNullable(context.getRecipient())
                .map(data -> new AarRecipient()
                        .recipientType(data.getRecipientType())
                        .taxId(data.getTaxId()))
                .orElse(null);
    }

    private AarNotification createNotification(TemplateRequestContext context) {
        return Optional.ofNullable(context.getNotification())
                .map(data -> new AarNotification()
                        .iun(data.getIun())
                        .subject(data.getSubject())
                        .sender(createSender(data)))
                .orElse(null);
    }

    private AarSender createSender(TemplateNotification notification) {
        return Optional.ofNullable(notification.getSender())
                .map(data -> new AarSender()
                        .paDenomination(data.getPaDenomination()))
                .orElse(null);
    }

    private String getYamlText(String templateKey, String recipientType, String language) {
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
