package it.pagopa.pn.cucumber.steps.pa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD_V2.Problem;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCsv.*;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.dataTable.DataTableTypeRaddAlt;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import software.amazon.awssdk.regions.Region;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static it.pagopa.pn.cucumber.utils.NotificationValue.generateRandomNumber;
import static it.pagopa.pn.cucumber.utils.RaddAltValue.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
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
    //protected String xPagopaPnCxId = getDefaultValue(RADD_PN_CX_ID.key);
    protected String xPagopaPnCxId = "98765432109";
    protected String locationId;
    protected RegistryV2 registryV2Response;
    protected String PARTNER_ID_NOT_VALID = "#@#";

    protected int lastHttpStatus;

    @Autowired
    public AnagraficaRaddAltSteps(PnRaddAlternativeClientImpl raddAltClient, PnRaddAlternativeV2ClientImpl raddAltClientV2, SharedSteps sharedSteps, DataTableTypeRaddAlt dataTableTypeRaddAlt, SettableAuthTokenRaddCognito settableAuthTokenRaddCognito) {
        this.raddAltClient = raddAltClient;
        this.raddAltClientV2 = raddAltClientV2;
        this.sharedSteps = sharedSteps;
        this.dataTableTypeRaddAlt = dataTableTypeRaddAlt;
        this.settableAuthTokenRaddCognito = settableAuthTokenRaddCognito;
    }


    // STEPS API V2

    @After("@deleteNewSite")
    public void afterApiRaddV2() {
        //vieneCancellatoSportelloRaddV2();
        vieneCancellatoSportelloRaddV2ConErrore();
        log.info("Delete from @After");
    }


    @Given("l' utente con username {string} password {string} e clientId {string} richiede e riceve un token valido tramite cognito")
    public void getTokenCognito(String username, String password, String clientId) {

        AuthenticatorCognito authenticator = new AuthenticatorCognito(username, password, clientId, Region.EU_SOUTH_1);
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

    /*
     * tipocontrollo= "tutti" : controllo su tutti i campi ;
     * tipocontrollo= "obbligatori" : controllo sui soli campi mandatory"
     * */
    @And("la response registry V2 della lettura deve avere i campi {string} valorizzati")
    public void validateMandatoryFieldsInRegistryRead(String tipocontrollo) {

        this.registryV2Response = this.getRegistryResponseV2.getItems().get(0);
        this.checkFieldsRegistryResponseV2(tipocontrollo);
    }

    private boolean isValidString(String value) {
        return value != null && !value.isBlank();
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

        this.registryV2Response = response.getItems().stream()
                .filter(x -> locationId.equals(x.getLocationId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Nessun RegistryV2 trovato con il locationId atteso: " + locationId));
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
                log.info("GetRegistryResponseV2 response: {}", sportello);
            });
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{endDate: " + (this.requestid == null ? "NULL" : this.requestid) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @When("viene richiesta la lista degli sportelli Radd V2 con errore")
    public void listaSportelliRaddV2Error(Map<String, String> dataSportello) {

        try {
            raddAltClientV2.retrieveRegistries(
                    this.xPagopaPnCxId,
                    getValue(dataSportello, RADD_FILTER_LIMIT.key) == null ? null : Integer.parseInt(getValue(dataSportello, RADD_FILTER_LIMIT.key)),
                    getValue(dataSportello, RADD_FILTER_LASTKEY.key) == null ? null : getValue(dataSportello, RADD_FILTER_LASTKEY.key));

        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @Then("viene impostato un partenr Id non valido")
    public void setPartenIdNotValid() {

        xPagopaPnCxId = PARTNER_ID_NOT_VALID;
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

    @Then("cancello i registriV2 con externalCode:")
    public void deleteRegistryV2ByexternalCode(List<String> externalCodes) {
        GetRegistryResponseV2 response = this.getRegistryResponseV2;

        for (String externalCode : externalCodes) {
            Optional<RegistryV2> registryToDelete = response.getItems().stream()
                    .filter(r -> r.getExternalCodes() != null && r.getExternalCodes().contains(externalCode))
                    .findFirst();

            if (registryToDelete.isPresent()) {
                String locationIdTmp = registryToDelete.get().getLocationId();

                try {
                    vieneCancellatoSportelloRaddV2Parameter(locationIdTmp);
                    System.out.println("Registry con locationId " + locationIdTmp + " eliminato (externalCode: " + externalCode + ").");
                } catch (RestClientException e) {
                    throw new RuntimeException("Errore durante la cancellazione del registry con locationId " + locationIdTmp, e);
                }
            } else {
                throw new AssertionError("Nessun registry trovato con externalCode: " + externalCode);
            }
        }
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

            log.info("Response inserimento: {}", creationResponse);

        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{Response Upload CSV: " + (creationResponse == null ? "NULL" : creationResponse) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @When("viene generato uno sportello Radd V2 con restituzione errore con dati:")
    public void vieneGeneratoConErroreSportelloRaddV2(@Transpose CreateRegistryRequestV2 dataSportelloV2) {
        try {
            this.createRegistryRequestV2 = dataSportelloV2;
            RegistryV2 response = raddAltClientV2.addRegistry(xPagopaPnCxId, dataSportelloV2);
            this.registryV2Response = response;
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
    public void upDateSportelloRaddV2LocationId(Map<String, String> datiAggiornamento) {

        UpdateRegistryRequestV2 aggiornamentoSportelloRadd = dataTableTypeRaddAlt.convertUpdateRegistryRequestV2(datiAggiornamento);

        String locationIdTmp;

        if (datiAggiornamento.containsKey("locationId")) {
            String value = datiAggiornamento.get("locationId");
            locationIdTmp = "NULL".equalsIgnoreCase(value) ? null : value;
        } else {
            locationIdTmp = this.locationId;
        }
        try {
            RegistryV2 response = raddAltClientV2.updateRegistry(this.xPagopaPnCxId, locationIdTmp, aggiornamentoSportelloRadd);

            this.registryV2Response = response;
            this.updateRegistryRequestV2 = aggiornamentoSportelloRadd;

            log.info("Response Body:\n{}", response.toString());
            log.info("Request Body:\n{}", aggiornamentoSportelloRadd.toString());

        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
            log.info("errore: {}", e.getStatusText());
            log.info("Request Body:\n{}", aggiornamentoSportelloRadd.toString());
        }
    }

    @When("viene cancellato lo sportello Radd V2 appena inserito tramite locationId con status code")
    public void vieneCancellatoSportelloRaddV2HttpInfo() {

        try {
            ResponseEntity<Void> response = Assertions.assertDoesNotThrow(
                    () -> raddAltClientV2.deleteRegistryWithHttpInfo(xPagopaPnCxId, registryV2Response.getLocationId()),
                    "La chiamata a deleteRegistryWithHttpInfo ha lanciato un'eccezione"
            );
            Assertions.assertTrue(response.getStatusCode().is2xxSuccessful(), "La cancellazione non è andata a buon fine");
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            this.locationId = registryV2Response.getLocationId();

        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage();
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @When("viene cancellato lo sportello Radd V2 appena inserito tramite locationId")
    public void vieneCancellatoSportelloRaddV2() {

        if (registryV2Response == null || registryV2Response.getLocationId() == null) {
            log.warn("registryV2Response o locationId null: nessuno sportello da cancellare.");
            return;
        }
        try {
            Assertions.assertDoesNotThrow(
                    () -> raddAltClientV2.deleteRegistry(xPagopaPnCxId, registryV2Response.getLocationId()),
                    "La chiamata a deleteRegistry ha lanciato un'eccezione"
            );
            this.locationId = registryV2Response.getLocationId();
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage();
            throw new AssertionFailedError(
                    message,
                    assertionFailedError.getCause()
            );
        }
    }

    /*
     * tipocontrollo= "ASSENTE" : verifica che il location id non sia restituito nella redsponse di lettura sedi (quindi cancellato correttamente)
     * altrimenti ne verifica la presenza
     * */
    @Then("verifica che il locationId oggetto della cancellazione è {string} nella response di lettura")
    public void responseDoesNotContainLocationId(String tipoControllo) {

        GetRegistryResponseV2 response = this.getRegistryResponseV2;

        boolean trovato = response.getItems().stream()
                .anyMatch(registry -> registry.getLocationId() != null &&
                        registry.getLocationId().equals(locationId));


        if (tipoControllo.equalsIgnoreCase("ASSENTE"))
            assertFalse(trovato,
                    "La response contiene un RegistryV2 con locationId: " + locationId + " " + response.toString());
        else
            assertTrue(trovato,
                    "La response contiene un RegistryV2 con locationId: " + locationId + " " + response.toString());

        log.info("RegistryV2 response: {}", response.toString());
    }

    @When("viene cancellato lo sportello Radd V2 appena inserito tramite locationId: {string} con errore")
    public void vieneCancellatoSportelloRaddV2Parameter(String locationId) {

        String locationIdTmp = "NULL".equalsIgnoreCase(locationId) ? null : locationId;
        log.info("location id to delete: {}", locationId);
        try {
            raddAltClientV2.deleteRegistryWithHttpInfo(xPagopaPnCxId, locationIdTmp);
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @When("viene cancellato lo sportello Radd V2 appena inserito tramite locationId: {string} e partnerId: {string} con errore")
    public void dancellatoSportelloRaddV2Parameters(String locationId, String partnerId) {

        String locationIdTmp = "NULL".equalsIgnoreCase(locationId) ? null : locationId;
        String partnerIdTmp = "NULL".equalsIgnoreCase(locationId) ? null : partnerId;

        try {
            raddAltClientV2.deleteRegistryWithHttpInfo(partnerIdTmp, locationIdTmp);
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);

        }
    }

    @When("viene cancellato lo sportello Radd V2 appena inserito con partnerId: {string} con errore")
    public void deleteSportelloRaddV2Parameter(String partnerId) {

        String partnerIdTmp = "NULL".equalsIgnoreCase(partnerId) ? null : partnerId;

        try {
            raddAltClientV2.deleteRegistryWithHttpInfo(partnerIdTmp, locationId);
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @When("viene cancellato lo sportello Radd V2 appena inserito tramite locationId con errore")
    public void vieneCancellatoSportelloRaddV2ConErrore() {

        if (registryV2Response == null || registryV2Response.getLocationId() == null) {
            log.warn("registryV2Response o locationId null: nessuno sportello da cancellare.");
            return;
        }
        try {
            raddAltClientV2.deleteRegistryWithHttpInfo(xPagopaPnCxId, registryV2Response.getLocationId());
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @Then("l'operazione Radd V2 ha prodotto un errore con status code {string}")
    public void operationProducedAnError(String statusCode) {

        HttpStatusCodeException httpStatusCodeException = this.sharedSteps.consumeNotificationError();

        String createRequestStr = (this.createRegistryRequestV2 != null)
                ? this.createRegistryRequestV2.toString()
                : "createRegistryRequestV2 is null";

        String updateRequestStr = (this.updateRegistryRequestV2 != null)
                ? this.updateRegistryRequestV2.toString()
                : "updateRegistryRequestV2 is null";

        String locationIdStr = (this.locationId != null)
                ? this.locationId.toString()
                : "locationId is null";

        assertThat(httpStatusCodeException)
                .as("L'eccezione httpStatusCodeException non dovrebbe essere nulla. "
                                + "createRegistryRequestV2: %s, updateRegistryRequestV2: %s, locationId: %s",
                        createRequestStr, updateRequestStr, locationIdStr)
                .isNotNull();

        String actualStatusCode = httpStatusCodeException.getStatusCode().toString().substring(0, 3);

        assertThat(actualStatusCode)
                .as("Il codice di stato HTTP non corrisponde a quello atteso. Atteso: %s, Ottenuto: %s. "
                                + "createRegistryRequestV2: %s, updateRegistryRequestV2: %s, locationId: %s",
                        statusCode, actualStatusCode, createRequestStr, updateRequestStr, locationIdStr)
                .isEqualTo(statusCode);
    }

    @Then("la response V2 deve aver restiutito in automatico startValidity odierno in formato yyyy-MM-dd")
    public void checkStartValidityIsToday() {
        String startValidity = registryV2Response.getStartValidity();
        if (startValidity == null) {
            throw new AssertionError("startValidity è null");
        }
        System.out.println("startValidity: '" + startValidity + "'");

        String datePart = startValidity.length() >= 10 ? startValidity.substring(0, 10) : startValidity;

        LocalDate parsedDate = LocalDate.parse(datePart); // usa default ISO yyyy-MM-dd
        LocalDate today = LocalDate.now(ZoneId.of("Europe/Rome"));

        if (!parsedDate.equals(today)) {
            throw new AssertionError("startValidity (" + parsedDate + ") != oggi (" + today + ")");
        }
    }

    @Then("la response V2 a seguito del nuovo inserimento deve contenere i valori attesi")
    public void checkResponseValueFromInsert(DataTable dataTable) {
        Map<String, String> expectedData = dataTable.asMap(String.class, String.class);

        RegistryV2 response = registryV2Response;

        assertSoftly(softly -> {

            if (expectedData.get("partnerId") != null) {
                log.info("Check partnerId: atteso={} ottenuto={}", expectedData.get("partnerId"), response.getPartnerId());
                softly.assertThat(response.getPartnerId()).isEqualTo(expectedData.get("partnerId"));
            }

            //update
            if (expectedData.get("description") != null) {
                log.info("Check description: atteso={} ottenuto={}", expectedData.get("description"), response.getDescription());
                softly.assertThat(response.getDescription()).isEqualTo(expectedData.get("description"));
            }

            //update
            if (expectedData.get("email") != null) {
                log.info("Check email: atteso={} ottenuto={}", expectedData.get("email"), response.getEmail());
                softly.assertThat(response.getEmail()).isEqualTo(expectedData.get("email"));
            }

            //update
            if (expectedData.get("appointmentRequired") != null) {
                log.info("Check appointmentRequired: atteso={} ottenuto={}", expectedData.get("appointmentRequired"), response.getAppointmentRequired());
                softly.assertThat(response.getAppointmentRequired())
                        .isEqualTo(Boolean.valueOf(expectedData.get("appointmentRequired")));
            }

            //update
            if (expectedData.get("externalCodes") != null) {
                List<String> expectedCodes = Arrays.stream(expectedData.get("externalCodes").split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());
                log.info("Check externalCodes: atteso={} ottenuto={}", expectedCodes, response.getExternalCodes());
                softly.assertThat(response.getExternalCodes())
                        .containsExactlyInAnyOrderElementsOf(expectedCodes);
            }

            //update
            if (expectedData.get("phoneNumbers") != null) {
                List<String> expectedPhones = Arrays.stream(expectedData.get("phoneNumbers").split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());
                log.info("Check phoneNumbers: atteso={} ottenuto={}", expectedPhones, response.getPhoneNumbers());
                softly.assertThat(response.getPhoneNumbers())
                        .containsExactlyInAnyOrderElementsOf(expectedPhones);
            }

            //update
            if (expectedData.get("openingTime") != null) {
                log.info("Check openingTime: atteso={} ottenuto={}", expectedData.get("openingTime"), response.getOpeningTime());
                softly.assertThat(response.getOpeningTime()).isEqualTo(expectedData.get("openingTime"));
            }

            //update
            if (expectedData.get("endValidity") != null) {
                log.info("Check endValidity: atteso={} ottenuto={}", expectedData.get("endValidity"), response.getEndValidity());
                softly.assertThat(response.getEndValidity()).isEqualTo(expectedData.get("endValidity"));
            }

            //update
            if (expectedData.get("website") != null) {
                log.info("Check website: atteso={} ottenuto={}", expectedData.get("website"), response.getWebsite());
                softly.assertThat(response.getWebsite()).isEqualTo(expectedData.get("website"));
            }

            if (expectedData.get("startValidity") != null) {
                log.info("Check startValidity: atteso={} ottenuto={}", expectedData.get("startValidity"), response.getEndValidity());
                softly.assertThat(response.getStartValidity()).isEqualTo(expectedData.get("startValidity"));
            }

            softly.assertThat(response.getNormalizedAddress())
                    .withFailMessage("normalizedAddress non deve essere null quando ci sono valori attesi")
                    .isNotNull();

            NormalizedAddress address = response.getNormalizedAddress();

            if (expectedData.get("province") != null) {
                log.info("Check province normalizzato: atteso={} ottenuto={}", expectedData.get("province"), address.getProvince());
                softly.assertThat(address.getProvince())
                        .withFailMessage("province normalizzato non corrisponde: atteso=%s ottenuto=%s",
                                expectedData.get("province"), address.getProvince())
                        .isEqualTo(expectedData.get("province"));
            }

            if (expectedData.get("addressRow") != null) {
                log.info("Check addressRow normalizzato: atteso={} ottenuto={}", expectedData.get("addressRow"), address.getAddressRow());
                softly.assertThat(address.getAddressRow())
                        .withFailMessage("addressRow normalizzato non corrisponde: atteso=%s ottenuto=%s",
                                expectedData.get("addressRow"), address.getAddressRow())
                        .isEqualTo(expectedData.get("addressRow"));
            }

            if (expectedData.get("cap") != null) {
                log.info("Check cap normalizzato: atteso={} ottenuto={}", expectedData.get("cap"), address.getCap());
                softly.assertThat(address.getCap())
                        .withFailMessage("CAP normalizzato non corrisponde: atteso=%s ottenuto=%s",
                                expectedData.get("cap"), address.getCap())
                        .isEqualTo(expectedData.get("cap"));
            }

            if (expectedData.get("city") != null) {
                log.info("Check city normalizzato: atteso={} ottenuto={}", expectedData.get("city"), address.getCity());
                assertThat(address.getCity())
                        .withFailMessage("city normalizzato non corrisponde: atteso=%s ottenuto=%s",
                                expectedData.get("city"), address.getCity())
                        .isEqualTo(expectedData.get("city"));
            }

            if (expectedData.get("country") != null) {
                log.info("Check country normalizzato: atteso={} ottenuto={}", expectedData.get("country"), address.getCountry());
                softly.assertThat(address.getCountry())
                        .withFailMessage("country normalizzato non corrisponde: atteso=%s ottenuto=%s",
                                expectedData.get("country"), address.getCountry())
                        .isEqualTo(expectedData.get("country"));
            }

            AddressV2 addressV2 = response.getAddress();

            if (expectedData.get("address_province") != null) {
                log.info("Check province in input: atteso={} ottenuto={}", expectedData.get("address_province"), addressV2.getProvince());
                softly.assertThat(addressV2.getProvince())
                        .withFailMessage("province in input non corrisponde: atteso=%s ottenuto=%s",
                                expectedData.get("address_province"), addressV2.getProvince())
                        .isEqualTo(expectedData.get("address_province"));
            }

            if (expectedData.get("address_addressRow") != null) {
                log.info("Check addressRow in input: atteso={} ottenuto={}", expectedData.get("address_addressRow"), addressV2.getAddressRow());
                softly.assertThat(addressV2.getAddressRow())
                        .withFailMessage("addressRow in input non corrisponde: atteso=%s ottenuto=%s",
                                expectedData.get("address_addressRow"), addressV2.getAddressRow())
                        .isEqualTo(expectedData.get("address_addressRow"));
            }

            if (expectedData.get("address_cap") != null) {
                log.info("Check cap in input: atteso={} ottenuto={}", expectedData.get("address_cap"), addressV2.getCap());
                softly.assertThat(addressV2.getCap())
                        .withFailMessage("CAP in input non corrisponde: atteso=%s ottenuto=%s",
                                expectedData.get("address_cap"), addressV2.getCap())
                        .isEqualTo(expectedData.get("address_cap"));
            }

            if (expectedData.get("address_city") != null) {
                log.info("Check city in input: atteso={} ottenuto={}", expectedData.get("address_city"), addressV2.getCity());
                softly.assertThat(addressV2.getCity())
                        .withFailMessage("city in input non corrisponde: atteso=%s ottenuto=%s",
                                expectedData.get("address_city"), addressV2.getCity())
                        .isEqualTo(expectedData.get("address_city"));
            }

            if (expectedData.get("address_country") != null) {
                log.info("Check country in input: atteso={} ottenuto={}", expectedData.get("address_country"), addressV2.getCountry());
                softly.assertThat(addressV2.getCountry())
                        .withFailMessage("country in input non corrisponde: atteso=%s ottenuto=%s",
                                expectedData.get("address_country"), addressV2.getCountry())
                        .isEqualTo(expectedData.get("address_country"));
            }
        });
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

    /*
     * tipocontrollo= "tutti" : controllo su tutti i campi ;
     * tipocontrollo= "obbligatori" : controllo sui soli campi mandatory"
     * */
    @Then("la response registry V2 deve avere i campi {string} valorizzati")
    public void checkFieldsRegistryResponseV2(String tipoControllo) {
        RegistryV2 response = registryV2Response;

        assertThat(response)
                .withFailMessage("La response V2 non deve essere null")
                .isNotNull();

        NormalizedAddress addr = response.getNormalizedAddress();
        assertThat(addr).withFailMessage("NormalizedAddress mancante").isNotNull();

        //Campi obbligatori sempre controllati
        assertTrue(isValidString(addr.getAddressRow()), "addressRow normalizzato mancante o vuoto");
        assertTrue(isValidString(addr.getCap()), "cap normalizzato mancante o vuoto");
        assertTrue(isValidString(addr.getCity()), "city normalizzato mancante o vuoto");
        assertTrue(isValidString(addr.getProvince()), "province normalizzato mancante o vuoto");
        assertTrue(isValidString(addr.getCountry()), "country normalizzato mancante o vuoto");
        assertNotNull(addr.getBiasPoint(), "biasPoint mancante o vuoto");
        assertNotNull(addr.getLatitude(), "latitude mancante");
        assertNotNull(addr.getLongitude(), "longitude mancante");

        AddressV2 addressV2 = response.getAddress();

        assertTrue(isValidString(addressV2.getAddressRow()), "addressRow mancante o vuoto");
        assertTrue(isValidString(addressV2.getCap()), "cap mancante o vuoto");
        assertTrue(isValidString(addressV2.getCity()), "city mancante o vuoto");
        assertTrue(isValidString(addressV2.getProvince()), "province mancante o vuoto");
        assertTrue(isValidString(addressV2.getCountry()), "country mancante o vuoto");

        //campi opzionali
        if ("tutti".equalsIgnoreCase(tipoControllo)) {
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
        }

        log.info("RegistryV2 response: {}", response.toString());
    }


    @Then("la response registry V2 deve avere i campi correttamente formattati")
    public void checkAdvancedRegistryResponseFields() {
        RegistryV2 response = registryV2Response;

        assertThat(response)
                .withFailMessage("La response V2 non deve essere null")
                .isNotNull();

        NormalizedAddress addr = response.getNormalizedAddress();
        assertThat(addr).withFailMessage("NormalizedAddress mancante").isNotNull();

        assertSoftly(softly -> {

            // addressRow: lettere/numeri/spazi/punti/virgole/apostrofi/trattini, lunghezza 5-100
            if (addr.getAddressRow() != null) {
                softly.assertThat(addr.getAddressRow())
                        .withFailMessage("addressRow non valido: deve contenere solo lettere/numeri/spazi/punti/virgole/apostrofi/trattini e avere lunghezza 5-100")
                        .matches("^[a-zA-Z0-9 .,\\-'’]{5,100}$");
            }

            // cap: esattamente 5 cifre
            if (addr.getCap() != null) {
                softly.assertThat(addr.getCap())
                        .withFailMessage("CAP non valido: deve contenere esattamente 5 cifre")
                        .matches("^\\d{5}$");
            }

            // province: due lettere maiuscole
            if (addr.getProvince() != null) {
                softly.assertThat(addr.getProvince())
                        .withFailMessage("Province non valida: deve contenere solo due lettere maiuscole (es. RM)")
                        .matches("^[A-Z]{2}$");
            }

            // latitude: valore numerico tra -90 e 90
            if (addr.getLatitude() != null) {
                softly.assertThat(Double.parseDouble(addr.getLatitude()))
                        .withFailMessage("Latitude non valida: deve essere compresa tra -90 e 90")
                        .isBetween(-90.0, 90.0);
            }

            // longitude: valore numerico tra -180 e 180
            if (addr.getLongitude() != null) {
                softly.assertThat(Double.parseDouble(addr.getLongitude()))
                        .withFailMessage("Longitude non valida: deve essere compresa tra -180 e 180")
                        .isBetween(-180.0, 180.0);
            }

            // biasPoint
            if (addr.getBiasPoint() != null) {
                NormalizedAddressAllOfBiasPoint biasPoint = addr.getBiasPoint();

                if (biasPoint.getCountry() != null) {
                    softly.assertThat(biasPoint.getCountry())
                            .withFailMessage("biasPoint Country deve essere 0 o 1")
                            .isIn(BigDecimal.ZERO, BigDecimal.ONE);
                }

                if (biasPoint.getLocality() != null) {
                    softly.assertThat(biasPoint.getLocality())
                            .withFailMessage("biasPoint Locality deve essere 0 o 1")
                            .isIn(BigDecimal.ZERO, BigDecimal.ONE);
                }
                if (biasPoint.getOverall() != null) {
                    softly.assertThat(biasPoint.getOverall())
                            .withFailMessage("biasPoint Overall deve essere 0 o 1")
                            .isBetween(BigDecimal.ZERO, BigDecimal.ONE);
                }

                if (biasPoint.getAddressNumber() != null) {
                    softly.assertThat(biasPoint.getAddressNumber())
                            .withFailMessage("biasPoint AddressNumber deve essere 0 o 1")
                            .isIn(BigDecimal.ZERO, BigDecimal.ONE);
                }
                if (biasPoint.getSubRegion() != null) {
                    softly.assertThat(biasPoint.getSubRegion())
                            .withFailMessage("biasPoint SubRegion deve essere 0 o 1")
                            .isIn(BigDecimal.ZERO, BigDecimal.ONE);
                }

                if (biasPoint.getPostalCode() != null) {
                    softly.assertThat(biasPoint.getPostalCode())
                            .withFailMessage("biasPoint PostCode deve essere 0 o 1")
                            .isIn(BigDecimal.ZERO, BigDecimal.ONE);
                }
            }

            // description → lunghezza tra 0 e 200
            if (response.getDescription() != null) {
                int len = response.getDescription().length();
                softly.assertThat(len > 0 || len < 200)
                        .withFailMessage("Description deve avere lunghezza >0 o <200, trovata: %s", len)
                        .isTrue();
            }

            // phoneNumbers → massimo 2
            if (response.getPhoneNumbers() != null) {
                softly.assertThat(response.getPhoneNumbers())
                        .as("phoneNumbers")
                        .hasSizeLessThanOrEqualTo(2);
            }
            // partnerId → esattamente 11 cifre
            if (response.getPartnerId() != null) {
                softly.assertThat(response.getPartnerId())
                        .withFailMessage("Il partnerId deve contenere esattamente 11 cifre numeriche")
                        .matches("^\\d{11}$");
            }

            // email → pattern
            if (response.getEmail() != null) {
                softly.assertThat(response.getEmail())
                        .withFailMessage("Formato email non valido")
                        .matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

            }
            // website → pattern URL valido
            if (response.getWebsite() != null) {
                softly.assertThat(response.getWebsite())
                        .withFailMessage("Formato URL non valido")
                        .matches("^(https?:\\/\\/)?([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(\\/[^\\s]*)?$");
            }

            // startValidity → yyyy-MM-dd
            if (response.getStartValidity() != null) {
                softly.assertThat(response.getStartValidity())
                        .withFailMessage("startValidity non deve essere nel formato yyyy-MM-dd")
                        .matches("^\\d{4}-\\d{2}-\\d{2}$");
            }

            // endValidity → formato yyyy-MM-dd e > startValidity
            if (response.getEndValidity() != null && response.getStartValidity() != null) {
                softly.assertThat(response.getEndValidity())
                        .withFailMessage("endValidity deve essere nel formato yyyy-MM-dd")
                        .matches("^\\d{4}-\\d{2}-\\d{2}$");

                try {
                    LocalDate end = LocalDate.parse(response.getEndValidity());
                    LocalDate start = LocalDate.parse(response.getStartValidity());
                    softly.assertThat(end.isAfter(start))
                            .withFailMessage("endValidity (%s) deve essere successiva a startValidity (%s)", end, start)
                            .isTrue();
                } catch (DateTimeParseException e) {
                    fail("startValidity o endValidity non sono nel formato corretto: " + e.getMessage());
                }
            }

            // externalCodes → lunghezza > 4
            if (response.getExternalCodes() != null && !response.getExternalCodes().isEmpty()) {
                String firstCode = response.getExternalCodes().get(0);
                softly.assertThat(firstCode)
                        .withFailMessage("Il primo externalCode non deve essere null")
                        .isNotNull();
                        //.hasSizeGreaterThan(4);
            }

            // appointmentRequired → booleano
            if (response.getAppointmentRequired() != null) {
                softly.assertThat(response.getAppointmentRequired())
                        .as("appointmentRequired deve essere booleano").isInstanceOf(Boolean.class);
            }

            // partnerType → deve essere "CAF"
            if (response.getPartnerType() != null) {
                softly.assertThat(response.getPartnerType())
                        .withFailMessage("partnerType deve essere 'CAF'")
                        .isEqualTo("CAF");
            }

            if (response.getCreationTimestamp() != null && response.getUpdateTimestamp() != null) {
                softly.assertThat(response.getUpdateTimestamp().isAfter(response.getCreationTimestamp()) ||
                                response.getUpdateTimestamp().isEqual(response.getCreationTimestamp()))
                        .withFailMessage("updateTimestamp deve essere successivo o uguale a creationTimestamp")
                        .isTrue();
            }

            // creationTimestamp → deve essere non null (già validato come OffsetDateTime)
            softly.assertThat(response.getCreationTimestamp())
                    .withFailMessage("creationTimestamp non deve essere null")
                    .isNotNull();

            // updateTimestamp → deve essere non null
            softly.assertThat(response.getUpdateTimestamp())
                    .withFailMessage("updateTimestamp non deve essere null")
                    .isNotNull();

            log.info("Controlli avanzati completati per RegistryV2: {}", response.toString());
        });
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
                , 200
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

    // --- (START) --- PN-17459 | PN-17678 --- (START) --- //
    @When("aggiorno la sede RADD tramite PATCH impostando")
    public void aggiornoLaSedeRaddTramitePatchImpostando(DataTable dataTable) {
        Map<String, String> values = dataTable.asMaps().get(0);
        UpdateRegistryRequestV2 request = new UpdateRegistryRequestV2();
        Coordinates coordinates = new Coordinates();
        coordinates.setLatitude(mapDouble(values.get("latitude")));
        coordinates.setLongitude(mapDouble(values.get("longitude")));
        request.setCoordinates(coordinates);
        this.updateRegistryRequestV2 = request;
        executePatch(request);
    }

    @When("aggiorno la sede RADD tramite PUT Selective impostando")
    public void aggiornoLaSedeRaddTramitePutSelectiveImpostando(DataTable table) {
        Map<String, String> row = table.asMaps().get(0);
        String field = row.get("field");
        String value = mapValue(row.get("value"));

        SelectiveUpdateRegistryRequestV2 request = buildSelectivePutRequestFromCreationRequest();
        applyPutSelectiveField(request, field, value);
        executePutSelective(request);
    }

    @When("aggiorno la sede RADD tramite PUT Selective utilizzando la request {string} impostando")
    public void updateSelective(String requestType, DataTable table) {

        Map<String, String> row = table.asMaps().get(0);
        String field = row.get("field");
        String value = mapValue(row.get("value"));
        SelectiveUpdateRegistryRequestV2 request;
        if ("ALWAYS".equals(requestType)) request = new SelectiveUpdateRegistryRequestV2Always(buildSelectivePutRequestFromCreationRequest());
        else if ("NON_NULL".equals(requestType)) request = buildSelectivePutRequestFromCreationRequest();
        else throw new IllegalStateException("requestType non valido.");

        applyPutSelectiveField(request, field, value);

        executePutSelective(request);
    }

    @Then("la response deve restituire status code {int}")
    public void laResponseDeveRestituireStatusCode(int expectedStatusCode) {
        Assertions.assertEquals(expectedStatusCode, lastHttpStatus);
    }

    @Then("se lo status della response è 400, il messaggio di errore deve contenere il messaggio generato da tipo {string} e valore {string}")
    public void seLoStatusDellaResponseE400IlMessaggioDiErroreDeveContenere(String expectedErrorType, String testedValueString) throws JsonProcessingException {
        if (lastHttpStatus != 400) return;
        HttpStatusCodeException e = sharedSteps.getNotificationError();
        assertNotNull(e, "Attesa una risposta di errore HTTP ma nessuna eccezione è stata intercettata");
        String body = e.getResponseBodyAsString();
        ObjectMapper mapper = new ObjectMapper();
        Problem problem = mapper.readValue(body, Problem.class);
        String expectedDetail = buildExpectedErrorMessage(expectedErrorType, testedValueString);
        boolean found = problem.getErrors().stream().anyMatch(err -> expectedDetail.contains(err.getDetail()));
        assertTrue(found, "Messaggio di errore atteso non trovato. Atteso: " + expectedDetail + " - Body: " + body);
    }

    @Then("se lo status della response è 200, la response deve contenere i valori corretti per lat {string} e lon {string}")
    public void seLoStatusDellaResponseE200LaResponseDeveContenereINuoviValori(String expectedLat, String expectedLon) {
        if (lastHttpStatus != 200) return;
        assertNotNull(registryV2Response, "registryV2Response è null");
        String actualLat = registryV2Response != null ? registryV2Response.getNormalizedAddress().getLatitude() : null;
        String actualLon = registryV2Response != null ? registryV2Response.getNormalizedAddress().getLongitude() : null;
        assertEquals(expectedLat, actualLat, "Latitude non corretta. Attesa: " + expectedLat + " trovata: " + actualLat);
        assertEquals(expectedLon, actualLon, "Longitude non corretta. Attesa: " + expectedLon + " trovata: " + actualLon);
    }

    private SelectiveUpdateRegistryRequestV2 buildSelectivePutRequestFromCreationRequest() {
        if (createRegistryRequestV2 == null) throw new IllegalStateException("CreateRegistryRequestV2 non presente.");
        else if( createRegistryRequestV2.getAddress() == null) throw new IllegalStateException("CreateRegistryRequestV2.address non presente");
        SelectiveUpdateRegistryRequestV2 request = new SelectiveUpdateRegistryRequestV2();
        request.setAddress(cloneAddressFromCreation());
        request.setDescription("Descrizione RADD");
        request.setPhoneNumbers(List.of("33312345678"));
        request.setExternalCodes(List.of("EXT01QA"));
        return request;
    }

    private AddressV2 cloneAddressFromCreation() {
        if (createRegistryRequestV2 == null || createRegistryRequestV2.getAddress() == null) throw new IllegalStateException("CreateRegistryRequestV2 o address non presenti");
        AddressV2 src = createRegistryRequestV2.getAddress();
        return new AddressV2().addressRow(src.getAddressRow()).cap(src.getCap()).city(src.getCity()).province(src.getProvince()).country(src.getCountry());
    }

    private String mapValue(String value){
        if(StringUtils.equalsIgnoreCase(value, "NULL")) return null;
        else if(StringUtils.equalsIgnoreCase(value, "BLANK")) return "";
        else if(StringUtils.equalsIgnoreCase(value, "<201_characters>")) return "A".repeat(201);
        else return value;
    }

    private Double mapDouble(String value) {
        if (value == null) return null;
        else if ("NULL".equalsIgnoreCase(value)) return null;
        try { return Double.valueOf(value); }
        catch (NumberFormatException e) { throw e; }
    }

    private void applyPutSelectiveField(SelectiveUpdateRegistryRequestV2 request, String field, String value){
        if (request == null) throw new IllegalStateException("request non presente");
        else if (request.getAddress() == null) throw new IllegalStateException("address non presente");

        switch (field) {
            case "NOT_MANDATORY_NULL":
                request.setEmail(null);
                request.setOpeningTime(null);
                request.setEndValidity(null);
                request.setWebsite(null);
                request.setAppointmentRequired(false);
                break;
            case "NOT_MANDATORY_BLANK":
                request.setEmail("");
                request.setOpeningTime("");
                request.setEndValidity("");
                request.setWebsite("");
                break;
            case "description":
                request.setDescription(value);
                break;
            case "phoneNumbers":
                if (value == null) request.setPhoneNumbers(null);
                else if ("[]".equals(value)) request.setPhoneNumbers(List.of());
                else request.setPhoneNumbers(Arrays.stream(value.split(",")).map(String::trim).toList());
                break;
            case "externalCodes":
                if (value == null) request.setExternalCodes(null);
                else if ("[]".equals(value)) request.setExternalCodes(List.of());
                else request.setExternalCodes(Arrays.stream(value.split(",")).map(String::trim).toList());
                break;
            case "addressRow":
                request.getAddress().setAddressRow(value);
                break;
            case "addressCap":
                request.getAddress().setCap(value);
                break;
            case "addressCity":
                request.getAddress().setCity(value);
                break;
            case "addressProvince":
                request.getAddress().setProvince(value);
                break;
            case "addressCountry":
                request.getAddress().setCountry(value);
                break;
            case "email":
                request.setEmail(value);
                break;
            case "openingTime":
                request.setOpeningTime(value);
                break;
            case "startValidity":
                request.setStartValidity(value);
                break;
            case "endValidity":
                request.setEndValidity(value);
                break;
            case "website":
                request.setWebsite(value);
                break;
            case "appointmentRequired":
                request.setAppointmentRequired(value == null ? null : Boolean.parseBoolean(value));
                break;
            default:
                throw new IllegalArgumentException("Campo non gestito nel PUT Selective: " + field);
        }
    }

    private String buildExpectedErrorMessage(String errorType, String value) {
        switch (errorType) {
            case "RANGE_MAX_LAT":
                return "Validation errors: [format attribute \"double\" not supported, numeric instance is greater than the required maximum (maximum: 90, found: " + value + ")]";
            case "RANGE_MIN_LAT":
                return "Validation errors: [numeric instance is lower than the required minimum (minimum: -90, found: " + value + "), format attribute \"double\" not supported]";
            case "RANGE_MAX_LON":
                return "Validation errors: [format attribute \"double\" not supported, numeric instance is greater than the required maximum (maximum: 180, found: " + value + ")]";
            case "RANGE_MIN_LON":
                return "Validation errors: [format attribute \"double\" not supported, numeric instance is lower than the required minimum (minimum: -180, found: " + value + ")]";
            case "NULL_LAT":
                return "Validation errors: [instance type (null) does not match any allowed primitive type (allowed: [\"integer\",\"number\"]), format attribute \"double\" not supported]";
            case "NULL_LON":
                return "Validation errors: [instance type (null) does not match any allowed primitive type (allowed: [\"integer\",\"number\"]), format attribute \"double\" not supported]";
            case "NULL_LAT_LON":
                return "Validation errors: [instance type (null) does not match any allowed primitive type (allowed: [\"integer\",\"number\"]), format attribute \"double\" not supported]";
            default:
                return "UNKNOWN_ERROR";
        }
    }

    private void executePutSelective(SelectiveUpdateRegistryRequestV2 request) {
        try {
            registryV2Response = raddAltClientV2.selectiveUpdateRegistry(xPagopaPnCxId, locationId, request);
            lastHttpStatus = HttpStatus.OK.value();
        } catch (HttpStatusCodeException e) {
            lastHttpStatus = e.getStatusCode().value();
            sharedSteps.setNotificationError(e);
        }
    }

    private void executePatch(UpdateRegistryRequestV2 request) {
        try {
            RegistryV2 response = raddAltClientV2.updateRegistry( this.xPagopaPnCxId, this.locationId, request );
            this.registryV2Response = response;
            this.lastHttpStatus = HttpStatus.OK.value();
        } catch (HttpStatusCodeException e) {
            this.lastHttpStatus = e.getStatusCode().value();
            this.sharedSteps.setNotificationError(e);
        }
    }
    // --- (END) --- PN-17459 | PN-17678 --- (END) --- //

}