package it.pagopa.pn.client.b2b.pa;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;

@SpringBootTest( classes = {
        PnPaB2bExternalClientImpl.class,
        PnPaB2bInternalClientImpl.class,
        ApiKeysConfiguration.class,
        RestTemplateConfiguration.class,
        PnPaB2bUtils.class
})
@PropertySource( value = "file:config/api-keys.properties", ignoreResourceNotFound = true )
public class NewNotificationTest {

    @Autowired
    private PnPaB2bUtils utils;

    @Test
    public void insertNewNotification() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        NewNotificationRequest request = new NewNotificationRequest()
                .subject("Test inserimento " + dateFormat.format(calendar.getTime()))
                .cancelledIun("")
                .group("TestGroup")
                ._abstract("Abstract della notifica")
                .senderDenomination("Il comune di Milano")
                .senderTaxId("CFComuneMilano")
                .notificationFeePolicy( NewNotificationRequest.NotificationFeePolicyEnum.FLAT_RATE )
                .physicalCommunicationType( NewNotificationRequest.PhysicalCommunicationTypeEnum.REGISTERED_LETTER_890 )
                .paProtocolNumber("" + System.currentTimeMillis())
                .addDocumentsItem( newDocument( "classpath:/sample.pdf" ) )
                .addRecipientsItem( newRecipient( "Fiera", "FRMTTR76M06B715E","classpath:/sample.pdf"))
                ;

        Assertions.assertDoesNotThrow(() -> {
            NewNotificationResponse newNotificationRequest = utils.uploadNotification( request );
            FullSentNotification newNotification = utils.waitForRequestAcceptation( newNotificationRequest );
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

    private NotificationRecipient newRecipient(String prefix, String taxId, String resourcePath ) {
        long epochMillis = System.currentTimeMillis();

        NotificationRecipient recipient = new NotificationRecipient()
                .denomination( prefix + " denomination")
                .taxId( taxId )
                .digitalDomicile( new NotificationDigitalAddress()
                        .type(NotificationDigitalAddress.TypeEnum.PEC)
                        .address( "FRMTTR76M06B715E@pnpagopa.postecert.local")
                )
                .physicalAddress( new NotificationPhysicalAddress()
                        .address("Via senza nome")
                        .municipality("Milano")
                        .province("MI")
                        .foreignState("ITALIA")
                        .zip("40100")
                )
                .recipientType( NotificationRecipient.RecipientTypeEnum.PF )
                .payment( new NotificationPaymentInfo()
                                .creditorTaxId("77777777777")
                                .noticeCode( String.format("30201%13d", epochMillis ) )
                                .noticeCodeAlternative( String.format("30201%13d", epochMillis+1 ) )
                                .pagoPaForm( newAttachment( resourcePath ))
                                .f24flatRate( newAttachment( resourcePath ) )
                                .f24standard( newAttachment( resourcePath ) )
                );

        try {
            Thread.sleep(10);
        }
        catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }

        return recipient;
    }

}
