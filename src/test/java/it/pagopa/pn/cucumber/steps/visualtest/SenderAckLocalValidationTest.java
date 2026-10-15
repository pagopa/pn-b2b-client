package it.pagopa.pn.cucumber.steps.visualtest;

import it.pagopa.common.pdf.visualtest.DynamicFieldAnalyzer;
import it.pagopa.common.pdf.visualtest.ExtractedPdfText;
import it.pagopa.common.pdf.visualtest.PdfComplianceChecker;
import it.pagopa.common.pdf.visualtest.PdfComplianceReport;
import it.pagopa.common.pdf.visualtest.PdfDocumentTemplate;
import it.pagopa.common.pdf.visualtest.PdfTextExtractor;
import it.pagopa.pn.cucumber.steps.visualtest.templates.SenderAckTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class SenderAckLocalValidationTest {

    private static final String PDF_PATH =
            "src/test/resources/it/pagopa/pn/cucumber/visualtest/expected-pdfs/sender-ack/expected.pdf";

    @Test
    @DisplayName("Verifica rapida offline dei campi dinamici del SENDER_ACK sul PDF reale scaricato")
    void testCampiDinamiciPdfReale() throws Exception {
        File pdfFile = new File(PDF_PATH);
        assertTrue(pdfFile.exists(), "Il file expected.pdf deve esistere: " + pdfFile.getAbsolutePath());

        byte[] pdfBytes = Files.readAllBytes(pdfFile.toPath());
        PdfDocumentTemplate template = SenderAckTemplate.build();

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
    @DisplayName("Verifica rapida conformita visiva completa (diff 0% contro se stesso)")
    void testVisualDiffControSeStesso() throws Exception {
        File pdfFile = new File(PDF_PATH);
        assertTrue(pdfFile.exists());

        byte[] pdfBytes = Files.readAllBytes(pdfFile.toPath());
        PdfDocumentTemplate template = SenderAckTemplate.build();

        PdfComplianceReport report = PdfComplianceChecker.check(pdfBytes, pdfBytes, template, null);

        assertNotNull(report);
        report.assertCompliant();
        assertTrue(report.isCompliant(), "Il confronto del documento contro se stesso deve essere al 100% compliant");
    }

        @Test
    @DisplayName("Caso Negativo: Verifica che un PDF corrotto/alterato fallisca e generi il file di diff")
    void testVisualDiffFailsOnCorruptedPdf() throws Exception {
        File pdfFile = new File(PDF_PATH);
        assertTrue(pdfFile.exists());
        byte[] goldenBytes = Files.readAllBytes(pdfFile.toPath());

        // Corruzione: disegniamo un rettangolo nero anomalo sul layout con PDFBox
        byte[] corruptedBytes;
        try (org.apache.pdfbox.pdmodel.PDDocument doc = org.apache.pdfbox.Loader.loadPDF(goldenBytes)) {
            org.apache.pdfbox.pdmodel.PDPage page = doc.getPage(0);
            try (org.apache.pdfbox.pdmodel.PDPageContentStream cs =
                    new org.apache.pdfbox.pdmodel.PDPageContentStream(doc, page,
                            org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode.APPEND, true, true)) {
                cs.setNonStrokingColor(0, 0, 0);
                cs.addRect(150, 450, 180, 40); // corruzione grafica evidente
                cs.fill();
            }
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            doc.save(baos);
            corruptedBytes = baos.toByteArray();
        }

        PdfDocumentTemplate template = SenderAckTemplate.build();
        java.nio.file.Path diffDir = java.nio.file.Paths.get("target", "pdf-visual-diff");
        java.nio.file.Files.createDirectories(diffDir);

        // Salviamo anche il PDF corrotto effettivo per ispezione visiva
        java.nio.file.Path corruptedPath = diffDir.resolve("sender-ack-corrupted.pdf");
        java.nio.file.Files.write(corruptedPath, corruptedBytes);

        java.nio.file.Path diffPath = diffDir.resolve("sender-ack-corrupted-diff.pdf");

        PdfComplianceReport report = PdfComplianceChecker.check(goldenBytes, corruptedBytes, template, diffPath);

        // Il test di robustezza DEVE rilevare la non conformità
        assertNotNull(report);
        assertFalse(report.isCompliant(), "Un PDF corrotto DEVE fallire il controllo visivo!");
        assertThrows(AssertionError.class, report::assertCompliant);
        assertTrue(java.nio.file.Files.exists(diffPath), "Il file di diff visivo deve essere stato generato!");
    }

    @Test
    @DisplayName("Verifica layout con iniezione caratteri HTML di fuzzing (Simulazione Igor)")
    void testVisualDiffConCaratteriHtmlIniettati() throws Exception {
        File pdfFile = new File(PDF_PATH);
        assertTrue(pdfFile.exists());
        byte[] goldenBytes = Files.readAllBytes(pdfFile.toPath());

        // Payload tipico del fuzzing di Igor con caratteri HTML sensibili
        String fuzzedPayload = "COMUNE DI MILANO <script>alert('xss')</script> & \"Societa\" 'S.p.A.'";

        byte[] fuzzedBytes;
        try (org.apache.pdfbox.pdmodel.PDDocument doc = org.apache.pdfbox.Loader.loadPDF(goldenBytes)) {
            org.apache.pdfbox.pdmodel.PDPage page = doc.getPage(0);
            try (org.apache.pdfbox.pdmodel.PDPageContentStream cs =
                    new org.apache.pdfbox.pdmodel.PDPageContentStream(doc, page,
                            org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode.APPEND, true, true)) {
                // Sovrascriviamo l'area con testo contenente i caratteri HTML (escapati o raw per il PDF)
                cs.setNonStrokingColor(1.0f, 1.0f, 1.0f);
                cs.addRect(50, 480, 400, 25);
                cs.fill();

                cs.beginText();
                cs.setNonStrokingColor(0.0f, 0.0f, 0.0f);
                cs.setFont(new org.apache.pdfbox.pdmodel.font.PDType1Font(
                        org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName.HELVETICA_BOLD), 9);
                cs.newLineAtOffset(50, 490);
                cs.showText("Mittente fuzzed: COMUNE DI MILANO & Societa S.p.A.");
                cs.endText();
            }
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            doc.save(baos);
            fuzzedBytes = baos.toByteArray();
        }

        PdfDocumentTemplate template = SenderAckTemplate.build();
        java.nio.file.Path diffDir = java.nio.file.Paths.get("target", "pdf-visual-diff");
        java.nio.file.Files.createDirectories(diffDir);

        // Salviamo il PDF fuzzed su disco per visualizzazione umana
        java.nio.file.Path fuzzedPdfPath = diffDir.resolve("sender-ack-html-fuzzed.pdf");
        java.nio.file.Files.write(fuzzedPdfPath, fuzzedBytes);

        // Salviamo il diff visivo generato dal confronto
        java.nio.file.Path diffPath = diffDir.resolve("sender-ack-html-fuzzed-diff.pdf");

        PdfComplianceReport report = PdfComplianceChecker.check(goldenBytes, fuzzedBytes, template, diffPath);

        assertNotNull(report);
        // Poiché il testo inserito altera l'area non compresa nella sola maschera dinamica, il diff deve rilevarlo
        assertFalse(report.isCompliant(), "La modifica al layout con testo fuzzed deve produrre un diff rilevato");
        assertTrue(java.nio.file.Files.exists(diffPath), "Il file di diff visivo sender-ack-html-fuzzed-diff.pdf deve essere generato");
    }
}
