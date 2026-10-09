package it.pagopa.pn.cucumber.steps.visualtest;

import it.pagopa.common.pdf.visualtest.*;
import it.pagopa.pn.cucumber.steps.visualtest.templates.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.nio.file.Files;
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
    @DisplayName("Validazione rapida offline estrazione e conformita campi dinamici su expected.pdf")
    void testCampiDinamiciEstrazione(String folderName, PdfDocumentTemplate template) throws Exception {
        File pdfFile = new File(BASE_PATH + folderName + "/expected.pdf");
        assertTrue(pdfFile.exists(), "Il file expected.pdf deve esistere in " + pdfFile.getAbsolutePath());

        byte[] pdfBytes = Files.readAllBytes(pdfFile.toPath());
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0, "Il PDF non deve essere vuoto");

        // 1. Estrazione testo con coordinate
        ExtractedPdfText extracted = PdfTextExtractor.extract(pdfBytes);
        assertNotNull(extracted, "ExtractedPdfText non deve essere null per " + folderName);
        assertFalse(extracted.allFragments().isEmpty(), "I frammenti di testo non devono essere vuoti per " + folderName);

        // 2. Analisi e binding dei campi dinamici
        DynamicFieldAnalyzer.AnalysisResult result = DynamicFieldAnalyzer.analyze(extracted, template);

        System.out.println("==================================================");
        System.out.println("TEMPLATE: " + template.key() + " (" + folderName + ")");
        System.out.println("Valid: " + result.isValid());
        if (!result.isValid()) {
            System.err.println("Issues riscontrate:");
            result.issues().forEach(issue -> System.err.println("  • " + issue));
        } else {
            System.out.println("Tutti i campi dinamici configurati sono stati estratti e validati correttamente!");
        }
        System.out.println("==================================================");

        if (!result.isValid()) {
            StringBuilder sb = new StringBuilder("Problemi riscontrati nel template '")
                    .append(template.key())
                    .append("':\n");
            result.issues().forEach(issue -> sb.append("  • ").append(issue).append("\n"));
            fail(sb.toString());
        }

        assertTrue(result.isValid(), "Tutti i campi dinamici devono essere validi per " + template.key());
    }
}
