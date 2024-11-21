package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templates_engine.model.Notification;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templates_engine.model.NotificationReceiverLegalFact;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class NotificationReceiverLegalFactStrategy implements ITemplateEngineStrategy {

    private ITemplateEngineClient templateEngineClient;

    public NotificationReceiverLegalFactStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        NotificationReceiverLegalFact legalFact = createRequest(body, context);
        File file = templateEngineClient.notificationReceivedLegalFact(selectLanguage(language), legalFact);
        return new TemplateEngineResult(file);
    }

    private NotificationReceiverLegalFact createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new NotificationReceiverLegalFact()
                .notification(context.getNotification())
                .subject(context.getSubject())
                .digests(context.getDigests())
                .sendDate(context.getSendDate())
                .physicalAddressAndDenomination(context.getPhysicalAddressAndDenomination());
    }
}
