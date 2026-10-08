package it.pagopa.common.pdf.visualtest;

import it.pagopa.common.pdf.visualtest.support.TestPdfFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitari del {@link PdfComplianceChecker} su PDF generati da zero con {@link TestPdfFactory}.
 *
 * <p>Verificano che:</p>
 * <ul>
 *   <li>le parti statiche vengano confrontate pixel-per-pixel con il documento di riferimento;</li>
 *   <li>le parti dinamiche dichiarate nel template siano escluse dal confronto visivo
 *       ma validate dai rispettivi {@link FieldValidator};</li>
 *   <li>un numero di pagine diverso venga segnalato subito come errore strutturale.</li>
 * </ul>
 */
class PdfComplianceCheckerTest {

    private static final float MAX_OFFSET_X = 250f;
    private static final float MAX_OFFSET_Y = 3f;
    private static final String LEGAL_FACT_PDF =
            "assets/PN_LEGAL_FACTS-941b53bac8904dd597df965044d32d17.pdf";
    private static final String LEGAL_FACT_CORROTTO_PDF =
            "assets/PN_LEGAL_FACTS_CORROTTO.pdf";
    private static final String LEGAL_FACT_2_PDF =
            "assets/PN_LEGAL_FACTS_2.pdf";

    private static final Pattern IUN =
            Pattern.compile("\\b[A-Z]{4}-[A-Z]{4}-[A-Z]{4}-\\d{6}-[A-Z]-\\d\\b");
    private static final Pattern CODICE_FISCALE =
            Pattern.compile("\\b(?:[A-Z]{6}\\d{2}[A-Z]\\d{2}[A-Z]\\d{3}[A-Z]|\\d{11})\\b");
    private static final Pattern EMAIL =
            Pattern.compile("[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}");
    private static final Pattern DATA_IT =
            Pattern.compile("\\b(?:0?[1-9]|[12]\\d|3[01])/(?:0?[1-9]|1[0-2])/\\d{4}\\b");


    /**
     * Template del documento di test. Le etichette vengono cercate per singola parola
     * perché l'estrattore produce un frammento per parola.
     */
    private static PdfDocumentTemplate template() {
        return PdfDocumentTemplate.builder("TEST_DOC")
                .expectedPageCount(1)
                .field("codiceIdentificativo",
                        FieldLocators.labelProximity("identificativo:", MAX_OFFSET_X, MAX_OFFSET_Y),
                        FieldValidators.uuid(),
                        MaskStrategy.TIGHT_BOX)
                .field("dataEmissione",
                        FieldLocators.labelProximity("emissione:", MAX_OFFSET_X, MAX_OFFSET_Y),
                        FieldValidators.isoDate(),
                        MaskStrategy.TIGHT_BOX)
                .field("importoDovuto",
                        FieldLocators.labelProximity("dovuto:", MAX_OFFSET_X, MAX_OFFSET_Y),
                        FieldValidators.positiveInteger(),
                        MaskStrategy.TIGHT_BOX)
                .build();
    }

    /**
     * Template del documento di test. Le etichette vengono cercate per singola parola
     * perché l'estrattore produce un frammento per parola.
     */
    private static PdfDocumentTemplate legalFactTemplate() {
        return PdfDocumentTemplate.builder("LEGAL_FACT")
                .expectedPageCount(1)
                .field("IUN",
                        FieldLocators.regex(IUN),
                        FieldValidators.regex(IUN, "IUN (XXXX-XXXX-XXXX-YYYYMM-X-N)"),
                        MaskStrategy.TIGHT_BOX)
                .field("nome cognome",
                        FieldLocators.labelProximity("Ragione Sociale", 300f, 20f),
                        FieldValidators.present(),
                        MaskStrategy.FULL_LINE)
                .field("codice fiscale",
                        FieldLocators.regex(CODICE_FISCALE),
                        FieldValidators.regex(CODICE_FISCALE, "codice fiscale PF/PG"),
                        MaskStrategy.TIGHT_BOX)
                .field("domicilio digitale",
                        FieldLocators.regex(EMAIL),
                        FieldValidators.regex(EMAIL, "indirizzo PEC/email"),
                        MaskStrategy.TIGHT_BOX)
                .field("data",
                        FieldLocators.regex(DATA_IT),
                        FieldValidators.regex(DATA_IT, "data dd/MM/yyyy"),
                        MaskStrategy.TIGHT_BOX)
                .build();
    }

