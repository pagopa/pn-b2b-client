package it.pagopa.pn.cucumber.steps.visualtest;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.common.pdf.visualtest.DocumentTemplateRegistry;
import it.pagopa.common.pdf.visualtest.PdfComplianceChecker;
import it.pagopa.common.pdf.visualtest.PdfComplianceReport;
import it.pagopa.common.pdf.visualtest.PdfDocumentTemplate;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Step Cucumber per il Visual Regression Testing dei documenti PDF.
 *
 * <p>Integra il motore {@link PdfComplianceChecker} di {@code common} con il
 * contesto Cucumber del modulo {@code pn-b2bclient}.</p>
 *
 * <p>Il golden master di ogni template è posizionato in:
 * {@code src/test/resources/it/pagopa/pn/cucumber/visualtest/expected-pdfs/<KEY>/expected.pdf}</p>
 */
@Slf4j
public class PdfVisualRegressionSteps {

    private final SharedSteps sharedSteps;
    private final DocumentTemplateRegistry registry;
    private final it.pagopa.pn.cucumber.steps.pa.LegalFactContentVerifySteps legalFactContentVerifySteps;
    private final it.pagopa.pn.cucumber.steps.templateEngine.TemplateEngineSteps templateEngineSteps;

    /** Ultimo PDF scaricato (in byte) per poterlo riusare in step successivi. */
    private byte[] lastDownloadedPdf;

    /** Ultimo report prodotto (utile per step di asserzione separata). */
    private PdfComplianceReport lastReport;

    @Autowired
    public PdfVisualRegressionSteps(SharedSteps sharedSteps,
                                    DocumentTemplateRegistry registry,
                                    it.pagopa.pn.cucumber.steps.pa.LegalFactContentVerifySteps legalFactContentVerifySteps,
                                    @org.springframework.context.annotation.Lazy it.pagopa.pn.cucumber.steps.templateEngine.TemplateEngineSteps templateEngineSteps) {
        this.sharedSteps = sharedSteps;
        this.registry    = registry;
        this.legalFactContentVerifySteps = legalFactContentVerifySteps;
        this.templateEngineSteps = templateEngineSteps;
    }

    // -----------------------------------------------------------------------
    // Step: download PDF e confronto ibrido in un unico passo
    // -----------------------------------------------------------------------

    /**
     * Confronta il PDF generato nell'ultimo step di Template Engine con il golden master.
     */
    @Then("si verifica la conformità visiva del PDF generato dal template engine con il template {string}")
    public void verificaConformitaVisivaTemplateEngine(String templateKey) {
        Assertions.assertNotNull(templateEngineSteps, "TemplateEngineSteps non disponibile");
        byte[] actualPdf = templateEngineSteps.getLatestPdfBytes();
        Assertions.assertNotNull(actualPdf, "Nessun PDF generato dal Template Engine disponibile per la verifica");
        verificaConformitaVisiva(actualPdf, templateKey);
    }

    /**
     * Scarica il PDF dall'URL fornito e lo confronta con il golden master
     * del template indicato dalla chiave.
     *
     * <p>Step Gherkin:</p>
     * <pre>
     * Then si verifica la conformità visiva del PDF scaricato da "{url}" con il template "{templateKey}"
     * </pre>
     */
    @Then("si verifica la conformità visiva del PDF scaricato da {string} con il template {string}")
    public void verificaConformitaVisivaDa(String downloadUrl, String templateKey) {
        byte[] actualPdf = B2bUtils.downloadFile(downloadUrl);
        verificaConformitaVisiva(actualPdf, templateKey);
    }

    /**
     * Scarica il PDF dal URL memorizzato nel contesto Shared Steps
     * (ad es. l'URL del legal fact precedentemente scaricato) e lo confronta
     * con il golden master del template.
     *
     * <p>Step Gherkin:</p>
     * <pre>
     * Then si verifica la conformità visiva del PDF con il template "{templateKey}"
     * </pre>
     */
    @Then("si verifica la conformità visiva del PDF con il template {string}")
    public void verificaConformitaVisivaUltimoUrl(String templateKey) {
        String url = resolveLastLegalFactUrl();
        byte[] actualPdf = B2bUtils.downloadFile(url);
        verificaConformitaVisiva(actualPdf, templateKey);
    }

