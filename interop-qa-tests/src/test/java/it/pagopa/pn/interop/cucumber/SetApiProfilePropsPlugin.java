package it.pagopa.pn.interop.cucumber;

import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.EventPublisher;

import java.util.*;

public class SetApiProfilePropsPlugin implements ConcurrentEventListener {

    private static final Set<String> ALLOWED_KEYS = Set.of(
            "api.m2m.version",
            "api.mode",
            "api.set"
    );

    public SetApiProfilePropsPlugin(String config) {
        Map<String, String> props = parseAndValidate(config);
        props.forEach(System::setProperty);
    }

    private Map<String, String> parseAndValidate(String config) {

        if (config == null || config.isBlank()) {
            throw new IllegalArgumentException(
                    "Plugin configuration string is null or empty"
            );
        }

        Map<String, String> result = new HashMap<>();

        String[] pairs = config.split(";");
        for (String pair : pairs) {

            if (pair.isBlank()) continue;

            String[] kv = pair.split("=", 2);

            if (kv.length != 2) {
                throw new IllegalArgumentException(
                        "Invalid key=value pair: '" + pair + "'"
                );
            }

            String key = kv[0].trim();
            String value = kv[1].trim();

            if (key.isEmpty() || value.isEmpty()) {
                throw new IllegalArgumentException(
                        "Empty key or value in pair: '" + pair + "'"
                );
            }

            if (!ALLOWED_KEYS.contains(key)) {
                throw new IllegalArgumentException(
                        "Unsupported configuration key: '" + key + "'"
                );
            }

            if (result.containsKey(key)) {
                throw new IllegalArgumentException(
                        "Duplicate configuration key: '" + key + "'"
                );
            }

            result.put(key, value);
        }

        for (String required : ALLOWED_KEYS) {
            if (!result.containsKey(required)) {
                throw new IllegalArgumentException(
                        "Missing required configuration key: '" + required + "'"
                );
            }
        }

        return result;
    }

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        // niente
    }
}