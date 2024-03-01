package it.pagopa.pn;

import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaB2bExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.*;
import it.pagopa.pn.client.b2b.pa.springconfig.ApiKeysConfiguration;
import it.pagopa.pn.client.b2b.pa.springconfig.BearerTokenConfiguration;
import it.pagopa.pn.client.b2b.pa.springconfig.RestTemplateConfiguration;
import it.pagopa.pn.client.b2b.pa.springconfig.TimingConfiguration;
import it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenSingleton;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

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
        PnServiceDeskClientImplNoApiKey.class,
        PnServiceDeskClientImplWrongApiKey.class,
        PnGPDClientImpl.class,
        PnPaymentInfoClientImpl.class,
        PnRaddFsuClientImpl.class,
        PnRaddAlternativeClientImpl.class
})
@TestPropertySource(properties = {"spring.profiles.active=dev"})
public class NewNotificationTest {

    @Autowired
    private PnPaB2bUtils utils;


    @Test
    public void insertNewNotification() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");


        // README!!!!!!!!!!!!!!!!!!!! PER TE SVILUPPATORE !!!!!!!!!!!!!!!!
        // modificare a FLAT_RATE o DELIVERY_MODE a piacere
        NotificationFeePolicy policy = NotificationFeePolicy.DELIVERY_MODE;
        // modificare se si vuole inviare un f24. invia i metadati in base al delivery_mode
        boolean enableF24Attachment = true;
        // modifica pure i parametri a piacimento
        NewNotificationRequestV23 request = new NewNotificationRequestV23()
                .subject("Test inserimento " + dateFormat.format(calendar.getTime()))
                .cancelledIun(null)
                ._abstract("Abstract della notifica")
                .senderDenomination("Comune di Sapppada")
                .pagoPaIntMode(NewNotificationRequestV23.PagoPaIntModeEnum.SYNC)
                .taxonomyCode("010202N")
                .paFee(100)
                .vat(22)
                .senderTaxId("00207190257")
                .notificationFeePolicy( policy )
                .physicalCommunicationType( NewNotificationRequestV23.PhysicalCommunicationTypeEnum.REGISTERED_LETTER_890 )
                .paProtocolNumber("" + System.currentTimeMillis())
                .addDocumentsItem( newDocument( "classpath:/sample.pdf" ) )
                .addRecipientsItem( newRecipient( 
                        policy!=NotificationFeePolicy.FLAT_RATE,"Leo ", "DVNLRD52D15M059P","classpath:/sample.pdf",
                         enableF24Attachment?(policy==NotificationFeePolicy.FLAT_RATE?"classpath:/f24_flat.json":"classpath:/f24_deliverymode.json"):null,
                        RECIPIENT_TYPE_DIGITAL.DIGITAL_KO, RECIPIENT_TYPE_ANALOG.ANALOG_OK))
                //.addRecipientsItem( newRecipient( policy!=NotificationFeePolicy.FLAT_RATE,"Fiera ", "FRMTTR76M06B715E","classpath:/sample.pdf",
                //        enableF24Attachment?(policy==NotificationFeePolicy.FLAT_RATE?"classpath:/f24_flat.json":"classpath:/f24_deliverymode.json"):null,
                //        RECIPIENT_TYPE_DIGITAL.NO_DIGITAL, RECIPIENT_TYPE_ANALOG.ANALOG_OK))
                ;


