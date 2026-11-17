package it.pagopa.common.pdf.visualtest;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StaticTextAndLayoutRulesTest {

    private static final PdfDocumentTemplate IUN_TEMPLATE = PdfDocumentTemplate.builder("T")
            .field("IUN", FieldLocators.regex(Pattern.compile("[A-Z]{4}-[A-Z]{4}-[A-Z]{4}-\\d{6}-[A-Z]-\\d")),
                    FieldValidators.present(), MaskStrategy.TO_LINE_END_AND_NEXT_LINE)
            .build();

    @Test
    void dynamicValuesAndLineBreaksDoNotChangeTheStaticText() {
        ExtractedPdfText golden = text(List.of("notifica", "IUN", "AAAA-BBBB-CCCC-202601-A-1,", "dall'art.", "26"));
        ExtractedPdfText reflowed = text(List.of("notifica", "IUN", "WMWM-MWMW-WMWM-202612-W-1,", "dall'", "art.", "26"));

        assertTrue(StaticTextComparator.firstDifference(
                StaticTextComparator.normalizedWords(golden, IUN_TEMPLATE),
                StaticTextComparator.normalizedWords(reflowed, IUN_TEMPLATE)).isEmpty());
    }

    @Test
    void aChangedStaticWordIsReported() {
        ExtractedPdfText golden = text(List.of("destinatario", "Mario", "Gherkin"));
        ExtractedPdfText actual = text(List.of("destinatario", "Maria", "Gherkin"));

        assertTrue(StaticTextComparator.firstDifference(
                StaticTextComparator.normalizedWords(golden, IUN_TEMPLATE),
                StaticTextComparator.normalizedWords(actual, IUN_TEMPLATE)).isPresent());
    }

    @Test
    void occurrenceLocatorMatchesMultiWordLabelsAndSkipsProse() {
        List<TextFragment> fragments = List.of(
                new TextFragment("il", 28, 700, 8, 10, 0),
                new TextFragment("domicilio", 40, 700, 40, 10, 0),
                new TextFragment("digitale", 85, 700, 35, 10, 0),
                new TextFragment("speciale", 125, 700, 35, 10, 0),
                new TextFragment("Domicilio", 28, 600, 40, 10, 0),
                new TextFragment("digitale", 74, 600, 35, 10, 0),
                new TextFragment("prova@pec.it", 298, 600, 80, 10, 0),
                new TextFragment("Codice", 28, 586, 30, 10, 0));

        List<TextFragment> located = FieldLocators.labelProximityOccurrence("Domicilio digitale", 1).locate(fragments);

        assertEquals(List.of("prova@pec.it"), located.stream().map(TextFragment::text).toList());
    }

    @Test
    void toLineEndAndNextLineMasksTheRestOfTheLineAndTheFollowingOne() {
        TextFragment value = new TextFragment("AAAA-BBBB-CCCC-202601-A-1", 200, 600, 140, 10, 0);

        List<ExclusionArea> areas = ExclusionArea.areas(value, MaskStrategy.TO_LINE_END_AND_NEXT_LINE, 595);

        assertEquals(2, areas.size());
        assertTrue(areas.get(0).x() < 200 && areas.get(0).x() + areas.get(0).width() >= 595);
        assertTrue(areas.get(1).x() == 0 && areas.get(1).y() < 600 - 10);
    }

    private static ExtractedPdfText text(List<String> words) {
        List<TextFragment> fragments = new ArrayList<>();
        float x = 28;
        for (String w : words) {
            fragments.add(new TextFragment(w, x, 700, w.length() * 5f, 10, 0));
            x += w.length() * 5f + 4;
        }
        return new ExtractedPdfText(List.of(new PageText(0, fragments)));
    }
}
