package it.pagopa.pn.client.b2b.pa.service.utils;

import lombok.Getter;

@Getter
public enum RaddOperator {

    UPLOADER( "1234556", SettableAuthTokenRadd.AuthTokenRaddType.ISSUER_1),
    STANDARD( "1234556", SettableAuthTokenRadd.AuthTokenRaddType.ISSUER_2),
    WITHOUT_ROLE("1234556", SettableAuthTokenRadd.AuthTokenRaddType.ISSUER_3);

    private final String uid;
    private final SettableAuthTokenRadd.AuthTokenRaddType issuerType;

    RaddOperator(String uid, SettableAuthTokenRadd.AuthTokenRaddType issuerType) {
        this.uid = uid;
        this.issuerType = issuerType;
    }
}
