package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.default_checker;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.IFileChecker;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.model.file_token.source.IFileTokenSource;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model.ArchivedFile;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSSignedData;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@RequiredArgsConstructor
public class Pm7GzFileChecker implements IFileChecker {
   private final GzFileChecker gzFileChecker;

    @Override
    public boolean hasToken(ArchivedFile encodedFile, IFileTokenSource fileTokenSource) {
        try {
            // Estrai il contenuto originale dal file .p7m
            CMSSignedData signedData = new CMSSignedData(encodedFile.getContent());
            CMSProcessable signedContent = signedData.getSignedContent();
            byte[] originalBytes = (byte[]) signedContent.getContent();

            // Convertilo in InputStream (è un .gz)
            InputStream decodedStream = new ByteArrayInputStream(originalBytes);
            ArchivedFile decodedFile = ArchivedFile.copyMetadataOf(encodedFile);
            decodedFile.setContent(decodedStream);

            return gzFileChecker.hasToken(decodedFile, fileTokenSource);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
