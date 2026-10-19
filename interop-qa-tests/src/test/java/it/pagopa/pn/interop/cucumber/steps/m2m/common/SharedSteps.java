package it.pagopa.pn.interop.cucumber.steps.m2m.common;


import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.enums.EntityIdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;


public class SharedSteps {

    private static ApplicationContext context;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        SharedSteps.context = applicationContext;
    }

    @Then("{entityType} {word} restituito")
    @Then("lista di {entityType} {word} restituita")
    public void exsist(Class<? extends ICommonSteps> stepClass, String presence) {
        resolveStep(stepClass).exsist(presence);
    }

    @Then("{entityType} viene restituito e combacia con il record creato")
    @Then("lista di {entityType} viene restituita e combacia con i record creati")
    public void exsistAndMatch(Class<? extends ICommonSteps> stepClass) {
        resolveStep(stepClass).exsist("match");
    }

    @When("l'utente tenta di recuperare {entityType} con:")
    public void getBy(Class<? extends ICommonSteps> stepClass, DataTable table) {
        resolveStep(stepClass).getBy(table.asMap());
    }

    @When("l'utente tenta di recuperare il record di {entityType} creato")
    public void getByFirstExpectedId(Class<? extends ICommonSteps> stepClass) {
        resolveStep(stepClass).getByFirstExpectedId();
    }

    @When("l'utente tenta di recuperare la lista completa di {entityType}")
    @When("l'utente tenta di recuperare la lista di {entityType}")
    public void getAll(Class<? extends ICommonSteps> stepClass) {
        resolveStep(stepClass).getAll();
    }

    /**
     * Recupera una specifica pagina della lista entity type.
     *
     * @param pageIndex numero della pagina da recuperare; è da intendersi come pagina,
     *                 non come offset, e parte da 1
     * @param pageSize numero massimo di elementi da includere nella pagina richiesta
     */

    @When("l'utente tenta di recuperare la pagina {int} della lista di {entityType} con un limite di {int} elementi")
    public void getCertifiedDiscreteAttributesPage(int pageIndex, Class<? extends ICommonSteps> stepClass, int pageSize) {
        resolveStep(stepClass).getPage(pageIndex, pageSize);
    }

    @When("l'utente tenta di recuperare {entityType} con un id {entityIdType}")
    public void getByIdType(Class<? extends ICommonSteps> stepClass, EntityIdType entityIdType) {
        resolveStep(stepClass).getByIdType(entityIdType);
    }

    @Then("{entityType} è presente solo se lo status code è {int}")
    @Then("la lista di {entityType} è presente solo se lo status code è {int}")
    public void verifyByHttpStatus(Class<? extends ICommonSteps> stepClass, int expectedStatusCode) {
        resolveStep(stepClass).verifyByHttpStatus(expectedStatusCode);
    }

    private ICommonSteps resolveStep(Class<? extends ICommonSteps> stepClass) {
        return context.getBean(stepClass);
    }

}

