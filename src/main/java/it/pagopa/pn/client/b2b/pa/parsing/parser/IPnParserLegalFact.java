package it.pagopa.pn.client.b2b.pa.parsing.parser;

import it.pagopa.pn.client.b2b.pa.parsing.model.IPnLegalFact;
import it.pagopa.pn.client.b2b.pa.parsing.model.PnParserRecord;


public interface IPnParserLegalFact {
    IPnLegalFact getLegalFactNotificaDowntime(PnParserRecord.PnParserContent content);
    IPnLegalFact getLegalFactNotificaDigitale(PnParserRecord.PnParserContent content);
    IPnLegalFact getLegalFactNotificaMancatoRecapito(PnParserRecord.PnParserContent content);
    IPnLegalFact getLegalFactNotificaAvvenutoAccesso(PnParserRecord.PnParserContent content);
    IPnLegalFact getLegalFactNotificaAvvenutoAccessoDelegato(PnParserRecord.PnParserContent content);
    IPnLegalFact getLegalFactNotificaPresaInCarico(PnParserRecord.PnParserContent content);
    IPnLegalFact getLegalFactNotificaPresaInCaricoMultiDestinatario(PnParserRecord.PnParserContent content);
}