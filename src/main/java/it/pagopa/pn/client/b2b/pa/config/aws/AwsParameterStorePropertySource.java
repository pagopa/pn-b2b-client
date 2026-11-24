package it.pagopa.pn.client.b2b.pa.config.aws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.PropertySource;
import software.amazon.awssdk.services.ssm.SsmAsyncClient;
import software.amazon.awssdk.services.ssm.model.ParameterNotFoundException;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Parametri di AWS Parameter Store indicati nel placeholder con il nome completo, es. ${/pn-test-e2e/domain:fallback}.
 * Ogni parametro viene letto una sola volta, al primo utilizzo.
 */
@Slf4j
public class AwsParameterStorePropertySource extends PropertySource<SsmAsyncClient> {

    private static final String NAME = "awsParameterStore";
    // Solo nomi SSM validi: Spring cerca anche "nome:fallback" intero prima di separare il fallback
    private static final Pattern PARAMETER_NAME = Pattern.compile("/[A-Za-z0-9_./-]+");

    private final Map<String, Optional<String>> cache = new ConcurrentHashMap<>();

    public AwsParameterStorePropertySource(SsmAsyncClient ssmClient) {
        super(NAME, ssmClient);
    }

    @Override
    public Object getProperty(String name) {
        if (!PARAMETER_NAME.matcher(name).matches()) {
            return null;
        }
        return cache.computeIfAbsent(name, this::readParameter).orElse(null);
    }

    private Optional<String> readParameter(String name) {
        try {
            return Optional.ofNullable(AwsConfigEnvironmentPostProcessor.join(getSource()
                            .getParameter(r -> r.name(name).withDecryption(true)))
                    .parameter()
                    .value());
        } catch (ParameterNotFoundException e) {
            log.warn("AWS parameter {} not found, fallback will be used", name);
        } catch (RuntimeException e) {
            log.warn("AWS parameter {} not loaded, fallback will be used: {}", name, e.getMessage());
        }
        return Optional.empty();
    }
}
