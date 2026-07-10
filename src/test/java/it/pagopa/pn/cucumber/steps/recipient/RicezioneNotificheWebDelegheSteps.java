package it.pagopa.pn.cucumber.steps.recipient;

import io.cucumber.java.Before;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.FullNotificationSearchResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.LegalNotificationSearchResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.TimelineElementV28;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.NotificationStatusV26;
import it.pagopa.pn.client.b2b.pa.domain.NotificationSearchParam;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV29;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV28;
import it.pagopa.pn.client.b2b.pa.service.IPnWebMandateClient;
import it.pagopa.pn.client.b2b.pa.service.IPnWebRecipientClient;
import it.pagopa.pn.client.b2b.pa.service.impl.B2BRecipientExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.B2bMandateServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnWebMandateExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnWebRecipientExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import it.pagopa.pn.client.b2b.pa.wrapper.BundleFullReceivedNotification;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.AcceptRequestDto;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.CxTypeAuthFleet;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.MandateDto;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.OrganizationIdDto;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.UpdateRequestDto;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.UserDto;
import it.pagopa.pn.cucumber.steps.SendSharedContext;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.utils.notificationsearch.NotificationSearchCriteriaMapper;
import it.pagopa.pn.cucumber.utils.notificationsearch.NotificationSearchRowAssertions;
import it.pagopa.pn.cucumber.utils.token.TokenResolver;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static it.pagopa.pn.client.b2b.pa.domain.Costanti.COMUNE_1;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.COMUNE_2;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.COMUNE_MULTI;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.COMUNE_ROOT;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.COMUNE_SON;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.CUCUMBER_SPA;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.GHERKIN_SRL;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.MARIO_CUCUMBER;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.MARIO_GHERKIN;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.NOTIFICATION_VIEWED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;


@Slf4j
public class RicezioneNotificheWebDelegheSteps {
    private final ApplicationContext context;
    @Getter
    private IPnWebMandateClient webMandateClient;
    private IPnWebRecipientClient webRecipientClient;
    private final SharedSteps sharedSteps;
    private final SendSharedContext sendSharedContext;
    private final NotificationSearchCriteriaMapper notificationSearchCriteriaMapper;
    @Getter
    private MandateDto mandateToSearch;
    private final SettableBearerToken.BearerTokenType baseUser = SettableBearerToken.BearerTokenType.USER_2;
    private final String verificationCode = "24411";
    private HttpStatusCodeException notificationError;

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

    private boolean isUseB2BFlag;

    @Before("@useB2B")
    public void beforeMethod() {
        this.webMandateClient = context.getBean(B2bMandateServiceClientImpl.class);
        if (!(webRecipientClient instanceof B2BRecipientExternalClientImpl)) {
            this.webRecipientClient = context.getBean(B2BRecipientExternalClientImpl.class);
            sharedSteps.setWebRecipientClient(webRecipientClient);
        }
        isUseB2BFlag = true;
    }

    @Autowired
    public RicezioneNotificheWebDelegheSteps(ApplicationContext context, PnWebMandateExternalClientImpl webMandateClient, SharedSteps sharedSteps,
                                             NotificationSearchCriteriaMapper notificationSearchCriteriaMapper, SendSharedContext sendSharedContext) {
        this.context = context;
        this.webMandateClient = webMandateClient;
        this.sharedSteps = sharedSteps;
        this.webRecipientClient = sharedSteps.getWebRecipientClient();
        this.notificationSearchCriteriaMapper = notificationSearchCriteriaMapper;
        this.sendSharedContext = sendSharedContext;
    }

    /**
     * Imposta la delega usata come riferimento per le ricerche successive, propagandone l'id a
     * {@link SharedSteps} affinché sia risolvibile tramite il token dinamico {@code :mandateId}.
     */
    public void setMandateToSearch(MandateDto mandateToSearch) {
        this.mandateToSearch = mandateToSearch;
        sharedSteps.setMandateId(mandateToSearch != null ? mandateToSearch.getMandateId() : null);
    }

