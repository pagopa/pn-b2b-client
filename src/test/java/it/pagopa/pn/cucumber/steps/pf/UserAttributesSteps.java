package it.pagopa.pn.cucumber.steps.pf;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.service.IPnWebUserAttributesClient;
import it.pagopa.pn.client.b2b.pa.service.impl.PnWebUserAttributesInternalClientImpl;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaluserconsents.model.Consent;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaluserconsents.model.ConsentType;
import it.pagopa.pn.client.b2b.pa.wrapper.ApiCallHelper;
import it.pagopa.pn.client.b2b.pa.wrapper.ApiResult;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

public class UserAttributesSteps {

    private final IPnWebUserAttributesClient webUserAttributesClient;//B2B
    private Consent consent;
    private ApiResult<?> consentError;


    @Autowired
    public UserAttributesSteps(PnWebUserAttributesInternalClientImpl webUserAttributesClient) {
        this.webUserAttributesClient = webUserAttributesClient;
    }

    @Given("Viene richiesto l'ultimo consenso di tipo {string}")
    public void vieneRichiestoUltimoConsensoTipo(String type) {
        ConsentType consentType = null;
        switch (type) {
            case "TOS":
                consentType = ConsentType.TOS;
                break;
            case "DATAPRIVACY":
                consentType = ConsentType.DATAPRIVACY;
                break;
            default:
                throw new IllegalArgumentException();
        }

        ConsentType requestedType = consentType;
        ApiResult<Consent> result = ApiCallHelper.callBody(() -> this.webUserAttributesClient.getConsentByType(requestedType, null));
        if (result.is2xx()) {
            consent = result.body();
            System.out.println("CONSENT: " + consent);
        } else {
            this.consentError = result;
        }
    }

    @Then("Il recupero del consenso non ha prodotto errori")
    public void recuperoDelConsensoNonHaProdottoErrori() {
        Assertions.assertNull(consentError, () -> "Il recupero del consenso ha prodotto l'errore HTTP "
                + consentError.statusCode() + ": " + consentError.problem());
    }

    @And("Il consenso è accettato")
    public void ilConsensoAccettato() {
        Assertions.assertEquals(Boolean.TRUE, consent.getAccepted());
    }
}