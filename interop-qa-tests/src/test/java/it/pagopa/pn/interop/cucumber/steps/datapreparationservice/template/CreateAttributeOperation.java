package it.pagopa.pn.interop.cucumber.steps.datapreparationservice.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
public class CreateAttributeOperation implements ICreateOperation<Object, UUID> {
    private Supplier<Object> apiCaller;
    private Function<Object, UUID> resultExtractor;
}
