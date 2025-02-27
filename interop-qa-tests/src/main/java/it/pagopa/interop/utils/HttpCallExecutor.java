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
    private HttpStatus clientResponse;
    private Object response;

    public <T> HttpStatus performCall(Supplier<T> promise) {
        try {
            response = promise.get();
            log.trace("Response: {}", response);
            clientResponse = HttpStatus.OK;
        } catch (HttpStatusCodeException e) {
            clientResponse = e.getStatusCode();
        }
        return clientResponse;
    }

    public <T> T performCall(Supplier<T> promise, Function<T, HttpStatus> httpStatusMapper) {
        T promiseResponse = null;
        try {
            promiseResponse = promise.get();
            response = promiseResponse;
            clientResponse = httpStatusMapper.apply(promiseResponse);
        } catch (HttpStatusCodeException e) {
            clientResponse = e.getStatusCode();
        }
        return promiseResponse;
    }

    public HttpStatus performCall(Runnable promise) {
        try {
            promise.run();
            clientResponse = HttpStatus.OK;
        } catch (HttpStatusCodeException e) {
            clientResponse = e.getStatusCode();
        }
        return clientResponse;
    }


}
