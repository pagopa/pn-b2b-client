package it.pagopa.pn.client.b2b.pa.service.utils;


public interface SettableBearerToken {
    enum BearerTokenType {
        USER_1,
        USER_2,
        USER_3,
        USER_4,
        USER_5,
        PG_1,
        PG_2,
        PG_3,
        PG_4,
        PG_5,
        PG_B2B_1,
        PG_B2B_2,
        USER_SCADUTO,
        MVP_1,
        MVP_2,
        GA,
        SON,
        ROOT,
        SUPPORT_1,
        TENANT_1,
        TENANT_2
    }

    boolean setBearerToken(BearerTokenType bearerToken);

    BearerTokenType getBearerTokenSetted();
}