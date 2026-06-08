package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

@Component
public class ConfirmEmailBodyObjectStrategy implements ITemplateEngineStrategy{
    private ITemplateEngineClient templateEngineClient;

    public ConfirmEmailBodyObjectStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        String file = templateEngineClient.emailsubject(selectLanguage(language));
        return new TemplateEngineResult(file);
    }

    @Override
    public String getTextToCheckLanguage(String language, String recipientType) {
        return switch (language.toUpperCase()) {
            case  "ITALIANA" -> {
                yield "SEND - Conferma la tua email";
            }
            case "TEDESCA" -> {
                yield "SEND - Bestätige deine E-Mail-Adresse";
            }
            case "SLOVENA" -> {
                yield "SEND - Potrdi svoj e-poštni naslov";
            }
            case "FRANCESE" -> {
                yield "SEND - Confirme ton adresse e-mail";
            }
            case  "INGLESE" -> {
                yield "SEND - Confirm your email";
            }
            default -> throw new IllegalArgumentException("NO VALID LANGUANGE");
        };
    }
}
