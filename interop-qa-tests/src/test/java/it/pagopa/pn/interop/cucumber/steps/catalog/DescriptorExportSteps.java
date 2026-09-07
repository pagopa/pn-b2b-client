package it.pagopa.pn.interop.cucumber.steps.catalog;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.AsyncExchangeProperties;
import it.pagopa.interop.generated.openapi.clients.bff.model.FileResource;
import it.pagopa.pn.interop.cucumber.steps.DocumentMetadata;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.utility.BlobFileCreator;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
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
import java.time.OffsetDateTime;
import java.util.*;

public class DescriptorExportSteps {
    /**
     * Campi di {@link EServiceSeed} confrontati con la radice del configuration.json.
     * Il naming è 1:1 fra seed e file esportato, quindi il nome del campo è anche il JSON pointer relativo.
     */
    private static final List<String> E_SERVICE_SEED_FIELDS = List.of(
            "name", "description", "technology", "mode", "isConsumerDelegable", "isClientAccessDelegable");

    /**
     * Campi di {@link UpdateEServiceDescriptorSeed} confrontati con il nodo {@code /descriptor} del configuration.json.
     * {@code attributes} e {@code asyncExchangeProperties} sono esclusi: il primo non è esportato con la stessa forma,
     * il secondo ha una verifica dedicata.
     */
    private static final List<String> DESCRIPTOR_SEED_FIELDS = List.of(
            "audience", "voucherLifespan", "dailyCallsPerConsumer", "dailyCallsTotal", "agreementApprovalPolicy");

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
    private String packageRoot = null;
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

        verifyTopLevelConfigurationFields(softly);
        verifyDescriptorConfigurationFields(softly);

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

        verifyDocumentsAgainstContext(softly, packageRoot);

