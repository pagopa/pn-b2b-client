package it.pagopa.interop.eservice.service;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public
class EServiceAttribute<T> {

    private Integer groupIndex;
    private T attribute;
}
