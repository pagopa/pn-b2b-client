package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templates_engine.model.NotificationAAR;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templates_engine.model.PecDeliveryWorkflowLegalFact;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class NotificationAARStrategy implements ITemplateEngineStrategy {

    private ITemplateEngineClient templateEngineClient;

    public NotificationAARStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        NotificationAAR legalFact = createRequest(body, context);
        File file = templateEngineClient. notificationAAR(selectLanguage(language), legalFact);
        return new TemplateEngineResult(file);
    }

    private NotificationAAR createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new NotificationAAR()
                .recipient(context.getRecipient())
                .notification(context.getNotification())
                .qrCodeQuickAccessLink(context.getQrCodeQuickAccessLink())
                .piattaformaNotificheURL(context.getPiattaformaNotificheURL())
                .piattaformaNotificheURLLabel(context.getPiattaformaNotificheURLLabel())
                .perfezionamentoURL(context.getPerfezionamentoURL())
                .perfezionamentoURLLabel(context.getPerfezionamentoURLLabel())
                .sendURL(context.getSendURL())
                .sendURLLAbel(context.getSendURLLAbel());
    }
}
