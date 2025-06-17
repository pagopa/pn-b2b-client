package it.pagopa.pn.interop.cucumber.steps.m2m.common;


import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.common.enums.EntityIdType;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


public class SharedSteps {

    private static ApplicationContext context;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        SharedSteps.context = applicationContext;
    }

    @Then("{entityType} {word} restituito")
    @Then("lista di {entityType} {word} restituita")
    public void exsist(Pair<String, Class<? extends ICommonSteps>> stepDef, String presence) {
        resolveStep(stepDef).exsist(presence);
    }

    @Then("{entityType} viene restituito e combacia con il record creato")
    @Then("lista di {entityType} viene restituita e combacia con i record creati")
    public void exsistAndMatch(Pair<String, Class<? extends ICommonSteps>> stepDef) {
        resolveStep(stepDef).exsist("match");
    }

    @When("l'utente tenta di recuperare {entityType} con:")
    public void getBy(Pair<String, Class<? extends ICommonSteps>> stepDef, DataTable table) {
       resolveStep(stepDef).getBy(table.asMap());
    }

    @When("l'utente tenta di recuperare il record di {entityType} creato")
    public void getByFirstExpectedId(Pair<String, Class<? extends ICommonSteps>> stepDef) {
        resolveStep(stepDef).getByFirstExpectedId();
    }

    @When("l'utente tenta di recuperare la lista di {entityType}")
    public void getAll(Pair<String, Class<? extends ICommonSteps>> stepDef) {
        resolveStep(stepDef).getAll();
    }

    @When("l'utente tenta di recuperare {entityType} con un id {entityIdType}")
    public void getByIdType(Pair<String, Class<? extends ICommonSteps>> stepDef, EntityIdType entityIdType) {
       resolveStep(stepDef).getByIdType(entityIdType);
    }

    @Then("{entityType} è presente solo se lo status code è {int}")
    @Then("la lista di {entityType} è presente solo se lo status code è {int}")
    public void verifyByHttpStatus(Pair<String, Class<? extends ICommonSteps>> stepDef, int expectedStatusCode) {
        resolveStep(stepDef).verifyByHttpStatus(expectedStatusCode);
    }

    private ICommonSteps resolveStep(Pair<String, Class<? extends ICommonSteps>> stepDef) {
        return context.getBean(stepDef.getRight());
    }

}

