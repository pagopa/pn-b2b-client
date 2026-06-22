package it.pagopa.pn.cucumber.steps.tracciamentoEventi;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.externalchannel.model.EventsDto;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.externalchannel.model.RequestDto;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.externalchannels.v1.model.DigitalCourtesyMailRequest;
import it.pagopa.pn.client.b2b.pa.service.IPnEcInternalClient;
import it.pagopa.pn.client.b2b.pa.service.IPnExternalChannelsInternalClient;
import it.pagopa.pn.client.b2b.pa.service.impl.PnSafeStoragePrivateClientImpl;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.model.FileCreationRequest;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.model.FileCreationResponse;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class TracciamentoEventiPeoSteps {

    private final SharedSteps sharedSteps;
    private final PnSafeStoragePrivateClientImpl safeStorageClient;
    private final IPnExternalChannelsInternalClient externalChannelsInternalClient;
    private final IPnEcInternalClient ecInternalClient;
    private String clientInUse;
    private String requestId;
    private DigitalCourtesyMailRequest lastEmailRequest;
    private static final String EICAR = "X5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*";

    @Autowired
    public TracciamentoEventiPeoSteps(SharedSteps sharedSteps,
                                      PnSafeStoragePrivateClientImpl safeStorageClient,
                                      IPnExternalChannelsInternalClient externalChannelsInternalClient,
                                      IPnEcInternalClient ecInternalClient) {
        this.sharedSteps = sharedSteps;
        this.safeStorageClient = safeStorageClient;
        this.externalChannelsInternalClient = externalChannelsInternalClient;
        this.ecInternalClient = ecInternalClient;
    }

    @Given("viene impostato il client {string}")
    public void setClientInUse(String clientId) {
        this.clientInUse = clientId;
        safeStorageClient.customApiClient(clientId);
    }

    @When("viene inviata una mail tramite PEO all'indirizzo {string} con allegato {string}")
    public void sendEmailWithAttachment(String emailAddress, String attachmentType) {
        DigitalCourtesyMailRequest emailRequest = buildEmailRequest(emailAddress, attachmentType);

        ResponseEntity response = sendDigitalCourtesyMessage(emailRequest);
        assertThat(response.getStatusCode().is2xxSuccessful()).as("La chiamata di invio mail ha prodotto un errore col seguente status code: {}", response.getStatusCodeValue()).isTrue();
        log.info("Email inviata con successo");
        log.info("Request id: {}", requestId);
    }

    @When("viene inviata nuovamente la stessa mail tramite PEO e si ottiene status code 204")
    public void sendSameEmailAgain() {
        assertThat(lastEmailRequest)
                .as("Dev'essere stata inviata una mail in precedenza per poterla reinviare")
                .isNotNull();

        ResponseEntity response = sendDigitalCourtesyMessage(lastEmailRequest);
        assertThat(response.getStatusCodeValue())
                .as("La seconda chiamata di invio mail con gli stessi parametri e requestId '%s' dovrebbe restituire status code 204, ma ha restituito %s", requestId, response.getStatusCodeValue())
                .isEqualTo(204);
        log.info("Seconda mail con stessi parametri inviata, status code 204 ottenuto come atteso");
        log.info("Request id: {}", requestId);
    }

    private DigitalCourtesyMailRequest buildEmailRequest(String emailAddress, String attachmentType) {
        String timestamp = Instant.now().toString();
        requestId = "TEST_QA_" + timestamp;

        DigitalCourtesyMailRequest emailRequest = new DigitalCourtesyMailRequest();
        emailRequest.setRequestId(requestId);
        emailRequest.setEventType("COURTESY_MESSAGE");
        emailRequest.setClientRequestTimeStamp(timestamp);
        emailRequest.setQos(DigitalCourtesyMailRequest.QosEnum.INTERACTIVE);
        emailRequest.setReceiverDigitalAddress(emailAddress);
        emailRequest.setMessageText("This is a courtesy message from QA");
        emailRequest.setChannel(DigitalCourtesyMailRequest.ChannelEnum.EMAIL);
        emailRequest.setSubjectText("Test QA invio email");
        emailRequest.setMessageContentType(DigitalCourtesyMailRequest.MessageContentTypeEnum.PLAIN);
        emailRequest.setAttachmentUrls(
                attachmentType.equalsIgnoreCase("virus") ? List.of(uploadEicarVirusFile()) : new ArrayList<>());

        this.lastEmailRequest = emailRequest;
        return emailRequest;
    }

    private ResponseEntity sendDigitalCourtesyMessage(DigitalCourtesyMailRequest emailRequest) {
        return externalChannelsInternalClient.sendDigitalCourtesyMessage(emailRequest.getRequestId(), clientInUse, emailRequest);
    }

    private String uploadEicarVirusFile() {
        byte[] byteArray = EICAR.getBytes(StandardCharsets.US_ASCII);
        String sha256 = B2bUtils.computeSha256(new ByteArrayInputStream(byteArray));

        FileCreationRequest request = new FileCreationRequest();
        request.setStatus("SAVED");
        request.setContentType("text/plain");
        request.setDocumentType("PN_NOTIFICATION_ATTACHMENTS");

        // Chiamata a Safe Storage per registrare il file e ottenere la presigned url di upload
        ResponseEntity<FileCreationResponse> responseEntity = safeStorageClient.createFileWithHttpInfo(clientInUse, sha256, "SHA256", request);
        assertThat(responseEntity).as("La responseEntity non dev'essere null").isNotNull();
        FileCreationResponse fileCreationResponse = responseEntity.getBody();
        assertThat(fileCreationResponse).as("La FileCreationResponse non dev'essere null").isNotNull();

        String fileKey = fileCreationResponse.getKey();
        String secret = fileCreationResponse.getSecret();
        String url = fileCreationResponse.getUploadUrl();

        B2bUtils.loadToPresignedFromByteArray(url, secret, sha256, byteArray, "text/plain");
        return "safestorage://" + fileKey;
    }


    @Then("la request recuperata da gestore-repository deve avere un'eventsList con i seguenti eventi {string}")
    public void retrieveRequestFromGestoreRepository(String expectedContent) {
        log.info("Waiting up to 2 minutes for the email to be delivered");
        await()
                .atMost(2, TimeUnit.MINUTES)
                .pollInterval(10, TimeUnit.SECONDS)
                .ignoreExceptions()
                .untilAsserted(() -> {
                    RequestDto requestRetrieved = ecInternalClient.getRequest(clientInUse, requestId);
                    log.info("Request retrieved: {}", requestRetrieved);

                    assertThat(requestRetrieved).as("La request recuperata con id %s e client %s non dev'essere null", requestId, clientInUse).isNotNull();
                    assertThat(requestRetrieved.getRequestMetadata()).as("I metadati della request recuperata con id %s e client %s non devono essere null", requestId, clientInUse).isNotNull();
                    assertThat(requestRetrieved.getRequestMetadata().getEventsList()).as("La lista di eventi dei metadati della request recuperata con id %s e client %s non dev'essere null", requestId, clientInUse).isNotNull();
                    checkEventsList(requestRetrieved.getRequestMetadata().getEventsList(), expectedContent);
                });
    }

    private void checkEventsList(List<EventsDto> eventsList, String expectedContent) {
        String[] eventsExpected = expectedContent.split(";");
        Map<String, String> mapCodeStatus = new HashMap<>();
        for (String event : eventsExpected) {
            String[] codeStatus = event.split("-");
            mapCodeStatus.put(codeStatus[0], codeStatus[1]);
        }
        mapCodeStatus.forEach((statusCode, status) -> {
            EventsDto target = eventsList.stream().filter(
                    e -> e.getDigProgrStatus() != null
                            && e.getDigProgrStatus().getStatus().equals(status)
                            && e.getDigProgrStatus().getStatusCode().equals(statusCode)).findFirst().orElse(null);
            assertThat(target)
                    .as("L'eventsList dovrebbe contenere un evento con statusCode '%s' e status '%s'", statusCode, status)
                    .isNotNull();
            log.info("Event expected found: {}", target);
        });
    }
}
