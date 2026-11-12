package it.pagopa.pn.cucumber.streamversion.hooks.v23;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import it.pagopa.pn.cucumber.steps.pa.webhookVersions.StreamVersion;
import it.pagopa.pn.cucumber.streamversion.context.StreamVersionContext;
import it.pagopa.pn.cucumber.streamversion.hooks.StreamVersionHook;

public class StreamVersionV23Hook extends StreamVersionHook {

    public StreamVersionV23Hook(StreamVersionContext streamVersionContext) {
        super(streamVersionContext, StreamVersion.V23);
    }

    @Before(order = 0)
    public void initializeStreamVersion(Scenario scenario) {
        super.initializeStreamVersion(scenario);
    }
}
