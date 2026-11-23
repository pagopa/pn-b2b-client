package it.pagopa.pn.cucumber.steps.visualtest;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.common.pdf.visualtest.DynamicFieldAnalyzer;
import it.pagopa.common.pdf.visualtest.FieldLocators;
import it.pagopa.common.pdf.visualtest.PdfComplianceChecker;
import it.pagopa.common.pdf.visualtest.PdfComplianceReport;
import it.pagopa.common.pdf.visualtest.PdfTextExtractor;
import it.pagopa.common.pdf.visualtest.TextFragment;
import it.pagopa.common.polling.PollingProfile;
import it.pagopa.common.polling.Poller;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV29;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactsIdV20;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV28;
import it.pagopa.pn.client.b2b.pa.provider.DestinatarioRegistry;
import it.pagopa.pn.client.b2b.pa.service.IPnDowntimeLogsClient;
import it.pagopa.pn.client.b2b.pa.service.IPnSafeStoragePrivateClient;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaB2bInternalInformalClientImpl;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.InformalTimelineElementV1;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.model.FileDownloadResponse;
import it.pagopa.pn.cucumber.steps.SendSharedContext;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.PnDowntimeEntry;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.PnFunctionality;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.PnFunctionalityStatus;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.PnStatusUpdateEvent;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.PnDowntimeHistoryResponse;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import it.pagopa.pn.cucumber.steps.tracciaturaIndagine.reader.TimelineDetailsReader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Visual regression of the PDFs produced by SEND: the document of the scenario is compared with its golden master
 * committed in the repo; a scenario whose document has no golden master is skipped (see {@link GoldenMasterProvider}).
 */
@Slf4j
public class PdfVisualRegressionSteps {

    private static final Path ACTUAL_ROOT = Paths.get("target", "vrt", "actual");
    private static final Path DIFF_ROOT = Paths.get("target", "vrt", "diff");

    private final SharedSteps sharedSteps;
    private final DestinatarioRegistry destinatarioRegistry;
    private final IPnDowntimeLogsClient downtimeLogsClient;
    private final TimelineDetailsReader timelineDetails;
    private final int downtimeMonthsBack;
    private final String downtimeOperatorUid;
    private final PnPaB2bInternalInformalClientImpl informalClient;
    private final IPnSafeStoragePrivateClient safeStorageClient;
    private final SendSharedContext sendSharedContext;

    private VisualDocument document;
    private byte[] actualPdf;
    private Map<String, String> hiddenDetails;
    private PnFunctionality createdDowntimeFunctionality;
    private OffsetDateTime createdDowntimeStart;

    @Autowired
    public PdfVisualRegressionSteps(SharedSteps sharedSteps,
                                    DestinatarioRegistry destinatarioRegistry,
                                    IPnDowntimeLogsClient downtimeLogsClient,
                                    TimelineDetailsReader timelineDetails,
                                    PnPaB2bInternalInformalClientImpl informalClient,
                                    IPnSafeStoragePrivateClient safeStorageClient,
                                    SendSharedContext sendSharedContext,
                                    @Value("${pn.vrt.downtime-months-back:6}") int downtimeMonthsBack,
                                    @Value("${pn.vrt.downtime-operator-uid:pn-b2b-client-vrt}") String downtimeOperatorUid) {
        this.sharedSteps = sharedSteps;
        this.destinatarioRegistry = destinatarioRegistry;
        this.downtimeLogsClient = downtimeLogsClient;
        this.timelineDetails = timelineDetails;
        this.downtimeMonthsBack = downtimeMonthsBack;
        this.downtimeOperatorUid = downtimeOperatorUid;
        this.informalClient = informalClient;
        this.safeStorageClient = safeStorageClient;
        this.sendSharedContext = sendSharedContext;
    }

    @Given("viene generata la notifica di riferimento del documento {string}")
    public void vieneGenerataLaNotificaDiRiferimento(String documentKey) {
        VisualDocument visualDocument = VisualDocument.of(documentKey);
        GoldenMasterProvider.assumeGoldenMasterInRepo(visualDocument);
        VisualFixture fixture = VisualFixture.load(visualDocument);
        sharedSteps.prepareNotificationRequest(fixture.notificationData());
        sharedSteps.addDestinatarioWithParams(destinatarioRegistry.destinatario(fixture.recipientName()),
                fixture.recipientData());
    }

