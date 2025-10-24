package it.pagopa.pn.cucumber.steps.utilitySteps;

import it.pagopa.pn.ciechecker.generator.api.CieGeneratorApi;
import it.pagopa.pn.ciechecker.generator.api.CieGeneratorApiImpl;
import it.pagopa.pn.ciechecker.model.CieValidationData;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateIo.model.CIEValidationData;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateIo.model.MRTDData;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateIo.model.NISData;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
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

    public CIEValidationData generateCieValidationData(Path outputDir, String codiceFiscale, LocalDate expirationDate, String nonce) {
        CieValidationData libraryOutput = cieGenerator.generateCieValidationData(outputDir, codiceFiscale, expirationDate, nonce);
        assertThat(libraryOutput).as("output non generato").isNotNull();
        return convertOutputToClassForAcceptation(libraryOutput);
    }

    private CIEValidationData convertOutputToClassForAcceptation(CieValidationData libraryOutput) {
        byte[] rawData;
        CIEValidationData convertedOutput = new CIEValidationData();

        //SIGNED NONCE
        rawData = Base64.getDecoder().decode(libraryOutput.getSignedNonce());
        convertedOutput.setSignedNonce(new String(rawData, StandardCharsets.UTF_8));

        NISData convertedNisData = new NISData();
        //NISData.nis
        rawData = Base64.getDecoder().decode(libraryOutput.getCieIas().getNis());
        convertedNisData.setNis(new String(rawData, StandardCharsets.UTF_8));
        //NISData.sod
        rawData = Base64.getDecoder().decode(libraryOutput.getCieIas().getSod());
        convertedNisData.setSod(new String(rawData, StandardCharsets.UTF_8));
        //NISData.pubKey
        rawData = Base64.getDecoder().decode(libraryOutput.getCieIas().getPublicKey());
        convertedNisData.setPubKey(new String(rawData, StandardCharsets.UTF_8));
        convertedOutput.setNisData(convertedNisData);

        MRTDData convertedMrtdData = new MRTDData();
        //MRTDData.dg1
        rawData = Base64.getDecoder().decode(libraryOutput.getCieMrtd().getDg1());
        convertedMrtdData.setDg1(new String(rawData, StandardCharsets.UTF_8));
        //MRTDData.dg11
        rawData = Base64.getDecoder().decode(libraryOutput.getCieMrtd().getDg11());
        convertedMrtdData.setDg11(new String(rawData, StandardCharsets.UTF_8));
        //MRTDData.sod
        rawData = Base64.getDecoder().decode(libraryOutput.getCieMrtd().getSod());
        convertedMrtdData.setSod(new String(rawData, StandardCharsets.UTF_8));
        convertedOutput.setMrtdData(convertedMrtdData);

//        convertedOutput.setSignedNonce(libraryOutput.getSignedNonce().toString());
//        convertedNisData.setNis(libraryOutput.getCieIas().getNis().toString());
//        convertedNisData.setSod(libraryOutput.getCieIas().getSod().toString());
//        convertedNisData.setPubKey(libraryOutput.getCieIas().getPublicKey().toString());
//        convertedMrtdData.setDg1(libraryOutput.getCieMrtd().getDg1().toString());
//        convertedMrtdData.setDg11(libraryOutput.getCieMrtd().getDg11().toString());
//        convertedMrtdData.setSod(libraryOutput.getCieMrtd().getSod().toString());

        return convertedOutput;
    }
}
