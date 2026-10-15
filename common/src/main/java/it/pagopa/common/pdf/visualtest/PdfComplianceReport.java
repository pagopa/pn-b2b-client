package it.pagopa.common.pdf.visualtest;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

/**
 * Report aggregato prodotto da {@link PdfComplianceChecker}.
 *
 * <p>Contiene:</p>
 * <ul>
 *   <li>Gli errori di validazione dei campi dinamici ({@link FieldIssue})</li>
 *   <li>Il risultato del confronto visivo pixel-level ({@link PdfVisualComparator.VisualDiffResult})</li>
 *   <li>Gli eventuali errori di struttura (es. numero pagine errato)</li>
 * </ul>
 */
public final class PdfComplianceReport {

    private final List<FieldIssue> fieldIssues;
    private final PdfVisualComparator.VisualDiffResult visualResult;
    private final List<String> structuralErrors;

    PdfComplianceReport(
            List<FieldIssue> fieldIssues,
            PdfVisualComparator.VisualDiffResult visualResult,
            List<String> structuralErrors) {
        this.fieldIssues       = Collections.unmodifiableList(new ArrayList<>(fieldIssues));
        this.visualResult      = visualResult;
        this.structuralErrors  = Collections.unmodifiableList(new ArrayList<>(structuralErrors));
    }

    /** Problemi riscontrati durante la validazione dei campi dinamici. */
    public List<FieldIssue> fieldIssues() {
        return fieldIssues;
    }

    /** Risultato del confronto visivo pixel-level (null se il confronto non è stato eseguito). */
    public PdfVisualComparator.VisualDiffResult visualResult() {
        return visualResult;
    }

    /** Errori strutturali (es. numero di pagine errato, PDF non leggibile). */
    public List<String> structuralErrors() {
        return structuralErrors;
    }

    /** @return {@code true} se il documento è pienamente conforme (zero errori su tutti i livelli) */
    public boolean isCompliant() {
        boolean fieldOk    = fieldIssues.isEmpty();
        boolean structOk   = structuralErrors.isEmpty();
        boolean visualOk   = visualResult == null || visualResult.equal();
        return fieldOk && structOk && visualOk;
    }

    /**
     * Solleva un'eccezione con report dettagliato se il documento non è conforme.
     *
     * @throws AssertionError con messaggio auto-esplicativo se {@link #isCompliant()} è {@code false}
     */
    public void assertCompliant() {
        if (isCompliant()) {
            return;
        }
        StringJoiner sb = new StringJoiner("\n");
        sb.add("═══════════════════════════════════════════════════════════════");
        sb.add("PDF COMPLIANCE CHECK – FALLITO");
        sb.add("═══════════════════════════════════════════════════════════════");

        if (!structuralErrors.isEmpty()) {
            sb.add("▸ ERRORI STRUTTURALI:");
            structuralErrors.forEach(e -> sb.add("  • " + e));
        }

        if (!fieldIssues.isEmpty()) {
            sb.add("▸ ERRORI DI VALIDAZIONE CAMPI DINAMICI:");
            fieldIssues.forEach(fi -> sb.add("  • " + fi));
        }

        if (visualResult != null && !visualResult.equal()) {
            sb.add("▸ DIFFERENZE VISIVE:");
            sb.add("  • " + visualResult.errorMessage());
            Path diffPath = visualResult.diffImagePath();
            if (diffPath != null) {
                sb.add("  • Immagine di diff: " + diffPath.toAbsolutePath());
            }
        }

        sb.add("═══════════════════════════════════════════════════════════════");
        throw new AssertionError(sb.toString());
    }

    @Override
    public String toString() {
        return "PdfComplianceReport{compliant=" + isCompliant()
                + ", fieldIssues=" + fieldIssues.size()
                + ", structuralErrors=" + structuralErrors.size()
                + ", visualEqual=" + (visualResult != null ? visualResult.equal() : "N/A")
                + "}";
    }
}
