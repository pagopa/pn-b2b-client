package it.pagopa.interop.config.springconfig.springconfig;

import it.pagopa.interop.M2MVersionsMapper;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.FileDownloadMultipart;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

/* DEV. NOTE 25 02 2026: Questo componente, come molti altri inerenti API v3, si basa sull'assunto che
* il DTO di api v3 - in questo caso FileDownloadMultipart - sia identico alla sua controparte v2 (al momento vero). */
public class FileDownloadMultipartConverterV3 extends AbstractHttpMessageConverter<FileDownloadMultipart> {
    private final FileDownloadMultipartConverter converterV2;
    private final M2MVersionsMapper mapperV2;

    public FileDownloadMultipartConverterV3(
            FileDownloadMultipartConverter converterV2,
            M2MVersionsMapper mapperV2) {
        super(MediaType.MULTIPART_FORM_DATA);
        this.converterV2 = converterV2;
        this.mapperV2 = mapperV2;
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return FileDownloadMultipart.class.isAssignableFrom(clazz);
    }

    @Override
    protected FileDownloadMultipart readInternal(Class<? extends FileDownloadMultipart> clazz, HttpInputMessage inputMessage)
        throws HttpMessageNotReadableException {
        it.pagopa.interop.generated.openapi.clients.m2mGateway.model.FileDownloadMultipart multipartV2 = converterV2.readInternal(it.pagopa.interop.generated.openapi.clients.m2mGateway.model.FileDownloadMultipart.class, inputMessage);
        return mapperV2.mapToV3(multipartV2);
    }

    @Override
    protected void writeInternal(FileDownloadMultipart fileDownloadMultipart, HttpOutputMessage outputMessage)
        throws HttpMessageNotWritableException {
        converterV2.writeInternal(mapperV2.mapToV2(fileDownloadMultipart), outputMessage);
    }
}
