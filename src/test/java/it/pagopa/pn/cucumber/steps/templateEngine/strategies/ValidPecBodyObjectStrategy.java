package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

@Component
public class ValidPecBodyObjectStrategy implements ITemplateEngineStrategy {
    private ITemplateEngineClient templateEngineClient;

    public ValidPecBodyObjectStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        String file = templateEngineClient.pecsubjectconfirm(selectLanguage(language));
        return new TemplateEngineResult(file);
    }

    @Override
    public String getTextToCheckLanguage(String language, String recipientType) {
        return switch (language.toUpperCase()) {
            case  "ITALIANA" -> {
                yield "SEND - Domicilio digitale PEC confermato";
            }
            case "TEDESCA" -> {
                yield "SEND - Digitales PEC-Domizil bestätigt";
            }
            case "SLOVENA" -> {
                yield "SEND - Digitalno prebivališče PEC potrjeno";
            }
            case "FRANCESE" -> {
                yield "SEND - Domicile numérique PEC confirmé";
            }
            case  "INGLESE" -> {
                yield "SEND - PEC digital domicile confirmed";
            }
            default -> throw new IllegalArgumentException("NO VALID LANGUANGE");
        };
    }
}
