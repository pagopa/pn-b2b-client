package it.pagopa.pn.interop.cucumber.steps.authorization.domain;

import lombok.Data;

@Data
public class ExternalId {
    private String origin;
    private String value;
}
