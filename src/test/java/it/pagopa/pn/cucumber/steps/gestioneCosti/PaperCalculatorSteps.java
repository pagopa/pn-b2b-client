package it.pagopa.pn.cucumber.steps.gestioneCosti;

import com.opencsv.bean.CsvToBeanBuilder;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.model.ShipmentCalculateRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.model.ShipmentCalculateResponse;
import it.pagopa.pn.client.b2b.pa.service.IPaperCalculatorClientImpl;
import it.pagopa.pn.cucumber.steps.gestioneCosti.domain.CalculateRequestParameter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AssertionFailureBuilder;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Optional;

@Slf4j
public class PaperCalculatorSteps {
    private final IPaperCalculatorClientImpl paperCalculatorClient;
    private ShipmentCalculateRequest shipmentCalculateRequest;
    private ResponseEntity<ShipmentCalculateResponse> calculateResponseResponseEntity;
    private List<CalculateRequestParameter> requestParamsFromCsv;

    public PaperCalculatorSteps(IPaperCalculatorClientImpl paperCalculatorClient) {
        this.paperCalculatorClient = paperCalculatorClient;
    }

    @Given("viene creata una richiesta con valori di default")
    public ShipmentCalculateRequest createDefaultShipmentCalculateRequest() {
        return createShipmentCalculateRequest(ShipmentCalculateRequest.ProductEnum._890, "00102", 2, true, 12);
    }

    @Given("viene creata una richiesta con i seguenti valori")
    public ShipmentCalculateRequest createShipmentCalculateRequest(@Transpose CalculateRequestParameter parameter) {
        return createShipmentCalculateRequest(parameter.getProduct(), parameter.getGeokey(), parameter.getNumSides(), parameter.getIsReversePrinter(), parameter.getPageWeight());
    }

    @When("viene chiamata l'api di calcolo costi con tenderId {string}")
    public void callPaperCalculateCost(String tenderId) {
        try {
            calculateResponseResponseEntity = paperCalculatorClient.calculateCostWithHttpInfo(tenderId, shipmentCalculateRequest);
        } catch (HttpClientErrorException.BadRequest ex) {
            calculateResponseResponseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException.NotFound ex) {
            calculateResponseResponseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Then("l'api ritorna status code {int}")
    public void verifyShipmentCalculateResponseStatusCode(int statusCode) {
        Assertions.assertEquals(statusCode,calculateResponseResponseEntity.getStatusCode().value());
    }

    private ShipmentCalculateRequest createShipmentCalculateRequest(ShipmentCalculateRequest.ProductEnum product, String geokey,
            Integer numSides, Boolean isReversePrinter, Integer pageWeight) {
        shipmentCalculateRequest = new ShipmentCalculateRequest();
        shipmentCalculateRequest.setProduct(product);
        shipmentCalculateRequest.setGeokey(geokey);
        shipmentCalculateRequest.setNumSides(numSides);
        shipmentCalculateRequest.setIsReversePrinter(isReversePrinter);
        shipmentCalculateRequest.setPageWeight(pageWeight);
        return shipmentCalculateRequest;
    }

    @Given("vengono recuperati i valori delle richieste da file")
    public List<CalculateRequestParameter> transformCsvToObject() throws FileNotFoundException {
        try {
            requestParamsFromCsv = new CsvToBeanBuilder(new FileReader("src/main/resources/TEST_massivo_costi.csv"))
                    .withType(CalculateRequestParameter.class)
                    .withSeparator(';')
                    .withIgnoreLeadingWhiteSpace(true)
                    .withSkipLines(1)
                    .build()
                    .parse();
        } catch (FileNotFoundException exception) {
            log.error("Error reading csv file, it is not present or has invalid format!");
            throw exception;
        }
        return requestParamsFromCsv;
    }

    @Then("viene invocata l'api e si controlla che il risultato sia quello atteso")
    public void callApiAndCheckResult() {
        requestParamsFromCsv
                .forEach(x -> {
                    createShipmentCalculateRequest(x.getProduct(), x.getGeokey(), x.getNumSides(), x.getIsReversePrinter(), x.getPageWeight());
                    callPaperCalculateCost(x.getTenderId());
                    Optional.ofNullable(calculateResponseResponseEntity)
                            .map(HttpEntity::getBody)
                            .map(ShipmentCalculateResponse::getCost)
                            .ifPresentOrElse((value) -> Assertions.assertEquals(x.getExpectedResult(), value),
                                    () -> AssertionFailureBuilder.assertionFailure().message("Si è verificato un errore nel recuperare il costo per il CAP: " + x.getGeokey()).buildAndThrow()
                            );
                });
    }

}
