package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.registry;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.InteropFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.FileInfo;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.FileLocation;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.FilenameFormat;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.source.ListFileTokenSource;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.FileValidator;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.model.S3BucketInfoBuilder;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils.TokenResolver;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Set;

import static it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.BucketRole.*;
import static it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.InteropFile.*;

@RequiredArgsConstructor
public class FileInfoRegistry {

    private final TokenResolver tokenResolver;
    private final String documentBucketBase;
    private final String documentWormBucketBase;
    private final String eventBucketBase;
    private final String eventWormBucketBase;

    private final Map<InteropFile, FileInfo> registry = Map.of(
            RISK_ANALYSIS_DOC, new FileInfo(
                    RISK_ANALYSIS_DOC,
                    new FileValidator(tokenResolver, ListFileTokenSource.of("Analisi del rischio"), null),
                    Set.of(new FileLocation(STANDARD, S3BucketInfoBuilder.builder().fullPath(documentBucketBase+"risk-analysis/:riskAnalysisId").build(), FilenameFormat.PDF_DOC),
                           new FileLocation(WORM, S3BucketInfoBuilder.builder().fullPath(documentWormBucketBase).build(), FilenameFormat.PDF_SIGNED_DOC))
            ),

            AGREEMENT_CONTRACT_DOC, new FileInfo(
                    AGREEMENT_CONTRACT_DOC,
                    new FileValidator(tokenResolver, ListFileTokenSource.of("richiesta di fruizione", ":agreementId"), null),
                    Set.of(new FileLocation(STANDARD, S3BucketInfoBuilder.builder().fullPath(documentBucketBase+"agreement/:agreementId").build(), FilenameFormat.PDF_DOC),
                            new FileLocation(WORM, S3BucketInfoBuilder.builder().fullPath(documentWormBucketBase).build(), FilenameFormat.PDF_SIGNED_DOC))
            ),

            CONSUMER_DELEGATION_REQUEST_DOC, new FileInfo(
                    CONSUMER_DELEGATION_REQUEST_DOC,
                    new FileValidator(tokenResolver, ListFileTokenSource.of("richiesta di delega alla fruizione", ":agreementId"), null),
                    Set.of(new FileLocation(STANDARD, S3BucketInfoBuilder.builder().fullPath(documentBucketBase+"delegation/:consumerDelegationId").build(), FilenameFormat.PDF_DOC),
                            new FileLocation(WORM, S3BucketInfoBuilder.builder().fullPath(documentWormBucketBase).build(), FilenameFormat.PDF_SIGNED_DOC))
            ),


            CONSUMER_DELEGATION_REVOKED_DOC, null
    );

    public FileInfo getFileInfo(InteropFile file) {
        FileInfo info = registry.get(file);
        if (info == null) throw new RuntimeException("File info non trovata");
        return info;
    }
}
