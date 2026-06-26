package it.pagopa.pn.cucumber.steps.pa.hooks;

import io.cucumber.java.Before;
import it.pagopa.pn.client.b2b.pa.service.impl.PnRaddAlternativeClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnRaddVpceAdapter;
import it.pagopa.pn.cucumber.steps.pa.RaddAltSteps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
@Slf4j
public class RaddHooks {

    private final ApplicationContext context;
    private final RaddAltSteps raddAltSteps;

    @Autowired
    public RaddHooks(ApplicationContext context, RaddAltSteps raddAltSteps) {
        this.context = context;
        this.raddAltSteps = raddAltSteps;
    }
    @Before("@useRaddVpce")
    public void useRaddVpce() {
        log.info("Using RADD VPCE adapter");

        raddAltSteps.setRaddClient(
                context.getBean(PnRaddVpceAdapter.class)
        );

    }

    @Before("@raddAlt")
    public void useRaddAlternative() {
        log.info("Using RADD Alternative client");

        raddAltSteps.setRaddClient(
                context.getBean(PnRaddAlternativeClientImpl.class)
        );
    }
}

