package it.pagopa.pari.cucumber.steps.registrobeni;

import io.cucumber.java.en.When;
import it.pagopa.pari.cucumber.utils.ApiClientContext;
import it.pagopa.pari.cucumber.utils.SharedCommonContext;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.ProductDTO;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.ProductListDTO;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.ProductStatus;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.ProductsUpdateDTO;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class RegistroBeniProductsUpdateSteps {
    private static final String REJECT_PRODUCT_MOTIVATION = "Reject Product Motivation";
    private static final String SUPERVISED_PRODUCT_MOTIVATION = "Supervised Product Motivation";
    private final ApiClientContext apiClientContext;
    private final SharedCommonContext sharedCommonContext;

    public RegistroBeniProductsUpdateSteps(ApiClientContext apiClientContext, SharedCommonContext sharedCommonContext) {
        this.apiClientContext = apiClientContext;
        this.sharedCommonContext = sharedCommonContext;
    }

    @When("viene contrassegnato il prodotto appena aggiunto")
    public void markProductAsSupervisioned() {
        List<String> gtinCodes = sharedCommonContext.getLastProductsUploaded().stream().map(ProductDTO::getGtinCode).toList();
        for (String gtinCode : gtinCodes) {
            ProductListDTO productListDTO = apiClientContext.getRegisterPortalOperationClient().getProducts(0, 10, null, null, null, gtinCode, null, null, null, sharedCommonContext.getUserData().getOrgId());
            Assertions.assertFalse(productListDTO.getContent().stream().anyMatch(item -> item.getStatus() != ProductStatus.UPLOADED));
        }
        ProductsUpdateDTO productsUpdateDTO = new ProductsUpdateDTO().gtinCodes(gtinCodes).currentStatus(ProductStatus.UPLOADED).motivation(SUPERVISED_PRODUCT_MOTIVATION);
        apiClientContext.getRegisterPortalOperationClient().updateProductStatusSupervised(productsUpdateDTO);
    }

    @When("viene escluso un prodotto tra quelli nella lista")
    public void markProductAsRejected() {
        ProductsUpdateDTO productsUpdateDTO = new ProductsUpdateDTO().gtinCodes(List.of()).motivation(REJECT_PRODUCT_MOTIVATION);
        apiClientContext.getRegisterPortalOperationClient().updateProductStatusRejected(productsUpdateDTO);
    }


}
