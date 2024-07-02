package it.pagopa.pn.client.b2b.pa.parsing.parser;

import it.pagopa.pn.client.b2b.pa.parsing.model.PnParserRecord;
import it.pagopa.pn.client.b2b.pa.parsing.parser.utils.PnTextSlidingWindow;
import it.pagopa.pn.client.b2b.pa.parsing.service.IPnParserService;
import java.util.List;


public interface IPnContentExtractor {
    PnParserRecord.PnParserContent extractContent(byte[] source, IPnParserService.LegalFactType legalFactType);
    PnParserRecord.PnParserContent getContent(String text, List<String> values, IPnParserService.LegalFactType legalFactType);
    String getField(PnTextSlidingWindow pnTextSlidingWindow, List<String> values);
    String cleanUp(String text, boolean mantainWhitespace);
}