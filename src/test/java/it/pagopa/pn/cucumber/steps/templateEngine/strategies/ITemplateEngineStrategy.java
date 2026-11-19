package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.LanguageEnum;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;

import java.util.List;

public interface ITemplateEngineStrategy {

    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context);

    public String getTextToCheckLanguage(String language, String recipientType);

    /**
     * Tutti i testi che devono essere presenti (contains "AND") nel documento generato per la lingua/recipientType
     * indicati. Di default e' un singolo elemento pari a {@link #getTextToCheckLanguage}; le strategie che leggono
     * il testo atteso da config/template-engine.yml lo sovrascrivono per restituire più valori quando necessario.
     */
    default List<String> getTextsToCheckLanguage(String language, String recipientType) {
        return List.of(getTextToCheckLanguage(language, recipientType));
    }

    /**
     * Testo completo (decodificato, senza markup) che deve essere contenuto nel contenuto
     * visibile del template per la lingua/recipientType indicati. A differenza di
     * {@link #getTextToCheckLanguage}, che verifica una porzione del template contro l'HTML
     * grezzo, questo metodo verifica l'intero contenuto testuale visibile; va sovrascritto solo
     * dalle strategie che espongono un controllo di questo tipo, leggendo il testo atteso da
     * config/template-engine.yml.
     */
    default String getFullTextToCheck(String language, String recipientType) {
        throw new UnsupportedOperationException(
                "Controllo sul testo visibile completo non supportato da " + getClass().getSimpleName());
    }

    default LanguageEnum selectLanguage(String language) {
        return switch (language.toUpperCase()) {
            case "ITALIANA" -> LanguageEnum.IT;
            case "TEDESCA" -> LanguageEnum.DE;
            case "SLOVENA" -> LanguageEnum.SL;
            case "FRANCESE" -> LanguageEnum.FR;
            case "INGLESE" -> LanguageEnum.EN;
            case "NULL" -> null;
            default -> throw new IllegalArgumentException();
        };
    }
}
