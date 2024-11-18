package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templates_engine.model.Pecbody;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.TemplateEngineResult;
import org.springframework.stereotype.Component;

@Component
public class ValidPecBodyStrategy implements ITemplateEngineStrategy{
    private ITemplateEngineClient templateEngineClient;

    public ValidPecBodyStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language) {
        Pecbody body = new Pecbody();
        String file = templateEngineClient.pecbodyconfirm(selectLanguage(language), body);
        return new TemplateEngineResult(file);
    }
}
