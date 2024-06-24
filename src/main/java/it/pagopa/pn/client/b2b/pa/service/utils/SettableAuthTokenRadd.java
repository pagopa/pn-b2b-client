package it.pagopa.pn.client.b2b.pa.service.utils;


public interface SettableAuthTokenRadd {
    enum AuthTokenRaddType { ISSUER_1, ISSUER_2, ISSUER_NON_CENSITO, DATI_ERRATI, ISSUER_SCADUTO, AUD_ERRATA, KID_DIVERSO, PRIVATE_DIVERSO, HEADER_ERRATO, OVER_50KB }

    boolean setAuthTokenRadd(AuthTokenRaddType bearerToken);
    AuthTokenRaddType getAuthTokenRaddSetted();
}