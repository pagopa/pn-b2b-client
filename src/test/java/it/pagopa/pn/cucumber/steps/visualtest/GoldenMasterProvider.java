package it.pagopa.pn.cucumber.steps.visualtest;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Optional;

/**
 * Golden master of a document: the one committed in the repo, otherwise rendered by the Template Engine
 * from the document fixture. A PDF downloaded from an environment never becomes a golden master.
 */
@Slf4j
public class GoldenMasterProvider {

    static final String REPO_GOLDEN_ROOT = "it/pagopa/pn/cucumber/visualTest/expected-pdfs/";
    static final Path GENERATED_GOLDEN_ROOT = Paths.get("target", "vrt", "generated-golden");

    private final TemplateEngineGoldenRenderer renderer;

    public GoldenMasterProvider(TemplateEngineGoldenRenderer renderer) {
        this.renderer = renderer;
    }

    public byte[] resolve(VisualDocument document) {
        return fromRepo(document).orElseGet(() -> fromTemplateEngine(document));
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

    private byte[] fromTemplateEngine(VisualDocument document) {
        byte[] pdf = renderer.render(document);
        Path target = GENERATED_GOLDEN_ROOT.resolve(document.folder()).resolve("expected.pdf");
        try {
            Files.createDirectories(target.getParent());
            Files.write(target, pdf);
        } catch (IOException e) {
            throw new UncheckedIOException("Impossibile salvare il golden generato in " + target, e);
        }
        log.warn("Golden master di {} assente nel repo: generato dal Template Engine in {}. "
                        + "Dopo la revisione visiva copiarlo in src/test/resources/{}{}/expected.pdf",
                document.key(), target.toAbsolutePath(), REPO_GOLDEN_ROOT, document.folder());
        return pdf;
    }
}
