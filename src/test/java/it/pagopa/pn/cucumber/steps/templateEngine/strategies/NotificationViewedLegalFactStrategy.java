package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.*;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Optional;

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
                .delegate(createDelegate(context))
                .when(context.getWhen())
                .recipient(createRecipient(context));
    }

    private NotificationViewedDelegate createDelegate(TemplateRequestContext context) {
        return Optional.ofNullable(context.getDelegate())
                .map(data -> new NotificationViewedDelegate()
                        .denomination(data.getDenomination())
                        .taxId(data.getTaxId())
                )
                .orElse(null);
    }

    private NotificationViewedRecipient createRecipient(TemplateRequestContext context) {
        return Optional.ofNullable(context.getRecipient())
                .map(data -> new NotificationViewedRecipient()
                        .denomination(data.getDenomination())
                        .taxId(data.getTaxId()))
                .orElse(null);
    }
}
