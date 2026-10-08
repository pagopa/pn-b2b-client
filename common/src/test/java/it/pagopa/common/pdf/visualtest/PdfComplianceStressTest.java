package it.pagopa.common.pdf.visualtest;

import it.pagopa.common.pdf.visualtest.support.TestPdfFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Stress test suite offline e mirata sulle 3 dimensioni critiche del modulo di validazione PDF:
 * <ul>
 *   <li><b>Dimensione 1</b>: Robustezza & Gestione Edge Cases (file vuoti, corrotti, protetti, pagine ruotate)</li>
 *   <li><b>Dimensione 3</b>: Limiti del Confronto Visivo & Masking (micro-jitter, tolleranza, leakage fuori maschera)</li>
 *   <li><b>Dimensione 4</b>: Stress Regole di Campo & Locators (Unicode, multiline, duplicazione etichette, boundary conditions)</li>
 * </ul>
 */
class PdfComplianceStressTest {

    private static final float MAX_OFFSET_X = 250f;
    private static final float MAX_OFFSET_Y = 3f;

    private static PdfDocumentTemplate defaultTemplate() {
        return PdfDocumentTemplate.builder("STRESS_TEMPLATE")
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

    // =======================================================================
    // DIMENSIONE 1: Robustezza & Edge Cases sui PDF
    // =======================================================================
    @Nested
    @DisplayName("Dimensione 1: Robustezza & Anomalie File PDF")
    class Dimension1Robustness {

        @Test
        @DisplayName("Payload null genera un errore strutturale gestito senza eccezioni incontrollate")
        void nullPayloadHandling() {
            byte[] reference = TestPdfFactory.reference();

            PdfComplianceReport report = PdfComplianceChecker.check(reference, null, defaultTemplate());

            assertFalse(report.isCompliant());
            assertFalse(report.structuralErrors().isEmpty(), "Atteso almeno un errore strutturale");
            assertNull(report.visualResult());
        }

        @Test
        @DisplayName("Payload byte array vuoto (0 byte) genera errore strutturale controllato")
        void emptyPayloadHandling() {
            byte[] reference = TestPdfFactory.reference();
            byte[] empty = new byte[0];

            PdfComplianceReport report = PdfComplianceChecker.check(reference, empty, defaultTemplate());

            assertFalse(report.isCompliant());
            assertFalse(report.structuralErrors().isEmpty());
            assertNull(report.visualResult());
        }

        @ParameterizedTest(name = "[{index}] Stringa non PDF: \"{0}\"")
        @ValueSource(strings = {
                "NOT_A_PDF_HEADER",
                "%PDF-1.4 ma tronco subito senza xref",
                "   \r\n\t  ",
                "{\"error\": \"gateway timeout\"}"
        })
        @DisplayName("File non validi o con header fittizi vengono bloccati come errori strutturali")
        void nonPdfOrGarbageHandling(String garbage) {
            byte[] reference = TestPdfFactory.reference();
            byte[] actual = garbage.getBytes(StandardCharsets.UTF_8);

            PdfComplianceReport report = PdfComplianceChecker.check(reference, actual, defaultTemplate());

            assertFalse(report.isCompliant());
            assertFalse(report.structuralErrors().isEmpty());
            assertNull(report.visualResult());
        }

        @Test
        @DisplayName("PDF valido ma troncato a metà dei byte fallisce in modo sicuro senza crash")
        void truncatedValidPdfHandling() {
            byte[] reference = TestPdfFactory.reference();
            // Troncamento del PDF valido a circa il 40% della sua dimensione
            byte[] truncated = Arrays.copyOf(reference, reference.length * 2 / 5);

            PdfComplianceReport report = PdfComplianceChecker.check(reference, truncated, defaultTemplate());

            assertFalse(report.isCompliant());
            assertFalse(report.structuralErrors().isEmpty());
            assertNull(report.visualResult());
        }

        @Test
        @DisplayName("PDF completamente vuoto (pagina bianca senza testo né immagini) segnala l'assenza dei campi attesi")
        void blankPagePdfReportsMissingFieldsWithoutNpe() {
            byte[] reference = TestPdfFactory.reference();
            byte[] blankPdf = TestPdfFactory.spec().blankPage(true).build();

            PdfComplianceReport report = PdfComplianceChecker.check(reference, blankPdf, defaultTemplate());

            assertFalse(report.isCompliant());
            assertEquals(3, report.fieldIssues().size(), "Tutti i campi devono risultare non trovati");
            for (FieldIssue issue : report.fieldIssues()) {
                assertTrue(issue.locatedFragments().isEmpty());
                assertTrue(issue.errorMessage().contains("non trovato"));
            }
        }

        @Test
        @DisplayName("PDF protetto da password utente fallisce con errore strutturale chiaro")
        void passwordProtectedPdfFailsGracefully() {
            byte[] reference = TestPdfFactory.reference();
            byte[] encrypted = TestPdfFactory.spec().password("secret123", "ownerSecret").build();

            PdfComplianceReport report = PdfComplianceChecker.check(reference, encrypted, defaultTemplate());

            assertFalse(report.isCompliant());
            assertFalse(report.structuralErrors().isEmpty(), "Deve essere registrato un errore strutturale di apertura");
        }

        @Test
        @DisplayName("PDF con orientamento pagina ruotato (90 gradi) viene processato senza crash")
        void rotatedPageHandling() {
            byte[] reference = TestPdfFactory.reference();
            byte[] rotated = TestPdfFactory.spec().pageRotation(90).build();

            // Documento ruotato: l'estrazione non deve lanciare eccezioni
            assertDoesNotThrow(() -> {
                PdfComplianceReport report = PdfComplianceChecker.check(reference, rotated, defaultTemplate());
                assertNotNull(report);
            });
        }
    }

    // =======================================================================
    // DIMENSIONE 3: Limiti del Confronto Visivo, Soglie & Masking
    // =======================================================================
    @Nested
    @DisplayName("Dimensione 3: Precisione Visiva, Soglie & Masking")
    class Dimension3VisualAndMasking {

        @Test
        @DisplayName("Variazioni dinamiche legittime di lunghezze molto diverse sono correttamente coperte dalla maschera")
        void extremeDynamicValueLengthsAreMasked() {
            byte[] reference = TestPdfFactory.reference();
            // Valori validi ma con lunghezze e cifre estreme
            byte[] actual = TestPdfFactory.spec()
                    .id("00000000-0000-4000-8000-000000000000")
                    .date("2099-12-31")
                    .amount("999999999999") // amount molto lungo
                    .build();

            PdfComplianceReport report = PdfComplianceChecker.check(reference, actual, defaultTemplate());

            assertTrue(report.isCompliant(), () -> "Tutti i campi sono validi ed esclusi: " + report);
            assertTrue(report.visualResult().equal());
        }

        @Test
        @DisplayName("Un'anomalia grafica posizionata immediatamente fuori dall'area mascherata viene intercettata")
        void disturbanceOutsideMaskIsDetected() {
            byte[] reference = TestPdfFactory.reference();
            // Inseriamo un testo anomalo appena fuori dall'area del valore ID (+260pt a destra della label)
            byte[] actualWithDisturbance = TestPdfFactory.spec()
                    .extraTextNearId("DISTURBANCE_OUTSIDE_MASK", 260f, 0f)
                    .build();

            PdfComplianceReport report = PdfComplianceChecker.check(reference, actualWithDisturbance, defaultTemplate());

            assertFalse(report.isCompliant(), "La distorsione visiva al di fuori della maschera deve essere rilevata");
            assertNotNull(report.visualResult());
            assertFalse(report.visualResult().equal());
        }

        @Test
        @DisplayName("Un macro-spostamento verticale del layout statico (es. 10pt) supera la soglia di tolleranza")
        void macroLayoutShiftIsAlwaysDetected() {
            byte[] reference = TestPdfFactory.reference();
            byte[] shifted = TestPdfFactory.spec().layoutShiftY(10f).build();

            PdfComplianceReport report = PdfComplianceChecker.check(reference, shifted, defaultTemplate());

            assertFalse(report.isCompliant());
            assertNotNull(report.visualResult());
            assertFalse(report.visualResult().equal(), "Lo shift di 10pt deve essere rilevato come diff visivo");
        }

        @Test
        @DisplayName("Modifica macroscopica del titolo statico viene rilevata con diff visivo")
        void macroscopicStaticTextChangeIsDetected() {
            byte[] reference = TestPdfFactory.reference();
            byte[] changedTitle = TestPdfFactory.spec().title("TITOLO COMPLETAMENTE SOSTITUITO").build();

            PdfComplianceReport report = PdfComplianceChecker.check(reference, changedTitle, defaultTemplate());

            assertFalse(report.isCompliant());
            assertNotNull(report.visualResult());
            assertFalse(report.visualResult().equal());
        }
    }

    // =======================================================================
    // DIMENSIONE 4: Stress Regole di Campo & Locators
    // =======================================================================
    @Nested
    @DisplayName("Dimensione 4: Stress Regole di Campo & Locators")
    class Dimension4FieldRulesAndLocators {

        @Test
        @DisplayName("Testo statico e dinamico con caratteri speciali (trattini, parentesi, punteggiatura)")
        void specialCharactersAndPunctuationHandling() {
            String testParagraph = "Rif. pratica: [PROJ-2026/A-1] - Valore stabilito (ex art. 26).";
            byte[] pdf = TestPdfFactory.spec().paragraph(testParagraph).build();

            ExtractedPdfText extracted = PdfTextExtractor.extract(pdf);
            assertNotNull(extracted);

            PdfDocumentTemplate template = PdfDocumentTemplate.builder("SPECIAL_CHARS")
                    .field("pratica",
                            FieldLocators.regex(Pattern.compile("\\[PROJ-2026/A-1\\]")),
                            FieldValidators.containsText("PROJ-2026/A-1"),
                            MaskStrategy.TIGHT_BOX)
                    .build();

            DynamicFieldAnalyzer.AnalysisResult analysis = DynamicFieldAnalyzer.analyze(extracted, template);
            assertTrue(analysis.isValid(), "Il campo con caratteri speciali e parentesi quadre deve essere individuato e validato");
            assertEquals(1, analysis.exclusions().size());
        }

        @Test
        @DisplayName("Duplicazione della medesima label di ancoraggio nella stessa pagina individua i valori multipli")
        void duplicateAnchorLabelsOnSamePage() {
            byte[] pdf = TestPdfFactory.spec().duplicateIdField(true).build();

            ExtractedPdfText extracted = PdfTextExtractor.extract(pdf);
            for (TextFragment f : extracted.allFragments()) {
                System.out.println("DEBUG_FRAG: '" + f.text() + "' at x=" + f.x() + ", y=" + f.y());
            }

            FieldLocator locator = FieldLocators.labelProximity("identificativo:", MAX_OFFSET_X, MAX_OFFSET_Y);
            List<TextFragment> fragments = locator.locate(extracted.allFragments());
            System.out.println("LOCATED: " + fragments.size());

            // Devono essere individuati i frammenti di entrambe le istanze
            assertTrue(fragments.size() >= 2, "Devono essere localizzati i valori di entrambe le occorrenze della label: trovati " + fragments.size());
        }

        @Test
        @DisplayName("Boundary conditions per FieldLocators.fixedRegion: frammenti dentro, sul confine e fuori")
        void fixedRegionBoundaryConditions() {
            byte[] pdf = TestPdfFactory.reference();
            ExtractedPdfText extracted = PdfTextExtractor.extract(pdf);
            List<TextFragment> all = extracted.allFragments();

            // Cerchiamo un frammento reale per verificarne le coordinate
            TextFragment titleFragment = all.stream()
                    .filter(f -> f.text().contains("ATTESTAZIONE"))
                    .findFirst()
                    .orElseThrow();

            // 1. Regione che include esattamente il titolo con 1pt di margine
            FieldLocator insideLocator = FieldLocators.fixedRegion(
                    0,
                    titleFragment.x() - 1f,
                    titleFragment.y() - 1f,
                    titleFragment.width() + 2f,
                    titleFragment.height() + 2f);
            List<TextFragment> inside = insideLocator.locate(all);
            assertFalse(inside.isEmpty(), "Il frammento deve essere trovato all'interno della regione");
            assertTrue(inside.stream().anyMatch(f -> f.text().contains("ATTESTAZIONE")));

            // 2. Regione disgiunta spostata di 100pt più a destra rispetto al frammento
            FieldLocator outsideLocator = FieldLocators.fixedRegion(
                    0,
                    titleFragment.xEnd() + 50f,
                    titleFragment.y(),
                    100f,
                    titleFragment.height());
            List<TextFragment> outside = outsideLocator.locate(all);
            assertFalse(outside.stream().anyMatch(f -> f.text().contains("ATTESTAZIONE")),
                    "Il titolo non deve trovarsi nella regione disgiunta");
        }

        @Test
        @DisplayName("Boundary condition di maxOffsetPtX per labelProximity: valore appena dentro vs oltre il limite")
        void labelProximityMaxOffsetBoundary() {
            byte[] pdf = TestPdfFactory.reference();
            ExtractedPdfText extracted = PdfTextExtractor.extract(pdf);

            // Trova la distanza X effettiva tra label "identificativo:" e il valore
            TextFragment labelFrag = extracted.allFragments().stream()
                    .filter(f -> f.text().toLowerCase().contains("identificativo:"))
                    .findFirst()
                    .orElseThrow();

            TextFragment valueFrag = extracted.allFragments().stream()
                    .filter(f -> f.text().equals(TestPdfFactory.DEFAULT_ID))
                    .findFirst()
                    .orElseThrow();

            float actualDistanceX = valueFrag.x() - labelFrag.xEnd();
            assertTrue(actualDistanceX > 0, "Il valore deve essere a destra della label");

            // Offset sufficiente (+ 5pt): deve trovare il valore
            FieldLocator matchLocator = FieldLocators.labelProximity("identificativo:", actualDistanceX + 5f, MAX_OFFSET_Y);
            List<TextFragment> match = matchLocator.locate(extracted.allFragments());
            assertTrue(match.stream().anyMatch(f -> f.text().equals(TestPdfFactory.DEFAULT_ID)));

            // Offset troppo stretto (- 5pt): NON deve trovare il valore
            FieldLocator failLocator = FieldLocators.labelProximity("identificativo:", actualDistanceX - 5f, MAX_OFFSET_Y);
            List<TextFragment> noMatch = failLocator.locate(extracted.allFragments());
            assertFalse(noMatch.stream().anyMatch(f -> f.text().equals(TestPdfFactory.DEFAULT_ID)));
        }

        @Test
        @DisplayName("FieldValidators.containsText gestisce case-insensitivity e spazi multipli correttamente")
        void containsTextNormalization() {
            FieldValidator validator = FieldValidators.containsText("pagopa s.p.a.");

            TextFragment f1 = new TextFragment("PagoPA", 10, 10, 50, 12, 0);
            TextFragment f2 = new TextFragment("S.p.A.", 65, 10, 40, 12, 0);

            // I frammenti combinati formano "PagoPA S.p.A."
            String err = validator.validate(List.of(f1, f2));
            assertNull(err, "containsText deve fare match ignorando il case e raggruppando i frammenti");
        }
    }
}
