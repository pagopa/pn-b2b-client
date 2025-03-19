package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.*;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateNotification;
import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateRecipient;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Component
public class NotificationReceiverLegalFactStrategy implements ITemplateEngineStrategy {

    private String ITA_TEXT;
    private String DE_TEXT;
    private String SL_TEXT;
    private String FR_TEXT;

    private ITemplateEngineClient templateEngineClient;

    public NotificationReceiverLegalFactStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        NotificationReceivedLegalFact legalFact = createRequest(body, context);
        Resource file = templateEngineClient.notificationReceivedLegalFact(selectLanguage(language), legalFact);
        return new TemplateEngineResult(file);
    }

    @Override
    public String getTextToCheckLanguage(String language) {
        return switch (language.toUpperCase()) {
            case  "ITALIANA" -> {
                yield "notifica presa in carico Ai sensi dell’art. 26, comma 11, del decreto-legge 76/2020 s.m.i., la PagoPA S.p.A. nella sua qualità di gestore ex lege della Piattaforma Notifiche Digitali di cui allo stesso art. 26 (anche nota come SEND - Servizio Notifiche Digitali), con ogni valore legale per l'opponibilità a terzi, ATTESTA CHE:";
            }
            case "TEDESCA" -> {
                yield "Gemäß Artikel 26, Absatz 11 des Gesetzesdekrets 76/2020 in seiner geänderten und ergänzten Fassung BESCHEINIGT PagoPA S.p.A. als rechtmäßiger Verwalter der im selben Artikel 26 genannten Plattform für digitale Zustellungen (auch bekannt als SEND - Servizio Notifiche Digitali), mit aller rechtlichen Befugnis, diese Dritten gegenüber zu vertreten, HIERMIT, dass: am die sendende Person , Steuernummer dem Betreiber die in IUN genannten und mit den string string string string folgenden Hashes eindeutig identifizierten IT-Dokumente zur Verfügung gestellt hat: Ai sensi dell’art. 26, comma 11, del decreto-legge 76/2020 s.m.i., la PagoPA S.p.A. nella sua qualità di gestore ex lege della Piattaforma Notifiche Digitali di cui allo stesso art. 26 (anche nota come SEND - Servizio Notifiche Digitali), con ogni valore legale per l'opponibilità a terzi, ATTESTA CHE";
            }
            case "SLOVENA" -> {
                yield "V skladu z 11. odstavkom 26. člena zakonskega odloka 76/2020 s spremembami, družba PagoPA S.p.A. kot zakoniti upravljavec Platforme za digitalno obveščanje, opredeljene v istem 26. členu (znana tudi kot SEND - Servizio Notifiche Digitali), z vsemi pravnimi učinki za veljavnost do tretjih oseb, POTRJUJE, DA: je , davčna številka , dne dal upravljavcu na voljo elektronske dokumente, navedene v IUN , ki string string string string so identificirani z naslednjimi zgoščenimi vrednostmi (hash): Ai sensi dell’art. 26, comma 11, del decreto-legge 76/2020 s.m.i., la PagoPA S.p.A. nella sua qualità di gestore ex lege della Piattaforma Notifiche Digitali di cui allo stesso art. 26 (anche nota come SEND - Servizio Notifiche Digitali), con ogni valore legale per l'opponibilità a terzi, ATTESTA CHE";
            }
            case "FRANCESE" -> {
                yield "Conformément à l’art. 26, paragraphe 11, du décret-loi italien 76/2020 tel que modifié et complété, la société PagoPA S. p.A., en sa qualité d’opérateur ex lege de la Plateforme Notifications Numériques visée audit article 26 (également connue sous le nom de SEND - Servizio Notifiche Digitali), avec plein effet juridique quant à l’opposabilité aux tiers, CERTIFIE QUE";
            }
            default -> throw new IllegalArgumentException("NO VALID LANGUANGE");
        };
    }


    private NotificationReceivedLegalFact createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new NotificationReceivedLegalFact()
                .notification(createNotification(context))
                .subject(context.getSubject())
                .digests(context.getDigests())
                .sendDate(context.getSendDate());
    }

    private NotificationReceivedNotification createNotification(TemplateRequestContext context) {
        return Optional.ofNullable(context.getNotification())
                .map(data -> new NotificationReceivedNotification()
                            .iun(data.getIun())
                            .sender(createSender(data))
                            .recipients(createRecipients(data)))
                .orElse(null);
    }

    private NotificationReceivedSender createSender(TemplateNotification notification) {
        return Optional.ofNullable(notification.getSender())
                .map(data -> new NotificationReceivedSender()
                        .paDenomination(data.getPaDenomination())
                        .paTaxId(data.getPaTaxId()))
                .orElse(null);
    }

    private List<NotificationReceivedRecipient> createRecipients(TemplateNotification notification) {
        return Optional.ofNullable(notification.getRecipients())
                .map(data -> data.stream()
                        .map(d -> new NotificationReceivedRecipient()
                                .denomination(d.getDenomination())
                            .taxId(d.getTaxId())
                            .physicalAddressAndDenomination(d.getPhysicalAddressAndDenomination())
                            .digitalDomicile(createDigitalDomicile(d)))
                        .toList())
                .orElse(null);
    }

    private NotificationReceivedDigitalDomicile createDigitalDomicile(TemplateRecipient recipient) {
        return Optional.ofNullable(recipient.getDigitalDomicile())
                .map(data -> new NotificationReceivedDigitalDomicile()
                        .address(data.getAddress()))
                .orElse(null);
    }
}
