package it.pagopa.pn.cucumber.steps.visualtest;

import it.pagopa.common.pdf.visualtest.DynamicFieldAnalyzer;
import it.pagopa.common.pdf.visualtest.ExtractedPdfText;
import it.pagopa.common.pdf.visualtest.PdfComplianceChecker;
import it.pagopa.common.pdf.visualtest.PdfComplianceReport;
import it.pagopa.common.pdf.visualtest.PdfDocumentTemplate;
import it.pagopa.common.pdf.visualtest.PdfTextExtractor;
import it.pagopa.pn.cucumber.steps.visualtest.templates.NotificationCancelledTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationCancelledLocalValidationTest {

    private static final String EXPECTED_PDF_PATH =
            "src/test/resources/it/pagopa/pn/cucumber/visualtest/expected-pdfs/notification-cancelled/expected.pdf";
    private static final String ACTUAL_PDF_PATH =
            "target/downloaded-pdfs/notification-cancelled/actual.pdf";

    @Test
    @DisplayName("Verifica campi dinamici su expected.pdf")
    void testCampiDinamiciExpected() throws Exception {
        File pdfFile = new File(EXPECTED_PDF_PATH);
        assertTrue(pdfFile.exists(), "Il file expected.pdf deve esistere: " + pdfFile.getAbsolutePath());

        byte[] pdfBytes = Files.readAllBytes(pdfFile.toPath());
        PdfDocumentTemplate template = NotificationCancelledTemplate.build();

        ExtractedPdfText extracted = PdfTextExtractor.extract(pdfBytes);
        assertNotNull(extracted);
        assertFalse(extracted.allFragments().isEmpty(), "I frammenti di testo non devono essere vuoti");

        DynamicFieldAnalyzer.AnalysisResult result = DynamicFieldAnalyzer.analyze(extracted, template);
        if (!result.isValid()) {
            StringBuilder sb = new StringBuilder("Campi non validi:\n");
            result.issues().forEach(issue -> sb.append("  • ").append(issue).append("\n"));
            fail(sb.toString());
        }
        assertTrue(result.isValid(), "Tutti i campi dinamici devono essere validi");
    }

    @Test
    @DisplayName("Sanity: confronto expected contro se stesso (deve passare)")
    void testExpectedControSeStesso() throws Exception {
        File pdfFile = new File(EXPECTED_PDF_PATH);
        assertTrue(pdfFile.exists());

        byte[] pdfBytes = Files.readAllBytes(pdfFile.toPath());
        PdfDocumentTemplate template = NotificationCancelledTemplate.build();

        PdfComplianceReport report = PdfComplianceChecker.check(pdfBytes, pdfBytes, template, null);
        assertNotNull(report);
        report.assertCompliant();
        assertTrue(report.isCompliant());
    }

    @Test
    @DisplayName("Sanity: confronto actual contro se stesso (deve passare se actual esiste)")
    void testActualControSeStesso() throws Exception {
        File pdfFile = new File(ACTUAL_PDF_PATH);
        if (!pdfFile.exists()) {
            return; // skip if actual.pdf has not been downloaded yet
        }

        byte[] pdfBytes = Files.readAllBytes(pdfFile.toPath());
        PdfDocumentTemplate template = NotificationCancelledTemplate.build();

        PdfComplianceReport report = PdfComplianceChecker.check(pdfBytes, pdfBytes, template, null);
        assertNotNull(report);
        report.assertCompliant();
        assertTrue(report.isCompliant());
    }

    @Test
    @DisplayName("Confronto expected contro actual")
    void testExpectedControActual() throws Exception {
        File expFile = new File(EXPECTED_PDF_PATH);
        File actFile = new File(ACTUAL_PDF_PATH);
        if (!actFile.exists()) {
            return;
        }

        byte[] expBytes = Files.readAllBytes(expFile.toPath());
        byte[] actBytes = Files.readAllBytes(actFile.toPath());
        PdfDocumentTemplate template = NotificationCancelledTemplate.build();

        java.nio.file.Path diffPath = java.nio.file.Paths.get("target", "pdf-visual-diff", "notification_cancelled_sanity_diff.pdf");
        PdfComplianceReport report = PdfComplianceChecker.check(expBytes, actBytes, template, diffPath);
        assertNotNull(report);
        report.assertCompliant();
        assertTrue(report.isCompliant());
    }
}
