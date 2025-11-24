package it.pagopa.pari.cucumber.steps.registrobeni;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pari.cucumber.utils.ApiClientContext;
import it.pagopa.pari.cucumber.utils.SharedCommonContext;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.ProductDTO;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.ProductListDTO;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.ProductStatus;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.ProductsUpdateDTO;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.RegisterUploadResponseDTO;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.UpdateResponseDTO;
import it.pagopa.pari.registrobeni.domain.StatusChangeChronology;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegistroBeniProductsUpdateSteps {
    private static final String PRODUCT_MOTIVATION = "Motivation";
    private final ApiClientContext apiClientContext;
    private final SharedCommonContext sharedCommonContext;

    private UpdateResponseDTO updateResponseDTO;
    private HttpStatus httpStatus;

    public RegistroBeniProductsUpdateSteps(ApiClientContext apiClientContext, SharedCommonContext sharedCommonContext) {
        this.apiClientContext = apiClientContext;
        this.sharedCommonContext = sharedCommonContext;
    }

    @Then("si verifica che l'operazione di aggiornamento ritorni i seguenti valori:")
    public void verifyResponse(DataTable dataTable) {
        Map<String, String> expectedResults = dataTable.asMap();
        assertEquals(expectedResults.get("errorKey"), Optional.ofNullable(updateResponseDTO.getErrorKey()).map(UpdateResponseDTO.ErrorKeyEnum::getValue).orElse(null), "Mismatch on errorKey!");
        assertEquals(expectedResults.get("status"), Optional.ofNullable(updateResponseDTO.getStatus()).map(UpdateResponseDTO.StatusEnum::getValue).orElse(null), "Mismatch on status field!");
    }

    @When("viene contrassegnato il prodotto appena aggiunto")
    public void markProductAsSupervisioned() {
        try {
            searchAndmarkProduct(productsUpdateDTO -> apiClientContext.getRegisterPortalOperationClient().updateProductStatusSupervised(productsUpdateDTO));
        } catch (HttpStatusCodeException ex) {
            httpStatus = ex.getStatusCode();
        }
    }

    @When("viene contrassegnato il prodotto con codice eprel: {string}")
    public void superviseByEprelCode(String eprelCode) {
        ProductListDTO productListDTO = getProductByEprelCode(eprelCode, null);
        ProductDTO productFound = productListDTO.getContent().get(0);
        ProductsUpdateDTO productsUpdateDTO = new ProductsUpdateDTO().gtinCodes(List.of(productFound.getGtinCode())).currentStatus(productFound.getStatus()).motivation(PRODUCT_MOTIVATION);
        apiClientContext.getRegisterPortalOperationClient().updateProductStatusSupervised(productsUpdateDTO);
    }

    @When("vengono esclusi i prodotti appena aggiunti")
    @When("viene escluso il prodotto appena aggiunto")
    public void markProductAsRejected() {
        try {
            searchAndmarkProduct(productsUpdateDTO -> apiClientContext.getRegisterPortalOperationClient().updateProductStatusRejected(productsUpdateDTO));
        } catch (HttpStatusCodeException ex) {
            httpStatus = ex.getStatusCode();
        }
    }

    @When("viene escluso il prodotto con codice eprel: {string}")
    public void rejectByEprelCode(String eprelCode) {
        ProductListDTO productListDTO = getProductByEprelCode(eprelCode, null);
        ProductDTO productFound = productListDTO.getContent().get(0);
        ProductsUpdateDTO productsUpdateDTO = new ProductsUpdateDTO().gtinCodes(List.of(productFound.getGtinCode())).currentStatus(productFound.getStatus()).motivation(PRODUCT_MOTIVATION);
        apiClientContext.getRegisterPortalOperationClient().updateProductStatusRejected(productsUpdateDTO);
    }

    @And("si verifica che lo stato del prodotto con codice eprel: {string} sia: {string}")
    public void verifyProductState(String eprelCode, String status) {
        ProductListDTO productListDTO = getProductByEprelCode(eprelCode, null);
        ProductDTO productFound = productListDTO.getContent().get(0);
        assertEquals(ProductStatus.fromValue(status), productFound.getStatus());
    }

    @And("si verifica che per il prodotto {string} ci sia {int} motivazione")
    @And("si verifica che per il prodotto {string} ci siano {int} motivazioni")
    public void verifyMotivationPresent(String eprelCode, int motivationNumber) {
        ProductListDTO productListDTO = getProductByEprelCode(eprelCode, null);
        ProductDTO productFound = productListDTO.getContent().get(0);

        Assertions.assertNotNull(productListDTO);
        Assertions.assertNotNull(productListDTO.getContent());
        Assertions.assertFalse(productListDTO.getContent().isEmpty());
        ObjectMapper objectMapper = new ObjectMapper();
        Assertions.assertEquals(motivationNumber, Optional.ofNullable(productListDTO.getContent().get(0)).map(ProductDTO::getStatusChangeChronology).orElse(List.of())
                .stream()
                .map(obj -> objectMapper.convertValue(obj, StatusChangeChronology.class))
                .filter(item -> !item.getMotivation().isEmpty())
                .toList().size()
        );
    }

    @When("viene iniziato l'iter di approvazione del prodotto")
    public void markProductAsWaitApproved() {
        try {
            searchAndmarkProduct(productsUpdateDTO -> apiClientContext.getRegisterPortalOperationClient().updateProductStatusWaitApproved(productsUpdateDTO));
        } catch (HttpStatusCodeException ex) {
            httpStatus = ex.getStatusCode();
        }
    }

    @When("viene approvato il prodotto appena aggiunto")
    public void markProductAsApproved() {
        try {
            searchAndmarkProduct(productsUpdateDTO -> apiClientContext.getRegisterPortalOperationClient().updateProductStatusApproved(productsUpdateDTO));
        } catch (HttpStatusCodeException ex) {
            httpStatus = ex.getStatusCode();
        }
    }

    @When("viene ripristinato il prodotto appena aggiunto da L2")
    public void markProductAsRestoredFromL2() {
        searchAndmarkProduct(productsUpdateDTO -> apiClientContext.getRegisterPortalOperationClient().updateProductStatusRestored(productsUpdateDTO));
    }

    private void searchAndmarkProduct(Function<ProductsUpdateDTO, UpdateResponseDTO> operation) {
        List<String> gtinCodes = sharedCommonContext.getLastProductsUploaded().stream().map(ProductDTO::getGtinCode).toList();
        for (String gtinCode : gtinCodes) {
            ProductListDTO productListDTO = apiClientContext.getRegisterPortalOperationClient().getProducts(0, 10, null, null, null, gtinCode, null, null, null, null, null, null, null);
            ProductStatus productStatus = productListDTO.getContent().stream().filter(item -> gtinCode.equalsIgnoreCase(item.getGtinCode())).map(ProductDTO::getStatus).findAny().orElse(null);
            Assertions.assertNotNull(productStatus, "Invalid product state!");
            ProductsUpdateDTO productsUpdateDTO = new ProductsUpdateDTO().gtinCodes(gtinCodes).currentStatus(productStatus).motivation(PRODUCT_MOTIVATION);
            updateResponseDTO = operation.apply(productsUpdateDTO);
        }
    }

    private ProductListDTO getProductByEprelCode(String eprelCode, String sort) {
        String gtinCode = sharedCommonContext.getLastProductsUploaded().stream().filter(product -> product.getEprelCode().equals(eprelCode)).map(ProductDTO::getGtinCode).findFirst().orElse(null);
        Assertions.assertNotNull(gtinCode, "Product with the desired eprelCode not found!");
        return apiClientContext.getRegisterPortalOperationClient().getProducts(0, 10, sort, null, null, null, null, gtinCode, null, null, null, null, null);
    }

    @Then("si verifica che la chiamata abbia ritornato uno status code: {int}")
    public void verifyStatusCodeResponse(int expectedStatusCode) {
        Assertions.assertNotNull(httpStatus);
        Assertions.assertEquals(expectedStatusCode, httpStatus.value());

    }


}
