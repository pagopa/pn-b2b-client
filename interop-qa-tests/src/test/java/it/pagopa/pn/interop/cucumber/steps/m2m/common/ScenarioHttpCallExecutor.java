package it.pagopa.pn.interop.cucumber.steps.m2m.common;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.common.IHttpExecutor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.function.Supplier;

@Component
@ScenarioScope
@Data
public class ScenarioHttpCallExecutor implements IHttpExecutor {

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

