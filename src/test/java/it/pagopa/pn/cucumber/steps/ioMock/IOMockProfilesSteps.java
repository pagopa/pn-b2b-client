package it.pagopa.pn.cucumber.steps.ioMock;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import it.pagopa.common.util.StringUtils;
import it.pagopa.pn.cucumber.steps.ioMock.context.IoMockScenarioContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IOMockProfilesSteps {

    private final IoMockScenarioContext context;

    @Autowired
    public IOMockProfilesSteps(IoMockScenarioContext context) {
        this.context = context;
    }

    //-----------------------------------------------------------------------------------------
    // PROFILES DOMAIN GIVEN STEPS
    //-----------------------------------------------------------------------------------------

    @Given("preparo una richiesta di verifica profilo con codice fiscale {string}")
    public void prepareProfileRequestWithFiscalCode(String fiscalCode) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("fiscal_code", StringUtils.resolveValue(fiscalCode));
        context.setRequestPayload(payload);
    }

    @Given("preparo una richiesta di verifica profilo senza il campo {string}")
    public void prepareProfileRequestMissingField(String fieldName) {
        Map<String, Object> payload = new HashMap<>();
        // Payload privo del campo indicato
        context.setRequestPayload(payload);
    }

    @Given("preparo una richiesta di verifica profilo contenente campi non definiti nelle specifiche OpenAPI")
    public void prepareProfileRequestWithExtraFields() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("fiscal_code", "STANDAR_CF_00001");
        payload.put("unknown_extra_field", "unexpected_value");
        payload.put("invalid_parameter", 12345);
        context.setRequestPayload(payload);
    }

    //-----------------------------------------------------------------------------------------
    // PROFILES DOMAIN THEN / AND STEPS
    //-----------------------------------------------------------------------------------------

    @And("^verifico che il body della risposta contenga \"([^\"]*)\" impostato a (true|false)$")
    public void verifySenderAllowedFieldRegex(String fieldName, String boolVal) {
        boolean expectedValue = Boolean.parseBoolean(boolVal);
        checkSenderAllowedField(fieldName, expectedValue);
    }

    @And("verifico che il body della risposta contenga {string} impostato a {string}")
    public void verifySenderAllowedFieldString(String fieldName, String boolVal) {
        boolean expectedValue = Boolean.parseBoolean(boolVal);
        checkSenderAllowedField(fieldName, expectedValue);
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
