package it.pagopa.common.util;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.util.Store;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class PdfSignatureVerifier {

    private PdfSignatureVerifier() {
    }

    public static boolean hasSignature(byte[] pdfContent) {
        try (PDDocument document = Loader.loadPDF(pdfContent)) {
            return !findSignatureContents(document).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public static List<String> getSignatureNames(byte[] pdfContent) {
        try (PDDocument document = Loader.loadPDF(pdfContent)) {
            List<String> names = new ArrayList<>();
            for (byte[] ignored : findSignatureContents(document)) {
                names.add("PDFSignature");
            }
            return names;
        } catch (Exception e) {
            return List.of();
        }
    }

    public static boolean verifyPdfSignature(byte[] pdfContent) {
        try (PDDocument document = Loader.loadPDF(pdfContent)) {
            List<byte[]> signatureContents = findSignatureContents(document);
            if (signatureContents.isEmpty()) {
                return false;
            }

            for (byte[] cmsBytes : signatureContents) {
                if (!verifyCmsSignature(cmsBytes)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static List<byte[]> findSignatureContents(PDDocument document) {
        List<byte[]> result = new ArrayList<>();
        visit(document.getDocumentCatalog().getCOSObject(), result);
        return result;
    }

    private static void visit(COSBase base, List<byte[]> result) {
        if (base == null) {
            return;
        }

        if (base instanceof COSDictionary dict) {
            if (COSName.TYPE.equals(dict.getItem(COSName.TYPE)) && COSName.SIG.equals(dict.getItem(COSName.TYPE))) {
                COSBase contents = dict.getItem(COSName.CONTENTS);
                if (contents instanceof COSString cosString) {
                    result.add(cosString.getBytes());
                }
            }

            for (COSName key : dict.keySet()) {
                visit(dict.getItem(key), result);
            }
        } else if (base instanceof COSArray array) {
            for (COSBase element : array) {
                visit(element, result);
            }
        }
    }

    private static boolean verifyCmsSignature(byte[] cmsBytes) throws Exception {
        if (cmsBytes == null || cmsBytes.length == 0) {
            return false;
        }

        CMSSignedData signedData = new CMSSignedData(cmsBytes);
        SignerInformationStore signerInfoStore = signedData.getSignerInfos();

        if (signerInfoStore.getSigners().isEmpty()) {
            return false;
        }

        Store<X509CertificateHolder> certStore = signedData.getCertificates();

        for (Object signerObj : signerInfoStore.getSigners()) {
            SignerInformation signerInfo = (SignerInformation) signerObj;
            Collection<X509CertificateHolder> matches = certStore.getMatches(signerInfo.getSID());

            if (matches.isEmpty()) {
                return false;
            }

            X509CertificateHolder certHolder = matches.iterator().next();
            X509Certificate cert = new JcaX509CertificateConverter()
                    .setProvider("BC")
                    .getCertificate(certHolder);

            boolean valid = signerInfo.verify(new JcaSimpleSignerInfoVerifierBuilder()
                    .setProvider("BC")
                    .build(cert));

            if (!valid) {
                return false;
            }
        }

        return true;
    }
}