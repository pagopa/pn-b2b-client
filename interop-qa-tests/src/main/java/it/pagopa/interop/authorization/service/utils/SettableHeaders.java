package it.pagopa.interop.authorization.service.utils;

import org.springframework.http.HttpHeaders;

public interface SettableHeaders {
    void setHeaders(HttpHeaders headers);
}