package it.pagopa.pn.cucumber.steps.pa.invioNotificheVersions;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.payment.PaymentInfoRequest;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NewNotificationRequestV24;
import it.pagopa.pn.cucumber.steps.pa.InvioNotificheB2bSteps;
import it.pagopa.pn.cucumber.steps.pa.notificationVersions.NotificationStepsV24;
import it.pagopa.pn.cucumber.steps.pa.notificationVersions.NotificationVersion;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Data
public class InvioNotificheStepsV24 implements InvioNotificheStepsInterface {

    private InvioNotificheB2bSteps invioNotificheB2bSteps;
    private final NotificationVersion version;

    public InvioNotificheStepsV24(InvioNotificheB2bSteps invioNotificheB2bSteps) {
        version = NotificationVersion.V24;
        this.invioNotificheB2bSteps = invioNotificheB2bSteps;
    }

    private NotificationStepsV24 getNotificationStep() {
        return (NotificationStepsV24) invioNotificheB2bSteps.getSharedSteps().getMapOfVersionSteps().get(NotificationVersion.V24);
    }

    @Override
    public void verificaStatoPagamentoNotifica(String status, String errorCode, String creditorTaxId, String noticeCode) {
        NewNotificationRequestV24 newNotificationRequest = getNotificationStep().getNotificationRequest();
        if (creditorTaxId == null)
            creditorTaxId = newNotificationRequest.getRecipients().get(0).getPayments().get(0).getPagoPa().getCreditorTaxId();
        if (noticeCode == null)
            noticeCode = newNotificationRequest.getRecipients().get(0).getPayments().get(0).getPagoPa().getNoticeCode();


        invioNotificheB2bSteps.setPaymentInfoResponse(invioNotificheB2bSteps.getPaymentInfoResponse());

        List<PaymentInfoRequest> paymentInfoRequestList = new ArrayList<>();
        PaymentInfoRequest paymentInfoRequest = new PaymentInfoRequest()
                .creditorTaxId(creditorTaxId)
                .noticeCode(noticeCode);
        paymentInfoRequestList.add(paymentInfoRequest);

        log.info("Messaggio json da allegare: " + paymentInfoRequest);

        try {
            invioNotificheB2bSteps.setPaymentInfoResponse(invioNotificheB2bSteps.getPnPaymentInfoClientImpl().getPaymentInfoV21(paymentInfoRequestList));
            log.info("Informazioni sullo stato del Pagamento: " + invioNotificheB2bSteps.getPaymentInfoResponse());

            assertThat(invioNotificheB2bSteps.getPaymentInfoResponse())
                    .as("La risposta del pagamento non dovrebbe essere nulla")
                    .isNotNull();

            if (status != null && errorCode == null) {
                assertThat(invioNotificheB2bSteps.getPaymentInfoResponse().get(0).getStatus().getValue())
                        .as("Lo stato nella risposta dovrebbe essere uguale a " + status, status)
                        .isEqualToIgnoringCase(status);
            } else if (status == null && errorCode != null) {
                assertThat(invioNotificheB2bSteps.getPaymentInfoResponse().get(0).getErrorCode())
                        .as("Il codice errore nella risposta dovrebbe essere uguale a '%s'", errorCode)
                        .isEqualToIgnoringCase(errorCode);
            }
        } catch (AssertionError assertionError) {
            String message = assertionError.getMessage() +
                    "{Informazioni sullo stato del Pagamento: " + (invioNotificheB2bSteps.getPaymentInfoResponse() == null ? "NULL" : invioNotificheB2bSteps.getPaymentInfoResponse()) + " }";
            throw new AssertionError(message, assertionError);
        }
    }

}
