package it.pagopa.pn.interop.cucumber.steps.catalog;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.assertj.core.api.SoftAssertions;
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

        String packageRoot = readExportedPackage();
        Assertions.assertNotNull(configJson, "configuration.json not found in exported package");

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(configJson.at(descriptorPath).getNodeType())
                .as("descriptor node in configuration.json (%s)", descriptorPath)
                .isEqualTo(JsonNodeType.OBJECT);

        String uploadedInterfacePath = sharedStepsContext.getEServicesCommonContext().getInterfaceUploadPath();
        String interfaceEntryName = assertInterfaceEntry(
                softly,
                packageRoot,
                interfacePath,
                "Interfaccia",
                uploadedInterfacePath,
                "interface"
        );

        JsonNode asyncExchangeNode = configJson.at(asyncExchangePath);

        if (expectAsyncChecks) {
            softly.assertThat(asyncExchangeNode.isBoolean() && asyncExchangeNode.booleanValue())
                    .as("asyncExchange flag in configuration.json (%s)", asyncExchangePath)
                    .isTrue();

            verifyAsyncExchangeProperties(softly);

            String callbackUploadPath = sharedStepsContext.getEServicesCommonContext().getCallbackInterfaceUploadPath();
            String callbackInterfaceEntryName = assertInterfaceEntry(
                    softly,
                    packageRoot,
                    callbackInterfacePath,
                    "Interfaccia Callback",
                    callbackUploadPath,
                    "callback interface"
            );

            if (interfaceEntryName != null && callbackInterfaceEntryName != null) {
                softly.assertThat(callbackInterfaceEntryName)
                        .as("callback interface entry must differ from interface entry")
                        .isNotEqualTo(interfaceEntryName);
            }
        } else {
            softly.assertThat(asyncExchangeNode.getNodeType())
                    .as("asyncExchange node in configuration.json (%s) must be absent, null or boolean", asyncExchangePath)
                    .isIn(JsonNodeType.MISSING, JsonNodeType.NULL, JsonNodeType.BOOLEAN);
            softly.assertThat(asyncExchangeNode.isBoolean() && asyncExchangeNode.booleanValue())
                    .as("asyncExchange flag in configuration.json (%s) must not be true", asyncExchangePath)
                    .isFalse();
            softly.assertThat(hasValuedFields(configJson.at(asyncExchangePropertiesPath)))
                    .as("asyncExchangeProperties in configuration.json (%s) must not contain valued fields", asyncExchangePropertiesPath)
                    .isFalse();
            softly.assertThat(hasValuedFields(configJson.at(callbackInterfacePath)))
                    .as("asyncExchangeCallbackInterface in configuration.json (%s) must not contain valued fields", callbackInterfacePath)
                    .isFalse();
        }

        softly.assertAll();
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

    private String assertInterfaceEntry(SoftAssertions softly,
                                        String packageRoot,
                                        String interfacePath,
                                        String expectedPrettyName,
                                        String uploadedFilePath,
                                        String interfaceDescription) {
        String prettyNamePath = interfacePath + "/prettyName";
        String filePathPath = interfacePath + "/path";

        softly.assertThat(configJson.at(interfacePath).getNodeType())
                .as("%s node in configuration.json (%s)", interfaceDescription, interfacePath)
                .isEqualTo(JsonNodeType.OBJECT);

        softly.assertThat(configJson.at(prettyNamePath).textValue())
                .as("%s prettyName in configuration.json (%s)", interfaceDescription, prettyNamePath)
                .isEqualTo(expectedPrettyName);

        String interfaceFilePath = configJson.at(filePathPath).textValue();
        softly.assertThat(interfaceFilePath)
                .as("%s path in configuration.json (%s)", interfaceDescription, filePathPath)
                .isNotNull();

        String entryName = resolveEntryName(packageRoot, interfaceFilePath);
        softly.assertThat(entryName)
                .as("%s entry in zip (declared path: %s)", interfaceDescription, interfaceFilePath)
                .isNotNull();

        softly.assertThat(uploadedFilePath)
                .as("uploaded %s path in test context", interfaceDescription)
                .isNotNull();

        if (entryName != null && uploadedFilePath != null) {
            try {
                verifyEntryContentMatchesUploadedFile(
                        entryName,
                        uploadedFilePath,
                        "%s content is not coherent with uploaded file".formatted(interfaceDescription)
                );
            } catch (IOException e) {
                softly.fail("Unable to compare %s content".formatted(interfaceDescription), e);
            }
        }

        return entryName;
    }

    private void verifyEntryContentMatchesUploadedFile(String entryName, String uploadedFilePath, String assertionMessage) throws IOException {
        byte[] exportedFile = zipEntryContents.get(entryName);
        byte[] expectedFile = Files.readAllBytes(Path.of(uploadedFilePath));
        Assertions.assertArrayEquals(expectedFile, exportedFile, assertionMessage);
    }

    private void verifyAsyncExchangeProperties(SoftAssertions softly) {
        AsyncExchangeProperties expectedAsyncProperties;
        try {
            expectedAsyncProperties = sharedStepsContext.getEServicesCommonContext().getAsyncExchangeProperties();
            if (expectedAsyncProperties == null) {
                expectedAsyncProperties = clientTokenConfigurator.getProducerClient().getProducerEServiceDescriptor(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        sharedStepsContext.getEServicesCommonContext().getDescriptorId()
                ).getAsyncExchangeProperties();
            }
        } catch (Exception e) {
            softly.fail("Unable to retrieve expected asyncExchangeProperties from context/producer descriptor", e);
            return;
        }

        softly.assertThat(expectedAsyncProperties)
                .as("expected asyncExchangeProperties from context/producer descriptor")
                .isNotNull();

        JsonNode asyncExchangePropertiesNode = configJson.at(asyncExchangePropertiesPath);
        softly.assertThat(asyncExchangePropertiesNode.getNodeType())
                .as("asyncExchangeProperties node in configuration.json (%s)", asyncExchangePropertiesPath)
                .isEqualTo(JsonNodeType.OBJECT);

        if (expectedAsyncProperties == null || !asyncExchangePropertiesNode.isObject()) {
            return;
        }

        try {
            AsyncExchangeProperties exportedAsyncProperties = objectMapper.treeToValue(asyncExchangePropertiesNode, AsyncExchangeProperties.class);
            softly.assertThat(exportedAsyncProperties)
                    .as("exported asyncExchangeProperties")
                    .isEqualTo(expectedAsyncProperties);
        } catch (JsonProcessingException e) {
            softly.fail("Unable to deserialize asyncExchangeProperties from configuration.json", e);
        }
    }

    @Then("il documento di configurazione contiene anche l’analisi del rischio compilata dall’erogatore")
    public void verifyConfigurationDocumentContainsRiskAnanlysis() {
        String riskAnalysisPath = "/riskAnalysis";
        Assertions.assertNotNull(configJson, "configuration.json not read yet: run the package verification step first");

        JsonNode riskAnalysis = configJson.at(riskAnalysisPath);
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(riskAnalysis.getNodeType())
                .as("riskAnalysis node in configuration.json (%s)", riskAnalysisPath)
                .isEqualTo(JsonNodeType.ARRAY);
        softly.assertThat(riskAnalysis.size())
                .as("riskAnalysis entries in configuration.json (%s)", riskAnalysisPath)
                .isPositive();
        softly.assertAll();
    }

    @Then("il pacchetto contiene anche i documenti che sono mappati nel documento di configurazione")
    public void verifyPackageContainsAllRequiredDocuments() {
        String docsPath = descriptorPath + "/docs";
        Assertions.assertNotNull(configJson, "configuration.json not read yet: run the package verification step first");

        JsonNode docs = configJson.at(docsPath);
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(docs.getNodeType())
                .as("descriptor docs node in configuration.json (%s)", docsPath)
                .isEqualTo(JsonNodeType.ARRAY);

        docs.forEach(doc -> {
            String path = doc.at("/path").textValue();
            softly.assertThat(path)
                    .as("document path in configuration.json (%s)", docsPath)
                    .isNotNull();
            if (path != null) {
                softly.assertThat(zipEntries.stream().anyMatch(entryName -> entryName.endsWith(path)))
                        .as("document path %s must be present in zip entries", path)
                        .isTrue();
            }
        });
        softly.assertAll();
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
