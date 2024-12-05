package it.pagopa.interop.domain;

import lombok.Data;

@Data
public class ExternalId {
    private String origin;
    private String value;
}
