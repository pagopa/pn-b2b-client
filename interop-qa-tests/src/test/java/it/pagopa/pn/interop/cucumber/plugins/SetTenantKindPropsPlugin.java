package it.pagopa.pn.interop.cucumber.plugins;

import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.TestCaseStarted;

public class SetTenantKindPropsPlugin implements ConcurrentEventListener {

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestCaseStarted.class, event -> System.setProperty("suite.AdeguamentoAnalisiRischioTest", "true"));
    }

}