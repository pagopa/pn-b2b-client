package it.pagopa.pn.cucumber.steps.recipient;

import io.cucumber.java.Before;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV25;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV25;
import it.pagopa.pn.client.b2b.pa.service.IPnWebMandateClient;
import it.pagopa.pn.client.b2b.pa.service.IPnWebRecipientClient;
import it.pagopa.pn.client.b2b.pa.service.impl.B2BRecipientExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.B2bMandateServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnWebMandateExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.*;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.FullReceivedNotificationV24;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.NotificationSearchResponse;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.awaitility.Awaitility.await;


@Slf4j
public class RicezioneNotificheWebDelegheSteps {
    private final ApplicationContext context;
    private IPnWebMandateClient webMandateClient;
    private IPnWebRecipientClient webRecipientClient;
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
    private UserDto userDtoCustom;
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

    @Before("@useB2B")
    public void beforeMethod() {
        this.webMandateClient = context.getBean(B2bMandateServiceClientImpl.class);
        if (!(webRecipientClient instanceof B2BRecipientExternalClientImpl)) {
            this.webRecipientClient = context.getBean(B2BRecipientExternalClientImpl.class);
            sharedSteps.setWebRecipientClient(webRecipientClient);
        }
    }

    @Autowired
    public RicezioneNotificheWebDelegheSteps(ApplicationContext context, PnWebMandateExternalClientImpl webMandateClient, SharedSteps sharedSteps) {
        this.context = context;
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

        return switch (user) {
            case "Mario Cucumber" -> marioCucumberTaxID;
            case "Mario Gherkin" -> marioGherkinTaxID;
            case "GherkinSrl" -> gherkinSrltaxId;
            case "CucumberSpa" -> cucumberSpataxId;
            case "Utente errato" -> "asdasdasd";
            default -> throw new IllegalArgumentException();
        };
    }

    private UserDto getUserDtoByuser(String user) {

        return switch (user.trim().toLowerCase()) {
            case "mario cucumber" -> createUserDto("Mario Cucumber", "Mario","Cucumber", marioCucumberTaxID, null, true);
            case "mario gherkin" -> createUserDto("Mario Gherkin", "Mario", "Gherkin", marioGherkinTaxID, null, true);
            case "gherkinsrl" -> createUserDto("gherkinsrl", "gherkin", "srl", gherkinSrltaxId, "gherkinsrl", false);
            case "cucumberspa" -> createUserDto("cucumberspa", "cucumber", "spa", cucumberSpataxId, "cucumberspa", false);
            default -> throw new IllegalArgumentException();
        };
    }

