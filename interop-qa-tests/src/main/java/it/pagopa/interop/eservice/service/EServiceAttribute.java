package it.pagopa.interop.eservice.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public
class EServiceAttribute<T> {
    private Integer groupIndex;
    private T attribute;
}
