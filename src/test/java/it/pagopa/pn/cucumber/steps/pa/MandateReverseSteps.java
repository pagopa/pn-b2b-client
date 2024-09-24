package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.MandateDtoRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.UserDto;
import it.pagopa.pn.client.b2b.pa.service.IMandateReverseServiceClient;
import it.pagopa.pn.client.b2b.pa.service.IPnWebMandateClient;
import it.pagopa.pn.client.b2b.pa.service.IPnWebRecipientClient;
import it.pagopa.pn.client.b2b.pa.service.impl.B2bMandateServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.B2BRecipientExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.AcceptRequestDto;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.MandateDto;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.NotificationSearchResponse;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import org.apache.commons.lang.time.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.*;

public class MandateReverseSteps {
    private final IMandateReverseServiceClient mandateReverseServiceClient;
    private final IPnWebMandateClient mandateServiceClient;
    private final IPnWebRecipientClient b2BRecipientExternalClient;
    private final SharedSteps sharedSteps;
    private MandateDtoRequest mandateDtoRequest;
    private ResponseEntity<String> mandateReverseResponse;
    private ResponseEntity<Void> acceptMandateResponse;
    private final List<String> groups = new ArrayList<>();

    public MandateReverseSteps(IMandateReverseServiceClient mandateReverseServiceClient, B2bMandateServiceClientImpl mandateServiceClient, SharedSteps sharedSteps, B2BRecipientExternalClientImpl b2BRecipientExternalClient) {
        this.mandateReverseServiceClient = mandateReverseServiceClient;
        this.mandateServiceClient = mandateServiceClient;
        this.sharedSteps = sharedSteps;
        this.b2BRecipientExternalClient = b2BRecipientExternalClient;
    }

