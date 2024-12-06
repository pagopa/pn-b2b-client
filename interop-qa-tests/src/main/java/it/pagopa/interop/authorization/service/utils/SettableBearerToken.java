package it.pagopa.interop.authorization.service.utils;

public interface SettableBearerToken {
    enum BearerTokenType { CONSUMER, API}
    void setBearerToken(String bearerToken);
    BearerTokenType getBearerTokenSetted();
}