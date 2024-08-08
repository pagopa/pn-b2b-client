package it.pagopa.pn.client.b2b.pa.parsing.parser;

import it.pagopa.pn.client.b2b.pa.parsing.dto.PnParserRecord;
import it.pagopa.pn.client.b2b.pa.parsing.parser.utils.PnTextSlidingWindow;
import java.util.List;


public interface IPnContentExtractor {
    PnParserRecord.PnParserContent extractContent(byte[] source, IPnParserLegalFact.LegalFactType legalFactType);
    PnParserRecord.PnParserContent getContent(String text, List<String> values, IPnParserLegalFact.LegalFactType legalFactType);
    String getField(PnTextSlidingWindow pnTextSlidingWindow, List<String> values);
    String cleanUp(String text, boolean mantainWhitespace);
}