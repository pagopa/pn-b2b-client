package it.pagopa.pn.client.b2b.pa.parsing.model;

import it.pagopa.pn.client.b2b.pa.parsing.service.IPnParserService;
import java.util.List;
import java.util.Map;


public class PnParserRecord {
    public record PnParserContent(String text, List<String> valueList) {}
    public record PnParserFieldToken(String field, String value) {}
    public record PnParserFieldValues(Map<IPnParserService.LegalFactField, String> fieldValue) {}
}