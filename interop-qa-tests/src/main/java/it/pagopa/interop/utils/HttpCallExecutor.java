package it.pagopa.interop.utils;

import it.pagopa.interop.common.IHttpExecutor;
import lombok.Data;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.function.Supplier;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Data
public class HttpCallExecutor implements IHttpExecutor {
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

    public void setRawResponse(int statusCode, Object rawBody) {
        this.clientResponse = HttpStatus.valueOf(statusCode);
        this.response = rawBody;
    }

}
