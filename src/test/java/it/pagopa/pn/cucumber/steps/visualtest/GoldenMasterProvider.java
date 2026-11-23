package it.pagopa.pn.cucumber.steps.visualtest;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Optional;

import static org.assertj.core.api.Assumptions.assumeThat;

/**
 * Golden master of a document: only the one committed in the repo. A golden master is rendered once by the Template
 * Engine ({@link GoldenMasterGenerationTool}), reviewed by a person and committed. There is no fallback: a scenario
 * whose document has no committed golden master is skipped, never compared against something rendered on the fly.
 * A PDF downloaded from an environment never becomes a golden master.
 */
public final class GoldenMasterProvider {

    static final String REPO_GOLDEN_ROOT = "it/pagopa/pn/cucumber/visualTest/expected-pdfs/";
    static final Path GENERATED_GOLDEN_ROOT = Paths.get("target", "vrt", "generated-golden");

    private GoldenMasterProvider() {
    }

    /** Skips the running scenario when the document has no golden master in the repo. Call it before touching the environment. */
    public static void assumeGoldenMasterInRepo(VisualDocument document) {
        assumeThat(fromRepo(document).isPresent())
                .as("Golden master di %s assente nel repo (%s%s/expected.pdf): scenario saltato",
                        document.key(), REPO_GOLDEN_ROOT, document.folder())
                .isTrue();
    }

    public static byte[] fromRepoOrFail(VisualDocument document) {
        return fromRepo(document).orElseThrow(() -> new IllegalStateException(
                "Golden master di " + document.key() + " assente nel repo (" + REPO_GOLDEN_ROOT + document.folder()
                        + "/expected.pdf)"));
    }

    public static Optional<byte[]> fromRepo(VisualDocument document) {
        ClassPathResource resource = new ClassPathResource(REPO_GOLDEN_ROOT + document.folder() + "/expected.pdf");
        if (!resource.exists()) {
            return Optional.empty();
        }
        try (InputStream is = resource.getInputStream()) {
            return Optional.of(is.readAllBytes());
        } catch (IOException e) {
            throw new UncheckedIOException("Golden master non leggibile: " + resource.getPath(), e);
        }
    }

    /** Creation date written by the Template Engine in the committed golden master. */
    public static Optional<OffsetDateTime> repoGoldenCreation(VisualDocument document) {
        return fromRepo(document).map(pdf -> {
            try (PDDocument doc = Loader.loadPDF(pdf)) {
                Calendar created = doc.getDocumentInformation().getCreationDate();
                return created == null ? null : created.toInstant().atOffset(ZoneOffset.UTC);
            } catch (IOException e) {
                throw new UncheckedIOException("Golden master non leggibile per " + document.key(), e);
            }
        });
    }
}
