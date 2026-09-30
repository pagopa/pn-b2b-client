package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.processor.process;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.ContentType;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.processor.IFileProcessor;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.processor.model.ProcessedFile;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSSignedData;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class P7mFileProcessor implements IFileProcessor {

    @Override
    public boolean supports(ContentType contentType) {
        return contentType == ContentType.P7M;
    }

    @Override
    public ProcessedFile process(ProcessedFile input) {
        try {
            CMSSignedData signedData = new CMSSignedData(input.content());
            CMSProcessable signedContent = signedData.getSignedContent();

            byte[] gzBytes = (byte[]) signedContent.getContent();
            InputStream gzStream = new ByteArrayInputStream(gzBytes);

            // Convenzione di dominio: P7M → ZIP
            return new ProcessedFile(gzStream, ContentType.ZIP);

        } catch (Exception e) {
            throw new RuntimeException("Errore durante l'estrazione del contenuto P7M", e);
        }
    }
}