    /** The timeline element that carries the document must already be there (scenario waits for it). */
    @When("si recupera il documento {string} della notifica")
    public void siRecuperaIlDocumentoDellaNotifica(String documentKey) {
        document = VisualDocument.of(documentKey);
        GoldenMasterProvider.assumeGoldenMasterInRepo(document);
        String key = switch (document.source().kind()) {
            case TIMELINE_LEGAL_FACT -> legalFactKey(document.source());
            case HIDDEN_LEGAL_FACT -> hiddenLegalFactKey(document.source());
            case AAR -> aarKey(document.source());
            case NOT_RETRIEVABLE -> throw new IllegalStateException(documentKey + " non recuperabile da SEND: "
                    + document.source().keyPrefix());
            case DOWNTIME -> throw new IllegalArgumentException(documentKey + " si recupera dai disservizi, non dalla notifica");
            case INFORMAL_COVERPAGE -> throw new IllegalArgumentException(documentKey + " si recupera dalla notifica bonaria");
        };
        String iun = sharedSteps.getNotificationIun();
        LegalFactDownloadMetadataResponse metadata = Poller.pollUntil(
                () -> downloadMetadataOrNull(iun, key),
                response -> response != null && response.getUrl() != null,
                PollingProfile.SLOW.toConfig(),
                last -> "Documento " + documentKey + " (" + key + ") non ancora disponibile per " + iun);
        storeActual(B2bUtils.downloadFile(metadata.getUrl()), iun);
    }

    /**
     * The cover page key is on the informal timeline; the file is downloaded from the private SafeStorage API
     * ({@code pn.safeStorage.base-url}: in local runs a tunnel on 8888 towards the confidential ALB).
     */
    @When("si recupera il documento {string} della notifica bonaria")
    public void siRecuperaIlDocumentoDellaNotificaBonaria(String documentKey) {
        document = VisualDocument.of(documentKey);
        GoldenMasterProvider.assumeGoldenMasterInRepo(document);
        Assertions.assertEquals(VisualDocument.Source.Kind.INFORMAL_COVERPAGE, document.source().kind(),
                documentKey + " non si recupera dalla notifica bonaria");
        String iun = informalIun();
        String senderId = sendSharedContext.getInformalNotificationContext().getSenderId();
        String category = document.source().keyPrefix();
        String key = Poller.pollUntil(
                () -> informalClient.getSentInformalNotificationSender(senderId, iun, false).getTimeline().stream()
                        .filter(e -> e.getCategory() != null && category.equals(e.getCategory().getValue()))
                        .map(InformalTimelineElementV1::getDetails)
                        .filter(Objects::nonNull)
                        .map(d -> d.getFileKey())
                        .filter(Objects::nonNull)
                        .findFirst().orElse(null),
                Objects::nonNull,
                PollingProfile.SLOW.toConfig(),
                last -> "Elemento " + category + " con fileKey non ancora prodotto per la bonaria " + iun);
        String fileKey = safeStorageKey(key);
        FileDownloadResponse file = Poller.pollUntil(
                () -> safeStorageFileOrNull(fileKey),
                response -> response != null && response.getDownload() != null && response.getDownload().getUrl() != null,
                PollingProfile.SLOW.toConfig(),
                last -> "Copertina " + fileKey + " non ancora disponibile su SafeStorage per " + iun);
        storeActual(B2bUtils.downloadFile(file.getDownload().getUrl()), iun);
    }

    private FileDownloadResponse safeStorageFileOrNull(String fileKey) {
        try {
            return safeStorageClient.getFile(fileKey, false, false);
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        }
    }

    private String informalIun() {
        String iun = sendSharedContext.getInformalNotificationContext().getIun();
        Assertions.assertNotNull(iun, "Nessuna notifica bonaria inviata nello scenario");
        return iun;
    }

    /**
     * Prepared, not yet run: the only way to obtain a malfunction attestation newer than the golden master is to
     * open and close a downtime through the internal pn-downtime-logs API (KO then OK on one functionality). It
     * writes to the environment's downtime history and shows in the platform status, so the scenario that uses it
     * stays @ignore until the team agrees on running it.
     */
    @Given("viene aperto e chiuso un disservizio della funzionalità {string}")
    public void vieneApertoEChiusoUnDisservizio(String functionality) {
        GoldenMasterProvider.assumeGoldenMasterInRepo(VisualDocument.MALFUNCTION);
        createdDowntimeFunctionality = PnFunctionality.valueOf(functionality);
        createdDowntimeStart = OffsetDateTime.now().minusMinutes(2).withNano(0);
        downtimeLogsClient.addStatusChangeEvent(downtimeOperatorUid, List.of(downtimeEvent(PnFunctionalityStatus.KO,
                createdDowntimeStart)));
        downtimeLogsClient.addStatusChangeEvent(downtimeOperatorUid, List.of(downtimeEvent(PnFunctionalityStatus.OK,
                createdDowntimeStart.plusMinutes(1))));
    }

