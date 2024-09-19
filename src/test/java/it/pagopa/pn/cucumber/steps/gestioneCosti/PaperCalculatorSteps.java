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
import org.opentest4j.AssertionFailedError;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class PaperCalculatorSteps {
    private final IPaperCalculatorClientImpl paperCalculatorClient;
    private ShipmentCalculateRequest shipmentCalculateRequest;
    private ResponseEntity<ShipmentCalculateResponse> calculateResponseResponseEntity;
    private List<CalculateRequestParameter> requestParamsFromCsv;
    private final List<String> errorList;

    public PaperCalculatorSteps(IPaperCalculatorClientImpl paperCalculatorClient) {
        this.paperCalculatorClient = paperCalculatorClient;
        this.errorList = new ArrayList<>();
    }

    @Given("viene creata una richiesta con valori di default")
    public ShipmentCalculateRequest createDefaultShipmentCalculateRequest() {
        return createShipmentCalculateRequest(ShipmentCalculateRequest.ProductEnum._890, "00102", 2, true);
    }

    @Given("viene creata una richiesta con i seguenti valori")
    public ShipmentCalculateRequest createShipmentCalculateRequest(@Transpose CalculateRequestParameter parameter) {
        return createShipmentCalculateRequest(parameter.getProduct(), parameter.getGeokey(), parameter.getNumSides(), parameter.getIsReversePrinter());
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
            Integer numSides, Boolean isReversePrinter) {
        shipmentCalculateRequest = new ShipmentCalculateRequest();
        shipmentCalculateRequest.setProduct(product);
        shipmentCalculateRequest.setGeokey(geokey);
        shipmentCalculateRequest.setNumSides(numSides);
        shipmentCalculateRequest.setIsReversePrinter(isReversePrinter);
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

    @Then("viene invocata l'api e si controlla che il risultato sia quello atteso per la gara con tenderId {string}")
    public void callApiAndCheckResult(String tenderId) {
        requestParamsFromCsv
                .forEach(x -> {
                    createShipmentCalculateRequest(x.getProduct(), x.getGeokey(), x.getNumSides(), x.getIsReversePrinter());
                    callPaperCalculateCost(tenderId);
                    Optional.ofNullable(calculateResponseResponseEntity)
                            .map(HttpEntity::getBody)
                            .map(ShipmentCalculateResponse::getCost)
                            .ifPresentOrElse((value) -> checkIfEquals(x, value),
                                    () -> errorList.add(formatSingleErrorMessage(x, AssertionFailureBuilder.assertionFailure().message("errore nel recuperare il costo").build())));
                });
        Assertions.assertTrue(errorList.isEmpty(), createGeneralErrorMessage(errorList));
    }

    private void checkIfEquals(CalculateRequestParameter calculateRequestParameter, Integer actualCost) {
        try {
            Assertions.assertEquals(calculateRequestParameter.getExpectedResult(), actualCost);
        } catch (AssertionFailedError ex) {
            errorList.add(formatSingleErrorMessage(calculateRequestParameter, ex));
        }
    }

    private String createGeneralErrorMessage(List<String> err) {
        StringBuilder stringBuilder = new StringBuilder("Si è verificato un problema con le seguenti tuple: ");
        for (String c : err) stringBuilder.append(c);
        return stringBuilder.toString();
    }

    private String formatSingleErrorMessage(CalculateRequestParameter calculateRequestParameter, AssertionFailedError assertionFailedError) {
        return String.format("%s;%s;%s;%d;%d;%d;%b;%s;%s;%d %s\n",
                calculateRequestParameter.getGeokey(), calculateRequestParameter.getProduct().getValue(), calculateRequestParameter.getPageWeight(),
                calculateRequestParameter.getPageNumber(), calculateRequestParameter.getNumSides(),
                calculateRequestParameter.getIsReversePrinter(), calculateRequestParameter.getCost(), calculateRequestParameter.getCostPlusEuroDigital(),
                calculateRequestParameter.getExpectedResult(), assertionFailedError.getMessage());
    }

}
