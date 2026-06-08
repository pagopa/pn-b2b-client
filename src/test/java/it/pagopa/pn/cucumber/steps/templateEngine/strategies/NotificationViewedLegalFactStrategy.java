package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.*;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class NotificationViewedLegalFactStrategy  implements ITemplateEngineStrategy{
    private ITemplateEngineClient templateEngineClient;

    public NotificationViewedLegalFactStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        NotificationViewedLegalFact legalFact = createRequest(body, context);
        Resource file = templateEngineClient.notificationViewedLegalFact(selectLanguage(language), legalFact);
        return new TemplateEngineResult(file);
    }

    @Override
    public String getTextToCheckLanguage(String language, String recipientType) {
        return switch (language.toUpperCase()) {
            case  "ITALIANA" -> {
                yield "Attestazione opponibile a terzi: avvenuto accesso Ai sensi dell’art. 26, comma 11, del decreto-legge 76/2020 s.m.i., la PagoPA S.p.A. nella sua qualità di gestore ex lege della Piattaforma Notifiche Digitali di cui allo stesso art. 26 (anche nota come SEND - Servizio Notifiche Digitali), con ogni valore legale per l'opponibilità a terzi, ATTESTA CHE";
            }
            case "TEDESCA" -> {
                yield "Attestazione opponibile a terzi: avvenuto accesso Gemäß Artikel 26, Absatz 11 des Gesetzesdekrets 76/2020 in geltender Fassung BESCHEINIGT PagoPA S.p.A. als rechtmäßiger Betreiber der im selben Artikel 26 genannten Plattform für digitale Zustellungen (auch bekannt als SEND - Servizio Notifiche Digitali), mit jeglicher Rechtskräftigkeit für die Entgegenhaltung gegenüber Dritten, HIERMIT, dass:";
            }
            case "SLOVENA" -> {
                yield "Attestazione opponibile a terzi: avvenuto accesso V skladu z 11. odstavkom 26. člena zakonskega odloka 76/2020 s spremembami, družba PagoPA S.p.A. kot zakoniti upravljavec Platforme za digitalno obveščanje, opredeljene v istem 26. členu (znana tudi kot SEND – Servizio Notifiche Digitali), z vsemi pravnimi učinki za veljavnost do tretjih oseb, POTRJUJE, DA:";
            }
            case "FRANCESE" -> {
                yield "Attestazione opponibile a terzi: avvenuto accesso Conformément à l’art. 26, paragraphe 11, du décret-loi italien 76/2020 tel que modifié et complété, la société PagoPA , en sa qualité d’opérateur ex lege de la Plateforme Notifications Numériques visée audit article 26 (également S.p.A. connue sous le nom de SEND - Servizio Notifiche Digitali), avec plein effet juridique quant à l’opposabilité aux tiers, CERTIFIE QUE:";
            }
            case  "INGLESE" -> {
                yield "Attestazione opponibile a terzi: avvenuto accesso Pursuant to Art. 26, paragraph 11, of Decree-Law 76/2020 as amended, PagoPA S.p.A. in its capacity as the statutory manager of the Piattaforma Notifiche Digitali referred to in the same Art. 26 (also known as SEND - Servizio Notifiche Digitali), with full legal value for enforceability against third parties, HEREBY CERTIFIES THAT";
            }
            default -> throw new IllegalArgumentException("NO VALID LANGUANGE");
        };
    }

    private NotificationViewedLegalFact createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new NotificationViewedLegalFact()
                .iun(context.getIun())
                .delegate(createDelegate(context))
                .when(context.getWhen())
                .recipient(createRecipient(context));
    }

    private NotificationViewedDelegate createDelegate(TemplateRequestContext context) {
        return Optional.ofNullable(context.getDelegate())
                .map(data -> new NotificationViewedDelegate()
                        .denomination(data.getDenomination())
                        .taxId(data.getTaxId())
                )
                .orElse(null);
    }

    private NotificationViewedRecipient createRecipient(TemplateRequestContext context) {
        return Optional.ofNullable(context.getRecipient())
                .map(data -> new NotificationViewedRecipient()
                        .denomination(data.getDenomination())
                        .taxId(data.getTaxId()))
                .orElse(null);
    }
}
