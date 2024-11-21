package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templates_engine.model.AnalogDeliveryWorkflowFailureLegalFact;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templates_engine.model.PecDeliveryWorkflowLegalFact;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class AnalogDeliveryWorkflowFailureLegalFactStrategy implements ITemplateEngineStrategy {

    private ITemplateEngineClient templateEngineClient;

    public AnalogDeliveryWorkflowFailureLegalFactStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        AnalogDeliveryWorkflowFailureLegalFact legalFact = createRequest(body, context);
        File file = templateEngineClient.analogDeliveryWorkflowFailureLegalFact(selectLanguage(language), legalFact);
        return new TemplateEngineResult(file);
    }

    private AnalogDeliveryWorkflowFailureLegalFact createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new AnalogDeliveryWorkflowFailureLegalFact()
                .iun(context.getIun())
                .recipient(context.getRecipient())
                .endWorkflowDate(context.getEndWorkflowDate())
                .endWorkflowTime(context.getEndWorkflowTime());
    }
}
