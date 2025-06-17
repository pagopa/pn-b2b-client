package it.pagopa.interop.common;

import org.springframework.http.HttpStatus;

import java.util.function.Supplier;

public interface IHttpExecutor {
    <T> HttpStatus performCall(Supplier<T> promise);
    HttpStatus performCall(Runnable promise);
    HttpStatus getClientResponse();
    Object getResponse();
    void setRawResponse(int statusCode, Object rawBody);
}