    /** The attestation is generated asynchronously when the downtime closes. */
    @When("si recupera il documento {string} del disservizio creato")
    public void siRecuperaIlDocumentoDelDisservizioCreato(String documentKey) {
        document = VisualDocument.of(documentKey);
        GoldenMasterProvider.assumeGoldenMasterInRepo(document);
        Assertions.assertNotNull(createdDowntimeStart, "Nessun disservizio creato nello scenario");
        PnDowntimeEntry downtime = Poller.pollUntil(
                this::createdDowntimeWithLegalFact,
                Objects::nonNull,
                PollingProfile.SLOW.toConfig(),
                last -> "Attestazione del disservizio " + createdDowntimeFunctionality + " del " + createdDowntimeStart
                        + " non ancora disponibile");
        String url = downtimeLogsClient.getLegalFact(downtime.getLegalFactId()).getUrl();
        Assertions.assertNotNull(url, "URL del documento del disservizio " + downtime.getLegalFactId() + " assente");
        storeActual(B2bUtils.downloadFile(url), downtime.getLegalFactId());
    }

    private PnStatusUpdateEvent downtimeEvent(PnFunctionalityStatus status, OffsetDateTime timestamp) {
        return new PnStatusUpdateEvent()
                .status(status)
                .timestamp(timestamp)
                .functionality(List.of(createdDowntimeFunctionality))
                .sourceType(PnStatusUpdateEvent.SourceTypeEnum.OPERATOR)
                .source("pn-b2b-client visual regression");
    }

    private PnDowntimeEntry createdDowntimeWithLegalFact() {
        PnDowntimeHistoryResponse resolved = downtimeLogsClient.getResolved(createdDowntimeStart.getYear(),
                createdDowntimeStart.getMonthValue());
        if (resolved == null || resolved.getResult() == null) {
            return null;
        }
        return resolved.getResult().stream()
                .filter(e -> createdDowntimeFunctionality.equals(e.getFunctionality()))
                .filter(e -> e.getStartDate() != null
                        && Math.abs(e.getStartDate().toEpochSecond() - createdDowntimeStart.toEpochSecond()) <= 2)
                .filter(e -> e.getLegalFactId() != null && Boolean.TRUE.equals(e.getFileAvailable()))
                .findFirst().orElse(null);
    }

    @When("si recupera il documento {string} dell'ultimo disservizio risolto")
    public void siRecuperaIlDocumentoDellUltimoDisservizio(String documentKey) {
        document = VisualDocument.of(documentKey);
        GoldenMasterProvider.assumeGoldenMasterInRepo(document);
        OffsetDateTime notBefore = GoldenMasterProvider.repoGoldenCreation(document).orElse(OffsetDateTime.MIN);
        PnDowntimeEntry downtime = latestResolvedDowntimeWithLegalFact(notBefore);
        String url = downtimeLogsClient.getLegalFact(downtime.getLegalFactId()).getUrl();
        Assertions.assertNotNull(url, "URL del documento del disservizio " + downtime.getLegalFactId() + " assente");
        storeActual(B2bUtils.downloadFile(url), downtime.getLegalFactId());
    }

    @Then("il documento è conforme al golden master")
    public void ilDocumentoEConformeAlGoldenMaster() {
        byte[] expected = GoldenMasterProvider.fromRepoOrFail(currentDocument());
        PdfComplianceReport report = PdfComplianceChecker.check(expected, currentActual(), currentDocument().template(),
                diffPath());
        log.info("VRT {}: {}", currentDocument().key(), report);
        report.layoutNotes().forEach(note -> log.info("VRT {}: {}", currentDocument().key(), note));
        report.assertCompliant();
        assertValuesBelongToTheNotification();
    }

