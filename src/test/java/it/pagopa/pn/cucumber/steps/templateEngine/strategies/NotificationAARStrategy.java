package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AarNotification;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AarRecipient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AarSender;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.NotificationAar;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateNotification;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class NotificationAARStrategy implements ITemplateEngineStrategy {

    private final ITemplateEngineClient templateEngineClient;

    public NotificationAARStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        NotificationAar legalFact = createRequest(body, context);
        Resource file = templateEngineClient.notificationAAR(selectLanguage(language), legalFact);
        return new TemplateEngineResult(file);
    }

    @Override
    public String getTextToCheckLanguage(String language, String recipientType) {
        return switch (language.toUpperCase()) {
            case  "ITALIANA" -> {
                yield "string COMUNICAZIONE A VALORE LEGALE Identificativo Univoco Notifica: string Codice fiscale - Persona giuridica: string Inviata tramite notifichedigitali.it Hai ricevuto una comunicazione a valore legale da string con oggetto: string Prendi visione della copia dei documenti allegati o accedi ai documenti originali online seguendo le istruzioni. Tieni presente che il contenuto della comunicazione produrrà effetti giuridici nei tuoi confronti anche senza la tua presa visione dei documenti. ACCEDI ORA AI DOCUMENTI ONLINE Consultazione online gratuita Inquadra il con la fotocamera del tuo dispositivo oppure vai sul sito web codice QR string Accedi e scarica i documenti: contengono informazioni importanti che ti riguardano Se previsto un pagamento, puoi pagare con tutti i canali abilitati a pagoPA L’importo si aggiornerà con i costi di notifica: 2 € per le notifiche digitali, più elevati per la raccomandata cartacea, inviata solo in assenza di canali digitali o se non apri la comunicazione in tempo Il presente documento è una comunicazione a valore legale che ti invita a prendere visione dei documenti a te notificati e che avranno conseguenze nei tuoi confronti in ogni caso. Se previsto un pagamento, l'importo da pagare dipenderà dalla modalità che scegli per accedere alla notifica, dalla tipologia dell'eventuale raccomandata a te inviata e scelta dall'ente, dal numero di tentativi di recapito. Hai 120 giorni dalla data in cui la notifica assume valore di legge per accedere ai documenti online. Trascorsi i 120 giorni, i documenti non saranno più disponibili e dovrai rivolgerti all'ente che te li ha inviati. Scopri di più su come calcolare i tempi su www.string";
            }
            case "TEDESCA" -> {
                yield "AVVISO DI AVVENUTA RICEZIONE Benachrichtigung über den erfolgten Empfang • Avviso di Avvenuta Ricezione (AAR): string Du hast eine rechtsgültige Mitteilung von der Körperschaft string erhalten: . Sollten siehe die zugestellten Dokumente diese Dokumente nicht der vorliegenden Mitteilung beigefügt sein, kannst du im Internet über SEND auf diese";
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

    private NotificationAar createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new NotificationAar()
                .recipient(createRecipient(context))
                .notification(createNotification(context))
                .qrCodeQuickAccessLink(context.getQrCodeQuickAccessLink())
                .piattaformaNotificheURL(context.getPiattaformaNotificheURL())
                .piattaformaNotificheURLLabel(context.getPiattaformaNotificheURLLabel())
                .perfezionamentoURL(context.getPerfezionamentoURL())
                .perfezionamentoURLLabel(context.getPerfezionamentoURLLabel());
    }

    private AarRecipient createRecipient(TemplateRequestContext context) {
        return Optional.ofNullable(context.getRecipient())
                .map(data -> new AarRecipient()
                        .recipientType(data.getRecipientType())
                        .taxId(data.getTaxId()))
                .orElse(null);
    }

    private AarNotification createNotification(TemplateRequestContext context) {
        return Optional.ofNullable(context.getNotification())
                .map(data -> new AarNotification()
                        .iun(data.getIun())
                        .subject(data.getSubject())
                        .sender(createSender(data)))
                .orElse(null);
    }

    private AarSender createSender(TemplateNotification notification) {
        return Optional.ofNullable(notification.getSender())
                .map(data -> new AarSender()
                        .paDenomination(data.getPaDenomination()))
                .orElse(null);
    }
}
