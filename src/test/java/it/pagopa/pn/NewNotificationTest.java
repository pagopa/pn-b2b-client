package it.pagopa.pn;

import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.impl.PnPaB2bExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.impl.PnPaB2bInternalClientImpl;
import it.pagopa.pn.client.b2b.pa.springconfig.ApiKeysConfiguration;
import it.pagopa.pn.client.b2b.pa.springconfig.RestTemplateConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.TestPropertySource;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@SpringBootTest( classes = {
        PnPaB2bExternalClientImpl.class,
        PnPaB2bInternalClientImpl.class,
        ApiKeysConfiguration.class,
        RestTemplateConfiguration.class,
        PnPaB2bUtils.class
})
@TestPropertySource(properties = {"spring.profiles.active=dev"})
public class NewNotificationTest {

    @Autowired
    private PnPaB2bUtils utils;

    @Test
    public void insertNewNotification() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        NewNotificationRequestV21 request = new NewNotificationRequestV21()
                .subject("Test inserimento " + dateFormat.format(calendar.getTime()))
                .cancelledIun(null)
                ._abstract("Abstract della notifica")
                .senderDenomination("Comune di Milano")
                .senderTaxId("01199250158")
                .notificationFeePolicy( NotificationFeePolicy.FLAT_RATE )
                .physicalCommunicationType( NewNotificationRequestV21.PhysicalCommunicationTypeEnum.REGISTERED_LETTER_890 )
                .paProtocolNumber("" + System.currentTimeMillis())
                .addDocumentsItem( newDocument( "classpath:/sample.pdf" ) )
                .addRecipientsItem( newRecipient( "Fiera", "FRMTTR76M06B715E","classpath:/sample.pdf"))
                ;

        Assertions.assertDoesNotThrow(() -> {
            NewNotificationResponse newNotificationRequest = utils.uploadNotification( request );
            FullSentNotificationV21 newNotification = utils.waitForRequestAcceptation( newNotificationRequest );
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

    private NotificationRecipientV21 newRecipient(String prefix, String taxId, String resourcePath ) {
        long epochMillis = System.currentTimeMillis();

        NotificationRecipientV21 recipient = new NotificationRecipientV21()
                .denomination( prefix + " denomination")
                .taxId( taxId )
                .digitalDomicile( new NotificationDigitalAddress()
                        .type(NotificationDigitalAddress.TypeEnum.PEC)
                        .address( "FRMTTR76M06B715E@pec.pagopa.it")
                )
                .physicalAddress( new NotificationPhysicalAddress()
                        .address("Via senza nome")
                        .municipality("Milano")
                        .province("MI")
                        .foreignState("ITALIA")
                        .zip("40100")
                );
               // .recipientType( NotificationRecipientV21.RecipientTypeEnum.PF )
                //TODO Modificare.....
              //  .payment( new NotificationPaymentInfo()
               //                 .creditorTaxId("77777777777")
             //                   .noticeCode( String.format("30201%13d", epochMillis ) )
             //                   .noticeCodeAlternative( String.format("30201%13d", epochMillis+1 ) )
            //                    .pagoPaForm( newAttachment( resourcePath ))
//                                .f24flatRate( newAttachment( resourcePath ) )
//                                .f24standard( newAttachment( resourcePath ) )
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
