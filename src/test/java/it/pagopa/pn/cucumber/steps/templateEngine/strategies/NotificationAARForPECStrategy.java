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
    private final ITemplateEngineClient templateEngineClient;

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
                yield "L&apos;ente mittente conosce questo indirizzo PEC perch&eacute; l&apos;hai inserito nei tuoi recapiti di SEND, perch&eacute; fa parte di uno dei registri previsti dal Codice Amministrazione Digitale o perch&eacute; gliel&apos;hai comunicato in passato. Per modificare i recapiti o aggiungerne altri, <a title=\"Continua su Piattaforma Notifiche\" class=\"link\" href=\"stringrecapiti\" style=\"color: #0073e6; text-decoration: underline\">vai ai tuoi recapiti";
            }
            case "TEDESCA" -> {
                yield "Die ausgebende K&#246;rperschaft kennt diese PEC-Adresse, weil du sie in deine SEND-Adressen aufgenommen hast, weil sie Teil eines der im Kodex der digitalen Verwaltung vorgesehenen Register ist oder weil du sie ihr fr&#252;her mitgeteilt hast. Um Adressen zu bearbeiten oder weitere hinzuzuf&#252;gen, <a title=\"Weiter auf der Plattform Benachrichtigungen\" class=\"link\" href=\"stringrecapiti\" style=\"color: #0073e6; text-decoration: underline\">gehe bitte zu deinen Adressen</a>";
            }
            case "SLOVENA" -> {
                yield "Organizacija po&#353;iljateljica pozna ta naslov PEC, ker ste ga vnesli v svoje kontaktne podatke SEND, ker je del enega od registrov, ki jih dolo&#269;a Kodeks o digitalni upravi, ali ker ste jim ga v preteklosti sporo&#269;ili. &#268;e &#382;elite spremeniti svoje kontaktne podatke ali dodati nove, <a title=\"Nadaljujte na platformo za obvestila\" class=\"link\" href=\"stringrecapiti\" style=\"color: #0073e6; text-decoration: underline\">pojdite na svoje kontaktne podatke</a>";
            }
            case "FRANCESE" -> {
                yield "L&apos;entit&eacute; &eacute;mettrice conna&icirc;t cette adresse PEC parce que vous l&apos;avez incluse dans vos coordonn&eacute;es SEND, parce qu&apos;elle fait partie d&apos;un des registres pr&eacute;vus par le Code Administration Num&eacute;rique ou parce que vous la lui avez communiqu&eacute;e dans le pass&eacute;. Pour modifier les coordonn&eacute;es ou en ajouter d&apos;autres, <a title=\"Continuer sur la Plateforme Notifications\" class=\"link\" href=\"stringrecapiti\" style=\"color: #0073e6; text-decoration: underline\">acc&eacute;dez &agrave; vos coordonn&eacute;es</a>";
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
