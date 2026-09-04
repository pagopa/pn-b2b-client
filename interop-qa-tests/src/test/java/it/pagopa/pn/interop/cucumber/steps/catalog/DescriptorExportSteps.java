package it.pagopa.pn.interop.cucumber.steps.catalog;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.AsyncExchangeProperties;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class DescriptorExportSteps {
    private final String configurationFileName = "configuration.json";
    private final String descriptorPath = "/descriptor";
    private final String asyncExchangePath = "/asyncExchange";
    private final String asyncExchangePropertiesPath = descriptorPath + "/asyncExchangeProperties";
    private final String callbackInterfacePath = descriptorPath + "/asyncExchangeCallbackInterface";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;
    private final BlobFileCreator blobFileCreator;
    private final BFFDataPreparationService dataPreparationService;
    private JsonNode configJson = null;
    private final List<String> zipEntries = new ArrayList<>();
    private final Map<String, byte[]> zipEntryContents = new HashMap<>();


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
        verifyPackageFormattedCorrectly(false);
    }

    @Then("il pacchetto asincrono risulta correttamente formattato")
    public void verifyAsyncPackageFormattedCorrectly() throws IOException {
        verifyPackageFormattedCorrectly(true);
    }

    private void verifyPackageFormattedCorrectly(boolean expectAsyncChecks) throws IOException {
        String interfacePath = descriptorPath + "/interface";
        String interfacePrettyNamePath = interfacePath + "/prettyName";
        String interfaceFilePath = interfacePath + "/path";
        String interfacePrettyName = "Interfaccia";
        String callbackInterfacePrettyNamePath = callbackInterfacePath + "/prettyName";
        String callbackInterfaceFilePath = callbackInterfacePath + "/path";
        String callbackInterfacePrettyName = "Interfaccia Callback";

        String packageRoot = readExportedPackage();
        Assertions.assertNotNull(configJson, "configuration.json not found in exported package");

        Assertions.assertEquals(JsonNodeType.OBJECT, configJson.at(descriptorPath).getNodeType(),
                "descriptor not found in configuration.json");

        Assertions.assertEquals(JsonNodeType.OBJECT, configJson.at(interfacePath).getNodeType(),
                "interface not found in configuration.json");

        Assertions.assertEquals(interfacePrettyName, configJson.at(interfacePrettyNamePath).textValue(),
                "Unexpected interface prettyName in configuration.json");

        String interfaceFile = configJson.at(interfaceFilePath).textValue();
        Assertions.assertNotNull(interfaceFile, "Interface path not found in configuration.json");

        String interfaceEntryName = resolveEntryName(packageRoot, interfaceFile);
        Assertions.assertNotNull(interfaceEntryName, "Interface not found in zip!");

        String uploadedInterfacePath = sharedStepsContext.getEServicesCommonContext().getInterfaceUploadPath();
        Assertions.assertNotNull(uploadedInterfacePath, "Uploaded interface path not available in test context");

        verifyEntryContentMatchesUploadedFile(interfaceEntryName, uploadedInterfacePath,
                "Interface content is not coherent with uploaded interface file");

        JsonNode asyncExchangeNode = configJson.at(asyncExchangePath);

        if (expectAsyncChecks) {
            Assertions.assertTrue(asyncExchangeNode.isBoolean() && asyncExchangeNode.booleanValue(),
                    "asyncExchange flag is not true in configuration.json");

            verifyAsyncExchangeProperties();

            Assertions.assertEquals(JsonNodeType.OBJECT, configJson.at(callbackInterfacePath).getNodeType(),
                    "asyncExchangeCallbackInterface not found in configuration.json");

            Assertions.assertEquals(callbackInterfacePrettyName, configJson.at(callbackInterfacePrettyNamePath).textValue(),
                    "Unexpected callback interface prettyName in configuration.json");

            String callbackInterfaceFile = configJson.at(callbackInterfaceFilePath).textValue();
            Assertions.assertNotNull(callbackInterfaceFile, "Callback interface path not found in configuration.json");

            String callbackInterfaceEntryName = resolveEntryName(packageRoot, callbackInterfaceFile);
            Assertions.assertNotNull(callbackInterfaceEntryName, "Callback interface not found in zip!");
            Assertions.assertNotEquals(interfaceEntryName, callbackInterfaceEntryName,
                    "Interface and callback interface are mapped to the same package entry");

            String callbackUploadPath = sharedStepsContext.getEServicesCommonContext().getCallbackInterfaceUploadPath();
            Assertions.assertNotNull(callbackUploadPath, "Uploaded callback interface path not available in test context");
            verifyEntryContentMatchesUploadedFile(callbackInterfaceEntryName, callbackUploadPath,
                    "Callback interface content is not coherent with uploaded callback interface file");
        } else {
            Assertions.assertTrue(asyncExchangeNode.isMissingNode() || asyncExchangeNode.isNull() || asyncExchangeNode.isBoolean(),
                    "asyncExchange node in configuration.json is unexpectedly not boolean");
            Assertions.assertFalse(asyncExchangeNode.isBoolean() && asyncExchangeNode.booleanValue(),
                    "asyncExchange flag is unexpectedly true in configuration.json");
            Assertions.assertFalse(hasValuedFields(configJson.at(asyncExchangePropertiesPath)),
                    "asyncExchangeProperties unexpectedly valued in configuration.json");
            Assertions.assertFalse(hasValuedFields(configJson.at(callbackInterfacePath)),
                    "asyncExchangeCallbackInterface unexpectedly valued in configuration.json");
        }
    }

    private String readExportedPackage() throws IOException {
        zipEntries.clear();
        zipEntryContents.clear();
        configJson = null;
        String packageRoot = null;

        URI fileUrl = ((FileResource) httpCallExecutor.getResponse()).getUrl();
        try (InputStream byteStream = new ByteArrayInputStream(downloadFile(fileUrl));
             ZipArchiveInputStream zipStream = new ZipArchiveInputStream(byteStream)) {
            ArchiveEntry entry;
            while ((entry = zipStream.getNextEntry()) != null) {
                String entryName = entry.getName();
                zipEntries.add(entryName);
                byte[] entryContent = entry.isDirectory() ? new byte[0] : zipStream.readAllBytes();
                zipEntryContents.put(entryName, entryContent);

                if (isConfigurationEntry(entryName)) {
                    configJson = objectMapper.readTree(new String(entryContent, StandardCharsets.UTF_8));
                    packageRoot = entryName.substring(0, entryName.length() - configurationFileName.length());
                }
            }
        }
        return packageRoot;
    }

    private boolean isConfigurationEntry(String entryName) {
        String normalizedEntryName = normalizePath(entryName);
        return normalizedEntryName.equals(configurationFileName)
                || normalizedEntryName.endsWith("/" + configurationFileName);
    }

    private String resolveEntryName(String packageRoot, String path) {
        if (path == null) {
            return null;
        }
        String expectedEntryName = normalizePath((packageRoot == null ? "" : packageRoot) + path);
        return zipEntries.stream()
                .filter(name -> normalizePath(name).equals(expectedEntryName))
                .findFirst()
                .orElse(null);
    }

    private String normalizePath(String path) {
        return path.replace('\\', '/').replaceAll("^\\./", "");
    }

    private boolean hasValuedFields(JsonNode jsonNode) {
        return jsonNode != null && jsonNode.isObject() && jsonNode.properties().stream()
                .anyMatch(field -> !field.getValue().isNull());
    }

    private void verifyEntryContentMatchesUploadedFile(String entryName, String uploadedFilePath, String assertionMessage) throws IOException {
        byte[] exportedFile = zipEntryContents.get(entryName);
        byte[] expectedFile = Files.readAllBytes(Path.of(uploadedFilePath));
        Assertions.assertArrayEquals(expectedFile, exportedFile, assertionMessage);
    }

    private void verifyAsyncExchangeProperties() {
        AsyncExchangeProperties expectedAsyncProperties = sharedStepsContext.getEServicesCommonContext().getAsyncExchangeProperties();
        if (expectedAsyncProperties == null) {
            expectedAsyncProperties = clientTokenConfigurator.getProducerClient().getProducerEServiceDescriptor(
                    sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                    sharedStepsContext.getEServicesCommonContext().getDescriptorId()
            ).getAsyncExchangeProperties();
        }

        Assertions.assertNotNull(expectedAsyncProperties,
                "Expected asyncExchangeProperties not available in test context nor in producer descriptor");

        JsonNode asyncExchangePropertiesNode = configJson.at(asyncExchangePropertiesPath);
        Assertions.assertEquals(JsonNodeType.OBJECT, asyncExchangePropertiesNode.getNodeType(),
                "asyncExchangeProperties not found in configuration.json");

        AsyncExchangeProperties exportedAsyncProperties;
        try {
            exportedAsyncProperties = objectMapper.treeToValue(asyncExchangePropertiesNode, AsyncExchangeProperties.class);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to deserialize asyncExchangeProperties from configuration.json", e);
        }

        assertThat(exportedAsyncProperties)
                .as("Exported asyncExchangeProperties")
                .usingRecursiveComparison()
                .isEqualTo(expectedAsyncProperties);
    }

    @Then("il documento di configurazione contiene anche l’analisi del rischio compilata dall’erogatore")
    public void verifyConfigurationDocumentContainsRiskAnanlysis() {
        String riskAnalysisPath = "/riskAnalysis";
        Assertions.assertNotNull(configJson, "configuration.json not read yet: run the package verification step first");

        JsonNode riskAnalysis = configJson.at(riskAnalysisPath);
        Assertions.assertEquals(JsonNodeType.ARRAY, riskAnalysis.getNodeType(), "RiskAnalysis not found");
        Assertions.assertFalse(riskAnalysis.isEmpty(), "RiskAnalysis not found");
    }

    @Then("il pacchetto contiene anche i documenti che sono mappati nel documento di configurazione")
    public void verifyPackageContainsAllRequiredDocuments() {
        String docsPath = descriptorPath + "/docs";
        Assertions.assertNotNull(configJson, "configuration.json not read yet: run the package verification step first");

        JsonNode docs = configJson.at(docsPath);
        Assertions.assertEquals(JsonNodeType.ARRAY, docs.getNodeType(), "Descriptor docs not found in configuration.json");

        List<String> paths = new ArrayList<>();
        docs.forEach(doc -> {
            JsonNode pathNode = doc.at("/path");
            if (pathNode != null && pathNode.isTextual()) {
                paths.add(pathNode.asText());
            }
        });

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
