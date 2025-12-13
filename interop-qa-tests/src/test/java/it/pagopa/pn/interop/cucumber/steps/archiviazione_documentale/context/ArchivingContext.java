package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.context;


import static it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.FileType.*;

import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.FileType;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model.ArchivedFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model.S3BucketInfo;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model.S3BucketInfoBuilder;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils.ArchivingUtils;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils.TokenResolver;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArchivingContext {

    private final TokenResolver tokenResolver;
    private final Map<FileType, String> wormBuckets;
    private final Map<FileType, String> buckets;
    private String centerTimestamp;
    @Getter @Setter private ArchivedFile currentFile;

    //TODO: inizializzare i path per ogni file
    public ArchivingContext(String unsignedDocumentBasePath, String signedDocumentBasePath, SharedStepsContext sharedStepsContext) {
        tokenResolver = new TokenResolver(sharedStepsContext);

        wormBuckets = new HashMap<>();
        wormBuckets.put(AGREEMENT_ACTIVATED_SIGNED, "interop-signed-application-documents-qa-es1/interop-qa-documents-signer/:year/:onlyMonth/:onlyDay/");
        wormBuckets.put(AGREEMENT_ACTIVATED_EVENT_SIGNED, "interop-signed-application-documents-qa-es1/interop-qa-documents-signer/:year/:onlyMonth/:onlyDay/");
        wormBuckets.put(CONSUMER_DELEGATION_APPROVED_SIGNED, "interop-signed-application-documents-qa-es1/interop-qa-documents-signer/:year/:onlyMonth/:onlyDay/");
        wormBuckets.put(CONSUMER_DELEGATION_APPROVED_EVENT_SIGNED, "interop-signed-domain-events-qa-es1/interop-qa-events-signer/:year/:onlyMonth/:onlyDay/");
        wormBuckets.put(PURPOSE_ACTIVATED_SIGNED, "interop-signed-domain-events-qa-es1/interop-qa-events-signer/:year/:onlyMonth/:onlyDay/");


        buckets = new HashMap<>();
        buckets.put(AGREEMENT_ACTIVATED, String.format("interop-application-documents-qa-es1/qa/generated-documents-unsigned/agreement/%s/",sharedStepsContext.getAgreementId()));
        buckets.put(AGREEMENT_ACTIVATED_EVENT, "interop-domain-events-qa-es1/year=:year/month=:onlyMonth/day=:onlyDay/");
        buckets.put(CONSUMER_DELEGATION_APPROVED, "interop-application-documents-qa-es1/qa/generated-documents-unsigned/delegation/:consumerDelegationId");
        buckets.put(CONSUMER_DELEGATION_APPROVED_EVENT, "interop-domain-events-qa-es1/year=:year/month=:onlyMonth/day=:onlyDay/");
        buckets.put(PURPOSE_ACTIVATED, "interop-application-documents-qa-es1/qa/generated-documents-unsigned/risk-analysis/:riskAnalysisId");
        buckets.put(PURPOSE_VERSION_ACTIVATED, "interop-application-documents-qa-es1/qa/generated-documents-unsigned/risk-analysis/:riskAnalysisId");
        buckets.put(NEW_PURPOSE_VERSION_ACTIVATED, "interop-application-documents-qa-es1/qa/generated-documents-unsigned/risk-analysis/:riskAnalysisId");
    }

    public S3BucketInfo getBucket(boolean isWorm, FileType fileType) {
        String fullPath = isWorm ? wormBuckets.get(fileType) : buckets.get(fileType);
        List<String> splitResolvedPath = tokenResolver.resolve(List.of(fullPath.split("/")));
        String resolvedFullPath = String.join("/", splitResolvedPath);

        return S3BucketInfoBuilder.builder().fullPath(resolvedFullPath).build();
    }

    public String getCenterTimestamp() {
        if (centerTimestamp == null) centerTimestamp = ArchivingUtils.now();
        return centerTimestamp;
    }
}
