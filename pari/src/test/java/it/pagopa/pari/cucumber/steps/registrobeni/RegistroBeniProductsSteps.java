package it.pagopa.pari.cucumber.steps.registrobeni;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pari.cucumber.utils.ApiClientContext;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.ProductListDTO;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RegistroBeniProductsSteps {
    private final ApiClientContext apiClientContext;
    private ProductListDTO productListDTO;

    public RegistroBeniProductsSteps(ApiClientContext apiClientContext) {
        this.apiClientContext = apiClientContext;
    }

    @When("vengono recuperati i prodotti precedentemente caricati")
    public void retrieveProducts() {
        productListDTO = apiClientContext.getRegisterPortalOperationClient().getProducts("", 0, 10, null, null,
                null, null, null, null);
    }

    @Then("si verifica che la lista di prodotti ritornata non sia nulla")
    public void verifyResponseProductsList() {
        assertNotNull(productListDTO);
        assertNotNull(productListDTO.getContent());
        assertFalse(productListDTO.getContent().isEmpty());
    }


}
