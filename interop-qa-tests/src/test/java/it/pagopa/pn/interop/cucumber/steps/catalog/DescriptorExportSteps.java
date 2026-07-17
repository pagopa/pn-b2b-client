package it.pagopa.pn.interop.cucumber.steps.catalog;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.FileResource;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.utility.BlobFileCreator;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.junit.jupiter.api.Assertions;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DescriptorExportSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;
    private final BlobFileCreator blobFileCreator;
    private final BFFDataPreparationService dataPreparationService;
    private JsonObject configJson = null;
    private final List<String> zipEntries = new ArrayList<>();


    public DescriptorExportSteps(ClientTokenConfigurator clientTokenConfigurator,
                                 SharedStepsContext sharedStepsContext,
                                 BFFDataPreparationService dataPreparationService,
                                 BlobFileCreator blobFileCreator) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.dataPreparationService = dataPreparationService;
        this.blobFileCreator = blobFileCreator;
    }

    @When("l'utente effettua una richiesta di export del descrittore")
    public void userExportsDescriptor() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().exportEServiceDescriptor(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        sharedStepsContext.getEServicesCommonContext().getDescriptorId()
                )
        );
    }

    @Then("il pacchetto risulta correttamente formattato")
    public void verifyPackageFormattedCorrectly() throws IOException {
        URI fileUrl = ((FileResource) httpCallExecutor.getResponse()).getUrl();
        try (InputStream byteStream = new ByteArrayInputStream(downloadFile(fileUrl));
             ZipArchiveInputStream zipStream = new ZipArchiveInputStream(byteStream)) {
            ArchiveEntry entry;
            while ((entry = zipStream.getNextEntry()) != null) {
                String entryName = entry.getName();
                zipEntries.add(entryName);

                if (entryName.endsWith("/configuration.json")) {
                    String json = new String(zipStream.readAllBytes(), StandardCharsets.UTF_8);
                    configJson = JsonParser.parseString(json).getAsJsonObject();
                }
            }
        }
        Assertions.assertNotNull(configJson, "Configuration.json not found");

        String interfacePath = configJson
                .getAsJsonObject("descriptor")
                .getAsJsonObject("interface")
                .get("path").getAsString();

        Assertions.assertTrue(zipEntries.stream().anyMatch(name -> name.endsWith(interfacePath)),
                "Interface not found in zip!");
    }

    @Then("il documento di configurazione contiene anche l’analisi del rischio compilata dall’erogatore")
    public void verifyConfigurationDocumentContainsRiskAnanlysis() {
        JsonArray riskAnalysis = configJson.getAsJsonArray("riskAnalysis");
        Assertions.assertFalse(riskAnalysis.isEmpty(), "RiskAnalysis not found");
    }

    @Then("il pacchetto contiene anche i documenti che sono mappati nel documento di configurazione")
    public void verifyPackageContainsAllRequiredDocuments() {
        List<String> paths = new ArrayList<>();
        configJson.getAsJsonObject("descriptor")
                .getAsJsonArray("docs")
                .forEach(docs -> paths.add(docs.getAsJsonObject().get("path").getAsString()));


        paths.forEach(p -> Assertions.assertTrue(zipEntries.stream().anyMatch(entryName -> entryName.endsWith(p))));
    }

    @Given("l'utente ha già aggiunto un documento al descrittore")
    public void userAddDocumentDescriptor() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UUID uuid = UUID.randomUUID();
        Resource textDoc = blobFileCreator.createBlobTempFileWithExtension("Document " + uuid, "txt",
            "Some random text - %s".formatted(uuid).getBytes(
                StandardCharsets.UTF_8));
        dataPreparationService.addDocumentToDescriptor(
                sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                sharedStepsContext.getEServicesCommonContext().getDescriptorId(),
                null,
                textDoc
        );
    }

    private byte[] downloadFile(URI fileUrl) {
//        fileUrl = fileUrl.replace("%2F", "/");
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> response = restTemplate.getForEntity(fileUrl, byte[].class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Failed to download file: " + response.getStatusCode());
        }
        return response.getBody();
    }



}
