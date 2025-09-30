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
        Assertions.assertNotNull(listPointOfSaleDTO.getPointsOfSale());
        Assertions.assertFalse(listPointOfSaleDTO.getPointsOfSale().isEmpty());
    }

    @When("si recupera il dettaglio di uno specifico punto vendita")
    public void getPosDetails() {
        verifyPoSList();
        pointOfSaleDTO = apiClientContext.getMerchantOperationClient().getPointOfSale(sharedCommonContext.getUserData().getMerchantId(),
                listPointOfSaleDTO.getPointsOfSale().get(0).getId());
        Assertions.assertNotNull(pointOfSaleDTO);
    }

    @When("si recupera il dettaglio di uno specifico punto vendita con i seguenti parametri:")
    public void getPosDetails(DataTable dataTable) {
        Map<String, String> dataTableMap = dataTable.asMap();
        String merchantId = Optional.ofNullable(dataTableMap.get("merchantId")).orElse(sharedCommonContext.getUserData().getMerchantId());
        String pointOfSaleId = Optional.ofNullable(dataTableMap.get("pointOfSaleId")).orElse(listPointOfSaleDTO.getPointsOfSale().get(0).getId());
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

    private PointOfSaleDTO createNewPointOfSaleDTO(Map<String, String> dataTableMap) {
        String contactEmail = String.format("test.p%d@prova.com", new Random().nextInt(100000));
        return new PointOfSaleDTO().type(PointOfSaleDTO.TypeEnum.ONLINE)
                .id(getData(dataTableMap, "id", "688cb2c22fb2709e4ba6d18d"))
                .franchiseName(getData(dataTableMap, "franchiseName", "Test8"))
                .region(getData(dataTableMap, "region", "Lombardia")).province(getData(dataTableMap, "province", "MI"))
                .city(getData(dataTableMap, "city", "Milano")).zipCode(getData(dataTableMap, "zipCode", "20100"))
                .address(getData(dataTableMap, "address", "Via Trieste, 65015 Montesilvano PE, Italia"))
                .streetNumber(getData(dataTableMap, "streetNumber", "12"))
                .webSite(getData(dataTableMap, "webSite", "https://www.mediaworld.it/")).contactEmail(contactEmail)
                .contactName(getData(dataTableMap, "contactName", "Mario"))
                .contactSurname(getData(dataTableMap, "contactSurname", "Rossi")).channelEmail(getData(dataTableMap, "channelEmail", "support@superstore.it"))
                .channelPhone(getData(dataTableMap, "channelPhone", "+39021234567"))
                .channelGeolink(getData(dataTableMap, "channelGeoLink", "https://maps.app.goo.gl/abc123"))
                .channelWebsite(getData(dataTableMap, "channelWebiste", "https://channel.superstore.it"));
    }

    private String getData(Map<String, String> dataTableMap, String property, String defaultValue) {
        String value = dataTableMap.get(property);
        return "NULL".equals(value) ? null : Optional.ofNullable(value).orElse(defaultValue);
    }

}
