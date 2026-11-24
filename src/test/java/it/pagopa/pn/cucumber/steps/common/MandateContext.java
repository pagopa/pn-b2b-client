package it.pagopa.pn.cucumber.steps.common;

import it.pagopa.pn.client.b2b.pa.domain.Destinatario;
import lombok.Data;

@Data
public class MandateContext {
    private String mandateId;
    private Destinatario delegate;
    private Destinatario delegator;
}
