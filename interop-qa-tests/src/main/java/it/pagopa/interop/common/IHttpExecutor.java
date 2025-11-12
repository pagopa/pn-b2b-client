package it.pagopa.interop.common;

import java.util.function.Function;
import org.springframework.http.HttpStatus;

import java.util.function.Supplier;
import org.springframework.http.ResponseEntity;

public interface IHttpExecutor {
    <T> HttpStatus performCall(Supplier<T> promise);

    <T> T performCallSavingBodyResponse(Supplier<ResponseEntity<T>> promise);

    /* TODO 11/03/2025: potrebbe essere il caso di restituire sempre una ResponseEntity,
     * eventualmente con body vuoto e statusCode debitamente valorizzato in caso di eccezioni,
     * facendo così anche a meno del secondo parametro. Probabilmente si avrebbe un comportamento
     * meglio standardizzato, visto che la versione attuale potenzialmente potrebbe lasciare
     * l'oggetto response a NULL, in caso di eccezioni. */
    <T> T performCall(Supplier<T> promise, Function<T, HttpStatus> httpStatusMapper);

    HttpStatus performCall(Runnable promise);
    HttpStatus getResponseStatus();
    Object getResponse();
    String getErrorMessage();
    void setRawResponse(int statusCode, Object rawBody);
}
