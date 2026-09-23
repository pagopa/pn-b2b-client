package it.pagopa.pn.cucumber.steps;

import io.cucumber.java.Before;
import io.cucumber.spring.ScenarioScope;
import it.pagopa.pn.cucumber.steps.common.InformalNotificationContext;
import it.pagopa.pn.cucumber.steps.common.LegalNotificationContext;
import it.pagopa.pn.cucumber.steps.common.MandateContext;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
@ScenarioScope
public class SendSharedContext {
    private InformalNotificationContext informalNotificationContext;
    private LegalNotificationContext legalNotificationContext;
    private MandateContext mandateContext;

    @Before
    public void resetSharedStepsContext() {
        informalNotificationContext = new InformalNotificationContext();
        legalNotificationContext = new LegalNotificationContext();
        mandateContext = new MandateContext();
    }
}
