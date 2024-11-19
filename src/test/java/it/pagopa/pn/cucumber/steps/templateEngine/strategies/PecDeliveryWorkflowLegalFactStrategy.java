package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templates_engine.model.PecDeliveryWorkflowLegalFact;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.TemplateEngineResult;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class PecDeliveryWorkflowLegalFactStrategy implements ITemplateEngineStrategy{

    private ITemplateEngineClient templateEngineClient;

    public PecDeliveryWorkflowLegalFactStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body) {
        PecDeliveryWorkflowLegalFact legalFact = new PecDeliveryWorkflowLegalFact();
        File file = templateEngineClient.pecDeliveryWorkflowLegalFact(selectLanguage(language), legalFact);
        return new TemplateEngineResult(file);
    }
}
