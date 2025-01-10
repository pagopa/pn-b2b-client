package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AarForEmailNotification;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AarForEmailSender;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.NotificationAarForEmail;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateNotification;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class NotificationAARForEMAILStrategy implements ITemplateEngineStrategy {
    private ITemplateEngineClient templateEngineClient;

    public NotificationAARForEMAILStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        NotificationAarForEmail legalFact = createRequest(body, context);
        String result = templateEngineClient.notificationAARForEMAIL(selectLanguage(language), legalFact);
        return new TemplateEngineResult(result);
    }

    private NotificationAarForEmail createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new NotificationAarForEmail()
                .notification(createAarForEmailNotification(context))
                .pnFaqSendURL(context.getPnFaqSendURL())
                .quickAccessLink(context.getQrCodeQuickAccessLink())
                .piattaformaNotificheURL(context.getPiattaformaNotificheURL())
                .perfezionamentoURL(context.getPerfezionamentoURL());
    }

    private AarForEmailNotification createAarForEmailNotification(TemplateRequestContext context) {
        return Optional.ofNullable(context.getNotification())
                .map(data -> new AarForEmailNotification()
                        .iun(data.getIun())
                        .sender(createSender(data)))
                .orElse(null);
    }

    private AarForEmailSender createSender(TemplateNotification notification) {
        return Optional.ofNullable(notification.getSender())
                .map(data -> new AarForEmailSender()
                        .paDenomination(data.getPaDenomination()))
                .orElse(null);
    }
}
