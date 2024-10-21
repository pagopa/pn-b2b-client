package it.pagopa.pn.client.b2b.pa.parsing.dto;

import it.pagopa.pn.client.b2b.pa.parsing.parser.IPnParserLegalFact;
import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class PnParserParameter {
    private IPnParserLegalFact.LegalFactField legalFactField;
    private IPnParserLegalFact.LegalFactType legalFactType;
    private int multiDestinatarioPosition;


    public boolean isValidField() {
        if(legalFactField != null) {
            return legalFactType.getLegalFactFieldList().contains(legalFactField);
        }
        return true;
    }
}