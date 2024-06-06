package it.pagopa.pn.cucumber.steps.pa;

import com.opencsv.CSVWriter;
import io.cucumber.java.After;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.service.impl.PnRaddAlternativeClientImpl;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableAuthTokenRadd;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static it.pagopa.pn.cucumber.steps.utilitySteps.ThreadUtils.threadWaitMilliseconds;
import static it.pagopa.pn.cucumber.utils.NotificationValue.generateRandomNumber;
import static it.pagopa.pn.cucumber.utils.RaddAltValue.*;

@Slf4j
public class AnagraficaRaddAltSteps {

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

    private static final Integer NUM_CHECK_STATE_CSV = 18;
    private static final Integer WAITING_STATE_CSV = 10000;

    @Autowired
    public AnagraficaRaddAltSteps(PnRaddAlternativeClientImpl raddAltClient, PnPaB2bUtils pnPaB2bUtils,
                                  SharedSteps sharedSteps, DataTableTypeRaddAlt dataTableTypeRaddAlt) {
        this.raddAltClient = raddAltClient;
        this.sharedSteps = sharedSteps;
        this.pnPaB2bUtils = pnPaB2bUtils;
        this.dataTableTypeRaddAlt = dataTableTypeRaddAlt;
    }

    @When("viene caricato il csv con dati:")
    public void vieneGeneratoIlCsv(List<Map<String, String>> dataCsv) throws IOException {
        log.info("dataCsv: {}", dataCsv);
        creazioneCsv(dataCsv,true);
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
        creazioneCsv(dataCsv, formatoCsv.equalsIgnoreCase("corretto"));
        RegistryUploadRequest registryUploadRequest = new RegistryUploadRequest().checksum(this.shaCSV);

        try {
            RegistryUploadResponse responseUploadCsv = raddAltClient.uploadRegistryRequests(this.uid, registryUploadRequest);
            if(responseUploadCsv!=null) {
                this.requestid = responseUploadCsv.getRequestId();
                pnPaB2bUtils.preloadRadCSVDocument("classpath:/" + this.fileCsvName,this.shaCSV,responseUploadCsv,true);
            }
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @When("viene caricato il csv 2 volte con dati:")
    public void vieneGeneratoIlCsvConResituzioneErrore(List<Map<String, String>> dataCsv) throws IOException {
        creazioneCsv(dataCsv, true);
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

            threadWaitMilliseconds(WAITING_STATE_CSV);

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

            for (int i=0;i<sportello.getItems().size();i++) {
                Assertions.assertNotNull(sportello.getItems().get(i));
                Assertions.assertNotNull(sportello.getItems().get(i).getRequestId());
                Assertions.assertNotNull(sportello.getItems().get(i).getRegistryId());
                Assertions.assertNotNull(sportello.getItems().get(i).getOriginalRequest());
                Assertions.assertNotNull(sportello.getItems().get(i).getOriginalRequest().getOriginalAddress());
            }
            this.sportelliCsvRaddista= sportello;
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


    @When("si controlla che il sporetello sia in stato {string}")
    public void vieneCercatoloSportelloEControlloStato( String status) {
        RegistryRequestResponse dato= null;

        for (int i = 0; i < NUM_CHECK_STATE_CSV; i++) {

            threadWaitMilliseconds(WAITING_STATE_CSV);

            it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCsv.RequestResponse sportello= raddAltClient.retrieveRequestItems(
                    this.uid
                    ,  this.requestid
                    , 100
                    , null);

            dato= sportello.getItems().stream().filter(elem->elem.getRequestId().equalsIgnoreCase(this.requestid) && elem.getStatus().equalsIgnoreCase(status)).findAny().orElse(null);

            if(status.equalsIgnoreCase("accepted")){
                threadWaitMilliseconds(20000);

            }

            if (dato!=null && dato.getStatus().equalsIgnoreCase(status)) {
                log.info("sporetllo status corretto: '{}'",dato);
                break;
            }


        }

        try {

            Assertions.assertEquals(dato.getStatus(),status);
            this.requestid=dato.getRequestId();
            this.registryId=dato.getRegistryId();

        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{sporettllo: " + (dato == null ? "NULL" : dato) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }


    @When("si controlla che il sporetello sia in stato REJECTED con errore {string} con dati:")
    public void vieneCercatoloSportelloEControlloStatoAReject( String errore, @Transpose CreateRegistryRequest dataSportello) {

        RegistryRequestResponse dato= this.sportelliCsvRaddista.getItems().stream().filter(elem->elem.getOriginalRequest().getOriginalAddress().getCity().equalsIgnoreCase(dataSportello.getAddress().getCity())).findAny().orElse(null);

        try {

            Assertions.assertEquals(dato.getStatus(),"REJECTED");
        Assertions.assertTrue(dato.getError().contains(errore));

        } catch (AssertionFailedError assertionFailedError) {
        String message = assertionFailedError.getMessage() +
                "{sporettllo: " + (dato == null ? "NULL" : dato) + " }";
        throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
    }
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
                    getValue(datiAggiornamento, RADD_REGISTRYID.key)==null?null:
                            getValue(datiAggiornamento, RADD_REGISTRYID.key).equalsIgnoreCase("corretto")? this.registryId: getValue(datiAggiornamento, RADD_REGISTRYID.key),
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
            }else{
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


    public void creazioneCsv(List<Map<String, String>> dataCsv, boolean formatoCsv) throws IOException {
       this.fileCsvName = "file" + generateRandomNumber() + ".csv";
        String fileCaricamento = "target/classes/" + this.fileCsvName;


        List<CreateRegistryRequest> csvData = dataTableTypeRaddAlt.convertToListRegistryRequestData(dataCsv);

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
    public void changeRaddista(String raddista) {
        switch (raddista.toLowerCase()) {
            case "issuer_1" -> raddAltClient.setAuthTokenRadd(SettableAuthTokenRadd.AuthTokenRaddType.ISSUER_1);
            case "issuer_2" -> raddAltClient.setAuthTokenRadd(SettableAuthTokenRadd.AuthTokenRaddType.ISSUER_2);
            default -> throw new IllegalArgumentException();
        }
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
            for (RegistryRequestResponse sportelli:sportello.getItems()) {
                raddAltClient.deleteRegistry(this.uid, sportelli.getRegistryId(), dataTableTypeRaddAlt.setData("now"));
            }
        }
    }

}