package it.pagopa.pn.cucumber.steps.pa;

import com.opencsv.CSVWriter;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.service.impl.PnRaddAlternativeClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnRaddAlternativeV2ClientImpl;
import it.pagopa.pn.client.b2b.pa.service.utils.AuthenticatorCognito;
import it.pagopa.pn.client.b2b.pa.service.utils.RaddOperator;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableAuthTokenRaddCognito;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD.Address;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD.*;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD_V2.*;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCsv.*;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.dataTable.DataTableTypeRaddAlt;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import software.amazon.awssdk.regions.Region;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static it.pagopa.pn.cucumber.utils.NotificationValue.generateRandomNumber;
import static it.pagopa.pn.cucumber.utils.RaddAltValue.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class AnagraficaRaddAltSteps {

    private static final int WAITING_ACCEPTED_STATE = 20000;
    private static final String ACCEPTED = "accepted";
    private final PnRaddAlternativeClientImpl raddAltClient;
    private final PnRaddAlternativeV2ClientImpl raddAltClientV2;
    private final SharedSteps sharedSteps;
    private final DataTableTypeRaddAlt dataTableTypeRaddAlt;

    private String fileCsvName;
    private String shaCSV;
    private String requestid;
    private String registryId;
    private CreateRegistryRequest sportelloRaddCrud;
    private RegistriesResponse sportelliRaddista;
    private it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCsv.RequestResponse sportelliCsvRaddista;
    private final String uid = getDefaultValue(RADD_UID.key);
    private static final Integer NUM_CHECK_STATE_CSV = 100;
    private static final Integer WAITING_STATE_CSV = 15000;
    private String pageIndex = null;
    private final List<Address> addresses = new ArrayList<>();

    private String tokenCognito;
    private CreateRegistryRequestV2 createRegistryRequestV2;
    private GetRegistryResponseV2 getRegistryResponseV2;
    private UpdateRegistryRequestV2 updateRegistryRequestV2;
    private final SettableAuthTokenRaddCognito settableAuthTokenRaddCognito;

    @Autowired
    public AnagraficaRaddAltSteps(PnRaddAlternativeClientImpl raddAltClient, PnRaddAlternativeV2ClientImpl raddAltClientV2, SharedSteps sharedSteps, DataTableTypeRaddAlt dataTableTypeRaddAlt, SettableAuthTokenRaddCognito settableAuthTokenRaddCognito) {
        this.raddAltClient = raddAltClient;
        this.raddAltClientV2 = raddAltClientV2;
        this.sharedSteps = sharedSteps;
        this.dataTableTypeRaddAlt = dataTableTypeRaddAlt;
        this.settableAuthTokenRaddCognito = settableAuthTokenRaddCognito;
    }

    //protected String xPagopaPnCxId = getDefaultValue(RADD_PN_CX_ID.key);
    protected String xPagopaPnCxId = "Test-QA";
    protected String locationId;
    protected RegistryV2 registryV2Response;


    // V2

    @Given("l' utente con username {string} password {string} e clientId {string} richiede e riceve un token valido tramite cognito")
    public void getTokenCognito(String username, String password, String clientId) {

        AuthenticatorCognito authenticator = new AuthenticatorCognito(username, password, clientId, Region.EU_SOUTH_1); // cambia regione
        this.tokenCognito = authenticator.generateJwtToken();

        assertNotNull(tokenCognito, "Il token JWT non deve essere nullo");
        assertTrue(tokenCognito.length() > 10, "Il token JWT sembra troppo corto");
        System.out.println("Token ottenuto: " + tokenCognito);

        raddAltClientV2.selectRaddista(tokenCognito);

    }


    /*
     * "LETTURA_SCRITTURA" = usa credenziali user1, "SOLO_LETTURA" = usa credenziali user2
     * */
    @Given("Effettuo l'autenticazione per l' utente con permessi: {string}")
    public void getSpecificTokenCognito(String permessi) {

        String token = settableAuthTokenRaddCognito.generateToken(permessi);

        raddAltClientV2.selectRaddista(token);
    }

    @And("la response registry V2 deve avere tutti i campi obbligatori valorizzati")
    public void validateMandatoryFieldsInRegistry() {
        RegistryV2 registry = this.registryV2Response;

        assertNotNull(registry, "La response è null");

        NormalizedAddress addr = registry.getNormalizedAddress();
        assertNotNull(addr, "NormalizedAddress mancante");

        assertTrue(isValidString(addr.getAddressRow()), "addressRow mancante o vuoto");
        assertTrue(isValidString(addr.getCap()), "cap mancante o vuoto");
        assertTrue(isValidString(addr.getCity()), "city mancante o vuoto");
        assertTrue(isValidString(addr.getProvince()), "province mancante o vuoto");
        assertTrue(isValidString(addr.getCountry()), "country mancante o vuoto");
        assertNotNull(addr.getBiasPoint(), "biasPoint mancante o vuoto");
        assertNotNull(addr.getLatitude(), "latitude mancante");
        assertNotNull(addr.getLongitude(), "longitude mancante");

        log.info("RegistryV2 response: ", registry.toString());

    }

    @And("la response registry V2 della lettura deve avere tutti i campi obbligatori valorizzati")
    public void validateMandatoryFieldsInRegistryRead() {

        this.registryV2Response = this.getRegistryResponseV2.getItems().get(0);
        this.validateMandatoryFieldsInRegistry();
    }

    private boolean isValidString(String value) {
        return value != null && !value.isBlank();
    }


    @Then("la response V2 contiene almeno un externalCode uguale a quello della request")
    public void responseContainsExternalCodeFromRequest() {
        CreateRegistryRequestV2 request = this.createRegistryRequestV2;
        GetRegistryResponseV2 response = this.getRegistryResponseV2;

        List<String> expectedExternalCodes = request.getExternalCodes();

        boolean trovato = response.getItems().stream()
                .anyMatch(registry -> registry.getExternalCodes() != null &&
                        registry.getExternalCodes().stream().anyMatch(expectedExternalCodes::contains));

        assertTrue(trovato,
                "Nessun RegistryV2 nella response contiene almeno un externalCode della request");
    }


    @Then("viene verificato che l' ultimo sportello inserito venga restituito nella lista tramite locationId")
    public void locationIdPresentInResponse() {

        GetRegistryResponseV2 response = raddAltClientV2.retrieveRegistries(xPagopaPnCxId, 100, null);

        try {
            Assertions.assertNotNull(response, "La risposta da retrieveRegistries è NULL");
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    " {Lista sportelli attesa per requestId=" + (this.requestid == null ? "NULL" : this.requestid) + " }";
            throw new AssertionFailedError(
                    message,
                    assertionFailedError.getExpected(),
                    assertionFailedError.getActual(),
                    assertionFailedError.getCause()
            );
        }
        Assertions.assertFalse(
                response.getItems().isEmpty(),
                "La lista di RegistryV2 restituita da retrieveRegistries è vuota"
        );
        assertThat(response.getItems())
                .as("Verifica che il locationId atteso sia presente nella lista di RegistryV2")
                .extracting(RegistryV2::getLocationId)
                .contains(locationId);
    }


    @When("viene richiesta la lista degli sportelli Radd V2 con dati:")
    public void vieneRichiestolaListaDeiSportelliRaddV2(Map<String, String> dataSportello) {

        try {
            Assertions.assertDoesNotThrow(() -> {
                GetRegistryResponseV2 sportello = raddAltClientV2.retrieveRegistries(
                        this.xPagopaPnCxId,
                        getValue(dataSportello, RADD_FILTER_LIMIT.key) == null ? null : Integer.parseInt(getValue(dataSportello, RADD_FILTER_LIMIT.key)),
                        getValue(dataSportello, RADD_FILTER_LASTKEY.key) == null ? null : getValue(dataSportello, RADD_FILTER_LASTKEY.key)
                );
                this.getRegistryResponseV2 = sportello;

                if (this.getRegistryResponseV2 == null) {
                    throw new AssertionFailedError("La response è nulla", null, "Response valorizzata");
                }
            });
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{endDate: " + (this.requestid == null ? "NULL" : this.requestid) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @Then("la response V2 deve contenere {int} items")
    public void checkNumberOfItems(int expectedCount) {
        GetRegistryResponseV2 response = getRegistryResponseV2;

        if (response.getItems() == null) {
            throw new AssertionError("La lista 'items' è null");
        }

        int actualCount = response.getItems().size();

        System.out.println("Numero di items nella response: " + actualCount);

        assertThat(actualCount)
                .as("Il numero di items non corrisponde")
                .isEqualTo(expectedCount);
    }

    @When("viene generato uno sportello Radd V2 con dati:")
    public void vieneGeneratoSportelloRaddV2(@Transpose CreateRegistryRequestV2 dataSportello) {

        this.createRegistryRequestV2 = dataSportello;

        log.info("Request inserimento: {}", dataSportello);
        RegistryV2 creationResponse = raddAltClientV2.addRegistry(xPagopaPnCxId, dataSportello);

        try {
            Assertions.assertNotNull(creationResponse, "La response di addRegistry è NULL");
            Assertions.assertNotNull(creationResponse.getLocationId(), "locationId nullo nella response");
            Assertions.assertFalse(creationResponse.getLocationId().isBlank(), "locationId vuoto nella response");

            this.locationId = creationResponse.getLocationId();
            this.registryV2Response = creationResponse;

        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{Response Upload CSV: " + (creationResponse == null ? "NULL" : creationResponse) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @When("viene generato uno sportello Radd V2 con restituzione errore con dati:")
    public void vieneGeneratoConErroreSportelloRaddV2(@Transpose CreateRegistryRequestV2 dataSportelloV2) {
        try {
            raddAltClientV2.addRegistry(xPagopaPnCxId, dataSportelloV2);
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }


    @When("viene modificato uno sportello Radd V2 con dati:")
    public void vieneModificatoSportelloRaddDatiV2(@Transpose UpdateRegistryRequestV2 dataSportelloUpdate) {
        log.info("Upload Request: {}", createRegistryRequestV2);
        try {

            RegistryV2 response = Assertions.assertDoesNotThrow(
                    () -> raddAltClientV2.updateRegistry(this.xPagopaPnCxId, this.locationId, dataSportelloUpdate)
            );

            this.registryV2Response = response;
            this.updateRegistryRequestV2 = dataSportelloUpdate;

        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{Response Upload CSV: " + (createRegistryRequestV2 == null ? "NULL" : createRegistryRequestV2) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @When("viene modificato uno sportello Radd V2 con dati errati:")
    public void vieneModificatoSportelloRaddV2(Map<String, String> datiAggiornamento) {

        UpdateRegistryRequestV2 aggiornamentoSportelloRadd = dataTableTypeRaddAlt.convertUpdateRegistryRequestV2(datiAggiornamento);

        try {
            raddAltClientV2.updateRegistry(
                    getValue(datiAggiornamento, RADD_PN_CX_ID.key),
                    getValue(datiAggiornamento, RADD_LOCATION_ID.key), aggiornamentoSportelloRadd);
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
            log.info("errore: {}", e.getStatusText());
        }
    }

    @When("viene cancellato lo sportello Radd V2 appena inserito tramite locationId")
    public void vieneCancellatoSportelloRaddV2() {

        try {
            ResponseEntity<Void> response = Assertions.assertDoesNotThrow(
                    () -> raddAltClientV2.deleteRegistryWithHttpInfo(xPagopaPnCxId, registryV2Response.getLocationId()),
                    "La chiamata a deleteRegistryWithHttpInfo ha lanciato un'eccezione"
            );
            Assertions.assertTrue(response.getStatusCode().is2xxSuccessful(), "La cancellazione non è andata a buon fine");
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage();
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @When("viene cancellato lo sportello Radd V2 appena inserito tramite locationId: {string} con errore")
    public void vieneCancellatoSportelloRaddV2Parameter(String locationId) {

        try {
            raddAltClientV2.deleteRegistryWithHttpInfo(xPagopaPnCxId, locationId);
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @When("viene cancellato lo sportello Radd V2 appena inserito tramite locationId con errore")
    public void vieneCancellatoSportelloRaddV2ConErrore() {

        try {
            raddAltClientV2.deleteRegistryWithHttpInfo(xPagopaPnCxId, registryV2Response.getLocationId());
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @Then("la response V2 a seguito del nuovo inserimento deve contenere i valori attesi")
    public void checkResponseValueFromInsert(DataTable dataTable) {
        Map<String, String> expectedData = dataTable.asMap(String.class, String.class);

        RegistryV2 response = registryV2Response;

        if (expectedData.get("partnerId") != null) {
            assertThat(response.getPartnerId()).isEqualTo(expectedData.get("partnerId"));
        }

        //if (expectedData.get("locationId") != null) {
        //    assertThat(response.getLocationId()).isEqualTo(expectedData.get("locationId"));
        //}

        if (expectedData.get("description") != null) {
            assertThat(response.getDescription()).isEqualTo(expectedData.get("description"));
        }

        if (expectedData.get("email") != null) {
            assertThat(response.getEmail()).isEqualTo(expectedData.get("email"));
        }

        if (expectedData.get("appointmentRequired") != null) {
            assertThat(response.getAppointmentRequired())
                    .isEqualTo(Boolean.valueOf(expectedData.get("appointmentRequired")));
        }

        if (expectedData.get("externalCodes") != null) {
            List<String> expectedCodes = Arrays.stream(expectedData.get("externalCodes").split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
            assertThat(response.getExternalCodes())
                    .containsExactlyInAnyOrderElementsOf(expectedCodes);
        }

        if (expectedData.get("phoneNumbers") != null) {
            List<String> expectedPhones = Arrays.stream(expectedData.get("phoneNumbers").split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
            assertThat(response.getPhoneNumbers())
                    .containsExactlyInAnyOrderElementsOf(expectedPhones);
        }

        assertThat(response.getNormalizedAddress())
                .withFailMessage("normalizedAddress non deve essere null quando ci sono valori attesi")
                .isNotNull();

        NormalizedAddress address = response.getNormalizedAddress();

        if (expectedData.get("addressRow") != null) {
            assertThat(address.getAddressRow()).isEqualTo(expectedData.get("addressRow"));
        }
        if (expectedData.get("city") != null) {
            assertThat(address.getCity()).isEqualTo(expectedData.get("city"));
        }
        if (expectedData.get("cap") != null) {
            assertThat(address.getCap()).isEqualTo(expectedData.get("cap"));
        }
        if (expectedData.get("province") != null) {
            assertThat(address.getProvince()).isEqualTo(expectedData.get("province"));
        }
    }

    @Then("i campi della response devono corrispondere alla request di update")
    public void confrontaCampiUpdateConResponse() {
        assertNotNull(this.registryV2Response, "La response RegistryV2 è null");
        assertNotNull(this.updateRegistryRequestV2, "La request UpdateRegistryRequestV2 è null");

        RegistryV2 resp = this.registryV2Response;
        UpdateRegistryRequestV2 req = this.updateRegistryRequestV2;

        assertEquals(req.getDescription(), resp.getDescription(), "Description diversa");
        assertEquals(req.getOpeningTime(), resp.getOpeningTime(), "OpeningTime diversa");
        assertEquals(req.getEndValidity(), resp.getEndValidity(), "EndValidity diversa");
        assertEquals(req.getWebsite(), resp.getWebsite(), "Website diverso");
        assertEquals(req.getEmail(), resp.getEmail(), "Email diversa");
        assertEquals(req.getAppointmentRequired(), resp.getAppointmentRequired(), "AppointmentRequired diverso");

        // liste
        assertIterableEquals(req.getExternalCodes(), resp.getExternalCodes(), "ExternalCodes diversi");
        assertIterableEquals(req.getPhoneNumbers(), resp.getPhoneNumbers(), "PhoneNumbers diversi");
    }

    @Then("la response V2 deve avere tutti i campi valorizzati")
    public void checkAllFieldsAreNotNull() {
        RegistryV2 response = registryV2Response;

        assertThat(response).withFailMessage("La response V2 non deve essere null").isNotNull();

        //Campi principali di RegistryV2
        assertThat(response.getPartnerId()).as("partnerId").isNotNull();
        assertThat(response.getLocationId()).as("locationId").isNotNull();
        assertThat(response.getDescription()).as("description").isNotNull();
        assertThat(response.getPhoneNumbers()).as("phoneNumbers").isNotNull();
        assertThat(response.getEmail()).as("email").isNotNull();
        assertThat(response.getOpeningTime()).as("openingTime").isNotNull();
        assertThat(response.getStartValidity()).as("startValidity").isNotNull();
        assertThat(response.getEndValidity()).as("endValidity").isNotNull();
        assertThat(response.getExternalCodes()).as("externalCodes").isNotNull();
        assertThat(response.getAppointmentRequired()).as("appointmentRequired").isNotNull();
        assertThat(response.getWebsite()).as("website").isNotNull();
        assertThat(response.getPartnerType()).as("partnerType").isNotNull();
        assertThat(response.getCreationTimestamp()).as("creationTimestamp").isNotNull();
        assertThat(response.getUpdateTimestamp()).as("updateTimestamp").isNotNull();

        assertThat(response.getNormalizedAddress())
                .as("normalizedAddress")
                .isNotNull();

        NormalizedAddress address = response.getNormalizedAddress();

        // Campi di NormalizedAddress
        assertThat(address.getAddressRow()).as("addressRow").isNotNull();
        assertThat(address.getCap()).as("cap").isNotNull();
        assertThat(address.getCity()).as("city").isNotNull();
        assertThat(address.getProvince()).as("province").isNotNull();
        assertThat(address.getCountry()).as("country").isNotNull();
        assertThat(address.getLatitude()).as("latitude").isNotNull();
        assertThat(address.getLongitude()).as("longitude").isNotNull();
        assertThat(address.getBiasPoint()).as("biasPoint").isNotNull();
    }

    @Then("la response V2 deve aver restiutito in automatico startValidity odierno in formato yyyy-MM-dd")
    public void checkStartValidityIsToday() {
        String startValidity = registryV2Response.getStartValidity();
        if (startValidity == null) {
            throw new AssertionError("startValidity è null");
        }
        System.out.println("startValidity: '" + startValidity + "'");

        // Prende solo i primi 10 caratteri in caso arrivi un datetime completo
        String datePart = startValidity.length() >= 10 ? startValidity.substring(0, 10) : startValidity;

        LocalDate parsedDate = LocalDate.parse(datePart); // usa default ISO yyyy-MM-dd
        LocalDate today = LocalDate.now(ZoneId.of("Europe/Rome"));

        if (!parsedDate.equals(today)) {
            throw new AssertionError("startValidity (" + parsedDate + ") != oggi (" + today + ")");
        }
    }


    //       V1

    @When("viene caricato il csv con dati:")
    public void vieneGeneratoIlCsv(List<Map<String, String>> dataCsv) throws IOException {
        log.info("dataCsv: {}", dataCsv);
        creazioneCsv(dataCsv, true, addresses);
        RegistryUploadRequest registryUploadRequest = new RegistryUploadRequest().checksum(this.shaCSV);

        RegistryUploadResponse responseUploadCsv = raddAltClient.uploadRegistryRequests(this.uid, registryUploadRequest);
        try {
            Assertions.assertNotNull(responseUploadCsv);
            Assertions.assertNotNull(responseUploadCsv.getRequestId());
            Assertions.assertNotNull(responseUploadCsv.getSecret());
            Assertions.assertNotNull(responseUploadCsv.getUrl());
            Assertions.assertNotNull(responseUploadCsv.getFileKey());
            this.requestid = responseUploadCsv.getRequestId();
            B2bUtils.preloadRaddCsvDocument(sharedSteps.getContext(), "classpath:/" + this.fileCsvName, this.shaCSV, responseUploadCsv, true);

        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{Response Upload CSV: " + (responseUploadCsv == null ? "NULL" : responseUploadCsv) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }


    @When("viene caricato il csv con formatto {string} con restituzione errore con dati:")
    public void vieneGeneratoIlCsvConResituzioneErrore(String formatoCsv, List<Map<String, String>> dataCsv) throws IOException {
        creazioneCsv(dataCsv, formatoCsv.equalsIgnoreCase("corretto"), null);
        RegistryUploadRequest registryUploadRequest = new RegistryUploadRequest().checksum(this.shaCSV);

        try {
            RegistryUploadResponse responseUploadCsv = raddAltClient.uploadRegistryRequests(this.uid, registryUploadRequest);
            if (responseUploadCsv != null) {
                this.requestid = responseUploadCsv.getRequestId();
                B2bUtils.preloadRaddCsvDocument(sharedSteps.getContext(), "classpath:/" + this.fileCsvName, this.shaCSV, responseUploadCsv, true);
            }
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @When("viene caricato il csv 2 volte con dati:")
    public void vieneGeneratoIlCsvConResituzioneErrore(List<Map<String, String>> dataCsv) throws IOException {
        creazioneCsv(dataCsv, true, addresses);
        RegistryUploadRequest registryUploadRequest = new RegistryUploadRequest().checksum(this.shaCSV);
        RegistryUploadResponse responseUploadCsv = null;

        try {
            responseUploadCsv = raddAltClient.uploadRegistryRequests(this.uid, registryUploadRequest);
            this.requestid = responseUploadCsv.getRequestId();
            raddAltClient.uploadRegistryRequests(this.uid, registryUploadRequest);

        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
            B2bUtils.preloadRaddCsvDocument(sharedSteps.getContext(), "classpath:/" + this.fileCsvName, this.shaCSV, responseUploadCsv, true);
        }
    }

    @When("viene controllato lo stato di caricamento del csv a {string}")
    public void vieneControllatoLoStatoDelCsv(String stato) {

        VerifyRequestResponse responseUploadCsv = null;
        //TODO: utilizzare algoritmo di polling
        for (int i = 0; i < NUM_CHECK_STATE_CSV; i++) {
            responseUploadCsv = raddAltClient.verifyRequest(this.uid, this.requestid);

            if (stato.equalsIgnoreCase("DONE") && responseUploadCsv.getStatus().equalsIgnoreCase("REPLACED")) {
                break;
            }

            if (responseUploadCsv.getStatus().equalsIgnoreCase(stato)) {
                break;
            }

            waitFor(WAITING_STATE_CSV);
        }

        try {
            Assertions.assertNotNull(responseUploadCsv);
            Assertions.assertNotNull(responseUploadCsv.getStatus());
            assertEquals(stato.toUpperCase(), responseUploadCsv.getStatus());
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{Response Upload CSV: " + (responseUploadCsv == null ? "NULL" : responseUploadCsv) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }

    }

    @When("viene controllato lo stato di caricamento del csv a REJECTED con messaggio di errore {string}")
    public void vieneControllatoErroreLoStatoDelCsv(String mesaggioErrore) {

        VerifyRequestResponse responseVerifyCsv = raddAltClient.verifyRequest(this.uid, this.requestid);

        try {
            Assertions.assertNotNull(responseVerifyCsv);
            Assertions.assertNotNull(responseVerifyCsv.getStatus());
            Assertions.assertNotNull(responseVerifyCsv.getError());
            assertEquals("REJECTED", responseVerifyCsv.getStatus());
            assertEquals(mesaggioErrore, responseVerifyCsv.getError());

        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{Response Upload CSV: " + (responseVerifyCsv == null ? "NULL" : responseVerifyCsv) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }

    }

    @When("viene eseguita la richiesta per controllo dello stato di caricamento del csv con restituzione errore")
    public void vieneControllatoErroreSullaRichiestaDelloStatoDelCsv(Map<String, String> richiestaSportello) {

        try {
            raddAltClient.verifyRequest(getValue(richiestaSportello, RADD_UID.key), getValue(richiestaSportello, RADD_REQUESTID.key));
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }

    }

    @When("controllo che venga restituito vuoto perchè non presente")
    public void controlloNonPresenza() {

        try {
            Assertions.assertTrue(this.sportelliCsvRaddista.getItems().isEmpty());
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @When("viene richiesta la lista degli sportelli caricati dal csv:")
    public void vieneRichiestolaListaDeiSportelliRaddDelCsv(Map<String, String> dataSportello) {

        it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCsv.RequestResponse sportello = raddAltClient.retrieveRequestItems(
                getValue(dataSportello, RADD_UID.key)
                , getValue(dataSportello, RADD_REQUESTID.key) == null ? null :
                        getValue(dataSportello, RADD_REQUESTID.key).equalsIgnoreCase("corretto") ? this.requestid : getValue(dataSportello, RADD_REQUESTID.key)
                , getValue(dataSportello, RADD_FILTER_LIMIT.key) == null ? null : Integer.parseInt(getValue(dataSportello, RADD_FILTER_LIMIT.key))
                , getValue(dataSportello, RADD_FILTER_LASTKEY.key) == null ? null : getValue(dataSportello, RADD_FILTER_LASTKEY.key));

        try {
            Assertions.assertNotNull(sportello);
            Assertions.assertNotNull(sportello.getItems());

            log.info("lista sportelli: {}", sportello);

            for (int i = 0; i < sportello.getItems().size(); i++) {
                Assertions.assertNotNull(sportello.getItems().get(i));
                Assertions.assertNotNull(sportello.getItems().get(i).getRequestId());
                Assertions.assertNotNull(sportello.getItems().get(i).getRegistryId());
                Assertions.assertNotNull(sportello.getItems().get(i).getOriginalRequest());
                Assertions.assertNotNull(sportello.getItems().get(i).getOriginalRequest().getOriginalAddress());
            }
            this.sportelliCsvRaddista = sportello;
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{endDate: " + (this.requestid == null ? "NULL" : this.requestid) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @When("viene richiesta la lista degli sportelli caricati dal csv con dati errati:")
    public void vieneRichiestolaListaDeiSportelliRaddDelCsvDatiErrati(Map<String, String> dataSportello) {
        try {
            this.sportelliCsvRaddista = raddAltClient.retrieveRequestItems(
                    getValue(dataSportello, RADD_UID.key),
                    getValue(dataSportello, RADD_REQUESTID.key) == null ? null : getValue(dataSportello, RADD_REQUESTID.key),
                    getValue(dataSportello, RADD_FILTER_LIMIT.key) == null ? null : Integer.parseInt(getValue(dataSportello, RADD_FILTER_LIMIT.key)),
                    getValue(dataSportello, RADD_FILTER_LASTKEY.key) == null ? null : getValue(dataSportello, RADD_FILTER_LASTKEY.key));
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @When("si controlla che il sportello sia in stato {string}")
    public void vieneCercatoloSportelloEControlloStato(String status) {
        RegistryRequestResponse dato = IntStream.range(0, NUM_CHECK_STATE_CSV)
                .mapToObj(numCheck -> getRequestResponse(status))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        log.info("sportello trovato: '{}'", dato);

        try {
            Assertions.assertNotNull(dato);
            assertEquals(status, dato.getStatus());
            this.requestid = dato.getRequestId();
            this.registryId = dato.getRegistryId();

        } catch (AssertionFailedError assertionFailedError) {
            throwAssertFailerForSportelloIssue(assertionFailedError, dato);
        }
    }

    @Then("si controlla che lo sportello allo stato index sia in stato status con il messaggio errorMessage:")
    public void siControllaCheIlSportelloSiaInStatoConIlMessaggio(List<Map<String, String>> csvData) {
        List<RegistryRequestResponse> dato = IntStream.range(0, NUM_CHECK_STATE_CSV)
                .mapToObj(numCheck -> getRequestResponse(csvData))
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .filter(distinctByKey(data -> data))
                .limit(csvData.size())
                .collect(Collectors.toList());
        try {
            log.info("sportelli trovati: '{}'", dato);
            Assertions.assertNotNull(dato);
            Assertions.assertFalse(dato.isEmpty());
            assertEquals(csvData.size(), dato.size());

            Assertions.assertNotNull(dato);
            this.requestid = dato.stream().map(RegistryRequestResponse::getRequestId)
                    .filter(Objects::nonNull).findFirst().orElse(this.requestid);
            this.registryId = dato.stream().map(RegistryRequestResponse::getRegistryId)
                    .filter(Objects::nonNull).findFirst().orElse(this.registryId);

        } catch (AssertionFailedError assertionFailedError) {
            throwAssertFailerForSportelloIssue(assertionFailedError, dato);
        }
    }

    @Then("si controlla che gli sportelli inseriti siano nello status giusto:")
    public void siControllaCheGliSportelliInseritiSianoNelloStatusGiusto(List<Map<String, String>> csvData) {
        List<RegistryRequestResponse> dato = new ArrayList<>();

        while (pageIndex == null || !pageIndex.isEmpty()) {
            List<RegistryRequestResponse> requestResponses = getRequestResponse(csvData);
            requestResponses.stream()
                    .filter(Objects::nonNull)
                    .filter(distinctByKey(data -> data))
                    .limit(csvData.size())
                    .forEach(dato::add);
        }

        try {
            log.info("sportelli trovati: '{}'", dato);
            Assertions.assertNotNull(dato);
            Assertions.assertFalse(dato.isEmpty());
            assertEquals(csvData.size(), dato.size());

            Assertions.assertNotNull(dato);
            this.requestid = dato.stream().map(RegistryRequestResponse::getRequestId)
                    .filter(Objects::nonNull).findFirst().orElse(this.requestid);
            this.registryId = dato.stream().map(RegistryRequestResponse::getRegistryId)
                    .filter(Objects::nonNull).findFirst().orElse(this.registryId);

        } catch (AssertionFailedError assertionFailedError) {
            throwAssertFailerForSportelloIssue(assertionFailedError, dato);
        }
    }

    private RegistryRequestResponse getRequestResponse(String status) {
        waitFor(WAITING_STATE_CSV);
        RegistryRequestResponse registryRequestResponse = getRegistryRequestResponse(status);
        if (status.equalsIgnoreCase(ACCEPTED)) waitFor(WAITING_ACCEPTED_STATE);
        return registryRequestResponse;
    }

    private List<RegistryRequestResponse> getRequestResponse(List<Map<String, String>> csvData) {
        return getRegistryRequestResponse(csvData);
    }

    private RegistryRequestResponse getRegistryRequestResponse(String status) {
        return Optional.ofNullable(retrieveSportello())
                .map(RequestResponse::getItems)
                .flatMap(data -> data.stream()
                        .filter(elem -> elem.getRequestId() != null && elem.getStatus() != null)
                        .filter(elem -> elem.getRequestId().equalsIgnoreCase(this.requestid)
                                && elem.getStatus().equalsIgnoreCase(status))
                        .findAny())
                .orElse(null);
    }

    private List<RegistryRequestResponse> getRegistryRequestResponse(List<Map<String, String>> csvData) {
        return retrieveSportelloFromCSV().getItems().stream()
                .filter(elem -> elem.getRequestId() != null && elem.getStatus() != null && elem.getOriginalRequest() != null)
                .filter(elem -> elem.getRequestId().equalsIgnoreCase(this.requestid))
                .filter(elem -> checkStatusAndMessageValid(elem, csvData, addresses))
                .collect(Collectors.toList());
    }

    private it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCsv.RequestResponse retrieveSportello() {
        return raddAltClient.retrieveRequestItems(
                this.uid
                , this.requestid
                , 100
                , null);
    }

    private it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCsv.RequestResponse retrieveSportelloFromCSV() {
        RequestResponse response = raddAltClient.retrieveRequestItems(
                this.uid
                , this.requestid
                , 100
                , pageIndex);
        pageIndex = Optional.ofNullable(response.getNextPagesKey())
                .filter(data -> !data.isEmpty())
                .map(data -> data.get(0))
                .orElse("");
        return response;
    }

    private void throwAssertFailerForSportelloIssue(AssertionFailedError assertionFailedError, RegistryRequestResponse dato) {
        String message = assertionFailedError.getMessage() +
                " {sportello: " + (dato == null ? "NULL" : dato) + " requestId: " + this.requestid + " }";
        throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
    }

    private void throwAssertFailerForSportelloIssue(AssertionFailedError assertionFailedError, List<RegistryRequestResponse> dato) {
        String message = assertionFailedError.getMessage() +
                " {sportello: " + (dato == null ? "NULL" : dato) + " requestId: " + this.requestid + " }";
        throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
    }

    @When("viene generato uno sportello Radd con dati:")
    public void vieneGeneratoSportelloRadd(@Transpose CreateRegistryRequest dataSportello) {

        this.sportelloRaddCrud = dataSportello;

        log.info("Request inserimento: {}", dataSportello);
        CreateRegistryResponse creationResponse = raddAltClient.addRegistry(this.uid, dataSportello);

        try {
            Assertions.assertNotNull(creationResponse);
            Assertions.assertNotNull(creationResponse.getRequestId());

            this.requestid = creationResponse.getRequestId();
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{Response Upload CSV: " + (creationResponse == null ? "NULL" : creationResponse) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @When("viene generato uno sportello Radd con restituzione errore con dati:")
    public void vieneGeneratoConErroreSportelloRadd(@Transpose CreateRegistryRequest dataSportello) {
        try {
            raddAltClient.addRegistry(this.uid, dataSportello);
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @When("viene modificato uno sportello Radd con dati:")
    public void vieneModificatoSportelloRadd(@Transpose UpdateRegistryRequest dataSportello) {
        log.info("Upload Request: {}", dataSportello);
        try {
            Assertions.assertDoesNotThrow(() -> raddAltClient.updateRegistry(this.uid, this.registryId, dataSportello));
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{Response Upload CSV: " + (dataSportello == null ? "NULL" : dataSportello) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @When("viene modificato uno sportello Radd con dati errati:")
    public void vieneModificatoSportelloRadd(Map<String, String> datiAggiornamento) {

        UpdateRegistryRequest aggiornamentoSportelloRadd = dataTableTypeRaddAlt.convertUpdateRegistryRequest(datiAggiornamento);

        try {
            raddAltClient.updateRegistry(
                    getValue(datiAggiornamento, RADD_UID.key),
                    getValue(datiAggiornamento, RADD_REGISTRYID.key) == null ? null :
                            getValue(datiAggiornamento, RADD_REGISTRYID.key).equalsIgnoreCase("corretto") ? this.registryId : getValue(datiAggiornamento, RADD_REGISTRYID.key),
                    aggiornamentoSportelloRadd);
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
            log.info("errore: {}", e.getStatusText());
        }
    }

    @When("viene cancellato uno sportello Radd con dati:")
    public void vieneCancellatoSportelloRadd(Map<String, String> richiestaCancellazione) {
        String endDate = getValue(richiestaCancellazione, RADD_END_VALIDITY.key);

        if (endDate != null) {
            if (endDate.toLowerCase().contains("corretto")) {
                endDate = this.sportelloRaddCrud.getStartValidity();
            } else {
                endDate = dataTableTypeRaddAlt.setData(endDate);
            }
        }

        log.info("data cancellazione sportello: {}", endDate);

        try {
            String finalEndDate = endDate;
            Assertions.assertDoesNotThrow(() -> raddAltClient.deleteRegistry(this.uid, this.registryId, finalEndDate));
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{endDate: " + (endDate == null ? "NULL" : endDate) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @When("viene cancellato uno sportello Radd con dati errati:")
    public void vieneCancellatoSportelloRaddDatiErrati(Map<String, String> richiestaCancellazione) {

        try {
            raddAltClient.deleteRegistry(
                    getValue(richiestaCancellazione, RADD_UID.key),
                    getValue(richiestaCancellazione, RADD_REGISTRYID.key) == null ? null :
                            getValue(richiestaCancellazione, RADD_REGISTRYID.key).equalsIgnoreCase("corretto") ? this.registryId : getValue(richiestaCancellazione, RADD_REGISTRYID.key),
                    getValue(richiestaCancellazione, RADD_END_VALIDITY.key) == null ? null :
                            dataTableTypeRaddAlt.setData(getValue(richiestaCancellazione, RADD_END_VALIDITY.key)));
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
            log.info("errore: {}", e.getStatusText());
        }
    }

    @When("viene richiesta la lista degli sportelli con dati:")
    public void vieneRichiestolaListaDeiSportelliRadd(Map<String, String> dataSportello) {

        RegistriesResponse sportello = raddAltClient.retrieveRegistries(
                this.uid
                , getValue(dataSportello, RADD_FILTER_LIMIT.key) == null ? null : Integer.parseInt(getValue(dataSportello, RADD_FILTER_LIMIT.key))
                , getValue(dataSportello, RADD_FILTER_LASTKEY.key) == null ? null : getValue(dataSportello, RADD_FILTER_LASTKEY.key)
                , getValue(dataSportello, ADDRESS_RADD_CAP.key) == null ? null : getValue(dataSportello, ADDRESS_RADD_CAP.key)
                , getValue(dataSportello, ADDRESS_RADD_CITY.key) == null ? null : getValue(dataSportello, ADDRESS_RADD_CITY.key)
                , getValue(dataSportello, ADDRESS_RADD_PROVINCE.key) == null ? null : getValue(dataSportello, ADDRESS_RADD_PROVINCE.key)
                , getValue(dataSportello, RADD_EXTERNAL_CODE.key) == null ? null : getValue(dataSportello, RADD_EXTERNAL_CODE.key));
        try {

            if (!sportello.getRegistries().isEmpty() && sportello.getRegistries().size() != 0) {
                this.registryId = sportello.getRegistries().get(0).getRegistryId();
            }
            this.sportelliRaddista = sportello;

            log.info("lista sportelli: {}", sportello);

        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{endDate: " + (this.requestid == null ? "NULL" : this.requestid) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @Then("viene effettuato il controllo se la richiesta ha trovato dei sportelli")
    public void vieneControlaltoLaRichiestaDellaListaDeiSportelliRadd() {

        try {
            Assertions.assertNotNull(this.sportelliRaddista);
            Assertions.assertNotNull(this.sportelliRaddista.getRegistries());
            Assertions.assertFalse(this.sportelliRaddista.getRegistries().isEmpty());
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{Lista sportelli: " + (this.requestid == null ? "NULL" : this.requestid) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }

    }

    @Then("viene effettuato il controllo se la richiesta abbia dato lista vuota")
    public void vieneControllatoRichiestolaListaVuotaDeiSportelliRadd() {

        try {
            Assertions.assertNotNull(this.sportelliRaddista);
            Assertions.assertNotNull(this.sportelliRaddista.getRegistries());
            Assertions.assertTrue(this.sportelliRaddista.getRegistries().isEmpty());
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{Lista sportelli: " + (this.sportelliRaddista == null ? "NULL" : this.sportelliRaddista) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }

    }

    @When("viene richiesta la lista degli sportelli con dati")
    public void vieneRichiestolaListaDeiSportelliRaddConDatiErrati(Map<String, String> dataSportello) {

        try {
            raddAltClient.retrieveRegistries(
                    this.uid
                    , getValue(dataSportello, RADD_FILTER_LIMIT.key) == null ? null : Integer.parseInt(getValue(dataSportello, RADD_FILTER_LIMIT.key))
                    , getValue(dataSportello, RADD_FILTER_LASTKEY.key) == null ? null : getValue(dataSportello, RADD_FILTER_LASTKEY.key)
                    , getValue(dataSportello, ADDRESS_RADD_CAP.key) == null ? null : getValue(dataSportello, ADDRESS_RADD_CAP.key)
                    , getValue(dataSportello, ADDRESS_RADD_CITY.key) == null ? null : getValue(dataSportello, ADDRESS_RADD_CITY.key)
                    , getValue(dataSportello, ADDRESS_RADD_PROVINCE.key) == null ? null : getValue(dataSportello, ADDRESS_RADD_PROVINCE.key)
                    , getValue(dataSportello, RADD_EXTERNAL_CODE.key) == null ? null : getValue(dataSportello, RADD_EXTERNAL_CODE.key));


        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @When("viene contrallato il numero di sportelli trovati sia uguale a {int}")
    public void vieneControllatoCheVenganoRitornatiTotValori(Integer numValori) {
        try {
            assertEquals(numValori, this.sportelliRaddista.getRegistries().size());
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{Lista sportelli: " + (this.sportelliRaddista == null ? "NULL" : this.sportelliRaddista) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    public void creazioneCsv(List<Map<String, String>> dataCsv, boolean formatoCsv, List<Address> addresses) throws IOException {
        this.fileCsvName = "file" + generateRandomNumber() + ".csv";
        String fileCaricamento = "target/classes/" + this.fileCsvName;

        List<CreateRegistryRequest> csvData = dataTableTypeRaddAlt.convertToListRegistryRequestData(dataCsv, addresses);

        List<String[]> data = new ArrayList<>();

        data.add(new String[]{"paese", "citta", "provincia", "cap", "via", "dataInizioValidità", "dataFineValidità", "descrizione", "orariApertura", "coordinateGeoReferenziali", "telefono", "capacita", "exsternalCode"});
        for (int i = 0; i < csvData.size(); i++) {
            data.add(new String[]{
                    csvData.get(i).getAddress().getCountry(),
                    csvData.get(i).getAddress().getCity(),
                    csvData.get(i).getAddress().getPr(),
                    csvData.get(i).getAddress().getCap(),
                    csvData.get(i).getAddress().getAddressRow(),
                    csvData.get(i).getStartValidity(),
                    csvData.get(i).getEndValidity(),
                    csvData.get(i).getDescription(),
                    csvData.get(i).getOpeningTime(),
                    csvData.get(i).getGeoLocation().getLatitude() + "," +
                            csvData.get(i).getGeoLocation().getLongitude(),
                    csvData.get(i).getPhoneNumber(),
                    getValue(dataCsv.get(i), RADD_CAPACITY.key),
                    csvData.get(i).getExternalCode(),
            });
        }

        if (formatoCsv) {
            try (CSVWriter writer = new CSVWriter(new FileWriter(fileCaricamento, StandardCharsets.UTF_8), ';',
                    CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {
                writer.writeAll(data);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try (CSVWriter writer = new CSVWriter(new FileWriter(fileCaricamento, StandardCharsets.UTF_8), ',',
                    CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {
                writer.writeAll(data);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        this.shaCSV = B2bUtils.computeSha256(sharedSteps.getContext(), "classpath:/" + this.fileCsvName);
    }

    @Then("viene cambiato raddista con {string}")
    public void changeRaddista(String raddOperatorType) {
        setOperatorRaddJWT(raddOperatorType);
    }

    @After("@puliziaSportelli")
    public void cancellazioneSportello() {
        raddAltClient.deleteRegistry(this.uid, this.registryId, dataTableTypeRaddAlt.setData("now"));
    }

    @After("@puliziaSportelliCsv")
    public void cancellazioneSportelliCSv() {

        if (this.sportelliCsvRaddista != null) {
            for (RegistryRequestResponse sportelli : this.sportelliCsvRaddista.getItems()) {
                raddAltClient.deleteRegistry(this.uid, sportelli.getRegistryId(), dataTableTypeRaddAlt.setData("now"));
            }
        } else {
            it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCsv.RequestResponse sportello = raddAltClient.retrieveRequestItems(
                    this.uid
                    , this.requestid
                    , null
                    , null);
            for (RegistryRequestResponse sportelli : sportello.getItems()) {
                if (sportelli.getStatus().equalsIgnoreCase("ACCEPTED"))
                    raddAltClient.deleteRegistry(this.uid, sportelli.getRegistryId(), dataTableTypeRaddAlt.setData("now"));
            }
        }
    }

    private static void waitFor(Integer waitingStateCsv) {
        try {
            Thread.sleep(waitingStateCsv);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkStatusAndMessageValid(RegistryRequestResponse elem, List<Map<String, String>> csvData, List<Address> addresses) {
        it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCsv.Address addressReceived = elem.getOriginalRequest().getOriginalAddress();
        return csvData.stream()
                .anyMatch(data -> {
                    String index = data.get("index");
                    return checkStatus(elem, data) && checkInCaseOfError(elem, data)
                            && (addressReceived == null || sameAddress(addresses.get(Integer.parseInt(index)), addressReceived));
                });
    }

    private boolean checkStatus(RegistryRequestResponse elem, Map<String, String> data) {
        String status = data.get("status");
        return elem.getStatus().equalsIgnoreCase(status);
    }

    private boolean checkInCaseOfError(RegistryRequestResponse elem, Map<String, String> data) {
        String errorMessage = data.get("errorMessage");
        String status = data.get("status");
        return !status.equalsIgnoreCase("REJECTED") || errorMessage == null || (elem.getError() != null && elem.getError().equalsIgnoreCase(errorMessage));
    }

    private boolean sameAddress(Address expectedAddress,
                                it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCsv.Address actualAddress) {
        return ((actualAddress.getAddressRow() == null || actualAddress.getAddressRow().equalsIgnoreCase(expectedAddress.getAddressRow()))
                && (actualAddress.getCap() == null || actualAddress.getCap().equalsIgnoreCase(expectedAddress.getCap()))
                && (actualAddress.getCity() == null || actualAddress.getCity().equalsIgnoreCase(expectedAddress.getCity()))
                && (actualAddress.getPr() == null || actualAddress.getPr().equalsIgnoreCase(expectedAddress.getPr()))
                && (actualAddress.getCountry() == null || actualAddress.getCountry().equalsIgnoreCase(expectedAddress.getCountry())));
    }

    public <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> {
            Object key = keyExtractor.apply(t);
            if (key == null) key = new Object();
            return map.putIfAbsent(key, Boolean.TRUE) == null;
        };
    }

    private RaddOperator setOperatorRaddJWT(String raddOperatorType) {
        RaddOperator raddOperator = RaddOperator.valueOf(raddOperatorType);
        raddAltClient.setAuthTokenRadd(raddOperator.getIssuerType());
        return raddOperator;
    }

}