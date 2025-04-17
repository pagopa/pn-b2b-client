package it.pagopa.pn;

import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.config.PnB2bClientTimingConfigs;
import it.pagopa.pn.client.b2b.pa.config.springconfig.ApiKeysConfiguration;
import it.pagopa.pn.client.b2b.pa.config.springconfig.BearerTokenConfiguration;
import it.pagopa.pn.client.b2b.pa.config.springconfig.RestTemplateConfiguration;
import it.pagopa.pn.client.b2b.pa.config.springconfig.TimingConfiguration;
import it.pagopa.pn.client.b2b.pa.exception.PnB2bException;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingFactory;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV28;
import it.pagopa.pn.client.b2b.pa.polling.impl.v28.PnPollingServiceValidationStatusV28;
import it.pagopa.pn.client.b2b.pa.service.impl.*;
import it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenSingleton;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.Base64Utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static it.pagopa.pn.client.b2b.pa.PnPaB2bUtils.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;


@SpringBootTest(classes = {
        ApiKeysConfiguration.class,
        BearerTokenConfiguration.class,
        TimingConfiguration.class,
        RestTemplateConfiguration.class,
        PnPaB2bUtils.class,
        PnPaB2bExternalClientImpl.class,
        PnWebRecipientExternalClientImpl.class,
        PnWebhookB2bExternalClientImpl.class,
        PnWebMandateExternalClientImpl.class,
        PnExternalServiceClientImpl.class,
        PnWebUserAttributesExternalClientImpl.class,
        PnAppIOB2bExternalClientImpl.class,
        PnApiKeyManagerExternalClientImpl.class,
        PnDowntimeLogsExternalClientImpl.class,
        PnIoUserAttributerExternaClient.class,
        PnWebPaClientImpl.class,
        PnPrivateDeliveryPushExternalClient.class,
        InteropTokenSingleton.class,
        PnServiceDeskClientImpl.class,
        PnGPDClientImpl.class,
        PnPaymentInfoClientImpl.class,
        PnRaddFsuClientImpl.class,
        PnRaddAlternativeClientImpl.class,
        TimingForPolling.class,
        PnB2bClientTimingConfigs.class,
        PnPollingFactory.class,
})


@Slf4j
@TestPropertySource(properties = {"spring.profiles.active=test"})
@EnableConfigurationProperties
public class NewNotificationTest {

    @Autowired
    private PnPaB2bUtils utils;


    @Test
    void insertNewNotification() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        // README!!!!!!!!!!!!!!!!!!!! PER TE SVILUPPATORE !!!!!!!!!!!!!!!!
        // modificare a FLAT_RATE o DELIVERY_MODE a piacere
        NotificationFeePolicy policy = NotificationFeePolicy.DELIVERY_MODE;
        // modificare se si vuole inviare un f24. invia i metadati in base al delivery_mode
        boolean enableF24Attachment = true;
        // modifica pure i parametri a piacimento
        NewNotificationRequestV25 request = new NewNotificationRequestV25()
                .subject("Test inserimento " + dateFormat.format(calendar.getTime()))
                .cancelledIun(null)
                ._abstract("Abstract della notifica")
                .senderDenomination("Comune di Sappada")
                .pagoPaIntMode(NewNotificationRequestV25.PagoPaIntModeEnum.SYNC)
                .taxonomyCode("010202N")
                .paFee(100)
                .vat(22)
                .senderTaxId("00207190257")
                .notificationFeePolicy(policy)
                .physicalCommunicationType(NewNotificationRequestV25.PhysicalCommunicationTypeEnum.REGISTERED_LETTER_890)
                .paProtocolNumber(String.valueOf(System.currentTimeMillis()))
                .addDocumentsItem(newDocument("classpath:/sample.pdf"))
                .addRecipientsItem(newRecipient(
                        policy != NotificationFeePolicy.FLAT_RATE,
                        "Leo ",
                        "DVNLRD52D15M059P",
                        "classpath:/sample.pdf",
                        enableF24Attachment ? (policy == NotificationFeePolicy.FLAT_RATE ? "classpath:/f24_flat.json" : "classpath:/f24_deliverymode.json") : null,
                        RECIPIENT_TYPE_DIGITAL.DIGITAL_KO, RECIPIENT_TYPE_ANALOG.ANALOG_OK))
                //.addRecipientsItem( newRecipient( policy!=NotificationFeePolicy.FLAT_RATE,"Fiera ", "FRMTTR76M06B715E","classpath:/sample.pdf",
                //        enableF24Attachment?(policy==NotificationFeePolicy.FLAT_RATE?"classpath:/f24_flat.json":"classpath:/f24_deliverymode.json"):null,
                //        RECIPIENT_TYPE_DIGITAL.NO_DIGITAL, RECIPIENT_TYPE_ANALOG.ANALOG_OK))
                ;


