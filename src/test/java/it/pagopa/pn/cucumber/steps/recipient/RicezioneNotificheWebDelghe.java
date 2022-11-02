package it.pagopa.pn.cucumber.steps.recipient;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.testclient.IPnWebMandateClient;
import it.pagopa.pn.client.b2b.pa.testclient.IPnWebRecipientClient;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.AcceptRequestDto;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.MandateDto;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.UserDto;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.FullReceivedNotification;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import org.apache.commons.lang.time.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.ByteArrayInputStream;
import java.lang.invoke.MethodHandles;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


public class RicezioneNotificheWebDelghe {


    private final IPnWebMandateClient webMandateClient;
    private final IPnWebRecipientClient webRecipientClient;
    private final SharedSteps sharedSteps;
    private final PnPaB2bUtils b2bUtils;

    private MandateDto mandateToSearch;
    private final String verificationCode = "24411";
    private final String baseUserCf = "CLMCST42R12D969Z";
    private HttpStatusCodeException notificationError;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    public RicezioneNotificheWebDelghe(IPnWebMandateClient webMandateClient, IPnWebRecipientClient webRecipientClient, SharedSteps sharedSteps) {
        this.webMandateClient = webMandateClient;
        this.webRecipientClient = webRecipientClient;
        this.sharedSteps = sharedSteps;
        this.b2bUtils = sharedSteps.getB2bUtils();
    }