    // -----------------------------------------------------------------------
    // Documenti conformi
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("Documenti conformi")
    class Compliant {

        @Test
        @DisplayName("Due documenti identici risultano conformi")
        void identicalDocumentsAreCompliant() {
            byte[] reference = TestPdfFactory.reference();
            byte[] actual = TestPdfFactory.reference();

            PdfComplianceReport report = PdfComplianceChecker.check(reference, actual, template());

            assertTrue(report.isCompliant(), report::toString);
            assertTrue(report.fieldIssues().isEmpty());
            assertTrue(report.structuralErrors().isEmpty());
            assertNotNull(report.visualResult(), "Il confronto visivo deve essere eseguito");
            assertTrue(report.visualResult().equal());
            assertEquals(1, report.visualResult().pageCount());
            assertDoesNotThrow(report::assertCompliant);
        }

        @Test
        @DisplayName("Due PDF identici risultano conformi")
        void identicalPDFAreCompliant() {

            byte[] pdf = PageCount.loadResource(LEGAL_FACT_PDF);
            PdfComplianceReport report = PdfComplianceChecker.check(pdf, pdf, legalFactTemplate());
            assertTrue(report.isCompliant(), report::toString);

            assertTrue(report.isCompliant(), report::toString);
            assertTrue(report.fieldIssues().isEmpty());
            assertTrue(report.structuralErrors().isEmpty());
            assertNotNull(report.visualResult(), "Il confronto visivo deve essere eseguito");
            assertTrue(report.visualResult().equal());
            assertEquals(1, report.visualResult().pageCount());
            assertDoesNotThrow(report::assertCompliant);
        }

        @Test
        @DisplayName("Due PDF diversi risultano diversi")
        void differentPDFAreNotCompliant() {
            byte[] pdf = PageCount.loadResource(LEGAL_FACT_PDF);
            byte[] corruptedPdf = PageCount.loadResource(LEGAL_FACT_CORROTTO_PDF);

            PdfComplianceReport report = PdfComplianceChecker.check(pdf, corruptedPdf, PdfDocumentTemplate.builder("TEST_DOC")
                    .expectedPageCount(1).build());
            assertFalse(report.isCompliant(), report::toString);
        }

        @Test
        @DisplayName("Due istanze PDF dello stesso template risultano conformi")
        void sameTemplatePDFInstanceAreCompliant() {
            byte[] pdf = PageCount.loadResource(LEGAL_FACT_PDF);
            byte[] pdfSimile = PageCount.loadResource(LEGAL_FACT_2_PDF);

            PdfComplianceReport report = PdfComplianceChecker.check(pdf, pdfSimile, legalFactTemplate());
            assertTrue(report.isCompliant(), report::toString);
        }

        @Test
        @DisplayName("Valori dinamici diversi ma nel formato atteso non producono differenze visive")
        void differentValidDynamicValuesAreMaskedAndCompliant() {
            byte[] reference = TestPdfFactory.reference();
            byte[] actual = TestPdfFactory.spec()
                    .id("a1b2c3d4-e5f6-4789-abcd-ef0123456789")
                    .date("2025-12-01")
                    .amount("98765")
                    .build();

            PdfComplianceReport report = PdfComplianceChecker.check(reference, actual, template());

            assertTrue(report.fieldIssues().isEmpty(), () -> "Field issues: " + report.fieldIssues());
            assertTrue(report.visualResult().equal(),
                    () -> "Le aree dinamiche devono essere escluse dal diff: " + report.visualResult());
            assertTrue(report.isCompliant(), report::toString);
        }
    }

    // -----------------------------------------------------------------------
    // Validazione dei campi dinamici
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("Validazione formato dei campi dinamici")
    class DynamicFields {

