package it.pagopa.pn.interop.cucumber.utility;

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
}