    /** The IUN, the tax id and the time printed in the document are those of this scenario's notification. */
    private void assertValuesBelongToTheNotification() {
        VisualDocument.Source source = currentDocument().source();
        if (source.kind() == VisualDocument.Source.Kind.INFORMAL_COVERPAGE) {
            String taxId = sendSharedContext.getInformalNotificationContext().getRecipient().getDestinatario().getTaxId();
            List<String> problems = DynamicValueChecks.check(PdfTextExtractor.extract(currentActual()).allFragments(),
                    informalIun(), taxId, null, null);
            Assertions.assertTrue(problems.isEmpty(), () -> "Valori del documento " + currentDocument().key()
                    + " non della notifica bonaria: " + problems);
            return;
        }
        if (source.kind() != VisualDocument.Source.Kind.TIMELINE_LEGAL_FACT && source.kind() != VisualDocument.Source.Kind.AAR
                && source.kind() != VisualDocument.Source.Kind.HIDDEN_LEGAL_FACT) {
            return;
        }
        String taxId = destinatarioRegistry.destinatario(VisualFixture.load(currentDocument()).recipientName()).getTaxId();
        // The printed time lies between the sending of the notification and the timeline element of the event; the
        // decorrenza termini of the hidden legal fact is the timeoutDate itself.
        OffsetDateTime notBefore = null;
        OffsetDateTime notAfter = null;
        if (source.kind() == VisualDocument.Source.Kind.TIMELINE_LEGAL_FACT) {
            FullSentNotificationV29 notification = sharedSteps.getSentNotificationLastVersion();
            notBefore = notification.getSentAt();
            notAfter = notification.getTimeline().stream().filter(e -> source.timelineCategory().equals(e.getCategory()))
                    .map(TimelineElementV28::getTimestamp).filter(Objects::nonNull).findFirst().orElse(null);
        } else if (source.kind() == VisualDocument.Source.Kind.HIDDEN_LEGAL_FACT && hiddenDetails.get("timeoutDate") != null) {
            notBefore = OffsetDateTime.parse(hiddenDetails.get("timeoutDate"));
            notAfter = notBefore;
        }
        boolean printsTime = currentDocument().template().fieldDefinitions().stream()
                .anyMatch(d -> d.fieldName().startsWith("Data"));
        List<String> problems = DynamicValueChecks.check(PdfTextExtractor.extract(currentActual()).allFragments(),
                sharedSteps.getNotificationIun(), taxId, printsTime ? notBefore : null, printsTime ? notAfter : null);
        Assertions.assertTrue(problems.isEmpty(), () -> "Valori del documento " + currentDocument().key()
                + " non della notifica: " + problems);
    }

    @Then("i campi dinamici del documento sono validi")
    public void iCampiDinamiciDelDocumentoSonoValidi() {
        DynamicFieldAnalyzer.AnalysisResult analysis =
                DynamicFieldAnalyzer.analyze(PdfTextExtractor.extract(currentActual()), currentDocument().template());
        Assertions.assertTrue(analysis.isValid(), () -> "Campi dinamici non validi in " + currentDocument().key() + ":\n"
                + analysis.issues().stream().map(Object::toString).collect(Collectors.joining("\n")));
    }

    /** Fields of a block repeated once per recipient: the N-th occurrence of each label belongs to recipient N. */
    @Then("per il destinatario {int} del documento si verificano i seguenti campi")
    public void perIlDestinatarioDelDocumentoSiVerificanoICampi(int recipientIndex, List<Map<String, String>> fields) {
        Assertions.assertTrue(recipientIndex >= 1, "L'indice del destinatario parte da 1");
        List<TextFragment> fragments = PdfTextExtractor.extract(currentActual()).allFragments();
        for (Map<String, String> row : fields) {
            String label = row.get("campo");
            String expected = row.get("valore");
            String found = FieldLocators.labelProximityOccurrence(label, recipientIndex).locate(fragments).stream()
                    .map(TextFragment::text)
                    .collect(Collectors.joining(" "));
            Assertions.assertTrue(found.contains(expected), "Campo '" + label + "' del destinatario " + recipientIndex
                    + ": atteso \"" + expected + "\", trovato \"" + found + "\"");
        }
    }

    /** A legal fact that is still being generated by SafeStorage answers 404 for a while. */
    private LegalFactDownloadMetadataResponse downloadMetadataOrNull(String iun, String key) {
        try {
            return sharedSteps.getB2bClient().getDownloadLegalFact(iun, key);
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        }
    }

