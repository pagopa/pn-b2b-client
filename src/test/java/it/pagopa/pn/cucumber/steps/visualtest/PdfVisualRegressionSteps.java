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
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactsIdV20;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV28;
import it.pagopa.pn.client.b2b.pa.provider.DestinatarioRegistry;
import it.pagopa.pn.client.b2b.pa.service.IPnDowntimeLogsClient;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.PnDowntimeEntry;
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
 * (repo, otherwise rendered by the Template Engine from the same fixture that built the notification).
 */
@Slf4j
public class PdfVisualRegressionSteps {

    private static final Path ACTUAL_ROOT = Paths.get("target", "vrt", "actual");
    private static final Path DIFF_ROOT = Paths.get("target", "vrt", "diff");

    private final SharedSteps sharedSteps;
    private final DestinatarioRegistry destinatarioRegistry;
    private final IPnDowntimeLogsClient downtimeLogsClient;
    private final TimelineDetailsReader timelineDetails;
    private final GoldenMasterProvider goldenMasters;
    private final int downtimeMonthsBack;

    private VisualDocument document;
    private byte[] actualPdf;
    private Map<String, String> hiddenDetails;

    @Autowired
    public PdfVisualRegressionSteps(SharedSteps sharedSteps,
                                    DestinatarioRegistry destinatarioRegistry,
                                    IPnDowntimeLogsClient downtimeLogsClient,
                                    ITemplateEngineClient templateEngineClient,
                                    TimelineDetailsReader timelineDetails,
                                    @Value("${pn.vrt.downtime-months-back:6}") int downtimeMonthsBack) {
        this.sharedSteps = sharedSteps;
        this.destinatarioRegistry = destinatarioRegistry;
        this.downtimeLogsClient = downtimeLogsClient;
        this.timelineDetails = timelineDetails;
        this.goldenMasters = new GoldenMasterProvider(new TemplateEngineGoldenRenderer(templateEngineClient));
        this.downtimeMonthsBack = downtimeMonthsBack;
    }

    @Given("viene generata la notifica di riferimento del documento {string}")
    public void vieneGenerataLaNotificaDiRiferimento(String documentKey) {
        VisualFixture fixture = VisualFixture.load(VisualDocument.of(documentKey));
        sharedSteps.prepareNotificationRequest(fixture.notificationData());
        sharedSteps.addDestinatarioWithParams(destinatarioRegistry.destinatario(fixture.recipientName()),
                fixture.recipientData());
    }

    /** The timeline element that carries the document must already be there (scenario waits for it). */
    @When("si recupera il documento {string} della notifica")
    public void siRecuperaIlDocumentoDellaNotifica(String documentKey) {
        document = VisualDocument.of(documentKey);
        String key = switch (document.source().kind()) {
            case TIMELINE_LEGAL_FACT -> legalFactKey(document.source());
            case HIDDEN_LEGAL_FACT -> hiddenLegalFactKey(document.source());
            case AAR -> aarKey(document.source());
            case NOT_RETRIEVABLE -> throw new IllegalStateException(documentKey + " non recuperabile da SEND: "
                    + document.source().keyPrefix());
            case DOWNTIME -> throw new IllegalArgumentException(documentKey + " si recupera dai disservizi, non dalla notifica");
        };
        String iun = sharedSteps.getNotificationIun();
        LegalFactDownloadMetadataResponse metadata = Poller.pollUntil(
                () -> downloadMetadataOrNull(iun, key),
                response -> response != null && response.getUrl() != null,
                PollingProfile.SLOW.toConfig(),
                last -> "Documento " + documentKey + " (" + key + ") non ancora disponibile per " + iun);
        storeActual(B2bUtils.downloadFile(metadata.getUrl()), iun);
    }

    @When("si recupera il documento {string} dell'ultimo disservizio risolto")
    public void siRecuperaIlDocumentoDellUltimoDisservizio(String documentKey) {
        document = VisualDocument.of(documentKey);
        OffsetDateTime notBefore = GoldenMasterProvider.repoGoldenCreation(document).orElse(OffsetDateTime.MIN);
        PnDowntimeEntry downtime = latestResolvedDowntimeWithLegalFact(notBefore);
        String url = downtimeLogsClient.getLegalFact(downtime.getLegalFactId()).getUrl();
        Assertions.assertNotNull(url, "URL del documento del disservizio " + downtime.getLegalFactId() + " assente");
        storeActual(B2bUtils.downloadFile(url), downtime.getLegalFactId());
    }

    @Then("il documento è conforme al golden master")
    public void ilDocumentoEConformeAlGoldenMaster() {
        byte[] expected = goldenMasters.resolve(currentDocument());
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
        if (source.kind() != VisualDocument.Source.Kind.TIMELINE_LEGAL_FACT && source.kind() != VisualDocument.Source.Kind.AAR
                && source.kind() != VisualDocument.Source.Kind.HIDDEN_LEGAL_FACT) {
            return;
        }
        String taxId = destinatarioRegistry.destinatario(VisualFixture.load(currentDocument()).recipientName()).getTaxId();
        OffsetDateTime eventTime = source.kind() == VisualDocument.Source.Kind.TIMELINE_LEGAL_FACT
                ? timeline().stream().filter(e -> source.timelineCategory().equals(e.getCategory()))
                .map(TimelineElementV28::getTimestamp).filter(Objects::nonNull).findFirst().orElse(null)
                : source.kind() == VisualDocument.Source.Kind.HIDDEN_LEGAL_FACT && hiddenDetails.get("timeoutDate") != null
                ? OffsetDateTime.parse(hiddenDetails.get("timeoutDate"))
                : null;
        List<String> problems = DynamicValueChecks.check(PdfTextExtractor.extract(currentActual()).allFragments(),
                sharedSteps.getNotificationIun(), taxId, currentDocument().template().fieldDefinitions().stream()
                        .anyMatch(d -> d.fieldName().startsWith("Data")) ? eventTime : null);
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
        String id = sharedSteps.getNotificationIun() != null ? sharedSteps.getNotificationIun() : "run-" + System.currentTimeMillis();
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
