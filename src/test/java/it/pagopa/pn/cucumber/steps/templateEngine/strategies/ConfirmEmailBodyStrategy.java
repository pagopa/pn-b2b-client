package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templates_engine.model.Emailbody;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

@Component
public class ConfirmEmailBodyStrategy implements ITemplateEngineStrategy {
    private ITemplateEngineClient templateEngineClient;

    public ConfirmEmailBodyStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        Emailbody emailBody = createRequest(body, context);
        String file = templateEngineClient.emailbody(selectLanguage(language), emailBody);
        return new TemplateEngineResult(file);
    }

    private Emailbody createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new Emailbody()
                .verificationCode(context.getVerificationCode());
    }
}
