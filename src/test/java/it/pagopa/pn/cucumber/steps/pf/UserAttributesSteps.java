package it.pagopa.pn.cucumber.steps.pf;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.testclient.IPnWebUserAttributesClient;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.consents.model.Consent;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.consents.model.ConsentType;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpStatusCodeException;

public class UserAttributesSteps {

    private final IPnWebUserAttributesClient webUserAttributesClient;

    private Consent consent;
    private HttpStatusCodeException consentError;



    @Autowired
    public UserAttributesSteps(IPnWebUserAttributesClient webUserAttributesClient) {
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

        try {
            consent = this.webUserAttributesClient.getConsentByType(consentType, null);
            System.out.println("CONSENT: " + consent);
        } catch (HttpStatusCodeException e) {
            this.consentError = e;
        }
    }

    @Then("Il recupero del consenso non ha prodotto errori")
    public void recuperoDelConsensoNonHaProdottoErrori() {
        Assertions.assertNull(consentError);
    }

    @And("Il consenso è accettato")
    public void ilConsensoAccettato() {
        Assertions.assertTrue(consent.getAccepted());
    }

 

    @Given("viene testato l'otp")
    public void vieneTestatoLOtp() {
    }
}
