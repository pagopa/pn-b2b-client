package it.pagopa.interop.utils;

import static java.util.Objects.isNull;

import it.pagopa.interop.common.IHttpExecutor;
import lombok.Data;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

@Slf4j
@Getter
@Data
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HttpCallExecutor implements IHttpExecutor {
    private HttpStatus responseStatus;
    private String errorMessage;
    private Object response;

    @Override
    public <T> HttpStatus performCall(Supplier<T> promise) {
        try {
            response = promise.get();
            responseStatus = HttpStatus.OK;
        } catch (HttpStatusCodeException e) {
            responseStatus = e.getStatusCode();
            errorMessage = e.getMessage();
        }
        return responseStatus;
    }

    @Override
    public <T> T performCallSavingBodyResponse(Supplier<ResponseEntity<T>> promise) {
        ResponseEntity<T> tResponseEntity = performCall(promise, ResponseEntity::getStatusCode);
        T body = isNull(tResponseEntity) ? null : tResponseEntity.getBody();
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
    public HttpStatus performCall(Runnable promise) {
        try {
            promise.run();
            responseStatus = HttpStatus.OK;
        } catch (HttpStatusCodeException e) {
            responseStatus = e.getStatusCode();
            errorMessage = e.getMessage();
        }
        return responseStatus;
    }
}
