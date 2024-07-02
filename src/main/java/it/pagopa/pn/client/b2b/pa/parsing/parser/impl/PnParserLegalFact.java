package it.pagopa.pn.client.b2b.pa.parsing.parser.impl;

import it.pagopa.pn.client.b2b.pa.parsing.config.PnLegalFactTokens;
import it.pagopa.pn.client.b2b.pa.parsing.dto.*;
import it.pagopa.pn.client.b2b.pa.parsing.model.IPnLegalFact;
import it.pagopa.pn.client.b2b.pa.parsing.model.PnParserRecord;
import it.pagopa.pn.client.b2b.pa.parsing.model.impl.PnDestinatario;
import it.pagopa.pn.client.b2b.pa.parsing.model.impl.PnDestinatarioDigitale;
import it.pagopa.pn.client.b2b.pa.parsing.parser.IPnParserLegalFact;
import it.pagopa.pn.client.b2b.pa.parsing.parser.utils.PnLegalFactContent;


public class PnParserLegalFact extends PnLegalFactContent implements IPnParserLegalFact {
    public PnParserLegalFact(PnLegalFactTokens pnLegalFactTokens) {
        super(pnLegalFactTokens);
    }

    @Override
    public IPnLegalFact getLegalFactNotificaDowntime(PnParserRecord.PnParserContent content) {
        return PnLegalFactNotificaDowntime.builder()
                .dataOraDecorrenza(getDataOraDecorrenza(content))
                .dataOraFine(getDataOraFineDecorrenza(content))
                .build();
    }

    @Override
    public IPnLegalFact getLegalFactNotificaDigitale(PnParserRecord.PnParserContent content) {
        return PnLegalFactNotificaDigitale.builder()
                .iun(getIun(content, false))
                .pnDestinatario((PnDestinatarioDigitale) getDestinatario(content, true, false,true, false))
                .dataAttestazioneOpponibile(getDataAttestazioneOpponibile(content, false, false, false, true, false))
                .build();
    }

    @Override
    public IPnLegalFact getLegalFactNotificaMancatoRecapito(PnParserRecord.PnParserContent content) {
        return PnLegalFactNotificaMancatoRecapito.builder()
                .iun(getIun(content, false))
                .pnDestinatario((PnDestinatario) getDestinatario(content, true, false,false, false))
                .primaData(getPrimaData(content))
                .secondaData(getSecondaData(content))
                .dataAttestazioneOpponibile(getDataAttestazioneOpponibile(content, false, false, false, false, true))
                .build();
    }

    @Override
    public IPnLegalFact getLegalFactNotificaAvvenutoAccesso(PnParserRecord.PnParserContent content) {
        return PnLegalFactNotificaAvvenutoAccesso.builder()
                .iun(getIun(content, false))
                .dataAttestazioneOpponibile(getDataAttestazioneOpponibile(content, true, false, false, false, false))
                .pnDestinatario((PnDestinatario) getDestinatario(content, false, false,false, false))
                .build();
    }

    @Override
    public IPnLegalFact getLegalFactNotificaAvvenutoAccessoDelegato(PnParserRecord.PnParserContent content) {
        return PnLegalFactNotificaAvvenutoAccessoDelegato.builder()
                .iun(getIun(content, false))
                .dataAttestazioneOpponibile(getDataAttestazioneOpponibile(content, false, true, false, false, false))
                .pnDestinatario((PnDestinatario) getDestinatarioOrDelegato(content, true))
                .delegato((PnDestinatario) getDestinatarioOrDelegato(content, false))
                .build();
    }

    @Override
    public IPnLegalFact getLegalFactNotificaPresaInCarico(PnParserRecord.PnParserContent content) {
        return PnLegalFactNotificaPresaInCarico.builder()
                .dataAttestazioneOpponibile(getDataAttestazioneOpponibile(content, false, false, true, false, false))
                .mittente(getMittente(content))
                .cfMittente(getCfMittente(content))
                .iun(getIun(content, true))
                .pnDestinatario((PnDestinatario) getDestinatario(content, false,true, false, false))
                .build();
    }

    @Override
    public IPnLegalFact getLegalFactNotificaPresaInCaricoMultiDestinatario(PnParserRecord.PnParserContent content) {
        return PnLegalFactNotificaPresaInCaricoMultiDestinatario.builder()
                .dataAttestazioneOpponibile(getDataAttestazioneOpponibile(content, false, false, true, false, false))
                .mittente(getMittente(content))
                .cfMittente(getCfMittente(content))
                .iun(getIun(content, true))
                .pnDestinatario((PnDestinatario) getDestinatario(content, false,true, false, true))
                .destinatariAnalogici(getDestinatariAnalogici(content))
                .build();
    }
}