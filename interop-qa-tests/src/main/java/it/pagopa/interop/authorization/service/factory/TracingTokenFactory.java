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
public class TracingTokenFactory extends SessionTokenFactory {

    // well-known DEV link:
    // https://dev.interop.pagopa.it/.well-known/jwks.json
    private static final String WELLKNOWN_URL = "https://tracing-qa-only-well-known-qa.s3.eu-south-1.amazonaws.com/.well-known/jwks.json";

    private Map<String, Map<String, List<String>>> cachedTokens = null;

    public TracingTokenFactory(
        InteropClientConfigs interopClientConfigs,
        ConfigFileReader configFileReader,
        KmsClient kmsClient
    ) {
        super(interopClientConfigs, configFileReader, kmsClient);
    }

    @Override
    public Map<String, Map<String, List<String>>> loadToken() {
        getSessionTokenPayloadTemplate().put("aud", "{{ENVIRONMENT}}.interop.pagopa.it/m2m");
        try {
            if (cachedTokens == null) cachedTokens = generateSessionToken();
        } catch (Exception ex) {
            throw new IllegalArgumentException("There was an error while creating the session token: " + ex.getMessage(), ex);
        }
        return cachedTokens;
    }

    @Override
    public String getRemoteWellknownUrl() {
        return WELLKNOWN_URL;
    }

    @Override
    public String getMaintenanceToken() throws Exception {
        return "dummy";
    }
}