    /**
     * Verifica solo i campi dinamici (senza visual diff) del PDF scaricato.
     * Utile come step di smoke senza necessità di golden master.
     *
     * <p>Step Gherkin:</p>
     * <pre>
     * Then si verificano i campi dinamici del PDF con il template "{templateKey}"
     * </pre>
     */
    @Then("si verificano i campi dinamici del PDF con il template {string}")
    public void verificaCampiDinamiciSoloUrl(String templateKey) {
        String url = resolveLastLegalFactUrl();
        byte[] actualPdf = B2bUtils.downloadFile(url);
        this.lastDownloadedPdf = actualPdf;
        saveDownloadedPdfArtifacts(templateKey, actualPdf);

        PdfDocumentTemplate template = registry.getOrThrow(templateKey);

        it.pagopa.common.pdf.visualtest.ExtractedPdfText extracted =
                it.pagopa.common.pdf.visualtest.PdfTextExtractor.extract(actualPdf);
        it.pagopa.common.pdf.visualtest.DynamicFieldAnalyzer.AnalysisResult analysis =
                it.pagopa.common.pdf.visualtest.DynamicFieldAnalyzer.analyze(extracted, template);

        if (!analysis.isValid()) {
            StringJoiner sb = new StringJoiner("\n");
            sb.add("Errori nei campi dinamici del PDF (template: " + templateKey + "):");
            analysis.issues().forEach(issue -> sb.add("  • " + issue));
            Assertions.fail(sb.toString());
        }
        log.info("PdfVisualRegressionSteps: campi dinamici OK per template '{}'", templateKey);
    }

    // -----------------------------------------------------------------------
    // Step: assert sul last report
    // -----------------------------------------------------------------------

    @And("il report PDF di conformità non deve avere errori")
    public void ilReportPdfNonDeveAvereErrori() {
        Assertions.assertNotNull(lastReport,
                "Nessun report PDF disponibile: eseguire prima uno step di verifica conformità");
        lastReport.assertCompliant();
    }

    // -----------------------------------------------------------------------
    // Step: verifica campi per destinatario (DataTable campo/valore)
    // -----------------------------------------------------------------------

    /**
     * Verifica, per un determinato destinatario, il valore di uno o più campi del PDF.
     *
     * <p>Ogni campo è individuato tramite {@link it.pagopa.common.pdf.visualtest.FieldLocators#labelProximityOccurrence}:
     * si cerca l'etichetta indicata nella colonna {@code campo} e si prende la sua N-esima
     * occorrenza nel documento, dove N è l'indice del destinatario (1-based). Questo copre
     * i documenti in cui un blocco di campi (nome, codice fiscale, domicilio digitale, ...)
     * si ripete una volta per ciascun destinatario.</p>
     *
     * <p>Step Gherkin:</p>
     * <pre>
     * Then per il destinatario 1 del PDF si verificano i seguenti campi
     *   | campo                            | valore           |
     *   | Codice Fiscale                   | BRGLRZ80D58H501Q |
     *   | Domicilio digitale               | prova@pec.it     |
     * </pre>
     */
    @Then("per il destinatario {int} del PDF si verificano i seguenti campi")
    public void perIlDestinatarioDelPdfSiVerificanoISeguentiCampi(int recipientIndex, List<Map<String, String>> campiAttesi) {
        Assertions.assertTrue(recipientIndex >= 1, "L'indice del destinatario deve essere >= 1 (1-based)");

        List<it.pagopa.common.pdf.visualtest.TextFragment> allFragments =
                it.pagopa.common.pdf.visualtest.PdfTextExtractor.extract(lastDownloadedPdfOrThrow()).allFragments();

        for (Map<String, String> riga : campiAttesi) {
            String campo = riga.get("campo");
            String valoreAtteso = riga.get("valore");
            Assertions.assertNotNull(campo, "Colonna 'campo' mancante nella tabella");
            Assertions.assertNotNull(valoreAtteso, "Colonna 'valore' mancante nella tabella");

            List<it.pagopa.common.pdf.visualtest.TextFragment> located =
                    it.pagopa.common.pdf.visualtest.FieldLocators
                            .labelProximityOccurrence(campo, recipientIndex)
                            .locate(allFragments);

            Assertions.assertFalse(located.isEmpty(),
                    "Campo '" + campo + "' non trovato per il destinatario " + recipientIndex);

            String found = located.stream()
                    .map(it.pagopa.common.pdf.visualtest.TextFragment::text)
                    .reduce((a, b) -> a + " " + b)
                    .orElse("");

            Assertions.assertTrue(found.contains(valoreAtteso),
                    "Campo '" + campo + "' per il destinatario " + recipientIndex
                            + ": atteso un testo contenente \"" + valoreAtteso + "\" ma trovato \"" + found + "\"");
        }
        log.info("PdfVisualRegressionSteps: {} campi verificati per il destinatario {}", campiAttesi.size(), recipientIndex);
    }

    // -----------------------------------------------------------------------
    // Internals
    // -----------------------------------------------------------------------

    /**
     * Restituisce l'ultimo PDF scaricato/generato, oppure fallisce con un messaggio chiaro
     * se nessuno step precedente lo ha ancora reso disponibile.
     */
    private byte[] lastDownloadedPdfOrThrow() {
        Assertions.assertNotNull(lastDownloadedPdf,
                "Nessun PDF disponibile: eseguire prima uno step che scarichi o generi il PDF "
                        + "(es. uno step di verifica conformità o di download del legal fact)");
        return lastDownloadedPdf;
    }