    private boolean setBearerToken(String user) {
        return switch (user.trim().toLowerCase()) {
            case "mario cucumber" -> webMandateClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_1);
            case "mario gherkin" -> webMandateClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_2);
            case "gherkinsrl" -> webMandateClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
            case "cucumberspa" -> webMandateClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_2);
            default -> throw new IllegalArgumentException();
        };
    }

    @Then("si verifica che lo status code sia: {int}")
    public void checkStatusCode(int statusCode) {
        Assertions.assertEquals(statusCode, this.notificationError.getStatusCode().value());
    }

    @And("{string} viene delegato da user")
    public void delegateUserCustom(String delegator) {
        setBearerToken(delegator);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        MandateDto mandate = (new MandateDto()
                .delegator(getUserDtoByuser(delegator)))
                .delegate(userDtoCustom)
                .verificationCode(verificationCode)
                .datefrom(sdf.format(new Date()))
                .visibilityIds(new LinkedList<>())
                .status(MandateDto.StatusEnum.PENDING)
                .dateto(sdf.format(DateUtils.addDays(new Date(), 1))
                );

        System.out.println("MANDATE: " + mandate);
        try {
            webMandateClient.createMandate(mandate);
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
        }
    }

    @And("{string} viene delegato da {string}")
    public void delegateUser(String delegate, String delegator) {
        setBearerToken(delegator);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        MandateDto mandate = new MandateDto()
                .delegator(getUserDtoByuser(delegator))
                .delegate(getUserDtoByuser(delegate))
                .verificationCode(verificationCode)
                .datefrom(sdf.format(new Date()))
                .visibilityIds(new LinkedList<>())
                .status(MandateDto.StatusEnum.PENDING)
                .dateto(sdf.format(DateUtils.addDays(new Date(), 1)));

        System.out.println("MANDATE: " + mandate);
        try {
            webMandateClient.createMandate(mandate);
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
        }
    }

    @And("{string} viene delegato da {string} con data di fine delega antecedente a quella di inizio")
    public void delegateUserWithInvalidDateRange(String delegate, String delegator) {
        setBearerToken(delegator);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        MandateDto mandate = (new MandateDto()
                .delegator(getUserDtoByuser(delegator)))
                .delegate(getUserDtoByuser(delegate))
                .verificationCode(verificationCode)
                .dateto(sdf.format(new Date()))
                .visibilityIds(new LinkedList<>())
                .status(MandateDto.StatusEnum.PENDING)
                .datefrom(sdf.format(DateUtils.addDays(new Date(), 1))
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
        setBearerToken(delegator);
        OrganizationIdDto organizationIdDto = new OrganizationIdDto();

        switch (comune) {
            case "Comune_1" -> organizationIdDto
                    .name("Comune di Milano")
                    .uniqueIdentifier(senderId);
            case "Comune_2" -> organizationIdDto
                    .name("Comune di Verona")
                    .uniqueIdentifier(senderId2);
            case "Comune_Multi" -> organizationIdDto
                    .name("Comune di Palermo")
                    .uniqueIdentifier(senderIdGA);
            case "Comune_Son" -> organizationIdDto
                    .name("Ufficio per la transizione al Digitale")
                    .uniqueIdentifier(senderIdSON);
            case "Comune_Root" -> organizationIdDto
                    .name("Comune di Aglientu")
                    .uniqueIdentifier(senderIdROOT);
            default -> throw new IllegalStateException();
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

    @Given("{string} rifiuta se presente la delega ricevuta {string} da portale")
    public void userRejectMandateFromUI(String delegate, String delegator) {
        this.webMandateClient = context.getBean(PnWebMandateExternalClientImpl.class);
        userRejectIfPresentMandateOfAnotheruser(delegate, delegator);
    }


    @Given("{string} rifiuta se presente la delega ricevuta {string}")
    public void userRejectIfPresentMandateOfAnotheruser(String delegate, String delegator) {
        setBearerToken(delegate);
        String delegatorTaxId = getTaxIdByUser(delegator);

        List<MandateDto> mandateList = webMandateClient.searchMandatesByDelegate(delegatorTaxId,null);

        MandateDto mandateDto = null;
        for (MandateDto mandate : mandateList) {
            log.debug("MANDATE-LIST: {}", mandateList);
            if (Objects.requireNonNull(mandate.getDelegator()).getFiscalCode() != null && mandate.getDelegator().getFiscalCode().equalsIgnoreCase(delegatorTaxId)) {
                mandateDto = mandate;
                break;
            }
        }
        if (mandateDto != null) {
            MandateDto finalMandateDto = mandateDto;
            Assertions.assertDoesNotThrow(() -> webMandateClient.rejectMandate(finalMandateDto.getMandateId()));

        }
    }

    @And("{string} accetta la delega {string}")
    public void userAcceptsMandateOfAnotherUser(String delegate, String delegator) {
        setBearerToken(delegate);
        String delegatorTaxId = getTaxIdByUser(delegator);

        List<MandateDto> mandateList = webMandateClient.searchMandatesByDelegate(delegatorTaxId, null);
        System.out.println("MANDATE-LIST: " + mandateList);
        MandateDto mandateDto = mandateList.stream().filter(mandate -> Objects.requireNonNull(mandate.getDelegator()).getFiscalCode() != null && mandate.getDelegator().getFiscalCode().equalsIgnoreCase(delegatorTaxId)).findFirst().orElse(null);

        Assertions.assertNotNull(mandateDto);
        this.mandateToSearch = mandateDto;
        Assertions.assertDoesNotThrow(() -> webMandateClient.acceptMandate(mandateDto.getMandateId(), new AcceptRequestDto().verificationCode(verificationCode)));

    }

    @And("{string} accetta la delega {string} associando un gruppo")
    public void userAcceptsMandateWithGroups(String delegate, String delegator) {
        setBearerToken(delegate);
        String delegatorTaxId = getTaxIdByUser(delegator);

        List<MandateDto> mandateList = webMandateClient.searchMandatesByDelegate(delegatorTaxId, null);
        System.out.println("MANDATE-LIST: " + mandateList);
        MandateDto mandateDto = mandateList.stream().filter(mandate -> Objects.requireNonNull(mandate.getDelegator()).getFiscalCode() != null && mandate.getDelegator().getFiscalCode().equalsIgnoreCase(delegatorTaxId)).findFirst().orElse(null);

        Assertions.assertNotNull(mandateDto);
        this.mandateToSearch = mandateDto;

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

        AcceptRequestDto acceptRequestDto = new AcceptRequestDto().verificationCode(verificationCode).groups(List.of(gruppoAttivo));
        Assertions.assertDoesNotThrow(() -> webMandateClient.acceptMandate(mandateDto.getMandateId(), acceptRequestDto));

    }

    @And("la notifica può essere correttamente letta da {string} con delega")
    public void notificationCanBeCorrectlyReadFromWithMandate(String recipient) {
        sharedSteps.selectUser(recipient);
        Assertions.assertDoesNotThrow(() -> {
            webRecipientClient.getReceivedNotification(sharedSteps.getSentNotification().getIun(), mandateToSearch.getMandateId());
        });
    }

    @Then("come delegante {string} l'associazione a gruppi sulla delega di {string}")
    public void removeGrups(String delegator, String delegate) {
        sharedSteps.selectUser(delegator);

        UpdateRequestDto updateRequestDto = new UpdateRequestDto();
        updateRequestDto.setGroups(new LinkedList<>());

        Assertions.assertDoesNotThrow(() -> webMandateClient.updateMandate( mandateToSearch.getMandateId(), updateRequestDto));
    }

    @Then("come amministratore {string} associa alla delega il primo gruppo disponibile attivo per il delegato {string}")
    public void comeAmministratoreDaVoglioModificareUnaDelegaPerAssociarlaAdUnGruppo(String recipient, String delegato) {
        sharedSteps.selectUser(delegato);

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


        String xPagopaPnCxRole = "ADMIN";
        //TODO capire dove recuperare il dato
        //Questo è l’identificativo della PG, e come gli altri header viene recuperato dal token JWT di autorizzazione
        String xPagopaPnCxId = switch (webRecipientClient.getBearerTokenSetted()) {
            case PG_1 -> sharedSteps.getIdOrganizationGherkinSrl();
            case PG_2 -> sharedSteps.getIdOrganizationCucumberSpa();
            default -> null;
        };

        List<String> gruppi = new ArrayList<>();
        if (gruppoAttivo != null && !gruppoAttivo.isEmpty()) {
            gruppi.add(gruppoAttivo);
        }

        UpdateRequestDto updateRequestDto = new UpdateRequestDto();
        updateRequestDto.setGroups(gruppi);

        Assertions.assertDoesNotThrow(() -> webMandateClient.updateMandate(xPagopaPnCxId, CxTypeAuthFleet.PG, mandateToSearch.getMandateId(), null, xPagopaPnCxRole, updateRequestDto));

        String delegatorTaxId = getTaxIdByUser(recipient);
        List<MandateDto> mandateList = webMandateClient.searchMandatesByDelegate(delegatorTaxId, null);
        MandateDto mandateDto = null;
        for (MandateDto mandate : mandateList) {
            if (Objects.requireNonNull(mandate.getMandateId()).equalsIgnoreCase(mandateToSearch.getMandateId())) {
                mandateDto = mandate;
                break;
            }
        }
        String gruppoAssegnato = "";
        if (mandateDto != null && mandateDto.getGroups() != null && !mandateDto.getGroups().isEmpty()) {
            gruppoAssegnato = mandateDto.getGroups().get(0).getId();
        }

        Assertions.assertNotNull(gruppoAttivo);
        Assertions.assertEquals(gruppoAttivo, gruppoAssegnato);

    }

    @Then("il documento notificato può essere correttamente recuperato da {string} con delega")
    public void theDocumentCanBeProperlyRetrievedByWithMandate(String recipient) {
        sharedSteps.selectUser(recipient);
        NotificationAttachmentDownloadMetadataResponse downloadResponse = getReceivedNotificationDocument();
        verifySha256(downloadResponse);
    }

    @Then("il documento notificato non può essere correttamente recuperato da {string} con delega restituendo un errore {string}")
    public void theDocumentCanNotBeProperlyRetrievedByWithMandate(String recipient, String statusCode) {
        sharedSteps.selectUser(recipient);

        try {
            Assertions.assertDoesNotThrow(() -> {
                getReceivedNotificationDocument();
            });
        } catch (AssertionFailedError assertionFailedError) {
            System.out.println(assertionFailedError.getCause().toString());
            System.out.println(assertionFailedError.getCause().getMessage());
            System.out.println(assertionFailedError.getCause().getMessage().substring(0, 3).equals(statusCode));
        }

    }

    private NotificationAttachmentDownloadMetadataResponse getReceivedNotificationDocument() {
        return webRecipientClient.getReceivedNotificationDocument(
                sharedSteps.getSentNotification().getIun(),
                Integer.parseInt(Objects.requireNonNull(sharedSteps.getSentNotification().getDocuments().get(0).getDocIdx())),
                UUID.fromString(Objects.requireNonNull(mandateToSearch.getMandateId()))
        );
    }

    @Then("l'allegato {string} può essere correttamente recuperato da {string} con delega")
    public void attachmentCanBeCorrectlyRetrievedFromWithMandate(String attachmentName, String recipient) {
        //TODO Modificare attachmentIdx al momento e 0...............
        sharedSteps.selectUser(recipient);
        NotificationAttachmentDownloadMetadataResponse downloadResponse = webRecipientClient.getReceivedNotificationAttachment(
                sharedSteps.getSentNotification().getIun(),
                attachmentName,
                UUID.fromString(Objects.requireNonNull(mandateToSearch.getMandateId())), 0);

        if (downloadResponse != null && downloadResponse.getRetryAfter() != null && downloadResponse.getRetryAfter() > 0) {
            try {
                await().atMost(downloadResponse.getRetryAfter() * 3L, TimeUnit.MILLISECONDS);
                downloadResponse = webRecipientClient.getReceivedNotificationAttachment(
                        sharedSteps.getSentNotification().getIun(),
                        attachmentName,
                        UUID.fromString(mandateToSearch.getMandateId()), 0);
            } catch (RuntimeException exc) {
                log.error("Await error exception: {}", exc.getMessage());
                throw exc;
            }
        }
        if (!"F24".equalsIgnoreCase(attachmentName)) {
            verifySha256(downloadResponse);
        }
    }

    private void verifySha256(NotificationAttachmentDownloadMetadataResponse downloadResponse) {
        AtomicReference<String> Sha256 = new AtomicReference<>("");
        Assertions.assertDoesNotThrow(() -> {
            byte[] bytes = Assertions.assertDoesNotThrow(() ->
                    b2bUtils.downloadFile(Objects.requireNonNull(downloadResponse).getUrl()));
            Sha256.set(b2bUtils.computeSha256(new ByteArrayInputStream(bytes)));
        });
        Assertions.assertEquals(Sha256.get(), Objects.requireNonNull(downloadResponse).getSha256());
    }

    @And("{string} revoca la delega a {string}")
    public void userRevokesMandate(String delegator, String delegate) {
        setBearerToken(delegator);

        List<MandateDto> mandateList = webMandateClient.listMandatesByDelegator1();
        System.out.println("MANDATE LIST: " + mandateList);
        MandateDto mandateDto = null;
        for (MandateDto mandate : mandateList) {
            if (Objects.requireNonNull(mandate.getDelegate()).getLastName() != null &&
                    Objects.requireNonNull(mandate.getDelegate().getDisplayName()).equalsIgnoreCase(delegate)) {
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
        setBearerToken(delegate);
        String delegatorTaxId = getTaxIdByUser(delegator);

        List<MandateDto> mandateList = webMandateClient.searchMandatesByDelegate(delegatorTaxId, null);
        MandateDto mandateDto = null;
        for (MandateDto mandate : mandateList) {
            if (Objects.requireNonNull(mandate.getDelegator()).getFiscalCode() != null && mandate.getDelegator().getFiscalCode().equalsIgnoreCase(delegatorTaxId)) {
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
            it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.TimelineElementCategoryV23 timelineElementCategoryV23 =
                    it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.TimelineElementCategoryV23.NOTIFICATION_RADD_RETRIEVED;
            it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.TimelineElementV25 timelineElement = getTimelineElementV23WebRecipient(timelineElementCategoryV23);

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
            it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.TimelineElementCategoryV23 timelineElementCategoryV23 =
                    it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.TimelineElementCategoryV23.NOTIFICATION_VIEWED;
            it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.TimelineElementV25 timelineElement = getTimelineElementV23WebRecipient(timelineElementCategoryV23);

            Assertions.assertNull(timelineElement);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
        webRecipientClient.setBearerToken(baseUser);
    }

    private it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.TimelineElementV25 getTimelineElementV23WebRecipient(it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.TimelineElementCategoryV23 timelineElementCategoryV23) {

        FullReceivedNotificationV24 result = webRecipientClient.getReceivedNotification(sharedSteps.getSentNotification().getIun(), null);
        log.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());

        return result
                .getTimeline()
                .stream()
                .filter(elem -> Objects.requireNonNull(elem.getCategory())
                        .equals(timelineElementCategoryV23))
                .findAny()
                .orElse(null);
    }

    @And("la notifica può essere correttamente letta da {string} per comune {string}")
    public void notificationCanBeCorrectlyReadFromAtPa(String recipient, String pa) {
        sharedSteps.selectPA(pa);
        sharedSteps.selectUser(recipient);
        Assertions.assertDoesNotThrow(() -> {
            webRecipientClient.getReceivedNotification(sharedSteps.getSentNotification().getIun(), null);
        });
    }

    private NotificationSearchResponse notificationSearchResponse;
    @And("{string} visualizza l'elenco delle notifiche per comune {string}")
    public void notificationCanBeCorrectlyReadFromAtPa(String recipient, String pa, @Transpose RicezioneNotificheWebSteps.NotificationSearchParam searchParam) {
        sharedSteps.selectPA(pa);
        sharedSteps.selectUser(recipient);
        try{
            this.notificationSearchResponse = webRecipientClient.searchReceivedNotification(searchParam.startDate, searchParam.endDate, searchParam.mandateId /*mandateId = null by default*/,
                    searchParam.senderId, searchParam.status, searchParam.subjectRegExp,
                    searchParam.iunMatch, searchParam.size, null);
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
            this.notificationError = e;
        }
    }

    @And("{string} visualizza l'elenco delle notifiche del delegante {string} per comune {string}")
    public void notificationCanBeCorrectlyReadFromAtPa(String user, String recipient, String pa, @Transpose RicezioneNotificheWebSteps.NotificationSearchParam searchParam) {
        sharedSteps.selectPA(pa);
        sharedSteps.selectUser(user);
        try{
            this.notificationSearchResponse = webRecipientClient.searchReceivedDelegatedNotification(
                    searchParam.startDate, searchParam.endDate, getRecipientId(recipient),
                    null, searchParam.senderId, searchParam.status,
                    searchParam.iunMatch, searchParam.size, null);
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
            this.notificationError = e;
        }
    }

    //TODO: insert recipientID da selfcare (si possono recuperare dai token)
    private String getRecipientId(String recipientId){
        return switch (recipientId.toLowerCase().trim()){
            case "mario cucumber" -> "123";
            case "mario gherkin" -> "345";
            case "gherkinsrl" -> "789";
            case "cucumberspa" -> "1011";
            default -> throw new IllegalStateException("Unexpected value: " + recipientId);
        };
    }

    @And("Si verifica che il numero di notifiche restituite nella pagina sia {int}")
    public void verifyNumberOfNotification(Integer number){
        Assertions.assertEquals(notificationSearchResponse.getResultsPage().size(),number);
    }


    @And("si verifica che l'elemento di timeline della lettura riporti i dati di {string}")
    public void siVerificaCheLElementoDiTimelineDellaLetturaRiportiIDatiDi(String user) {
        TimelineElementV25 timelineElement = getTimelineElementV23();

        String userTaxId = getTaxIdByUser(user);
        System.out.println("TIMELINE ELEMENT: " + timelineElement);
        Assertions.assertNotNull(timelineElement);
        Assertions.assertNotNull(timelineElement.getDetails());
        Assertions.assertNotNull(timelineElement.getDetails().getDelegateInfo());
        Assertions.assertEquals(userTaxId, timelineElement.getDetails().getDelegateInfo().getTaxId());
    }

    @And("si verifica che l'elemento di timeline della lettura non riporti i dati del delegato")
    public void siVerificaCheLElementoDiTimelineDellaLetturaNonRiportiIDatiDi() {
        TimelineElementV25 timelineElement = getTimelineElementV23();

        System.out.println("TIMELINE ELEMENT: " + timelineElement);
        Assertions.assertNotNull(timelineElement);
        Assertions.assertNotNull(timelineElement.getDetails());
        Assertions.assertNull(timelineElement.getDetails().getDelegateInfo());
    }

    private TimelineElementV25 getTimelineElementV23() {
        try {
            await().atMost(sharedSteps.getWorkFlowWait() * 2, TimeUnit.MILLISECONDS);
        } catch (RuntimeException exception) {
            exception.printStackTrace();
        }
        sharedSteps.setSentNotification(sharedSteps.getB2bClient().getSentNotification(sharedSteps.getSentNotification().getIun()));

        return sharedSteps
                .getSentNotification()
                .getTimeline()
                .stream()
                .filter(elem -> Objects.requireNonNull(elem.getCategory())
                        .equals(TimelineElementCategoryV23.NOTIFICATION_VIEWED))
                .findAny()
                .orElse(null);
    }

    @Then("il delegato {string} visualizza le deleghe a suo carico")
    public void delegateViewsAssignedMandates(String user) {
        delegateViewMandate(user, null);
    }

    @Then("il delegato {string} visualizza le deleghe a suo carico con stato {string}")
    public void delegateViewsAssignedMandatesWithStatus(String user, String status) {
        try{
            delegateViewMandate(user, status);
        } catch (HttpStatusCodeException e) {
        this.notificationError = e;
        }
    }

    private void delegateViewMandate(String user, String statusFilter){
        setBearerToken(user);
        try {
            List<MandateDto> mandateList = webMandateClient.listMandatesByDelegate1(statusFilter);
            Assertions.assertNotNull(mandateList, "La lista mandateList è null");
            Assertions.assertFalse(mandateList.isEmpty(), "La lista mandateList è vuota");
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
        }
    }


    @Then("il delegato {string} visualizza le deleghe da parte di un delegante con CF: {string}")
    public void delegateViewsAssignedMandates(String user, String fiscalCode) {
        setBearerToken(user);
        try {
            List<MandateDto> mandateList = webMandateClient.searchMandatesByDelegate(fiscalCode, null);
            Assertions.assertNotNull(mandateList, "La lista mandateList è null");
            Assertions.assertFalse(mandateList.isEmpty(), "La lista mandateList è vuota");
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
        }
    }

    @Then("il delegato {string} visualizza le deleghe da parte di {string} in stato {string}")
    public void delegateViewsAssignedMandatesWithStatus(String delegate, String delegator, String status) {
        setBearerToken(delegate);
        try {
            List<MandateDto> mandateList;
            if (status.trim().equals("")) {
                mandateList = webMandateClient.searchMandatesByDelegate(getTaxIdByUser(delegator), null);
            } else if (delegator.trim().equals("")) {
                mandateList = webMandateClient.searchMandatesByDelegateStatusFilter("", List.of(status), null);
            } else {
                mandateList = webMandateClient.searchMandatesByDelegateStatusFilter(getTaxIdByUser(delegator), List.of(status), null);
                Assertions.assertNotNull(mandateList, "La lista mandateList è null");
                Assertions.assertFalse(mandateList.isEmpty(), "La lista mandateList è vuota");
            }
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
        }
    }


    @And("{string} visualizza le deleghe")
    public void visualizzaLeDeleghe(String user) {
        setBearerToken(user);

        List<MandateDto> mandateList = webMandateClient.listMandatesByDelegate1(null);
        List<MandateDto> mandateDtos = Assertions.assertDoesNotThrow(()-> webMandateClient.listMandatesByDelegator1());

        System.out.println("TOKEN SETTED (user: +" + user + ") : " + webMandateClient.getBearerTokenSetted());
        System.out.println("MANDATE-LIST (user: +" + user + ") : " + mandateList);
        System.out.println("TOKEN SETTED (user: +" + user + ") : " + webMandateClient.getBearerTokenSetted());
        System.out.println("MANDATE-LIST-DELEGATOR (user: +" + user + ") : " + mandateDtos);
    }

    @And("viene creata una delega con i seguenti parametri errati:")
    public void createMandateWithNotValidDate(Map<String, String> data) {
            setBearerToken(data.get("delegator"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            MandateDto mandate = new MandateDto()
                    .delegator(getUserDtoByuser(data.get("delegator")))
                    .delegate(Optional.ofNullable(data.get("delegate")).map(this::getInvalidUserDto).orElse(getUserDtoByuser("mario gherkin")))
                    .verificationCode(verificationCode)
                    .datefrom(data.getOrDefault("dateFrom", sdf.format(new Date())))
                    .visibilityIds(new LinkedList<>())
                    .status(MandateDto.StatusEnum.PENDING)
                    .dateto(data.getOrDefault("dateTo", sdf.format(DateUtils.addDays(new Date(), 1))));

            System.out.println("MANDATE: " + mandate);
            try {
                webMandateClient.createMandate(mandate);
            } catch (HttpStatusCodeException e) {
                this.notificationError = e;
            }
        }

    private UserDto getInvalidUserDto(String delegator) {
        return switch (delegator) {
            case "EMPTY_FISCAL_CODE" -> createUserDto("Mario Cucumber", "Mario", "Cucumber", null, null, true);
            case "INVALID_FISCAL_CODE" ->  createUserDto("Mario Cucumber", "Mario", "Cucumber", "AAA8090ZAC", null, true);
            case "EMPTY_FIRST_NAME" -> createUserDto("Mario Cucumber", null, "Cucumber", "CLMCST42R12D969Z", null, true);
            case "EMPTY_LAST_NAME" -> createUserDto("Mario Cucumber", "Mario", null, "CLMCST42R12D969Z", null, true);
            case "FIRST_NAME_NOT_VALID" -> createUserDto("Cristoforo Colombo", "PippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippo", "Colombo", "FRMTTR76M06B715E", null, true);
            case "LAST_NAME_NOT_VALID" -> createUserDto("Cristoforo Colombo", "Cristoforo", "PippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippo", "FRMTTR76M06B715E", null, true);
            case "EMPTY_DISPLAY_NAME" -> createUserDto(null, "Mario", "Cucumber", "CLMCST42R12D969Z", null, true);
            case "DISPLAY_NAME_NOT_VALID" -> createUserDto("PippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippo", "Mario", "Cucumber", "CLMCST42R12D969Z", null, true);
            default -> throw new IllegalStateException("Unexpected value: " + delegator);
        };
    }

    private String getDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return switch (date) {
            case "TODAY" -> sdf.format(new Date());
            case "TOMORROW" -> sdf.format(DateUtils.addDays(new Date(), 1));
            case "PAST_DATE" -> "2023-01-01";
            case "INVALID_FORMAT" -> "01-01-2023";
            case "EMPTY_DATE" -> null;
            default -> throw new IllegalStateException("Unexpected value: " + date);
        };
    }

    private UserDto createUserDto(String displayName, String firstName, String lastName, String fiscalCode, String companyName, boolean isPerson) {
    return new UserDto()
                .displayName(displayName)
                .firstName(firstName)
                .lastName(lastName)
                .fiscalCode(fiscalCode)
                .companyName(companyName)
                .person(isPerson);
    }
}