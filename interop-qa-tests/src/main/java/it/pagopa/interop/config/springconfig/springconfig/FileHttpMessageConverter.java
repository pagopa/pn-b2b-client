package it.pagopa.interop.config.springconfig.springconfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

public class FileHttpMessageConverter extends AbstractHttpMessageConverter<File> {
    private final boolean deleteFilesOnShutdown;
    
    public FileHttpMessageConverter() {
        this(true);
    }
    
    public FileHttpMessageConverter(boolean deleteFilesOnShutdown) {
        super(MediaType.ALL);
        this.deleteFilesOnShutdown = deleteFilesOnShutdown;
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return File.class.isAssignableFrom(clazz);
    }

    @Override
    protected File readInternal(Class<? extends File> clazz, HttpInputMessage inputMessage)
        throws IOException, HttpMessageNotReadableException {
        File of = File.createTempFile(this.getClass().getName() + "-", ".tmp");
        if (deleteFilesOnShutdown) {
            of.deleteOnExit();
        }

        try (FileOutputStream out = new FileOutputStream(of)) {
            inputMessage.getBody().transferTo(out);
            return of;
        }
    }

    @Override
    protected void writeInternal(File file, HttpOutputMessage outputMessage)
        throws IOException, HttpMessageNotWritableException {
        outputMessage.getBody().write(file.getName().getBytes());
    }
}
