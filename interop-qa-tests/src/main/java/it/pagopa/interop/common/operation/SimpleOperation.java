package it.pagopa.interop.common.operation;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.function.Function;
import java.util.function.Supplier;

@Data
@AllArgsConstructor(staticName = "of")
public class SimpleOperation<T, R> implements IOperation<T, R> {
    private Supplier<T> apiCaller;
    private Function<T, R> resultExtractor;
}
