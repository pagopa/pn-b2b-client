package it.pagopa.pn.cucumber.steps.recipient;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateIo.model.MandateCreationRequest;
import it.pagopa.pn.client.b2b.pa.service.IPnMandateAppIoClient;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Slf4j
@Data
public class DelegheTemporaneeSteps {

    private final SharedSteps sharedSteps;

    private final IPnMandateAppIoClient mandateAppIoClient;

    private String qrCode;

    @Autowired
    public DelegheTemporaneeSteps(SharedSteps sharedSteps, IPnMandateAppIoClient mandateAppIoClient) {
        this.sharedSteps = sharedSteps;
        this.mandateAppIoClient = mandateAppIoClient;
    }

    @Given("viene generato il QR Code {isValidQrCode} relativo alla notifica appena creata")
    public void vieneGeneratoIlCodiceQRPerLaNotificaCreata(boolean isValidQrCode) {
        qrCode = isValidQrCode ? sharedSteps.vieneRichiestoIlCodiceQRPerLoIUN(sharedSteps.getNotificationIun(), 0) :
                sharedSteps.vieneRichiestoIlCodiceQRPerLoIUN(sharedSteps.getNotificationIun(), 0) + "MALF";
    }

    //RSSMRA95A58H501Z --------> UNICO CF VALIDO IN DEV PER CIE


    //TODO delegante potrebbe essere superfluo
    @When("{string} viene temporaneamente delegato da {string} passando {string}")
    public void creaDelegaTemporanea(String delegate, String delegator, String inputParamsType) {

        MandateCreationRequest mandateCreationRequest = new MandateCreationRequest();
        mandateCreationRequest.setAarQrCodeValue(getQrCodeCreationRequest(inputParamsType));
        String taxId = "RSSMRA95A58H501Z";//TODO, cambiare in base a nome delegato
        mandateAppIoClient.createIOMandate(
                taxId, null, null, null, null,
                null, null, null, null, null,
                mandateCreationRequest);
    }

    private String getQrCodeCreationRequest(String inputParamsType) {
        assertThat(qrCode).as("TODO MESSAGE").isNotNull();
        return switch (inputParamsType.toUpperCase()) {
            case "QRCODE NON VALIDO" -> qrCode + "MALF";
            default -> qrCode;
        };
    }
//        setBearerToken(delegator);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        MandateDto mandate = new MandateDto()
//                .delegator(getUserDtoByUser(delegator))
//                .delegate(getUserDtoByUser(delegate))
//                .verificationCode(verificationCode)
//                .datefrom(sdf.format(new Date()))
//                .visibilityIds(new LinkedList<>())
//                .status(MandateDto.StatusEnum.PENDING)
//                .dateto(sdf.format(DateUtils.addDays(new Date(), 1)));
//
//        System.out.println("MANDATE: " + mandate);
//        try {
//            webMandateClient.createMandate(mandate);
//        } catch (HttpStatusCodeException e) {
//            this.notificationError = e;
//        }
//    }
//
//    private boolean setBearerToken(String user) {
//        return switch (user.trim()) {
//            case MARIO_CUCUMBER -> mandateIoClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_1);
//            case MARIO_GHERKIN -> mandateIoClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_2);
//            case GHERKIN_SRL -> mandateIoClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
//            case CUCUMBER_SPA -> mandateIoClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_2);
//            default -> throw new IllegalArgumentException();
//        };
//    }
}
