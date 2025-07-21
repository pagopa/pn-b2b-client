package it.pagopa.interop;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class ListRequest {
    @NonNull
    private Integer offset;

    @NonNull
    private Integer limit;
}