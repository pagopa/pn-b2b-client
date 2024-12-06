package it.pagopa.pn.interop.cucumber.steps.utils;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.function.Supplier;

@Data
public class HttpCallExecutor {
    private HttpStatus clientResponse = HttpStatus.OK;
    private Object response;

    public <T> HttpStatus performCall(Supplier<T> promise) {
        try {
            response = promise.get();
        } catch (HttpClientErrorException e) {
            clientResponse = HttpStatus.FORBIDDEN;
        }
        return clientResponse;
    }

    public HttpStatus performCall(Runnable promise) {
        try {
            promise.run();
        } catch (HttpClientErrorException e) {
            clientResponse = HttpStatus.FORBIDDEN;
        }
        return clientResponse;
    }


}
