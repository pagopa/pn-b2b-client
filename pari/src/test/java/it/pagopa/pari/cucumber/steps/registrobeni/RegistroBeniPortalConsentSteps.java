package it.pagopa.pari.cucumber.steps.registrobeni;

import io.cucumber.java.en.Given;
import it.pagopa.pari.cucumber.steps.registrobeni.StepParameterTypes.ConsentAction;
import it.pagopa.pari.cucumber.domain.JWTUserDataRegistry;
import it.pagopa.pari.cucumber.utils.ApiClientContext;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.PortalConsentDTO;
import it.pagopa.pari.registrobeni.domain.RdbRole;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@Slf4j
public class RegistroBeniPortalConsentSteps {
    private final ApiClientContext apiClientContext;
    private final RestTemplate restTemplate;
    private final JWTUserDataRegistry jwtUserDataRegistry;

    public RegistroBeniPortalConsentSteps(ApiClientContext apiClientContext, RestTemplate restTemplate, JWTUserDataRegistry jwtUserDataRegistry) {
        this.apiClientContext = apiClientContext;
        this.restTemplate = restTemplate;
        this.jwtUserDataRegistry = jwtUserDataRegistry;
    }

    @Given("viene rimossa l'accettazione dei ToS per l'utente: {rdbRole}")
    public void removeToSForUser(RdbRole user) throws URISyntaxException {
        URI uri = new URIBuilder("https://idpay.itn.internal.dev.cstar.pagopa.it/idpayassetregisterbackend/idpay/consent")
                .addParameter("userId", jwtUserDataRegistry.getUserData(user).getUid())
                .build();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> requestEntity = new HttpEntity<>(httpHeaders);
        try {
            restTemplate.exchange(uri, HttpMethod.DELETE, requestEntity, Void.class);
        } catch (HttpStatusCodeException e) {
            log.info("I ToS sono stati già cancellati per l'utenza desiderata!");
        }
    }

    @Given("l'utente accetta i ToS con successo")
    public void userHandleTos() {
        PortalConsentDTO portalConsentApi = new PortalConsentDTO();
        portalConsentApi.setFirstAcceptance(true);
        portalConsentApi.setVersionId("mock-version-id");
        Assertions.assertDoesNotThrow(() -> apiClientContext.getRegisterPortalOperationClient().savePortalConsent(portalConsentApi));
    }

    @Given("si verifica che i ToS {consentAction} stati accettati")
    public void getUserToS(ConsentAction consentAction) {
        PortalConsentDTO portalConsentDTO = apiClientContext.getRegisterPortalOperationClient().getConsent();
        assertNotNull(portalConsentDTO);
        if (ConsentAction.NON_SONO.equals(consentAction)) {
            assertEquals(true, portalConsentDTO.getFirstAcceptance());
            assertNotNull(portalConsentDTO.getVersionId());
        } else {
            assertNull(portalConsentDTO.getFirstAcceptance());
            assertNull(portalConsentDTO.getVersionId());
        }
    }


}
