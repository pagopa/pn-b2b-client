package it.pagopa.common.pdf.visualtest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Registro dei {@link PdfDocumentTemplate} disponibili, identificati da una chiave stringa.
 *
 * <p>Il registro è <em>privo di stato statico condiviso</em>: ogni istanza è autonoma
 * e non utilizza variabili static o singleton. Le istanze in diversi moduli possono
 * coesistere senza interferenze.</p>
 *
 * <p>Nel modulo {@code pn-b2bclient} questa classe viene configurata come Spring Bean
 * nella classe {@code @Configuration} del modulo, con tutti i template registrati via
 * {@link Builder}.</p>
 *
 * <p>Esempio di utilizzo:</p>
 * <pre>{@code
 * DocumentTemplateRegistry registry = DocumentTemplateRegistry.builder()
 *     .register(myTemplate)
 *     .build();
 *
 * PdfDocumentTemplate template = registry.get("SENDER_ACK")
 *     .orElseThrow(() -> new IllegalArgumentException("Template non trovato"));
 * }</pre>
 */
public final class DocumentTemplateRegistry {

    private final Map<String, PdfDocumentTemplate> templates;

    private DocumentTemplateRegistry(Map<String, PdfDocumentTemplate> templates) {
        this.templates = Map.copyOf(templates);
    }

    /**
     * Cerca un template per chiave.
     *
     * @param key chiave identificativa del template (case-sensitive)
     * @return {@link Optional} con il template se presente, vuoto altrimenti
     */
    public Optional<PdfDocumentTemplate> get(String key) {
        return Optional.ofNullable(templates.get(key));
    }

    /**
     * Restituisce il template per chiave, sollevando eccezione se non trovato.
     *
     * @param key chiave identificativa del template
     * @return il template associato alla chiave
     * @throws IllegalArgumentException se la chiave non è registrata
     */
    public PdfDocumentTemplate getOrThrow(String key) {
        return get(key).orElseThrow(() ->
                new IllegalArgumentException(
                        "Nessun PdfDocumentTemplate registrato per la chiave: \"" + key + "\". "
                        + "Template disponibili: " + templates.keySet()));
    }

    /** @return il numero di template registrati */
    public int size() {
        return templates.size();
    }

    // -----------------------------------------------------------------------
    // Builder
    // -----------------------------------------------------------------------

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final Map<String, PdfDocumentTemplate> templates = new HashMap<>();

        private Builder() {
        }

        /**
         * Registra un template, usando {@link PdfDocumentTemplate#key()} come chiave.
         *
         * @throws IllegalStateException se un template con la stessa chiave è già stato registrato
         */
        public Builder register(PdfDocumentTemplate template) {
            String key = template.key();
            if (templates.containsKey(key)) {
                throw new IllegalStateException(
                        "Template con chiave \"" + key + "\" già registrato nel registry");
            }
            templates.put(key, template);
            return this;
        }

        public DocumentTemplateRegistry build() {
            return new DocumentTemplateRegistry(templates);
        }
    }
}
