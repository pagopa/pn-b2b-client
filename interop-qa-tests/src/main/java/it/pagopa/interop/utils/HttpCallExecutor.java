package it.pagopa.interop.utils;

import java.util.function.Function;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

@Slf4j
@Getter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HttpCallExecutor {
    private HttpStatus responseStatus;
    private Object response;

    public <T> HttpStatus performCall(Supplier<T> promise) {
        try {
            response = promise.get();
            log.trace("Response: {}", response);
            responseStatus = HttpStatus.OK;
        } catch (HttpStatusCodeException e) {
            responseStatus = e.getStatusCode();
        }
        return responseStatus;
    }

    public <T> T performCall(Supplier<T> promise, Function<T, HttpStatus> httpStatusMapper) {
        T promiseResponse = null;
        try {
            promiseResponse = promise.get();
            log.debug("Response: {}", promiseResponse);
            response = promiseResponse;
            responseStatus = httpStatusMapper.apply(promiseResponse);
        } catch (HttpStatusCodeException e) {
            responseStatus = e.getStatusCode();
        }
        return promiseResponse;
    }

    public HttpStatus performCall(Runnable promise) {
        try {
            promise.run();
            responseStatus = HttpStatus.OK;
        } catch (HttpStatusCodeException e) {
            responseStatus = e.getStatusCode();
        }
        return responseStatus;
    }
}
