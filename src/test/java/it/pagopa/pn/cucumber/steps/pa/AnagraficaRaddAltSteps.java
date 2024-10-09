package it.pagopa.pn.cucumber.steps.pa;

import com.opencsv.CSVWriter;
import io.cucumber.java.After;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.service.impl.PnRaddAlternativeClientImpl;
import it.pagopa.pn.client.b2b.pa.service.utils.RaddOperator;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD.*;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD.Address;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCsv.*;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCsv.VerifyRequestResponse;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.dataTable.DataTableTypeRaddAlt;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static it.pagopa.pn.cucumber.utils.NotificationValue.generateRandomNumber;
import static it.pagopa.pn.cucumber.utils.RaddAltValue.*;

@Slf4j
public class AnagraficaRaddAltSteps {

    private static final int WAITING_ACCEPTED_STATE = 20000;
    private static final String ACCEPTED = "accepted";

    private final PnRaddAlternativeClientImpl raddAltClient;
    private final SharedSteps sharedSteps;
    private final PnPaB2bUtils pnPaB2bUtils;
    private final DataTableTypeRaddAlt dataTableTypeRaddAlt;

    private String fileCsvName;
    private String shaCSV;
    private String requestid;
    private String registryId;
    private CreateRegistryRequest sportelloRaddCrud;
    private RegistriesResponse sportelliRaddista;
    private it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCsv.RequestResponse sportelliCsvRaddista;

    private String uid = getDefaultValue(RADD_UID.key);

    private static final Integer NUM_CHECK_STATE_CSV = 100;
    private static final Integer WAITING_STATE_CSV = 15000;

    private String pageIndex = null;
    private List<Address> addresses = new ArrayList<>();

    @Autowired
    public AnagraficaRaddAltSteps(PnRaddAlternativeClientImpl raddAltClient, PnPaB2bUtils pnPaB2bUtils, SharedSteps sharedSteps,
          DataTableTypeRaddAlt dataTableTypeRaddAlt) {
        this.raddAltClient = raddAltClient;
        this.sharedSteps = sharedSteps;
        this.pnPaB2bUtils = pnPaB2bUtils;
        this.dataTableTypeRaddAlt = dataTableTypeRaddAlt;
    }

    @When("viene caricato il csv con dati:")
    public void vieneGeneratoIlCsv(List<Map<String, String>> dataCsv) throws IOException {
        log.info("dataCsv: {}", dataCsv);
        creazioneCsv(dataCsv,true, addresses);
        RegistryUploadRequest registryUploadRequest = new RegistryUploadRequest().checksum(this.shaCSV);

        RegistryUploadResponse responseUploadCsv = raddAltClient.uploadRegistryRequests(this.uid, registryUploadRequest);
        try {
            Assertions.assertNotNull(responseUploadCsv);
            Assertions.assertNotNull(responseUploadCsv.getRequestId());
            Assertions.assertNotNull(responseUploadCsv.getSecret());
            Assertions.assertNotNull(responseUploadCsv.getUrl());
            Assertions.assertNotNull(responseUploadCsv.getFileKey());
            this.requestid = responseUploadCsv.getRequestId();
            pnPaB2bUtils.preloadRadCSVDocument("classpath:/" + this.fileCsvName,this.shaCSV,responseUploadCsv,true);

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
                pnPaB2bUtils.preloadRadCSVDocument("classpath:/" + this.fileCsvName,this.shaCSV,responseUploadCsv,true);
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
            pnPaB2bUtils.preloadRadCSVDocument("classpath:/" + this.fileCsvName,this.shaCSV,responseUploadCsv,true);
        }
    }

