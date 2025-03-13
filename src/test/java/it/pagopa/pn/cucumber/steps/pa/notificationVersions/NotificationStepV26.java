package it.pagopa.pn.cucumber.steps.pa.notificationVersions;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.utils.NotificationValue;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Objects;

import static it.pagopa.pn.cucumber.steps.SharedSteps.threadWait;
import static it.pagopa.pn.cucumber.utils.NotificationValue.*;

@Data
@Slf4j
public class NotificationStepV26 implements NotificationInterface {


    //    @Value("${pn.bearer-token.user1.taxID}")
    public static String MARIO_CUCUMBER_TAX_ID = "FRMTTR76M06B715E";
    //    @Value("${pn.bearer-token.user2.taxID}")
    public static String MARIO_GHERKIN_TAX_ID = "CLMCST42R12D969Z";
    //    @Value("${pn.bearer-token.user4.taxID}")
    public static String GALILEO_GALILEI_TAX_ID = "GLLGLL64B15G702I";
    public static final String CUCUMBER_SRL_TAX_ID = "20517490320";
    public static final String GHERKIN_SRL_TAX_ID = "12666810299";
    public static final String CUCUMBER_SPA_TAX_ID = "20517490320";
    public static final String GHERKIN_SPA_TAX_ID = "12666810299";
    public static final String CUCUMBER_ANALOGIC_TAX_ID = "LBPHLS94A56C826R";
    public static final String GHERKIN_ANALOGIC_TAX_ID = "05722930657";
    public static final String CUCUMBER_SOCIETY_TAX_ID = "20517490320";
    public static final String GHERKIN_IRREPERIBILE_TAX_ID = "00749900049";

//    private static final NotificationDigitalAddress PEC = new NotificationDigitalAddress().type(NotificationDigitalAddress.TypeEnum.PEC).address(getDigitalAddressValue()))

    private enum Destinatario {
        MARIO_GHERKIN("Mario Gherkin", MARIO_GHERKIN_TAX_ID, null, null),
        MARIO_CUCUMBER("Mario Cucumber", MARIO_CUCUMBER_TAX_ID, null, null);

        final String denomination;
        final String taxId;
        final NotificationRecipientV23.RecipientTypeEnum recipientType;
        final NotificationDigitalAddress digitalDomicile;

        Destinatario(String name, String taxId, NotificationRecipientV23.RecipientTypeEnum recipientType, NotificationDigitalAddress digitalAddress) {
            this.denomination = name;
            this.taxId = taxId;
            this.recipientType = recipientType;
            this.digitalDomicile = digitalAddress;
        }

        public static Destinatario getByName(String name) {
            for (Destinatario destinatario : values()) {
                if (destinatario.denomination.trim().equalsIgnoreCase(name)) {
                    return destinatario;
                }
            }
            throw new IllegalArgumentException("Destinatario inesistente: " + name);
        }
    }

    private NewNotificationRequestV24 notificationRequest;
    private NewNotificationResponse notificationResponse;
    private FullSentNotificationV26 fullSentNotification;
    private OffsetDateTime notificationCreationDate;
    private final SharedSteps.NotificationVersion version;
    private SharedSteps sharedSteps;

    public NotificationStepV26(SharedSteps sharedSteps) {
        version = SharedSteps.NotificationVersion.V26;
        this.sharedSteps = sharedSteps;
    }

    @Override
    public void setNotificationRequest(Map<String, String> data) {
        notificationRequest = sharedSteps.getDataTableTypeUtil().convertNotificationRequest(data);
        sharedSteps.setVersionUsed(version);
    }

    @Override
    public void addRecipitentToNotification(String recipientName, Map<String, String> data) {
        NotificationRecipientV23 notificationRecipient = sharedSteps.getDataTableTypeUtil().convertNotificationRecipient(data);
        if (recipientName != null) {
            Destinatario destinatario = Destinatario.getByName(recipientName);
            notificationRecipient.setDenomination(destinatario.denomination);
            notificationRecipient.setTaxId(destinatario.taxId);
            notificationRecipient.setRecipientType(destinatario.recipientType);
            notificationRecipient.setDigitalDomicile(destinatario.digitalDomicile);

            if (notificationRequest.getNotificationFeePolicy() == NotificationFeePolicy.DELIVERY_MODE
                    && NotificationValue.getValue(data, PAYMENT.key) != null) {
                String pagopaFormValue = getValue(data, PAYMENT_PAGOPA_FORM.key);
                if (pagopaFormValue != null && !pagopaFormValue.equalsIgnoreCase("NO")) {
                    for (NotificationPaymentItem payments : Objects.requireNonNull(notificationRecipient.getPayments())) {
                        Objects.requireNonNull(payments.getPagoPa()).setApplyCost(true);
                    }
                }
            }
        }
        notificationRequest.addRecipientsItem(notificationRecipient);
    }

    //MATTEO TEST
    @Override
    public void sendNotification(String status, int wait) {
        try {
            Assertions.assertDoesNotThrow(() -> {
                notificationCreationDate = OffsetDateTime.now();
                notificationResponse = sharedSteps.getB2bUtils().uploadNotification(notificationRequest);
                threadWait(wait);
                fullSentNotification = sharedSteps.getB2bUtils().waitForRequestAcceptation(notificationResponse);
            });
            threadWait(wait);
            Assertions.assertNotNull(fullSentNotification);
            System.out.println("BELLAAAAAAAAAAAAAA TODO MATTEO");
        } catch (AssertionFailedError assertionFailedError) {
            String requestId = notificationResponse == null ? "NULL" : notificationResponse.getNotificationRequestId();
            String message = assertionFailedError.getMessage() + "{RequestID: " + requestId + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }


}
