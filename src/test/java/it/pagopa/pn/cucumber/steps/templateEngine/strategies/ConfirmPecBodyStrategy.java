package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.PecVerificationCodeBody;
import it.pagopa.pn.client.b2b.pa.config.TemplateEngineMessageConfigs;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ConfirmPecBodyStrategy implements ITemplateEngineStrategy {
    private ITemplateEngineClient templateEngineClient;
    private final TemplateEngineMessageConfigs configs;

    public ConfirmPecBodyStrategy(ITemplateEngineClient templateEngineClient, TemplateEngineMessageConfigs configs) {
        this.templateEngineClient = templateEngineClient;
        this.configs = configs;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        PecVerificationCodeBody pecBody = createRequest(body,context);
        String file = templateEngineClient.pecbody(selectLanguage(language), pecBody);
        return new TemplateEngineResult(file);
    }

    @Override
    public String getTextToCheckLanguage(String language, String recipientType) {
        return "Ricevi questa PEC perch&eacute; hai inserito il tuo indirizzo";
    }

    @Override
    public String getFullTextToCheck(String language, String recipientType) {
        return String.join(" ", getYamlText("otp-conferma-pec", recipientType, language));
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

    private PecVerificationCodeBody createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new PecVerificationCodeBody()
                .verificationCode(context.getVerificationCode())
                .recipientType("PF");
    }
}
