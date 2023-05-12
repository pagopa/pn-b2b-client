package it.pagopa.pn.client.b2b.pa.testclient;

public interface SettableBearerToken {
    enum BearerTokenType { USER_1, USER_2 , PG_1 , PG_2}

    boolean setBearerToken(BearerTokenType bearerToken);

    BearerTokenType getBearerTokenSetted();
}
