package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templates_engine.model.LanguageEnum;
import it.pagopa.pn.cucumber.steps.templateEngine.TemplateEngineResult;

public interface ITemplateEngineStrategy {

    public TemplateEngineResult retrieveTemplate(String language, boolean body);

    default LanguageEnum selectLanguage(String language) {
        return switch (language.toUpperCase()) {
            case "ITALIANA" -> LanguageEnum.IT;
            case "TEDESCA" -> LanguageEnum.DE;
            case "SLOVENA" -> LanguageEnum.SL;
            case "FRANCESE" -> LanguageEnum.FR;
            case "NULL" -> null;
            default -> throw new IllegalArgumentException();
        };
    }
}
