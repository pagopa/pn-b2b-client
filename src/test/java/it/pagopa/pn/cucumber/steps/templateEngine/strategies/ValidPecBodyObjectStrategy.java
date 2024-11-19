package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.TemplateEngineResult;
import org.springframework.stereotype.Component;

@Component
public class ValidPecBodyObjectStrategy implements ITemplateEngineStrategy {
    private ITemplateEngineClient templateEngineClient;

    public ValidPecBodyObjectStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body) {
        String file = templateEngineClient.pecsubjectconfirm(selectLanguage(language));
        return new TemplateEngineResult(file);
    }
}
