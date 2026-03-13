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
                softly.assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("");
                softly.assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("ROSSO Scala 2");
            });
            case "MRNMRZ04D07L781J" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("TODO2");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("TODO");
            }
            case "VNNVNN99T16L781L" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("TODO3");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("TODO");
            }
            case "PRZPLA89E02L781K" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("TODO4");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("TODO");
            }
            case "BRNBNN92S02L781R" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("TODO5");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("TODO");
            }
            case "LNNLNZ02L27L781Z" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("TODO6");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("TODO");
            }
            case "QDRQMD99C20L781Y" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("TODO7");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("TODO");
            }
            case "JRIJNN05A01L781M" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("TODO8");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("TODO");
            }
            case "RZORNZ95C11L781S" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("TODO9");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("TODO");
            }
            case "RGHLVC01H09H501K" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("TODO10");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("TODO");
            }
            default -> throw new IllegalArgumentException("TaxId non riconosciuto: " + taxId);
        }
    }

    private void checkAddressAlgorithmMinimal() {
        switch (taxId) {
            case "GNVGCM97E04L781N" -> SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("");
                softly.assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("ROSSO Scala 2");
            });
            case "MRNMRZ04D07L781J" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("Via Elena da Persico");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("");
            }
            case "VNNVNN99T16L781L" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("");
            }
            case "PRZPLA89E02L781K" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("Via Elena da Persico 12/A KM 50");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("");
            }
            case "BRNBNN92S02L781R" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("Elena da Persico A SNC");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("BLU Scala 5");
            }
            case "LNNLNZ02L27L781Z" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("Via Elena da Persico 12/A");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("");
            }
            case "QDRQMD99C20L781Y" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("Via Elena da Persico 12/A");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("BLU");
            }
            case "JRIJNN05A01L781M" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("Via Elena da Persico 12/A");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("Non res.");
            }
            case "RZORNZ95C11L781S" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("Via Elena da Persico 12/A");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("ROSSO");
            }
            case "RGHLVC01H09H501K" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("Via Elena da Persico 12/A SNC");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("Res.");
            }
            default -> throw new IllegalArgumentException("TaxId non riconosciuto: " + taxId);
        }
    }

    private void checkAddressAlgorithmFull() {
        switch (taxId) {
            case "GNVGCM97E04L781N" -> SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("");
                softly.assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("ROSSO Scala 2");
            });
            case "MRNMRZ04D07L781J" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("TODO2");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("TODO");
            }
            case "VNNVNN99T16L781L" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("TODO3");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("TODO");
            }
            case "PRZPLA89E02L781K" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("TODO4");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("TODO");
            }
            case "BRNBNN92S02L781R" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("TODO5");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("TODO");
            }
            case "LNNLNZ02L27L781Z" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("TODO6");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("TODO");
            }
            case "QDRQMD99C20L781Y" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("TODO7");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("TODO");
            }
            case "JRIJNN05A01L781M" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("TODO8");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("TODO");
            }
            case "RZORNZ95C11L781S" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("TODO9");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("TODO");
            }
            case "RGHLVC01H09H501K" -> {
                assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", "OLD", taxId)).isEqualTo("TODO10");
                assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", "OLD", taxId)).isEqualTo("TODO");
            }
            default -> throw new IllegalArgumentException("TaxId non riconosciuto: " + taxId);
        }
    }
}
