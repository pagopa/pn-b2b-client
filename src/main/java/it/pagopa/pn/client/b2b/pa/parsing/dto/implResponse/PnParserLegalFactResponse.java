package it.pagopa.pn.client.b2b.pa.parsing.dto.implResponse;

import it.pagopa.pn.client.b2b.pa.parsing.dto.IPnParserResponse;


public class PnParserLegalFactResponse implements IPnParserResponse {
    private final PnLegalFactResponse pnLegalFactResponse;


    public PnParserLegalFactResponse(PnLegalFactResponse pnLegalFactResponse) {
        this.pnLegalFactResponse = pnLegalFactResponse;
    }

    @Override
    public PnLegalFactResponse getResponse() {
        return pnLegalFactResponse;
    }
}