package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.*;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateNotification;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Optional;

@Component
public class NotificationAARRADDaltStrategy implements ITemplateEngineStrategy {
    private ITemplateEngineClient templateEngineClient;

    public NotificationAARRADDaltStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        NotificationAarRaddAlt legalFact = createRequest(body, context);
        Resource file = templateEngineClient. notificationAARRADDalt(selectLanguage(language), legalFact);
        return new TemplateEngineResult(file);
    }

    @Override
    public String getTextToCheckLanguage(String language) {
        return switch (language.toUpperCase()) {
            case  "ITALIANA" -> {
                yield "AVVISO DI AVVENUTA RICEZIONE 1. 2. 3. RITIRA UNA COPIA SUL TERRITORIO - A PAGAMENTO Richiedi i documenti notificati anche senza SPID o CIE presso i Punti di ritiro SEND (CAF e altri esercenti convenzionati).";
            }
            case "TEDESCA" -> {
                yield "AVVISO DI AVVENUTA RICEZIONE Empfangsbestätigung Feststellungs • Avviso di Avvenuta Ricezione (AAR): string Du hast eine rechtsgültige Mitteilung von string: . Wenn sie nicht in diesem siehe die zugestellten Dokumente Bescheid enthalten sind,";
            }
            case "SLOVENA" -> {
                yield "AVVISO DI AVVENUTA RICEZIONE Obvestilo o prejemu: • Avviso di Avvenuta Ricezione (AAR): string Prejeli ste uradno sporočilo od string: . Če niso vključeni v to obvestilo, Prejeli ste uradno sporočilo od do dokumentov .";
            }
            case "FRANCESE" -> {
                yield "AVVISO DI AVVENUTA RICEZIONE Accusé de réception • Avviso di Avvenuta Ricezione (AAR): string Vous avez reçu une communication ayant une valeur juridique de string: . S'ils ne figurent voir les documents notifiés.";
            }
            default -> throw new IllegalArgumentException("NO VALID LANGUANGE");
        };
    }

    private NotificationAarRaddAlt createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new NotificationAarRaddAlt()
                .recipient(createRecipient(context))
                .notification(createNotification(context))
                .qrCodeQuickAccessLink(context.getQrCodeQuickAccessLink())
                .piattaformaNotificheURL(context.getPiattaformaNotificheURL())
                .piattaformaNotificheURLLabel(context.getPiattaformaNotificheURLLabel())
                .perfezionamentoURL(context.getPerfezionamentoURL())
                .perfezionamentoURLLabel(context.getPerfezionamentoURLLabel())
                .sendURL(context.getSendURL())
                .sendURLLAbel(context.getSendURLLAbel())
                .raddPhoneNumber(context.getRaddPhoneNumber());
    }

    private AarRaddAltNotification createNotification(TemplateRequestContext context) {
        return Optional.ofNullable(context.getNotification())
                .map(data -> new AarRaddAltNotification()
                        .iun(data.getIun())
                        .subject(data.getSubject())
                        .sender(createSender(data)))
                .orElse(null);
    }

    private AarRaddAltRecipient createRecipient(TemplateRequestContext context) {
        return Optional.ofNullable(context.getRecipient())
                .map(data -> new AarRaddAltRecipient()
                        .denomination(data.getDenomination())
                        .taxId(data.getTaxId())
                        .recipientType(data.getRecipientType()))
                .orElse(null);
    }

    private AarRaddAltSender createSender(TemplateNotification notification) {
        return Optional.ofNullable(notification.getSender())
                .map(data -> new AarRaddAltSender()
                        .paDenomination(data.getPaDenomination()))
                .orElse(null);
    }
}
