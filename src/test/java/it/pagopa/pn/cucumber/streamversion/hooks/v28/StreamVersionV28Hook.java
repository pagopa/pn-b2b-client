package it.pagopa.pn.cucumber.streamversion.hooks.v28;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import it.pagopa.pn.cucumber.steps.pa.webhookVersions.StreamVersion;
import it.pagopa.pn.cucumber.streamversion.context.StreamVersionContext;
import it.pagopa.pn.cucumber.streamversion.hooks.StreamVersionHook;

public class StreamVersionV28Hook extends StreamVersionHook {

    public StreamVersionV28Hook(StreamVersionContext streamVersionContext) {
        super(streamVersionContext, StreamVersion.V28);
    }

    @Before(order = 0)
    public void initializeStreamVersion(Scenario scenario) {
        super.initializeStreamVersion(scenario);
    }
}
