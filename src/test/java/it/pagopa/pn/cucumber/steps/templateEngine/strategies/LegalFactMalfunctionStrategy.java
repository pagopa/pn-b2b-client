package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.MalfunctionLegalFact;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class LegalFactMalfunctionStrategy implements ITemplateEngineStrategy {

    private ITemplateEngineClient templateEngineClient;

    public LegalFactMalfunctionStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        MalfunctionLegalFact legalFact = createRequest(body, context);
        Resource file = templateEngineClient.legalFactMalfunction(selectLanguage(language), legalFact);
        return new  TemplateEngineResult(file);
    }

    @Override
    public String getTextToCheckLanguage(String language) {
        return switch (language.toUpperCase()) {
            case  "ITALIANA" -> {
                yield "Attestazione opponibile a terzi: malfunzionamento e ripristino Ai sensi dell’art. 26, comma 11, del decreto-legge 76/2020 s.m.i., la PagoPA S.p.A. nella sua qualità di gestore ex lege della Piattaforma Notifiche Digitali di cui allo stesso art. 26 (anche nota come SEND - Servizio Notifiche Digitali), con ogni valore legale per l'opponibilità a terzi, ATTESTA CHE";
            }
            case "TEDESCA" -> {
                yield "Attestazione opponibile a terzi: malfunzionamento e ripristino Gemäß Artikel 26, Absatz 11 des Gesetzesdekrets 76/2020 in seiner geänderten und ergänzten Fassung BESCHEINIGT PagoPA S.p.A. als rechtmäßiger Verwalter der im selben Artikel 26 genannten Plattform für digitale Zustellungen (auch bekannt als SEND - Servizio Notifiche Digitali), mit aller rechtlichen Befugnis, diese Dritten gegenüber zu vertreten, HIERMIT, dass:";
            }
            case "SLOVENA" -> {
                yield "Attestazione opponibile a terzi: malfunzionamento e ripristino V skladu z 11. odstavkom 26. člena zakonskega odloka 76/2020 s spremembami, družba PagoPA S.p.A. kot zakoniti upravljavec Platforme za digitalno obveščanje, opredeljene v istem 26. členu (znana tudi kot SEND - Servizio Notifiche Digitali), z vsemi pravnimi učinki za veljavnost do tretjih oseb, POTRJUJE, DA:";
            }
            case "FRANCESE" -> {
                yield "Attestazione opponibile a terzi: malfunzionamento e ripristino Conformément à l’art. 26, paragraphe 11, du décret-loi italien 76/2020 tel que modifié et complété, la société PagoPA S. p.A., en sa qualité d’opérateur ex lege de la Plateforme Notifications Numériques visée au même article 26 (également connue sous le nom de SEND - Servizio Notifiche Digitali), avec plein effet juridique quant à l’opposabilité aux tiers, CERTIFIE QUE:";
            }
            default -> throw new IllegalArgumentException("NO VALID LANGUANGE");
        };
    }

    private MalfunctionLegalFact createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new MalfunctionLegalFact()
                .endDate(context.getEndDate())
                .startDate(context.getStartDate())
                .timeReferenceStartDate(context.getTimeReferenceStartDate())
                .timeReferenceEndDate(context.getTimeReferenceEndDate());
    }
}
