package it.pagopa.interop.config.springconfig.springconfig;

import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.FileDownloadMultipart;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.FileCleaningTracker;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

public class FileDownloadMultipartConverter extends AbstractHttpMessageConverter<FileDownloadMultipart> {

    public FileDownloadMultipartConverter() {
        super(MediaType.MULTIPART_FORM_DATA);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return FileDownloadMultipart.class.isAssignableFrom(clazz);
    }

    @Override
    protected FileDownloadMultipart readInternal(Class<? extends FileDownloadMultipart> clazz, HttpInputMessage inputMessage)
        throws HttpMessageNotReadableException {
        try {
            manageInputMessageMark(inputMessage.getBody());

            DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
            fileItemFactory.setSizeThreshold(1_000_000);
            fileItemFactory.setFileCleaningTracker(new FileCleaningTracker());
            FileUpload fileUpload = new FileUpload(fileItemFactory);
            List<FileItem> items = fileUpload.parseRequest(new HttpInputMessageRequestContext(inputMessage));

            FileDownloadMultipart result = new FileDownloadMultipart();
            for (FileItem item : items) {
                String fieldName = item.getFieldName();

                if (item.isFormField()) {
                    // È un campo di testo (metadata)
                    String value = item.getString(StandardCharsets.UTF_8.name());
                    switch (fieldName) {
                        case FileDownloadMultipart.JSON_PROPERTY_ID:
                            result.setId(UUID.fromString(value));
                            break;
                        case FileDownloadMultipart.JSON_PROPERTY_FILENAME:
                            result.setFilename(value);
                            break;
                        case FileDownloadMultipart.JSON_PROPERTY_CONTENT_TYPE:
                            result.setContentType(value);
                            break;
                        case FileDownloadMultipart.JSON_PROPERTY_PRETTY_NAME:
                            result.setPrettyName(value);
                            break;
                        default:
                            // no-op
                    }
                } else {
                    // È il file vero e proprio
                    if (FileDownloadMultipart.JSON_PROPERTY_FILE.equals(fieldName)) {
                        FileHttpMessageConverter fileHttpMessageConverter = new FileHttpMessageConverter();
                        inputMessage.getBody().reset();
                        result.setFile(fileHttpMessageConverter.read(File.class, inputMessage));
                    }
                }
            }

            return result;
        } catch (FileUploadException | IOException e) {
            throw new HttpMessageNotReadableException("Impossibile fare il parsing della risposta multipart", e, inputMessage);
        }
    }

    /* DEV. NOTE 24/07/2025: è richiesto il supporto al marking così da poter resettare lo stream,
    * in modo da permettere la lettura anche al componente FileHttpMessageConverter. Qualora Spring
    * smettesse di restituire uno stream resettabile, basterebbe smettere di usare
    * FileHttpMessageConverter e leggere il file direttamente attraverso l'oggetto FileItem,
    * per esempio con:
            File tempFile = File.createTempFile("download-", ".tmp");
            item.write(tempFile);
            result.setFile(tempFile);
    */
    private void manageInputMessageMark(InputStream body) throws IOException {
        if(!body.markSupported()) {
            throw new IOException("Need a markable input stream");
        }
        body.mark(Integer.MAX_VALUE);
    }

    @Override
    protected void writeInternal(FileDownloadMultipart fileDownloadMultipart, HttpOutputMessage outputMessage)
        throws HttpMessageNotWritableException {
        // non sussistono casi d'uso per l'invio di oggetti di questo tipo
        throw new UnsupportedOperationException("La serializzazione di FileDownloadMultipart non è supportata");
    }

    // Classe helper interna per adattare HttpInputMessage a ciò che FileUpload si aspetta
    private static class HttpInputMessageRequestContext implements org.apache.commons.fileupload.RequestContext {
        private final HttpInputMessage inputMessage;

        public HttpInputMessageRequestContext(HttpInputMessage inputMessage) {
            this.inputMessage = inputMessage;
        }

        @Override
        public String getCharacterEncoding() {
            return StandardCharsets.UTF_8.name();
        }

        @Override
        public String getContentType() {
            return inputMessage.getHeaders().getContentType().toString();
        }

        @Override
        public int getContentLength() {
            return (int) inputMessage.getHeaders().getContentLength();
        }

        @Override
        public java.io.InputStream getInputStream() throws IOException {
            return inputMessage.getBody();
        }
    }
}
