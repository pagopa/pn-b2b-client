package it.pagopa.pn.interop.cucumber.steps.utils;

import lombok.Data;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.function.Supplier;

@Data
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class HttpCallExecutor {
    private HttpStatus clientResponse;
    private Object response;

    public <T> HttpStatus performCall(Supplier<T> promise) {
        try {
            response = promise.get();
            clientResponse = HttpStatus.OK;
        } catch (HttpStatusCodeException e) {
            clientResponse = e.getStatusCode();
        }
        return clientResponse;
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
