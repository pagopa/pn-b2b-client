package it.pagopa.pn.cucumber.steps.gestionecosti;

import com.opencsv.bean.CsvToBeanBuilder;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.api.PaperCalculatorApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.model.ShipmentCalculateRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.model.ShipmentCalculateResponse;
import it.pagopa.pn.cucumber.utils.CalculateRequestParameter;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Objects;

public class PaperCalculatorSteps {
    private final PaperCalculatorApi paperCalculatorApi;
    private ShipmentCalculateRequest shipmentCalculateRequest;
    private ResponseEntity<ShipmentCalculateResponse> calculateResponseResponseEntity;
    private List<CalculateRequestParameter> requestParamsFromCsv;

    @Autowired
    public PaperCalculatorSteps(PaperCalculatorApi paperCalculatorApi) {
        this.paperCalculatorApi = paperCalculatorApi;
    }

    @Given("viene creata una richiesta con valori di default")
    public ShipmentCalculateRequest createDefaultShipmentCalculateRequest() {
        return createShipmentCalculateRequest(ShipmentCalculateRequest.ProductEnum.RIS, "", 1, true, 12);
    }

    @Given("viene creata una richiesta con i seguenti valori")
    public ShipmentCalculateRequest createShipmentCalculateRequest(@Transpose CalculateRequestParameter parameter) {
        return createShipmentCalculateRequest(parameter.getProduct(), parameter.getGeokey(), parameter.getNumPages(), parameter.getIsReversePrinter(), parameter.getWeight());
    }


    @When("viene chiamata l'api di calcolo costi con tenderId {string}")
    public void callPaperCalculateCost(String tenderId) {
        calculateResponseResponseEntity = paperCalculatorApi.calculateCostWithHttpInfo(tenderId, shipmentCalculateRequest);
    }

    @Then("l'api ritorna status code {int}")
    public void verifyShipmentCalculateResponseStatusCode(int statusCode) {
        Assertions.assertEquals(statusCode,calculateResponseResponseEntity.getStatusCode().value());
    }

    private ShipmentCalculateRequest createShipmentCalculateRequest(ShipmentCalculateRequest.ProductEnum product, String geokey, int numPages,
        boolean isReversePrinter, int weight) {
        shipmentCalculateRequest = new ShipmentCalculateRequest();
        shipmentCalculateRequest.setProduct(product);
        shipmentCalculateRequest.setGeokey(geokey);
        shipmentCalculateRequest.setNumPages(numPages);
        shipmentCalculateRequest.setIsReversePrinter(isReversePrinter);
        shipmentCalculateRequest.setWeight(weight);
        return shipmentCalculateRequest;
    }

    @Given("vengono recuperati i valori delle richieste da file")
    public List<CalculateRequestParameter> transformCsvToObject() throws FileNotFoundException {
        String fileName = "src/main/resources/TEST_massivo_costi - TEST.csv";
        requestParamsFromCsv = new CsvToBeanBuilder(new FileReader(fileName))
                .withType(CalculateRequestParameter.class)
                .withSeparator(';')
                .withIgnoreLeadingWhiteSpace(true)
                .withSkipLines(1)
                .build()
                .parse();
        return requestParamsFromCsv;
    }

    @Then("viene invocata l'api e si controlla che il risultato sia quello atteso")
    public void callApiAndCheckResult() {
        requestParamsFromCsv.stream()
                .forEach(x -> {
                    createShipmentCalculateRequest(x.getProduct(), x.getGeokey(), x.getNumPages(), x.getIsReversePrinter(), x.getWeight());
                    callPaperCalculateCost("");
                    Assertions.assertEquals(Integer.parseInt(x.getExpectedResult()), Objects.requireNonNull(calculateResponseResponseEntity.getBody()).getCost());
                });
    }




}
