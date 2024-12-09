package it.pagopa.interop.authorization.domain;

import lombok.Data;

@Data
public class ExternalId {
    private String origin;
    private String value;
}
