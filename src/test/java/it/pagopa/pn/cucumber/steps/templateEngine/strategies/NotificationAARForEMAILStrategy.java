package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AarForEmailNotificationAnalog;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AarForEmailSenderAnalog;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.NotificationAarForEmailAnalog;
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
        NotificationAarForEmailAnalog legalFact = createRequestAnalog(body, context);
        String result = templateEngineClient.notificationAARForEMAILAnalog(selectLanguage(language), legalFact);
        return new TemplateEngineResult(result);
    }

    @Override
    public String getTextToCheckLanguage(String language) {
        return switch (language.toUpperCase()) {
            case  "ITALIANA" -> {
                yield "Se accedi alla comunicazione entro 5 giorni dall’invio di questa email, eviterai una raccomandata cartacea e gli eventuali costi.";
            }
            case "TEDESCA" -> {
                yield "Solltest du keine PEC-Adresse haben und die Dokumente innerhalb von 5 Tagen (120 Stunden) nach Versand der Nachricht einsehen, erh&#228;ltst du keine Zustellung mittels Einschreibebrief.";
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



    private NotificationAarForEmailAnalog createRequestAnalog(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new NotificationAarForEmailAnalog()
                .notification(createAarForEmailNotificationAnalog(context))
                .pnFaqSendURL(context.getPnFaqSendURL())
                .quickAccessLink(context.getQrCodeQuickAccessLink())
                .piattaformaNotificheURL(context.getPiattaformaNotificheURL())
                .perfezionamentoURL(context.getPerfezionamentoURL());
    }


    private AarForEmailNotificationAnalog createAarForEmailNotificationAnalog(TemplateRequestContext context) {
        return Optional.ofNullable(context.getNotification())
                .map(data -> new AarForEmailNotificationAnalog()
                        .iun(data.getIun())
                        .sender(createSenderAnalog(data)))
                .orElse(null);
    }


    private AarForEmailSenderAnalog createSenderAnalog(TemplateNotification notification) {
        return Optional.ofNullable(notification.getSender())
                .map(data -> new AarForEmailSenderAnalog()
                        .paDenomination(data.getPaDenomination()))
                .orElse(null);
    }
}

