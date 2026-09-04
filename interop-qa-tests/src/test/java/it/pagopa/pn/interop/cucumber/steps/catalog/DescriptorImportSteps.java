package it.pagopa.pn.interop.cucumber.steps.catalog;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;
import it.pagopa.pn.interop.cucumber.utility.CommonUtils;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.zip.ZipOutputStream;

public class DescriptorImportSteps {
    private static final String ZIP_EXTENSION = ".zip";

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;
    private final CommonUtils commonUtils;
    private final EServicesCommonContext eServicesCommonContext;

    private String folderName;
    private String zipFileName;
    private URI url;
    private ProducerEServiceDescriptor producerEServiceDescriptor;

    public DescriptorImportSteps(ClientTokenConfigurator clientTokenConfigurator,
                                 SharedStepsContext sharedStepsContext,
                                 CommonUtils commonUtils) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.eServicesCommonContext = sharedStepsContext.getEServicesCommonContext();
        this.commonUtils = commonUtils;
    }

    @Given("l'utente ha già un pacchetto con un eservice in mode RECEIVE ed una risk analysis obsoleta")
    public void userAddPackageWithOutdatedRiskAnalysis() {
        folderName = "exportedWithOldRiskAnalysis";
        updateAndZipConfig(configJson ->
                configJson.addProperty("name", "e-service-IMPORTED-" + sharedStepsContext.getTestSeed()), false);
    }

    @Given("l'utente ha già un pacchetto correttamente strutturato con un eservice {isAsynchronous} in mode {string}")
    public void userAddPackageWithMode(Boolean isAsync, String eserviceMode) {
        folderName = getFolderName(eserviceMode, isAsync);
        updateAndZipConfig(configJson ->
                configJson.addProperty("name", "e-service-IMPORTED-" + sharedStepsContext.getTestSeed()), false);
    }

    @Given("l'utente ha già un pacchetto correttamente strutturato con un eservice in mode {string}")
    public void userAddPackageWithMode(String eserviceMode) {
        userAddPackageWithMode(false, eserviceMode);
    }

    /**
     * Rinomina lo zip del pacchetto già preparato, senza toccarne il contenuto.
     * Utile per testare scenari in cui il nome del file caricato non corrisponde a quanto atteso.
     * <p>
     * Vincolo: opera solo su risorse già disponibili in questo momento dello scenario (lo zip locale sul filesystem),
     * quindi va invocato prima della richiesta della presignedURL (che altrimenti verrebbe generata con il nome vecchio).
     * <p>
     * Procedura: normalizza il nome richiesto (rimuovendo un'eventuale estensione ".zip" già presente), verifica che lo
     * zip corrente esista, elimina un eventuale zip di destinazione omonimo e rinomina il file. Se il nome richiesto
     * coincide con quello attuale non fa nulla.
     *
     * @param packageName nuovo nome del pacchetto, con o senza estensione ".zip"
     */
    @Given("il nome del pacchetto viene modificato in {string}")
    public void renamePackageName(String packageName) {
        String newZipBaseName = normalizeZipBaseName(packageName);
        if (newZipBaseName.equals(getZipBaseName())) {
            return;
        }

        File currentZipFile = getZipFile(getZipBaseName());
        if (!currentZipFile.exists()) {
            throw new IllegalStateException("Pacchetto non ancora creato: " + currentZipFile.getPath());
        }

        try {
            File renamedZipFile = getZipFile(newZipBaseName);
            FileUtils.deleteQuietly(renamedZipFile);
            FileUtils.moveFile(currentZipFile, renamedZipFile);
        } catch (IOException e) {
            throw new RuntimeException("Errore durante la rinomina del pacchetto", e);
        }

        zipFileName = newZipBaseName;
    }

    /**
     * Modifica il contenuto del pacchetto (l'archivio zip) in modo che contenga esattamente il numero richiesto di
     * main directory di primo livello, per testare la validazione lato server che ne pretende esattamente una.
     * <p>
     * Nota sulla semantica: il "pacchetto" è lo zip, che normalmente contiene una sola cartella di primo livello
     * (la main directory con configuration.json, interface.yaml, ecc.). Il conteggio si riferisce quindi alla radice
     * dell'archivio, non al contenuto interno della main directory.
     * <p>
     * Vincolo: opera solo su risorse già disponibili in questo momento dello scenario (la cartella template su
     * classpath e lo zip locale), quindi va invocato prima della richiesta della presignedURL.
     * <p>
     * Procedura: prepara una directory temporanea di staging in cui copia la main directory tante volte quante
     * richieste (rinominando le copie successive per evitare conflitti), quindi ricrea lo zip inserendo tali cartelle
     * come elementi di primo livello. Con 0 cartelle viene prodotto un archivio vuoto. La directory temporanea viene
     * sempre eliminata, anche in caso di errore; le risorse di test su classpath non vengono mai alterate.
     *
     * @param foldersCount numero di main directory di primo livello desiderato nello zip (>= 0)
     */
    @Given("il contenuto del pacchetto viene modificato così che contenga {int} cartelle correttamente create")
    public void changePackageContentFolderCount(int foldersCount) {
        if (foldersCount < 0) {
            throw new IllegalArgumentException("Il numero di cartelle richiesto non può essere negativo: " + foldersCount);
        }

        File packageFolder = getPackageFolder();

        // Si lavora su una copia temporanea per non alterare in modo permanente le risorse di test
        File stagingFolder = null;
        try {
            stagingFolder = Files.createTempDirectory("descriptor-import-").toFile();

            List<File> mainDirectories = new ArrayList<>();
            for (int copyIndex = 0; copyIndex < foldersCount; copyIndex++) {
                String mainDirectoryName = copyIndex == 0
                        ? folderName
                        : String.format("%s-copy-%d", folderName, copyIndex);
                File mainDirectory = new File(stagingFolder, mainDirectoryName);
                FileUtils.copyDirectory(packageFolder, mainDirectory);
                mainDirectories.add(mainDirectory);
            }

            createZipFromFolders(mainDirectories, getZipBaseName());
        } catch (IOException e) {
            throw new RuntimeException("Errore durante la modifica del contenuto del pacchetto", e);
        } finally {
            FileUtils.deleteQuietly(stagingFolder);
        }
    }

    /**
     * Determina il nome della cartella template su classpath da usare come pacchetto, in base al mode dell'e-service
     * e al fatto che sia sincrono o asincrono.
     *
     * @throws IllegalArgumentException se eserviceMode non è "DELIVER" né "RECEIVE"
     */
    private String getFolderName(String eserviceMode, Boolean isAsync) {
        return switch (eserviceMode) {
            case "DELIVER" -> Boolean.TRUE.equals(isAsync)
                    ? "exportedAsyncWithDocument"
                    : "exportedWithDocument";
            case "RECEIVE" -> Boolean.TRUE.equals(isAsync)
                    ? "exportedAsyncWithRiskAnalysis"
                    : "exportedWithRiskAnalysis";
            default -> throw new IllegalArgumentException("Invalid eservice mode: " + eserviceMode);
        };
    }

    /**
     * Nome base (senza estensione) dello zip attualmente in uso: coincide con {@link #folderName} finché non viene
     * eseguita una rinomina esplicita tramite {@link #renamePackageName(String)}, dopodiché prevale {@link #zipFileName}.
     */
    private String getZipBaseName() {
        return zipFileName != null ? zipFileName : folderName;
    }

    /** Costruisce il riferimento al file zip locale a partire dal suo nome base (senza estensione). */
    private File getZipFile(String zipBaseName) {
        return new File(zipBaseName + ZIP_EXTENSION);
    }

    /** Nome file completo (con estensione ".zip") dello zip attualmente in uso. */
    private String getZipFileName() {
        return getZipBaseName() + ZIP_EXTENSION;
    }

    /**
     * Normalizza il nome di pacchetto fornito dallo step Gherkin: rimuove spazi superflui ed eventuale estensione
     * ".zip" (case-insensitive), così che il chiamante possa indicare il nome con o senza estensione.
     *
     * @throws IllegalArgumentException se il nome risultante è vuoto
     */
    private String normalizeZipBaseName(String packageName) {
        String normalizedName = packageName == null ? "" : packageName.trim();
        if (normalizedName.toLowerCase(java.util.Locale.ROOT).endsWith(ZIP_EXTENSION)) {
            normalizedName = normalizedName.substring(0, normalizedName.length() - ZIP_EXTENSION.length());
        }

        if (normalizedName.isEmpty()) {
            throw new IllegalArgumentException("Il nome del pacchetto non può essere vuoto: " + packageName);
        }

        return normalizedName;
    }

    /**
     * Risolve, a partire dal classpath, la cartella template corrispondente a {@link #folderName}.
     *
     * @throws IllegalStateException se la risorsa non esiste sul classpath
     * @throws RuntimeException      se l'URL della risorsa non è convertibile in un percorso di filesystem valido
     */
    private File getPackageFolder() {
        URL packageUrl = getClass().getClassLoader().getResource(folderName);
        if (packageUrl == null) {
            throw new IllegalStateException("Pacchetto non trovato: " + folderName);
        }

        try {
            return new File(packageUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Errore durante il recupero della cartella del pacchetto: " + folderName, e);
        }
    }

    /**
     * Comprime (ri)creando da zero il contenuto di folderPath in uno zip locale il cui nome è derivato da zipBaseName,
     * eliminando preventivamente un eventuale zip omonimo già presente.
     */
    private void createZipFromFolder(File folderPath, String zipBaseName) throws IOException {
        createZipFromFolders(List.of(folderPath), zipBaseName);
    }

    /**
     * Crea da zero uno zip locale (derivato da zipBaseName) inserendo le cartelle indicate come elementi di primo
     * livello dell'archivio, eliminando preventivamente un eventuale zip omonimo già presente.
     * <p>
     * Se la lista è vuota viene prodotto un archivio zip valido ma privo di contenuti, caso non gestibile tramite
     * l'API di zip4j (che scrive il file solo al primo inserimento) e quindi realizzato con ZipOutputStream.
     */
    private void createZipFromFolders(List<File> folders, String zipBaseName) throws IOException {
        File zipFile = getZipFile(zipBaseName);
        FileUtils.deleteQuietly(zipFile);

        if (folders.isEmpty()) {
            try (ZipOutputStream emptyZip = new ZipOutputStream(Files.newOutputStream(zipFile.toPath()))) {
                emptyZip.finish();
            }
            return;
        }

        try (ZipFile zip = new ZipFile(zipFile)) {
            for (File folder : folders) {
                zip.addFolder(folder);
            }
        } catch (Exception e) {
            throw new IOException("Errore durante la creazione dello zip: " + zipFile.getPath(), e);
        }
    }

    @Given("l'utente ha già un pacchetto non correttamente strutturato con campi richiesti mancanti")
    public void verifyIncorrectPackagePresenceWithMissingFields() {
        folderName = "exportedWithDocument";
        updateAndZipConfig(configJson ->
                configJson.remove("name"), false);
    }

    @Given("l'utente ha già un pacchetto non correttamente strutturato con documenti mancanti nel percorso previsto")
    public void verifyIncorrectPackagePresenceWithMissingDocuments() {
        folderName = "exportedWithDocument";
        updateAndZipConfig(configJson -> {
            JsonArray docs = configJson.getAsJsonObject("descriptor").getAsJsonArray("docs");
            if (docs != null && !docs.isEmpty()) {
                JsonObject firstDoc = docs.get(0).getAsJsonObject();
                firstDoc.addProperty("path", "unknown");
            }
        }, false);
    }

    @Given("l'utente ha già un pacchetto non correttamente strutturato con file non previsti")
    public void verifyIncorrectPackagePresenceWithWrongFile() {
        folderName = "exportedWithDocument";
        updateAndZipConfig(configJson ->
                configJson.addProperty("name", "e-service-IMPORTED-" + sharedStepsContext.getTestSeed()), true);
    }

    @Given("l'utente ha già richiesto una presignedURL per il caricamento del pacchetto")
    public void userHasAlreadyRequiredPresignedURL() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().getImportEservicePresignedUrl(getZipFileName())
        );
        commonUtils.assertValidResponse();
        url = ((PresignedUrl) httpCallExecutor.getResponse()).getUrl();
    }

    /** Carica lo zip del pacchetto corrente sulla presignedURL già ottenuta tramite {@link #uploadFile}. */
    @Given("è già stato caricato il pacchetto nella presignedURL")
    public void uploadPackageInPresignedURL() throws IOException {
        uploadFile(url, "./" + getZipFileName());
    }

    @When("l'utente effettua una richiesta di import del descrittore")
    public void performDescriptorImport() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().importEService(
                        new FileResource().filename(getZipFileName()).url(url)
                )
        );
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            CreatedEServiceDescriptor createdEServiceDescriptor = ((CreatedEServiceDescriptor) httpCallExecutor.getResponse());
            eServicesCommonContext.setEserviceId(createdEServiceDescriptor.getId());
            eServicesCommonContext.setDescriptorId(createdEServiceDescriptor.getDescriptorId());
        }
    }

    @When("l'utente effettua una richiesta di import del descrittore con nome del file errato")
    public void performDescriptorImportWithWrongFilename() {
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().importEService(
                        new FileResource().filename("unknown.zip").url(url)
                )
        );
    }

    @Then("il descrittore viene correttamente creato in stato DRAFT")
    public void isDescriptorSuccessfullyCreatedWithDraftState() {
        sharedStepsContext.getPollingService().makePolling(
                () -> httpCallExecutor.performCall(
                        () -> clientTokenConfigurator.getProducerClient().getProducerEServiceDescriptor(
                                eServicesCommonContext.getEserviceId(),
                                eServicesCommonContext.getDescriptorId()
                        )
                ),
                res -> res != HttpStatus.NOT_FOUND,
                "E-Service Descriptor not found!"
        );

        producerEServiceDescriptor = clientTokenConfigurator.getProducerClient().getProducerEServiceDescriptor(
                eServicesCommonContext.getEserviceId(),
                eServicesCommonContext.getDescriptorId()
        );
        Assertions.assertEquals(EServiceDescriptorState.DRAFT, producerEServiceDescriptor.getState());
    }

    @Then("i due documenti risultano correttamente caricati")
    public void verifyDocumentsSuccessfullyUploaded() {
        sharedStepsContext.getPollingService().makePolling(
                () -> httpCallExecutor.performCall(
                        () -> clientTokenConfigurator.getProducerClient().getProducerEServiceDescriptor(
                                eServicesCommonContext.getEserviceId(),
                                eServicesCommonContext.getDescriptorId()
                        )
                ),
                res -> res != HttpStatus.NOT_FOUND
                        && ((ProducerEServiceDescriptor) httpCallExecutor.getResponse()).getDocs().size() == 2,
                "There was no E-Service Descriptor found!"
        );

        producerEServiceDescriptor = clientTokenConfigurator.getProducerClient().getProducerEServiceDescriptor(
                eServicesCommonContext.getEserviceId(),
                eServicesCommonContext.getDescriptorId()
        );

        Assertions.assertFalse(producerEServiceDescriptor.getDocs().isEmpty(), "Error: No docs found!");
    }

    @Then("l'eservice contiene l'analisi del rischio")
    public void verifyEServiceContainsRiskAnalysis() {
        Assertions.assertFalse(producerEServiceDescriptor.getEservice().getRiskAnalysis().isEmpty());
    }

    /**
     * Metodo di supporto condiviso da tutti gli step "Given" che preparano un pacchetto: legge il configuration.json
     * della cartella template (identificata da {@link #folderName}), applica eventuali normalizzazioni di base
     * (nome e-service valorizzato, path del documento "unknown" corretto), applica poi le modifiche specifiche
     * passate da updateConfig, riscrive il file e ricompatta la cartella in uno zip.
     * <p>
     * Se notAllowedFiles è true, aggiunge nella cartella un file non dichiarato nel configuration.json
     * (notAllowedFile.txt), per simulare pacchetti con contenuti non previsti; altrimenti lo rimuove se presente,
     * per garantire uno stato pulito tra scenari differenti.
     *
     * @param updateConfig    modifiche puntuali da applicare al JSON di configurazione dopo le normalizzazioni di base
     * @param notAllowedFiles se true include nel pacchetto un file non previsto dal configuration.json
     */
    private void updateAndZipConfig(Consumer<JsonObject> updateConfig, boolean notAllowedFiles) {
        zipFileName = folderName;
        try {
            File folderPath = getPackageFolder();
            File configFile = new File(folderPath, "configuration.json");
            File notAllowedFile = new File(String.format("%s/notAllowedFile.txt", folderPath.getPath()));
            // Handle notAllowedFile.txt
            if (notAllowedFiles) {
                FileUtils.write(notAllowedFile, "", StandardCharsets.UTF_8);
            } else if (notAllowedFile.exists()) {
                FileUtils.forceDelete(notAllowedFile);
            }

            // Read JSON
            String jsonStr = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);
            JsonObject configJson = JsonParser.parseString(jsonStr).getAsJsonObject();

            // Update configJson if necessary
            if (!configJson.has("name") || configJson.get("name").getAsString().isEmpty()) {
                configJson.addProperty("name", "e-service-IMPORTED-" + sharedStepsContext.getTestSeed());
            }

            JsonArray docs = configJson.getAsJsonObject("descriptor").getAsJsonArray("docs");
            if (docs != null && !docs.isEmpty()) {
                JsonObject firstDoc = docs.get(0).getAsJsonObject();
                if ("unknown".equals(firstDoc.get("path").getAsString())) {
                    firstDoc.addProperty("path", "documents/documento-test-qa.pdf");
                }
            }

            // Apply custom changes
            updateConfig.accept(configJson);

            // Write updated JSON
            FileUtils.write(configFile,
                    new GsonBuilder().setPrettyPrinting().create().toJson(configJson),
                    StandardCharsets.UTF_8);

            createZipFromFolder(folderPath, getZipBaseName());
        } catch (Exception e) {
            throw new RuntimeException("Errore durante l'aggiornamento del file JSON o la compressione della cartella", e);
        }
    }

    /**
     * Carica il contenuto binario dello zip indicato su una presignedURL tramite una PUT HTTP.
     *
     * @param fileUrl     presignedURL su cui effettuare l'upload
     * @param zipFilePath percorso locale dello zip da caricare
     * @throws RuntimeException se la risposta non ha uno status code 2xx
     */
    public void uploadFile(URI fileUrl, String zipFilePath) throws IOException {
        RestTemplate restTemplate = new RestTemplate();

        File zipFile = new File(zipFilePath);
        byte[] fileBytes = Files.readAllBytes(zipFile.toPath());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); // Or MediaType.valueOf("application/zip")
        HttpEntity<byte[]> requestEntity = new HttpEntity<>(fileBytes, headers);
        System.out.println(fileUrl);
        ResponseEntity<String> response = restTemplate.exchange(
                fileUrl,
                HttpMethod.PUT,
                requestEntity,
                String.class
        );
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("File upload failed with status code: " + response.getStatusCode());
        }
    }

}
