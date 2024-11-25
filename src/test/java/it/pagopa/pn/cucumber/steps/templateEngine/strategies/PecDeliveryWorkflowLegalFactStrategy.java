package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templates_engine.model.PecDeliveryWorkflowLegalFact;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class PecDeliveryWorkflowLegalFactStrategy implements ITemplateEngineStrategy{

    private ITemplateEngineClient templateEngineClient;

    public PecDeliveryWorkflowLegalFactStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        PecDeliveryWorkflowLegalFact legalFact = createRequest(body, context);
        File file = templateEngineClient.pecDeliveryWorkflowLegalFact(selectLanguage(language), legalFact);
        return new TemplateEngineResult(file);
    }

    private PecDeliveryWorkflowLegalFact createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new PecDeliveryWorkflowLegalFact()
                .deliveries(context.getDeliveries())
                .iun(context.getIun())
                .endWorkflowDate(context.getEndWorkflowDate())
                .endWorkflowStatus(context.getEndWorkflowStatus());
    }

}
