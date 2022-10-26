package it.pagopa.pn.cucumber.steps;


import io.cucumber.java.DataTableType;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NewNotificationRequest;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationDigitalAddress;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationRecipient;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.impl.PnPaB2bExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.springconfig.ApiKeysConfiguration;
import it.pagopa.pn.client.b2b.pa.springconfig.BearerTokenConfiguration;
import it.pagopa.pn.client.b2b.pa.springconfig.RestTemplateConfiguration;
import it.pagopa.pn.client.b2b.pa.testclient.*;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.AcceptRequestDto;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.MandateDto;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.UserDto;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.NotificationStatus;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.*;
import org.apache.commons.lang.time.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.io.ByteArrayInputStream;
import java.lang.invoke.MethodHandles;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


public class RicezioneNotificheWebSteps  {

    @Autowired
    private IPnWebRecipientClient webRecipientClient;

    @Autowired
    private IPnWebMandateClient webMandateClient;

    @Autowired
    private PnPaB2bUtils b2bUtils;

    @Autowired
    private DataTableTypeUtil dataTableTypeUtil;

    @Autowired
    private GenerazioneInvioNotificaB2bSteps notificationGlue;

    private HttpClientErrorException httpClientError;
    private HttpServerErrorException notificationError;
    private MandateDto mandateToSearch;
    private final String verificationCode = "24411";
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());



    @Then("la notifica può essere correttamente recuperata dal destinatario")
    public void laNotificaPuoEssereCorrettamenteRecuperataDalDestinatario() {
        Assertions.assertDoesNotThrow(() -> {
            webRecipientClient.getReceivedNotification(notificationGlue.getSentNotification().getIun(), null);
        });
    }


    @Then("il documento notificato può essere correttamente recuperato")
    public void ilDocumentoNotificatoPuoEssereCorrettamenteRecuperato() {
        NotificationAttachmentDownloadMetadataResponse downloadResponse = webRecipientClient.getReceivedNotificationDocument(
                notificationGlue.getSentNotification().getIun(),
                Integer.parseInt(notificationGlue.getSentNotification().getDocuments().get(0).getDocIdx()),
                null
        );
        AtomicReference<String> Sha256 = new AtomicReference<>("");
        Assertions.assertDoesNotThrow(() -> {
            byte[] bytes = Assertions.assertDoesNotThrow(() ->
                    b2bUtils.downloadFile(downloadResponse.getUrl()));
            Sha256.set(b2bUtils.computeSha256(new ByteArrayInputStream(bytes)));
        });
        Assertions.assertEquals(Sha256.get(),downloadResponse.getSha256());
    }


    @Then("l'allegato {string} può essere correttamente recuperato")
    public void lAllegatoPuoEssereCorrettamenteRecuperato(String attachmentName) {
        NotificationAttachmentDownloadMetadataResponse downloadResponse = webRecipientClient.getReceivedNotificationAttachment(
                notificationGlue.getSentNotification().getIun(),
                attachmentName,
                null);
        AtomicReference<String> Sha256 = new AtomicReference<>("");
        Assertions.assertDoesNotThrow(() -> {
            byte[] bytes = Assertions.assertDoesNotThrow(() ->
                    b2bUtils.downloadFile(downloadResponse.getUrl()));
            Sha256.set(b2bUtils.computeSha256(new ByteArrayInputStream(bytes)));
        });
        Assertions.assertEquals(Sha256.get(),downloadResponse.getSha256());
    }

    @And("si tenta il recupero dell'allegato {string}")
    public void siTentaIlRecuperoDelllAllegato(String attachmentName) {
        try {
            webRecipientClient.getReceivedNotificationAttachment(
                    notificationGlue.getSentNotification().getIun(),
                    attachmentName,
                    null);
        } catch (HttpClientErrorException e) {
            this.httpClientError = e;
        }
    }

    @Then("il download dell'allegato ha prodotto un errore con status code {string}")
    public void ilDownloadAllegatoHaProdottoUnErrore(String statusCode) {
        Assertions.assertTrue((this.httpClientError != null) &&
                (this.httpClientError.getStatusCode().toString().substring(0,3).equals(statusCode)));
    }


    @And("si tenta il recupero della notifica da parte del destinatario")
    public void siTentaIlRecuperoDellaNotificaDaParteDelDestinatario() {
        try {
            webRecipientClient.getReceivedNotification(notificationGlue.getSentNotification().getIun(), null);
        } catch (HttpServerErrorException e) {
            this.notificationError = e;
        }
    }

    @Then("l'operazione di recupero ha prodotto un errore con status code {string}")
    public void lOperazioneDiRecuperoHaProdottoUnErroreConStatusCode(String statusCode) {
        Assertions.assertTrue((this.notificationError != null) &&
                (this.notificationError.getStatusCode().toString().substring(0,3).equals(statusCode)));
    }


    @Then("la notifica può essere correttamente recuperata con una ricerca")
    public void laNotificaPuoEssereCorrettamenteRecuperataConUnaRicercaInBaseAlla(@Transpose NotificationSearchParam searchParam) {
        Assertions.assertTrue(searchNotification(searchParam));
    }

    @DataTableType
    public NotificationSearchParam convertNotificationSearchParam(Map<String, String> data){
        NotificationSearchParam searchParam = new NotificationSearchParam();

        Calendar now = Calendar.getInstance();
        int month = now.get(Calendar.MONTH);
        String monthString = (((month+"").length() == 2 || month == 9)?(month+1):("0"+(month+1)))+"";
        int day = now.get(Calendar.DAY_OF_MONTH);
        String dayString = (day+"").length() == 2? (day+""):("0"+day);
        String start = data.getOrDefault("startDate",dayString+"/"+monthString+"/"+now.get(Calendar.YEAR));
        String end = data.getOrDefault("endDate",null);

        OffsetDateTime sentAt = notificationGlue.getSentNotification().getSentAt();
        LocalDateTime localDateStart = LocalDate.parse(start, DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay();
        OffsetDateTime startDate = OffsetDateTime.of(localDateStart,sentAt.getOffset());

        OffsetDateTime endDate;
        if(end != null){
            LocalDateTime localDateEnd = LocalDate.parse(end, DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay();
            endDate = OffsetDateTime.of(localDateEnd,sentAt.getOffset());
        }else{
            endDate = sentAt;
        }

        searchParam.startDate = startDate;
        searchParam.endDate = endDate;
        //searchParam.mandateId = data.getOrDefault("mandateId",null);
        //searchParam.senderId = data.getOrDefault("senderId",null);
        searchParam.subjectRegExp = data.getOrDefault("subjectRegExp",null);
        String iun = data.getOrDefault("iunMatch",null);
        searchParam.iunMatch = ((iun != null && iun.equalsIgnoreCase("ACTUAL")? notificationGlue.getSentNotification().getIun():iun));
        searchParam.size = Integer.parseInt(data.getOrDefault("size","10"));
        return searchParam;
    }

    private boolean searchNotification(NotificationSearchParam searchParam){
        boolean beenFound;
        NotificationSearchResponse notificationSearchResponse = webRecipientClient
                .searchReceivedNotification(
                        searchParam.startDate, searchParam.endDate, searchParam.mandateId,
                        searchParam.senderId, searchParam.status, searchParam.subjectRegExp,
                        searchParam.iunMatch, searchParam.size, null);
        List<NotificationSearchRow> resultsPage = notificationSearchResponse.getResultsPage();
        beenFound = resultsPage.stream().filter(elem -> elem.getIun().equals(notificationGlue.getSentNotification().getIun())).findAny().orElse(null) != null;
        if(!beenFound && Boolean.TRUE.equals(notificationSearchResponse.getMoreResult())){
            while(Boolean.TRUE.equals(notificationSearchResponse.getMoreResult())){
                List<String> nextPagesKey = notificationSearchResponse.getNextPagesKey();
                for(String pageKey: nextPagesKey){
                    notificationSearchResponse = webRecipientClient
                            .searchReceivedNotification(
                                    searchParam.startDate, searchParam.endDate, searchParam.mandateId,
                                    searchParam.senderId, searchParam.status, searchParam.subjectRegExp,
                                    searchParam.iunMatch, searchParam.size, pageKey);
                    beenFound = resultsPage.stream().filter(elem -> elem.getIun().equals(notificationGlue.getSentNotification().getIun())).findAny().orElse(null) != null;
                    if(beenFound)break;
                }//for
                if(beenFound)break;
            }//while
        }//search cycle
        return beenFound;
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
        }catch (HttpClientErrorException e) {
            this.httpClientError = e;
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

    @Then("la notifica può essere correttamente recuperata dal delegato")
    public void laNotificaPuoEssereCorrettamenteRecuperataDalDelegato() {
        Assertions.assertDoesNotThrow(() -> {
            webRecipientClient.getReceivedNotification(notificationGlue.getSentNotification().getIun(), mandateToSearch.getMandateId());
        });
    }

    @Then("il documento notificato può essere correttamente recuperato dal delegato")
    public void ilDocumentoNotificatoPuoEssereCorrettamenteRecuperatoDalDelegato() {
        NotificationAttachmentDownloadMetadataResponse downloadResponse = webRecipientClient.getReceivedNotificationDocument(
                notificationGlue.getSentNotification().getIun(),
                Integer.parseInt(notificationGlue.getSentNotification().getDocuments().get(0).getDocIdx()),
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
                notificationGlue.getSentNotification().getIun(),
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


    @Then("si tenta il recupero della notifica da parte del delegato che produce un errore con status code {string}")
    public void siTentaIlRecuperoDellaNotificaDaParteDelDelegatoCheProduceUnErroreConStatusCode(String statusCode) {
        HttpClientErrorException httpClientErrorException = null;
        try {
            FullReceivedNotification receivedNotification =
                    webRecipientClient.getReceivedNotification(notificationGlue.getSentNotification().getIun(), mandateToSearch.getMandateId());
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
        Assertions.assertTrue((httpClientError != null) &&
                (httpClientError.getStatusCode().toString().substring(0,3).equals(statusCode)));
    }


    private static class  NotificationSearchParam{
        OffsetDateTime startDate;
        OffsetDateTime endDate;
        String mandateId;
        String senderId;
        NotificationStatus status;
        String subjectRegExp;
        String iunMatch;
        Integer size = 10;
    }
}
