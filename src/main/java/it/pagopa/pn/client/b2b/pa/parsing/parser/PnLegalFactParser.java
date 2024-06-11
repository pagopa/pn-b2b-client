package it.pagopa.pn.client.b2b.pa.parsing.parser;

import it.pagopa.pn.client.b2b.pa.parsing.config.PnB2bLegalFactTextTokens;
import it.pagopa.pn.client.b2b.pa.parsing.design.IPnLegalFact;
import it.pagopa.pn.client.b2b.pa.parsing.dto.*;
import it.pagopa.pn.client.b2b.pa.parsing.service.IPnParserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import java.util.HashMap;


@Slf4j
@Component
public class PnLegalFactParser extends PnCoreTokens implements IPnParserService {
    @Autowired
    private ResourceLoader resourceLoader;


    @Autowired
    public PnLegalFactParser(PnB2bLegalFactTextTokens pnB2bLegalFactTextTokens)  {
        super(pnB2bLegalFactTextTokens);
    }

    @Override
    public HashMap<String, Object> extractSingleField(String source, IPnParserService.LegalFactKeyValues field, LegalFactType legalFactType) {
        HashMap<String, Object> result = new HashMap<>();

        if (legalFactType.getLegalFactKeyValuesList().contains(field)) {
            IPnLegalFact pnLegalFact = parse(source, legalFactType);
            if(pnLegalFact instanceof PnLegalFactNotificaDowntime notificaDowntime) {
                result.put(field.getField(), notificaDowntime.getAllLegalFactValues().get(field.getField()));
                return result;
            } else if(pnLegalFact instanceof PnLegalFactNotificaDigitale notificaDigitale) {
                if(field.getField().equals(LegalFactKeyValues.DESTINATARIO.getField())){
                    return getDestinatario(notificaDigitale, false);
                } else {
                    result.put(field.getField(), notificaDigitale.getAllLegalFactValues().get(field.getField()));
                }
                return result;
            } else if(pnLegalFact instanceof PnLegalFactNotificaMancatoRecapito notificaMancatoRecapito) {
                if(field.getField().equals(LegalFactKeyValues.DESTINATARIO.getField())){
                    return getDestinatario(notificaMancatoRecapito, false);
                } else if(field.getField().equals(LegalFactKeyValues.SECONDO_DESTINATARIO_DIGITALE.getField())){
                    return getSecondoDestinatarioDigitale(notificaMancatoRecapito);
                } else {
                    result.put(field.getField(), notificaMancatoRecapito.getAllLegalFactValues().get(field.getField()));
                }
                return result;
            } else if(pnLegalFact instanceof PnLegalFactNotificaPresaInCaricoMultiDestinatario notificaPresaInCaricoMultiDestinatario) {
                if(field.getField().equals(LegalFactKeyValues.DESTINATARIO.getField())){
                    return getDestinatario(notificaPresaInCaricoMultiDestinatario, false);
                } else if(field.getField().equals(LegalFactKeyValues.DESTINATARI_ANALOGICI.getField())){
                    return getDestinatariAnalogici(notificaPresaInCaricoMultiDestinatario);
                } else {
                    result.put(field.getField(), notificaPresaInCaricoMultiDestinatario.getAllLegalFactValues().get(field.getField()));
                }
                return result;
            } else if(pnLegalFact instanceof PnLegalFactNotificaPresaInCarico notificaPresaInCarico) {
                if(field.getField().equals(LegalFactKeyValues.DESTINATARIO.getField())){
                    return getDestinatario(notificaPresaInCarico, false);
                } else {
                    result.put(field.getField(), notificaPresaInCarico.getAllLegalFactValues().get(field.getField()));
                }
                return result;
            } else if(pnLegalFact instanceof PnLegalFactNotificaAvvenutoAccessoDelegato notificaAvvenutoSuccessoDelegato) {
                if(field.getField().equals(LegalFactKeyValues.DESTINATARIO.getField())){
                    return getDestinatario(notificaAvvenutoSuccessoDelegato, false);
                } else if(field.getField().equals(LegalFactKeyValues.DELEGATO.getField())){
                    return getDelegato(notificaAvvenutoSuccessoDelegato);
                } else {
                    result.put(field.getField(), notificaAvvenutoSuccessoDelegato.getAllLegalFactValues().get(field.getField()));
                }
                return result;
            } else if(pnLegalFact instanceof PnLegalFactNotificaAvvenutoAccesso notificaAvvenutoSuccesso) {
                if(field.getField().equals(LegalFactKeyValues.DESTINATARIO.getField())){
                    return getDestinatario(notificaAvvenutoSuccesso, false);
                } else {
                    result.put(field.getField(), notificaAvvenutoSuccesso.getAllLegalFactValues().get(field.getField()));
                }
                return result;
            }
        }
        return result;
    }

    @Override
    public IPnLegalFact extractAllField(String source, LegalFactType legalFactType) {
        return parse(source, legalFactType);
    }

    private IPnLegalFact parse(String source, LegalFactType legalFactType) {
        String pdfText = formatAndCleanUp(extractContent(source));
        if(legalFactType.equals(LegalFactType.LEGALFACT_NOTIFICA_DOWNTIME)) {
            return super.getLegalFactNotificaDowntime(pdfText);
        } else if(legalFactType.equals(LegalFactType.LEGALFACT_NOTIFICA_DIGITALE)) {
            return super.getLegalFactNotificaDigitale(pdfText);
        } else if(legalFactType.equals(LegalFactType.LEGALFACT_NOTIFICA_MANCATO_RECAPITO)) {
            return super.getLegalFactNotificaMancatoRecapito(pdfText, legalFactType);
        } else if(legalFactType.equals(LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO)) {
            return super.getLegalFactNotificaPresaInCarico(pdfText, legalFactType);
        } else if(legalFactType.equals(LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO_MULTIDESTINATARIO)) {
            return super.getLegalFactNotificaPresaInCaricoMultiDestinatario(pdfText, legalFactType);
        } else if(legalFactType.equals(LegalFactType.LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO)) {
            return super.getLegalFactNotificaAvvenutoAccesso(pdfText);
        } else if(legalFactType.equals(LegalFactType.LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO_DELEGATO)) {
            return super.getLegalFactNotificaAvvenutoAccessoDelegato(pdfText);
        }
        return null;
    }

    private String extractContent(final String source) {
        Resource resource = resourceLoader.getResource(source);
        try (final PDDocument document = Loader.loadPDF(resource.getFile())) {
            final PDFTextStripper pdfStripper = new PDFTextStripper();
            pdfStripper.setSortByPosition(true);
            return pdfStripper.getText(document);
        } catch (Exception exception) {
            log.error("Error parsing PDF {}", exception);
        }
        return null;
    }
}