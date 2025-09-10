package it.pagopa.pari.cucumber.steps.registrobeni;

import io.cucumber.java.en.When;
import it.pagopa.pari.cucumber.utils.ApiClientContext;
import it.pagopa.pari.cucumber.utils.SharedCommonContext;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.ProductDTO;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.ProductsUpdateDTO;

import java.util.List;

public class RegistroBeniProductsUpdateSteps {
    private static final String REJECT_PRODUCT_MOTIVATION = "Reject Product Motivation";
    private final ApiClientContext apiClientContext;
    private final SharedCommonContext sharedCommonContext;

    public RegistroBeniProductsUpdateSteps(ApiClientContext apiClientContext, SharedCommonContext sharedCommonContext) {
        this.apiClientContext = apiClientContext;
        this.sharedCommonContext = sharedCommonContext;
    }

    @When("viene contrassegnato il prodotto appena aggiunto")
    public void markProductAsSupervisioned() {
        List<String> gtinCodes = sharedCommonContext.getProductDTO().stream().map(ProductDTO::getGtinCode).toList();
        ProductsUpdateDTO productsUpdateDTO = new ProductsUpdateDTO().gtinCodes(gtinCodes).motivation("");
        apiClientContext.getRegisterPortalOperationClient().updateProductStatusSupervised(productsUpdateDTO);
    }

    @When("viene escluso un prodotto tra quelli nella lista")
    public void markProductAsRejected() {
        ProductsUpdateDTO productsUpdateDTO = new ProductsUpdateDTO().gtinCodes(List.of()).motivation(REJECT_PRODUCT_MOTIVATION);
        apiClientContext.getRegisterPortalOperationClient().updateProductStatusRejected(productsUpdateDTO);
    }


}
