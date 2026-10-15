package it.pagopa.interop.utils;

import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.common.interceptor.dpop.IntegrityValidationInterceptor;
import it.pagopa.interop.utils.delay_service.DelayService;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.isNull;

@Slf4j
@Getter
@Data
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HttpCallExecutor implements IHttpExecutor {

    private final String ONGOING_OPERATION_CONFLICT_ERROR = "Request conflicts with an ongoing operation on the same resource";
    private final int MAX_ATTEMPTS = 4;

    private HttpStatus responseStatus;
    private String errorMessage;
    private Object response;

    private HttpStatus snapResponseStatus;
    private Object snapResponse;
    private String snapErrorMessage;
    private int attempts = 0;
    private final DelayService delayService;

    public HttpCallExecutor(DelayService delayService) {
        this.delayService = delayService;
    }

    @Override
    public <T> HttpStatus performCall(Supplier<T> promise) {
        try {
            response = promise.get();
            responseStatus = HttpStatus.OK;
            errorMessage = null;
            attempts = 0;
        } catch (HttpStatusCodeException e) {
            response = null;
            responseStatus = e.getStatusCode();
            errorMessage = e.getMessage();
            handleConflicts(() -> performCall(promise));
        } catch (IntegrityValidationInterceptor.IntegrityValidationException e) {
            responseStatus = e.getHttpStatus();
            errorMessage = e.getMessage();
            attempts = 0;
        }
        return responseStatus;
    }

    private void handleConflicts(Runnable retryAction) {
        if (this.ongoingOperationConflict() && attempts++ <= MAX_ATTEMPTS) {
            log.warn("An ongoing operation conflict occurred, retrying: attempt {}...", attempts);
            delayService.delay();
            retryAction.run();
        }
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
            attempts = 0;
        } catch (HttpStatusCodeException e) {
            response = null;
            responseStatus = e.getStatusCode();
            errorMessage = e.getMessage();
            handleConflicts(() -> performCall(promise, httpStatusMapper));
        }
        return promiseResponse;
    }

    @Override
    public HttpStatus performCall(Runnable promise) {
        try {
            promise.run();
            responseStatus = HttpStatus.OK;
            attempts = 0;
        } catch (HttpStatusCodeException e) {
            response = null;
            responseStatus = e.getStatusCode();
            errorMessage = e.getMessage();
            handleConflicts(() -> performCall(promise));
        }
        return responseStatus;
    }

    @Override
    public Boolean ongoingOperationConflict() {
        return errorMessage != null &&
                errorMessage.contains(ONGOING_OPERATION_CONFLICT_ERROR);
    }

    public void setRawResponse(int statusCode, Object rawBody) {
        this.responseStatus = HttpStatus.valueOf(statusCode);
        this.response = rawBody;
    }

    @Override
    public void snapshot() {
        this.snapResponseStatus = this.responseStatus;
        this.snapResponse = this.response;
        this.snapErrorMessage = this.errorMessage;
    }

    @Override
    public void resetFormSnapshot() {
        this.responseStatus = this.snapResponseStatus;
        this.response = this.snapResponse;
        this.errorMessage = this.snapErrorMessage;
    }

}
