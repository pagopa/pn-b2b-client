package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.MandateDtoRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.UserDto;
import it.pagopa.pn.client.b2b.pa.service.IMandateReverseServiceClient;
import it.pagopa.pn.client.b2b.pa.service.IMandateServiceClient;
import org.apache.commons.lang.time.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.http.ResponseEntity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MandateReverseSteps {
    private final IMandateReverseServiceClient mandateReverseServiceClient;
    private final IMandateServiceClient mandateServiceClient;
    private MandateDtoRequest mandateDtoRequest;
    private ResponseEntity<String> mandateReverseResponse;

    public MandateReverseSteps(IMandateReverseServiceClient mandateReverseServiceClient, IMandateServiceClient mandateServiceClient) {
        this.mandateReverseServiceClient = mandateReverseServiceClient;
        this.mandateServiceClient = mandateServiceClient;
    }

    @Given("viene popolata la richiesta")
    public void createMandateDtoRequest() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        mandateDtoRequest = new MandateDtoRequest();
        mandateDtoRequest.setDatefrom(sdf.format(new Date()));
        mandateDtoRequest.setDateto(sdf.format(DateUtils.addDays(new Date(), 1)));
        UserDto userDto = new UserDto();
        userDto.setDisplayName("Mario Cucumber");
        userDto.setFirstName("Mario");
        userDto.setLastName("Cucumber");
        userDto.setFiscalCode("FRMTTR76M06B715E");
        userDto.setPerson(true);
        mandateDtoRequest.setDelegator(userDto);
    }

    @When("viene effettuata la chiamata all'api b2b")
    public void callApi() {
        mandateReverseResponse = mandateReverseServiceClient.createReverseMandateWithHttpInfo(mandateDtoRequest);
    }

    @Then("si verifica che la risposta contenga status code: {int}")
    public void checkStatusCode(int statusCode) {
        Assertions.assertEquals(statusCode, mandateReverseResponse.getStatusCode().value());
    }

    @And("si verifica che la delega è stata creata con stato pending")
    public void verifyMandateIsCreatedWithPendingStatus() {
        mandateServiceClient.listMandatesByDelegate1("PENDING").stream()
                .filter(x -> x.getMandateId().equals(mandateReverseResponse.getBody()))
                .findAny()
                .orElseThrow(() -> new AssertionFailedError("Delega non trovata tra quelle in stato PENDING!"));

    }


}
