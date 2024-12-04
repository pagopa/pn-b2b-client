package it.pagopa.interop.service.utils;

public interface SettableBearerToken {
    enum BearerTokenType { CONSUMER, API}
    void setBearerToken(String bearerToken);
    BearerTokenType getBearerTokenSetted();
}