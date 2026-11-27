package it.pagopa.pn.client.b2b.pa.config.aws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
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
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.SdkEventLoopGroup;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerAsyncClient;
import software.amazon.awssdk.services.ssm.SsmAsyncClient;
import software.amazon.awssdk.services.ssm.model.GetParametersResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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
 * I valori si leggono una volta all'avvio e restano in memoria per tutta la run; i client si chiudono subito dopo.
 * Client asincroni (Netty): con org.apache.http a DEBUG in config/application.properties il client Apache
 * scriverebbe nel log le risposte, cioè i valori letti.
 */
public class AwsConfigEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String ENABLED_PROPERTY = "pn.aws.config.enabled";
    private static final String SECRET_IDS_PROPERTY = "pn.aws.config.secret-ids";
    private static final String SECRETS_PROPERTY_SOURCE_NAME = "awsSecretsManager";
    private static final String PARAMETERS_PROPERTY_SOURCE_NAME = "awsParameterStore";

    private static final Region REGION = Region.EU_SOUTH_1;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final int MAX_PARAMETERS_PER_CALL = 10;
    // Nome nel placeholder e tipo di fallback: ${nome:fallback}, ${nome:} (fallback vuoto) oppure ${nome}
    private static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{([^${}:]+)(:}|:|})");
    // I parametri SSM si indicano con il nome completo, che inizia con /
    private static final Pattern PARAMETER_NAME = Pattern.compile("/[A-Za-z0-9_./-]+");

    private final Log log;

    /** Creato da Spring Boot, che passa il log differito: i messaggi escono quando il logging è configurato. */
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
        List<String> secretIds = Binder.get(environment)
                .bind(SECRET_IDS_PROPERTY, Bindable.listOf(String.class))
                .orElse(List.of());
        MutablePropertySources propertySources = environment.getPropertySources();
        Set<Placeholder> placeholders = findPlaceholders(propertySources);
        // Event loop creato qui e chiuso senza attesa: se lo gestisce l'SDK, ogni client in chiusura aspetta 2 s
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(2, new DefaultThreadFactory("aws-config", true));
        try (DefaultCredentialsProvider credentialsProvider = DefaultCredentialsProvider.builder().build();
             SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
                     .eventLoopGroup(SdkEventLoopGroup.create(eventLoopGroup))
                     .build()) {
            credentialsProvider.resolveCredentials();
            Map<String, Object> parameters = loadParameters(parameterNames(placeholders), credentialsProvider, httpClient);
            Map<String, Object> secrets = loadSecrets(secretIds, credentialsProvider, httpClient);
            warnMissingSecretKeys(placeholders, propertySources, secrets);
            addAfterSystemEnvironment(propertySources, new MapPropertySource(PARAMETERS_PROPERTY_SOURCE_NAME, parameters));
            addAfterSystemEnvironment(propertySources, new MapPropertySource(SECRETS_PROPERTY_SOURCE_NAME, secrets));
            log.info("AWS config loaded in " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) + " ms");
        } catch (SdkException e) {
            log.warn("AWS config not loaded, placeholders will use local values or fallbacks: " + e.getMessage());
        } finally {
            eventLoopGroup.shutdownGracefully(0, 0, TimeUnit.SECONDS);
        }
    }

    @Override
    public int getOrder() {
        // dopo il caricamento di config/application*.properties, per poter leggere pn.aws.config.*
        return ConfigDataEnvironmentPostProcessor.ORDER + 1;
    }

    private record Placeholder(String name, boolean emptyFallback) {
    }

    // Placeholder dei valori effettivi: se una property è passata con -D, il valore del file
    // di properties non si usa e i suoi placeholder non contano
    private static Set<Placeholder> findPlaceholders(PropertySources propertySources) {
        Set<String> keys = new HashSet<>();
        Set<Placeholder> placeholders = new LinkedHashSet<>();
        for (PropertySource<?> source : propertySources) {
            if (source instanceof EnumerablePropertySource<?> enumerable) {
                for (String key : enumerable.getPropertyNames()) {
                    if (keys.add(key) && enumerable.getProperty(key) instanceof String value) {
                        Matcher matcher = PLACEHOLDER.matcher(value);
                        while (matcher.find()) {
                            placeholders.add(new Placeholder(matcher.group(1), ":}".equals(matcher.group(2))));
                        }
                    }
                }
            }
        }
        return placeholders;
    }

    private static List<String> parameterNames(Set<Placeholder> placeholders) {
        return placeholders.stream()
                .map(Placeholder::name)
                .filter(name -> PARAMETER_NAME.matcher(name).matches())
                .distinct()
                .toList();
    }

    // GetParameters accetta al massimo 10 nomi: le chiamate partono tutte insieme e si attendono alla fine
    private Map<String, Object> loadParameters(List<String> names, AwsCredentialsProvider credentialsProvider,
                                               SdkAsyncHttpClient httpClient) {
        Map<String, Object> values = new HashMap<>();
        if (names.isEmpty()) {
            return values;
        }
        List<List<String>> groups = new ArrayList<>();
        for (int i = 0; i < names.size(); i += MAX_PARAMETERS_PER_CALL) {
            groups.add(names.subList(i, Math.min(i + MAX_PARAMETERS_PER_CALL, names.size())));
        }
        try (SsmAsyncClient client = SsmAsyncClient.builder()
                .region(REGION)
                .credentialsProvider(credentialsProvider)
                .httpClient(httpClient)
                .build()) {
            List<CompletableFuture<GetParametersResponse>> responses = groups.stream()
                    .map(group -> client.getParameters(r -> r.names(group).withDecryption(true)))
                    .toList();
            for (int i = 0; i < groups.size(); i++) {
                try {
                    GetParametersResponse response = join(responses.get(i));
                    response.parameters().forEach(parameter -> values.put(parameter.name(), parameter.value()));
                    response.invalidParameters()
                            .forEach(name -> log.warn("AWS parameter " + name + " not found, fallback will be used"));
                } catch (SdkException e) {
                    log.warn("AWS parameters " + groups.get(i) + " not loaded, fallbacks will be used: " + e.getMessage());
                }
            }
        }
        log.info("AWS parameters: " + values.size() + " of " + names.size() + " loaded with " + groups.size() + " calls");
        return values;
    }

    private Map<String, Object> loadSecrets(List<String> secretIds, AwsCredentialsProvider credentialsProvider,
                                            SdkAsyncHttpClient httpClient) {
        Map<String, Object> values = new HashMap<>();
        if (secretIds.isEmpty()) {
            return values;
        }
        try (SecretsManagerAsyncClient client = SecretsManagerAsyncClient.builder()
                .region(REGION)
                .credentialsProvider(credentialsProvider)
                .httpClient(httpClient)
                .build()) {
            for (String secretId : secretIds) {
                try {
                    String secret = join(client.getSecretValue(r -> r.secretId(secretId))).secretString();
                    log.info("AWS secret " + secretId + ": " + putSecretValues(values, secretId, secret) + " values loaded");
                } catch (SdkException e) {
                    log.warn("AWS secret " + secretId + " not loaded, fallbacks will be used: " + e.getMessage());
                }
            }
        }
        return values;
    }

    // Chiavi con fallback vuoto che né le property né i segreti letti forniscono: la property resterebbe vuota.
    // Se il segreto non è stato letto l'avviso c'è già.
    private void warnMissingSecretKeys(Set<Placeholder> placeholders, PropertySources propertySources,
                                       Map<String, Object> secrets) {
        if (secrets.isEmpty()) {
            return;
        }
        List<String> missing = placeholders.stream()
                .filter(Placeholder::emptyFallback)
                .map(Placeholder::name)
                .filter(name -> !PARAMETER_NAME.matcher(name).matches() && !secrets.containsKey(name))
                .filter(name -> propertySources.stream().noneMatch(source -> source.containsProperty(name)))
                .distinct()
                .sorted()
                .toList();
        if (!missing.isEmpty()) {
            log.warn("AWS secret keys not found, empty fallbacks will be used: " + missing);
        }
    }

    /** Attende la risposta AWS e rilancia l'eccezione originale al posto della CompletionException. */
    private static <T> T join(CompletableFuture<T> response) {
        try {
            return response.join();
        } catch (CompletionException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            }
            throw e;
        }
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
