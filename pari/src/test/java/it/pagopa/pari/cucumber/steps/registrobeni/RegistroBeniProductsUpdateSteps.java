package it.pagopa.pari.cucumber.steps.registrobeni;

import io.cucumber.java.en.When;
import it.pagopa.pari.cucumber.utils.ApiClientContext;
import it.pagopa.pari.cucumber.utils.SharedCommonContext;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.ProductsUpdateDTO;

import java.util.List;

public class RegistroBeniProductsUpdateSteps {
    private final ApiClientContext apiClientContext;
    private final SharedCommonContext sharedCommonContext;

    public RegistroBeniProductsUpdateSteps(ApiClientContext apiClientContext, SharedCommonContext sharedCommonContext) {
        this.apiClientContext = apiClientContext;
        this.sharedCommonContext = sharedCommonContext;
    }

    @When("viene contrassegnato un prodotto tra quelli nella lista")
    public void markProductAsSupervisioned() {
        ProductsUpdateDTO productsUpdateDTO = new ProductsUpdateDTO().gtinCodes(List.of()).motivation("");
        apiClientContext.getRegisterPortalOperationClient().updateProductStatusSupervisioned("", productsUpdateDTO);
    }


}
