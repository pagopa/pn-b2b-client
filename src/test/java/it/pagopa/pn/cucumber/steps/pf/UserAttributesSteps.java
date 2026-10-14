package it.pagopa.pn.cucumber.steps.pf;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.common.http.HttpCallExecutor;
import it.pagopa.pn.client.b2b.pa.service.impl.PnWebUserAttributesInternalClientImpl;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaluserconsents.model.Consent;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaluserconsents.model.ConsentType;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

public class UserAttributesSteps {

    private final PnWebUserAttributesInternalClientImpl webUserAttributesClient;
    private final HttpCallExecutor httpCallExecutor;


    @Autowired
    public UserAttributesSteps(PnWebUserAttributesInternalClientImpl webUserAttributesClient, HttpCallExecutor httpCallExecutor) {
        this.webUserAttributesClient = webUserAttributesClient;
        this.httpCallExecutor = httpCallExecutor;
    }

    @Given("Viene richiesto l'ultimo consenso di tipo {string}")
    public void vieneRichiestoUltimoConsensoTipo(String type) {
        httpCallExecutor.callForEntity(() -> {
            ConsentType consentType = switch (type) {
                case "TOS" -> ConsentType.TOS;
                case "DATAPRIVACY" -> ConsentType.DATAPRIVACY;
                default -> throw new IllegalArgumentException("Tipo di consenso non supportato");
            };
            return webUserAttributesClient.getConsentByTypeWithHttpInfo(consentType, null);
        });
    }

    @Then("Il recupero del consenso non ha prodotto errori")
    public void recuperoDelConsensoNonHaProdottoErrori() {
        Assertions.assertTrue(httpCallExecutor.isSuccessful(),
                "Il recupero del consenso non ha prodotto un esito HTTP di successo");
    }

    @And("Il consenso è accettato")
    public void ilConsensoAccettato() {
        recuperoDelConsensoNonHaProdottoErrori();
        Consent consent = httpCallExecutor.getResponseBody(Consent.class);
        Assertions.assertEquals(Boolean.TRUE, consent.getAccepted());
    }
}
