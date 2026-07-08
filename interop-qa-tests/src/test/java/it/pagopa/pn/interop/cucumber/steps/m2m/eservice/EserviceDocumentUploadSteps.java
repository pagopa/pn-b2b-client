package it.pagopa.pn.interop.cucumber.steps.m2m.eservice;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.conf.UploadDocumentFilesProperties;
import it.pagopa.interop.eservice.service.IM2MEserviceDescriptorClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Document;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class EserviceDocumentUploadSteps {

    enum ExpectedOutcome {
        SUCCESS,
        FAILURE;

        boolean isSuccessExpected() {
            return this == SUCCESS;
        }
    }

    private record UploadAttemptResult(String fileType, String fileExtension, boolean success, String errorMessage) {
    }

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final PollingService pollingService;
    private final IM2MEserviceDescriptorClient descriptorClient;
    private final Map<String, Resource> filesByType;

    private final List<UploadAttemptResult> uploadAttempts = new ArrayList<>();

    public EserviceDocumentUploadSteps(
        ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        UploadDocumentFilesProperties uploadDocumentFilesProperties
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.pollingService = sharedStepsContext.getPollingService();
        this.descriptorClient = clientTokenConfigurator.getM2mEServiceDescriptorClient();
        this.descriptorClient.setHttpCallExecutor(sharedStepsContext.getHttpCallExecutor());
        this.filesByType = buildFileMap(uploadDocumentFilesProperties);
    }

    @When("l'utente tenta di caricare uno alla volta il seguente insieme di documenti")
    public void uploadDocumentsByTypeList(List<String> fileTypes) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        for (String type : fileTypes) {
            String normalizedType = normalize(type);
            attemptUpload(normalizedType, normalizedType);
        }
    }

    @When("l'utente tenta di caricare uno alla volta i seguenti tipi documenti, con l'estensione specificata")
    public void uploadDocumentsByTypeAndExtension(DataTable dataTable) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        for (Map<String, String> row : dataTable.asMaps()) {
            String documentType = normalize(row.get("documento"));
            String extension = normalize(row.get("estensione"));
            attemptUpload(documentType, extension);
        }
    }

    // TODO verificare collocazione
    @ParameterType("positivo|negativo")
    public ExpectedOutcome uploadOutcome(String expectedOutcome) {
        return switch (expectedOutcome) {
            case "positivo" -> ExpectedOutcome.SUCCESS;
            case "negativo" -> ExpectedOutcome.FAILURE;
            default -> throw new IllegalStateException("Unexpected value: " + expectedOutcome);
        };
    }

    @Then("tutti i tentativi di caricamento hanno esito {uploadOutcome}")
    public void verifyUploadAttemptsOutcome(ExpectedOutcome expectedOutcome) {
        Assertions.assertThat(uploadAttempts)
            .as("Nessun tentativo di caricamento e' stato registrato")
            .isNotEmpty();

        List<UploadAttemptResult> unexpectedResults = uploadAttempts.stream()
            .filter(result -> result.success() != expectedOutcome.isSuccessExpected())
            .toList();

        Assertions.assertThat(unexpectedResults)
            .as("Verifico che tutti i tentativi abbiano esito %s", expectedOutcome)
            .isEmpty();
    }

    private void attemptUpload(String fileType, String fileExtension) {
        HttpStatus uploadStatus;
        String uploadErrorMessage = null;
        boolean uploadSucceeded;

        try {
            UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
            UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();

            Resource sourceResource = resolveSourceResource(fileType);
            Resource uploadResource = buildUploadResource(sourceResource, fileExtension);

            String prettyName = buildPrettyName(fileType, fileExtension);
            uploadStatus = sharedStepsContext.getHttpCallExecutor().performCall(
                () -> descriptorClient.uploadDocument(eServiceId, descriptorId, uploadResource, prettyName)
            );

            uploadErrorMessage = sharedStepsContext.getHttpCallExecutor().getErrorMessage();
            Object uploadResponse = sharedStepsContext.getHttpCallExecutor().getResponse();
            uploadSucceeded = uploadStatus != null && uploadStatus.is2xxSuccessful();

            if (uploadSucceeded) {
                UUID uploadedDocumentId = extractDocumentId(uploadResponse);
                if (uploadedDocumentId == null) {
                    uploadSucceeded = false;
                    uploadErrorMessage = appendMessage(uploadErrorMessage, "documentId assente nella risposta upload");
                } else {
                    pollDocumentAvailability(eServiceId, descriptorId, uploadedDocumentId);
                }
            }
        } catch (Exception ex) {
            uploadSucceeded = false;
            uploadErrorMessage = appendMessage(uploadErrorMessage, ex.getMessage());
        }

        uploadAttempts.add(new UploadAttemptResult(fileType, fileExtension, uploadSucceeded, uploadErrorMessage));
    }

    private void pollDocumentAvailability(UUID eServiceId, UUID descriptorId, UUID documentId) {
        pollingService.makePolling(
            () -> sharedStepsContext.getHttpCallExecutor().performCall(
                () -> descriptorClient.downloadDocument(eServiceId, descriptorId, documentId)
            ),
            HttpStatus.OK::equals,
            status -> "downloadDocument non disponibile, status=" + status
        );
    }

    private Resource resolveSourceResource(String fileType) {
        Resource source = filesByType.get(fileType);
        if (source == null || !source.exists()) {
            throw new IllegalArgumentException("File non configurato per tipo: " + fileType);
        }
        return source;
    }

    private Resource buildUploadResource(Resource sourceResource, String extension) {
        String normalizedExtension = normalize(extension);
        String sourceExtension = normalize(FilenameUtils.getExtension(sourceResource.getFilename()));
        if (StringUtils.equals(sourceExtension, normalizedExtension)) {
            return sourceResource;
        }

        String suffix = normalizedExtension.isBlank() ? "" : "." + normalizedExtension;

        try {
            byte[] payload = sourceResource.getInputStream().readAllBytes();
            String fileName = "upload-" + UUID.randomUUID() + suffix;
            Path tempDirectory = Files.createTempDirectory("eservice-upload-");
            Path filePath = tempDirectory.resolve(fileName);
            Files.write(filePath, payload);
            filePath.toFile().deleteOnExit();
            tempDirectory.toFile().deleteOnExit();
            return new FileSystemResource(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Errore nella creazione del file temporaneo di upload", e);
        }
    }

    private UUID extractDocumentId(Object uploadResponse) {
        if (uploadResponse instanceof Document document) {
            return document.getId();
        }
        return null;
    }

    private String buildPrettyName(String fileType, String extension) {
        String normalizedExt = normalize(extension);
        String extToken = normalizedExt.isBlank() ? "noext" : normalizedExt;
        return "doc-" + fileType + "-" + extToken + "-" + UUID.randomUUID();
    }

    private Map<String, Resource> buildFileMap(UploadDocumentFilesProperties properties) {
        Map<String, Resource> map = new LinkedHashMap<>();
        properties.getFiles().forEach((fileType, path) -> map.put(normalize(fileType), new ClassPathResource(path)));
        return map;
    }

    private String normalize(String value) {
        return StringUtils.trimToEmpty(value).toLowerCase(Locale.ROOT);
    }


    private String appendMessage(String previousMessage, String newMessage) {
        if (newMessage == null || newMessage.isBlank()) {
            return previousMessage;
        }
        if (previousMessage == null || previousMessage.isBlank()) {
            return newMessage;
        }
        return previousMessage + " | " + newMessage;
    }
}