        softly.assertAll();
    }

    private String readExportedPackage() throws IOException {
        zipEntries.clear();
        zipEntryContents.clear();
        configJson = null;
        packageRoot = null;

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

    private void verifyTopLevelConfigurationFields(SoftAssertions softly) {
        assertMatchesSeed(softly,
                "",
                sharedStepsContext.getEServicesCommonContext().getEServiceSeed(),
                "e-service seed",
                E_SERVICE_SEED_FIELDS);
    }

    private void verifyDescriptorConfigurationFields(SoftAssertions softly) {
        assertMatchesSeed(softly,
                descriptorPath,
                sharedStepsContext.getEServicesCommonContext().getDescriptorSeed(
                        sharedStepsContext.getEServicesCommonContext().getDescriptorId()),
                "descriptor seed",
                DESCRIPTOR_SEED_FIELDS);
    }

    /**
     * Confronta in blocco i campi indicati fra il seed usato in fase di creazione (source of truth in contesto)
     * e il corrispondente nodo del configuration.json esportato, sfruttando il naming 1:1 fra i due modelli.
     * Il confronto avviene su {@link JsonNode}, quindi verifica contemporaneamente tipo e valore.
     */
    private void assertMatchesSeed(SoftAssertions softly, String basePointer, Object seed, String seedDescription, List<String> fields) {
        softly.assertThat(seed).as("expected %s in test context", seedDescription).isNotNull();
        if (seed == null) {
            return;
        }

        JsonNode expectedSeedNode = objectMapper.valueToTree(seed);
        fields.forEach(field -> {
            String pointer = basePointer + "/" + field;
            JsonNode expectedNode = expectedSeedNode.at("/" + field);
            JsonNode actualNode = configJson.at(pointer);

            softly.assertThat(isValued(expectedNode))
                    .as("expected value for %s must be valued in the %s (test context)", pointer, seedDescription)
                    .isTrue();
            softly.assertThat(isValued(actualNode))
                    .as("%s must be present and valued in configuration.json", pointer)
                    .isTrue();

            if (isValued(expectedNode) && isValued(actualNode)) {
                softly.assertThat(jsonEquals(expectedNode, actualNode))
                        .as("%s in configuration.json: expected %s but was %s", pointer, expectedNode, actualNode)
                        .isTrue();
            }
        });
    }

    private boolean isValued(JsonNode node) {
        return node != null && !node.isMissingNode() && !node.isNull();
    }

    /**
     * Uguaglianza fra nodi tollerante rispetto alla rappresentazione numerica (es. {@code IntNode} vs {@code LongNode})
     * e all'ordinamento degli array, irrilevante per i campi di configurazione confrontati (es. {@code audience}).
     */
    private boolean jsonEquals(JsonNode expected, JsonNode actual) {
        if (expected.isArray() && actual.isArray()) {
            return expected.size() == actual.size() && toNodeSet(expected).equals(toNodeSet(actual));
        }
        if (expected.isNumber() && actual.isNumber()) {
            return expected.decimalValue().compareTo(actual.decimalValue()) == 0;
        }
        return expected.equals(actual);
    }

    private Set<JsonNode> toNodeSet(JsonNode arrayNode) {
        Set<JsonNode> nodes = new HashSet<>();
        arrayNode.forEach(nodes::add);
        return nodes;
    }

    private void verifyDocumentsAgainstContext(SoftAssertions softly, String packageRoot) {
        // Step 1: validazione strutturale del nodo docs nel configuration.json.
        // Se il nodo non e un array, il metodo registra il problema e interrompe i controlli successivi.
        String docsPath = descriptorPath + "/docs";
        JsonNode docsNode = configJson.at(docsPath);
        softly.assertThat(docsNode.getNodeType())
                .as("descriptor docs node in configuration.json (%s)", docsPath)
                .isEqualTo(JsonNodeType.ARRAY);

        // Step 2: recupero del riferimento atteso dal contesto scenario.
        // Questo e il punto da aggiornare se cambia la source of truth dei metadati caricati.
        List<DocumentMetadata> expectedDocuments = sharedStepsContext.getEServicesCommonContext().getDocumentsMetadata();
        softly.assertThat(expectedDocuments)
                .as("expected descriptor docs metadata in test context")
                .isNotNull();
        if (expectedDocuments == null || !docsNode.isArray()) {
            return;
        }

        // Step 3: controllo di cardinalita (numero documenti esportati vs numero documenti attesi).
        softly.assertThat(docsNode.size())
                .as("descriptor docs entries count in configuration.json (%s)", docsPath)
                .isEqualTo(expectedDocuments.size());

        // Step 4: indicizzazione dei documenti esportati per prettyName.
        // Se in futuro il matching dovesse avvenire su un altro campo (es. id/path), intervenire qui.
        Map<String, JsonNode> docsByPrettyName = new HashMap<>();
        docsNode.forEach(doc -> {
            String prettyName = doc.at("/prettyName").textValue();
            String path = doc.at("/path").textValue();

            softly.assertThat(prettyName)
                    .as("document prettyName in configuration.json (%s)", docsPath)
                    .isNotNull();
            softly.assertThat(path)
                    .as("document path in configuration.json (%s)", docsPath)
                    .isNotNull();

            if (prettyName != null) {
                docsByPrettyName.put(prettyName, doc);
            }
        });

        // Step 5: verifica puntuale di ogni documento atteso:
        // - presenza entry in configuration.json
        // - risoluzione del file nello zip
        // - confronto contenuto file esportato vs file originale caricato
        for (DocumentMetadata expectedDocument : expectedDocuments) {
            softly.assertThat(expectedDocument.getPrettyName())
                    .as("expected document prettyName in test context")
                    .isNotNull();
            softly.assertThat(expectedDocument.getUploadPath())
                    .as("expected uploaded document path in test context for prettyName %s", expectedDocument.getPrettyName())
                    .isNotNull();

            String expectedPrettyName = expectedDocument.getPrettyName();
            if (expectedPrettyName == null) {
                continue;
            }

            JsonNode configuredDocument = docsByPrettyName.get(expectedPrettyName);
            softly.assertThat(configuredDocument)
                    .as("document with prettyName %s in configuration.json", expectedPrettyName)
                    .isNotNull();
            if (configuredDocument == null) {
                continue;
            }

            String configuredPath = configuredDocument.at("/path").textValue();
            String entryName = resolveEntryName(packageRoot, configuredPath);
            softly.assertThat(entryName)
                    .as("document entry in zip for prettyName %s (declared path: %s)", expectedPrettyName, configuredPath)
                    .isNotNull();

            if (entryName != null && expectedDocument.getUploadPath() != null) {
                try {
                    // Step 6: confronto byte-to-byte del contenuto.
                    // Se serve una policy diversa (hash, normalizzazione, ecc.), intervenire in questo punto.
                    verifyEntryContentMatchesUploadedFile(
                            entryName,
                            expectedDocument.getUploadPath(),
                            "document %s content is not coherent with uploaded file".formatted(expectedPrettyName)
                    );
                } catch (IOException e) {
                    softly.fail("Unable to compare document %s content".formatted(expectedPrettyName), e);
                }
            }
        }
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
        UpdateEServiceDescriptorSeed expectedDescriptorSeed = sharedStepsContext.getEServicesCommonContext().getDescriptorSeed(
                sharedStepsContext.getEServicesCommonContext().getDescriptorId());
        AsyncExchangeProperties expectedAsyncProperties = expectedDescriptorSeed == null ? null : expectedDescriptorSeed.getAsyncExchangeProperties();
        if (expectedAsyncProperties == null) {
            try {
                expectedAsyncProperties = clientTokenConfigurator.getProducerClient().getProducerEServiceDescriptor(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        sharedStepsContext.getEServicesCommonContext().getDescriptorId()
                ).getAsyncExchangeProperties();
            } catch (Exception e) {
                softly.fail("Unable to retrieve expected asyncExchangeProperties from context/producer descriptor", e);
                return;
            }
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
        Assertions.assertNotNull(configJson, "configuration.json not read yet: run the package verification step first");
        SoftAssertions softly = new SoftAssertions();
        verifyDocumentsAgainstContext(softly, packageRoot);
        softly.assertAll();
    }

    @Given("l'utente ha già aggiunto un documento al descrittore")
    public void userAddDocumentDescriptor() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UUID uuid = UUID.randomUUID();
        String prettyName = "Documento QA extra - " + uuid;
        Resource textDoc = blobFileCreator.createBlobTempFileWithExtension("Document " + uuid, "txt",
            "Some random text - %s".formatted(uuid).getBytes(
                StandardCharsets.UTF_8));
        UUID documentId = dataPreparationService.addDocumentToDescriptor(
                sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                sharedStepsContext.getEServicesCommonContext().getDescriptorId(),
                prettyName,
                textDoc
        );

        List<DocumentMetadata> documentsMetadata = sharedStepsContext.getEServicesCommonContext().getDocumentsMetadata();
        if (documentsMetadata == null) {
            documentsMetadata = new ArrayList<>();
            sharedStepsContext.getEServicesCommonContext().setDocumentsMetadata(documentsMetadata);
        } else if (!(documentsMetadata instanceof ArrayList)) {
            documentsMetadata = new ArrayList<>(documentsMetadata);
            sharedStepsContext.getEServicesCommonContext().setDocumentsMetadata(documentsMetadata);
        }

        documentsMetadata.add(DocumentMetadata.builder()
                .id(documentId)
                .name(textDoc.getFilename())
                .prettyName(prettyName)
                .uploadPath(extractUploadPath(textDoc))
                .createdAt(OffsetDateTime.now())
                .build());
    }

    private String extractUploadPath(Resource resource) {
        try {
            return resource.getFile().getPath();
        } catch (IOException e) {
            throw new RuntimeException("Unable to resolve uploaded document path", e);
        }
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
