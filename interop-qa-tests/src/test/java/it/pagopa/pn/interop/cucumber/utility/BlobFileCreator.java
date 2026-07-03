package it.pagopa.pn.interop.cucumber.utility;

import java.io.File;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class BlobFileCreator {
    public Resource createBlobFile(String blobFilePath, String fileNameToCreate) {
        Path filePath = Paths.get(blobFilePath);
        byte[] fileContent = null;
        try {
            fileContent = Files.readAllBytes(filePath);
            Path newFilePath = Paths.get(fileNameToCreate);
            Files.write(newFilePath, fileContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new FileSystemResource(filePath);
    }

    public Resource createBlobWithTempFile(String prefix, byte[] fileContent) {
        try {
            File tempFile = Files.createTempFile(prefix, null).toFile();
            tempFile.deleteOnExit();
            Files.write(tempFile.toPath(), fileContent);
            return new FileSystemResource(tempFile);
        } catch (IOException e) {
            throw new RuntimeException("Error occured during temp file creation", e);
        }
    }

    public void deleteTempFile(String fileName) {
        try {
            File file = new File(fileName);
            file.delete();
        } catch (Exception e) {
            throw new RuntimeException("Error occured during temp file deletion");
        }
    }
}
