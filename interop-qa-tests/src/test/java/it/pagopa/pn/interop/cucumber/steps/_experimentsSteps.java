package it.pagopa.pn.interop.cucumber.steps;

import io.cucumber.java.en.And;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;

@Getter
@Setter
@Slf4j
@AllArgsConstructor
public class _experimentsSteps {
    private _experimentsScenariosCounter counter;

    @And("gira scenario libero")
    public void giraScenarioLibero() throws InterruptedException {
        System.out.println("Scenari liberi in esecuzione concorrente: " + counter.incFreeScenarios());
        Thread.sleep(RandomUtils.insecure().randomInt(5000, 50000));
        this.counter.decFreeScenarios();
    }

    @And("gira scenario vincolato")
    public void giraScenarioVincolato() throws InterruptedException {
        //System.out.println("Scenari vincolati in esecuzione concorrente: " + counter.incLockedScenarios());
        System.out.println("Scenario vincolato: inizio");
        Thread.sleep(RandomUtils.insecure().randomInt(5000, 50000));
        this.counter.decLockedScenarios();
        System.out.println("Scenario vincolato: fine");
    }

    @And("gira scenario vincolato 2")
    public void giraScenarioVincolato2() throws InterruptedException {
        //System.out.println("Scenari vincolati in esecuzione concorrente: " + counter.incLockedScenarios());
        System.out.println("Scenario vincolato SECONDO TIPO: inizio");
        Thread.sleep(RandomUtils.insecure().randomInt(5000, 50000));
        this.counter.decLockedScenarios();
        System.out.println("Scenario vincolato SECONDO TIPO: fine");
    }
}