    private void selectPG(String user) {
        switch (user.trim().toLowerCase()) {
            case "gherkinsrl" -> {
                this.mandateReverseServiceClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
                this.b2BRecipientExternalClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
            }
            case "cucumberspa" -> {
                this.mandateReverseServiceClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_2);
                this.b2BRecipientExternalClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_2);
            }
            default -> throw new IllegalStateException("Unexpected value: " + user.trim().toLowerCase());
        };
    }

   @Given("{string} crea una delega verso se stesso a nome di {string}")
   public void createMandatePG(String delegate, String delegator) {
        selectPG(delegate);
        MandateDtoRequest request = new MandateDtoRequest();
        request.setDatefrom(getDateTo("TODAY"));
        request.setDateto(getDateTo("TOMORROW"));
        request.setDelegator(getUserDto(delegator));
        try {
            mandateReverseResponse = mandateReverseServiceClient.createReverseMandateWithHttpInfo(request);
        } catch (HttpClientErrorException.BadRequest exception) {
            mandateReverseResponse = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
   }

    @When("l'intermediario massivo effettua la richiesta di creazione delega verso se stesso a nome di {string}")
    public void callApi(String delegator) {
        createMandatePG("gherkinsrl", delegator);
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

    @And("la delega viene accettata dal delegato {string} senza associare nessun gruppo")
    public void acceptMandate(String delegate) {
        selectPG(delegate);
        //TODO add call to bff to retrieve the verificationCode
//        String delegatorTaxId = getTaxIdByUser(delegate);
//        List<MandateDto> mandateDtoList = mandateServiceClient.searchMandatesByDelegate(getTaxIdByUser(delegate), null);
//        MandateDto mandateDto = mandateDtoList.stream().filter(mandate -> Objects.requireNonNull(mandate.getDelegator()).getFiscalCode() != null && mandate.getDelegator().getFiscalCode().equalsIgnoreCase(delegatorTaxId)).findFirst().orElse(null);
        acceptMandateResponse = mandateServiceClient.acceptMandateWithHttpInfo(mandateReverseResponse.getBody(), new AcceptRequestDto().groups(null).verificationCode(""));
    }

    @And("la delega viene accettata dal delegato {string} associando un gruppo")
    public void acceptMandateWithGroup(String delegate) {
        selectPG(delegate);
        acceptMandateResponse = mandateServiceClient.acceptMandateWithHttpInfo(mandateReverseResponse.getBody(), new AcceptRequestDto().verificationCode("24411").groups(groups));
    }

    @And("la notifica non può essere recuperata da {string}")
    public void notificationDelegatedNotVisible(String delegate) {
        selectPG(delegate);
        Assertions.assertFalse(getReceivedDelegatedNotification());
    }

    @And("si verifica che la delega è stata accettata e la risposta contenga status code: {int}")
    public void checkAcceptMandateResponseStatusCode(int statusCode) {
        Assertions.assertEquals(statusCode, acceptMandateResponse.getStatusCode().value());
    }

    @Then("si verifica che la delega è stata creata senza un gruppo associato")
    public void isMandateCreatedWithoutGroup() {
        Assertions.assertFalse(isMandateGroupPresent());
    }

    @Then("viene recuperato il primo gruppo disponibile attivo per il delegato {string}")
    public void retrieveTheFirstGroupAvailableForDelegate(String delegate) {
        selectPG(delegate);
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

    private Optional<MandateDto> isMandatePresent() {
        return mandateServiceClient.listMandatesByDelegate1("pending").stream()
                .filter(x -> x.getMandateId().equals(mandateReverseResponse.getBody()))
                .findFirst();
    }

    private boolean isMandateGroupPresent() {
        return mandateServiceClient.listMandatesByDelegate1("active").stream()
                .filter(x -> x.getMandateId().equals(mandateReverseResponse.getBody()))
                .map(MandateDto::getGroups)
                .anyMatch(Objects::nonNull);
    }

    private UserDto getUserDto(String delegator) {
        return switch (delegator) {
            case "Mario Cucumber" -> createUserDto("Mario Cucumber", "Mario", "Cucumber", "FRMTTR76M06B715E", null, true);
            case "EMPTY_FISCAL_CODE" -> createUserDto("Cristoforo Colombo", "Cristoforo", "Colombo", null, null, true);
            case "INVALID_FISCAL_CODE" -> createUserDto("Cristoforo Colombo", "Cristoforo", "Colombo", "AAA8090ZAC", null, true);
            case "EMPTY_NAME" -> createUserDto(null, null, null, "CLMCST42R12D969Z", null, true);
            case "FIRST_NAME_NOT_VALID" -> createUserDto("Cristoforo Colombo", "PippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoP", "Colombo", "CLMCST42R12D969Z", null, true);
            case "LAST_NAME_NOT_VALID" -> createUserDto("Cristoforo Colombo", "Cristoforo", "PippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoP", "CLMCST42R12D969Z", null, true);
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

    private boolean getReceivedDelegatedNotification() {
        boolean beenFound;
        NotificationSearchResponse notificationSearchResponse = b2BRecipientExternalClient.searchReceivedDelegatedNotification(OffsetDateTime.now().minusDays(1), OffsetDateTime.now(), null,
                null, null, null, null, 10, null);
        beenFound = Objects.requireNonNull(notificationSearchResponse.getResultsPage()).stream().anyMatch(elem -> Objects.requireNonNull(elem.getMandateId()).equals(mandateReverseResponse.getBody()));
        if (!beenFound && Boolean.TRUE.equals(notificationSearchResponse.getMoreResult())) {
            for (String pageKey : notificationSearchResponse.getNextPagesKey()) {
                notificationSearchResponse = b2BRecipientExternalClient
                        .searchReceivedDelegatedNotification(OffsetDateTime.now().minusDays(1), OffsetDateTime.now(),
                                null, null, null, null, null, 10, pageKey);
                beenFound = Objects.requireNonNull(notificationSearchResponse.getResultsPage()).stream().anyMatch(elem -> Objects.requireNonNull(elem.getMandateId()).equals(mandateReverseResponse.getBody()));
                if (beenFound) break;
            }
        }
        return beenFound;
    }

}
