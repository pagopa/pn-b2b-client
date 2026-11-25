package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

@Component
public class PecBodyRejectObjectStrategy implements ITemplateEngineStrategy {
    private ITemplateEngineClient templateEngineClient;

    public PecBodyRejectObjectStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        String file = templateEngineClient.pecsubjectreject(selectLanguage(language));
        return new TemplateEngineResult(file);
    }

    @Override
    public String getTextToCheckLanguage(String language, String recipientType) {
        return switch (language.toUpperCase()) {
            case  "ITALIANA" -> {
                yield "SEND - La PEC che hai inserito non è valida";
            }
            case "TEDESCA" -> {
                yield "SEND - Die von dir eingegebene PEC ist ungültig";
            }
            case "SLOVENA" -> {
                yield "SEND - Naslov PEC, ki si ga vnesel, ni veljaven";
            }
            case "FRANCESE" -> {
                yield "SEND - L'adresse PEC que tu as saisie n'est pas valide";
            }
            case  "INGLESE" -> {
                yield "SEND - The PEC address you entered is invalid";
            }
            default -> throw new IllegalArgumentException("NO VALID LANGUANGE");
        };
    }
}