        Assertions.assertDoesNotThrow(() -> {

            NewNotificationResponse newNotificationRequest = utils.uploadNotification( request );
            FullSentNotificationV23 newNotification = utils.waitForRequestAcceptation( newNotificationRequest );
            Thread.sleep( 10 * 1000);
            utils.verifyNotification( newNotification );
        });
    }


    @Test
    @Disabled
    public void insertNewNotificationMulti() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        NewNotificationRequestV23 request = new NewNotificationRequestV23()
                .cancelledIun(null)
                ._abstract("Abstract della notifica")
                .senderDenomination("Comune di Sapppada")
                //.senderTaxId("01199250158")
                .senderTaxId("00207190257")
                .notificationFeePolicy( NotificationFeePolicy.FLAT_RATE )
                .physicalCommunicationType( NewNotificationRequestV23.PhysicalCommunicationTypeEnum.REGISTERED_LETTER_890 )
                .paProtocolNumber("" + System.currentTimeMillis())
                .addDocumentsItem( newDocument( "classpath:/sample.pdf" ) )
                .addRecipientsItem( newRecipient( false,"Leo ", "CNCGPP80A01H501J","classpath:/sample.pdf","classpath:/f24_flat.json", RECIPIENT_TYPE_DIGITAL.NO_DIGITAL, RECIPIENT_TYPE_ANALOG.ANALOG_KO))
                .addRecipientsItem( newRecipient( false, "Fiera", "FRMTTR76M06B715E","classpath:/sample.pdf","classpath:/f24_flat.json", RECIPIENT_TYPE_DIGITAL.DIGITAL_OK, RECIPIENT_TYPE_ANALOG.ANALOG_OK))
                ;

        Assertions.assertDoesNotThrow(() -> {
            NewNotificationResponse newNotificationRequest = utils.uploadNotification( request );
            FullSentNotificationV23 newNotification = utils.waitForRequestAcceptation( newNotificationRequest );
            Thread.sleep( 10 * 1000);
            utils.verifyNotification( newNotification );
        });
    }

    private NotificationDocument newDocument(String resourcePath ) {
        return new NotificationDocument()
                .contentType("application/pdf")
                .ref( new NotificationAttachmentBodyRef().key( resourcePath ));
    }

    private NotificationPaymentAttachment newAttachment(String resourcePath ) {
        return new NotificationPaymentAttachment()
                .contentType("application/pdf")
                .ref( new NotificationAttachmentBodyRef().key( resourcePath ));
    }



    private NotificationMetadataAttachment newMatadataAttachment(String resourcePath ) {
        return new NotificationMetadataAttachment()
                .contentType("application/json")
                .ref( new NotificationAttachmentBodyRef().key( resourcePath ));
    }


    private enum RECIPIENT_TYPE_DIGITAL{
        NO_DIGITAL, DIGITAL_OK, DIGITAL_KO
    }


    private enum RECIPIENT_TYPE_ANALOG{
        ANALOG_OK, ANALOG_KO
    }

    private NotificationRecipientV23 newRecipient(boolean withapplycost, String prefix, String taxId, String resourcePath, String resourcePathf24, RECIPIENT_TYPE_DIGITAL recipientTypeDigital, RECIPIENT_TYPE_ANALOG recipientTypeAnalog ) {

        long epochMillis = System.currentTimeMillis();

        NotificationRecipientV23 recipient = new NotificationRecipientV23()
                .denomination( prefix + " denomination")
                .taxId( taxId )
                .digitalDomicile(recipientTypeDigital==RECIPIENT_TYPE_DIGITAL.NO_DIGITAL?null:
                        recipientTypeDigital==RECIPIENT_TYPE_DIGITAL.DIGITAL_OK?
                        new NotificationDigitalAddress()
                            .type(NotificationDigitalAddress.TypeEnum.PEC)
                            .address( "FRMTTR76M06B715E@pec.pagopa.it"):
                        new NotificationDigitalAddress()
                            .type(NotificationDigitalAddress.TypeEnum.PEC)
                            .address( "FRMTTR76M06B715E@fail.it")
                )
                .physicalAddress(
                        recipientTypeAnalog==RECIPIENT_TYPE_ANALOG.ANALOG_OK?
                        new NotificationPhysicalAddress()
                                .address("via tutto ok 16")
                                .municipality("ROMA")
                                .province("RM")
                                .foreignState("ITALIA")
                                .zip("00028"):
                        new NotificationPhysicalAddress()
                                .address("via @FAIL-Irreperibile_AR 16")
                                .municipality("ROMA")
                                .province("RM")
                                .foreignState("ITALIA")
                                .zip("00028")
                )
                .recipientType( NotificationRecipientV23.RecipientTypeEnum.PF )
                .payments(List.of( new NotificationPaymentItem()
                                        .pagoPa(new PagoPaPayment().creditorTaxId("77777777777")
                                                .noticeCode( String.format("30201%13d", epochMillis ) )
                                                .applyCost(withapplycost)
                                                .attachment ( newAttachment( resourcePath ))),
                        new NotificationPaymentItem()
                                .pagoPa(new PagoPaPayment().creditorTaxId("77777777777")
                                        .noticeCode( String.format("30202%13d", epochMillis ) )
                                        .applyCost(false)
                                        .attachment ( newAttachment( resourcePath ))),
                        resourcePathf24 == null ?
                        new NotificationPaymentItem()
                                .pagoPa(new PagoPaPayment().creditorTaxId("77777777777")
                                        .noticeCode( String.format("30203%13d", epochMillis ) )
                                        .applyCost(false)
                                        .attachment ( newAttachment( resourcePath ))):
                        new NotificationPaymentItem()
                                .f24(new F24Payment()
                                        .applyCost(withapplycost)
                                        .title("f24 qualcosa")
                                        .metadataAttachment( newMatadataAttachment( resourcePathf24 ))),new NotificationPaymentItem()
                                .f24(new F24Payment()
                                        .applyCost(withapplycost)
                                        .title("f24 qualcosa 1")
                                        .metadataAttachment( newMatadataAttachment( resourcePathf24 ))),
                        new NotificationPaymentItem()
                                .f24(new F24Payment()
                                        .applyCost(false)
                                        .title("f24 qualcosa 2")
                                        .metadataAttachment( newMatadataAttachment( "classpath:/f24_flat.json" )))
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

        try {
            Thread.sleep(10);
        }
        catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }

        return recipient;
    }

}