package it.pagopa.pari.cucumber.steps.registrobeni;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.pari.cucumber.utils.ApiClientContext;
import it.pagopa.pari.cucumber.utils.SharedCommonContext;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.ProductDTO;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.ProductListDTO;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.ProductStatus;
import it.pagopa.pari.registrobeni.domain.RdbRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Optional;

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

    @Then("si verifica che il prodotto sia marcato come: {string}")
    public void verifyProductMarkedAsState(String state) throws InterruptedException {
        String orgId = sharedCommonContext.getUserData().getOrgRole().contains("invitalia") ? null : sharedCommonContext.getUserData().getOrgId();
        List<String> gtinCodes = sharedCommonContext.getLastProductsUploaded().stream().map(ProductDTO::getGtinCode).toList();
        for (String gtinCode : gtinCodes) {
            ProductListDTO productListDTO = apiClientContext.getRegisterPortalOperationClient().getProducts(0, 10, null, null, null, gtinCode, null, null, null, orgId);
            Assertions.assertFalse(productListDTO.getContent().isEmpty());
            Assertions.assertTrue(productListDTO.getContent().stream().allMatch(item -> item.getStatus().getValue().equalsIgnoreCase(state)));
        }
    }

    @And("si verifica che ci siano {int} motivazioni a seguito delle operazioni di (esclusione)(contrassegnazione) fatte da {rdbRole}")
    public void readMotivation(int expectedMotivation, RdbRole role) {
        String motivationRole = switch (role) {
            case INVITALIA_L1 -> "L1";
            case INVITALIA_L2 -> "L2";
            default -> throw new IllegalArgumentException("Invalid role passed!");
        };
        ObjectMapper objectMapper = new ObjectMapper();
        for (ProductDTO productDTO : sharedCommonContext.getLastProductsUploaded()) {
            ProductListDTO productListDTO = apiClientContext.getRegisterPortalOperationClient().getProducts(0, 10, null, null, productDTO.getEprelCode(), productDTO.getGtinCode(), null,
                    productDTO.getProductName(), productDTO.getStatus(), null);
            Assertions.assertNotNull(productListDTO);
            Assertions.assertNotNull(productListDTO.getContent());
            Assertions.assertFalse(productListDTO.getContent().isEmpty());

            Assertions.assertEquals(expectedMotivation, Optional.ofNullable(productListDTO.getContent().get(0)).map(ProductDTO::getStatusChangeChronology).orElse(List.of())
                    .stream()
                    .map(obj -> objectMapper.convertValue(obj, StatusChangeChronology.class))
                    .filter(item -> motivationRole.equals(item.getRole()))
                    .filter(item -> !item.getMotivation().isEmpty())
                    .toList().size()
            );

        }
    }

    @AllArgsConstructor
    @Getter
    @Setter
    @NoArgsConstructor
    private static class StatusChangeChronology {
        private String username;
        private String role;
        private String motivation;
        private String updateDate;
        private String currentStatus;
        private String targetStatus;
    }



//    @Then("viene verificata la presenza di un prodotto escluso, se non presente viene aggiunto")
//    public void getRejectedProductOrAdd() {
//        ProductListDTO productListDTO = null;
//        int i = 0;
//        while (productListDTO == null || i < productListDTO.getTotalPages()) {
//            productListDTO = apiClientContext.getRegisterPortalOperationClient().getProducts(0, 10, null, null, null, null,
//                    null, null, ProductStatus.REJECTED, sharedCommonContext.getUserData().getOrgId());
//            i++;
//            if (!productListDTO.getContent().isEmpty()) {
//                sharedCommonContext.setLastProductsUploaded(productListDTO.getContent());
//                break;
//            }
//        }
//
//
//    }




}
