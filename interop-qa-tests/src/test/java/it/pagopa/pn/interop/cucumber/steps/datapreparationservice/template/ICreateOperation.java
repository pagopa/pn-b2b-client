package it.pagopa.pn.interop.cucumber.steps.datapreparationservice.template;

import java.util.function.Function;
import java.util.function.Supplier;

public interface ICreateOperation<T, R> {
    Supplier<T> getApiCaller();
    Function<T, R> getResultExtractor();
}