package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.service.impl.PnRaddCapCoverageClientImpl;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableAuthTokenRaddCognito;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddcoverage.model.Coverage;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddcoverage.model.CreateCoverageRequest;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddcoverage.model.UpdateCoverageRequest;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.privateb2braddalt.model.CheckCoverageRequest;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.privateb2braddalt.model.CheckCoverageResponse;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.privateb2braddalt.model.SearchMode;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.dataTable.DataTableTypeRaddAlt;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@Slf4j
public class CoperturaCapRaddSteps {

    private final SharedSteps sharedSteps;
    private final PnRaddCapCoverageClientImpl raddCapCoverageClient;
    private final SettableAuthTokenRaddCognito settableAuthTokenRaddCognito;

    private String cap;
    private String locality;
    private String randomLocality;

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int LENGTH = 6;
    private static final Random RANDOM = new Random();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private CreateCoverageRequest request;
    private ResponseEntity<Coverage> response;
    private Coverage responseCoverage;
    private CheckCoverageRequest checkCoverageRequest;
    private UpdateCoverageRequest updateRequest;
    private CheckCoverageResponse checkCoverageResponse;

    private Map<String, Boolean> coverageMap = new HashMap<>();

    @Autowired
    public CoperturaCapRaddSteps(PnRaddCapCoverageClientImpl raddCapCoverageClient, SharedSteps sharedSteps, DataTableTypeRaddAlt dataTableTypeRaddAlt, SettableAuthTokenRaddCognito settableAuthTokenRaddCognito) {

        this.raddCapCoverageClient = raddCapCoverageClient;
        this.sharedSteps = sharedSteps;
        this.settableAuthTokenRaddCognito = settableAuthTokenRaddCognito;
    }


    /*
     * "LETTURA_SCRITTURA" = usa credenziali user1, "SOLO_LETTURA" = usa credenziali user2
     * */
    @Given("Effettuo l'autenticazione copertura cap per l' utente con permessi: {string}")
    public void getSpecificTokenCognitoCoverageRadd(String permessi) {

        String token = settableAuthTokenRaddCognito.generateToken(permessi);
        raddCapCoverageClient.selectRaddista(token);
    }

    @Given("setto i dati per creare una nuova copertura Radd con locality random:")
    public void newCoverageRequestRandomLocality(DataTable dataTable) {
        this.randomLocality = generateRandomWord();
        Map<String, String> data = dataTable.asMaps().get(0);
        request = new CreateCoverageRequest()
                .cap(toNullable(data.get("cap")))
                .locality(toNullable(randomLocality))
                .cadastralCode(toNullable(data.get("cadastralCode")))
                .province(toNullable(data.get("province")));
    }

    @Given("setto i dati per creare una nuova copertura Radd:")
    public void newCoverageRequest(DataTable dataTable) {

        Map<String, String> data = dataTable.asMaps().get(0);
        request = new CreateCoverageRequest()
                .cap(toNullable(data.get("cap")))
                .locality(toNullable(data.get("locality")))
                .cadastralCode(toNullable(data.get("cadastralCode")))
                .province(toNullable(data.get("province")));
    }

