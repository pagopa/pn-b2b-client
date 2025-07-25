package it.pagopa.pn.interop.cucumber.steps.catalog;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.FileResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.PresignedUrl;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;
import it.pagopa.pn.interop.cucumber.utility.CommonUtils;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.function.Consumer;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class DescriptorImportSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;
    private final CommonUtils commonUtils;
    private final EServicesCommonContext eServicesCommonContext;

    private String folderName;
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

    @Given("l'utente ha già un pacchetto correttamente strutturato con un eservice in mode {string}")
    public void userAddPackageWithMode(String eserviceMode) {
        folderName = ("DELIVER".equals(eserviceMode)) ? "exportedWithDocument" : "exportedWithRiskAnalysis";
        updateAndZipConfig(folderName, configJson ->
                configJson.addProperty("name", "e-service-IMPORTED-" + sharedStepsContext.getTestSeed()), false);
    }

    @Given("l'utente ha già un pacchetto non correttamente strutturato con campi richiesti mancanti")
    public void verifyIncorrectPackagePresenceWithMissingFields() {
        folderName = "exportedWithDocument";
        updateAndZipConfig(folderName, configJson ->
                configJson.remove("name"), false);
    }

    @Given("l'utente ha già un pacchetto non correttamente strutturato con documenti mancanti nel percorso previsto")
    public void verifyIncorrectPackagePresenceWithMissingDocuments() {
        folderName = "exportedWithDocument";
        updateAndZipConfig(folderName, configJson -> {
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
        updateAndZipConfig(folderName, configJson ->
                configJson.addProperty("name", "e-service-IMPORTED-" + sharedStepsContext.getTestSeed()), true);
    }

    @Given("l'utente ha già richiesto una presignedURL per il caricamento del pacchetto")
    public void userHasAlreadyRequiredPresignedURL() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().getImportEservicePresignedUrl(String.format("%s.zip", folderName))
        );
        commonUtils.assertValidResponse();
        url = ((PresignedUrl) httpCallExecutor.getResponse()).getUrl();
    }

    @Given("è già stato caricato il pacchetto nella presignedURL")
    public void uploadPackageInPresignedURL() throws IOException {
        uploadFile(url, String.format("./%s.zip", folderName));
    }

    @When("l'utente effettua una richiesta di import del descrittore")
    public void performDescriptorImport() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().importEService(
                        new FileResource().filename(String.format("%s.zip", folderName)).url(url)
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

    private void updateAndZipConfig(String folderName, Consumer<JsonObject> updateConfig, boolean notAllowedFiles) {
        URL configUrl = getClass().getClassLoader().getResource(folderName + "/configuration.json");
        //URL notAllowedFileUrl = getClass().getClassLoader().getResource(folderName + "/notAllowedFile.txt");
        File zipFile = new File(folderName + ".zip");
        try {
            File folderPath = new File(getClass().getClassLoader().getResource(folderName).toURI());
            File configFile = new File(configUrl.toURI());
            File notAllowedFile = new File(String.format("%s/notAllowedFile.txt", folderPath.getPath()));
           // File zipFile = new File(zipFileUrl.toURI());
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

            // Create ZIP with Zip4j
            if (zipFile.exists()) {
                FileUtils.forceDelete(zipFile);
            }

            try (ZipFile zip = new ZipFile(zipFile)) {
                zip.addFolder(folderPath);
            }
        } catch (Exception e) {
            throw new RuntimeException("Errore durante l'aggiornamento del file JSON o la compressione della cartella", e);
        }
    }

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
