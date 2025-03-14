package it.pagopa.pn.cucumber.steps.pa.notificationVersions;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.*;
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
public class NotificationStepsV21 implements NotificationStepsInterface {

    private NewNotificationRequestV21 notificationRequest;
    private NewNotificationResponse notificationResponse;
    private FullSentNotificationV21 fullSentNotification;
    private OffsetDateTime notificationCreationDate;
    private String selectedPA;
    private String senderTaxId;
    private final SharedSteps.NotificationVersion version;
    private SharedSteps sharedSteps;

    public NotificationStepsV21(SharedSteps sharedSteps) {
        version = SharedSteps.NotificationVersion.V21;
        this.sharedSteps = sharedSteps;
    }

    @Override
    public void setNotificationRequest(Map<String, String> data) {
        notificationRequest = sharedSteps.getDataTableTypeUtil().convertNotificationRequestV21(data);
        sharedSteps.setVersionUsed(version);
    }

    @Override
    public void addRecipitentToNotification(String recipientName, Map<String, String> data) {
        NotificationRecipientV21 notificationRecipient = sharedSteps.getDataTableTypeUtil().convertNotificationRecipientV21(data);
        if (notificationRequest.getNotificationFeePolicy() == NotificationFeePolicy.DELIVERY_MODE
                && NotificationValue.getValue(data, PAYMENT.key) != null) {
            String pagopaFormValue = getValue(data, PAYMENT_PAGOPA_FORM.key);
            if (pagopaFormValue != null && !pagopaFormValue.equalsIgnoreCase("NO")) {
                for (NotificationPaymentItem payments : Objects.requireNonNull(notificationRecipient.getPayments())) {
                    Objects.requireNonNull(payments.getPagoPa()).setApplyCost(true);
                }
            }
        }
        if (recipientName != null) {
            Destinatario destinatario = Destinatario.getByName(recipientName);
            notificationRecipient.setDenomination(destinatario.getDenomination());
            notificationRecipient.setTaxId(destinatario.getTaxId());
            notificationRecipient.setRecipientType(NotificationRecipientV21.RecipientTypeEnum.valueOf(destinatario.getRecipientType()));
            /** Nei vecchi metodi @And("Destinatario xxx") denomination e taxId venivano sempre settati
             * (recipientType veniva spesso passato null, ma in quei casi subentrava il valore di default PG)
             * e data veniva passata sempre come mappa vuota.
             * Al contrario nei vecchi metodi @And("Destinatario xxx e:"), data veniva passata come mappa con valori
             * e al contempo digitalDomicile era sempre null, in modo da non sovrascrivere eventuali valori passati.
             * Pertanto il seguente codice segue il vecchio comportamento, ma in maniera più chiara e coincisa */
            if (data.isEmpty()) {
                notificationRecipient.setDigitalDomicile(
                        new NotificationDigitalAddress()
                                .type(NotificationDigitalAddress.TypeEnum.valueOf(destinatario.getDigitalDomicileType()))
                                .address(DestinatariUtils.getDigitalAddressValue()));
            }
        }
        notificationRequest.addRecipientsItem(notificationRecipient);
    }


    @Override
    public String getNotificationRequestGroup() {
        return notificationRequest.getGroup();
    }

    @Override
    public void setNotificationRequestGroup(String group) {
        notificationRequest.setGroup(group);
    }

    //TODO MATTEO TEST
    @Override
    public void sendNotification(String status, int wait) {
        try {
            Assertions.assertDoesNotThrow(() -> {
                notificationCreationDate = OffsetDateTime.now();
                notificationResponse = sharedSteps.getB2bUtils().uploadNotificationV21(notificationRequest);
                threadWait(wait);
                fullSentNotification = sharedSteps.getB2bUtils().waitForRequestAcceptationV21(notificationResponse);
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
