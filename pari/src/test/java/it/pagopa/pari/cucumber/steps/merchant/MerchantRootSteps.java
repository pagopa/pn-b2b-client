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
import org.springframework.web.client.HttpStatusCodeException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class MerchantRootSteps {
    private final ApiClientContext apiClientContext;
    private final SharedCommonContext sharedCommonContext;

    private HttpStatusCodeException httpStatusCodeException;
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

    @When("la chiamata ritorna un errore con status code: {int}")
    public void verifyErrorStatusCode(int expectedStatusCode) {
        Assertions.assertEquals(expectedStatusCode, httpStatusCodeException.getStatusCode().value());
    }

    @Given("viene censito un nuovo punto vendita con i seguenti parametri:")
    public void addPos(DataTable dataTable) {
        Map<String, String> dataTableMap = dataTable.asMap();
        String merchantId = Optional.ofNullable(getData(dataTableMap, "merchantId")).orElse(sharedCommonContext.getUserData().getMerchantId());
        try {
            apiClientContext.getMerchantOperationClient().putPointOfSales(merchantId, List.of(createNewPointOfSaleDTO(dataTableMap)));
        } catch (HttpStatusCodeException ex) {
            httpStatusCodeException = ex;
        }
    }

    private PointOfSaleDTO createNewPointOfSaleDTO(Map<String, String> dataTableMap) {
        String contactEmail = String.format("test.p%d@prova.com", new Random().nextInt(100000));
        return new PointOfSaleDTO().type(PointOfSaleDTO.TypeEnum.ONLINE)
                .id(getData(dataTableMap, "id"))
                .franchiseName(getData(dataTableMap, "franchiseName")).region(getData(dataTableMap, "region")).province(getData(dataTableMap, "province"))
                .city(getData(dataTableMap, "city")).zipCode(getData(dataTableMap, "zipCode")).address(getData(dataTableMap, "address"))
                .streetNumber(getData(dataTableMap, "streetNumber"))
                .webSite(getData(dataTableMap, "webSite")).contactEmail(contactEmail)
                .contactName(getData(dataTableMap, "contactName"))
                .contactSurname(getData(dataTableMap, "contactSurname")).channelEmail(getData(dataTableMap, "channelEmail"))
                .channelPhone(getData(dataTableMap, "channelPhone"))
                .channelGeolink(getData(dataTableMap, "channelGeoLink")).channelWebsite(getData(dataTableMap, "channelWebiste"));
    }

    private String getData(Map<String, String> dataTableMap, String property) {
        return Optional.ofNullable(dataTableMap.get(property)).orElse(null);
    }

}
