package it.pagopa.pn.cucumber.steps.recipient;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElement;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory;
import it.pagopa.pn.client.b2b.pa.testclient.IPnWebMandateClient;
import it.pagopa.pn.client.b2b.pa.testclient.IPnWebRecipientClient;
import it.pagopa.pn.client.b2b.pa.testclient.SettableBearerToken;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.*;
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
import java.util.*;
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

    private final String gherkinSrltaxId;
    private final String cucumberSpataxId;

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    public RicezioneNotificheWebDelegheSteps(IPnWebMandateClient webMandateClient, SharedSteps sharedSteps) {
        this.webMandateClient = webMandateClient;
        this.sharedSteps = sharedSteps;
        this.webRecipientClient = sharedSteps.getWebRecipientClient();
        this.b2bUtils = sharedSteps.getB2bUtils();

        this.marioCucumberTaxID = sharedSteps.getMarioCucumberTaxID();
        this.marioGherkinTaxID = sharedSteps.getMarioGherkinTaxID();

        this.gherkinSrltaxId = sharedSteps.getGherkinSrltaxId();
        this.cucumberSpataxId = sharedSteps.getCucumberSpataxId();
    }

    private String getTaxIdByUser(String user) {
        String userTaxId;
        switch (user) {
            case "Mario Cucumber":
                userTaxId = marioCucumberTaxID;
                break;
            case "Mario Gherkin":
                userTaxId = marioGherkinTaxID;
                break;
            case "GherkinSrl":
                userTaxId = gherkinSrltaxId;
                break;
            case "CucumberSpa":
                userTaxId = cucumberSpataxId;
                break;
            default:
                throw new IllegalArgumentException();
        }

        return userTaxId;
    }

    private UserDto getUserDtoByuser(String user) {
        UserDto userDto;
        switch (user.trim().toLowerCase()) {
            case "mario cucumber":
                userDto = new UserDto()
                        .displayName("Mario Cucumber")
                        .firstName("Mario")
                        .lastName("Cucumber")
                        .fiscalCode(marioCucumberTaxID)
                        .person(true);
                break;
            case "mario gherkin":
                userDto = new UserDto()
                        .displayName("Mario Gherkin")
                        .firstName("Mario")
                        .lastName("Gherkin")
                        .fiscalCode(marioGherkinTaxID)
                        .person(true);
                break;
            case "gherkinsrl":
                userDto = new UserDto()
                        .displayName("gherkinsrl")
                        .firstName("gherkin")
                        .lastName("srl")
                        .fiscalCode(gherkinSrltaxId)
                        .companyName("gherkinsrl")
                        .person(false);
                break;
            case "cucumberspa":
                userDto = new UserDto()
                        .displayName("cucumberspa")
                        .firstName("cucumber")
                        .lastName("spa")
                        .fiscalCode(cucumberSpataxId)
                        .companyName("cucumberspa")
                        .person(false);
                break;
            default:
                throw new IllegalArgumentException();
        }

        return userDto;
    }

    private boolean setBearerToken(String user) {
        boolean beenSet = false;
        switch (user.trim().toLowerCase()) {
            case "mario cucumber":
                beenSet = webMandateClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_1);
                break;
            case "mario gherkin":
                beenSet = webMandateClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_2);
                break;
            case "gherkinsrl":
                beenSet = webMandateClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
                break;
            case "cucumberspa":
                beenSet = webMandateClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_2);
                break;
        }

        return beenSet;
    }

    @And("{string} viene delegato da {string}")
    public void delegateUser(String delegate, String delegator) {
        if (!setBearerToken(delegator)) {
            throw new IllegalArgumentException();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        MandateDto mandate = (new MandateDto()
                .delegator(getUserDtoByuser(delegator))
                .delegate(getUserDtoByuser(delegate))
                .verificationCode(verificationCode)
                .datefrom(sdf.format(new Date()))
                .visibilityIds(new LinkedList<>())
                .status(MandateDto.StatusEnum.PENDING)
                .dateto(sdf.format(DateUtils.addDays(new Date(), 1)))
        );

        System.out.println("MANDATE: " + mandate);
        try {
            webMandateClient.createMandate(mandate);
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
        }
    }

    @Given("{string} rifiuta se presente la delega ricevuta {string}")
    public void userRejectIfPresentMandateOfAnotheruser(String delegate, String delegator) {
        if (!setBearerToken(delegate)) {
            throw new IllegalArgumentException();
        }
        String delegatorTaxId = getTaxIdByUser(delegator);

        List<MandateDto> mandateList = webMandateClient.listMandatesByDelegate1(null);
        MandateDto mandateDto = null;
        for (MandateDto mandate : mandateList) {
            if (mandate.getDelegator().getFiscalCode() != null && mandate.getDelegator().getFiscalCode().equalsIgnoreCase(delegatorTaxId)) {
                mandateDto = mandate;
                break;
            }
        }
        if (mandateDto != null) {
            webMandateClient.rejectMandate(mandateDto.getMandateId());
        }
    }

    @And("{string} accetta la delega {string}")
    public void userAcceptsMandateOfAnotherUser(String delegate, String delegator) {
        if (!setBearerToken(delegate)) {
            throw new IllegalArgumentException();
        }
        String delegatorTaxId = getTaxIdByUser(delegator);

        List<MandateDto> mandateList = webMandateClient.listMandatesByDelegate1(null);
        System.out.println("MANDATE-LIST: "+mandateList);
        MandateDto mandateDto = null;
        for (MandateDto mandate : mandateList) {
            if (mandate.getDelegator().getFiscalCode() != null && mandate.getDelegator().getFiscalCode().equalsIgnoreCase(delegatorTaxId)) {
                mandateDto = mandate;
                break;
            }
        }

        Assertions.assertNotNull(mandateDto);
        this.mandateToSearch = mandateDto;
        webMandateClient.acceptMandate(mandateDto.getMandateId(), new AcceptRequestDto().verificationCode(verificationCode));
    }

    @And("la notifica può essere correttamente letta da {string} con delega")
    public void notificationCanBeCorrectlyReadFromWithMandate(String recipient) {
        sharedSteps.selectUser(recipient);
        Assertions.assertDoesNotThrow(() -> {
            webRecipientClient.getReceivedNotification(sharedSteps.getSentNotification().getIun(), mandateToSearch.getMandateId());
        });
    }

    @And("la notifica può essere correttamente modificata da {string} con delega")
    public void notificationCanBeCorrectlyModificataFromWithMandate(String recipient) {
        sharedSteps.selectUser(recipient);
      //  Assertions.assertDoesNotThrow(() -> {
           // webRecipientClient.getReceivedNotification(sharedSteps.getSentNotification().getIun(), mandateToSearch.getMandateId());

              //      * @param xPagopaPnCxId Customer/Receiver Identifier (required)
              //      * @param xPagopaPnCxType Customer/Receiver Type (required)
           //         * @param mandateId  (required)
            //        * @param xPagopaPnCxGroups Customer Groups (optional)
             //       * @param xPagopaPnCxRole Ruolo (estratto da token di Self Care) (optional)
             //       * @param updateRequestDto  (optional)
           // url --location --request PATCH 'http://localhost:8080/mandate/api/v1/mandate/1748533a-7020-4d47-9e4d-c95f558d8845/update' \
           // --header 'x-pagopa-pn-cx-id: PG-1748533a-7020-4d47-9e4d-c95f558d8845' \
           // --header 'x-pagopa-pn-cx-type: PG' \
          //  --header 'x-pagopa-pn-cx-groups;' \
          //  --header 'x-pagopa-pn-cx-role: ADMIN' \
         //   --header 'Content-Type: application/json' \
         //   --data '{
           // "groups":[
          //  "test1",
           //         "test4"
    //]
       // }'
      //  });

        //TODO Recuperare i gruppi della PG come Admin....
        List<HashMap<String, String>> resp =  sharedSteps.getPnExternalServiceClient().pgGroupInfo(webRecipientClient.getBearerTokenSetted());

        //TODO Recuperare la Lista dei gruppi della delega;
            List<GroupDto> gruppiDelega = mandateToSearch.getGroups();

            List<String> xPagopaPnCxGroups = new ArrayList<>();
            if (gruppiDelega!= null ){
                for (GroupDto gruppo : gruppiDelega) {
                    xPagopaPnCxGroups.add(gruppo.getName());
                }
            }
            //xPagopaPnCxGroups.add("test1");
            //xPagopaPnCxGroups.add("test2");

            String xPagopaPnCxRole="ADMIN";
            //TODO capire dove recuperare il dato
            //Questo è l’identificativo della PG, e come gli altri header viene recuperato dal token JWT di autorizzazione
        String xPagopaPnCxId =null;
        switch (webRecipientClient.getBearerTokenSetted()) {
            case PG_1:
                xPagopaPnCxId = sharedSteps.getIdOrganizationGherkinSrl();
            case PG_2:
                xPagopaPnCxId = sharedSteps.getIdOrganizationCucumberSpa();
        }
        String finalXPagopaPnCxId = xPagopaPnCxId;
        Assertions.assertDoesNotThrow(() -> {
        webMandateClient.updateMandate(finalXPagopaPnCxId,  CxTypeAuthFleet.PG,  mandateToSearch.getMandateId(),  xPagopaPnCxGroups,  xPagopaPnCxRole,  null);
        });

    }

    @Then("il documento notificato può essere correttamente recuperato da {string} con delega")
    public void theDocumentCanBeProperlyRetrievedByWithMandate(String recipient) {
        sharedSteps.selectUser(recipient);
        NotificationAttachmentDownloadMetadataResponse downloadResponse = webRecipientClient.getReceivedNotificationDocument(
                sharedSteps.getSentNotification().getIun(),
                Integer.parseInt(sharedSteps.getSentNotification().getDocuments().get(0).getDocIdx()),
                UUID.fromString(mandateToSearch.getMandateId())
        );
        AtomicReference<String> Sha256 = new AtomicReference<>("");
        Assertions.assertDoesNotThrow(() -> {
            byte[] bytes = Assertions.assertDoesNotThrow(() ->
                    b2bUtils.downloadFile(downloadResponse.getUrl()));
            Sha256.set(b2bUtils.computeSha256(new ByteArrayInputStream(bytes)));
        });
        Assertions.assertEquals(Sha256.get(), downloadResponse.getSha256());
    }

    @Then("l'allegato {string} può essere correttamente recuperato da {string} con delega")
    public void attachmentCanBeCorrectlyRetrievedFromWithMandate(String attachmentName, String recipient) {
        sharedSteps.selectUser(recipient);
        NotificationAttachmentDownloadMetadataResponse downloadResponse = webRecipientClient.getReceivedNotificationAttachment(
                sharedSteps.getSentNotification().getIun(),
                attachmentName,
                UUID.fromString(mandateToSearch.getMandateId()));
        AtomicReference<String> Sha256 = new AtomicReference<>("");
        Assertions.assertDoesNotThrow(() -> {
            byte[] bytes = Assertions.assertDoesNotThrow(() ->
                    b2bUtils.downloadFile(downloadResponse.getUrl()));
            Sha256.set(b2bUtils.computeSha256(new ByteArrayInputStream(bytes)));
        });
        Assertions.assertEquals(Sha256.get(), downloadResponse.getSha256());
    }

    @And("{string} revoca la delega a {string}")
    public void userRevokesMandate(String delegator, String delegate) {
        if (!setBearerToken(delegator)) {
            throw new IllegalArgumentException();
        }

        List<MandateDto> mandateList = webMandateClient.listMandatesByDelegator1();
        System.out.println("MANDATE LIST: " + mandateList);
        MandateDto mandateDto = null;
        for (MandateDto mandate : mandateList) {
            if (mandate.getDelegate().getLastName() != null &&
                    mandate.getDelegate().getDisplayName().toLowerCase().equalsIgnoreCase(delegate.toLowerCase())) {
                mandateDto = mandate;
                break;
            }
        }

        Assertions.assertNotNull(mandateDto);
        this.mandateToSearch = mandateDto;
        webMandateClient.revokeMandate(mandateDto.getMandateId());
    }

    @And("{string} rifiuta la delega ricevuta da {string}")
    public void delegateRefusesMandateReceivedFromDelegator(String delegate, String delegator) {
        if (!setBearerToken(delegate)) {
            throw new IllegalArgumentException();
        }
        String delegatorTaxId = getTaxIdByUser(delegator);
        List<MandateDto> mandateList = webMandateClient.listMandatesByDelegate1(null);
        MandateDto mandateDto = null;
        for (MandateDto mandate : mandateList) {
            if (mandate.getDelegator().getFiscalCode() != null && mandate.getDelegator().getFiscalCode().equalsIgnoreCase(delegatorTaxId)) {
                mandateDto = mandate;
                break;
            }
        }

        Assertions.assertNotNull(mandateDto);
        this.mandateToSearch = mandateDto;
        webMandateClient.rejectMandate(mandateDto.getMandateId());
    }

    @Then("si tenta la lettura della notifica da parte del delegato {string} che produce un errore con status code {string}")
    public void readNotificationWithError(String recipient, String statusCode) {
        sharedSteps.selectUser(recipient);
        HttpClientErrorException httpClientErrorException = null;
        try {
            FullReceivedNotification receivedNotification =
                    webRecipientClient.getReceivedNotification(sharedSteps.getSentNotification().getIun(), mandateToSearch.getMandateId());
        } catch (HttpClientErrorException e) {
            httpClientErrorException = e;
        }
        Assertions.assertTrue((httpClientErrorException != null) &&
                (httpClientErrorException.getStatusCode().toString().substring(0, 3).equals(statusCode)));
    }


    @Then("l'operazione di delega ha prodotto un errore con status code {string}")
    public void operationProducedAnErrorWithStatusCode(String statusCode) {
        Assertions.assertTrue((notificationError != null) &&
                (notificationError.getStatusCode().toString().substring(0, 3).equals(statusCode)));
    }

    @And("la notifica può essere correttamente letta da {string}")
    public void notificationCanBeCorrectlyReadFrom(String recipient) {
        sharedSteps.selectUser(recipient);
        Assertions.assertDoesNotThrow(() -> {
            webRecipientClient.getReceivedNotification(sharedSteps.getSentNotification().getIun(), null);
        });
        webRecipientClient.setBearerToken(baseUser);
    }

    @And("si verifica che l'elemento di timeline della lettura riporti i dati di {string}")
    public void siVerificaCheLElementoDiTimelineDellaLetturaRiportiIDatiDi(String user) {
        try {
            Thread.sleep(sharedSteps.getWorkFlowWait() * 2);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        sharedSteps.setSentNotification(sharedSteps.getB2bClient().getSentNotification(sharedSteps.getSentNotification().getIun()));

        TimelineElement timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategory.NOTIFICATION_VIEWED)).findAny().orElse(null);

        String userTaxId = getTaxIdByUser(user);
        System.out.println("TIMELINE ELEMENT: " + timelineElement);
        Assertions.assertNotNull(timelineElement);
        Assertions.assertNotNull(timelineElement.getDetails());
        Assertions.assertNotNull(timelineElement.getDetails().getDelegateInfo());
        Assertions.assertEquals(userTaxId, timelineElement.getDetails().getDelegateInfo().getTaxId());
    }

    @And("si verifica che l'elemento di timeline della lettura non riporti i dati del delegato")
    public void siVerificaCheLElementoDiTimelineDellaLetturaNonRiportiIDatiDi() {
        try {
            Thread.sleep(sharedSteps.getWorkFlowWait() * 2);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        sharedSteps.setSentNotification(sharedSteps.getB2bClient().getSentNotification(sharedSteps.getSentNotification().getIun()));

        TimelineElement timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategory.NOTIFICATION_VIEWED)).findAny().orElse(null);

        System.out.println("TIMELINE ELEMENT: " + timelineElement);
        Assertions.assertNotNull(timelineElement);
        Assertions.assertNotNull(timelineElement.getDetails());
        Assertions.assertNull(timelineElement.getDetails().getDelegateInfo());
    }



    //for debug
    @And("{string} visualizza le deleghe")
    public void visualizzaLeDeleghe(String user) {
        if (!setBearerToken(user)) {
            throw new IllegalArgumentException();
        }

        List<MandateDto> mandateList = webMandateClient.listMandatesByDelegate1(null);
        List<MandateDto> mandateDtos = webMandateClient.listMandatesByDelegator1();

        System.out.println("TOKEN SETTED (user: +"+user+") : "+webMandateClient.getBearerTokenSetted());
        System.out.println("MANDATE-LIST (user: +"+user+") : "+mandateList);
        System.out.println("TOKEN SETTED (user: +"+user+") : "+webMandateClient.getBearerTokenSetted());
        System.out.println("MANDATE-LIST-DELEGATOR (user: +"+user+") : "+mandateDtos);

    }
}
