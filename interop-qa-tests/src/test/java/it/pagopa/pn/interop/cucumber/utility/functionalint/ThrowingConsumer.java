package it.pagopa.pn.interop.cucumber.utility.functionalint;

@FunctionalInterface
public interface ThrowingConsumer<T> {
    void accept(T t) throws Exception;
}
