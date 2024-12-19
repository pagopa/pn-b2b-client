package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.PecVerificationCodeBody;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

@Component
public class ConfirmPecBodyStrategy implements ITemplateEngineStrategy {
    private ITemplateEngineClient templateEngineClient;

    public ConfirmPecBodyStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        PecVerificationCodeBody pecBody = createRequest(body,context);
        String file = templateEngineClient.pecbody(selectLanguage(language), pecBody);
        return new TemplateEngineResult(file);
    }

    @Override
    public String getTextToCheckLanguage(String language) {
        return "Ricevi questa comunicazione perch&eacute; hai inserito il tuo indirizzo";
    }

    private PecVerificationCodeBody createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new PecVerificationCodeBody()
                .verificationCode(context.getVerificationCode());
    }
}
