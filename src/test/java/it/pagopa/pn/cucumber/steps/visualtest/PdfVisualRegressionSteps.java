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

    /** Ultimo PDF scaricato (in byte) per poterlo riusare in step successivi. */
    private byte[] lastDownloadedPdf;

    /** Ultimo report prodotto (utile per step di asserzione separata). */
    private PdfComplianceReport lastReport;

    @Autowired
    public PdfVisualRegressionSteps(SharedSteps sharedSteps,
                                    DocumentTemplateRegistry registry,
                                    it.pagopa.pn.cucumber.steps.pa.LegalFactContentVerifySteps legalFactContentVerifySteps) {
        this.sharedSteps = sharedSteps;
        this.registry    = registry;
        this.legalFactContentVerifySteps = legalFactContentVerifySteps;
    }

    // -----------------------------------------------------------------------
    // Step: download PDF e confronto ibrido in un unico passo
    // -----------------------------------------------------------------------

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
    // Internals
    // -----------------------------------------------------------------------

    private void verificaConformitaVisiva(byte[] actualPdf, String templateKey) {
        this.lastDownloadedPdf = actualPdf;

        PdfDocumentTemplate template = registry.getOrThrow(templateKey);
        byte[] expectedPdf          = loadGoldenMaster(templateKey);

        Path diffOutputPath = buildDiffOutputPath(templateKey);

        PdfComplianceReport report = PdfComplianceChecker.check(
                expectedPdf, actualPdf, template, diffOutputPath);
        this.lastReport = report;

        log.info("PdfVisualRegressionSteps: report per template '{}': {}", templateKey, report);
        report.assertCompliant();
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
