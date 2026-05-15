package it.pagopa.pn.interop.cucumber.config.concurrency.writer;

import it.pagopa.pn.interop.cucumber.config.concurrency.ScenariosConcurrencyReporter;

public interface ScenariosConcurrencyReportWriter {

    void write(ScenariosConcurrencyReporter.TimelineModel model);

    void write(ScenariosConcurrencyReporter.OverlapModel model);

}
