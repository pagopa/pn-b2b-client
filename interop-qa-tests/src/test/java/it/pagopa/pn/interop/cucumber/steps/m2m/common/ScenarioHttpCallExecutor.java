package it.pagopa.pn.interop.cucumber.steps.m2m.common;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.common.IHttpExecutor;
import java.util.function.Function;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.function.Supplier;

@Component
@ScenarioScope
@Data
public class ScenarioHttpCallExecutor implements IHttpExecutor {

    private HttpStatus responseStatus;
    private Object response;
    private String errorMessage;

    @Override
    public <T> T performCallSavingBodyResponse(Supplier<ResponseEntity<T>> promise) {
        T body = performCall(promise, ResponseEntity::getStatusCode).getBody();
        response = body;
        return body;
    }

    /* TODO 11/03/2025: potrebbe essere il caso di restituire sempre una ResponseEntity,
     * eventualmente con body vuoto e statusCode debitamente valorizzato in caso di eccezioni,
     * facendo così anche a meno del secondo parametro. Probabilmente si avrebbe un comportamento
     * meglio standardizzato, visto che la versione attuale potenzialmente potrebbe lasciare
     * l'oggetto response a NULL, in caso di eccezioni. */
    @Override
    public <T> T performCall(Supplier<T> promise, Function<T, HttpStatus> httpStatusMapper) {
        T promiseResponse = null;
        try {
            promiseResponse = promise.get();
            response = promiseResponse;
            responseStatus = httpStatusMapper.apply(promiseResponse);
        } catch (HttpStatusCodeException e) {
            responseStatus = e.getStatusCode();
            errorMessage = e.getMessage();
        }
        return promiseResponse;
    }

    @Override
    public <T> HttpStatus performCall(Supplier<T> promise) {
        try {
            response = promise.get();
            responseStatus = HttpStatus.OK;
        } catch (HttpStatusCodeException e) {
            responseStatus = e.getStatusCode();
        }
        return responseStatus;
    }

    @Override
    public HttpStatus performCall(Runnable promise) {
        try {
            promise.run();
            responseStatus = HttpStatus.OK;
        } catch (HttpStatusCodeException e) {
            responseStatus = e.getStatusCode();
        }
        return responseStatus;
    }

    public void setRawResponse(int statusCode, Object rawBody) {
        this.clientResponse = HttpStatus.valueOf(statusCode);
        this.response = rawBody;
    }

}

