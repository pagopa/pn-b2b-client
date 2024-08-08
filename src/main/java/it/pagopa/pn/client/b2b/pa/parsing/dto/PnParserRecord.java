package it.pagopa.pn.client.b2b.pa.parsing.dto;

import it.pagopa.pn.client.b2b.pa.parsing.parser.IPnParserLegalFact;
import java.util.List;
import java.util.Map;


public class PnParserRecord {
    public record PnParserContent(String text, List<String> valueList) {}
    public record PnParserFieldToken(String field, String value) {}
    public record PnParserFieldValues(Map<IPnParserLegalFact.LegalFactField, String> fieldValue) {}
}