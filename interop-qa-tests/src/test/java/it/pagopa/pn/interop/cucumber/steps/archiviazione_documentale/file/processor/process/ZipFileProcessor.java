package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.processor.process;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.ContentType;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.processor.IFileProcessor;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.processor.model.ProcessedFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipFileProcessor implements IFileProcessor {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public boolean supports(ContentType contentType) {
        return contentType == ContentType.ZIP;
    }

    @Override
    public ProcessedFile process(ProcessedFile input) {
        try {
            byte[] decompressed = unzipToBytes(input.content());

            ContentType nextType = detectJsonFlavor(decompressed);

            return new ProcessedFile(
                    new ByteArrayInputStream(decompressed),
                    nextType
            );

        } catch (Exception e) {
            throw new RuntimeException("Errore durante processing ZIP", e);
        }
    }

    private byte[] unzipToBytes(InputStream zipStream) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(zipStream);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            ZipEntry entry = zis.getNextEntry();
            if (entry == null) {
                throw new IllegalStateException("File ZIP vuoto");
            }

            zis.transferTo(out);
            return out.toByteArray();
        }
    }

    /**
     * Decide tra NDJSON e JSON provando a parsare.
     */
    private ContentType detectJsonFlavor(byte[] bytes) {

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8))) {

            String firstLine = reader.readLine();
            if (firstLine == null) {
                throw new IllegalStateException("File ZIP vuoto");
            }

            // Se la prima riga è JSON valido → NDJSON
            MAPPER.readTree(firstLine);

            return ContentType.NDJSON;

        } catch (Exception e) {
            // fallback: JSON singolo
            try {
                MAPPER.readTree(bytes);
                return ContentType.JSON;
            } catch (Exception ex) {
                throw new RuntimeException("Contenuto ZIP non è JSON/NDJSON valido", ex);
            }
        }
    }
}