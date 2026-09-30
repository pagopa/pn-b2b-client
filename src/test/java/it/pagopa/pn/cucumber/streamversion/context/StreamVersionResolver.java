package it.pagopa.pn.cucumber.streamversion.context;

import it.pagopa.pn.cucumber.steps.pa.webhookVersions.StreamVersion;

import java.util.Arrays;

import static it.pagopa.pn.client.b2b.pa.domain.Costanti.MOST_RECENT;

public final class StreamVersionResolver {

    //TODO: modificare questo valore ogni volta che viene aggiunta una versione più recente
    public static final StreamVersion MOST_RECENT_VERSION = StreamVersion.V29;

    private StreamVersionResolver() {
    }

    /**
     * La versione esplicita prevale su quella della suite: i casi di compatibilità tra versioni
     * devono poter usare, nello stesso scenario, una versione diversa da quella della suite.
     */
    public static StreamVersion resolve(String explicitVersion, StreamVersionContext context) {
        if (explicitVersion == null) {
            return context.getRequiredStreamVersion();
        }
        return parse(explicitVersion);
    }

    public static StreamVersion parse(String version) {
        if (version == null || version.isBlank()) {
            throw new IllegalArgumentException("Versione dello stream non valorizzata");
        }
        String normalized = version.trim();
        if (normalized.equalsIgnoreCase(MOST_RECENT)) {
            return MOST_RECENT_VERSION;
        }
        try {
            return StreamVersion.valueOf(normalized.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format(
                    "Versione dello stream non valida: '%s'. Valori ammessi: %s o '%s'",
                    version, Arrays.toString(StreamVersion.values()), MOST_RECENT), e);
        }
    }
}
