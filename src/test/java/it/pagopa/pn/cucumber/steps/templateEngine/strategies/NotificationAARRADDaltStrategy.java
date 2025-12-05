package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.*;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateNotification;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class NotificationAARRADDaltStrategy implements ITemplateEngineStrategy {
    private final ITemplateEngineClient templateEngineClient;

    public NotificationAARRADDaltStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        NotificationAarRaddAlt legalFact = createRequest(body, context);
        Resource file = templateEngineClient.notificationAARRADDalt(selectLanguage(language), legalFact);
        return new TemplateEngineResult(file);
    }

    @Override
    public String getTextToCheckLanguage(String language, String recipientType) {
        return switch (language.toUpperCase()) {
            case  "ITALIANA" -> {
                yield "Accedi subito ai documenti online seguendo le istruzioni o, se preferisci, ritira i documenti in forma cartacea presso uno dei Punti di ritiro SEND (CAF e altri esercenti convenzionati). Tieni presente che il contenuto della comunicazione produrrà effetti giuridici nei tuoi confronti anche senza la tua presa visione.";
            }
            case "TEDESCA" -> {
                yield "Greife sofort online auf die Dokumente zu, indem du den Anweisungen folgst, oder, wenn du es vorziehst, hole die Dokumente in Papierform bei einer der SEND Abholstellen (CAF und andere vertraglich gebundene Betreiber) ab. Beachte, dass der Inhalt der Mitteilung dir gegenüber Rechtswirkungen entfaltet, auch wenn du die Dokumente nicht eingesehen hast.";
            }
            case "SLOVENA" -> {
                yield "Takoj dostopite do dokumentov na spletu, upoštevajoč navodila, ali, če želite, prevzemite dokumente v papirni obliki na enem od Prevzemnih mest SEND (CAF in drugi pooblaščeni izvajalci). Upoštevajte, da bo vsebina sporočila imela pravne učinke v zvezi z vami, tudi če dokumentov ne boste videli.";
            }
            case "FRANCESE" -> {
                yield "Accédez immédiatement aux documents en ligne en suivant les instructions, ou si vous préférez, retirez les documents sous forme papier auprès de l'un des Points de retrait SEND (CAF et autres prestataires conventionnés). Veuillez noter que le contenu de la communication produira des effets juridiques à votre égard même sans que vous ayez pris connaissance des documents";
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
