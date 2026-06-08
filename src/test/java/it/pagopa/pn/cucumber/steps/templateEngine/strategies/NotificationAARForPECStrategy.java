package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AarForPecNotification;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AarForPecRecipient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AarForPecSender;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.NotificationAarForPec;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateNotification;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class NotificationAARForPECStrategy implements ITemplateEngineStrategy {
    private final ITemplateEngineClient templateEngineClient;
    private Map<String, Map<String, String>> expectedText = Map.of(
            "ITALIANA", Map.of("PG", "L&#39;ente mittente conosce questo indirizzo PEC perch&eacute; l&#39;impresa lo ha inserito nei suoi recapiti di SEND, perch&eacute; fa parte di uno dei registri previsti dal Codice Amministrazione Digitale o perch&eacute; gli &egrave; stato comunicato in passato. Per modificare i recapiti o aggiungerne altri, <a title=\"Continua su Piattaforma Notifiche\" class=\"link\" href=\"https://cittadini.dev.notifichedigitali.it/recapiti\" style=\"color: #0073e6; text-decoration: underline\">vai ai tuoi recapiti</a>.",
                    "PF", "L&#39;ente mittente conosce questo indirizzo PEC perch&eacute; l&#39;hai inserito nei tuoi recapiti di SEND, perch&eacute; fa parte di uno dei registri previsti dal Codice Amministrazione Digitale o perch&eacute; gliel&#39;hai comunicato in passato. Per modificare i recapiti o aggiungerne altri, <a title=\"Continua su Piattaforma Notifiche\" class=\"link\" href=\"https://cittadini.dev.notifichedigitali.it/recapiti\" style=\"color: #0073e6; text-decoration: underline\">vai ai tuoi recapiti</a>."),
            "TEDESCA", Map.of("PG", "Die ausgebende K&#246;rperschaft kennt diese PEC-Adresse, weil Ihr Unternehmen sie in deine SEND-Adressen aufgenommen hat, weil sie Teil eines der im Kodex der digitalen Verwaltung vorgesehenen Register ist oder weil  sie ihr fr&#252;her mitgeteilt wurde. Um Adressen zu bearbeiten oder weitere hinzuzuf&#252;gen, <a title=\"Weiter auf der Plattform Benachrichtigungen\" class=\"link\" href=\"https://cittadini.dev.notifichedigitali.it/recapiti\" style=\"color: #0073e6; text-decoration: underline\">gehe bitte zu deinen Adressen</a>.",
                    "PF", "Die ausgebende K&#246;rperschaft kennt diese PEC-Adresse, weil du sie in deine SEND-Adressen aufgenommen hast, weil sie Teil eines der im Kodex der digitalen Verwaltung vorgesehenen Register ist oder weil du sie ihr fr&uuml;her mitgeteilt hast. Um Adressen zu bearbeiten oder weitere hinzuzuf&uuml;gen, <a title=\"Weiter auf der Plattform Benachrichtigungen\" class=\"link\" href=\"https://cittadini.dev.notifichedigitali.it/recapiti\" style=\"color: #0073e6; text-decoration: underline\">gehe bitte zu deinen Adressen</a>"),
            "SLOVENA", Map.of("PG", "Organizacija po&#353;iljateljica pozna ta naslov PEC, ker ste ga vnesli v svoje kontaktne podatke SEND, ker je del enega od registrov, ki jih dolo&#269;a Kodeks o digitalni upravi, ali ker ste jim ga v preteklosti sporo&#269;ili. &#268;e &#382;elite spremeniti svoje kontaktne podatke ali dodati nove, <a title=\"Nadaljujte na platformo za obvestila\" class=\"link\" href=\"https://cittadini.dev.notifichedigitali.it/recapiti\" style=\"color: #0073e6; text-decoration: underline\">pojdite na svoje kontaktne podatke</a>",
                    "PF", "Organizacija po&#353;iljateljica pozna ta naslov PEC, ker ga je podjetje vneslo v svoje kontaktne podatke SEND, ker je del enega od registrov, ki jih dolo&#269;a Kodeks o digitalni upravi, ali ker ga je v preteklosti sporo&#269;ili. &#268;e &#382;elite spremeniti svoje kontaktne podatke ali dodati nove, <a title=\"Nadaljujte na platformo za obvestila\" class=\"link\" href=\"https://cittadini.dev.notifichedigitali.it/recapiti\" style=\"color: #0073e6; text-decoration: underline\">pojdite na svoje kontaktne podatke</a>."),
            "FRANCESE", Map.of("PG", "L&#39;entit&eacute; &eacute;mettrice conna&icirc;t cette adresse PEC parce que l&#39;entreprise l&#39;a incluse dans ses coordonn&eacute;es SEND, parce qu&#39;elle fait partie d&#39;un des registres pr&eacute;vus par le Code Administration Num&eacute;rique ou parce qu&#39;elle lui a &#xE9;t&#xE9; communiqu&eacute;e dans le pass&eacute;. Pour modifier les coordonn&eacute;es ou en ajouter d&#39;autres, <a title=\"Continuer sur la Plateforme Notifications\" class=\"link\" href=\"https://cittadini.dev.notifichedigitali.it/recapiti\" style=\"color: #0073e6; text-decoration: underline\">acc&eacute;dez &agrave; vos coordonn&eacute;es</a>",
                    "PF", "L&#39;entit&eacute; &eacute;mettrice conna&icirc;t cette adresse PEC parce que vous l&#39;avez incluse dans vos coordonn&eacute;es SEND, parce qu&#39;elle fait partie d&#39;un des registres pr&eacute;vus par le Code Administration Num&eacute;rique ou parce vous la lui avez communiqu&eacute;e dans le pass&eacute;. Pour modifier les coordonn&eacute;es ou en ajouter d&#39;autres, <a title=\"Continuer sur la Plateforme Notifications\" class=\"link\" href=\"https://cittadini.dev.notifichedigitali.it/recapiti\" style=\"color: #0073e6; text-decoration: underline\">acc&eacute;dez &agrave; vos coordonn&eacute;es</a>."),
            "INGLESE", Map.of("PG", "The sending institution knows this PEC address because you entered it in your SEND contact details, because it is part of one of the registries provided for by the Digital Administration Code, or because it was communicated to them in the past. To change your contact details or add new ones, <a title=\"Continue to Piattaforma Notifiche\" class=\"link\" href=\"https://cittadini.dev.notifichedigitali.it/recapiti\" style=\"color: #0073e6; text-decoration: underline\">go to your contact details</a>",
                    "PF", "The sending institution knows this PEC address because you entered it in your SEND contact details, because it is part of one of the registries provided for by the Digital Administration Code, or because it was communicated to them in the past. To change your contact details or add new ones, <a title=\"Continue to Piattaforma Notifiche\" class=\"link\" href=\"https://cittadini.dev.notifichedigitali.it/recapiti\" style=\"color: #0073e6; text-decoration: underline\">go to your contact details</a>.")
    );

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
    public String getTextToCheckLanguage(String language, String recipientType) {
        return Optional.ofNullable(expectedText.get(language.toUpperCase()))
                .map(e -> e.get(recipientType.toUpperCase()))
                .orElseThrow(() -> new IllegalArgumentException("NO VALID LANGUANGE"));
    }

    private NotificationAarForPec createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new NotificationAarForPec()
                .notification(createNotification(context))
                //.pnFaqSendURL(context.getPnFaqSendURL())
                .quickAccessLink(context.getQrCodeQuickAccessLink())
                //.piattaformaNotificheURL(context.getPiattaformaNotificheURL())
                //.perfezionamentoURL(context.getPerfezionamentoURL())
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
