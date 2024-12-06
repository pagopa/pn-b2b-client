package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.PecDeliveryWorkflowDelivery;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.PecDeliveryWorkflowLegalFact;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Component
public class PecDeliveryWorkflowLegalFactStrategy implements ITemplateEngineStrategy{

    private ITemplateEngineClient templateEngineClient;

    public PecDeliveryWorkflowLegalFactStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        PecDeliveryWorkflowLegalFact legalFact = createRequest(body, context);
        Resource file = templateEngineClient.pecDeliveryWorkflowLegalFact(selectLanguage(language), legalFact);
        return new TemplateEngineResult(file);
    }

    private PecDeliveryWorkflowLegalFact createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new PecDeliveryWorkflowLegalFact()
                .deliveries(createDeliveries(context))
                .iun(context.getIun())
                .endWorkflowDate(context.getEndWorkflowDate())
                .endWorkflowStatus(context.getEndWorkflowStatus());
    }

    private List<PecDeliveryWorkflowDelivery> createDeliveries(TemplateRequestContext context) {
        return Optional.ofNullable(context.getDeliveries())
                .map(data -> data.stream()
                        .map(d -> new PecDeliveryWorkflowDelivery()
                                .address(d.getAddress())
                                .denomination(d.getDenomination())
                                .addressSource(d.getAddressSource())
                                .taxId(d.getTaxId())
                                .type(d.getType())
                                .responseDate(d.getResponseDate())
                                .ok(d.getOk()))
                        .toList())
                .orElse(null);
    }

}
