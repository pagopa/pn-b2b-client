package it.pagopa.pn.cucumber.steps.recipient;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.testclient.IPnWebMandateClient;
import it.pagopa.pn.client.b2b.pa.testclient.IPnWebRecipientClient;
import it.pagopa.pn.client.b2b.pa.testclient.SettableBearerToken;
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



public class RicezioneNotificheWebDelegheSteps {

    private final IPnWebMandateClient webMandateClient;
    private final IPnWebRecipientClient webRecipientClient;
    private final SharedSteps sharedSteps;
    private final PnPaB2bUtils b2bUtils;

    private MandateDto mandateToSearch;
    private final SettableBearerToken.BearerTokenType baseUser = SettableBearerToken.BearerTokenType.USER_2;
    private final String verificationCode = "24411";
    private HttpStatusCodeException notificationError;

    private final String marioCucumberTaxID;
    private final String marioGherkinTaxID;

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    public RicezioneNotificheWebDelegheSteps(IPnWebMandateClient webMandateClient, SharedSteps sharedSteps) {
        this.webMandateClient = webMandateClient;
        this.sharedSteps = sharedSteps;
        this.webRecipientClient = sharedSteps.getWebRecipientClient();
        this.b2bUtils = sharedSteps.getB2bUtils();

        this.marioCucumberTaxID = sharedSteps.getMarioCucumberTaxID();
        this.marioGherkinTaxID = sharedSteps.getMarioGherkinTaxID();
    }


