package it.pagopa.pn.cucumber.steps.nationalRegistry;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;
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
    private String address;
    private String addressDetail;
    private static final String ASSERT_MSG_FORMAT = "Il campo %s calcolato con algoritmo %s per il taxId %s non coincide con quanto atteso";

    @Autowired
    public AdeguamentoAnprSteps(@Value("${pn.delivery.base-url}") String deliveryBaseUrl) {
        this.deliveryBaseUrl = deliveryBaseUrl;
    }

    @Given("viene interrogato nationalRegistry per il codice fiscale {string}")
    public void interrogateANPR(String taxId) throws IOException, InterruptedException {
        this.taxId = taxId;
        HttpClient client = HttpClient.newHttpClient();
        String requestBody = """
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
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        log.info("Request per interrogazione ANPR: {}", request);
        log.info("Body per interrogazione ANPR:\n{}", requestBody);

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 300) {
            throw new RuntimeException("Errore nella chiamata POST: " + response.statusCode() + " - " + response.body());
        }
        log.info("ANPR interrogato con successo.");
        address = JsonPath.read(response.body(), "$.residentialAddresses[0].address");
        addressDetail = JsonPath.read(response.body(), "$.residentialAddresses[0].addressDetail");
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


    private void checkAddressAlgorithmOld() {
        switch (taxId) {
            case "GNVGCM97E04L781N" -> SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("  ");
                softly.assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("2");
            });
            case "JNOFBN86B05L781H", "BLLBBR95D46L781R" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("");
            }
            case "PRZPLA89E02L781K", "LNNLNZ02L27L781Z", "QDRQMD99C20L781Y", "JRIJNN05A01L781M", "RZORNZ95C11L781S", "RGHLVC01H09H501K" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("Via Elena da Persico 12/A");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("");
            }
            case "BRNBNN92S02L781R" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo(" Elena da Persico A");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("5");
            }
            default -> throw new IllegalArgumentException("TaxId non riconosciuto: " + taxId);
        }
    }

    private void checkAddressAlgorithmMinimal() {
        switch (taxId) {
            case "GNVGCM97E04L781N" -> SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "MINIMAL", taxId)).isEqualTo("");
                softly.assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "MINIMAL", taxId)).isEqualTo("ROSSO Scala 2");
            });
            case "JNOFBN86B05L781H", "BLLBBR95D46L781R" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "MINIMAL", taxId)).isEqualTo("");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "MINIMAL", taxId)).isEqualTo("");
            }
            case "PRZPLA89E02L781K" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "MINIMAL", taxId)).isEqualTo("Via Elena da Persico 12/A KM 50");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "MINIMAL", taxId)).isEqualTo("");
            }
            case "BRNBNN92S02L781R" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "MINIMAL", taxId)).isEqualTo("Elena da Persico A SNC");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "MINIMAL", taxId)).isEqualTo("BLU Scala 5");
            }
            case "LNNLNZ02L27L781Z" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "MINIMAL", taxId)).isEqualTo("Via Elena da Persico 12/A");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "MINIMAL", taxId)).isEqualTo("");
            }
            case "QDRQMD99C20L781Y" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "MINIMAL", taxId)).isEqualTo("Via Elena da Persico 12/A");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "MINIMAL", taxId)).isEqualTo("BLU");
            }
            case "JRIJNN05A01L781M" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "MINIMAL", taxId)).isEqualTo("Via Elena da Persico 12/A");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "MINIMAL", taxId)).isEqualTo("Non res.");
            }
            case "RZORNZ95C11L781S" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "MINIMAL", taxId)).isEqualTo("Via Elena da Persico 12/A");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "MINIMAL", taxId)).isEqualTo("ROSSO");
            }
            case "RGHLVC01H09H501K" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "MINIMAL", taxId)).isEqualTo("Via Elena da Persico 12/A SNC");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "MINIMAL", taxId)).isEqualTo("Res.");
            }
            default -> throw new IllegalArgumentException("TaxId non riconosciuto: " + taxId);
        }
    }

    private void checkAddressAlgorithmFull() {
        switch (taxId) {
            case "GNVGCM97E04L781N" -> SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "FULL", taxId)).isEqualTo("");
                softly.assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "FULL", taxId)).isEqualTo("ROSSO Corte 1 Scala 2 Scala est. SCAL 2");
            });
            case "JNOFBN86B05L781H", "BLLBBR95D46L781R" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "FULL", taxId)).isEqualTo("");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "FULL", taxId)).isEqualTo("");
            }
            case "PRZPLA89E02L781K" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "FULL", taxId)).isEqualTo("Via Elena da Persico KM 50");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "FULL", taxId)).isEqualTo("");
            }
            case "BRNBNN92S02L781R" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "FULL", taxId)).isEqualTo("Elena da Persico A SNC");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "FULL", taxId)).isEqualTo("BLU Scala 5");
            }
            case "LNNLNZ02L27L781Z" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "FULL", taxId)).isEqualTo("Via Elena da Persico 12/A CAD");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "FULL", taxId)).isEqualTo("");
            }
            case "QDRQMD99C20L781Y" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "FULL", taxId)).isEqualTo("Via Elena da Persico 12/A");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "FULL", taxId)).isEqualTo("BLU Interno 5 A");
            }
            case "JRIJNN05A01L781M" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "FULL", taxId)).isEqualTo("Via Elena da Persico 12/A");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "FULL", taxId)).isEqualTo("Non res. Interno 42 D");
            }
            case "RZORNZ95C11L781S" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "FULL", taxId)).isEqualTo("Via Elena da Persico 12/A");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "FULL", taxId)).isEqualTo("ROSSO Primo interno 5 A Secondo interno 42 D");
            }
            case "RGHLVC01H09H501K" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "FULL", taxId)).isEqualTo("Via Elena da Persico 12/A SNC");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "FULL", taxId)).isEqualTo("Res. Isolato 33");
            }
            default -> throw new IllegalArgumentException("TaxId non riconosciuto: " + taxId);
        }
    }
}
