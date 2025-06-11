package it.pagopa.pn.interop.cucumber.steps.m2m.common;


import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.enums.EntityIdType;


public class SharedSteps {

    @Then("{entityType} {word} restituito")
    @Then("lista di {entityType} {word} restituita")
    public void exsist(ICommonSteps commonSteps, String presence) {
        commonSteps.exsist(presence);
    }

    @Then("{entityType} viene restituito e combacia con il record creato")
    @Then("lista di {entityType} viene restituita e combacia con i record creati")
    public void exsistAndMatch(ICommonSteps commonSteps) {
        commonSteps.exsist("match");
    }

    @When("l'utente tenta di recuperare {entityType} con:")
    public void getBy(ICommonSteps commonSteps, DataTable table) {
       commonSteps.getBy(table.asMap());
    }

    @When("l'utente tenta di recuperare il record di {entityType} creato")
    public void getByFirstExpectedId(ICommonSteps commonSteps) {
        commonSteps.getByFirstExpectedId();
    }

    @When("l'utente tenta di recuperare la lista di {entityType}")
    public void getAll(ICommonSteps commonSteps) {
        commonSteps.getAll();
    }

    @When("l'utente tenta di recuperare {entityType} con un id {entityIdType}")
    public void getByIdType(ICommonSteps commonSteps, EntityIdType entityIdType) {
       commonSteps.getByIdType(entityIdType);
    }

    @Then("{entityType} è presente solo se lo status code è {int}")
    @Then("la lista di {entityType} è presente solo se lo status code è {int}")
    public void verifyByHttpStatus(ICommonSteps commonSteps, int expectedStatusCode) {
        commonSteps.verifyByHttpStatus(expectedStatusCode);
    }
}

