package it.pagopa.pari.cucumber.steps.merchant;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pari.cucumber.utils.ApiClientContext;
import it.pagopa.pari.cucumber.utils.SharedCommonContext;
import it.pagopa.pari.generated.openapi.clients.merchant.root.model.ListPointOfSaleDTO;
import it.pagopa.pari.generated.openapi.clients.merchant.root.model.PointOfSaleDTO;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class MerchantRootSteps {
    private final ApiClientContext apiClientContext;
    private final SharedCommonContext sharedCommonContext;

    private HttpStatusCodeException httpStatusCodeException = new HttpClientErrorException(HttpStatus.OK);
    private ListPointOfSaleDTO listPointOfSaleDTO;
    private PointOfSaleDTO pointOfSaleDTO;

    public MerchantRootSteps(ApiClientContext apiClientContext, SharedCommonContext sharedCommonContext) {
        this.apiClientContext = apiClientContext;
        this.sharedCommonContext = sharedCommonContext;
    }

    @When("viene recuperato l'elenco dei punti vendita")
    public void getPointOfSale(DataTable dataTable) {
        Map<String, String> dataTableMap = dataTable.asMap();
        String merchantId = Optional.ofNullable(dataTableMap.get("merchantId")).orElse(sharedCommonContext.getUserData().getMerchantId());
        String type = Optional.ofNullable(dataTableMap.get("type")).orElse(null);
        String city = Optional.ofNullable(dataTableMap.get("city")).orElse(null);
        String address = Optional.ofNullable(dataTableMap.get("address")).orElse(null);
        try {
            listPointOfSaleDTO = apiClientContext.getMerchantOperationClient().getPointOfSales(merchantId, type, city, address,
                    null, 0, 10, "asc");
        } catch (HttpStatusCodeException ex) {
            httpStatusCodeException = ex;
        }
    }

    @When("la lista dei punti vendita è correttamente popolata")
    public void verifyPoSList() {
        Assertions.assertNotNull(listPointOfSaleDTO);
        Assertions.assertNotNull(listPointOfSaleDTO.getContent());
        Assertions.assertFalse(listPointOfSaleDTO.getContent().isEmpty());
    }

    @When("si recupera il dettaglio di uno specifico punto vendita")
    public void getPosDetails() {
        verifyPoSList();
        pointOfSaleDTO = apiClientContext.getMerchantOperationClient().getPointOfSale(sharedCommonContext.getUserData().getMerchantId(),
                listPointOfSaleDTO.getContent().get(0).getId());
        Assertions.assertNotNull(pointOfSaleDTO);
    }

    @When("si recupera il dettaglio di uno specifico punto vendita con i seguenti parametri:")
    public void getPosDetails(DataTable dataTable) {
        Map<String, String> dataTableMap = dataTable.asMap();
        String merchantId = Optional.ofNullable(dataTableMap.get("merchantId")).orElse(sharedCommonContext.getUserData().getMerchantId());
        String pointOfSaleId = Optional.ofNullable(dataTableMap.get("pointOfSaleId")).orElse(listPointOfSaleDTO.getContent().get(0).getId());
        verifyPoSList();
        try {
            pointOfSaleDTO = apiClientContext.getMerchantOperationClient().getPointOfSale(merchantId, pointOfSaleId);
        } catch (HttpStatusCodeException ex) {
            httpStatusCodeException = ex;
        }
    }

    @When("la chiamata ritorna status code: {int}")
    public void verifyErrorStatusCode(int expectedStatusCode) {
        Assertions.assertEquals(expectedStatusCode, httpStatusCodeException.getStatusCode().value());
    }

    @Given("vengono modificati i seguenti parametri al punto vendita recuperato:")
    public void modifyPoS(DataTable dataTable) {
        Map<String, String> dataTableMap = dataTable.asMap();
        String merchantId = getData(dataTableMap, "merchantId", sharedCommonContext.getUserData().getMerchantId());
        try {
            apiClientContext.getMerchantOperationClient().putPointOfSales(merchantId, List.of(modifyPointOfSaleDTO(pointOfSaleDTO.getId(), dataTableMap)));
        } catch (HttpStatusCodeException ex) {
            httpStatusCodeException = ex;
        }
    }

    @Given("viene censito un nuovo punto vendita con i seguenti parametri:")
    public void addPos(DataTable dataTable) {
        Map<String, String> dataTableMap = dataTable.asMap();
        String merchantId = getData(dataTableMap, "merchantId", sharedCommonContext.getUserData().getMerchantId());
        try {
            apiClientContext.getMerchantOperationClient().putPointOfSales(merchantId, List.of(createNewPointOfSaleDTO(dataTableMap)));
        } catch (HttpStatusCodeException ex) {
            httpStatusCodeException = ex;
        }
    }

    private PointOfSaleDTO modifyPointOfSaleDTO(String id, Map<String, String> dataTableMap) {
        return buildPointOfSaleDTO(dataTableMap, id);
    }

    private PointOfSaleDTO createNewPointOfSaleDTO(Map<String, String> dataTableMap) {
        return buildPointOfSaleDTO(dataTableMap, null);
    }

    private PointOfSaleDTO buildPointOfSaleDTO(Map<String, String> dataTableMap, String id) {
        String contactEmail = String.format("test.p%d@prova.com", new Random().nextInt(100000));
        return new PointOfSaleDTO()
                .type(PointOfSaleDTO.TypeEnum.ONLINE)
                .id(id != null ? id : getData(dataTableMap, "id", "68e380144c70dd06f09d5f72"))
                .franchiseName(getData(dataTableMap, "franchiseName", "Test8"))
                .region(getData(dataTableMap, "region", "Puglia"))
                .province(getData(dataTableMap, "province", "BA"))
                .city(getData(dataTableMap, "city", "Altamura"))
                .zipCode(getData(dataTableMap, "zipCode", "70022"))
                .address(getData(dataTableMap, "address", "Via Santeramo in Colle, 70022 Altamura BA, Italia"))
                .streetNumber(getData(dataTableMap, "streetNumber", "12"))
                .website(getData(dataTableMap, "website", "https://www.mediaworld.it/"))
                .contactEmail(contactEmail)
                .contactName(getData(dataTableMap, "contactName", "Mario"))
                .contactSurname(getData(dataTableMap, "contactSurname", "Rossi"))
                .channelEmail(getData(dataTableMap, "channelEmail", "support@superstore.it"))
                .channelPhone(getData(dataTableMap, "channelPhone", "+39021234567"))
                .channelGeolink(getData(dataTableMap, "channelGeoLink", "https://maps.app.goo.gl/abc123"))
                .channelWebsite(getData(dataTableMap, "channelWebsite", "https://channel.superstore.it"));
    }

    private String getData(Map<String, String> dataTableMap, String property, String defaultValue) {
        String value = dataTableMap.get(property);
        return "NULL".equals(value) ? null : Optional.ofNullable(value).orElse(defaultValue);
    }

}
