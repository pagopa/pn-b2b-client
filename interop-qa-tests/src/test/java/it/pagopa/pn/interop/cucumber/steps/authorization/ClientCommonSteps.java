package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.Then;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeAdditionDetailsSeed;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Getter
@Setter
public class ClientCommonSteps {
    private HttpStatus clientResponse = HttpStatus.OK;
    private Object response;
    private List<UUID> clients;
    private String clientPublicKey;
    private PurposeAdditionDetailsSeed purposeId;

    @Then("si ottiene status code {string}")
    public void verifyStatusCode(String statusCode) {
        Assertions.assertEquals(statusCode, clientResponse.toString());
    }

    public <T> void performCall(Supplier<T> promise) {
        try {
            response = promise.get();
        } catch (HttpClientErrorException e) {
            clientResponse = HttpStatus.FORBIDDEN;
        }
    }

    public HttpStatus performCall(Runnable promise) {
        try {
            promise.run();
        } catch (HttpClientErrorException e) {
            clientResponse = HttpStatus.FORBIDDEN;
        }
        return clientResponse;
    }


}
