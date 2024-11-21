package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templates_engine.model.NotificationAARForPEC;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templates_engine.model.PecDeliveryWorkflowLegalFact;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

@Component
public class NotificationAARForPECStrategy implements ITemplateEngineStrategy {
    private ITemplateEngineClient templateEngineClient;

    public NotificationAARForPECStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        NotificationAARForPEC legalFact = createRequest(body, context);
        String file = templateEngineClient.notificationAARForPEC(selectLanguage(language), legalFact);
        return new TemplateEngineResult(file);
    }

    private NotificationAARForPEC createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new NotificationAARForPEC()
                .notification(context.getNotification())
                .logoBase64(context.getLogoBase64())
                .pnFaqSendURL(context.getPnFaqSendURL())
                .quickAccessLink(context.getQrCodeQuickAccessLink())
                .piattaformaNotificheURL(context.getPiattaformaNotificheURL())
                .perfezionamentoURL(context.getPerfezionamentoURL())
                .recipientType(context.getRecipientType())
                .recipient(context.getRecipient());
    }
}
