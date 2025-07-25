package it.pagopa.pari.cucumber.steps.registrobeni;

import io.cucumber.java.en.Given;
import it.pagopa.pari.cucumber.domain.JWTUserData;
import it.pagopa.pari.cucumber.utils.ApiClientContext;
import it.pagopa.pari.utils.JWTUserRoleProvider;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class RDBCommonSteps {
    private final ApiClientContext apiClientContext;
    private final RestTemplate restTemplate;
    private final JWTUserRoleProvider JWTUserRoleProvider;
    private final Map<String, JWTUserData> jwtUserDataMap = Map.of(
            "PRODUTTORE_1", JWTUserData.builder().uid("99457865-8a65-467f-aeec-7ce9f71c361a").name("Giuseppe")
                    .familyName("Polignano").orgId("b5ae0b41-b854-414e-8295-078595ee1db4").orgRole("operatore").build(),
            "INVITALIA", JWTUserData.builder().uid("195da70f-d3f0-4c57-b62e-ef471348e920").name("Lorenzo")
                    .familyName("Lollo").orgId("b5ae0b41-b854-414e-8295-078595ee1da1").orgRole("invitalia").build()
    );

    public RDBCommonSteps(ApiClientContext apiClientContext, RestTemplate restTemplate, JWTUserRoleProvider JWTUserRoleProvider) {
        this.apiClientContext = apiClientContext;
        this.restTemplate = restTemplate;
        this.JWTUserRoleProvider = JWTUserRoleProvider;
    }

    @Given("vengono generati tutti i token JWT necessari")
    public void generateJWTToken() {
        String url = "https://api-io.dev.cstar.pagopa.it/idpay-itn/register/token/test";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        for (String role : jwtUserDataMap.keySet()) {
            HttpEntity<JWTUserData> requestBody = new HttpEntity<>(jwtUserDataMap.get(role), httpHeaders);
            String jwtToken = restTemplate.postForEntity(url, requestBody, String.class).getBody();
            JWTUserRoleProvider.storeJwt(role, jwtToken);
        }
    }

    @Given("viene usata l'utenza: {string}")
    public void setUser(String user) {
        apiClientContext.setBearerToken(user);
    }


}