    private void verificaConformitaVisiva(byte[] actualPdf, String templateKey) {
        this.lastDownloadedPdf = actualPdf;
        saveDownloadedPdfArtifacts(templateKey, actualPdf);

        PdfDocumentTemplate template = registry.getOrThrow(templateKey);
        byte[] expectedPdf          = loadGoldenMaster(templateKey);

        Path diffOutputPath = buildDiffOutputPath(templateKey);

        PdfComplianceReport report = PdfComplianceChecker.check(
                expectedPdf, actualPdf, template, diffOutputPath);
        this.lastReport = report;

        log.info("PdfVisualRegressionSteps: report per template '{}': {}", templateKey, report);
        report.assertCompliant();
    }

    private void saveDownloadedPdfArtifacts(String templateKey, byte[] pdfBytes) {
        String folder = templateKey.toLowerCase().replace('_', '-');
        String iun = (sharedSteps != null && sharedSteps.getNotificationIun() != null)
                ? sharedSteps.getNotificationIun()
                : "NO_IUN";

        // 1. Salva in target/downloaded-pdfs/
        try {
            Path targetDir = Paths.get("target", "downloaded-pdfs", folder);
            Files.createDirectories(targetDir);
            Path actualFile = targetDir.resolve("actual.pdf");
            Path iunFile = targetDir.resolve(iun + "_" + folder + ".pdf");
            Files.write(actualFile, pdfBytes);
            Files.write(iunFile, pdfBytes);
            log.info("PdfVisualRegressionSteps: salvato PDF scaricato in {}", actualFile.toAbsolutePath());
        } catch (IOException e) {
            log.warn("PdfVisualRegressionSteps: impossibile salvare in target/downloaded-pdfs: {}", e.getMessage());
        }

        // 2. Salva in expected-pdfs (aggiornamento automatico golden master)
        try {
            Path expectedPath = Paths.get("src/test/resources/it/pagopa/pn/cucumber/visualtest/expected-pdfs", folder, "expected.pdf");
            Files.createDirectories(expectedPath.getParent());
            Files.write(expectedPath, pdfBytes);
            log.info("PdfVisualRegressionSteps: aggiornato Golden Master in {}", expectedPath.toAbsolutePath());
        } catch (IOException e) {
            log.warn("PdfVisualRegressionSteps: impossibile salvare in expected-pdfs: {}", e.getMessage());
        }
    }

    /**
     * Carica il golden master dal classpath.
     * Percorso atteso: {@code it/pagopa/pn/cucumber/visualtest/expected-pdfs/<key>/expected.pdf}
     */
    private byte[] loadGoldenMaster(String templateKey) {
        String resourcePath = "it/pagopa/pn/cucumber/visualtest/expected-pdfs/"
                + templateKey.toLowerCase().replace('_', '-')
                + "/expected.pdf";
        try (InputStream is = new ClassPathResource(resourcePath).getInputStream()) {
            return is.readAllBytes();
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Golden master PDF non trovato per il template '" + templateKey + "'. "
                    + "Percorso atteso nel classpath: " + resourcePath
                    + ". Posizionare il file in: "
                    + "src/test/resources/" + resourcePath,
                    e);
        }
    }

    /**
     * Costruisce il path di output per il diff, sotto {@code target/pdf-visual-diff/}.
     */
    private static Path buildDiffOutputPath(String templateKey) {
        Path diffDir = Paths.get("target", "pdf-visual-diff");
        try {
            Files.createDirectories(diffDir);
        } catch (IOException e) {
            log.warn("PdfVisualRegressionSteps: impossibile creare la directory diff: {}", e.getMessage());
            return null;
        }
        return diffDir.resolve(templateKey.toLowerCase() + "-diff.pdf");
    }

    /**
     * Recupera l'URL del legal fact corrente dal contesto Cucumber condiviso.
     */
    private String resolveLastLegalFactUrl() {
        if (legalFactContentVerifySteps != null && legalFactContentVerifySteps.getLegalFactUrl() != null) {
            return legalFactContentVerifySteps.getLegalFactUrl();
        }
        try {
            var legalFactStepsField = sharedSteps.getClass()
                    .getDeclaredField("legalFactUrl");
            legalFactStepsField.setAccessible(true);
            return (String) legalFactStepsField.get(sharedSteps);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Impossibile recuperare l'URL del legal fact dal contesto Cucumber. "
                    + "Assicurarsi che il campo 'legalFactUrl' sia stato impostato dagli step precedenti.",
                    e);
        }
    }

    // Inner helper per messaggio d'errore
    private static class StringJoiner {
        private final StringBuilder sb = new StringBuilder();
        private final String delimiter;

        StringJoiner(String delimiter) {
            this.delimiter = delimiter;
        }

        void add(String value) {
            if (!sb.isEmpty()) sb.append(delimiter);
            sb.append(value);
        }

        @Override
        public String toString() {
            return sb.toString();
        }
    }
}
