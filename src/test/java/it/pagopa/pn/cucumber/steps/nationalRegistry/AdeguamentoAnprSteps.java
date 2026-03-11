package it.pagopa.pn.cucumber.steps.nationalRegistry;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
public class AdeguamentoAnprSteps {
    private final String deliveryBaseUrl;
    private String taxId;
    private String anprResponseBody;

    @Autowired
    public AdeguamentoAnprSteps(@Value("${pn.delivery.base-url}") String deliveryBaseUrl) {
        this.deliveryBaseUrl = deliveryBaseUrl;
    }

    @Given("viene interrogato nationalRegistry per il codice fiscale {string}")
    public void interrogateANPR(String taxId) throws IOException, InterruptedException {
        this.taxId = taxId;
        HttpClient client = HttpClient.newHttpClient();
        String jsonBody = """
                {
                    "filter": {
                        "taxId": "%s",
                        "requestReason": "123",
                        "referenceRequestDate": "%s"
                    }
                }
                """.formatted(taxId, LocalDate.now().toString());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(deliveryBaseUrl + "/national-registries-private/anpr/address"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        log.info("Request per interrogazione ANPR: {}", request);
        log.info("Body per interrogazione ANPR:\n{}", jsonBody);

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 300) {
            throw new RuntimeException("Errore nella chiamata POST: " + response.statusCode() + " - " + response.body());
        }
        log.info("ANPR interrogato con successo.");
        anprResponseBody = B2bUtils.logPrettyResponse(response.body());
    }

    @Then("si verifica che l'indirizzo sia correttamente formattato secondo le logiche dell'algoritmo {string}")
    public void checkAddress(String algoritmo) {
        assertThat(anprResponseBody).as("La response ottenuta da ANPR non dev'essere null").isNotNull();
        switch (algoritmo.toUpperCase()) {
            case "OLD" -> checkAddressAlgorithmOld();
            case "MINIMAL" -> checkAddressAlgorithmMinimal();
            case "FULL" -> checkAddressAlgorithmFull();
            default -> throw new IllegalArgumentException("Invalid algorithm type: " + algoritmo);
        }
    }

    private void checkAddressAlgorithmFull() {
    }

    private void checkAddressAlgorithmMinimal() {
    }

    private void checkAddressAlgorithmOld() {
    }
}
