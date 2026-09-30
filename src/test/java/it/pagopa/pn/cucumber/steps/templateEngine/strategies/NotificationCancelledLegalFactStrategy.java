package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.*;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateNotification;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class NotificationCancelledLegalFactStrategy implements ITemplateEngineStrategy {

    private ITemplateEngineClient templateEngineClient;

    public NotificationCancelledLegalFactStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        NotificationCancelledLegalFact legalFact = createRequest(body, context);
        Resource file = templateEngineClient.notificationCancelledLegalFact(selectLanguage(language), legalFact);
        return new TemplateEngineResult(file);
    }

    @Override
    public String getTextToCheckLanguage(String language, String recipientType) {
        return switch (language.toUpperCase()) {
            case  "ITALIANA" -> {
                yield "Dichiarazione annullamento notifica sulla piattaforma SEND Con il presente documento, reso disponibile al destinatario esclusivamente sulla Piattaforma SEND, si dichiara che la notifica avente IUN string, eseguita nei confronti di:";
            }
            case "TEDESCA" -> {
                yield "Erklärung über die Annullierung der Zustellung auf der SEND-Plattform Dichiarazione annullamento notifica sulla piattaforma SEND Mit diesem Dokument, das dem Empfänger ausschließlich auf der SEND-Plattform zur Verfügung steht, wird erklärt, dass die Zustellung mit IUN string durchgeführt an den Empfänger:";
            }
            case "SLOVENA" -> {
                yield "Obvestilo o preklicu obvestila na platformi SEND Dichiarazione annullamento notifica sulla piattaforma SEND S tem dokumentom, ki je prejemniku na voljo izključno na platformi SEND, se izjavlja, da je obvestilo z IUN string za prejemnik:";
            }
            case "FRANCESE" -> {
                yield "Déclaration d'annulation de la notification sur la plate-forme SEND Dichiarazione annullamento notifica sulla piattaforma SEND Il est déclaré par la présente, mise à la disposition du destinataire exclusivement sur la plate-forme SEND, que la notification comportant IUN string, exécutée à l'encontre de:";
            }
            case  "INGLESE" -> {
                yield "Declaration of cancellation of notification on the SEND platform Dichiarazione annullamento notifica sulla piattaforma SEND With this document, made available to the recipient exclusively on the SEND Platform, it is declared that the notification with IUN string, performed against:";
            }
            default -> throw new IllegalArgumentException("NO VALID LANGUANGE");
        };
    }

    private NotificationCancelledLegalFact createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new NotificationCancelledLegalFact()
                .notification(createNotification(context))
                .notificationCancelledDate(context.getNotificationCancelledDate());
    }

    private NotificationCancelledNotification createNotification(TemplateRequestContext context) {
        return Optional.ofNullable(context.getNotification())
                .map(data -> new NotificationCancelledNotification()
                        .iun(data.getIun())
                        .sender(createSender(data))
                        .recipients(createRecipients(data)))
                .orElse(null);
    }

    private NotificationCancelledSender createSender(TemplateNotification notification) {
        return Optional.ofNullable(notification.getSender())
                .map(data -> new NotificationCancelledSender()
                        .paDenomination(data.getPaDenomination()))
                .orElse(null);
    }

    private List<NotificationCancelledRecipient> createRecipients(TemplateNotification notification) {
        return Optional.ofNullable(notification.getRecipients())
                .map(data -> data.stream()
                        .map(d -> new NotificationCancelledRecipient()
                                .denomination(d.getDenomination())
                                .taxId(d.getTaxId()))
                        .toList())
                .orElse(null);
    }
}
