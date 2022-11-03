package it.pagopa.pn.client.b2b.pa.testclient;

public interface SettableApiKey {

    enum ApiKeyType { MVP_1, MVP_2, GA }

    boolean setApiKeys(ApiKeyType apiKey);

    ApiKeyType getApiKeySetted();
}