    public static String generateRandomWord() {
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            int index = RANDOM.nextInt(ALPHABET.length());
            sb.append(ALPHABET.charAt(index));
        }
        return sb.toString();
    }

    @Then("la response deve contenere la località e il cap {string} attesi")
    public void la_risposta_deve_contenere_localita(String expectedCap) {
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(randomLocality, response.getBody().getLocality());
        assertEquals(expectedCap, response.getBody().getCap());
    }

    private String toNullable(String value) {
        if (value == null) return null;
        value = value.trim();
        if (value.isEmpty() || value.equalsIgnoreCase("null")) {
            return null;
        }
        return value;
    }

    @Then("l'operazione di copertura Radd ha prodotto un errore con status code {string}")
    public void operationProducedAnError(String statusCode) {
        HttpStatusCodeException httpStatusCodeException = this.sharedSteps.consumeNotificationError();
        assertThat(httpStatusCodeException)
                .as("L'eccezione httpStatusCodeException non dovrebbe essere nulla")
                .isNotNull();

        assertThat(httpStatusCodeException.getStatusCode().toString().substring(0, 3))
                .as("Il codice di stato HTTP non corrisponde a quello atteso", statusCode, httpStatusCodeException.getStatusCode().toString().substring(0, 3))
                .isEqualTo(statusCode);
    }

    @Then("setto i dati per aggiornare una copertura Radd:")
    public void setUpdateCoverageRequest(DataTable dataTable) {
        Map<String, String> data = dataTable.asMaps().get(0);

        this.cap = data.get("cap");

        if (data.get("locality").equalsIgnoreCase("/"))
            this.locality = this.randomLocality;
        else
            this.locality = toNullable(data.get("locality"));

        updateRequest = new UpdateCoverageRequest()
                .cadastralCode(toNullable(data.get("cadastralCode")))
                .province(toNullable(data.get("province")))
                .startValidity(toNullable(data.get("startValidity")) != null
                        ? toNullable(data.get("startValidity")).toString()
                        : null)
                .endValidity(toNullable(data.get("endValidity")) != null
                        ? toNullable(data.get("endValidity")).toString()
                        : null);

    }

    @Then("creo una nuova copertura Radd")
    public void newCoverage() {
        Assertions.assertDoesNotThrow(() -> {
            response = raddCapCoverageClient.addCoverageWithHttpInfo(request);
            assertNotNull(response, "La response non deve essere null");
            assertTrue(response.getStatusCode().is2xxSuccessful(),
                    "La chiamata non ha restituito un codice 2xx. Codice ricevuto: " + response.getStatusCodeValue());
        }, "Errore durante l'aggiornamento della copertura Radd");
    }


    @And("invoco l'API di aggiornamento copertura cap Radd")
    public void invokeUpdateCoverageApi() {
        Assertions.assertDoesNotThrow(() -> {

            responseCoverage = raddCapCoverageClient.updateCoverage(cap, locality, updateRequest);

            assertNotNull(response, "La response non deve essere null");
            assertTrue(response.getStatusCode().is2xxSuccessful(),
                    "La chiamata non ha restituito un codice 2xx. Codice ricevuto: " + response.getStatusCodeValue());

        }, "Errore durante l'aggiornamento della copertura Radd");
    }

    @And("invoco l'API di aggiornamento copertura cap Radd con errore")
    public void invokeUpdateCoverageApiError() {
        try {
            System.out.println("Request JSON per updateCoverage: " + updateRequest);
            response = raddCapCoverageClient.updateCoverageWithHttpInfo(cap, locality, updateRequest);

        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @Then("creo una nuova copertura Radd con Errore")
    public void newCoverageError() {
        try {
            response = raddCapCoverageClient.addCoverageWithHttpInfo(request);

        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @Then("la risposta deve contenere la località aggiornata {string}")
    public void verifyUpdatedLocality(String expectedLocality) {
        assertNotNull(response.getBody(), "Il body della response non deve essere null");
        assertEquals(expectedLocality, response.getBody().getLocality(),
                "La località nella response non corrisponde a quella attesa");
    }


    @Given("setto i dati per verificare la copertura Radd:")
    public void setCheckCoverageRequest(DataTable dataTable) {
        Map<String, String> data = dataTable.asMaps().get(0);

        String city;
        String nameRow2 = toNullable(data.get("nameRow2"));
        String addressRow = toNullable(data.get("addressRow"));
        String addressRow2 = toNullable(data.get("addressRow2"));
        String cap = toNullable(data.get("cap"));
        String city2 = toNullable(data.get("city2"));
        String pr = toNullable(data.get("pr"));
        String country = toNullable(data.get("country"));

        if (data.get("city").equalsIgnoreCase("/"))
            city = this.locality;
        else
            city = toNullable(data.get("city"));

        checkCoverageRequest = new CheckCoverageRequest()
                .nameRow2(nameRow2)
                .addressRow(addressRow)
                .addressRow2(addressRow2)
                .cap(cap)
                .city(city)
                .city2(city2)
                .pr(pr)
                .country(country);
    }

    @And("invoco l'API di verifica copertura cap Radd Complete mode con errore")
    public void invokeVerifyCoverageApiCError() {
        try {
            checkCoverageResponse = raddCapCoverageClient.checkCoverage(SearchMode.COMPLETE, checkCoverageRequest);

        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @Then("invoco l'API di verifica copertura cap Radd Complete mode")
    public void invokeVerifyCoverageApiC() {
        Assertions.assertDoesNotThrow(() -> {
            checkCoverageResponse = raddCapCoverageClient.checkCoverage(SearchMode.COMPLETE, checkCoverageRequest);
            assertNotNull(checkCoverageResponse, "La response non deve essere null");
//            assertTrue(checkCoverageResponse.getStatusCode().is2xxSuccessful(),
//                    "La chiamata non ha restituito un codice 2xx. Codice ricevuto: " + response.getStatusCodeValue());
        }, "Errore durante la verifica della copertura Radd");
    }

    @And("invoco l'API di verifica copertura cap Radd Light mode con errore")
    public void invokeVerifyCoverageApiLError() {
        try {
            checkCoverageResponse = raddCapCoverageClient.checkCoverage(SearchMode.LIGHT, checkCoverageRequest);

        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @And("invoco l'API di verifica copertura cap Radd mode: NULL con errore")
    public void invokeVerifyCoverageApiLModeError() {
        try {
            checkCoverageResponse = raddCapCoverageClient.checkCoverage(null, checkCoverageRequest);

        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @Then("invoco l'API di verifica copertura cap Radd Light mode")
    public void invokeVerifyCoverageApiL() {
        Assertions.assertDoesNotThrow(() -> {
            checkCoverageResponse = raddCapCoverageClient.checkCoverage(SearchMode.LIGHT, checkCoverageRequest);
            assertNotNull(checkCoverageResponse, "La response non deve essere null");
//            assertTrue(checkCoverageResponse.getStatusCode().is2xxSuccessful(),
//                    "La chiamata non ha restituito un codice 2xx. Codice ricevuto: " + response.getStatusCodeValue());
        }, "Errore durante l'aggiornamento della copertura Radd");
    }

    @And("per i dati forniti si verifica che lo stato di copertura sia {string}")
    public void isCoverage(String expectedCoverageStatus) {
        try {
            boolean actualCoverage = checkCoverageResponse.getHasCoverage();

            if ("COPERTO".equalsIgnoreCase(expectedCoverageStatus)) {
                assertTrue(actualCoverage,
                        "Atteso stato COPERTO, ma la risposta indica NON_COPERTO (hasCoverage = false).");
            } else if ("NON_COPERTO".equalsIgnoreCase(expectedCoverageStatus)) {
                assertFalse(actualCoverage,
                        "Atteso stato NON_COPERTO, ma la risposta indica COPERTO (hasCoverage = true).");
            } else {
                fail("Valore non valido per lo stato di copertura: '" + expectedCoverageStatus +
                        "'. Usa 'COPERTO' o 'NON_COPERTO'.");
            }
        } catch (Exception e) {
            fail("Errore inatteso durante la verifica dello stato di copertura: " + e.getMessage());
        }

    }

    @Given("leggo il file csv e calcolo la copertura attuale")
    public void getCoverageFromFile() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/TEST-cop-cap-radd.csv"))) {
            String line;
            boolean isHeader = true;

            DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
            OffsetDateTime now = OffsetDateTime.now();

            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (values.length < 4) continue;

                String configKey = values[0].replace("\"", "").trim();
                String startValidityStr = values[1].replace("\"", "").trim();
                String endValidityStr = values[3].replace("\"", "").trim();

                String zip = configKey.startsWith("ZIP##") ? configKey.substring(5) : configKey;

                try {
                    OffsetDateTime start = OffsetDateTime.parse(startValidityStr, formatter);
                    OffsetDateTime end = endValidityStr.isEmpty()
                            ? OffsetDateTime.parse("9999-12-31T00:00:00Z", formatter)
                            : OffsetDateTime.parse(endValidityStr, formatter);

                    boolean isActive = (now.isEqual(start) || now.isAfter(start)) &&
                            (now.isEqual(end) || now.isBefore(end));

                    coverageMap.put(zip, isActive);
                } catch (Exception e) {
                    System.err.println("Errore parsing per riga: " + configKey + " → " + e.getMessage());
                }
            }
        }

        System.out.println("Mappa copertura creata con " + coverageMap.size() + " elementi.");
        coverageMap.forEach((k, v) -> System.out.println(k + " = " + v));
    }

    @Then("verifico la copertura Radd dai dati del csv")
    public void setCheckCoverageRequest() {

        Map<String, String> report = new HashMap<>();

        coverageMap.forEach((cap, expectedCoverage) -> {

            CheckCoverageRequest checkCoverageRequest = new CheckCoverageRequest()
                    .cap(cap);
                    //.city("CITY");

            CheckCoverageResponse response = raddCapCoverageClient.checkCoverage(SearchMode.COMPLETE, checkCoverageRequest);

            boolean actualCoverage = response.getHasCoverage() != null && response.getHasCoverage();

            if (expectedCoverage && actualCoverage) {
                report.put(cap, "OK");
            } else {
                report.put(cap, "KO");
            }
        });
        System.out.println("===== REPORT COPERTURA RADD =====");
        report.forEach((cap, status) -> System.out.println(cap + " -> " + status));

        boolean hasKO = report.values().stream().anyMatch(status -> "KO".equals(status));
        if (hasKO) {
            throw new AssertionError("Almeno un CAP non ha la copertura prevista. Report: " + report);
        }

    }

}