package it.pagopa.pn.cucumber.steps.utilitySteps;

import it.pagopa.pn.ciechecker.generator.api.CieGeneratorApi;
import it.pagopa.pn.ciechecker.generator.api.CieGeneratorApiImpl;
import it.pagopa.pn.ciechecker.model.CieValidationData;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateIo.model.CIEValidationData;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateIo.model.MRTDData;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateIo.model.NISData;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Base64;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Component
public class CieGeneratorTool {

    private CieGeneratorApi cieGenerator;

    public CieGeneratorTool() {
        this.cieGenerator = new CieGeneratorApiImpl();
    }

    public CIEValidationData generateCieValidationData(Path outputDir, String codiceFiscaleDelegante, String codiceFiscaleOwnerCIE, LocalDate expirationDate, String nonce) {
        CieValidationData libraryOutput = cieGenerator.generateCieValidationData(outputDir, codiceFiscaleDelegante, codiceFiscaleOwnerCIE, expirationDate, nonce);
        assertThat(libraryOutput).as("output non generato").isNotNull();
        return convertOutputToClassForAcceptation(libraryOutput);
    }

    private CIEValidationData convertOutputToClassForAcceptation(CieValidationData libraryOutput) {
        CIEValidationData convertedOutput = new CIEValidationData();
        NISData convertedNisData = new NISData();
        MRTDData convertedMrtdData = new MRTDData();

//        convertedOutput.setSignedNonce(Base64URL.encode(libraryOutput.getSignedNonce()).toString());
//
//        convertedNisData.setNis(Base64URL.encode(libraryOutput.getCieIas().getNis()).toString());
//        convertedNisData.setPubKey(Base64URL.encode(libraryOutput.getCieIas().getPublicKey()).toString());
//        convertedNisData.setSod(Base64URL.encode(libraryOutput.getCieIas().getSod()).toString());
//
//        convertedMrtdData.setDg1(Base64URL.encode(libraryOutput.getCieMrtd().getDg1()).toString());
//        convertedMrtdData.setDg11(Base64URL.encode(libraryOutput.getCieMrtd().getDg11()).toString());
//        convertedMrtdData.setSod(Base64URL.encode(libraryOutput.getCieMrtd().getSod()).toString());

        Base64.Encoder encoder = Base64.getUrlEncoder();

        //SIGNED NONCE
        convertedOutput.setSignedNonce(encoder.encodeToString(libraryOutput.getSignedNonce()));

        //NISData.nis
        convertedNisData.setNis(encoder.encodeToString(libraryOutput.getCieIas().getNis()));
        //NISData.sod
        convertedNisData.setSod(encoder.encodeToString(libraryOutput.getCieIas().getSod()));
        //NISData.pubKey
        convertedNisData.setPubKey(encoder.encodeToString(libraryOutput.getCieIas().getPublicKey()));

        //MRTDData.dg1
        convertedMrtdData.setDg1(encoder.encodeToString(libraryOutput.getCieMrtd().getDg1()));
        //MRTDData.dg11
        convertedMrtdData.setDg11(encoder.encodeToString(libraryOutput.getCieMrtd().getDg11()));
        //MRTDData.sod
        convertedMrtdData.setSod(encoder.encodeToString(libraryOutput.getCieMrtd().getSod()));

        convertedOutput.setNisData(convertedNisData);
        convertedOutput.setMrtdData(convertedMrtdData);

        System.out.println(convertedOutput);
        return convertedOutput;
    }
}
