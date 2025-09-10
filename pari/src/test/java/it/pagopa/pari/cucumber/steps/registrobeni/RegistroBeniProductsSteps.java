package it.pagopa.pari.cucumber.steps.registrobeni;

import io.cucumber.java.en.Then;
import it.pagopa.pari.cucumber.utils.ApiClientContext;
import it.pagopa.pari.cucumber.utils.SharedCommonContext;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.ProductListDTO;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.ProductStatus;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RegistroBeniProductsSteps {
    private final ApiClientContext apiClientContext;
    private final SharedCommonContext sharedCommonContext;

    public RegistroBeniProductsSteps(ApiClientContext apiClientContext, SharedCommonContext sharedCommonContext) {
        this.apiClientContext = apiClientContext;
        this.sharedCommonContext = sharedCommonContext;
    }

    @Then("si verifica che la lista di prodotti caricati non sia nulla")
    public void verifyResponseProductsList() {
        ProductListDTO productListDTO = apiClientContext.getRegisterPortalOperationClient().getProducts(0, 10, null, null,
                null, null, null, null, null, sharedCommonContext.getUserData().getOrgId());
        assertNotNull(productListDTO);
        assertNotNull(productListDTO.getContent());
        assertFalse(productListDTO.getContent().isEmpty());
    }

    @Then("viene verificata la presenza di un prodotto escluso, se non presente viene aggiunto")
    public void getRejectedProductOrAdd() {
        ProductListDTO productListDTO = null;
        int i = 0;
        while (productListDTO == null || i < productListDTO.getTotalPages()) {
            productListDTO = apiClientContext.getRegisterPortalOperationClient().getProducts(0, 10, null, null, null, null,
                    null, null, ProductStatus.REJECTED, sharedCommonContext.getUserData().getOrgId());
            i++;
            if (!productListDTO.getContent().isEmpty()) {
                sharedCommonContext.setProductDTO(productListDTO.getContent());
                break;
            }
        }

        //TODO vanno richiamati i metodi per aggiungere un prodotto e rifiutarlo

    }




}
