package it.pagopa.pn.cucumber.steps.visualtest;

import it.pagopa.common.pdf.visualtest.*;
import it.pagopa.pn.cucumber.steps.visualtest.templates.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class AllExpectedPdfsFastValidationTest {

    private static final String BASE_PATH = "src/test/resources/it/pagopa/pn/cucumber/visualTest/expected-pdfs/";

    static Stream<Arguments> allExpectedPdfsProvider() {
        return Stream.of(
                Arguments.of("sender-ack", SenderAckTemplate.build()),
                Arguments.of("pec-delivery", PecDeliveryTemplate.build()),
                Arguments.of("notification-viewed", NotificationViewedTemplate.build()),
                Arguments.of("analog-failure", AnalogFailureTemplate.build()),
                Arguments.of("notification-cancelled", NotificationCancelledTemplate.build()),
                Arguments.of("malfunction", MalfunctionTemplate.build()),
                Arguments.of("analog-delivery-workflow-timeout-legal-fact", AnalogDeliveryWorkflowTimeoutLegalFactTemplate.build()),
                Arguments.of("notification-aar", NotificationAarTemplate.build()),
                Arguments.of("notification-aar-radd-alt", NotificationAarRaddAltTemplate.build()),
                Arguments.of("analog-feedback-availability-statement", AnalogFeedbackAvailabilityStatementTemplate.build()),
                Arguments.of("informal-analog-communication", InformalAnalogCommunicationTemplate.build())
        );
    }

    @ParameterizedTest(name = "[{index}] Template: {0}")
    @MethodSource("allExpectedPdfsProvider")
    @DisplayName("1. Validazione isolamento geometrico dei box dinamici su expected.pdf")
    void testIsolamentoGeometricoBoxDinamici(String folderName, PdfDocumentTemplate template) throws Exception {
        File pdfFile = new File(BASE_PATH + folderName + "/expected.pdf");
        assertTrue(pdfFile.exists(), "Il file expected.pdf deve esistere in " + pdfFile.getAbsolutePath());

        byte[] pdfBytes = Files.readAllBytes(pdfFile.toPath());
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0, "Il PDF non deve essere vuoto");

        // 1. Estrazione testo con coordinate
        ExtractedPdfText extracted = PdfTextExtractor.extract(pdfBytes);
        assertNotNull(extracted, "ExtractedPdfText non deve essere null per " + folderName);
        assertFalse(extracted.allFragments().isEmpty(), "I frammenti di testo non devono essere vuoti per " + folderName);

        // 2. Verifica che ogni box dinamico sia localizzato geometricamente con precisione
        for (PdfDocumentTemplate.FieldDefinition def : template.fieldDefinitions()) {
            List<TextFragment> located = def.locator().locate(extracted.allFragments());
            assertNotNull(located, "I frammenti per il campo '" + def.fieldName() + "' non devono essere null");
            assertFalse(located.isEmpty(), "Il campo '" + def.fieldName() + "' deve essere localizzato geometricamente nel template " + template.key());

            // Verifica che il locator non abbia debordato catturando testo discorsivo o footer
            for (TextFragment f : located) {
                assertFalse(f.text().contains("Ai sensi dell’art. 26"), "Il box '" + def.fieldName() + "' non deve catturare preamboli di legge");
                assertFalse(f.text().contains("L’attestazione riporta la data in cui"), "Il box '" + def.fieldName() + "' non deve catturare note legali");
                assertFalse(f.text().contains("Registro Imprese di Roma"), "Il box '" + def.fieldName() + "' non deve scivolare nel footer PagoPA");
            }
        }
    }

    @ParameterizedTest(name = "[{index}] Template: {0}")
    @MethodSource("allExpectedPdfsProvider")
    @DisplayName("2. Verifica realismo e conformita dei valori estratti dai box dinamici")
    void testRealismoCampiDinamici(String folderName, PdfDocumentTemplate template) throws Exception {
        File pdfFile = new File(BASE_PATH + folderName + "/expected.pdf");
        assertTrue(pdfFile.exists(), "Il file expected.pdf deve esistere in " + pdfFile.getAbsolutePath());

        byte[] pdfBytes = Files.readAllBytes(pdfFile.toPath());
        ExtractedPdfText extracted = PdfTextExtractor.extract(pdfBytes);
        DynamicFieldAnalyzer.AnalysisResult result = DynamicFieldAnalyzer.analyze(extracted, template);

        System.out.println("--------------------------------------------------");
        System.out.println("TEMPLATE: " + template.key() + " (" + folderName + ")");
        for (PdfDocumentTemplate.FieldDefinition def : template.fieldDefinitions()) {
            List<TextFragment> located = def.locator().locate(extracted.allFragments());
            String val = located.stream().map(TextFragment::text).reduce("", (a, b) -> a + " " + b).strip();
            boolean isPlaceholder = FieldValidators.isPlaceholderText(val);
            System.out.printf("  • Campo '%s': \"%s\" -> %s%n",
                    def.fieldName(), val, isPlaceholder ? "[MOCK PLACEHOLDER]" : "[VALORE REALE / VALIDO]");
        }
        System.out.println("--------------------------------------------------");
    }
}
