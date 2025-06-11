package it.pagopa.pn.interop.cucumber.steps.datapreparationservice.template;

import it.pagopa.interop.common.operation.IOperation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
public class AttributeOperation implements IOperation<Object, UUID> {
    private Supplier<Object> apiCaller;
    private Function<Object, UUID> resultExtractor;
}
