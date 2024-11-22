package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templates_engine.model.LegalFactMalfunction;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class LegalFactMalfunctionStrategy implements ITemplateEngineStrategy {

    private ITemplateEngineClient templateEngineClient;

    public LegalFactMalfunctionStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }
    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        LegalFactMalfunction legalFact = createRequest(body, context);
        File file = templateEngineClient.legalFactMalfunction(selectLanguage(language), legalFact);
        return new  TemplateEngineResult(file);
    }

    private LegalFactMalfunction createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new LegalFactMalfunction()
                .endDate(context.getEndDate())
                .startDate(context.getStartDate())
                .timeReferenceStartDate(context.getTimeReferenceStartDate())
                .timeReferenceEndDate(context.getTimeReferenceEndDate());
    }
}
