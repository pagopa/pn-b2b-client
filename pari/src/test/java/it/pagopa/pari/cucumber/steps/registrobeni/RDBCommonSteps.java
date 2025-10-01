package it.pagopa.pari.cucumber.steps.registrobeni;

import io.cucumber.java.en.Given;
import it.pagopa.pari.cucumber.domain.JWTUserData;
import it.pagopa.pari.cucumber.domain.JWTUserDataRegistry;
import it.pagopa.pari.cucumber.utils.ApiClientContext;
import it.pagopa.pari.cucumber.utils.SharedCommonContext;
import it.pagopa.pari.registrobeni.domain.RdbRole;
import it.pagopa.pari.utils.RdBJWTProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

public class RDBCommonSteps {
    private final String baseUrl;
    private final ApiClientContext apiClientContext;
    private final RestTemplate restTemplate;
    private final RdBJWTProvider jWTUserRoleProvider;
    private final JWTUserDataRegistry jwtUserDataRegistry;
    private final SharedCommonContext sharedCommonContext;

    public RDBCommonSteps(ApiClientContext apiClientContext, RestTemplate restTemplate, RdBJWTProvider rdBJWTProvider,
                          JWTUserDataRegistry jwtUserDataRegistry, @Value("${rdb.base-url}") String baseUrl,
                          SharedCommonContext sharedCommonContext) {
        this.baseUrl = baseUrl;
        this.apiClientContext = apiClientContext;
        this.restTemplate = restTemplate;
        this.jWTUserRoleProvider = rdBJWTProvider;
        this.jwtUserDataRegistry = jwtUserDataRegistry;
        this.sharedCommonContext = sharedCommonContext;
    }

    @Given("vengono generati tutti i token JWT necessari")
    public void generateJWTToken() {
        URI uri = URI.create(baseUrl + "/token/test");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        for (RdbRole role : jwtUserDataRegistry.getAll().keySet()) {
            HttpEntity<JWTUserData> requestBody = new HttpEntity<>(jwtUserDataRegistry.getUserData(role), httpHeaders);
            String jwtToken = restTemplate.postForEntity(uri, requestBody, String.class).getBody();
            jWTUserRoleProvider.storeJwt(role, jwtToken);
        }
    }

    @Given("viene usata l'utenza: {rdbRole}")
    public void setUser(RdbRole user) {
        sharedCommonContext.setUserData(jwtUserDataRegistry.getUserData(user));
        apiClientContext.setBearerToken(user);
    }


}
