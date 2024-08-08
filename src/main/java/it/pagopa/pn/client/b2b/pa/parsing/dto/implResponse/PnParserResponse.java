package it.pagopa.pn.client.b2b.pa.parsing.dto.implResponse;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PnParserResponse  {
    private String field;

    public PnParserResponse(String field) {
        this.field = field;
    }
}