package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.NotificationViewedLegalFact;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class NotificationViewedLegalFactStrategy  implements ITemplateEngineStrategy{
    private ITemplateEngineClient templateEngineClient;

    public NotificationViewedLegalFactStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        NotificationViewedLegalFact legalFact = createRequest(body, context);
        File file = templateEngineClient.notificationViewedLegalFact(selectLanguage(language), legalFact);
        return new TemplateEngineResult(file);
    }

    private NotificationViewedLegalFact createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new NotificationViewedLegalFact()
                .iun(context.getIun())
                .delegate(context.getDelegate())
                .when(context.getWhen())
                .recipient(context.getRecipient());
    }
}
