package it.pagopa.pn.cucumber.steps.gestionecosti;

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
    public List<String[]> readCsvFromUrl(String csvUrl) {
        List<String[]> records = new ArrayList<>();

        try {
            URL url = new URL(csvUrl);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;
            while ((line = br.readLine()) != null) {

                String[] values = line.split(";", -1);

                // Verifica che nessun valore sia null o vuoto
                for (String value : values) {
                    Assertions.assertNotNull(value);
                    Assertions.assertFalse(value.isEmpty());
                }

                records.add(values);
            }

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return records;
    }




}
