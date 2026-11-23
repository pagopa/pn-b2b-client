package it.pagopa.common.pdf.visualtest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The look of the variable values: font, size, rendering mode and colours of each value of the document must be those
 * of the golden master. The area of a value is masked in the pixel comparison (its width changes), so a value that
 * turns bold, changes font or colour would otherwise go unnoticed.
 */
final class ValueStyleComparator {

    private ValueStyleComparator() {
    }

    /** One message per value whose style differs from the golden one; empty when they all match. */
    static List<String> differences(ExtractedPdfText expected, ExtractedPdfText actual, PdfDocumentTemplate template) {
        List<String> out = new ArrayList<>();
        for (PdfDocumentTemplate.FieldDefinition def : template.fieldDefinitions()) {
            List<TextFragment> e = ordered(def.locator().locate(expected.allFragments()));
            List<TextFragment> a = ordered(def.locator().locate(actual.allFragments()));
            if (e.size() != a.size()) {
                continue; // how many times a field appears is a matter for the field validation
            }
            for (int i = 0; i < e.size(); i++) {
                String expectedStyle = e.get(i).style();
                String actualStyle = a.get(i).style();
                if (expectedStyle.isEmpty() || actualStyle.isEmpty() || expectedStyle.equals(actualStyle)) {
                    continue;
                }
                out.add("campo [" + def.fieldName() + "] a y=" + Math.round(a.get(i).y()) + ": stile atteso \"" + expectedStyle
                        + "\", trovato \"" + actualStyle + "\"");
            }
        }
        return out;
    }

    private static List<TextFragment> ordered(List<TextFragment> fragments) {
        List<TextFragment> sorted = new ArrayList<>(fragments);
        sorted.sort(Comparator.comparingInt(TextFragment::pageIndex)
                .thenComparing(f -> -Math.round(f.y()))
                .thenComparingDouble(TextFragment::x));
        return sorted;
    }
}
