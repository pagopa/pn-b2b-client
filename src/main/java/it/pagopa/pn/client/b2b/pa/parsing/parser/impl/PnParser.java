package it.pagopa.pn.client.b2b.pa.parsing.parser.impl;

import it.pagopa.pn.client.b2b.pa.parsing.config.PnLegalFactTokens;
import it.pagopa.pn.client.b2b.pa.parsing.dto.*;
import it.pagopa.pn.client.b2b.pa.parsing.dto.implResponse.PnLegalFactResponse;
import it.pagopa.pn.client.b2b.pa.parsing.dto.implResponse.PnParserLegalFactResponse;
import it.pagopa.pn.client.b2b.pa.parsing.dto.IPnLegalFact;
import it.pagopa.pn.client.b2b.pa.parsing.dto.PnParserRecord;
import it.pagopa.pn.client.b2b.pa.parsing.dto.impLegalFact.*;
import it.pagopa.pn.client.b2b.pa.parsing.parser.IPnParser;
import it.pagopa.pn.client.b2b.pa.parsing.parser.IPnParserLegalFact;
import it.pagopa.pn.client.b2b.pa.parsing.parser.IPnParserLegalFact.LegalFactType;
import it.pagopa.pn.client.b2b.pa.parsing.service.IPnParserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class PnParser implements IPnParserService, IPnParser {
    private final PnParserLegalFact parserLegalFact;


    @Autowired
    public PnParser(PnLegalFactTokens pnLegalFactTokens)  {
        this.parserLegalFact = new PnParserLegalFact(pnLegalFactTokens);
    }

    @Override
    public IPnParserResponse extractSingleField(byte[] source, PnParserParameter parserParameter) {
        PnParserLegalFactResponse pnParserLegalFactResponse = (PnParserLegalFactResponse) extractAllField(source, parserParameter);
        if(pnParserLegalFactResponse == null)
            return null;

        IPnParserLegalFact.LegalFactField field = parserParameter.getLegalFactField();
        if(pnParserLegalFactResponse.getResponse().getPnLegalFact() instanceof PnLegalFactNotificaDowntime notificaDowntime) {
            pnParserLegalFactResponse.getResponse().setField(notificaDowntime.getAllLegalFactValues().fieldValue().get(field));
            return pnParserLegalFactResponse;
        } else if(pnParserLegalFactResponse.getResponse().getPnLegalFact() instanceof PnLegalFactNotificaDigitale notificaDigitale) {
            pnParserLegalFactResponse.getResponse().setField(notificaDigitale.getAllLegalFactValues().fieldValue().get(field));
            return pnParserLegalFactResponse;
        } else if(pnParserLegalFactResponse.getResponse().getPnLegalFact() instanceof PnLegalFactNotificaMancatoRecapito notificaMancatoRecapito) {
            pnParserLegalFactResponse.getResponse().setField(notificaMancatoRecapito.getAllLegalFactValues().fieldValue().get(field));
            return pnParserLegalFactResponse;
        }else if(pnParserLegalFactResponse.getResponse().getPnLegalFact() instanceof PnLegalFactNotificaPresaInCaricoMultiDestinatario notificaPresaInCaricoMultiDestinatario) {
            if(parserParameter.getMultiDestinatarioPosition() == 0) {
                pnParserLegalFactResponse.getResponse().setField(notificaPresaInCaricoMultiDestinatario.getAllLegalFactValues().fieldValue().get(field));
            } else {
                PnParserRecord.PnParserFieldValues parserFieldValues = notificaPresaInCaricoMultiDestinatario.getDestinatariAnalogici().get(parserParameter.getMultiDestinatarioPosition()-1).getAllDestinatarioValues();
                pnParserLegalFactResponse.getResponse().setField(parserFieldValues.fieldValue().get(field));
            }
            return pnParserLegalFactResponse;
        } else if(pnParserLegalFactResponse.getResponse().getPnLegalFact() instanceof PnLegalFactNotificaPresaInCarico notificaPresaInCarico) {
            pnParserLegalFactResponse.getResponse().setField(notificaPresaInCarico.getAllLegalFactValues().fieldValue().get(field));
            return pnParserLegalFactResponse;
        } else if(pnParserLegalFactResponse.getResponse().getPnLegalFact() instanceof PnLegalFactNotificaAvvenutoAccessoDelegato notificaAvvenutoAccessoDelegato) {
            pnParserLegalFactResponse.getResponse().setField(notificaAvvenutoAccessoDelegato.getAllLegalFactValues().fieldValue().get(field));
            return pnParserLegalFactResponse;
        } else if(pnParserLegalFactResponse.getResponse().getPnLegalFact() instanceof PnLegalFactNotificaAvvenutoAccesso notificaAvvenutoAccesso) {
            pnParserLegalFactResponse.getResponse().setField(notificaAvvenutoAccesso.getAllLegalFactValues().fieldValue().get(field));
            return pnParserLegalFactResponse;
        }
        return null;
    }

    @Override
    public IPnParserResponse extractAllField(byte[] source, PnParserParameter parserParameter) {
        IPnLegalFact legalFact = parse(source, parserParameter);
        if(legalFact == null) {
            return null;
        }
        return new PnParserLegalFactResponse(new PnLegalFactResponse(legalFact));
    }

    @Override
    public IPnLegalFact parse(byte[] source, PnParserParameter parserParameter) {
        LegalFactType legalFactType = parserParameter.getLegalFactType();
        PnParserRecord.PnParserContent content = parserLegalFact.extractContent(source, legalFactType);
        if(content == null) {
            return null;
        }

        if(legalFactType.equals(LegalFactType.LEGALFACT_NOTIFICA_DOWNTIME)) {
            return parserLegalFact.getLegalFactNotificaDowntime(content);
        } else if(legalFactType.equals(LegalFactType.LEGALFACT_NOTIFICA_DIGITALE)) {
            return parserLegalFact.getLegalFactNotificaDigitale(content);
        } else if(legalFactType.equals(LegalFactType.LEGALFACT_NOTIFICA_MANCATO_RECAPITO)) {
            return parserLegalFact.getLegalFactNotificaMancatoRecapito(content);
        } else if(legalFactType.equals(LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO)) {
            return parserLegalFact.getLegalFactNotificaPresaInCarico(content);
        } else if(legalFactType.equals(LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO_MULTIDESTINATARIO)) {
            return parserLegalFact.getLegalFactNotificaPresaInCaricoMultiDestinatario(content);
        } else if(legalFactType.equals(LegalFactType.LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO)) {
            return parserLegalFact.getLegalFactNotificaAvvenutoAccesso(content);
        } else if(legalFactType.equals(LegalFactType.LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO_DELEGATO)) {
            return parserLegalFact.getLegalFactNotificaAvvenutoAccessoDelegato(content);
        } else {
            return null;
        }
    }
}