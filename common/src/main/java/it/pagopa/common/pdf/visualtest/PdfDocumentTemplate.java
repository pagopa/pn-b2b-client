package it.pagopa.common.pdf.visualtest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Template dichiarativo che descrive la struttura attesa di un documento PDF.
 *
 * <p>Un {@code PdfDocumentTemplate} contiene:</p>
 * <ul>
 *   <li>La chiave identificativa del template (usata dal {@link DocumentTemplateRegistry})</li>
 *   <li>Il numero di pagine atteso</li>
 *   <li>L'elenco delle {@link FieldDefinition} che descrivono i campi dinamici da validare e mascherare</li>
 * </ul>
 *
 * <p>Istanziato tramite il builder fluente {@link Builder}.</p>
 */
public final class PdfDocumentTemplate {

    private final String key;
    private final int expectedPageCount;
    private final List<FieldDefinition> fieldDefinitions;

    private PdfDocumentTemplate(Builder builder) {
        this.key = Objects.requireNonNull(builder.key, "key obbligatoria");
        this.expectedPageCount = builder.expectedPageCount;
        this.fieldDefinitions = Collections.unmodifiableList(new ArrayList<>(builder.fieldDefinitions));
    }

    /** Chiave identificativa univoca del template. */
    public String key() {
        return key;
    }

    /** Numero di pagine atteso per i documenti conformi a questo template ({@code -1} = non verificato). */
    public int expectedPageCount() {
        return expectedPageCount;
    }

    /** Definizioni dei campi dinamici da validare. */
    public List<FieldDefinition> fieldDefinitions() {
        return fieldDefinitions;
    }

    // -----------------------------------------------------------------------
    // Builder
    // -----------------------------------------------------------------------

    public static Builder builder(String key) {
        return new Builder(key);
    }

    public static final class Builder {

        private final String key;
        private int expectedPageCount = -1;
        private final List<FieldDefinition> fieldDefinitions = new ArrayList<>();

        private Builder(String key) {
            this.key = Objects.requireNonNull(key, "key obbligatoria");
        }

        /**
         * Imposta il numero di pagine atteso. Se non chiamato, il controllo sul conteggio pagine
         * viene saltato.
         */
        public Builder expectedPageCount(int count) {
            if (count < 1) throw new IllegalArgumentException("expectedPageCount deve essere >= 1");
            this.expectedPageCount = count;
            return this;
        }

        /**
         * Aggiunge una definizione di campo.
         *
         * @param fieldName   nome leggibile del campo (usato nei messaggi di errore)
         * @param locator     strategia di localizzazione del campo
         * @param validator   strategia di validazione del valore trovato
         * @param mask        strategia di mascheramento per il visual diff
         */
        public Builder field(String fieldName, FieldLocator locator, FieldValidator validator, MaskStrategy mask) {
            fieldDefinitions.add(new FieldDefinition(fieldName, locator, validator, mask));
            return this;
        }

        public PdfDocumentTemplate build() {
            return new PdfDocumentTemplate(this);
        }
    }

    // -----------------------------------------------------------------------
    // FieldDefinition
    // -----------------------------------------------------------------------

    /**
     * Definisce un singolo campo dinamico nel template: nome, locator, validator e mask.
     */
    public record FieldDefinition(
            String fieldName,
            FieldLocator locator,
            FieldValidator validator,
            MaskStrategy maskStrategy) {
    }
}
