package it.pagopa.pn.client.b2b.pa.wrapper;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public record ApiResult<T>(
        HttpStatus status,
        T body,                // valorizzato solo se 2xx
        String problem,        // valorizzato per non-2xx se disponibile
        HttpHeaders headers
) {
    public boolean is2xx() { return status.is2xxSuccessful(); }
}