    private String getTaxIdByUser(String user) {
        return switch (user) {
            case MARIO_CUCUMBER -> sharedSteps.getDestinatarioRegistry().DESTINATARIO_MARIO_CUCUMBER.getTaxId();
            case MARIO_GHERKIN -> sharedSteps.getDestinatarioRegistry().DESTINATARIO_MARIO_GHERKIN.getTaxId();
            case GHERKIN_SRL -> sharedSteps.getDestinatarioRegistry().DESTINATARIO_GHERKIN_SRL.getTaxId();
            case CUCUMBER_SPA -> sharedSteps.getDestinatarioRegistry().DESTINATARIO_CUCUMBER_SPA.getTaxId();
            case "Utente errato" -> "asdasdasd";
            default -> throw new IllegalArgumentException();
        };
    }

    private UserDto getUserDtoByUser(String user) {
        return switch (user.trim().toLowerCase()) {
            case "mario cucumber" ->
                    createUserDto(MARIO_CUCUMBER, "Mario", "Cucumber", sharedSteps.getDestinatarioRegistry().DESTINATARIO_MARIO_CUCUMBER.getTaxId(), null, true);
            case "mario gherkin" -> createUserDto(MARIO_GHERKIN, "Mario", "Gherkin", sharedSteps.getDestinatarioRegistry().DESTINATARIO_MARIO_GHERKIN.getTaxId(), null, true);
            case "gherkinsrl" -> createUserDto(GHERKIN_SRL, "gherkin", "srl", sharedSteps.getDestinatarioRegistry().DESTINATARIO_GHERKIN_SRL.getTaxId(), GHERKIN_SRL, false);
            case "cucumberspa" ->
                    createUserDto(CUCUMBER_SPA, "cucumber", "spa", sharedSteps.getDestinatarioRegistry().DESTINATARIO_CUCUMBER_SPA.getTaxId(), CUCUMBER_SPA, false);
            default -> throw new IllegalArgumentException();
        };
    }

