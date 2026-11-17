package it.pagopa.pn.cucumber.steps.visualtest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Single source of the data of a document: the same values build the notification sent in the scenario
 * and the Template Engine request that renders the golden master, so the two PDFs share the same layout.
 *
 * <p>The {@code templateEngine} section can reference the other sections with {@code ${notification.<key>}},
 * {@code ${recipient.name}} and {@code ${recipient.<key>}}.</p>
 */
public final class VisualFixture {

    static final String FIXTURE_ROOT = "it/pagopa/pn/cucumber/visualTest/fixtures/";
    private static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{([^}]+)}");
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

    private final Map<String, String> notification;
    private final String recipientName;
    private final Map<String, String> recipient;
    private final JsonNode templateEngine;

    private VisualFixture(Map<String, String> notification, String recipientName,
                          Map<String, String> recipient, JsonNode templateEngine) {
        this.notification = notification;
        this.recipientName = recipientName;
        this.recipient = recipient;
        this.templateEngine = templateEngine;
    }

    public static VisualFixture load(VisualDocument document) {
        String path = FIXTURE_ROOT + document.folder() + ".json";
        try (InputStream is = new ClassPathResource(path).getInputStream()) {
            JsonNode root = MAPPER.readTree(is);
            Map<String, String> notification = toMap(root.path("notification"));
            JsonNode recipientNode = root.path("recipient");
            Map<String, String> recipient = toMap(recipientNode.path("data"));
            String recipientName = recipientNode.path("name").asText(null);
            JsonNode resolved = MAPPER.readTree(resolve(root.path("templateEngine").toString(),
                    placeholders(notification, recipientName, recipient)));
            return new VisualFixture(notification, recipientName, recipient, resolved);
        } catch (IOException e) {
            throw new UncheckedIOException("Fixture VRT non leggibile: " + path, e);
        }
    }

    /** Data table of the step "viene generata una nuova notifica". */
    public Map<String, String> notificationData() {
        return new HashMap<>(notification);
    }

    /** Name accepted by the {@code {destinatario}} parameter type, null for documents without a notification. */
    public String recipientName() {
        return recipientName;
    }

    /** Data table of the step "destinatario ... e:". */
    public Map<String, String> recipientData() {
        return new HashMap<>(recipient);
    }

    public <T> T templateEngineRequest(Class<T> requestType) {
        try {
            return MAPPER.treeToValue(templateEngine, requestType);
        } catch (IOException e) {
            throw new UncheckedIOException("Sezione templateEngine non compatibile con " + requestType.getSimpleName(), e);
        }
    }

    private static Map<String, String> toMap(JsonNode node) {
        if (node.isMissingNode() || node.isNull()) {
            return new LinkedHashMap<>();
        }
        return MAPPER.convertValue(node, new TypeReference<LinkedHashMap<String, String>>() { });
    }

    private static Map<String, String> placeholders(Map<String, String> notification, String recipientName,
                                                    Map<String, String> recipient) {
        Map<String, String> values = new HashMap<>();
        notification.forEach((k, v) -> values.put("notification." + k, v));
        recipient.forEach((k, v) -> values.put("recipient." + k, v));
        if (recipientName != null) {
            values.put("recipient.name", recipientName);
        }
        return values;
    }

    private static String resolve(String json, Map<String, String> values) {
        Matcher matcher = PLACEHOLDER.matcher(json);
        StringBuilder out = new StringBuilder();
        while (matcher.find()) {
            String value = values.get(matcher.group(1));
            if (value == null) {
                throw new IllegalStateException("Placeholder senza valore nella fixture: " + matcher.group());
            }
            String escaped = MAPPER.valueToTree(value).toString();
            matcher.appendReplacement(out, Matcher.quoteReplacement(escaped.substring(1, escaped.length() - 1)));
        }
        matcher.appendTail(out);
        return out.toString();
    }
}
