package it.pagopa.interop.common.operation;

import java.util.function.Function;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public interface IOperation<T, R> {
    Supplier<T> getApiCaller();
    Function<T, R> getResultExtractor();

    @Data
    @Builder
    @AllArgsConstructor(staticName = "of")
    class OperationImpl<T,R> implements IOperation<T,R> {
        private Supplier<T> apiCaller;
        private Function<T, R> resultExtractor;
    }
}