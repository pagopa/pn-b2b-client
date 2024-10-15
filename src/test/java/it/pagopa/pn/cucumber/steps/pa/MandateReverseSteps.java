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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import lombok.extern.slf4j.Slf4j;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;

@Slf4j
public class MandateReverseSteps {
    private final IMandateReverseServiceClient mandateReverseServiceClient;
    private final IPnWebMandateClient mandateServiceClient;
    private final IPnWebRecipientClient b2BRecipientExternalClient;
    private final IBffMandateServiceApi bffMandateServiceApi;
    private final SharedSteps sharedSteps;
    private String reverseMandateResponse;
    private HttpStatusCodeException reverseMandateStatusCodeException;
    private final List<String> groups = new ArrayList<>();
    private final String verificationCode = "24411";

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
            log.info("[TEST]. CREATING MANDATE WITH DATA {}", request);
            reverseMandateResponse = mandateReverseServiceClient.createReverseMandate(request);
            log.info("[TEST]. CREATED MANDATE {}",  reverseMandateResponse);
        } catch (HttpStatusCodeException statusCodeException) {
            reverseMandateStatusCodeException = statusCodeException;
            log.info("[TEST]. MANDATE CREATION EXCEPTION");
        }
   }

    @Then("si verifica che la chiamata sia fallita con status code: {int}")
    public void checkStatusCode(int statusCode) {
        Assertions.assertEquals(statusCode, reverseMandateStatusCodeException.getStatusCode().value());
    }


    @Then("si verifica che esista la delega:")
    public void mandateExists(Map<String, String> mandateData) {
        String delegate = mandateData.get("delegate");
        String delegator = mandateData.get("delegator");
        String dateFrom = mandateData.get("dateFrom");
        String dateTo = mandateData.get("dateTo");
        String delegatorTaxId = getTaxIdByUser(delegator);

        assertFalse(mandateServiceClient.searchMandatesByDelegate(delegatorTaxId, null).stream()
                .filter(x ->  {
                    try {
                        return x.getDelegator().getFiscalCode().equals(delegatorTaxId);
                    }
                    catch(NullPointerException nPx) {
                        return false;
                    }
                })
                .filter(x -> getDate(dateFrom).equals(x.getDatefrom()))
                .filter(x -> getDate(dateTo).equals(x.getDateto())).toList().isEmpty());

    }

    @And("si verifica che la delega a nome di {string} è stata creata con stato pending")
    public void verifyMandateIsCreatedWithPendingStatus(String delegator) {
        MandateDto mandate = isMandatePresent(delegator).orElseThrow(() -> new AssertionFailedError("Mandate with PENDING status not found!"));
        Assertions.assertEquals(mandate.getStatus(), MandateDto.StatusEnum.PENDING);
    }

    @And("la delega a nome di {string} viene accettata da {string} senza associare nessun gruppo")
    public void acceptMandateWithoutGroup(String delegator, String delegate) {
        acceptMandate(delegate, null, getVerificationCode(delegator));
    }

    @And("la delega a nome di {string} viene accettata da {string} associando un gruppo")
    public void acceptMandateWithGroup(String delegator, String delegate) {
        assertFalse(groups.isEmpty(), "The group list cannot be empty!");
        acceptMandate(delegate, groups, getVerificationCode(delegator));
    }

    @And("la notifica non può essere recuperata da {string}")
    public void notificationDelegatedNotVisible(String delegate) {
        selectPG(delegate);
        Assertions.assertThrows(HttpClientErrorException.NotFound.class,
                () -> b2BRecipientExternalClient.getReceivedNotification(sharedSteps.getIunVersionamento(), reverseMandateResponse));
    }

    @Then("si verifica che la delega è stata creata senza un gruppo associato")
    public void isMandateCreatedWithoutGroup() {
        assertFalse(isMandateGroupPresent());
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
        log.info("[TEST]. LOOKING FOR VERIFICATION CODE FOR MANDATE {}", reverseMandateResponse);

bffMandateServiceApi.getMandatesByDelegatorV1()
        .stream()
        .forEach((k) -> {
            log.info("[TEST]. CURR MANDATE {}", k);
        });
        log.info("[TEST]. MANDATES WITH ID {}", reverseMandateResponse);

        try {
            verificationCode = bffMandateServiceApi.getMandatesByDelegatorV1()
                    .stream()
                    .filter(x -> x.getMandateId().equals(reverseMandateResponse))
                    .map(BffMandate::getVerificationCode)

                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        } catch (Exception ex) {
            throw new AssertionFailedError("There was an error while retrieving the verification code: " + ex);
        }
        return verificationCode;
    }

    private void acceptMandate(String delegate, List<String> groups, String verificationCode) {
        log.info("[TEST]. ACCEPTING MANDATE WITH VERIFICATION CODE {} USED TO ACCEPT MANDATE {} for delegate {}",
                verificationCode, reverseMandateResponse, delegate);
        selectPG(delegate);
        try {
            mandateServiceClient.acceptMandate(reverseMandateResponse, new AcceptRequestDto().groups(groups).verificationCode(verificationCode));
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
            case "mario cucumber" -> {
                this.bffMandateServiceApi.setBearerToken(SettableBearerToken.BearerTokenType.USER_1);
            }
            default -> throw new IllegalStateException("Unexpected value: " + user.trim().toLowerCase());
        };
    }

    private Optional<MandateDto> isMandatePresent(String delegator) {
        String delegatorTaxId = getTaxIdByUser(delegator);
        log.info("[TEST]. MANDATES FOR DELEGATOR ({},TAXID: {}): {}",
                delegator, delegatorTaxId, mandateServiceClient.searchMandatesByDelegate(delegatorTaxId, null).stream()
                .filter(x -> x.getMandateId().equals(reverseMandateResponse)).toList());
        return mandateServiceClient.searchMandatesByDelegate(delegatorTaxId, null).stream()
                .filter(x -> x.getMandateId().equals(reverseMandateResponse))
                .findFirst();
    }

    private boolean isMandateGroupPresent() {
        return mandateServiceClient.searchMandatesByDelegate(null, null).stream()
                .filter(x -> x.getMandateId().equals(reverseMandateResponse))
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
