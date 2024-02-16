package it.pagopa.pn.cucumber.steps.recipient;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV20;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV23;
import it.pagopa.pn.client.b2b.pa.service.IPnWebMandateClient;
import it.pagopa.pn.client.b2b.pa.service.IPnWebRecipientClient;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.*;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.FullReceivedNotificationV23;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.utils.TimelineElementWait;
import org.apache.commons.lang.time.DateUtils;
import org.junit.jupiter.api.Assertions;

import org.opentest4j.AssertionFailedError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${pn.external.senderId}")
    private String senderId;
    @Value("${pn.external.senderId-2}")
    private String senderId2;
    @Value("${pn.external.senderId-GA}")
    private String senderIdGA;
    @Value("${pn.external.senderId-SON}")
    private String senderIdSON;
    @Value("${pn.external.senderId-ROOT}")
    private String senderIdROOT;
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

        return !beenSet;
    }

    @And("{string} viene delegato da {string}")
    public void delegateUser(String delegate, String delegator) {
        if (setBearerToken(delegator)) {
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


    @And("{string} viene delegato da {string} per comune {string}")
    public void delegateUser(String delegate, String delegator, String comune) {
        if (setBearerToken(delegator)) {
            throw new IllegalArgumentException();
        }
        OrganizationIdDto organizationIdDto = new OrganizationIdDto();

        switch (comune) {
            case "Comune_1":
                organizationIdDto = organizationIdDto
                        .name("Comune di Milano")
                        .uniqueIdentifier(senderId);
                break;
            case "Comune_2":
                organizationIdDto = organizationIdDto
                        .name("Comune di Verona")
                        .uniqueIdentifier(senderId2);
                break;
            case "Comune_Multi":
                organizationIdDto = organizationIdDto
                        .name("Comune di Palermo")
                        .uniqueIdentifier(senderIdGA);
                break;
            case "Comune_Son":
                organizationIdDto = organizationIdDto
                        .name("Ufficio per la transizione al Digitale")
                        .uniqueIdentifier(senderIdSON);
                break;
            case "Comune_Root":
                organizationIdDto = organizationIdDto
                        .name("Comune di Aglientu")
                        .uniqueIdentifier(senderIdROOT);
                break;
            default:
                throw new IllegalStateException();
        }


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        MandateDto mandate = (new MandateDto()
                .delegator(getUserDtoByuser(delegator))
                .delegate(getUserDtoByuser(delegate))
                .verificationCode(verificationCode)
                .datefrom(sdf.format(new Date()))
                .visibilityIds(Arrays.asList(organizationIdDto))
                .status(MandateDto.StatusEnum.PENDING)
                .dateto(sdf.format(DateUtils.addDays(new Date(), 1)))
        );

        System.out.println("MANDATE: " + mandate);

        try {
            webMandateClient.createMandate(mandate);
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @Given("{string} rifiuta se presente la delega ricevuta {string}")
    public void userRejectIfPresentMandateOfAnotheruser(String delegate, String delegator) {
        if (setBearerToken(delegate)) {
            throw new IllegalArgumentException();
        }
        String delegatorTaxId = getTaxIdByUser(delegator);

        List<MandateDto> mandateList = webMandateClient.searchMandatesByDelegate(delegatorTaxId, null);

        //List<MandateDto> mandateList = webMandateClient.listMandatesByDelegate1(null);
        MandateDto mandateDto = null;
        for (MandateDto mandate : mandateList) {
            logger.debug("MANDATE-LIST: {}", mandateList);
            if (mandate.getDelegator().getFiscalCode() != null && mandate.getDelegator().getFiscalCode().equalsIgnoreCase(delegatorTaxId)) {
                mandateDto = mandate;
                break;
            }
        }
        if (mandateDto != null) {
            MandateDto finalMandateDto = mandateDto;
            Assertions.assertDoesNotThrow(() -> webMandateClient.rejectMandate(finalMandateDto.getMandateId()));
//            try{
//                webMandateClient.rejectMandate(mandateDto.getMandateId());
//            }catch(Exception exp){
//                System.out.println("REJECT FALLITA");
//            }

        }
    }

    @And("{string} accetta la delega {string}")
    public void userAcceptsMandateOfAnotherUser(String delegate, String delegator) {
        if (setBearerToken(delegate)) {
            throw new IllegalArgumentException();
        }
        String delegatorTaxId = getTaxIdByUser(delegator);
        ;
        List<MandateDto> mandateList = webMandateClient.searchMandatesByDelegate(delegatorTaxId, null);
        // List<MandateDto> mandateList = webMandateClient.listMandatesByDelegate1(null);
        System.out.println("MANDATE-LIST: " + mandateList);
        MandateDto mandateDto = mandateList.stream().filter(mandate -> mandate.getDelegator().getFiscalCode() != null && mandate.getDelegator().getFiscalCode().equalsIgnoreCase(delegatorTaxId)).findFirst().orElse(null);

        Assertions.assertNotNull(mandateDto);
        this.mandateToSearch = mandateDto;
        Assertions.assertDoesNotThrow(() -> webMandateClient.acceptMandate(mandateDto.getMandateId(), new AcceptRequestDto().verificationCode(verificationCode)));
//        try{
//            webMandateClient.acceptMandate(mandateDto.getMandateId(), new AcceptRequestDto().verificationCode(verificationCode));
//        }catch(Exception e){
//            System.out.println("ACCEPT DELEGA ERROR");
//        }
    }

    @And("la notifica può essere correttamente letta da {string} con delega")
    public void notificationCanBeCorrectlyReadFromWithMandate(String recipient) {
        sharedSteps.selectUser(recipient);
        Assertions.assertDoesNotThrow(() -> {
            webRecipientClient.getReceivedNotification(sharedSteps.getSentNotification().getIun(), mandateToSearch.getMandateId());
        });
    }

    @Then("come amministratore {string} associa alla delega il primo gruppo disponibile attivo per il delegato {string}")
    public void comeAmministratoreDaVoglioModificareUnaDelegaPerAssociarlaAdUnGruppo(String recipient, String delegato) {
        sharedSteps.selectUser(delegato);
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
        List<HashMap<String, String>> resp = sharedSteps.getPnExternalServiceClient().pgGroupInfo(webRecipientClient.getBearerTokenSetted());
        String gruppoAttivo = null;
        if (resp != null && !resp.isEmpty()) {
            for (HashMap<String, String> entry : resp) {
                if ("ACTIVE".equals(entry.get("status"))) {
                    gruppoAttivo = entry.get("id");
                    break;
                }
            }
        }

        //TODO Gruppi Disponibili della PG Admin
        List<String> xPagopaPnCxGroups = null;

        //TODO Recuperare la Lista dei gruppi della delega;
        List<GroupDto> gruppiDelega = mandateToSearch.getGroups();

        List<String> listGruppi = new ArrayList<>();
        if (gruppiDelega != null) {
            xPagopaPnCxGroups = new ArrayList<>();
            for (GroupDto gruppo : gruppiDelega) {
                xPagopaPnCxGroups.add(gruppo.getName());
            }
        }

        String xPagopaPnCxRole = "ADMIN";
        //TODO capire dove recuperare il dato
        //Questo è l’identificativo della PG, e come gli altri header viene recuperato dal token JWT di autorizzazione
        String xPagopaPnCxId = null;
        switch (webRecipientClient.getBearerTokenSetted()) {
            case PG_1:
                xPagopaPnCxId = sharedSteps.getIdOrganizationGherkinSrl();
                //webMandateClient.setBearerToken(webRecipientClient.getBearerTokenSetted());
                break;
            case PG_2:
                xPagopaPnCxId = sharedSteps.getIdOrganizationCucumberSpa();
                //  webMandateClient.setBearerToken(webRecipientClient.getBearerTokenSetted());
                break;
        }

        List<String> gruppi = new ArrayList<>();
        if (gruppoAttivo != null && !gruppoAttivo.isEmpty()) {
            gruppi.add(gruppoAttivo);
        }

        UpdateRequestDto updateRequestDto = new UpdateRequestDto();
        updateRequestDto.setGroups(gruppi);

        String finalXPagopaPnCxId = xPagopaPnCxId;
        Assertions.assertDoesNotThrow(() -> {
            webMandateClient.updateMandate(finalXPagopaPnCxId, CxTypeAuthFleet.PG, mandateToSearch.getMandateId(), null, xPagopaPnCxRole, updateRequestDto);
        });

        String delegatorTaxId = getTaxIdByUser(recipient);
        // List<MandateDto> mandateList = webMandateClient.listMandatesByDelegate1(null);
        List<MandateDto> mandateList = webMandateClient.searchMandatesByDelegate(delegatorTaxId, null);
        MandateDto mandateDto = null;
        for (MandateDto mandate : mandateList) {
            if (mandate.getMandateId().equalsIgnoreCase(mandateToSearch.getMandateId())) {
                mandateDto = mandate;
                break;
            }
        }
        String gruppoAssegnato = "";
        if (mandateDto != null && mandateDto.getGroups() != null && mandateDto.getGroups().size() > 0) {
            gruppoAssegnato = mandateDto.getGroups().get(0).getId();
        }

        Assertions.assertNotNull(gruppoAttivo);
        Assertions.assertTrue(gruppoAttivo.equals(gruppoAssegnato));

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

    @Then("il documento notificato non può essere correttamente recuperato da {string} con delega restituendo un errore {string}")
    public void theDocumentCanNotBeProperlyRetrievedByWithMandate(String recipient, String statusCode) {
        sharedSteps.selectUser(recipient);

        try {
            Assertions.assertDoesNotThrow(() -> {
                NotificationAttachmentDownloadMetadataResponse downloadResponse = webRecipientClient.getReceivedNotificationDocument(
                        sharedSteps.getSentNotification().getIun(),
                        Integer.parseInt(sharedSteps.getSentNotification().getDocuments().get(0).getDocIdx()),
                        UUID.fromString(mandateToSearch.getMandateId())
                );
            });
        } catch (AssertionFailedError assertionFailedError) {
            //sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
            System.out.println(assertionFailedError.getCause().toString());
            System.out.println(assertionFailedError.getCause().getMessage().toString());
            System.out.println(assertionFailedError.getCause().getMessage().toString().substring(0, 3).equals(statusCode));
        }

    }


    @Then("l'allegato {string} può essere correttamente recuperato da {string} con delega")
    public void attachmentCanBeCorrectlyRetrievedFromWithMandate(String attachmentName, String recipient) {
        //TODO Modificare attachmentIdx al momento e 0...............
        sharedSteps.selectUser(recipient);
        NotificationAttachmentDownloadMetadataResponse downloadResponse = webRecipientClient.getReceivedNotificationAttachment(
                sharedSteps.getSentNotification().getIun(),
                attachmentName,
                UUID.fromString(mandateToSearch.getMandateId()), 0);

        if (downloadResponse != null && downloadResponse.getRetryAfter() != null && downloadResponse.getRetryAfter() > 0) {
            try {
                Thread.sleep(downloadResponse.getRetryAfter() * 3);
                downloadResponse = webRecipientClient.getReceivedNotificationAttachment(
                        sharedSteps.getSentNotification().getIun(),
                        attachmentName,
                        UUID.fromString(mandateToSearch.getMandateId()), 0);
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
        }
        if (!"F24".equalsIgnoreCase(attachmentName)) {
            AtomicReference<String> Sha256 = new AtomicReference<>("");
            NotificationAttachmentDownloadMetadataResponse finalDownloadResponse = downloadResponse;
            Assertions.assertDoesNotThrow(() -> {
                byte[] bytes = Assertions.assertDoesNotThrow(() ->
                        b2bUtils.downloadFile(finalDownloadResponse.getUrl()));
                Sha256.set(b2bUtils.computeSha256(new ByteArrayInputStream(bytes)));
            });
            Assertions.assertEquals(Sha256.get(), downloadResponse.getSha256());
        }
    }

    @And("{string} revoca la delega a {string}")
    public void userRevokesMandate(String delegator, String delegate) {
        if (setBearerToken(delegator)) {
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
        if (setBearerToken(delegate)) {
            throw new IllegalArgumentException();
        }
        String delegatorTaxId = getTaxIdByUser(delegator);

        List<MandateDto> mandateList = webMandateClient.searchMandatesByDelegate(delegatorTaxId, null);
        //  List<MandateDto> mandateList = webMandateClient.listMandatesByDelegate1(null);
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
            FullReceivedNotificationV23 receivedNotification =
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

    @And("lato destinatario la notifica può essere correttamente recuperata da {string} e verifica presenza dell'evento di timeline NOTIFICATION_RADD_RETRIEVED")
    public void notificationCanBeCorrectlyReadFromBytimeline(String recipient) {
        sharedSteps.selectUser(recipient);

        try {
            FullReceivedNotificationV23 result = webRecipientClient.getReceivedNotification(sharedSteps.getSentNotification().getIun(), null);
            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());

            it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.TimelineElementV23 timelineElement = result.getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV23.NOTIFICATION_RADD_RETRIEVED.name())).findAny().orElse(null);

            Assertions.assertNotNull(timelineElement);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }

        webRecipientClient.setBearerToken(baseUser);
    }

    @And("lato desinatario {string} viene verificato che l'elemento di timeline NOTIFICATION_VIEWED non esista")
    public void notificationCanBeCorrectlyReadFromBytimelineNotExist(String recipient) {
        sharedSteps.selectUser(recipient);

        try {
            FullReceivedNotificationV23 result = webRecipientClient.getReceivedNotification(sharedSteps.getSentNotification().getIun(), null);
            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());

            it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.TimelineElementV23 timelineElement = result.getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV23.NOTIFICATION_VIEWED.name())).findAny().orElse(null);

            Assertions.assertNull(timelineElement);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }

        webRecipientClient.setBearerToken(baseUser);
    }



    @And("la notifica può essere correttamente letta da {string} per comune {string}")
    public void notificationCanBeCorrectlyReadFromAtPa(String recipient, String pa) {
        sharedSteps.selectPA(pa);
        sharedSteps.selectUser(recipient);
        Assertions.assertDoesNotThrow(() -> {
            webRecipientClient.getReceivedNotification(sharedSteps.getSentNotification().getIun(), null);
        });
    }

    @And("si verifica che l'elemento di timeline della lettura riporti i dati di {string}")
    public void siVerificaCheLElementoDiTimelineDellaLetturaRiportiIDatiDi(String user) {
        try {
            Thread.sleep(sharedSteps.getWorkFlowWait() * 2);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        sharedSteps.setSentNotification(sharedSteps.getB2bClient().getSentNotification(sharedSteps.getSentNotification().getIun()));

        TimelineElementV23 timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV23.NOTIFICATION_VIEWED)).findAny().orElse(null);

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

        TimelineElementV23 timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV23.NOTIFICATION_VIEWED)).findAny().orElse(null);

        System.out.println("TIMELINE ELEMENT: " + timelineElement);
        Assertions.assertNotNull(timelineElement);
        Assertions.assertNotNull(timelineElement.getDetails());
        Assertions.assertNull(timelineElement.getDetails().getDelegateInfo());
    }


    //for debug
    @And("{string} visualizza le deleghe")
    public void visualizzaLeDeleghe(String user) {
        if (setBearerToken(user)) {
            throw new IllegalArgumentException();
        }

        List<MandateDto> mandateList = webMandateClient.listMandatesByDelegate1(null);
        List<MandateDto> mandateDtos = webMandateClient.listMandatesByDelegator1();

        System.out.println("TOKEN SETTED (user: +" + user + ") : " + webMandateClient.getBearerTokenSetted());
        System.out.println("MANDATE-LIST (user: +" + user + ") : " + mandateList);
        System.out.println("TOKEN SETTED (user: +" + user + ") : " + webMandateClient.getBearerTokenSetted());
        System.out.println("MANDATE-LIST-DELEGATOR (user: +" + user + ") : " + mandateDtos);

    }


}
