package it.pagopa.pn.interop.cucumber.steps.m2m.common;

import io.cucumber.java.Before;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.attribute.AttributeSteps;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.EserviceDescriptorSteps;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.EserviceSteps;
import lombok.Data;

import java.lang.reflect.Constructor;
import java.util.List;

@Data
public class StepBootstrapper {
    private static boolean initialized = false;

    private final SharedStepsContext sharedStepsContext;
    private final ClientTokenConfigurator clientTokenConfigurator;

    // Lista delle classi di step da istanziare
    private final List<Class<? extends ICommonSteps>> stepClasses = List.of(
            EserviceSteps.class,
            AttributeSteps.class,
            EserviceDescriptorSteps.class
    );

    @Before(order = Integer.MIN_VALUE)
    public void bootstrapSteps() {
        if (initialized) return;
        initialized = true;

        for (Class<? extends ICommonSteps> stepClass : stepClasses) {
            try {
                Constructor<? extends ICommonSteps> constructor = stepClass.getConstructor(
                        SharedStepsContext.class, ClientTokenConfigurator.class
                );
                constructor.newInstance(sharedStepsContext, clientTokenConfigurator);
            } catch (Exception e) {
                throw new RuntimeException("Errore durante l'inizializzazione di " + stepClass.getSimpleName(), e);
            }
        }
    }
}
