package it.pagopa.pari.cucumber.steps.registrobeni;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pari.cucumber.utils.ApiClientContext;
import it.pagopa.pari.cucumber.utils.SharedCommonContext;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.InstitutionResponse;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.InstitutionsResponse;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.ProductDTO;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.ProductListDTO;
import lombok.Getter;
import lombok.Setter;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegistroBeniInstitutionsSteps {
    private final ApiClientContext apiClientContext;
    private final SharedCommonContext sharedCommonContext;
    private InstitutionsResponse institutionsResponse;
    private InstitutionResponse institutionResponse;
    private InstitutionDTO institutionDTO;
    private HttpStatusCodeException httpStatusCodeException;

    public RegistroBeniInstitutionsSteps(ApiClientContext apiClientContext, SharedCommonContext sharedCommonContext) {
        this.apiClientContext = apiClientContext;
        this.sharedCommonContext = sharedCommonContext;
    }

    @When("viene recuperata la lista di istituzioni")
    public void retrieveInstitutions() {
        try {
            institutionsResponse = apiClientContext.getRegisterPortalOperationClient().getInstitutionsList();
        } catch (HttpStatusCodeException e) {
            httpStatusCodeException = e;
        }
    }

    @When("viene recuperato il dettaglio di una specifica istituzione tra quelle recuperate precedentemente")
    public void retrieveInstitutionOverviewFromPreviousResponseList() {
        assertNotNull(institutionDTO);
        institutionResponse = apiClientContext.getRegisterPortalOperationClient().retrieveInstitutionById(institutionDTO.getInstitutionId());
    }

    @When("viene recuperata la lista prodotti di una specifica istituzione tra quelle recuperate precedentemente")
    public void retrieveInstitutionProducts() {
        assertNotNull(institutionsResponse);
        List<ProductDTO> productDTO = Optional.ofNullable(institutionsResponse.getInstitutions())
                .orElse(List.of())
                .stream()
                .map(this::createInstitution)
                .map(ist -> apiClientContext.getRegisterPortalOperationClient().getProducts(0, 10, null, null,
                        null, null, null, null, null, ist.getInstitutionId()))
                .filter(Objects::nonNull)
                .map(ProductListDTO::getContent)
                .filter(Objects::nonNull)
                .filter(list -> !list.isEmpty())
                .findFirst()
                .orElse(List.of());
        sharedCommonContext.setProductDTO(productDTO);
    }

    @And("si verifica che il prodotto ritornato abbia tutti i campi validi")
    public void verifyProductsData() {
        List<ProductDTO> productDTO = sharedCommonContext.getProductDTO();
        assertNotNull(productDTO);
        productDTO.forEach(x -> {
            assertNotNull(x.getOrganizationId());
            assertNotNull(x.getRegistrationDate());
            assertNotNull(x.getStatus());
            assertNotNull(x.getModel());
            assertNotNull(x.getCategory());
        });
    }

    @When("si tenta di recuperare il dettaglio di una specifica istituzione con id: {string}")
    public void retrieveIstitutionById(String institutionId) {
        try {
            institutionResponse = apiClientContext.getRegisterPortalOperationClient().retrieveInstitutionById(institutionId);
        } catch (HttpStatusCodeException e) {
            httpStatusCodeException = e;
        }
    }

    @Then("la chiamata ha restituito status code: {int}")
    public void verifyForbiddenResponse(int expectedStatusCode) {
        assertEquals(HttpStatus.valueOf(expectedStatusCode), httpStatusCodeException.getStatusCode());
    }

    @Then("si controlla che la lista ritornata sia popolata correttamente")
    public void verifyInstitutionResponse() {
        assertNull(httpStatusCodeException);
        assertNotNull(institutionsResponse);
        assertNotNull(institutionsResponse.getInstitutions());
        institutionsResponse.getInstitutions().forEach(x -> {
            institutionDTO = createInstitution(x);
            assertFalse(institutionDTO.getInstitutionId().isEmpty());
            assertFalse(institutionDTO.getCreatedAt().isEmpty());
            assertFalse(institutionDTO.getUpdatedAt().isEmpty());
            assertFalse(institutionDTO.getDescription().isEmpty());
        });
    }

    @Then("si controlla che il dettaglio dell'istituzione ritornata abbia tutti i campi validi")
    public void verifyInsitutionResponse() {
        assertNotNull(institutionResponse);
        assertTrue(StringUtils.isNotBlank(institutionResponse.getAddress()));
        assertTrue(StringUtils.isNotBlank(institutionResponse.getCity()));
        assertTrue(StringUtils.isNotBlank(institutionResponse.getCountry()));
        assertTrue(StringUtils.isNotBlank(institutionResponse.getDescription()));
        assertTrue(StringUtils.isNotBlank(institutionResponse.getDigitalAddress()));
        assertTrue(StringUtils.isNotBlank(institutionResponse.getFiscalCode()));
        assertTrue(StringUtils.isNotBlank(institutionResponse.getVatNumber()));
        assertTrue(StringUtils.isNotBlank(institutionResponse.getZipCode()));
    }

    private InstitutionDTO createInstitution(Object fromObj) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(fromObj, InstitutionDTO.class);
    }

    @Getter
    @Setter
    private static class InstitutionDTO {
        private String institutionId;
        private String createdAt;
        private String updatedAt;
        private String description;
    }
}
