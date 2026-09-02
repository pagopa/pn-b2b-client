package it.pagopa.pn.cucumber.steps.ioMock;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.common.util.StringUtils;
import it.pagopa.pn.cucumber.steps.ioMock.context.IoMockScenarioContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IOMockProfilesSteps {

    private final IoMockScenarioContext context;
    private final IOMockCommonSteps commonSteps;

    @Autowired
    public IOMockProfilesSteps(IoMockScenarioContext context, IOMockCommonSteps commonSteps) {
        this.context = context;
        this.commonSteps = commonSteps;
    }

    //-----------------------------------------------------------------------------------------
    // PROFILES DOMAIN GIVEN STEPS
    //-----------------------------------------------------------------------------------------

    @Given("un destinatario con codice fiscale in blacklist {string}")
    @Given("un destinatario abilitato al routing reale {string}")
    @Given("un destinatario con codice fiscale ordinario {string}")
    @Given("un destinatario non registrato ad App IO {string}")
    public void prepareProfileRequestWithFiscalCode(String fiscalCode) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("fiscal_code", StringUtils.resolveValue(fiscalCode));
        context.setRequestPayload(payload);
    }

    @Given("una richiesta di verifica profilo priva del campo {string}")
    public void prepareProfileRequestMissingField(String fieldName) {
        Map<String, Object> payload = new HashMap<>();
        context.setRequestPayload(payload);
    }

    @Given("una richiesta di verifica profilo contenente campi non previsti dalle specifiche")
    public void prepareProfileRequestWithExtraFields() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("fiscal_code", "STANDAR_CF_00001");
        payload.put("unknown_extra_field", "unexpected_value");
        payload.put("invalid_parameter", 12345);
        context.setRequestPayload(payload);
    }

    //-----------------------------------------------------------------------------------------
    // PROFILES DOMAIN WHEN STEPS
    //-----------------------------------------------------------------------------------------

    @When("viene richiesta la verifica del profilo utente")
    public void requestProfileVerification() {
        commonSteps.invokeEndpoint("POST /profiles");
    }

    //-----------------------------------------------------------------------------------------
    // PROFILES DOMAIN THEN STEPS
    //-----------------------------------------------------------------------------------------

    @Then("il profilo risulta non abilitato alla ricezione dei messaggi")
    public void verifyProfileSenderNotAllowed() {
        assertThat(context.getActualStatusCode())
                .as("Lo status code per profilo non abilitato deve essere 200 OK")
                .isEqualTo(HttpStatus.OK.value());
        checkSenderAllowedField("sender_allowed", false);
    }

    @Then("il profilo risulta abilitato alla ricezione dei messaggi")
    public void verifyProfileSenderAllowed() {
        assertThat(context.getActualStatusCode())
                .as("Lo status code per profilo abilitato deve essere 200 OK")
                .isEqualTo(HttpStatus.OK.value());
        checkSenderAllowedField("sender_allowed", true);
    }

    @Then("il profilo utente risulta non registrato")
    public void verifyProfileNotFound() {
        assertThat(context.getActualStatusCode())
                .as("Lo status code per profilo non registrato deve essere 404 Not Found")
                .isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Then("la richiesta di verifica profilo viene rifiutata per errore di validazione formale")
    public void verifyProfileValidationFailed() {
        assertThat(context.getActualStatusCode())
                .as("Lo status code per richiesta non conforme deve essere 400 Bad Request")
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void checkSenderAllowedField(String fieldName, boolean expectedValue) {
        assertThat(context.getResponseJson())
                .as("Il body della risposta non è presente o non è un JSON valido")
                .isNotNull();

        assertThat(context.getResponseJson().has(fieldName))
                .as("Il body della risposta non contiene il campo atteso: %s", fieldName)
                .isTrue();

        assertThat(context.getResponseJson().get(fieldName).asBoolean())
                .as("Il valore del campo %s non corrisponde a quello atteso (%s)", fieldName, expectedValue)
                .isEqualTo(expectedValue);
    }
}
