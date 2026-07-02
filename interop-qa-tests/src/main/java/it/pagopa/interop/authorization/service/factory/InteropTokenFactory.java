package it.pagopa.interop.authorization.service.factory;

import it.pagopa.interop.authorization.service.utils.ConfigFileReader;
import it.pagopa.interop.conf.InteropClientConfigs;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.kms.KmsClient;

@Slf4j
@Getter
@Setter
public class InteropTokenFactory extends SessionTokenFactory {
//    private static final String WELLKNOWN_URL = "https://qa.interop.pagopa.it/.well-known/jwks.json";
    private Map<String, Map<String, List<String>>> cachedTokens = null;

    public InteropTokenFactory(
        InteropClientConfigs interopClientConfigs,
        ConfigFileReader configFileReader,
        KmsClient kmsClient
        ) {
        super(interopClientConfigs, configFileReader, kmsClient);
        loadToken();
    }

    public synchronized Map<String, Map<String, List<String>>> loadToken() {
        getSessionTokenPayloadTemplate().put("aud", "{{ENVIRONMENT}}.interop.pagopa.it/ui");
        try {
            if (cachedTokens == null) cachedTokens = generateSessionToken();
        } catch (Exception ex) {
            throw new IllegalArgumentException("There was an error while creating the session token: " + ex.getMessage(), ex);
        }
        return cachedTokens;
    }

    @Override
    public String getRemoteWellknownUrl() {
        return super.getInteropClientConfigs().getRemoteWellknownUrl();
    }
}
