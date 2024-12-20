package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

@Component
public class ValidPecBodyStrategy implements ITemplateEngineStrategy{
    private ITemplateEngineClient templateEngineClient;

    public ValidPecBodyStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        //PecValidationContactsSuccessBody pecBody = createRequest(body, context);
        String file = templateEngineClient.pecbodyconfirm(selectLanguage(language));
        return new TemplateEngineResult(file);
    }

   /* private PecValidationContactsSuccessBody createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new PecValidationContactsSuccessBody()
                .verificationCode(context.getVerificationCode());
    }*/
}
