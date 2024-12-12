package it.pagopa.pn.interop.cucumber.steps.utils;

import io.cucumber.java.Before;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.concurrent.ThreadLocalRandom;

@Getter
public class SharedStepsContext {

    private int testSeed;

    @Before
    public void resetState() {
        testSeed = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
    }

}
