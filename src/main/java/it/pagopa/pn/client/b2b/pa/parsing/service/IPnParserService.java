package it.pagopa.pn.client.b2b.pa.parsing.service;

import it.pagopa.pn.client.b2b.pa.parsing.dto.IPnParserResponse;
import it.pagopa.pn.client.b2b.pa.parsing.dto.PnParserParameter;
import java.io.IOException;


public interface IPnParserService {
    IPnParserResponse extractSingleField(byte[] source, PnParserParameter parserParameter) throws IOException, NoSuchFieldException, IllegalAccessException;
    IPnParserResponse extractAllField(byte[] source, PnParserParameter parserParameter) throws IOException;
}