package it.pagopa.pn.client.b2b.pa.parsing.parser;

import it.pagopa.pn.client.b2b.pa.parsing.dto.IPnLegalFact;
import it.pagopa.pn.client.b2b.pa.parsing.dto.PnParserParameter;


public interface IPnParser {
    IPnLegalFact parse(byte[] source, PnParserParameter parserParameter);
}