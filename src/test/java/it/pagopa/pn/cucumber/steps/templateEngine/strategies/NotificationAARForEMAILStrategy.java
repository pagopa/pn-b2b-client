package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templates_engine.model.NotificationAARForEMAIL;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templates_engine.model.PecDeliveryWorkflowLegalFact;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

@Component
public class NotificationAARForEMAILStrategy implements ITemplateEngineStrategy {
    private ITemplateEngineClient templateEngineClient;

    public NotificationAARForEMAILStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        NotificationAARForEMAIL legalFact = createRequest(body, context);
        String result = templateEngineClient.notificationAARForEMAIL(selectLanguage(language), legalFact);
        return new TemplateEngineResult(result);
    }

    private NotificationAARForEMAIL createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new NotificationAARForEMAIL()
                .notification(context.getNotification())
                .sendLogoLink(context.getSendLogoLink())
                .pnFaqSendURL(context.getPnFaqSendURL())
                .quickAccessLink(context.getQrCodeQuickAccessLink())
                .piattaformaNotificheURL(context.getPiattaformaNotificheURL())
                .perfezionamentoURL(context.getPerfezionamentoURL());
    }
}
