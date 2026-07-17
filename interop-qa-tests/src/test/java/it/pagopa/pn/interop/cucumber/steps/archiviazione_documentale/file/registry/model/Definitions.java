package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.registry.model;

import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementState;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeVersionState;
import it.pagopa.interop.event.enums.InteropEvent;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.InteropFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.FilenameFormat;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.FileToken;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.source.ListFileTokenSource;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.source.MapFileTokenSource;

import java.util.List;

import static it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.model.BucketRole.STANDARD;
import static it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.model.BucketRole.WORM;
import static it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.InteropFile.*;

public class Definitions {
    public static List<FileInfoDefinition> definitions(
            String documentBucketBase,
            String documentWormBucketBase,
            String eventBucketBase,
            String eventWormBucketBase,
            String jwtDetailsBucketBase,
            String jwtDetailsWormBucketBase
    ) {

        return List.of(

                new FileInfoDefinition(
                        AUDIT_JWT_EVENTS_LOG,
                        MapFileTokenSource.of("jwtId", ":jwtId"),
                        MapFileTokenSource.of(),
                        List.of(new LocationDefinition(STANDARD, jwtDetailsBucketBase, FilenameFormat.NDJSON_LOG),
                                new LocationDefinition(WORM, jwtDetailsWormBucketBase, FilenameFormat.NDJSON_SIGNED_LOG)
                        )
                ),

                new FileInfoDefinition(
                        RISK_ANALYSIS_DOC,
                        MapFileTokenSource.of("E-Service", ":eServiceName", "Numero chiamate API/giorno", ":riskAnalysisDailyCalls"),
                        ListFileTokenSource.of("Analisi del rischio"),
                        List.of(new LocationDefinition(STANDARD, documentBucketBase + "risk-analysis/:riskAnalysisId", FilenameFormat.PDF_DOC),
                                new LocationDefinition(WORM, documentWormBucketBase, FilenameFormat.PDF_SIGNED_DOC)
                        )
                ),

                new FileInfoDefinition(
                        AGREEMENT_CONTRACT_DOC,
                        ListFileTokenSource.of("richiesta di fruizione", ":agreementId"),
                        null,
                        List.of(new LocationDefinition(STANDARD, documentBucketBase + "agreement/:agreementId", FilenameFormat.AGREEMENT_CONTRACT_PDF),
                                new LocationDefinition(WORM, documentWormBucketBase, FilenameFormat.PDF_SIGNED_DOC)
                        )
                ),

                new FileInfoDefinition(
                        CONSUMER_DELEGATION_REQUEST_DOC,
                        ListFileTokenSource.of("richiesta di delega alla fruizione", ":agreementId"),
                        null,
                        List.of(new LocationDefinition(STANDARD, documentBucketBase + "delegation/:consumerDelegationId", FilenameFormat.PDF_DOC),
                                new LocationDefinition(WORM, documentWormBucketBase, FilenameFormat.PDF_SIGNED_DOC)
                        )
                ),

                new FileInfoDefinition(
                        CONSUMER_DELEGATION_REVOKED_DOC,
                        ListFileTokenSource.of("Richiesta di revoca della delega", ":agreementId"),
                        null,
                        List.of(new LocationDefinition(STANDARD, documentBucketBase + "delegation/:consumerDelegationId", FilenameFormat.PDF_DOC),
                                new LocationDefinition(WORM, documentWormBucketBase, FilenameFormat.PDF_SIGNED_DOC)
                        )
                ),

                new FileInfoDefinition(
                        PRODUCER_DELEGATION_REVOKED_DOC,
                        ListFileTokenSource.of("Richiesta di revoca della delega all’erogazione", ":agreementId"),
                        null,
                        List.of(new LocationDefinition(STANDARD, documentBucketBase + "delegation/:producerDelegationId", FilenameFormat.PDF_DOC),
                                new LocationDefinition(WORM, documentWormBucketBase, FilenameFormat.PDF_SIGNED_DOC)
                        )
                ),

                new FileInfoDefinition(
                        PRODUCER_DELEGATION_REQUEST_DOC,
                        ListFileTokenSource.of("richiesta di delega all'erogazione", ":agreementId"),
                        null,
                        List.of(new LocationDefinition(STANDARD, documentBucketBase + "delegation/:producerDelegationId", FilenameFormat.PDF_DOC),
                                new LocationDefinition(WORM, documentWormBucketBase, FilenameFormat.PDF_SIGNED_DOC)
                        )
                ),


                buildStandardEventLogFileDefinition(
                        AGREEMENT_ACTIVATE_EVENTS_LOG,
                        InteropEvent.AGREEMENT_ACTIVATED,
                        ":agreementId",
                        AgreementState.ACTIVE.getValue(),
                        eventBucketBase,
                        eventWormBucketBase
                ),

                buildStandardEventLogFileDefinition(
                        AGREEMENT_SUSPENDED_BY_CONSUMER_EVENTS_LOG,
                        InteropEvent.AGREEMENT_SUSPENDED_BY_CONSUMER,
                        ":agreementId",
                        AgreementState.SUSPENDED.getValue(),
                        eventBucketBase,
                        eventWormBucketBase
                ),

                buildStandardEventLogFileDefinition(
                        AGREEMENT_ARCHIVED_BY_CONSUMER_EVENTS_LOG,
                        InteropEvent.AGREEMENT_ARCHIVED_BY_CONSUMER,
                        ":agreementId",
                        AgreementState.ARCHIVED.getValue(),
                        eventBucketBase,
                        eventWormBucketBase
                ),

                buildStandardEventLogFileDefinition(
                        PURPOSE_ACTIVATE_EVENTS_LOG,
                        InteropEvent.PURPOSE_ACTIVATED,
                        ":purposeId",
                        PurposeVersionState.ACTIVE.getValue(),
                        eventBucketBase,
                        eventWormBucketBase
                ),

                buildStandardEventLogFileDefinition(
                        NEW_PURPOSE_VERSION_ACTIVATE_EVENTS_LOG,
                        InteropEvent.NEW_PURPOSE_VERSION_ACTIVATED,
                        ":purposeId",
                        PurposeVersionState.ACTIVE.getValue(),
                        eventBucketBase,
                        eventWormBucketBase
                )
        );
    }

    private static FileInfoDefinition buildStandardEventLogFileDefinition(
            InteropFile type,
            InteropEvent event,
            String id,
            String state,
            String eventBucketBase,
            String eventWormBucketBase
    ) {

        return new FileInfoDefinition(
                type,
                MapFileTokenSource.of(
                        "event_name", event.getValue(),
                        "id", id,
                        "state", toCamelCase(state)
                ),
                MapFileTokenSource.of("timestamp", FileToken.hasValidTimestamp()),
                List.of(new LocationDefinition(STANDARD, eventBucketBase, FilenameFormat.EVENT_LOG),
                        new LocationDefinition(WORM, eventWormBucketBase, FilenameFormat.EVENT_SIGNED_LOG))
        );
    }

    private static String toCamelCase(String input) {
        if (input == null || input.isBlank()) {
            return input;
        }

        String[] parts = input.trim().split("[^a-zA-Z0-9]+"); // split su _, -, spazi, simboli

        StringBuilder result = new StringBuilder();

        for (String p : parts) {
            if (p == null || p.isEmpty()) continue;

            String lower = p.toLowerCase();
            result.append(Character.toUpperCase(lower.charAt(0)))
                    .append(lower.substring(1));
        }

        return result.toString();
    }

}
