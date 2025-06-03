package it.pagopa.interop.config.springconfig.springconfig;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;

public class FileHttpMessageConverter implements HttpMessageConverter<File> {

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return File.class.isAssignableFrom(clazz);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return File.class.isAssignableFrom(clazz);
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM);
    }

    @Override
    public File read(Class<? extends File> clazz, HttpInputMessage inputMessage) throws IOException {
        Path tempFile = Files.createTempFile("downloaded", ".tmp");
        Files.copy(inputMessage.getBody(), tempFile, StandardCopyOption.REPLACE_EXISTING);
        return tempFile.toFile();
    }

    @Override
    public void write(File file, MediaType contentType, HttpOutputMessage outputMessage) throws IOException {
        Files.copy(file.toPath(), outputMessage.getBody());
    }
}
