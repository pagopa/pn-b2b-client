package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.TemplateEngineResult;
import org.springframework.stereotype.Component;

@Component
public class ConfirmSmsBodyStrategy implements ITemplateEngineStrategy{

    private ITemplateEngineClient templateEngineClient;

    public ConfirmSmsBodyStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language) {
        String file = templateEngineClient.smsbody(selectLanguage(language));
        return new TemplateEngineResult(file);
    }
}
