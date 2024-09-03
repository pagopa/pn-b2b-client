package it.pagopa.pn.cucumber.steps.gestionecosti;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.api.PaperCalculatorApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.model.ShipmentCalculateRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.model.ShipmentCalculateResponse;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public class PaperCalculatorSteps {
    private final PaperCalculatorApi paperCalculatorApi;
    private ShipmentCalculateRequest shipmentCalculateRequest;
    private ResponseEntity<ShipmentCalculateResponse> calculateResponseResponseEntity;

    @Autowired
    public PaperCalculatorSteps(PaperCalculatorApi paperCalculatorApi) {
        this.paperCalculatorApi = paperCalculatorApi;
    }

    @Given("viene creata una richiesta con valori di default")
    public ShipmentCalculateRequest createDefaultShipmentCalculateRequest() {
        shipmentCalculateRequest = new ShipmentCalculateRequest();
//        request.setNumPages();
//        request.setGeokey(null);
        return shipmentCalculateRequest;
    }

    @When("viene chiamata l'api di calcolo costi con tenderId {string}")
    public void callPaperCalculateCost(String tenderId) {
        calculateResponseResponseEntity = paperCalculatorApi.calculateCostWithHttpInfo(tenderId, shipmentCalculateRequest);
    }

    @Then("l'api ritorna status code {int}")
    public void verifyShipmentCalculateResponseStatusCode(int statusCode) {
        Assertions.assertEquals(statusCode,calculateResponseResponseEntity.getStatusCode().value());
    }
}