        static Stream<Arguments> invalidDynamicValues() {
            return Stream.of(
                    Arguments.of("codiceIdentificativo",
                            TestPdfFactory.spec().id("NON-UN-UUID-VALIDO")),
                    Arguments.of("codiceIdentificativo",
                            // UUID v1: la versione non è 4
                            TestPdfFactory.spec().id("3f2b1c4d-5e6f-1a7b-8c9d-0e1f2a3b4c5d")),
                    Arguments.of("dataEmissione",
                            TestPdfFactory.spec().date("31/01/2024")),
                    Arguments.of("dataEmissione",
                            TestPdfFactory.spec().date("2024-13-45")),
                    Arguments.of("importoDovuto",
                            TestPdfFactory.spec().amount("abc")),
                    Arguments.of("importoDovuto",
                            TestPdfFactory.spec().amount("0"))
            );
        }

        @ParameterizedTest(name = "[{index}] campo {0} con formato non valido")
        @MethodSource("invalidDynamicValues")
        @DisplayName("Un valore dinamico in formato errato produce un FieldIssue sul campo corretto")
        void invalidDynamicValueIsReported(String expectedField, TestPdfFactory.Spec spec) {
            byte[] reference = TestPdfFactory.reference();
            byte[] actual = spec.build();

            PdfComplianceReport report = PdfComplianceChecker.check(reference, actual, template());

            assertFalse(report.isCompliant());
            assertEquals(1, report.fieldIssues().size(), () -> "Field issues: " + report.fieldIssues());
            FieldIssue issue = report.fieldIssues().get(0);
            assertEquals(expectedField, issue.fieldName());
            assertNotNull(issue.errorMessage());
            assertTrue(report.structuralErrors().isEmpty());

            AssertionError error = assertThrows(AssertionError.class, report::assertCompliant);
            assertTrue(error.getMessage().contains(expectedField));
        }

        @Test
        @DisplayName("Un campo dinamico assente viene segnalato come non trovato")
        void missingDynamicFieldIsReported() {
            byte[] reference = TestPdfFactory.reference();
            byte[] actual = TestPdfFactory.spec().omitIdField().build();

            PdfComplianceReport report = PdfComplianceChecker.check(reference, actual, template());

            assertFalse(report.isCompliant());
            assertTrue(report.fieldIssues().stream()
                            .anyMatch(i -> i.fieldName().equals("codiceIdentificativo")
                                    && i.locatedFragments().isEmpty()),
                    () -> "Field issues: " + report.fieldIssues());
        }
    }

    // -----------------------------------------------------------------------
    // Differenze visive sulle parti statiche
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("Rilevamento differenze visive sulle parti statiche")
    class VisualDifferences {

        static Stream<Arguments> staticDifferences() {
            return Stream.of(
                    Arguments.of("testo del titolo modificato",
                            TestPdfFactory.spec().title("ATTESTAZIONE DI PROVA")),
                    Arguments.of("testo del paragrafo modificato",
                            TestPdfFactory.spec().paragraph(
                                    "Il presente documento attesta la corretta generazione del contenuto statica.")),
                    Arguments.of("impaginazione traslata verticalmente",
                            TestPdfFactory.spec().layoutShiftY(-12f)),
                    Arguments.of("immagine con colore diverso",
                            TestPdfFactory.spec().logoColor(new Color(204, 0, 0))),
                    Arguments.of("tabella senza bordi",
                            TestPdfFactory.spec().drawTableBorders(false)),
                    Arguments.of("contenuto di una cella di tabella modificato",
                            TestPdfFactory.spec().tableRows(new String[][]{
                                    {"Voce", "Descrizione"},
                                    {"A", "Spese di notifica"},
                                    {"B", "Diritti di cancelleria"}
                            }))
            );
        }

        @ParameterizedTest(name = "[{index}] {0}")
        @MethodSource("staticDifferences")
        @DisplayName("Una differenza visiva reale sulla parte statica viene rilevata")
        void realVisualDifferenceIsDetected(String description, TestPdfFactory.Spec spec) {
            byte[] reference = TestPdfFactory.reference();
            byte[] actual = spec.build();

            PdfComplianceReport report = PdfComplianceChecker.check(reference, actual, template());

            assertTrue(report.fieldIssues().isEmpty(),
                    () -> "I campi dinamici sono validi, attesi zero field issues: " + report.fieldIssues());
            assertTrue(report.structuralErrors().isEmpty());
            assertNotNull(report.visualResult());
            assertFalse(report.visualResult().equal(), "Differenza non rilevata: " + description);
            assertFalse(report.isCompliant());
            assertThrows(AssertionError.class, report::assertCompliant);
        }

