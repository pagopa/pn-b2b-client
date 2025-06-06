package it.pagopa.pn.interop.cucumber.steps.datapreparationservice.template;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.function.Function;
import java.util.function.Supplier;

@Data
@AllArgsConstructor(staticName = "of")
public class SimpleCreateOperation<T, R> implements ICreateOperation<T, R> {
    private Supplier<T> apiCaller;
    private Function<T, R> resultExtractor;
}
