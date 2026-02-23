package it.pagopa.interop.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import org.apache.commons.io.FileUtils;

public final class BlobFileCreationUtils {
    public static File createTempFile(String prefix, InputStream fileContent) {
        try {
            File tempFile = Files.createTempFile(prefix, null).toFile();
            tempFile.deleteOnExit();
            FileUtils.copyInputStreamToFile(fileContent, tempFile);
            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException("Error occured during temp file creation", e);
        }
    }
}
