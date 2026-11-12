package it.pagopa.pn.cucumber.streamversion.hooks;

import io.cucumber.java.Scenario;
import it.pagopa.pn.cucumber.steps.pa.webhookVersions.StreamVersion;
import it.pagopa.pn.cucumber.streamversion.context.StreamVersionContext;

/**
 * Base degli hook di versione: ogni sottoclasse, in un proprio package hooks.vXX, dichiara l'hook @Before(order = 0).
 * L'ordine 0 lo fa eseguire prima degli hook con ordine predefinito, che trovano così la versione già valorizzata.
 * Questa classe non dichiara hook: Cucumber non consente di estendere classi che ne contengono.
 */
public abstract class StreamVersionHook {

    private final StreamVersionContext streamVersionContext;
    private final StreamVersion streamVersion;

    protected StreamVersionHook(StreamVersionContext streamVersionContext, StreamVersion streamVersion) {
        this.streamVersionContext = streamVersionContext;
        this.streamVersion = streamVersion;
    }

    protected void initializeStreamVersion(Scenario scenario) {
        streamVersionContext.initialize(streamVersion);
        scenario.log("Versione stream della suite: " + streamVersion);
    }
}
