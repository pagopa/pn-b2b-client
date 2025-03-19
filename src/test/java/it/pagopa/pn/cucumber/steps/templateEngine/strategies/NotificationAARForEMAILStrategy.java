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

    @Override
    public String getTextToCheckLanguage(String language) {
        return switch (language.toUpperCase()) {
            case  "ITALIANA" -> {
                yield "Se non hai una PEC e visualizzi i documenti entro 5 giorni (120 ore) dall&apos;invio del messaggio, non riceverai la notifica tramite raccomandata.";
            }
            case "TEDESCA" -> {
                yield "olltest du keine PEC-Adresse haben und die Dokumente innerhalb von 5 Tagen (120 Stunden) nach Versand der Nachricht einsehen, erhältst du keine Zustellung mittels Einschreibebrief.";
            }
            case "SLOVENA" -> {
                yield "e nimate certificiranega elektronskega naslova PEC in si dokumente ogledate v roku 5 dni (120 ur) od po&#353;iljanja sporo&#269;ila, ne boste prejeli obvestila po priporo";
            }
            case "FRANCESE" -> {
                yield "avez pas d&apos;adresse de courrier &eacute;lectronique certifi&eacute; (PEC) et que vous consultez les documents dans les 5 jours (120 heures) suivant l&apos;envoi du message, vous ne recevrez pas la notification par courrier recommand&eacute";
            }
            default -> throw new IllegalArgumentException("NO VALID LANGUANGE");
        };
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
