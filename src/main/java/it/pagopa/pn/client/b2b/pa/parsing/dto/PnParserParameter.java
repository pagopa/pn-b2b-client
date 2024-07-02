package it.pagopa.pn.client.b2b.pa.parsing.dto;

import it.pagopa.pn.client.b2b.pa.parsing.service.IPnParserService;
import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class PnParserParameter {
    private IPnParserService.LegalFactField legalFactField;
    private IPnParserService.LegalFactType legalFactType;
    private int multiDestinatarioPosition;


    public boolean isValidField() {
        return legalFactType.getLegalFactFieldList().contains(legalFactField);
    }
}