    @And("Mario Gherkin viene delegato da Mario Cucumber")
    public void marioGherkinIsDelegatedByMarioCucumber() {
        if(!webMandateClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_1)){
            throw new IllegalArgumentException();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        MandateDto mandate = (new MandateDto()
                .delegator(new UserDto()
                        .displayName("Mario Cucumber")
                        .firstName("Mario")
                        .lastName("Cucumber")
                        .fiscalCode(marioCucumberTaxID)
                        .companyName("")
                        .person(true))
                .delegate(new UserDto()
                        .displayName("Mario Gherkin")
                        .firstName("Mario")
                        .lastName("Gherkin")
                        .fiscalCode(marioGherkinTaxID)
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

    @Given("Mario Gherkin viene delegato da Mario Gherkin")
    public void marioGherkinIsDelegatedByMarioGherkin() {
        if(!webMandateClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_2)){
            throw new IllegalArgumentException();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        MandateDto mandate = (new MandateDto()
                .delegator(new UserDto()
                        .displayName("Mario Gherkin")
                        .firstName("Mario")
                        .lastName("Gherkin")
                        .fiscalCode(marioGherkinTaxID)
                        .companyName("")
                        .person(true))
                .delegate(new UserDto()
                        .displayName("Mario Gherkin")
                        .firstName("Mario")
                        .lastName("Gherkin")
                        .fiscalCode(marioGherkinTaxID)
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

    @Given("Mario Gherkin rifiuta se presente la delega ricevuta Mario Cucumber")
    public void marioGherkinRejectIfPresentMandateOfMarioCucumber() {
        webMandateClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_2);
        List<MandateDto> mandateList = webMandateClient.listMandatesByDelegate1(null);
        MandateDto mandateDto = null;
        for(MandateDto mandate: mandateList){
            if(mandate.getDelegator().getFiscalCode() != null && mandate.getDelegator().getFiscalCode().equalsIgnoreCase(marioCucumberTaxID)){
                mandateDto = mandate;
                break;
            }
        }
        if(mandateDto != null){
            webMandateClient.rejectMandate(mandateDto.getMandateId());
        }
    }

    @And("Mario Gherkin accetta la delega Mario Cucumber")
    public void marioGherkinAcceptsMandateOfMarioCucumber() {
        webMandateClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_2);
        List<MandateDto> mandateList = webMandateClient.listMandatesByDelegate1(null);
        MandateDto mandateDto = null;
        for(MandateDto mandate: mandateList){
            if(mandate.getDelegator().getFiscalCode() != null && mandate.getDelegator().getFiscalCode().equalsIgnoreCase(marioCucumberTaxID)){
                mandateDto = mandate;
                break;
            }
        }

        Assertions.assertNotNull(mandateDto);
        this.mandateToSearch = mandateDto;
        webMandateClient.acceptMandate(mandateDto.getMandateId(),new AcceptRequestDto().verificationCode(verificationCode));

    }

    @And("la notifica può essere correttamente letta da {string} con delega")
    public void notificationCanBeCorrectlyReadFromWithMandate(String recipient) {
        sharedSteps.selectUser(recipient);
        Assertions.assertDoesNotThrow(() -> {
            webRecipientClient.getReceivedNotification(sharedSteps.getSentNotification().getIun(), mandateToSearch.getMandateId());
        });
    }

    @Then("il documento notificato può essere correttamente recuperato da {string} con delega")
    public void theDocumentCanBeProperlyRetrievedByWithMandate(String recipient) {
        sharedSteps.selectUser(recipient);
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

    @Then("l'allegato {string} può essere correttamente recuperato da {string} con delega")
    public void attachmentCanBeCorrectlyRetrievedFromWithMandate(String attachmentName,String recipient) {
        sharedSteps.selectUser(recipient);
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

    @And("Mario Cucumber revoca la delega a Mario Gherkin")
    public void marioCucumberRevokesMandate() {
        webMandateClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_1);
        List<MandateDto> mandateList = webMandateClient.listMandatesByDelegator1();
        MandateDto mandateDto = null;
        for(MandateDto mandate: mandateList){
            if(mandate.getDelegate().getLastName() != null && mandate.getDelegate().getLastName().equalsIgnoreCase("Gherkin")){
                mandateDto = mandate;
                break;
            }
        }

        Assertions.assertNotNull(mandateDto);
        this.mandateToSearch = mandateDto;
        webMandateClient.revokeMandate(mandateDto.getMandateId());

    }

    @And("Mario Gherkin rifiuta la delega ricevuta da Mario Cucumber")
    public void marioGherkinRefusesMandateReceivedFrom() {
        webMandateClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_2);
        List<MandateDto> mandateList = webMandateClient.listMandatesByDelegate1(null);
        MandateDto mandateDto = null;
        for(MandateDto mandate: mandateList){
            if(mandate.getDelegator().getFiscalCode() != null && mandate.getDelegator().getFiscalCode().equalsIgnoreCase(marioCucumberTaxID)){
                mandateDto = mandate;
                break;
            }
        }

        Assertions.assertNotNull(mandateDto);
        this.mandateToSearch = mandateDto;
        webMandateClient.rejectMandate(mandateDto.getMandateId());

    }


    @Then("si tenta la lettura della notifica da parte del delegato {string} che produce un errore con status code {string}")
    public void readNotificationWithError(String recipient,String statusCode) {
        sharedSteps.selectUser(recipient);
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

    @Given("Mario Gherkin viene delegato Mario Cucumber con delega in scadenza")
    public void marioGherkinDelegateMarioCucumberWithExpiringMandate() {
        if(!webMandateClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_1)){
            throw new IllegalArgumentException();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        MandateDto mandate = (new MandateDto()
                .delegator(new UserDto()
                        .displayName("Mario Cucumber")
                        .firstName("Mario")
                        .lastName("Cucumber")
                        .fiscalCode(marioCucumberTaxID)
                        .companyName("")
                        .person(true))
                .delegate(new UserDto()
                        .displayName("Mario Gherkin")
                        .firstName("Mario")
                        .lastName("Gherkin")
                        .fiscalCode(marioGherkinTaxID)
                        .companyName("")
                        .person(true))
                .verificationCode(verificationCode)
                .datefrom(sdf.format(new Date()))
                .dateto(sdf.format(DateUtils.addMinutes(new Date(),2)))
        );
        webMandateClient.createMandate(mandate);

    }

    @And("si attende lo scadere della delega")
    public void awaitExpirationOfMandate() {
        try {
            Thread.sleep( 120 * 1000L);
        } catch (InterruptedException exc) {
            throw new RuntimeException( exc );
        }
    }

    @Then("l'operazione di delega ha prodotto un errore con status code {string}")
    public void operationProducedAnErrorWithStatusCode(String statusCode) {
        Assertions.assertTrue((notificationError != null) &&
                (notificationError.getStatusCode().toString().substring(0,3).equals(statusCode)));
    }

    @And("la notifica può essere correttamente letta da {string}")
    public void notificationCanBeCorrectlyReadFrom(String recipient) {
        sharedSteps.selectUser(recipient);
        Assertions.assertDoesNotThrow(() -> {
            webRecipientClient.getReceivedNotification(sharedSteps.getSentNotification().getIun(), null);
        });
        webRecipientClient.setBearerToken(baseUser);
    }





}