    @And("Cristoforo Colombo viene delegato da {string} {string} con cf {string}")
    public void cristoforoColomboVieneDelegatoDaConCf(String name, String surname, String cf) {
        if(!webMandateClient.setBearerToken(cf)){
            throw new IllegalArgumentException();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        MandateDto mandate = (new MandateDto()
                .delegator(new UserDto()
                        .displayName(name+" "+surname)
                        .firstName(name)
                        .lastName(surname)
                        .fiscalCode(cf)
                        .companyName("")
                        .person(true))
                .delegate(new UserDto()
                        .displayName("Cristoforo Colombo")
                        .firstName("Cristoforo")
                        .lastName("Colombo")
                        .fiscalCode("CLMCST42R12D969Z")
                        .companyName("")
                        .person(true))
                .verificationCode(verificationCode)
                .datefrom(sdf.format(new Date()))
                .dateto(sdf.format(DateUtils.addDays(new Date(),1)))
        );
        try {
            webMandateClient.createMandate(mandate);
        }catch (HttpStatusCodeException e) {
            this.notificationError = e;
        }
    }

    @Given("Cristoforo colombo rifiuta se presente la delega ricevuta da {string} {string} con cf {string}")
    public void vieneRifiutateSePresenteLaDelegaRicevutaDaConCf(String name, String surname, String cf) {
        webMandateClient.setBearerToken("CLMCST42R12D969Z");
        List<MandateDto> mandateList = webMandateClient.listMandatesByDelegate1(null);
        MandateDto mandateDto = null;
        for(MandateDto mandate: mandateList){
            if(mandate.getDelegator().getFiscalCode() != null && mandate.getDelegator().getFiscalCode().equalsIgnoreCase(cf)){
                mandateDto = mandate;
                break;
            }
        }
        if(mandateDto != null){
            webMandateClient.rejectMandate(mandateDto.getMandateId());
        }
    }

    @And("Cristoforo Colombo accetta la delega da {string}")
    public void cristoforoColomboAccettaLaDelegaDa(String cf) {
        webMandateClient.setBearerToken("CLMCST42R12D969Z");
        List<MandateDto> mandateList = webMandateClient.listMandatesByDelegate1(null);
        MandateDto mandateDto = null;
        for(MandateDto mandate: mandateList){
            if(mandate.getDelegator().getFiscalCode() != null && mandate.getDelegator().getFiscalCode().equalsIgnoreCase(cf)){
                mandateDto = mandate;
                break;
            }
        }

        Assertions.assertNotNull(mandateDto);
        this.mandateToSearch = mandateDto;
        webMandateClient.acceptMandate(mandateDto.getMandateId(),new AcceptRequestDto().verificationCode(verificationCode));

    }

    @Then("la notifica può essere correttamente letta dal delegato")
    public void laNotificaPuoEssereCorrettamenteRecuperataDalDelegato() {
        Assertions.assertDoesNotThrow(() -> {
            webRecipientClient.getReceivedNotification(sharedSteps.getSentNotification().getIun(), mandateToSearch.getMandateId());
        });
    }

    @Then("il documento notificato può essere correttamente recuperato dal delegato")
    public void ilDocumentoNotificatoPuoEssereCorrettamenteRecuperatoDalDelegato() {
        NotificationAttachmentDownloadMetadataResponse downloadResponse = webRecipientClient.getReceivedNotificationDocument(
                sharedSteps.getSentNotification().getIun(),
                Integer.parseInt(sharedSteps.getSentNotification().getDocuments().get(0).getDocIdx()),
                mandateToSearch.getMandateId()
        );
        AtomicReference<String> Sha256 = new AtomicReference<>("");
        Assertions.assertDoesNotThrow(() -> {
            byte[] bytes = Assertions.assertDoesNotThrow(() ->
                    b2bUtils.downloadFile(downloadResponse.getUrl()));
            Sha256.set(b2bUtils.computeSha256(new ByteArrayInputStream(bytes)));
        });
        Assertions.assertEquals(Sha256.get(),downloadResponse.getSha256());
    }

    @Then("l'allegato {string} può essere correttamente recuperato dal delegato")
    public void lAllegatoPuoEssereCorrettamenteRecuperatoDalDelegato(String attachmentName) {
        NotificationAttachmentDownloadMetadataResponse downloadResponse = webRecipientClient.getReceivedNotificationAttachment(
                sharedSteps.getSentNotification().getIun(),
                attachmentName,
                mandateToSearch.getMandateId());
        AtomicReference<String> Sha256 = new AtomicReference<>("");
        Assertions.assertDoesNotThrow(() -> {
            byte[] bytes = Assertions.assertDoesNotThrow(() ->
                    b2bUtils.downloadFile(downloadResponse.getUrl()));
            Sha256.set(b2bUtils.computeSha256(new ByteArrayInputStream(bytes)));
        });
        Assertions.assertEquals(Sha256.get(),downloadResponse.getSha256());
    }

    @And("{string} {string} con cf {string} revoca la delega a Cristoforo Colombo")
    public void conCfRevocaLaDelegaACristoforoColombo(String name, String surname, String cf) {
        webMandateClient.setBearerToken(cf);
        List<MandateDto> mandateList = webMandateClient.listMandatesByDelegator1();
        MandateDto mandateDto = null;
        for(MandateDto mandate: mandateList){
            if(mandate.getDelegate().getLastName() != null && mandate.getDelegate().getLastName().equalsIgnoreCase("Colombo")){
                mandateDto = mandate;
                break;
            }
        }

        Assertions.assertNotNull(mandateDto);
        this.mandateToSearch = mandateDto;
        webMandateClient.revokeMandate(mandateDto.getMandateId());

    }

    @And("Cristoforo Colombo rifiuta la delega da {string}")
    public void cristoforoColomboRifiutaLaDelegaDa(String cf) {
        webMandateClient.setBearerToken("CLMCST42R12D969Z");
        List<MandateDto> mandateList = webMandateClient.listMandatesByDelegate1(null);
        MandateDto mandateDto = null;
        for(MandateDto mandate: mandateList){
            if(mandate.getDelegator().getFiscalCode() != null && mandate.getDelegator().getFiscalCode().equalsIgnoreCase(cf)){
                mandateDto = mandate;
                break;
            }
        }

        Assertions.assertNotNull(mandateDto);
        this.mandateToSearch = mandateDto;
        webMandateClient.rejectMandate(mandateDto.getMandateId());

    }


    @Then("si tenta la lettura della notifica da parte del delegato che produce un errore con status code {string}")
    public void siTentaIlRecuperoDellaNotificaDaParteDelDelegatoCheProduceUnErroreConStatusCode(String statusCode) {
        HttpClientErrorException httpClientErrorException = null;
        try {
            FullReceivedNotification receivedNotification =
                    webRecipientClient.getReceivedNotification(sharedSteps.getSentNotification().getIun(), mandateToSearch.getMandateId());
        } catch (HttpClientErrorException e) {
            httpClientErrorException = e;
        }
        Assertions.assertTrue((httpClientErrorException != null) &&
                (httpClientErrorException.getStatusCode().toString().substring(0,3).equals(statusCode)));
    }

    @Given("Cristoforo Colombo viene delegato da {string} {string} con cf {string} con delega in scadenza")
    public void cristoforoColomboVieneDelegatoDaConCfConDelegaInScadenza(String name, String surname, String cf) {
        if(!webMandateClient.setBearerToken(cf)){
            throw new IllegalArgumentException();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        MandateDto mandate = (new MandateDto()
                .delegator(new UserDto()
                        .displayName(name+" "+surname)
                        .firstName(name)
                        .lastName(surname)
                        .fiscalCode(cf)
                        .companyName("")
                        .person(true))
                .delegate(new UserDto()
                        .displayName("Cristoforo Colombo")
                        .firstName("Cristoforo")
                        .lastName("Colombo")
                        .fiscalCode("CLMCST42R12D969Z")
                        .companyName("")
                        .person(true))
                .verificationCode(verificationCode)
                .datefrom(sdf.format(new Date()))
                .dateto(sdf.format(DateUtils.addMinutes(new Date(),2)))
        );
        webMandateClient.createMandate(mandate);

    }

    @And("si attende lo scadere della delega")
    public void siAttendeLoScadereDellaDelega() {
        try {
            Thread.sleep( 120 * 1000L);
        } catch (InterruptedException exc) {
            throw new RuntimeException( exc );
        }
    }

    @Then("l'operazione di delega ha prodotto un errore con status code {string}")
    public void lOperazioneDiDelegaHaProdottoUnErroreConStatusCode(String statusCode) {
        Assertions.assertTrue((notificationError != null) &&
                (notificationError.getStatusCode().toString().substring(0,3).equals(statusCode)));
    }

    @And("la notifica può essere correttamente letta dal destinatario {string}")
    public void laNotificaPuòEssereCorrettamenteRecuperataDalDestinatario(String cf) {
        boolean setted = webRecipientClient.setBearerToken(cf);
        if(!setted)throw new IllegalArgumentException();
        Assertions.assertDoesNotThrow(() -> {
            webRecipientClient.getReceivedNotification(sharedSteps.getSentNotification().getIun(), null);
        });
        if(!webRecipientClient.setBearerToken(baseUserCf))throw new IllegalArgumentException();
    }

}
