package it.pagopa.interop.authorization.service.utils;

public interface SettableBearerToken {
    enum BearerTokenType { CONSUMER, API, TENANT_1, TENANT_2}
    void setBearerToken(String bearerToken);
}