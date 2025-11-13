package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.context;

import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.DocumentType;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model.ArchivedDocument;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model.S3BucketInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class DocumentArchivingContext {
    private final String UNSIGNED_DOCUMENT_S3_BASE_PATH;
    private final String SIGNED_DOCUMENT_S3_BASE_PATH;
    final Map<DocumentType, S3BucketInfo> wormBuckets;
    final Map<DocumentType, S3BucketInfo> buckets;
    SharedStepsContext sharedStepsContext;
    ArchivedDocument currentFile;

    public DocumentArchivingContext(
            @Value("${s3.unsigned-document-base-path}") String unsignedDocumentBasePath,
            @Value("${s3.signed-document-base-path}") String signedDocumentBasePath) {
        this.UNSIGNED_DOCUMENT_S3_BASE_PATH = unsignedDocumentBasePath;
        this.SIGNED_DOCUMENT_S3_BASE_PATH = signedDocumentBasePath;

        wormBuckets = Map.of();
        buckets = Map.of();
    }
}