    public boolean setBearerToken(String user) {
        return switch (user.trim()) {
            case MARIO_CUCUMBER -> {
                setRequiredAPI(false);
                yield webMandateClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_1);
            }
            case MARIO_GHERKIN -> {
                setRequiredAPI(false);
                yield webMandateClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_2);
            }
            case GHERKIN_SRL -> {
                setRequiredAPI(isUseB2BFlag);
                yield webMandateClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
            }
            case CUCUMBER_SPA -> {
                setRequiredAPI(isUseB2BFlag);
                yield webMandateClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_2);
            }
            default -> throw new IllegalArgumentException();
        };
    }

    private void setRequiredAPI(boolean isB2BRequired) {
        IPnWebMandateClient mandateClient;
        IPnWebRecipientClient recipientClient;
        if (isB2BRequired) {
            mandateClient = context.getBean(B2bMandateServiceClientImpl.class);
            recipientClient = context.getBean(B2BRecipientExternalClientImpl.class);
        } else {
            mandateClient = context.getBean(PnWebMandateExternalClientImpl.class);
            recipientClient = context.getBean(PnWebRecipientExternalClientImpl.class);
        }
        this.webMandateClient = mandateClient;
        this.webRecipientClient = recipientClient;
        sharedSteps.setWebRecipientClient(webRecipientClient);
    }

    @Then("si verifica che lo status code sia: {int}")
    public void checkStatusCode(int statusCode) {
        Assertions.assertEquals(statusCode, this.notificationError.getStatusCode().value());
    }

    @Then("si verifica che venga ritornato un errore di tipo {string}")
    public void checkErrorType(String errorType) {
        Assertions.assertTrue(this.notificationError.getStatusCode().getReasonPhrase().toUpperCase().contains(errorType.toUpperCase()));
    }

    @And("{string} viene delegato da {string}")
    public void delegateUser(String delegate, String delegator) {
        setBearerToken(delegator);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        MandateDto mandate = new MandateDto()
                .delegator(getUserDtoByUser(delegator))
                .delegate(getUserDtoByUser(delegate))
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

    @And("{string} viene delegato da {string} per comune {string}")
    public void delegateUser(String delegate, String delegator, String comune) {
        setBearerToken(delegator);
        OrganizationIdDto organizationIdDto = new OrganizationIdDto();

        switch (comune) {
            case COMUNE_1 -> organizationIdDto
                    .name("Comune di Milano")
                    .uniqueIdentifier(senderId);
            case COMUNE_2 -> organizationIdDto
                    .name("Comune di Verona")
                    .uniqueIdentifier(senderId2);
            case COMUNE_MULTI -> organizationIdDto
                    .name("Comune di Palermo")
                    .uniqueIdentifier(senderIdGA);
            case COMUNE_SON -> organizationIdDto
                    .name("Ufficio per la transizione al Digitale")
                    .uniqueIdentifier(senderIdSON);
            case COMUNE_ROOT -> organizationIdDto
                    .name("Comune di Aglientu")
                    .uniqueIdentifier(senderIdROOT);
            default -> throw new IllegalStateException();
        }


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        MandateDto mandate = (new MandateDto()
                .delegator(getUserDtoByUser(delegator))
                .delegate(getUserDtoByUser(delegate))
                .verificationCode(verificationCode)
                .datefrom(sdf.format(new Date()))
                .visibilityIds(List.of(organizationIdDto))
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
    public void userRejectIfPresentMandateOfAnotherUser(String delegate, String delegator) {
        setBearerToken(delegate);
        String delegatorTaxId = getTaxIdByUser(delegator);

        List<MandateDto> mandateList = webMandateClient.searchMandatesByDelegate(delegatorTaxId, null);

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
        setMandateToSearch(mandateDto);
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
        setMandateToSearch(mandateDto);

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

    @And("la notifica {canBe} essere correttamente letta da {string} con delega")
    public void notificationCanBeCorrectlyReadFromWithMandate(boolean canBeRetrieved, String recipient) {
        sharedSteps.selectUser(recipient);
        BundleFullReceivedNotification receivedNotification = null;
        try {
            receivedNotification = webRecipientClient.getFullReceivedNotification(sharedSteps.getNotificationIun(), mandateToSearch.getMandateId());
        } catch (HttpClientErrorException e) {
            notificationError = e;
        }
        if (canBeRetrieved) {
            assertThat(receivedNotification).as("La notifica recuperata non dev'essere null").isNotNull();
        } else {
            assertThat(receivedNotification).as("La notifica recuperata dev'essere null").isNull();
            assertThat(notificationError).as("L'errore prodotto non dev'essere null").isNotNull();
        }
    }

    @Then("come delegante {string} l'associazione a gruppi sulla delega di {string}")
    public void removeGroups(String delegator, String delegate) {
        sharedSteps.selectUser(delegator);

        UpdateRequestDto updateRequestDto = new UpdateRequestDto();
        updateRequestDto.setGroups(new LinkedList<>());

        Assertions.assertDoesNotThrow(() -> webMandateClient.updateMandate(mandateToSearch.getMandateId(), updateRequestDto));
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
        verifyRetrievedDocument(downloadResponse);
    }

    @Then("il documento notificato non può essere correttamente recuperato da {string} con delega restituendo un errore {string}")
    public void theDocumentCanNotBeProperlyRetrievedByWithMandate(String recipient, String statusCode) {
        sharedSteps.selectUser(recipient);
        try {
            Assertions.assertDoesNotThrow(this::getReceivedNotificationDocument);
        } catch (AssertionFailedError assertionFailedError) {
            System.out.println(assertionFailedError.getCause().toString());
            System.out.println(assertionFailedError.getCause().getMessage());
            System.out.println(assertionFailedError.getCause().getMessage().substring(0, 3).equals(statusCode));
        }

    }

    private NotificationAttachmentDownloadMetadataResponse getReceivedNotificationDocument() {
        FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
        return webRecipientClient.getReceivedNotificationDocument(
                fullSentNotification.getIun(),
                Integer.parseInt(Objects.requireNonNull(fullSentNotification.getDocuments().get(0).getDocIdx())),
                UUID.fromString(Objects.requireNonNull(mandateToSearch.getMandateId()))
        );
    }

    @Then("l'allegato {string} {canBe} essere correttamente recuperato da {string} con delega")
    public void attachmentCanBeCorrectlyRetrievedFromWithMandate(String attachmentName, boolean canBeRetrieved, String recipient) {
        //TODO Modificare attachmentIdx al momento e 0...............
        sharedSteps.selectUser(recipient);
        NotificationAttachmentDownloadMetadataResponse downloadResponse = null;
        try {
            downloadResponse = webRecipientClient.getReceivedNotificationAttachment(
                    sharedSteps.getNotificationIun(),
                    attachmentName,
                    UUID.fromString(Objects.requireNonNull(mandateToSearch.getMandateId())), 0);

            if (downloadResponse != null && downloadResponse.getRetryAfter() != null && downloadResponse.getRetryAfter() > 0) {
                try {
                    await().atMost(downloadResponse.getRetryAfter() * 3L, TimeUnit.MILLISECONDS);
                    downloadResponse = webRecipientClient.getReceivedNotificationAttachment(
                            sharedSteps.getNotificationIun(),
                            attachmentName,
                            UUID.fromString(mandateToSearch.getMandateId()), 0);
                    verifyRetrievedDocument(downloadResponse);
                } catch (RuntimeException exc) {
                    log.error("Await error exception: {}", exc.getMessage());
                    throw exc;
                }
            }
//        if (!"F24".equalsIgnoreCase(attachmentName)) {
//            verifySha256(downloadResponse);
//        }
        } catch (HttpClientErrorException e) {
            notificationError = e;
        }
        if (canBeRetrieved) {
            assertThat(downloadResponse).as("L'allegato recuperato non dev'essere null").isNotNull();
        } else {
            assertThat(downloadResponse).as("L'allegato recuperato dev'essere null").isNull();
            assertThat(notificationError).as("L'errore prodotto non dev'essere null").isNotNull();
        }
    }

    private void verifyRetrievedDocument(NotificationAttachmentDownloadMetadataResponse downloadResponse) {
        Assertions.assertNotNull(downloadResponse.getFilename());
        Assertions.assertNotNull(downloadResponse.getContentLength());
        Assertions.assertNotNull(downloadResponse.getUrl());
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
        setMandateToSearch(mandateDto);
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
        setMandateToSearch(mandateDto);
        webMandateClient.rejectMandate(mandateDto.getMandateId());
    }

    @Then("si tenta la lettura della notifica da parte del delegato {string} che produce un errore con status code {string}")
    public void readNotificationWithError(String recipient, String statusCode) {
        sharedSteps.selectUser(recipient);
        HttpClientErrorException httpClientErrorException = null;
        try {
            webRecipientClient.getFullReceivedNotification(sharedSteps.getNotificationIun(), mandateToSearch.getMandateId());
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
            webRecipientClient.getFullReceivedNotification(sharedSteps.getNotificationIun(), null);
        });
        webRecipientClient.setBearerToken(baseUser);
    }

    @And("lato destinatario la notifica può essere correttamente recuperata da {string} e verifica presenza dell'evento di timeline {string}")
    public void notificationCanBeCorrectlyReadFromTimeline(String recipient, String timelineEventString) {
        sharedSteps.selectUser(recipient);
        try {
            TimelineElementCategoryV28 timelineElementCategory = TimelineElementCategoryV28.valueOf(timelineEventString);
            TimelineElementV28 timelineElement = getTimelineElementWebRecipient(timelineElementCategory);
            assertThat(timelineElement).as("Il timeline element non dev'essere null").isNotNull();
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Valore non valido per TimelineElementCategoryV28: " + timelineEventString, e);
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
        webRecipientClient.setBearerToken(baseUser);
    }

    @And("lato destinatario {string} viene verificato che l'elemento di timeline NOTIFICATION_VIEWED non esista")
    public void notificationCanBeCorrectlyReadFromByTimelineNotExist(String recipient) {
        // Seleziona l'utente destinatario
        sharedSteps.selectUser(recipient);

        // Definisce la categoria dell'elemento di timeline da verificare
        TimelineElementCategoryV28 category = TimelineElementCategoryV28.NOTIFICATION_VIEWED;

        // Recupera l'elemento di timeline per il destinatario
        TimelineElementV28 timelineElement = getTimelineElementWebRecipient(category);

        // Verifica che l'elemento di timeline non esista
        if (timelineElement != null) {
            log.error("Elemento di timeline '{}' trovato per il destinatario '{}', ma non avrebbe dovuto esistere. Dettagli: {}",
                    category, recipient, timelineElement);
        }

        try {
            // Asserzione che fallisce se l'elemento di timeline esiste
            Assertions.assertNull(timelineElement,
                    String.format("Elemento di timeline '%s' trovato per il destinatario '%s', ma non avrebbe dovuto esistere.",
                            category, recipient));
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
        // Reimposta il token di autenticazione per il client del destinatario
        webRecipientClient.setBearerToken(baseUser);
    }

    private TimelineElementV28 getTimelineElementWebRecipient(TimelineElementCategoryV28 timelineElementCategory) {
        BundleFullReceivedNotification result = webRecipientClient.getFullReceivedNotification(sharedSteps.getNotificationIun(), null);
        log.info("NOTIFICATION_TIMELINE: " + result.getTimeline());
        return result
                .getTimeline()
                .stream()
                .filter(elem -> Objects.requireNonNull(elem.getCategory().getValue()).equals(timelineElementCategory.getValue()))
                .findAny()
                .orElse(null);
    }

    @And("la notifica può essere correttamente letta da {string} per comune {string}")
    public void notificationCanBeCorrectlyReadFromAtPa(String recipient, String paName) {
        sharedSteps.setPA(paName);
        sharedSteps.selectUser(recipient);
        Assertions.assertDoesNotThrow(() -> webRecipientClient.getFullReceivedNotification(sharedSteps.getNotificationIun(), null));
    }

    private Integer notificationSearchResponseCount;
    private FullNotificationSearchResponse notificationSearchResponseFull;
    private LegalNotificationSearchResponse notificationSearchResponseLegal;

    @And("{string} visualizza l'elenco delle notifiche per comune {string}")
    public void notificationCanBeCorrectlyReadFromAtPa(String recipient, String paName, @Transpose NotificationSearchParam searchParam) {
        sharedSteps.setPA(paName);
        sharedSteps.selectUser(recipient);
        setRequiredAPI(isUseB2BFlag);
        NotificationStatusV26 notificationStatus = searchParam.status != null ? NotificationStatusV26.valueOf(searchParam.status) : null;
        try {
            this.notificationSearchResponseFull = webRecipientClient.searchReceivedNotification(
                    sharedSteps.getDestinatarioRegistry().destinatario(recipient), searchParam);
            this.notificationSearchResponseCount = notificationSearchResponseFull.getResultsPage().size();
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
            this.notificationError = e;
        }
    }

    @And("{string} visualizza l'elenco delle notifiche del delegante {string} per comune {string}")
    public void notificationCanBeCorrectlyReadFromAtPa(String user, String recipient, String paName, @Transpose NotificationSearchParam searchParam) {
        sharedSteps.setPA(paName);
        sharedSteps.selectUser(user);
        setRequiredAPI(isUseB2BFlag);
        NotificationStatusV26 notificationStatus = searchParam.status != null ? NotificationStatusV26.valueOf(searchParam.status) : null;
        try {
            this.notificationSearchResponseLegal = webRecipientClient.searchReceivedDelegatedNotification(
                    sharedSteps.getDestinatarioRegistry().destinatario(recipient), searchParam);
            this.notificationSearchResponseCount = notificationSearchResponseLegal.getResultsPage().size();
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
            this.notificationError = e;
        }
    }

    //TODO: insert recipientID da selfcare (si possono recuperare dai token)
    private String getRecipientId(String recipientId) {
        return switch (recipientId.toLowerCase().trim()) {
            case "mario cucumber" -> "123";
            case "mario gherkin" -> "345";
            case "gherkinsrl" -> "789";
            case "cucumberspa" -> "1011";
            default -> throw new IllegalStateException("Unexpected value: " + recipientId);
        };
    }

    @And("Si verifica che il numero di notifiche restituite nella pagina sia {int}")
    public void verifyNumberOfNotification(Integer number) {
        Assertions.assertEquals(notificationSearchResponseCount, number);
    }


    @And("si verifica che l'elemento di timeline della lettura riporti i dati di {string}")
    public void siVerificaCheLElementoDiTimelineDellaLetturaRiportiIDatiDi(String user) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV28 timelineElement = getTimelineElement();

        String userTaxId = getTaxIdByUser(user);
        log.info("TIMELINE ELEMENT : {}", timelineElement);
        assertThat(timelineElement).as("Il timeline element non dev'essere null").isNotNull();
        Assertions.assertNotNull(timelineElement.getDetails());
        Assertions.assertNotNull(timelineElement.getDetails().getDelegateInfo());
        Assertions.assertEquals(userTaxId, timelineElement.getDetails().getDelegateInfo().getTaxId());
    }

    @And("si verifica che l'elemento di timeline della lettura non riporti i dati del delegato")
    public void siVerificaCheLElementoDiTimelineDellaLetturaNonRiportiIDatiDi() {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV28 timelineElement = getTimelineElement();

        log.info("TIMELINE ELEMENT : {}", timelineElement);
        assertThat(timelineElement).as("Il timeline element non dev'essere null").isNotNull();
        Assertions.assertNotNull(timelineElement.getDetails());
        Assertions.assertNull(timelineElement.getDetails().getDelegateInfo());
    }

    private it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV28 getTimelineElement() {
        try {
            await().atMost(sharedSteps.getWorkFlowWait() * 2, TimeUnit.MILLISECONDS);
        } catch (RuntimeException exception) {
            exception.printStackTrace();
        }
        FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
        return fullSentNotification
                .getTimeline()
                .stream()
                .filter(elem ->
                        Objects.requireNonNull(elem.getCategory()).getValue().equals(NOTIFICATION_VIEWED))
                .findAny()
                .orElse(null);
    }

    @Then("il delegato {string} visualizza le deleghe a suo carico")
    public void delegateViewsAssignedMandates(String user) {
        delegateViewMandate(user, null);
    }

    @Then("il delegato {string} visualizza le deleghe a suo carico con stato {string}")
    public void delegateViewsAssignedMandatesWithStatus(String user, String status) {
        try {
            delegateViewMandate(user, status);
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
        }
    }

    private void delegateViewMandate(String user, String statusFilter) {
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
            }
            Assertions.assertNotNull(mandateList, "La lista mandateList è null");
            Assertions.assertFalse(mandateList.isEmpty(), "La lista mandateList è vuota");
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
        }
    }


    @And("{string} visualizza le deleghe")
    public void visualizzaLeDeleghe(String user) {
        setBearerToken(user);

        List<MandateDto> mandateList = webMandateClient.listMandatesByDelegate1(null);
        List<MandateDto> mandateDtos = Assertions.assertDoesNotThrow(() -> webMandateClient.listMandatesByDelegator1());

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
                .delegator(getUserDtoByUser(data.get("delegator")))
                .delegate(Optional.ofNullable(data.get("delegate")).map(this::getInvalidUserDto).orElse(getUserDtoByUser("mario gherkin")))
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
            case "INVALID_FISCAL_CODE" ->
                    createUserDto("Mario Cucumber", "Mario", "Cucumber", "AAA8090ZAC", null, true);
            case "EMPTY_FIRST_NAME" ->
                    createUserDto("Mario Cucumber", null, "Cucumber", "CLMCST42R12D969Z", null, true);
            case "EMPTY_LAST_NAME" -> createUserDto("Mario Cucumber", "Mario", null, "CLMCST42R12D969Z", null, true);
            case "FIRST_NAME_NOT_VALID" ->
                    createUserDto("Cristoforo Colombo", "PippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippo", "Colombo", "FRMTTR76M06B715E", null, true);
            case "LAST_NAME_NOT_VALID" ->
                    createUserDto("Cristoforo Colombo", "Cristoforo", "PippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippo", "FRMTTR76M06B715E", null, true);
            case "EMPTY_DISPLAY_NAME" -> createUserDto(null, "Mario", "Cucumber", "CLMCST42R12D969Z", null, true);
            case "DISPLAY_NAME_NOT_VALID" ->
                    createUserDto("PippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippo", "Mario", "Cucumber", "CLMCST42R12D969Z", null, true);
            default -> throw new IllegalStateException("Unexpected value: " + delegator);
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

    @And("l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:")
    public void verifyNotificationSearchResponse(Map<String, String> criteria) {
        List<?> resultsPage;
        if (notificationSearchResponseFull != null) {
            resultsPage = notificationSearchResponseFull.getResultsPage();
        } else if (notificationSearchResponseLegal != null) {
            resultsPage = notificationSearchResponseLegal.getResultsPage();
        } else {
            throw new IllegalStateException("Nessuna risposta di ricerca notifiche disponibile per la verifica dei criteri.");
        }

        Map<String, List<String>> resolvedCriteria = notificationSearchCriteriaMapper.build(criteria, new TokenResolver(sharedSteps, sendSharedContext));
        NotificationSearchRowAssertions.assertAllRowsMatchCriteria(resultsPage, resolvedCriteria);
    }
}