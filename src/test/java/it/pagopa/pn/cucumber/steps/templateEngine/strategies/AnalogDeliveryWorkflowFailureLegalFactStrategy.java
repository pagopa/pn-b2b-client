package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AnalogDeliveryWorkflowFailureLegalFact;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AnalogDeliveryWorkflowFailureRecipient;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Optional;

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
                .recipient(createAnalogDeliveryWorkflowFailureRecipient(context))
                .endWorkflowDate(context.getEndWorkflowDate())
                .endWorkflowTime(context.getEndWorkflowTime());
    }

    private AnalogDeliveryWorkflowFailureRecipient createAnalogDeliveryWorkflowFailureRecipient(TemplateRequestContext context) {
        return Optional.ofNullable(context.getRecipient())
                .map(data -> new AnalogDeliveryWorkflowFailureRecipient()
                            .denomination(data.getDenomination())
                            .taxId(data.getTaxId()))
                .orElse(null);
    }
}
