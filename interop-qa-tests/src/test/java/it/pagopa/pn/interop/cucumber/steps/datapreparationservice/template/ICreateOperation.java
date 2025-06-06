package it.pagopa.pn.interop.cucumber.steps.datapreparationservice.template;

import java.util.function.Function;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public interface ICreateOperation<T, R> {
    Supplier<T> getApiCaller();
    Function<T, R> getResultExtractor();

    @Data
    @Builder
    @AllArgsConstructor(staticName = "of")
    class CreateOperationImpl<T,R> implements ICreateOperation <T,R> {
        private Supplier<T> apiCaller;
        private Function<T, R> resultExtractor;
    }
}