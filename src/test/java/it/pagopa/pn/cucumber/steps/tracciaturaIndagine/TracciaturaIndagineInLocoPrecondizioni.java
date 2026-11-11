package it.pagopa.pn.cucumber.steps.tracciaturaIndagine;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assumptions;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Salta gli scenari della suite che hanno uno dei tag indicati in {@code pn.paper-source.skip-tags}
 * (variabile d'ambiente {@code PN_PAPER_SOURCE_SKIP_TAGS}), separati da virgola, es. "@registroImprese,@vasBonarie".
 * Serve dove il runner non accetta filtri sui tag (es. CodeBuild, che sceglie solo il runner): per default non salta nulla.
 */
@Slf4j
public class TracciaturaIndagineInLocoPrecondizioni {

    @Value("${pn.paper-source.skip-tags:}")
    private String skipTags;

    @Before(value = "@tracciaturaIndagineInLoco", order = 0)
    public void skipScenariEsclusi(Scenario scenario) {
        Set<String> matching = tagEsclusi(skipTags, scenario.getSourceTagNames());
        if (!matching.isEmpty()) {
            log.info("Scenario '{}' saltato per i tag {} (pn.paper-source.skip-tags)", scenario.getName(), matching);
        }
        Assumptions.assumeTrue(matching.isEmpty(), () -> "Scenario saltato per i tag " + matching);
    }

    /** Tag dello scenario presenti nell'elenco da escludere (con o senza '@', separati da virgola o spazi). */
    static Set<String> tagEsclusi(String skipTags, Collection<String> scenarioTags) {
        if (skipTags == null || skipTags.isBlank()) {
            return Set.of();
        }
        Set<String> excluded = Arrays.stream(skipTags.split("[,\\s]+"))
                .filter(tag -> !tag.isEmpty())
                .map(tag -> tag.startsWith("@") ? tag : "@" + tag)
                .collect(Collectors.toSet());
        return scenarioTags.stream().filter(excluded::contains).collect(Collectors.toSet());
    }
}