        @Test
        @DisplayName("In caso di differenza visiva viene salvato il file di diff")
        void diffFileIsWrittenWhenDifferent(@TempDir Path tmp) {
            Path diff = tmp.resolve("diff.pdf");
            byte[] reference = TestPdfFactory.reference();
            byte[] actual = TestPdfFactory.spec().title("TITOLO DIVERSO").build();

            PdfComplianceReport report = PdfComplianceChecker.check(reference, actual, template(), diff);

            assertFalse(report.isCompliant());
            assertEquals(diff, report.visualResult().diffImagePath());
            assertTrue(Files.exists(diff));
            AssertionError error = assertThrows(AssertionError.class, report::assertCompliant);
            assertTrue(error.getMessage().contains("DIFFERENZE VISIVE"));
        }

        @Test
        @DisplayName("Senza campi dinamici dichiarati, anche una variazione del valore dinamico è una differenza visiva")
        void dynamicValueChangeIsDetectedWhenNotDeclaredInTemplate() {
            PdfDocumentTemplate noDynamicFields = PdfDocumentTemplate.builder("NO_FIELDS")
                    .expectedPageCount(1)
                    .build();
            byte[] reference = TestPdfFactory.reference();
            byte[] actual = TestPdfFactory.spec().amount("98765").build();

            PdfComplianceReport report = PdfComplianceChecker.check(reference, actual, noDynamicFields);

            assertFalse(report.visualResult().equal());
            assertFalse(report.isCompliant());
        }
    }

    // -----------------------------------------------------------------------
    // Controllo strutturale sul numero di pagine
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("Controllo numero pagine")
    class PageCount {

        @Test
        @DisplayName("Un numero di pagine diverso dal template è non conforme e salta il confronto visivo")
        void differentPageCountFailsImmediately() {
            byte[] reference = TestPdfFactory.reference();
            byte[] actual = TestPdfFactory.spec().pageCount(2).build();

            PdfComplianceReport report = PdfComplianceChecker.check(reference, actual, template());

            assertFalse(report.isCompliant());
            assertEquals(List.of("Numero pagine atteso: 1, trovato: 2"), report.structuralErrors());
            assertNull(report.visualResult(), "Il confronto visivo non deve essere eseguito");
            AssertionError error = assertThrows(AssertionError.class, report::assertCompliant);
            assertTrue(error.getMessage().contains("ERRORI STRUTTURALI"));
        }

        @Test
        @DisplayName("Senza pagine attese nel template, il comparatore rileva comunque il numero di pagine diverso")
        void differentPageCountDetectedByVisualPreCheck() {
            PdfDocumentTemplate noPageCount = PdfDocumentTemplate.builder("NO_PAGE_COUNT").build();
            byte[] reference = TestPdfFactory.reference();
            byte[] actual = TestPdfFactory.spec().pageCount(3).build();

            PdfComplianceReport report = PdfComplianceChecker.check(reference, actual, noPageCount);

            assertFalse(report.isCompliant());
            assertTrue(report.structuralErrors().isEmpty());
            assertFalse(report.visualResult().equal());
            assertEquals(-1, report.visualResult().pageCount());
            assertTrue(report.visualResult().errorMessage().contains("atteso: 1, trovato: 3"));
        }

        @Test
        @DisplayName("Un contenuto non PDF viene segnalato come errore strutturale")
        void invalidPdfContentIsStructuralError() {
            byte[] reference = TestPdfFactory.reference();
            byte[] notAPdf = "questo non è un pdf".getBytes();

            PdfComplianceReport report = PdfComplianceChecker.check(reference, notAPdf, template());

            assertFalse(report.isCompliant());
            assertFalse(report.structuralErrors().isEmpty());
            assertNull(report.visualResult());
        }

        private static byte[] loadResource(String path) {
            try (InputStream is = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(path)) {
                if (is == null) {
                    throw new IllegalStateException("Risorsa non trovata nel classpath: " + path);
                }
                return is.readAllBytes();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