    @When("viene controllato lo stato di caricamento del csv a {string}")
    public void vieneControllatoLoStatoDelCsv(String stato) {

        VerifyRequestResponse responseUploadCsv = null;
        //TODO: utilizzare algoritmo di polling
        for (int i = 0; i < NUM_CHECK_STATE_CSV; i++) {
            responseUploadCsv = raddAltClient.verifyRequest(this.uid, this.requestid);

            if(stato.equalsIgnoreCase("DONE") && responseUploadCsv.getStatus().equalsIgnoreCase("REPLACED")) {
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
            Assertions.assertEquals(stato.toUpperCase(), responseUploadCsv.getStatus());
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
            Assertions.assertEquals("REJECTED", responseVerifyCsv.getStatus());
            Assertions.assertEquals(mesaggioErrore, responseVerifyCsv.getError());

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

        it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCsv.RequestResponse sportello= raddAltClient.retrieveRequestItems(
                getValue(dataSportello, RADD_UID.key)
                ,  getValue(dataSportello, RADD_REQUESTID.key) == null ? null :
                        getValue(dataSportello, RADD_REQUESTID.key).equalsIgnoreCase("corretto")? this.requestid : getValue(dataSportello, RADD_REQUESTID.key)
                , getValue(dataSportello, RADD_FILTER_LIMIT.key) == null ? null : Integer.parseInt(getValue(dataSportello, RADD_FILTER_LIMIT.key))
                , getValue(dataSportello, RADD_FILTER_LASTKEY.key) == null ? null : getValue(dataSportello, RADD_FILTER_LASTKEY.key));

        try {
            Assertions.assertNotNull(sportello);
            Assertions.assertNotNull(sportello.getItems());

            log.info("lista sportelli: {}",sportello);

            for (int i = 0 ; i < sportello.getItems().size(); i++) {
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
            it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCsv.RequestResponse sportello = raddAltClient.retrieveRequestItems(
                    getValue(dataSportello, RADD_UID.key)
                    , getValue(dataSportello, RADD_REQUESTID.key) == null ? null : getValue(dataSportello, RADD_REQUESTID.key)
                    , getValue(dataSportello, RADD_FILTER_LIMIT.key) == null ? null : Integer.parseInt(getValue(dataSportello, RADD_FILTER_LIMIT.key))
                    , getValue(dataSportello, RADD_FILTER_LASTKEY.key) == null ? null : getValue(dataSportello, RADD_FILTER_LASTKEY.key));

            this.sportelliCsvRaddista= sportello;
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @When("si controlla che il sportello sia in stato {string}")
    public void vieneCercatoloSportelloEControlloStato(String status) {
        RegistryRequestResponse dato = IntStream.range(0, NUM_CHECK_STATE_CSV)
                .mapToObj(numCheck -> {
                    return getRequestResponse(status);
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        log.info("sportello trovato: '{}'", dato);

        try {
            Assertions.assertNotNull(dato);
            Assertions.assertEquals(status, dato.getStatus());
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
            Assertions.assertEquals(csvData.size(), dato.size());

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
            Assertions.assertEquals(csvData.size(), dato.size());

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
        List<RegistryRequestResponse> registryRequestResponse = getRegistryRequestResponse(csvData);
        return registryRequestResponse;
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
                ,  this.requestid
                , 100
                , null);
    }

    private it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCsv.RequestResponse retrieveSportelloFromCSV() {
        RequestResponse response = raddAltClient.retrieveRequestItems(
                this.uid
                ,  this.requestid
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
        CreateRegistryResponse creationResponse=raddAltClient.addRegistry(this.uid,dataSportello);

        try {
            Assertions.assertNotNull(creationResponse);
            Assertions.assertNotNull(creationResponse.getRequestId());

            this.requestid=creationResponse.getRequestId();
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
    public void vieneModificatoSportelloRadd(Map<String,String> datiAggiornamento) {

        UpdateRegistryRequest aggiornamentoSportelloRadd = dataTableTypeRaddAlt.convertUpdateRegistryRequest(datiAggiornamento);

        try {
            raddAltClient.updateRegistry(
                    getValue(datiAggiornamento, RADD_UID.key),
                    getValue(datiAggiornamento, RADD_REGISTRYID.key) == null ? null :
                            getValue(datiAggiornamento, RADD_REGISTRYID.key).equalsIgnoreCase("corretto")? this.registryId : getValue(datiAggiornamento, RADD_REGISTRYID.key),
                    aggiornamentoSportelloRadd);
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
            log.info("errore: {}",e.getStatusText());
        }
    }

    @When("viene cancellato uno sportello Radd con dati:")
    public void vieneCancellatoSportelloRadd(Map<String,String> richiestaCancellazione) {
        String endDate = getValue(richiestaCancellazione,RADD_END_VALIDITY.key);

        if (endDate!=null) {
            if (endDate.toLowerCase().contains("corretto")) {
                endDate = this.sportelloRaddCrud.getStartValidity();
            } else {
                endDate = dataTableTypeRaddAlt.setData(endDate);
            }
        }

        log.info("data cancellazione sportello: {}",endDate);

        try {
            String finalEndDate = endDate;
            Assertions.assertDoesNotThrow(()-> raddAltClient.deleteRegistry(this.uid, this.registryId, finalEndDate));
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{endDate: " + (endDate== null ? "NULL" : endDate) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @When("viene cancellato uno sportello Radd con dati errati:")
    public void vieneCancellatoSportelloRaddDatiErrati(Map<String,String> richiestaCancellazione) {

        try {
            raddAltClient.deleteRegistry(
                    getValue(richiestaCancellazione, RADD_UID.key),
                    getValue(richiestaCancellazione, RADD_REGISTRYID.key)==null ? null:
                            getValue(richiestaCancellazione, RADD_REGISTRYID.key).equalsIgnoreCase("corretto")? this.registryId: getValue(richiestaCancellazione, RADD_REGISTRYID.key),
                    getValue(richiestaCancellazione,RADD_END_VALIDITY.key)==null? null :
                            dataTableTypeRaddAlt.setData(getValue(richiestaCancellazione,RADD_END_VALIDITY.key)));
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
            log.info("errore: {}",e.getStatusText());
        }
    }

    @When("viene richiesta la lista degli sportelli con dati:")
    public void vieneRichiestolaListaDeiSportelliRadd(Map<String, String> dataSportello) {

        RegistriesResponse sportello= raddAltClient.retrieveRegistries(
                this.uid
                , getValue(dataSportello, RADD_FILTER_LIMIT.key) == null ? null : Integer.parseInt(getValue(dataSportello, RADD_FILTER_LIMIT.key))
                , getValue(dataSportello, RADD_FILTER_LASTKEY.key) == null ? null : getValue(dataSportello, RADD_FILTER_LASTKEY.key)
                , getValue(dataSportello, ADDRESS_RADD_CAP.key) == null ? null : getValue(dataSportello, ADDRESS_RADD_CAP.key)
                , getValue(dataSportello, ADDRESS_RADD_CITY.key) == null ? null : getValue(dataSportello, ADDRESS_RADD_CITY.key)
                , getValue(dataSportello, ADDRESS_RADD_PROVINCE.key) == null ? null : getValue(dataSportello, ADDRESS_RADD_PROVINCE.key)
                , getValue(dataSportello, RADD_EXTERNAL_CODE.key) == null ? null : getValue(dataSportello, RADD_EXTERNAL_CODE.key));
        try {

            if(sportello.getRegistries().isEmpty() || sportello.getRegistries().size()==0) {
                this.sportelliRaddista =sportello;
            }else {
                this.registryId = sportello.getRegistries().get(0).getRegistryId();
                this.sportelliRaddista =sportello;
            }

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
            Assertions.assertEquals(numValori,this.sportelliRaddista.getRegistries().size());
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
                    csvData.get(i).getGeoLocation().getLatitude()+","+
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

        this.shaCSV = pnPaB2bUtils.computeSha256("classpath:/" + this.fileCsvName);
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

        if(this.sportelliCsvRaddista!=null){
            for (RegistryRequestResponse sportelli:this.sportelliCsvRaddista.getItems()) {
                raddAltClient.deleteRegistry(this.uid, sportelli.getRegistryId(), dataTableTypeRaddAlt.setData("now"));
            }
        }else{
            it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCsv.RequestResponse sportello= raddAltClient.retrieveRequestItems(
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

    private boolean checkStatus(RegistryRequestResponse elem, Map<String, String > data) {
        String status = data.get("status");
        return elem.getStatus().equalsIgnoreCase(status);
    }

    private boolean checkInCaseOfError(RegistryRequestResponse elem, Map<String, String > data) {
        String errorMessage = data.get("errorMessage");
        String status = data.get("status");
        return  !status.equalsIgnoreCase("REJECTED") || errorMessage == null || (elem.getError() != null && elem.getError().equalsIgnoreCase(errorMessage));
    }

    private boolean sameAddress(Address expectedAddress,
                                it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCsv.Address actualAddress) {
        return  ((actualAddress.getAddressRow() == null || actualAddress.getAddressRow().equalsIgnoreCase(expectedAddress.getAddressRow()))
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