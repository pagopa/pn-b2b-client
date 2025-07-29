package it.pagopa.pari.cucumber.steps.registrobeni;

import io.cucumber.java.en.Given;
import it.pagopa.pari.cucumber.domain.JWTUserData;
import it.pagopa.pari.cucumber.domain.JWTUserDataRegistry;
import it.pagopa.pari.cucumber.utils.ApiClientContext;
import it.pagopa.pari.utils.JWTUserRoleProvider;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class RDBCommonSteps {
    private final ApiClientContext apiClientContext;
    private final RestTemplate restTemplate;
    private final JWTUserRoleProvider JWTUserRoleProvider;
    private final JWTUserDataRegistry jwtUserDataRegistry;

    public RDBCommonSteps(ApiClientContext apiClientContext, RestTemplate restTemplate, JWTUserRoleProvider jwtUserRoleProvider,
                          JWTUserDataRegistry jwtUserDataRegistry) {
        this.apiClientContext = apiClientContext;
        this.restTemplate = restTemplate;
        this.JWTUserRoleProvider = jwtUserRoleProvider;
        this.jwtUserDataRegistry = jwtUserDataRegistry;
    }

    @Given("vengono generati tutti i token JWT necessari")
    public void generateJWTToken() {
        String url = "https://api-io.dev.cstar.pagopa.it/idpay-itn/register/token/test";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        for (String role : jwtUserDataRegistry.getAll().keySet()) {
            HttpEntity<JWTUserData> requestBody = new HttpEntity<>(jwtUserDataRegistry.getUserData(role), httpHeaders);
            String jwtToken = restTemplate.postForEntity(url, requestBody, String.class).getBody();
            JWTUserRoleProvider.storeJwt(role, jwtToken);
        }
    }

    @Given("viene usata l'utenza: {string}")
    public void setUser(String user) {
        apiClientContext.setBearerToken(user);
    }


}