    private String legalFactKey(VisualDocument.Source source) {
        return timeline().stream()
                .filter(e -> source.timelineCategory().equals(e.getCategory()))
                .map(TimelineElementV28::getLegalFactsIds)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .filter(id -> source.legalFactCategory().equals(id.getCategory()))
                .map(LegalFactsIdV20::getKey)
                .map(PdfVisualRegressionSteps::safeStorageKey)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Nessun legal fact " + source.legalFactCategory()
                        + " su " + source.timelineCategory() + " per " + sharedSteps.getNotificationIun()));
    }

    /** The element is not exposed by B2B: its legal fact id is read from pn-Timelines once the workflow has written it. */
    private String hiddenLegalFactKey(VisualDocument.Source source) {
        String iun = sharedSteps.getNotificationIun();
        List<Map<String, String>> details = Poller.pollUntil(
                () -> timelineDetails.readDetails(iun, source.keyPrefix(), 0, null),
                found -> found != null && found.stream().anyMatch(d -> d.get("legalFactId") != null),
                PollingProfile.SLOW.toConfig(),
                last -> "Elemento " + source.keyPrefix() + " con legal fact non ancora scritto per " + iun);
        hiddenDetails = details.stream().filter(d -> d.get("legalFactId") != null).findFirst().orElseThrow();
        return safeStorageKey(hiddenDetails.get("legalFactId"));
    }

    private String aarKey(VisualDocument.Source source) {
        return timeline().stream()
                .filter(e -> source.timelineCategory().equals(e.getCategory()))
                .map(e -> e.getDetails() == null ? null : e.getDetails().getGeneratedAarUrl())
                .filter(Objects::nonNull)
                .filter(url -> url.contains(source.keyPrefix()))
                .map(url -> url.substring(url.indexOf(source.keyPrefix())))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Nessun AAR generato per " + sharedSteps.getNotificationIun()));
    }

    private List<TimelineElementV28> timeline() {
        return sharedSteps.getSentNotificationLastVersion().getTimeline();
    }

    /** The attestation is produced once, when the downtime closes: older ones follow an older template. */
    private PnDowntimeEntry latestResolvedDowntimeWithLegalFact(OffsetDateTime notBefore) {
        LocalDate month = LocalDate.now();
        for (int i = 0; i <= downtimeMonthsBack; i++, month = month.minusMonths(1)) {
            PnDowntimeHistoryResponse resolved = downtimeLogsClient.getResolved(month.getYear(), month.getMonthValue());
            if (resolved != null && resolved.getResult() != null) {
                var latest = resolved.getResult().stream()
                        .filter(e -> e.getLegalFactId() != null && Boolean.TRUE.equals(e.getFileAvailable()))
                        .filter(e -> e.getFileAvailableTimestamp() != null && e.getFileAvailableTimestamp().isAfter(notBefore))
                        .max(Comparator.comparing(PnDowntimeEntry::getStartDate));
                if (latest.isPresent()) {
                    return latest.get();
                }
            }
        }
        throw new AssertionError("Nessun disservizio risolto negli ultimi " + downtimeMonthsBack
                + " mesi con attestazione prodotta dopo il golden master (" + notBefore + ")");
    }

    private static String safeStorageKey(String key) {
        return key.startsWith("safestorage://") ? key.substring("safestorage://".length()) : key;
    }

    private void storeActual(byte[] pdf, String executionId) {
        Assertions.assertTrue(pdf != null && pdf.length > 4 && pdf[0] == '%' && pdf[1] == 'P',
                "Il contenuto scaricato per " + currentDocument().key() + " non è un PDF");
        this.actualPdf = pdf;
        Path file = ACTUAL_ROOT.resolve(currentDocument().folder()).resolve(executionId + ".pdf");
        try {
            Files.createDirectories(file.getParent());
            Files.write(file, pdf);
        } catch (IOException e) {
            throw new UncheckedIOException("Impossibile salvare " + file, e);
        }
        log.info("VRT {}: documento salvato in {}", currentDocument().key(), file.toAbsolutePath());
    }

    private Path diffPath() {
        Path dir = DIFF_ROOT.resolve(currentDocument().folder());
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new UncheckedIOException("Impossibile creare " + dir, e);
        }
        String iun = currentDocument().source().kind() == VisualDocument.Source.Kind.INFORMAL_COVERPAGE
                ? sendSharedContext.getInformalNotificationContext().getIun() : sharedSteps.getNotificationIun();
        String id = iun != null ? iun : "run-" + System.currentTimeMillis();
        return dir.resolve(id + "-diff.pdf");
    }

    private VisualDocument currentDocument() {
        Assertions.assertNotNull(document, "Nessun documento recuperato: eseguire prima lo step di recupero");
        return document;
    }

    private byte[] currentActual() {
        Assertions.assertNotNull(actualPdf, "Nessun documento recuperato: eseguire prima lo step di recupero");
        return actualPdf;
    }
}