        Assertions.assertDoesNotThrow(() -> {
            NewNotificationResponse newNotificationRequest = uploadNotification(request);
            FullSentNotificationV27 newNotification = waitForRequestAcceptation(newNotificationRequest);
            await().atMost(10, SECONDS);
            utils.verifyNotification(newNotification);
        });
    }

    @Test
    @Disabled("To reviewed")
    void insertNewNotificationMulti() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        NewNotificationRequestV25 request = new NewNotificationRequestV25()
                .cancelledIun(null)
                ._abstract("Abstract della notifica")
                .senderDenomination("Comune di Sappada")
                //.senderTaxId("01199250158")
                .senderTaxId("00207190257")
                .notificationFeePolicy(NotificationFeePolicy.FLAT_RATE)
                .physicalCommunicationType(NewNotificationRequestV25.PhysicalCommunicationTypeEnum.REGISTERED_LETTER_890)
                .paProtocolNumber(String.valueOf(System.currentTimeMillis()))
                .addDocumentsItem(newDocument("classpath:/sample.pdf"))
                .addRecipientsItem(newRecipient(false, "Leo ", "CNCGPP80A01H501J", "classpath:/sample.pdf", "classpath:/f24_flat.json", RECIPIENT_TYPE_DIGITAL.NO_DIGITAL, RECIPIENT_TYPE_ANALOG.ANALOG_KO))
                .addRecipientsItem(newRecipient(false, "Fiera", "FRMTTR76M06B715E", "classpath:/sample.pdf", "classpath:/f24_flat.json", RECIPIENT_TYPE_DIGITAL.DIGITAL_OK, RECIPIENT_TYPE_ANALOG.ANALOG_OK));

        Assertions.assertDoesNotThrow(() -> {
            NewNotificationResponse newNotificationRequest = uploadNotification(request);
            FullSentNotificationV27 newNotification = waitForRequestAcceptation(newNotificationRequest);
            await().atMost(10, SECONDS);
            utils.verifyNotification(newNotification);
        });
    }

    private NotificationDocument newDocument(String resourcePath) {
        return new NotificationDocument()
                .contentType("application/pdf")
                .ref(new NotificationAttachmentBodyRef().key(resourcePath));
    }

    private NotificationPaymentAttachment newAttachment(String resourcePath) {
        return new NotificationPaymentAttachment()
                .contentType("application/pdf")
                .ref(new NotificationAttachmentBodyRef().key(resourcePath));
    }

    private NotificationMetadataAttachment newMatadataAttachment(String resourcePath) {
        return new NotificationMetadataAttachment()
                .contentType("application/json")
                .ref(new NotificationAttachmentBodyRef().key(resourcePath));
    }

    private enum RECIPIENT_TYPE_DIGITAL {
        NO_DIGITAL, DIGITAL_OK, DIGITAL_KO
    }

    private enum RECIPIENT_TYPE_ANALOG {
        ANALOG_OK, ANALOG_KO
    }

    private NotificationRecipientV24 newRecipient(boolean withApplyCost, String prefix, String taxId, String resourcePath, String resourcePathF24, RECIPIENT_TYPE_DIGITAL recipientTypeDigital, RECIPIENT_TYPE_ANALOG recipientTypeAnalog) {
        long epochMillis = System.currentTimeMillis();
        NotificationRecipientV24 recipient = new NotificationRecipientV24()
                .denomination(prefix + " denomination")
                .taxId(taxId)
                .digitalDomicile(recipientTypeDigital == RECIPIENT_TYPE_DIGITAL.NO_DIGITAL ? null :
                        recipientTypeDigital == RECIPIENT_TYPE_DIGITAL.DIGITAL_OK ?
                                new NotificationDigitalAddress()
                                        .type(NotificationDigitalAddress.TypeEnum.PEC)
                                        .address("FRMTTR76M06B715E@pec.pagopa.it") :
                                new NotificationDigitalAddress()
                                        .type(NotificationDigitalAddress.TypeEnum.PEC)
                                        .address("FRMTTR76M06B715E@fail.it")
                )
                .physicalAddress(
                        recipientTypeAnalog == RECIPIENT_TYPE_ANALOG.ANALOG_OK ?
                                new NotificationPhysicalAddress()
                                        .address("via tutto ok 16")
                                        .municipality("ROMA")
                                        .province("RM")
                                        .foreignState("ITALIA")
                                        .zip("00173") :
                                new NotificationPhysicalAddress()
                                        .address("via @FAIL-Irreperibile_AR 16")
                                        .municipality("ROMA")
                                        .province("RM")
                                        .foreignState("ITALIA")
                                        .zip("00173")
                )
                .recipientType(NotificationRecipientV24.RecipientTypeEnum.PF)
                .payments(List.of(new NotificationPaymentItem()
                                .pagoPa(new PagoPaPayment().creditorTaxId("77777777777")
                                        .noticeCode(String.format("30201%13d", epochMillis))
                                        .applyCost(withApplyCost)
                                        .attachment(newAttachment(resourcePath))),
                        new NotificationPaymentItem()
                                .pagoPa(new PagoPaPayment().creditorTaxId("77777777777")
                                        .noticeCode(String.format("30202%13d", epochMillis))
                                        .applyCost(false)
                                        .attachment(newAttachment(resourcePath))),
                        resourcePathF24 == null ?
                                new NotificationPaymentItem()
                                        .pagoPa(new PagoPaPayment().creditorTaxId("77777777777")
                                                .noticeCode(String.format("30203%13d", epochMillis))
                                                .applyCost(false)
                                                .attachment(newAttachment(resourcePath))) :
                                new NotificationPaymentItem()
                                        .f24(new F24Payment()
                                                .applyCost(withApplyCost)
                                                .title("f24 qualcosa")
                                                .metadataAttachment(newMatadataAttachment(resourcePathF24))),
                        new NotificationPaymentItem()
                                .f24(new F24Payment()
                                        .applyCost(withApplyCost)
                                        .title("f24 qualcosa 1")
                                        .metadataAttachment(newMatadataAttachment(resourcePathF24))),
                        new NotificationPaymentItem()
                                .f24(new F24Payment()
                                        .applyCost(false)
                                        .title("f24 qualcosa 2")
                                        .metadataAttachment(newMatadataAttachment("classpath:/f24_flat.json")))
                ));

        //TODO Modificare.....
        //  .payments( new NotificationPaymentInfo()
        //                 .creditorTaxId("77777777777")
        //                   .noticeCode( String.format("30201%13d", epochMillis ) )
        //                   .noticeCodeAlternative( String.format("30201%13d", epochMillis+1 ) )
        //                    .pagoPaForm( newAttachment( resourcePath ))
        //                        .f24flatRate( newAttachment( resourcePath ) )
        //                        .f24standard( newAttachment( resourcePath ) )
        //  );

        await().atMost(10, SECONDS);
        return recipient;
    }

    private NewNotificationResponse uploadNotification(NewNotificationRequestV25 request) throws IOException {
        //PRELOAD DOCUMENTI NOTIFICA
        List<NotificationDocument> newDocs = new ArrayList<>();
        for (NotificationDocument doc : request.getDocuments()) {
            try {
                Thread.sleep(utils.getRandom().nextInt(350));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new PnB2bException(e.getMessage());
            }
            //TODO MATTEO FINIRE
//            if (doc != null) {
//                newDocs.add(preloadDocument(doc));
//            }
        }
        request.setDocuments(newDocs);
        //PRELOAD DOCUMENTI DI PAGAMENTO
        preloadPayDocument(request);
        return getAndCheckSendNewNotification(request);
    }

    private NewNotificationResponse getAndCheckSendNewNotification(NewNotificationRequestV25 request) {
        log.info(NEW_NOTIFICATION_REQUEST, request);
        NewNotificationResponse response = utils.getClient().sendNewNotificationV25(request);
        log.info(NEW_NOTIFICATION_REQUEST_RESPONSE, response);
        if (response != null) {
            try {
                log.info(NEW_NOTIFICATION_IUN, new String(Base64Utils.decodeFromString(response.getNotificationRequestId())));
            } catch (Exception e) {
                throw new PnB2bException(e.getMessage());
            }
        }
        return response;
    }

    private FullSentNotificationV27 waitForRequestAcceptation(NewNotificationResponse response) {
        PnPollingServiceValidationStatusV28 validationStatus = (PnPollingServiceValidationStatusV28) utils.getPollingFactory().getPollingService(PnPollingStrategy.VALIDATION_STATUS_V28);
        PnPollingResponseV28 pollingResponse = validationStatus.waitForEvent(response.getNotificationRequestId(), PnPollingParameter.builder().value(ACCEPTED).build());
        return pollingResponse.getNotification() == null ? null : pollingResponse.getNotification();
    }

    private void preloadPayDocument(NewNotificationRequestV25 request) throws IOException {
        for (NotificationRecipientV24 recipient : request.getRecipients()) {
            List<NotificationPaymentItem> paymentList = recipient.getPayments();
            if (paymentList != null) {
                setAttachmentWithSleep(paymentList);
            }
        }
    }

    private void setAttachmentWithSleep(List<NotificationPaymentItem> paymentList) throws IOException {
        for (NotificationPaymentItem paymentInfo : paymentList) {
            try {
                Thread.sleep(utils.getRandom().nextInt(350));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new PnB2bException(e.getMessage());
            }
            //TODO MATTEO FINIRE
//            if (paymentInfo.getPagoPa() != null) {
//                paymentInfo.getPagoPa().setAttachment(preloadAttachment(paymentInfo.getPagoPa().getAttachment()));
//            }
//            if (paymentInfo.getF24() != null) {
//                paymentInfo.getF24().setMetadataAttachment(preloadWithMetadataAttachment(paymentInfo.getF24().getMetadataAttachment()));
//            }
        }
    }
}