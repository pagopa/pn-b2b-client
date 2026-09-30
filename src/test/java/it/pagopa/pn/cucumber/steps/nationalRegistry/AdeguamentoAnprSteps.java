package it.pagopa.pn.cucumber.steps.nationalRegistry;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import lombok.AllArgsConstructor;
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
import java.util.Map;

import static java.util.Map.entry;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
public class AdeguamentoAnprSteps {
    private final String deliveryBaseUrl;
    private String taxId;
    private String anprResponseBody;
    private String address;
    private String addressDetail;
    private static final String ASSERT_MSG_FORMAT = "Il campo %s calcolato con algoritmo %s per il taxId %s non coincide con quanto atteso";

    enum AnprAlgorithm {
        OLD, MINIMAL, FULL
    }

    @ParameterType("OLD|MINIMAL|FULL")
    public static AnprAlgorithm anprAlgorithm(String value) {
        return AnprAlgorithm.valueOf(value);
    }

    @AllArgsConstructor
    private static class ExpectedAnprOutput {
        private String address;
        private String addressDetails;
    }

    private static final Map<String, Map<AnprAlgorithm, ExpectedAnprOutput>> ANPR_ADDRESS_MAP = Map.ofEntries(
            //CF per ambienti inferiori a UAT censiti sul Mock NR
            entry("GNVGCM97E04L781N", Map.of(
                    AnprAlgorithm.OLD, new ExpectedAnprOutput("  ", "2"),
                    AnprAlgorithm.MINIMAL, new ExpectedAnprOutput("", "ROSSO Scala 2 Scala est. SCAL 2"),
                    AnprAlgorithm.FULL, new ExpectedAnprOutput("", "ROSSO Corte 1 Scala 2 Scala est. SCAL 2"))),
            entry("JNOFBN86B05L781H", Map.of(
                    AnprAlgorithm.OLD, new ExpectedAnprOutput("", ""),
                    AnprAlgorithm.MINIMAL, new ExpectedAnprOutput("", ""),
                    AnprAlgorithm.FULL, new ExpectedAnprOutput("", ""))),
            entry("BLLBBR95D46L781R", Map.of(
                    AnprAlgorithm.OLD, new ExpectedAnprOutput("", ""),
                    AnprAlgorithm.MINIMAL, new ExpectedAnprOutput("", ""),
                    AnprAlgorithm.FULL, new ExpectedAnprOutput("", ""))),
            entry("PRZPLA89E02L781K", Map.of(
                    AnprAlgorithm.OLD, new ExpectedAnprOutput("Via Elena da Persico 12/A", ""),
                    AnprAlgorithm.MINIMAL, new ExpectedAnprOutput("Via Elena da Persico 12/A KM 50", ""),
                    AnprAlgorithm.FULL, new ExpectedAnprOutput("Via Elena da Persico KM 50", ""))),
            entry("BRNBNN92S02L781R", Map.of(
                    AnprAlgorithm.OLD, new ExpectedAnprOutput(" Elena da Persico A", "5"),
                    AnprAlgorithm.MINIMAL, new ExpectedAnprOutput("Elena da Persico A SNC", "BLU Scala 5"),
                    AnprAlgorithm.FULL, new ExpectedAnprOutput("Elena da Persico A SNC", "BLU Scala 5"))),
            entry("LNNLNZ02L27L781Z", Map.of(
                    AnprAlgorithm.OLD, new ExpectedAnprOutput("Via Elena da Persico 12/A", ""),
                    AnprAlgorithm.MINIMAL, new ExpectedAnprOutput("Via Elena da Persico 12/A", ""),
                    AnprAlgorithm.FULL, new ExpectedAnprOutput("Via Elena da Persico 12/A CAD", ""))),
            entry("QDRQMD99C20L781Y", Map.of(
                    AnprAlgorithm.OLD, new ExpectedAnprOutput("Via Elena da Persico 12/A", ""),
                    AnprAlgorithm.MINIMAL, new ExpectedAnprOutput("Via Elena da Persico 12/A", "BLU"),
                    AnprAlgorithm.FULL, new ExpectedAnprOutput("Via Elena da Persico 12/A", "BLU Interno 5 A"))),
            entry("JRIJNN05A01L781M", Map.of(
                    AnprAlgorithm.OLD, new ExpectedAnprOutput("Via Elena da Persico 12/A", ""),
                    AnprAlgorithm.MINIMAL, new ExpectedAnprOutput("Via Elena da Persico 12/A", "Non res."),
                    AnprAlgorithm.FULL, new ExpectedAnprOutput("Via Elena da Persico 12/A", "Non res. Interno 42 D"))),
            entry("RZORNZ95C11L781S", Map.of(
                    AnprAlgorithm.OLD, new ExpectedAnprOutput("Via Elena da Persico 12/A", ""),
                    AnprAlgorithm.MINIMAL, new ExpectedAnprOutput("Via Elena da Persico 12/A", "ROSSO"),
                    AnprAlgorithm.FULL, new ExpectedAnprOutput("Via Elena da Persico 12/A", "ROSSO Primo interno 5 A Secondo interno 42 D"))),
            entry("RGHLVC01H09H501K", Map.of(
                    AnprAlgorithm.OLD, new ExpectedAnprOutput("Via Elena da Persico 12/A", ""),
                    AnprAlgorithm.MINIMAL, new ExpectedAnprOutput("Via Elena da Persico 12/A SNC", "Res."),
                    AnprAlgorithm.FULL, new ExpectedAnprOutput("Via Elena da Persico 12/A SNC", "Res. Isolato 33"))),
            //CF per ambiente UAT censiti su Real NR
            entry("VRDLSM78B02F839R", Map.of(
                    AnprAlgorithm.OLD, new ExpectedAnprOutput(" CAVOUR 1", ""),
                    AnprAlgorithm.MINIMAL, new ExpectedAnprOutput("CAVOUR 1 SNC", "ROSSO Scala est. 3"),
                    AnprAlgorithm.FULL, new ExpectedAnprOutput("CAVOUR 1 SNC", "ROSSO Scala est. 3 Interno 4 Isolato 7"))),
            entry("RSSMSM85E15H501L", Map.of(
                    AnprAlgorithm.OLD, new ExpectedAnprOutput("VIA Po A", "2"),
                    AnprAlgorithm.MINIMAL, new ExpectedAnprOutput("VIA Po KM 100", "BLU Scala 2 Scala est. 3"),
                    AnprAlgorithm.FULL, new ExpectedAnprOutput("VIA Po KM 100", "BLU Corte 1 Scala 2 Scala est. 3 Isolato 6"))),
            entry("KPRSMP91H12F205O", Map.of(
                    AnprAlgorithm.OLD, new ExpectedAnprOutput("VIA Via Elena da Persico 12/A", "1"),
                    AnprAlgorithm.MINIMAL, new ExpectedAnprOutput("VIA Via Elena da Persico 12/A SNC", "Res. Scala 1 Scala est. 1"),
                    AnprAlgorithm.FULL, new ExpectedAnprOutput("VIA Via Elena da Persico 12/A SNC", "Res. Corte 1 Scala 1 Scala est. 1 1 D"))),
            entry("KRSJSM88S03H501A", Map.of(
                    AnprAlgorithm.OLD, new ExpectedAnprOutput(" SOLO TOPONIMO ", ""),
                    AnprAlgorithm.MINIMAL, new ExpectedAnprOutput("SOLO TOPONIMO", ""),
                    AnprAlgorithm.FULL, new ExpectedAnprOutput("SOLO TOPONIMO", ""))),
            entry("BSMGPR92R62F205X", Map.of(
                    AnprAlgorithm.OLD, new ExpectedAnprOutput("Via Fiume 1/A", ""),
                    AnprAlgorithm.MINIMAL, new ExpectedAnprOutput("Via Fiume 1/A SNC", "ROSSO Scala est. 3"),
                    AnprAlgorithm.FULL, new ExpectedAnprOutput("Via Fiume 1/A SNC", "ROSSO Scala est. 3 Interno 4 Isolato 6")))
    );

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

    @Then("si verifica che l'indirizzo sia correttamente formattato secondo le logiche dell'algoritmo {anprAlgorithm}")
    public void checkAddress(AnprAlgorithm algorithm) {
        assertThat(anprResponseBody).as("La response ottenuta da ANPR non dev'essere null").isNotNull();

        ExpectedAnprOutput expectedOutput = ANPR_ADDRESS_MAP.get(taxId).get(algorithm);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(address).as(String.format(ASSERT_MSG_FORMAT, "address", algorithm, taxId)).isEqualTo(expectedOutput.address);
            softly.assertThat(addressDetail).as(String.format(ASSERT_MSG_FORMAT, "addressDetail", algorithm, taxId)).isEqualTo(expectedOutput.addressDetails);
        });
    }
}
