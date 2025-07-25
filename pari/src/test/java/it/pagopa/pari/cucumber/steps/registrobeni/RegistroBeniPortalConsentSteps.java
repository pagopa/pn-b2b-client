package it.pagopa.pari.cucumber.steps.registrobeni;

import io.cucumber.java.en.Given;
import it.pagopa.pari.cucumber.utils.ApiClientContext;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.PortalConsentDTO;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

public class RegistroBeniPortalConsentSteps {
    private final ApiClientContext apiClientContext;
    private final RestTemplate restTemplate;

    public RegistroBeniPortalConsentSteps(ApiClientContext apiClientContext, RestTemplate restTemplate) {
        this.apiClientContext = apiClientContext;
        this.restTemplate = restTemplate;
    }

    @Given("viene rimossa l'accettazione dei TOS per l'utente: {string}")
    public void removeToSForUser(String userId) throws URISyntaxException {
        URI uri = new URIBuilder("https://idpay.itn.internal.dev.cstar.pagopa.it/idpayassetregisterbackend/idpay/consent")
                .addParameter("userId", userId)
                .build();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        restTemplate.delete(uri);
    }

    @Given("l'utente {string} i TOS")
    public void userHandleTos(String action) {
        PortalConsentDTO portalConsentApi = new PortalConsentDTO();
        portalConsentApi.setFirstAcceptance(false);
//        portalConsentApi.setVersionId("1");
        apiClientContext.getRegisterPortalOperationClient().savePortalConsent(portalConsentApi);
    }
}
