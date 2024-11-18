package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templates_engine.model.LegalFactMalfunction;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.TemplateEngineResult;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class LegalFactMalfunctionStrategy implements ITemplateEngineStrategy {

    private ITemplateEngineClient templateEngineClient;

    public LegalFactMalfunctionStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }
    @Override
    public TemplateEngineResult retrieveTemplate(String language) {
        LegalFactMalfunction legalFact = new  LegalFactMalfunction();
        File file = templateEngineClient.legalFactMalfunction(selectLanguage(language), legalFact);
        return new  TemplateEngineResult(file);
    }
}
