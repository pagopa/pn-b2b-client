package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.MandateDtoRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.UserDto;
import it.pagopa.pn.client.b2b.pa.service.IBffMandateServiceApi;
import it.pagopa.pn.client.b2b.pa.service.IMandateReverseServiceClient;
import it.pagopa.pn.client.b2b.pa.service.IPnWebMandateClient;
import it.pagopa.pn.client.b2b.pa.service.IPnWebRecipientClient;
import it.pagopa.pn.client.b2b.pa.service.impl.B2bMandateServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.B2BRecipientExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import it.pagopa.pn.client.web.generated.openapi.clients.bff.recipientmandate.model.BffMandate;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.AcceptRequestDto;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.MandateDto;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import org.apache.commons.lang.time.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;

public class MandateReverseSteps {
    private final IMandateReverseServiceClient mandateReverseServiceClient;
    private final IPnWebMandateClient mandateServiceClient;
    private final IPnWebRecipientClient b2BRecipientExternalClient;
    private final IBffMandateServiceApi bffMandateServiceApi;
    private final SharedSteps sharedSteps;
    private ResponseEntity<String> mandateReverseResponse;
    private ResponseEntity<Void> acceptMandateResponse;
    private final List<String> groups = new ArrayList<>();

    public MandateReverseSteps(IMandateReverseServiceClient mandateReverseServiceClient, B2bMandateServiceClientImpl mandateServiceClient, SharedSteps sharedSteps,
                               B2BRecipientExternalClientImpl b2BRecipientExternalClient, IBffMandateServiceApi bffMandateServiceApi) {
        this.mandateReverseServiceClient = mandateReverseServiceClient;
        this.mandateServiceClient = mandateServiceClient;
        this.sharedSteps = sharedSteps;
        this.b2BRecipientExternalClient = b2BRecipientExternalClient;
        this.bffMandateServiceApi = bffMandateServiceApi;
    }

