package it.pagopa.pn.interop.cucumber;

import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.TestCaseFinished;
import io.cucumber.plugin.event.TestCaseStarted;

import java.util.HashMap;
import java.util.Map;

public class SetApiProfilePropsPlugin implements ConcurrentEventListener {
    private final ApiProfileConfig config;

    public SetApiProfilePropsPlugin(String configString) {
        this.config = parseConfig(configString);
    }

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestCaseStarted.class, event -> ApiProfileContext.set(config));
        publisher.registerHandlerFor(TestCaseFinished.class, event -> ApiProfileContext.clear());
    }

    private ApiProfileConfig parseConfig(String configString) {
        Map<String, String> map = new HashMap<>();
        for (String entry : configString.split(";")) {
            String[] kv = entry.split("=", 2);
            if (kv.length == 2) {
                map.put(kv[0].trim(), kv[1].trim());
            }
        }
        // Validazione chiavi supportate
        String apiMode = map.get("api.mode");
        String apiM2mVersion = map.get("api.m2m.version");
        String apiBffVersion = map.get("api.bff.version");
        String apiSet = map.get("api.set");
        if (apiMode == null || apiM2mVersion == null || apiBffVersion == null || apiSet == null) {
            throw new IllegalArgumentException("Tutte le chiavi api.mode, api.m2m.version, api.bff.version, api.set sono obbligatorie");
        }
        return new ApiProfileConfig(apiMode, apiM2mVersion, apiBffVersion, apiSet);
    }
}