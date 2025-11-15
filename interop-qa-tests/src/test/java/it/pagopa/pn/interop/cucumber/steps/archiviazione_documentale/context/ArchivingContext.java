package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.context;


import static it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.FileType.*;

import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.FileType;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model.ArchivedFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model.S3BucketInfo;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model.S3BucketInfoBuilder;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils.TokenResolver;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

public class ArchivingContext {
    private final TokenResolver tokenResolver;
    private final Map<FileType, String> wormBuckets;
    private final Map<FileType, String> buckets;
    @Getter @Setter private ArchivedFile currentFile;

    //TODO: inizializzare i path per ogni file
    public ArchivingContext(String unsignedDocumentBasePath, String signedDocumentBasePath, SharedStepsContext sharedStepsContext) {
        tokenResolver = new TokenResolver(sharedStepsContext);
        wormBuckets = Map.of(AGREEMENT_ACTIVATED, "/path/relativo/completo");
        buckets = Map.of(AGREEMENT_ACTIVATED, "/path/relativo/completo");
    }

    public S3BucketInfo getBucket(boolean isWorm, FileType fileType) {
        String fullPath = isWorm ? wormBuckets.get(fileType) : buckets.get(fileType);
        List<String> splitResolvedPath = tokenResolver.resolve(List.of(fullPath.split("/")));
        String resolvedFullPath = String.join("/", splitResolvedPath);

        return S3BucketInfoBuilder.builder().fullPath(resolvedFullPath).build();
    }
}
