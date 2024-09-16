package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.MandateDto;
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
import java.util.Map;
import java.util.Optional;

public class MandateReverseSteps {
    private final IMandateReverseServiceClient mandateReverseServiceClient;
    private final IMandateServiceClient mandateServiceClient;
    private MandateDtoRequest mandateDtoRequest;
    private ResponseEntity<String> mandateReverseResponse;

    public MandateReverseSteps(IMandateReverseServiceClient mandateReverseServiceClient, IMandateServiceClient mandateServiceClient) {
        this.mandateReverseServiceClient = mandateReverseServiceClient;
        this.mandateServiceClient = mandateServiceClient;
    }

    @Given("viene popolata la richiesta con:")
    public void createMandateDtoRequest(Map<String, String> data) {
        mandateDtoRequest = new MandateDtoRequest();
        mandateDtoRequest.setDatefrom(getDateTo("TODAY"));
        mandateDtoRequest.setDateto(getDateTo(data.getOrDefault("endTo", "TOMORROW")));
        mandateDtoRequest.setDelegator(getUserDto(data.getOrDefault("citizen", "VALID_CITIZEN")));
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
        isMandatePresent().orElseThrow(() -> new AssertionFailedError("Delega non trovata tra quelle in stato PENDING!"));
    }

    @And("si verifica che la delega non è stata creata")
    public void verifyMandateIsNotCreated() {
        isMandatePresent().ifPresent(x -> {throw new AssertionFailedError("Delega creata nonostante i campi errati!");});
    }

    private Optional<MandateDto> isMandatePresent() {
        return mandateServiceClient.listMandatesByDelegate1("PENDING").stream()
                .filter(x -> x.getMandateId().equals(mandateReverseResponse.getBody()))
                .findFirst();
    }

    private UserDto getUserDto(String citizen) {
        return switch (citizen) {
            case "VALID_CITIZEN" -> createUserDto("Cristoforo Colombo", "Cristoforo", "Colombo", "CLMCST42R12D969Z");
            case "EMPTY_FISCAL_CODE" -> createUserDto("Cristoforo Colombo", "Cristoforo", "Colombo", null);
            case "INVALID_FISCAL_CODE" -> createUserDto("Cristoforo Colombo", "Cristoforo", "Colombo", "AAA8090ZAC");
            case "EMPTY_NAME" -> createUserDto(null, null, null, "CLMCST42R12D969Z");
            case "FIRST_NAME_NOT_VALID" -> createUserDto("Cristoforo Colombo", "PippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoP", "Colombo", "CLMCST42R12D969Z");
            case "LAST_NAME_NOT_VALID" -> createUserDto("Cristoforo Colombo", "Cristoforo", "PippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoP", "CLMCST42R12D969Z");
            default -> throw new IllegalStateException("Unexpected value: " + citizen);
        };
    }

    private UserDto createUserDto(String displayName, String firstName, String lastName, String fiscalCode) {
        UserDto userDto = new UserDto();
        userDto.setPerson(true);
        userDto.setDisplayName(displayName);
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setFiscalCode(fiscalCode);
        return userDto;
    }

    private String getDateTo(String endTo) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return switch (endTo) {
            case "TODAY" -> sdf.format(new Date());
            case "TOMORROW" -> sdf.format(DateUtils.addDays(new Date(), 1));
            case "PAST_DATE" -> "2023-01-01";
            case "INVALID_FORMAT" -> "01-01-2023";
            case "EMPTY_DATE" -> null;
            default -> throw new IllegalStateException("Unexpected value: " + endTo);
        };
    }


}
