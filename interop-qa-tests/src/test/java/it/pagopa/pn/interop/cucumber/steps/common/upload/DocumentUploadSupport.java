package it.pagopa.pn.interop.cucumber.steps.common.upload;

import it.pagopa.interop.conf.UploadDocumentFilesProperties;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class DocumentUploadSupport {

    private final Map<String, Resource> filesByType;

    public DocumentUploadSupport(UploadDocumentFilesProperties uploadDocumentFilesProperties) {
        this.filesByType = uploadDocumentFilesProperties.asNormalizedResourceMap();
    }

    public List<UploadRequest> requestsFromTypeList(List<String> fileTypes) {
        return fileTypes.stream()
            .map(type -> {
                String normalizedType = normalize(type);
                return new UploadRequest(normalizedType, normalizedType);
            })
            .toList();
    }

    public List<UploadRequest> requestsFromTable(List<Map<String, String>> rows, String typeColumn, String extensionColumn) {
        return rows.stream()
            .map(row -> new UploadRequest(normalize(row.get(typeColumn)), normalize(row.get(extensionColumn))))
            .toList();
    }

    public void executeUploads(List<UploadRequest> requests, List<UploadAttemptResult> uploadAttempts, DocumentUploadOps ops) {
        for (UploadRequest request : requests) {
            uploadAttempts.add(attemptUpload(request, ops));
        }
    }

    public void verifyUploadAttemptsOutcome(List<UploadAttemptResult> uploadAttempts, ExpectedOutcome expectedOutcome) {
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

    public String normalize(String value) {
        return StringUtils.trimToEmpty(value).toLowerCase(Locale.ROOT);
    }

    private UploadAttemptResult attemptUpload(UploadRequest request, DocumentUploadOps ops) {
        String uploadErrorMessage = null;
        boolean uploadSucceeded;

        try {
            Resource sourceResource = resolveSourceResource(request.fileType());
            Resource uploadResource = buildUploadResource(sourceResource, request.fileExtension());
            String prettyName = buildPrettyName(request.fileType(), request.fileExtension());

            UploadOperationResult uploadResult = ops.upload(request, uploadResource, prettyName);
            uploadErrorMessage = uploadResult.errorMessage();
            uploadSucceeded = uploadResult.isSuccess();

            if (uploadSucceeded) {
                UUID uploadedDocumentId = ops.extractDocumentId(uploadResult.response());
                if (uploadedDocumentId == null) {
                    uploadSucceeded = false;
                    uploadErrorMessage = appendMessage(uploadErrorMessage, "documentId assente nella risposta upload");
                } else {
                    ops.pollDocumentAvailability(uploadedDocumentId);
                }
            }
        } catch (Exception ex) {
            uploadSucceeded = false;
            uploadErrorMessage = appendMessage(uploadErrorMessage, ex.getMessage());
        }

        return new UploadAttemptResult(request.fileType(), request.fileExtension(), uploadSucceeded, uploadErrorMessage);
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
            Path tempDirectory = Files.createTempDirectory("document-upload-");
            Path filePath = tempDirectory.resolve(fileName);
            Files.write(filePath, payload);
            filePath.toFile().deleteOnExit();
            tempDirectory.toFile().deleteOnExit();
            return new FileSystemResource(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Errore nella creazione del file temporaneo di upload", e);
        }
    }

    private String buildPrettyName(String fileType, String extension) {
        String normalizedExt = normalize(extension);
        String extToken = normalizedExt.isBlank() ? "noext" : normalizedExt;
        return "doc-" + fileType + "-" + extToken + "-" + UUID.randomUUID();
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

