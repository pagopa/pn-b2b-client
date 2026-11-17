package it.pagopa.pn.cucumber.streamversion.hooks.v26;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import it.pagopa.pn.cucumber.steps.pa.webhookVersions.StreamVersion;
import it.pagopa.pn.cucumber.streamversion.context.StreamVersionContext;
import it.pagopa.pn.cucumber.streamversion.hooks.StreamVersionHook;

public class StreamVersionV26Hook extends StreamVersionHook {

    public StreamVersionV26Hook(StreamVersionContext streamVersionContext) {
        super(streamVersionContext, StreamVersion.V26);
    }

    @Before(order = 0)
    public void initializeStreamVersion(Scenario scenario) {
        super.initializeStreamVersion(scenario);
    }
}
