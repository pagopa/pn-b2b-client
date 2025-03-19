package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.*;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateNotification;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class NotificationAARForPECStrategy implements ITemplateEngineStrategy {
    private ITemplateEngineClient templateEngineClient;

    public NotificationAARForPECStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        NotificationAarForPec legalFact = createRequest(body, context);
        String file = templateEngineClient.notificationAARForPEC(selectLanguage(language), legalFact);
        return new TemplateEngineResult(file);
    }

    @Override
    public String getTextToCheckLanguage(String language) {
        return switch (language.toUpperCase()) {
            case  "ITALIANA" -> {
                yield "fa parte di uno dei registri previsti dal Codice Amministrazione Digitale";
            }
            case "TEDESCA" -> {
                yield "Du erhältst diese Empfangsbestätigung als string mit der Steuernummer string. SEND ist ein Tool, das den Versand von Zustellungen durch die öffentliche Verwaltung sicherer, effizienter und kostengünstiger macht.";
            }
            case "SLOVENA" -> {
                yield "To obvestilo prejemate kot string z dav&#269;no &#353;tevilko string. SEND je orodje, ki omogo&#269;a varnej&#353;e, bolj u&#269;inkovito in gospodarno po&#353;iljanje obvestil javne uprave.";
            }
            case "FRANCESE" -> {
                yield "Ricevi questo avviso di avvenuta ricezione in qualità di persona string con Codice Fiscale string. SEND è uno strumento che rende l'invio di notifiche da parte della Pubblica Amministrazione più sicuro, efficiente ed economico.";
            }
            default -> throw new IllegalArgumentException("NO VALID LANGUANGE");
        };
    }

    private NotificationAarForPec createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new NotificationAarForPec()
                .notification(createNotification(context))
                .pnFaqSendURL(context.getPnFaqSendURL())
                .quickAccessLink(context.getQrCodeQuickAccessLink())
                .piattaformaNotificheURL(context.getPiattaformaNotificheURL())
                .perfezionamentoURL(context.getPerfezionamentoURL())
                .recipientType(context.getRecipientType())
                .recipient(createRecipient(context));
    }

    private AarForPecNotification createNotification(TemplateRequestContext context) {
        return Optional.ofNullable(context.getNotification())
                .map(data -> new AarForPecNotification()
                        .iun(data.getIun())
                        .subject(data.getSubject())
                        .sender(createSender(data)))
                .orElse(null);
    }

    private AarForPecSender createSender(TemplateNotification notification) {
        return Optional.ofNullable(notification.getSender())
                .map(data -> new AarForPecSender()
                        .paDenomination(data.getPaDenomination()))
                .orElse(null);
    }

    private AarForPecRecipient createRecipient(TemplateRequestContext context) {
        return Optional.ofNullable(context.getRecipient())
                .map(data -> new AarForPecRecipient()
                        .taxId(data.getTaxId()))
                .orElse(null);
    }
}
