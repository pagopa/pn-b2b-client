package it.pagopa.pn.interop.cucumber.steps;

import io.cucumber.java.Before;
import it.pagopa.pn.interop.cucumber.steps.utils.ClientCommonContext;
import it.pagopa.pn.interop.cucumber.steps.utils.EServicesCommonContext;
import it.pagopa.pn.interop.cucumber.steps.utils.PurposeCommonContext;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SharedStepsContext {

    private int testSeed = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
    private String tenantType;
    private String userToken;
    private ClientCommonContext clientCommonContext = new ClientCommonContext();
    private PurposeCommonContext purposeCommonContext = new PurposeCommonContext();
    private EServicesCommonContext eServicesCommonContext = new EServicesCommonContext();

    @Before
    public void resetSharedStepsContext() {
        testSeed = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
        clientCommonContext = new ClientCommonContext();
        purposeCommonContext = new PurposeCommonContext();
        eServicesCommonContext = new EServicesCommonContext();
    }

}
