package it.pagopa.pn.cucumber.steps.informalNotification.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class InformalCommunicationAdapterResolver {

    private final Map<InformalApiVersion, InformalCommunicationAdapter> adapters = new EnumMap<>(InformalApiVersion.class);
    private final InformalApiVersion defaultVersion;

    @Autowired
    public InformalCommunicationAdapterResolver(List<InformalCommunicationAdapter> adapters,
                                                @Value("${pn.informal.api-version:V1}") String defaultVersion) {
        adapters.forEach(adapter -> this.adapters.put(adapter.version(), adapter));
        this.defaultVersion = InformalApiVersion.valueOf(defaultVersion.trim().toUpperCase());
    }

    public InformalCommunicationAdapter current() {
        return get(defaultVersion);
    }

    public InformalCommunicationAdapter get(InformalApiVersion version) {
        InformalCommunicationAdapter adapter = adapters.get(version);
        if (adapter == null) {
            throw new IllegalStateException("Nessun adapter registrato per la versione " + version);
        }
        return adapter;
    }
}
