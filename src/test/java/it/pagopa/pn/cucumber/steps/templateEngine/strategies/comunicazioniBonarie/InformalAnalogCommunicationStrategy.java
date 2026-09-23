package it.pagopa.pn.cucumber.steps.templateEngine.strategies.comunicazioniBonarie;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.InformalCommunication;
import it.pagopa.pn.client.b2b.pa.config.TemplateEngineMessageConfigs;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import it.pagopa.pn.cucumber.steps.templateEngine.strategies.ITemplateEngineStrategy;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class InformalAnalogCommunicationStrategy implements ITemplateEngineStrategy {

    private final ITemplateEngineClient templateEngineClient;
    private final TemplateEngineMessageConfigs configs;

    public InformalAnalogCommunicationStrategy(ITemplateEngineClient templateEngineClient, TemplateEngineMessageConfigs configs) {
        this.templateEngineClient = templateEngineClient;
        this.configs = configs;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        InformalCommunication informalCommunication = createRequest(body, context);
        Resource file = templateEngineClient.informalAnalogCommunication(selectLanguage(language), informalCommunication);
        return new TemplateEngineResult(file);
    }

    @Override
    public String getTextToCheckLanguage(String language, String recipientType) {
        return String.join(" ", getYamlText("informal-analog-communication", recipientType, language));
    }

    @Override
    public List<String> getTextsToCheckLanguage(String language, String recipientType) {
        return getYamlText("informal-analog-communication", recipientType, language);
    }

    private InformalCommunication createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return InformalCommunicationRequestFactory.buildInformalCommunication(context.getRawParameters());
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
