package it.pagopa.pn.client.b2b.pa.provider;

import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SenderInfoProvider {

    private final Map<String, PaInfo> paInfoMap;

    @Getter
    public static class PaInfo {
        private final String paName;
        private final String senderId;
        private final SettableApiKey.ApiKeyType apiKeyType;
        private final SettableBearerToken.BearerTokenType bearerTokenType;

        public PaInfo(String paName, String senderId, SettableApiKey.ApiKeyType apiKeyType, SettableBearerToken.BearerTokenType bearerTokenType) {
            this.paName = paName;
            this.senderId = senderId;
            this.apiKeyType = apiKeyType;
            this.bearerTokenType = bearerTokenType;
        }
    }

    public SenderInfoProvider(
            @Value("${pn.external.senderId}") String senderId1,
            @Value("${pn.external.senderId-2}") String senderId2,
            @Value("${pn.external.senderId-GA}") String senderIdGA,
            @Value("${pn.external.senderId-SON}") String senderIdSON,
            @Value("${pn.external.senderId-ROOT}") String senderIdROOT) {

        this.paInfoMap = new HashMap<>();
        paInfoMap.put("COMUNE_1", new PaInfo(
                "COMUNE_1",
                senderId1,
                SettableApiKey.ApiKeyType.MVP_1,
                SettableBearerToken.BearerTokenType.MVP_1
        ));

        paInfoMap.put("COMUNE_2", new PaInfo(
                "COMUNE_2",
                senderId2,
                SettableApiKey.ApiKeyType.MVP_2,
                SettableBearerToken.BearerTokenType.MVP_2
        ));

        paInfoMap.put("COMUNE_MULTI", new PaInfo(
                "COMUNE_MULTI",
                senderIdGA,
                SettableApiKey.ApiKeyType.GA,
                SettableBearerToken.BearerTokenType.GA
        ));

        paInfoMap.put("COMUNE_SON", new PaInfo(
                "COMUNE_SON",
                senderIdSON,
                SettableApiKey.ApiKeyType.SON,
                SettableBearerToken.BearerTokenType.SON
        ));

        paInfoMap.put("COMUNE_ROOT", new PaInfo(
                "COMUNE_ROOT",
                senderIdROOT,
                SettableApiKey.ApiKeyType.ROOT,
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
}