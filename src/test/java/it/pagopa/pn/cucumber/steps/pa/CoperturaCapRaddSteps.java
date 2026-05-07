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

import java.io.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static it.pagopa.common.util.StringUtils.resolveValue;
import static it.pagopa.common.util.StringUtils.ALPHABET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static it.pagopa.common.util.DateUtils.FORMATTER_ISO;
import static it.pagopa.common.util.DateUtils.resolveDate;



@Slf4j
public class CoperturaCapRaddSteps {

    private final SharedSteps sharedSteps;
    private final PnRaddCapCoverageClientImpl raddCapCoverageClient;
    private final SettableAuthTokenRaddCognito settableAuthTokenRaddCognito;

    private String cap;
    private String locality;
    private String randomLocality;
    private LocalDate searchDate = null;

    private static final int LENGTH = 6;
    private static final Random RANDOM = new Random();


    private CreateCoverageRequest request;
    private ResponseEntity<Coverage> response;
    private Coverage responseCoverage;
    private CheckCoverageRequest checkCoverageRequest;
    private UpdateCoverageRequest updateRequest;
    private CheckCoverageResponse checkCoverageResponse;

    private Map<String, Map<String, Object>> coverageMap = new HashMap<>();

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
                .cap(resolveValue(data.get("cap")))
                .locality(resolveValue(randomLocality))
                .cadastralCode(resolveValue(data.get("cadastralCode")))
                .province(resolveValue(data.get("province")));
    }

    @Given("setto i dati per creare una nuova copertura Radd:")
    public void newCoverageRequest(DataTable dataTable) {

        Map<String, String> data = dataTable.asMaps().get(0);
        request = new CreateCoverageRequest()
                .cap(resolveValue(data.get("cap")))
                .locality(resolveValue(data.get("locality")))
                .cadastralCode(resolveValue(data.get("cadastralCode")))
                .province(resolveValue(data.get("province")));
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
            this.locality = resolveValue(data.get("locality"));

        updateRequest = new UpdateCoverageRequest()
                .cadastralCode(resolveValue(data.get("cadastralCode")))
                .province(resolveValue(data.get("province")))
                .startValidity(resolveDate(data.get("startValidity")))
                .endValidity(resolveDate(data.get("endValidity")));

    }

    @And("setto la data per la quale voglio verificare la copertura al {string}")
    public void setSearchDate(String searchDateStr) {

        searchDate = Optional.ofNullable(resolveDate(searchDateStr)).map(LocalDate::parse).orElse(null);
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
        String nameRow2 = resolveValue(data.get("nameRow2"));
        String addressRow = resolveValue(data.get("addressRow"));
        String addressRow2 = resolveValue(data.get("addressRow2"));
        String cap = resolveValue(data.get("cap"));
        String city2 = resolveValue(data.get("city2"));
        String pr = resolveValue(data.get("pr"));
        String country = resolveValue(data.get("country"));

        if (data.get("city").equalsIgnoreCase("/"))
            city = this.locality;
        else
            city = resolveValue(data.get("city"));

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
            checkCoverageResponse = raddCapCoverageClient.checkCoverage(SearchMode.COMPLETE, checkCoverageRequest, searchDate);

        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @Then("invoco l'API di verifica copertura cap Radd Complete mode")
    public void invokeVerifyCoverageApiC() {
        Assertions.assertDoesNotThrow(() -> {
            checkCoverageResponse = raddCapCoverageClient.checkCoverage(SearchMode.COMPLETE, checkCoverageRequest, searchDate);
            assertNotNull(checkCoverageResponse, "La response non deve essere null");
//            assertTrue(checkCoverageResponse.getStatusCode().is2xxSuccessful(),
//                    "La chiamata non ha restituito un codice 2xx. Codice ricevuto: " + response.getStatusCodeValue());
        }, "Errore durante la verifica della copertura Radd");
    }

    @And("invoco l'API di verifica copertura cap Radd Light mode con errore")
    public void invokeVerifyCoverageApiLError() {
        try {
            checkCoverageResponse = raddCapCoverageClient.checkCoverage(SearchMode.LIGHT, checkCoverageRequest, searchDate);

        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @And("invoco l'API di verifica copertura cap Radd mode: NULL con errore")
    public void invokeVerifyCoverageApiLModeError() {
        try {
            checkCoverageResponse = raddCapCoverageClient.checkCoverage(null, checkCoverageRequest, searchDate);

        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @Then("invoco l'API di verifica copertura cap Radd Light mode")
    public void invokeVerifyCoverageApiL() {
        Assertions.assertDoesNotThrow(() -> {
            checkCoverageResponse = raddCapCoverageClient.checkCoverage(SearchMode.LIGHT, checkCoverageRequest, searchDate);
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


    // Step lettura CSV


    @Given("leggo il file csv e salvo cap, localita e stato copertura")
    public void getCoverageFromFile() throws IOException {

        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/TEST-cop-cap-radd.csv"))) {
            String line;
            boolean isHeader = true;


            LocalDate now = LocalDate.now();

            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (values.length < 6) continue;

                String locality = values[0].replace("\"", "").trim();
                String cap = values[2].replace("\"", "").trim();
                String startValidityStr = values[4].replace("\"", "").trim();
                String endValidityStr = values[5].replace("\"", "").trim();

                if (cap.isEmpty()) continue;

                try {
                    LocalDate start = LocalDate.parse(startValidityStr, FORMATTER_ISO);
                    LocalDate end = endValidityStr.isEmpty()
                            ? LocalDate.parse("9999-12-31", FORMATTER_ISO)
                            : LocalDate.parse(endValidityStr, FORMATTER_ISO);

                    boolean isActive = (now.isEqual(start) || now.isAfter(start)) &&
                            (now.isEqual(end) || now.isBefore(end));

                    Map<String, Object> coverageData = new HashMap<>();
                    coverageData.put("locality", locality.isEmpty() ? null : locality);
                    coverageData.put("isActive", isActive);

                    coverageMap.put(cap, coverageData);

                } catch (Exception e) {
                    System.err.println("Errore parsing per CAP " + cap + ": " + e.getMessage());
                }
            }
        }

        System.out.println("Mappa copertura creata con " + coverageMap.size() + " elementi.");
        coverageMap.forEach((cap, data) ->
                System.out.println(cap + " → " + data)
        );
    }

    @Then("verifico che lo stato della copertura sia coerente tra file e database")
    public void setCheckCoverageRequest() {

        Map<String, String> report = new HashMap<>();

        coverageMap.forEach((cap, data) -> {

            String localityTmp = (String) data.get("locality");
            boolean expectedCoverage = (boolean) data.get("isActive");

            CheckCoverageRequest checkCoverageRequest = new CheckCoverageRequest()
                    .cap(cap);

            if (localityTmp != null) {
                checkCoverageRequest.city(localityTmp);
            } else {
                checkCoverageRequest.city("ND");
            }

            SearchMode mode = (localityTmp == null) ? SearchMode.LIGHT : SearchMode.COMPLETE;

            CheckCoverageResponse responseTmp = raddCapCoverageClient.checkCoverage(mode, checkCoverageRequest, searchDate);

            boolean actualCoverage = responseTmp.getHasCoverage() != null && responseTmp.getHasCoverage();

            if (expectedCoverage == actualCoverage) {
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

    @Given("inserisco i dati di copertura dal CSV nel database")
    public void insertCoverageFromCSV() throws IOException {

        int inserted = 0;
        int skipped = 0;
        int failed = 0;

        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/TEST-cop-cap-radd.csv"))) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (values.length < 6) continue;

                String locality = values[0].replace("\"", "").trim();
                String province = values[1].replace("\"", "").trim();
                String cap = values[2].replace("\"", "").trim();
                String cadastralCode = values[3].replace("\"", "").trim();
                String startValidityStr = values[4].replace("\"", "").trim();
                String endValidityStr = values[5].replace("\"", "").trim();

                if (cap.isEmpty()) continue;

                CreateCoverageRequest createRequest = new CreateCoverageRequest()
                        .cap(resolveValue(cap))
                        .locality(locality.isEmpty() ? "ND" : locality)
                        .cadastralCode(resolveValue(cadastralCode))
                        .province(resolveValue(province));

                try {
                    raddCapCoverageClient.addCoverageWithHttpInfo(createRequest);
                    inserted++;
                } catch (HttpStatusCodeException e) {
                    if (e.getStatusCode().value() == 409) {
                        System.out.println("Coverage già presente per CAP " + cap + ", skipping insert.");
                        skipped++;
                    } else {
                        System.out.println("Errore durante l'inserimento per CAP " + cap + ": " + e.getMessage());
                        failed++;
                    }
                } catch (Exception e) {
                    System.out.println("Errore generico durante l'inserimento per CAP " + cap + ": " + e.getMessage());
                    failed++;
                }
                UpdateCoverageRequest updateRequest = new UpdateCoverageRequest()
                        .cadastralCode(resolveValue(cadastralCode))
                        .province(resolveValue(province))
                        .startValidity(startValidityStr.isEmpty() ? null : startValidityStr)
                        .endValidity(endValidityStr.isEmpty() ? null : endValidityStr);

                try {
                    raddCapCoverageClient.updateCoverage(cap, locality, updateRequest);
                } catch (Exception e) {
                    System.out.println("Errore aggiornamento date per CAP " + cap + ": " + e.getMessage());
                }
            }
        }
        System.out.println(" *** REPORT INSERIMENTO COPERTURA CSV *** ");
        System.out.println("Record inseriti correttamente: " + inserted);
        System.out.println("Record saltati (409 Conflict): " + skipped);
        System.out.println("Record falliti: " + failed);
        System.out.println("Inserimento dati CSV completato con successo.");
    }

    @Given("leggo il file csv con cap e localita ed effettuo chiamate light e complete con report")
    public void checkCoverageLightAndComplete() throws IOException {

        String inputFile = "src/main/resources/TEST-cap-localita.csv";
        String outputFile = "src/main/resources/output/risultati_copertura.csv";

        File outputDir = new File("src/main/resources/output");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            boolean isHeader = true;

            bw.write("CAP;LOCALITA;ESITO_LIGHT;ESITO_COMPLETE");
            bw.newLine();

            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (values.length < 2) continue;

                String cap = values[0].replace("\"", "").trim();
                String locality = values[1].replace("\"", "").trim();

                if (cap.isEmpty()) continue;
                //Talvolta gli editor csv interpretano i cap come numeri e rimuovono gli 0 iniziali.
                // Le due righe di codice seguenti risolvono il problema.
                if (cap.length() < 5) {
                    cap = "0".repeat(5 - cap.length()) + cap;
                }

                // **** Chiamata con SearchMode.COMPLETE ****
                CheckCoverageRequest completeReq = new CheckCoverageRequest()
                        .cap(cap)
                        .city(locality);


                CheckCoverageResponse completeResp = assertDoesNotThrow(
                        () -> raddCapCoverageClient.checkCoverage(SearchMode.COMPLETE, completeReq, searchDate),
                        "Errore nella chiamata COMPLETE per CAP " + cap + ", LOCALITA " + locality
                );

                if (completeResp.getHasCoverage() == null) {
                    throw new AssertionError("Campo hasCoverage nullo per COMPLETE - CAP " + cap + ", LOCALITA " + locality);
                }

                String esitoComplete = (completeResp.getHasCoverage() != null && completeResp.getHasCoverage())
                        ? "SI" : "NO";

                // **** Chiamata con SearchMode.LIGHT ****
                CheckCoverageRequest lightReq = new CheckCoverageRequest()
                        .cap(cap)
                        .city(locality);

                CheckCoverageResponse lightResp = assertDoesNotThrow(
                        () -> raddCapCoverageClient.checkCoverage(SearchMode.LIGHT, lightReq, searchDate),
                        "Errore nella chiamata LIGHT per CAP " + cap + ", LOCALITA " + locality
                );
                if (lightResp.getHasCoverage() == null) {
                    throw new AssertionError("Campo hasCoverage nullo per LIGHT - CAP " + cap + ", LOCALITA " + locality);
                }

                String esitoLight = (lightResp.getHasCoverage() != null && lightResp.getHasCoverage())
                        ? "SI" : "NO";

                // **** Scrittura risultati nel file CSV ****
                bw.write(String.format("%s;%s;%s;%s", cap, locality, esitoLight, esitoComplete));
                bw.newLine();

                System.out.printf("CAP %s (%s): COMPLETE=%s, LIGHT=%s%n", cap, locality, esitoLight, esitoComplete);
            }

            System.out.println("===== REPORT COMPLETATO =====");
            System.out.println("File risultati generato in: " + outputFile);
        }
    }

}