package it.pagopa.pn.client.b2b.pa.parsing.dto.implResponse;

import it.pagopa.pn.client.b2b.pa.parsing.dto.IPnLegalFact;
import lombok.Getter;


@Getter
public class PnLegalFactResponse extends PnParserResponse {
    private final IPnLegalFact pnLegalFact;

    public PnLegalFactResponse(IPnLegalFact pnLegalFact) {
        super(null);
        this.pnLegalFact = pnLegalFact;
    }

    public PnLegalFactResponse(IPnLegalFact pnLegalFact, String field) {
        super(field);
        this.pnLegalFact = pnLegalFact;
    }
}