   @Given("viene effettuata una richiesta di creazione delega con i seguenti parametri:")
   public void createMandatePG(Map<String, String> data) {
        selectPG(data.get("delegate"));
        MandateDtoRequest request = new MandateDtoRequest();
        request.setDatefrom(getDate(data.getOrDefault("dateFrom", "TODAY")));
        request.setDateto(getDate(data.getOrDefault("dateTo", "TOMORROW")));
        request.setDelegator(getUserDto(data.getOrDefault("delegator", "CucumberSpa")));
        try {
            mandateReverseResponse = mandateReverseServiceClient.createReverseMandateWithHttpInfo(request);
        } catch (HttpClientErrorException.BadRequest exception) {
            mandateReverseResponse = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
   }

    @Then("si verifica che la risposta contenga status code: {int}")
    public void checkStatusCode(int statusCode) {
        Assertions.assertEquals(statusCode, mandateReverseResponse.getStatusCode().value());
    }

    @And("si verifica che la delega a nome di {string} è stata creata con stato pending")
    public void verifyMandateIsCreatedWithPendingStatus(String delegator) {
        isMandatePresent(delegator).orElseThrow(() -> new AssertionFailedError("Mandate with PENDING status not found!"));
    }

    @And("la delega a nome di {string} viene accettata dal delegato senza associare nessun gruppo")
    public void acceptMandateWithoutGroup(String delegator) {
        acceptMandate(null, getVerificationCode(delegator));
    }

    @And("la delega a nome di {string} viene accettata dal delegato associando un gruppo")
    public void acceptMandateWithGroup(String delegator) {
        Assertions.assertFalse(groups.isEmpty(), "The group list cannot be empty!");
        acceptMandate(groups, getVerificationCode(delegator));
    }

    @And("la notifica non può essere recuperata da {string}")
    public void notificationDelegatedNotVisible(String delegate) {
        selectPG(delegate);
        Assertions.assertThrows(HttpClientErrorException.NotFound.class,
                () -> b2BRecipientExternalClient.getReceivedNotification(sharedSteps.getIunVersionamento(), mandateReverseResponse.getBody()));
    }

    @And("si verifica che la delega è stata accettata e la risposta contenga status code: {int}")
    public void checkAcceptMandateResponseStatusCode(int statusCode) {
        Assertions.assertEquals(statusCode, acceptMandateResponse.getStatusCode().value());
    }

    @Then("si verifica che la delega è stata creata senza un gruppo associato")
    public void isMandateCreatedWithoutGroup() {
        Assertions.assertFalse(isMandateGroupPresent());
    }

    @Then("si verifica che la delega è stata creata con un gruppo associato")
    public void isMandateCreatedWithGroup() {
        Assertions.assertTrue(isMandateGroupPresent());
    }

    @Then("viene recuperato il primo gruppo disponibile attivo")
    public void retrieveTheFirstGroupAvailableForDelegator() {
        String activeGroup = sharedSteps.getPnExternalServiceClient().pgGroupInfo(mandateServiceClient.getBearerTokenSetted())
                .stream()
                .filter(Objects::nonNull)
                .filter(x -> "ACTIVE".equals(x.get("status")))
                .map(x -> x.get("id"))
                .findFirst().orElse(null);
        if (activeGroup != null && !activeGroup.isEmpty()) {
            groups.add(activeGroup);
        }
    }

    private String getVerificationCode(String delegator) {
        selectPG(delegator);
        String verificationCode;
        try {
            verificationCode = bffMandateServiceApi.getMandatesByDelegatorV1()
                    .stream()
                    .filter(x -> x.getMandateId().equals(mandateReverseResponse.getBody()))
                    .map(BffMandate::getVerificationCode)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        } catch (Exception ex) {
            throw new AssertionFailedError("There was an error while retrieving the verification code: " + ex);
        }
        return verificationCode;
    }

    private void acceptMandate(List<String> groups, String verificationCode) {
        try {
            acceptMandateResponse = mandateServiceClient.acceptMandateWithHttpInfo(mandateReverseResponse.getBody(), new AcceptRequestDto().groups(groups).verificationCode(verificationCode));
        } catch (Exception ex) {
            throw new AssertionFailedError("There was an error while accepting the mandate: " + ex);
        }
    }

    private void selectPG(String user) {
        switch (user.trim().toLowerCase()) {
            case "gherkinsrl" -> {
                this.mandateReverseServiceClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
                this.b2BRecipientExternalClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
                this.bffMandateServiceApi.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
                this.mandateServiceClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
            }
            case "cucumberspa" -> {
                this.mandateReverseServiceClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_2);
                this.b2BRecipientExternalClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_2);
                this.bffMandateServiceApi.setBearerToken(SettableBearerToken.BearerTokenType.PG_2);
                this.mandateServiceClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_2);
            }
            default -> throw new IllegalStateException("Unexpected value: " + user.trim().toLowerCase());
        };
    }

    private Optional<MandateDto> isMandatePresent(String delegator) {
        String delegatorTaxId = getTaxIdByUser(delegator);
        return mandateServiceClient.searchMandatesByDelegate(delegatorTaxId, null).stream()
                .filter(x -> x.getMandateId().equals(mandateReverseResponse.getBody()))
                .findFirst();
    }

    private boolean isMandateGroupPresent() {
        return mandateServiceClient.searchMandatesByDelegate(null, null).stream()
                .filter(x -> x.getMandateId().equals(mandateReverseResponse.getBody()))
                .map(MandateDto::getGroups)
                .filter(Objects::nonNull)
                .anyMatch(Predicate.not(List::isEmpty));
    }

    private UserDto getUserDto(String delegator) {
        return switch (delegator) {
            case "Mario Cucumber" -> createUserDto("Mario Cucumber", "Mario", "Cucumber", "FRMTTR76M06B715E", null, true);
            case "EMPTY_FISCAL_CODE" -> createUserDto("Cristoforo Colombo", "Cristoforo", "Colombo", null, null, true);
            case "INVALID_FISCAL_CODE" -> createUserDto("Cristoforo Colombo", "Cristoforo", "Colombo", "AAA8090ZAC", null, true);
            case "EMPTY_NAME" -> createUserDto(null, null, null, "CLMCST42R12D969Z", null, true);
            case "FIRST_NAME_NOT_VALID" -> createUserDto("Cristoforo Colombo", "PippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippo", "Colombo", "FRMTTR76M06B715E", null, true);
            case "LAST_NAME_NOT_VALID" -> createUserDto("Cristoforo Colombo", "Cristoforo", "PippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippo", "FRMTTR76M06B715E", null, true);
            case "GherkinSrl" -> createUserDto("gherkinsrl", "gherkin", "srl", "12666810299", "gherkinsrl", false);
            case "CucumberSpa" -> createUserDto("cucumberspa", "cucumber", "spa", "20517490320", "cucumberspa", false);
            default -> throw new IllegalStateException("Unexpected value: " + delegator);
        };
    }

    private UserDto createUserDto(String displayName, String firstName, String lastName, String fiscalCode, String companyName, boolean isPerson) {
        UserDto userDto = new UserDto();
        userDto.setPerson(isPerson);
        userDto.setDisplayName(displayName);
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setFiscalCode(fiscalCode);
        userDto.setCompanyName(companyName);
        return userDto;
    }

    private String getDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return switch (date) {
            case "TODAY" -> sdf.format(new Date());
            case "TOMORROW" -> sdf.format(DateUtils.addDays(new Date(), 1));
            case "PAST_DATE" -> "2023-01-01";
            case "INVALID_FORMAT" -> "01-01-2023";
            case "EMPTY_DATE" -> null;
            default -> throw new IllegalStateException("Unexpected value: " + date);
        };
    }

    private String getTaxIdByUser(String user) {
        return switch (user) {
            case "Mario Cucumber" -> sharedSteps.getMarioCucumberTaxID();
            case "GherkinSrl" -> sharedSteps.getGherkinSrltaxId();
            case "CucumberSpa" -> sharedSteps.getCucumberSpataxId();
            default -> throw new IllegalArgumentException();
        };
    }
}
