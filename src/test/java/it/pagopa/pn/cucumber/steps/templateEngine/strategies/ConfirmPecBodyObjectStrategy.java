package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

@Component
public class ConfirmPecBodyObjectStrategy implements ITemplateEngineStrategy {
    private ITemplateEngineClient templateEngineClient;

    public ConfirmPecBodyObjectStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        String file = templateEngineClient.pecsubject(selectLanguage(language));
        return new TemplateEngineResult(file);
    }
}