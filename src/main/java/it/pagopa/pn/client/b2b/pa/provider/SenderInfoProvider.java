package it.pagopa.pn.client.b2b.pa.provider;

import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SenderInfoProvider {

    private final Map<String, PaInfo> paInfoMap;

    @Getter
    @RequiredArgsConstructor
    public static class PaInfo {
        private final String paName;
        private final String senderId;
        private final SettableApiKey.ApiKeyType apiKeyType;
        private final String apiKey;
        private final SettableBearerToken.BearerTokenType bearerTokenType;
    }

    public SenderInfoProvider(

            @Value("${pn.external.senderId}") String senderId1,
            @Value("${pn.external.senderId-2}") String senderId2,
            @Value("${pn.external.senderId-GA}") String senderIdGA,
            @Value("${pn.external.senderId-SON}") String senderIdSON,
            @Value("${pn.external.senderId-ROOT}") String senderIdROOT,

            @Value("${pn.external.api-key}") String apiKeyMvp1,
            @Value("${pn.external.api-key-2}") String apiKeyMvp2,
            @Value("${pn.external.api-key-GA}") String apiKeyGa,
            @Value("${pn.external.api-key-SON}") String apiKeySon,
            @Value("${pn.external.api-key-ROOT}") String apiKeyRoot
    ) {

        this.paInfoMap = new HashMap<>();
        paInfoMap.put("COMUNE_1", new PaInfo(
                "COMUNE_1",
                senderId1,
                SettableApiKey.ApiKeyType.MVP_1,
                apiKeyMvp1,
                SettableBearerToken.BearerTokenType.MVP_1
        ));

        paInfoMap.put("COMUNE_2", new PaInfo(
                "COMUNE_2",
                senderId2,
                SettableApiKey.ApiKeyType.MVP_2,
                apiKeyMvp2,
                SettableBearerToken.BearerTokenType.MVP_2
        ));

        paInfoMap.put("COMUNE_MULTI", new PaInfo(
                "COMUNE_MULTI",
                senderIdGA,
                SettableApiKey.ApiKeyType.GA,
                apiKeyGa,
                SettableBearerToken.BearerTokenType.GA
        ));

        paInfoMap.put("COMUNE_SON", new PaInfo(
                "COMUNE_SON",
                senderIdSON,
                SettableApiKey.ApiKeyType.SON,
                apiKeySon,
                SettableBearerToken.BearerTokenType.SON
        ));

        paInfoMap.put("COMUNE_ROOT", new PaInfo(
                "COMUNE_ROOT",
                senderIdROOT,
                SettableApiKey.ApiKeyType.ROOT,
                apiKeyRoot,
                SettableBearerToken.BearerTokenType.ROOT
        ));
    }

    public PaInfo getPaInfo(String paName) {
        PaInfo info = paInfoMap.get(paName.toUpperCase());
        if (info == null) {
            throw new IllegalArgumentException("Invalid paName: " + paName);
        }
        return info;
    }

    public String getSenderId(String paName) {
        return getPaInfo(paName).getSenderId();
    }

    public SettableApiKey.ApiKeyType getApiKeyType(String paName) {
        return getPaInfo(paName).getApiKeyType();
    }

    public SettableBearerToken.BearerTokenType getBearerTokenType(String paName) {
        return getPaInfo(paName).getBearerTokenType();
    }

    public String getApiKey(String paName) {
        return getPaInfo(paName).getApiKey();

    }
}