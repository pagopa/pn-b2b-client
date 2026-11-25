package it.pagopa.pn.client.b2b.pa.config.aws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySources;
import org.springframework.core.env.StandardEnvironment;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerAsyncClient;
import software.amazon.awssdk.services.ssm.SsmAsyncClient;
import software.amazon.awssdk.services.ssm.model.GetParametersResponse;
import software.amazon.awssdk.services.ssm.model.Parameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rende disponibili come property i valori di AWS Secrets Manager e Parameter Store, così nei file di properties
 * basta il placeholder: ${e2eTestApiKey:fallback} per una chiave dei segreti, ${/pn-test-e2e/domain:fallback}
 * per un parametro SSM. Se il valore AWS manca si usa il fallback.
 * <p>
 * Client asincroni (Netty): con org.apache.http a DEBUG in config/application.properties il client Apache
 * scriverebbe nel log le risposte, cioè i valori letti.
 */
public class AwsConfigEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String ENABLED_PROPERTY = "pn.aws.config.enabled";
    private static final String SECRET_IDS_PROPERTY = "pn.aws.config.secret-ids";
    private static final String SECRETS_PROPERTY_SOURCE_NAME = "awsSecretsManager";

    private static final Region REGION = Region.EU_SOUTH_1;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final int MAX_PARAMETERS_PER_CALL = 10;
    private static final Pattern PARAMETER_PLACEHOLDER =
            Pattern.compile("\\$\\{(" + AwsParameterStorePropertySource.PARAMETER_NAME.pattern() + ")[:}]");

    private final Log log;

    public AwsConfigEnvironmentPostProcessor(DeferredLogFactory logFactory) {
        this.log = logFactory.getLog(AwsConfigEnvironmentPostProcessor.class);
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (Boolean.FALSE.equals(environment.getProperty(ENABLED_PROPERTY, Boolean.class, Boolean.TRUE))) {
            log.info("AWS config disabled: placeholders will use local values or fallbacks");
            return;
        }
        long start = System.nanoTime();
        AwsCredentialsProvider credentialsProvider = DefaultCredentialsProvider.create();
        try {
            credentialsProvider.resolveCredentials();
            List<String> secretIds = Binder.get(environment)
                    .bind(SECRET_IDS_PROPERTY, Bindable.listOf(String.class))
                    .orElse(List.of());
            MutablePropertySources propertySources = environment.getPropertySources();
            // Il client resta aperto per i parametri non citati nei file di properties, letti al primo utilizzo
            SsmAsyncClient ssmClient = SsmAsyncClient.builder().region(REGION).credentialsProvider(credentialsProvider).build();
            Map<String, Optional<String>> parameters = loadParameters(ssmClient, parameterNames(propertySources));
            Map<String, Object> secrets = loadSecrets(secretIds, credentialsProvider);
            addAfterSystemEnvironment(propertySources, new AwsParameterStorePropertySource(ssmClient, parameters));
            addAfterSystemEnvironment(propertySources, new MapPropertySource(SECRETS_PROPERTY_SOURCE_NAME, secrets));
            log.info("AWS config loaded in " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) + " ms");
        } catch (RuntimeException e) {
            log.warn("AWS config not loaded, placeholders will use local values or fallbacks: " + e.getMessage());
        }
    }

    @Override
    public int getOrder() {
        // dopo il caricamento di config/application*.properties, per poter leggere pn.aws.config.*
        return ConfigDataEnvironmentPostProcessor.ORDER + 1;
    }

    /** Attende la risposta AWS e rilancia l'eccezione originale al posto della CompletionException. */
    static <T> T join(CompletableFuture<T> response) {
        try {
            return response.join();
        } catch (CompletionException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            }
            throw e;
        }
    }

    // Nomi SSM nei placeholder dei valori effettivi: se la property è passata con -D, il valore del file
    // di properties non si usa e il suo parametro non si legge
    private static Set<String> parameterNames(PropertySources propertySources) {
        Set<String> keys = new HashSet<>();
        Set<String> names = new LinkedHashSet<>();
        for (PropertySource<?> source : propertySources) {
            if (source instanceof EnumerablePropertySource<?> enumerable) {
                for (String key : enumerable.getPropertyNames()) {
                    if (keys.add(key) && enumerable.getProperty(key) instanceof String value) {
                        Matcher placeholder = PARAMETER_PLACEHOLDER.matcher(value);
                        while (placeholder.find()) {
                            names.add(placeholder.group(1));
                        }
                    }
                }
            }
        }
        return names;
    }

    // GetParameters accetta al massimo 10 nomi: le chiamate partono tutte insieme e si attendono alla fine
    private Map<String, Optional<String>> loadParameters(SsmAsyncClient client, Set<String> names) {
        List<String> pending = new ArrayList<>(names);
        List<List<String>> groups = new ArrayList<>();
        for (int i = 0; i < pending.size(); i += MAX_PARAMETERS_PER_CALL) {
            groups.add(List.copyOf(pending.subList(i, Math.min(i + MAX_PARAMETERS_PER_CALL, pending.size()))));
        }
        List<CompletableFuture<GetParametersResponse>> responses = groups.stream()
                .map(group -> client.getParameters(r -> r.names(group).withDecryption(true)))
                .toList();
        Map<String, Optional<String>> values = new HashMap<>();
        int loaded = 0;
        for (int i = 0; i < groups.size(); i++) {
            try {
                GetParametersResponse response = join(responses.get(i));
                for (Parameter parameter : response.parameters()) {
                    values.put(parameter.name(), Optional.ofNullable(parameter.value()));
                    loaded++;
                }
                response.invalidParameters()
                        .forEach(name -> log.warn("AWS parameter " + name + " not found, fallback will be used"));
            } catch (RuntimeException e) {
                log.warn("AWS parameters " + groups.get(i) + " not loaded, fallbacks will be used: " + e.getMessage());
            }
            // anche i mancanti: non si richiedono di nuovo al primo utilizzo
            groups.get(i).forEach(name -> values.putIfAbsent(name, Optional.empty()));
        }
        log.info("AWS parameters: " + loaded + " of " + names.size() + " loaded with " + groups.size() + " calls");
        return values;
    }

    private Map<String, Object> loadSecrets(List<String> secretIds, AwsCredentialsProvider credentialsProvider) {
        Map<String, Object> values = new HashMap<>();
        if (secretIds.isEmpty()) {
            return values;
        }
        try (SecretsManagerAsyncClient client = SecretsManagerAsyncClient.builder()
                .region(REGION)
                .credentialsProvider(credentialsProvider)
                .build()) {
            for (String secretId : secretIds) {
                try {
                    String secret = join(client.getSecretValue(r -> r.secretId(secretId))).secretString();
                    log.info("AWS secret " + secretId + ": " + putSecretValues(values, secretId, secret) + " values loaded");
                } catch (RuntimeException e) {
                    log.warn("AWS secret " + secretId + " not loaded, fallbacks will be used: " + e.getMessage());
                }
            }
        }
        return values;
    }

    // Segreto JSON: una property per chiave. Segreto testuale: una property con il nome del segreto.
    private static int putSecretValues(Map<String, Object> values, String secretId, String secret) {
        if (secret == null) {
            return 0;
        }
        JsonNode json = parseJson(secret);
        if (json == null || !json.isObject()) {
            values.put(secretId, secret);
            return 1;
        }
        int loaded = 0;
        Iterator<Map.Entry<String, JsonNode>> fields = json.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            JsonNode value = field.getValue();
            if (!value.isNull()) {
                values.put(field.getKey(), value.isValueNode() ? value.asText() : value.toString());
                loaded++;
            }
        }
        return loaded;
    }

    private static JsonNode parseJson(String value) {
        try {
            return OBJECT_MAPPER.readTree(value);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    // Prima dei file di properties, dopo -D e variabili d'ambiente: un valore passato a mano vince su AWS
    private static void addAfterSystemEnvironment(MutablePropertySources propertySources, PropertySource<?> propertySource) {
        if (propertySources.contains(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME)) {
            propertySources.addAfter(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, propertySource);
        } else {
            propertySources.addFirst(propertySource);
        }
    }
}
