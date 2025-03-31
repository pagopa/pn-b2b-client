package it.pagopa.pn.cucumber.steps.pa.invioNotificheVersions;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.payment.PaymentInfoRequest;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NewNotificationRequestStatusResponseV23;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NewNotificationRequestV24;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NewNotificationResponse;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.InvioNotificheB2bSteps;
import it.pagopa.pn.cucumber.steps.pa.notificationVersions.NotificationStepsV24;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@Slf4j
@Data
public class InvioNotificheStepsV24 implements InvioNotificheStepsInterface {

    private InvioNotificheB2bSteps invioNotificheB2bSteps;
    private final SharedSteps.NotificationVersion version;

    public InvioNotificheStepsV24(InvioNotificheB2bSteps invioNotificheB2bSteps) {
        version = SharedSteps.NotificationVersion.V24;
        this.invioNotificheB2bSteps = invioNotificheB2bSteps;
    }

    private NotificationStepsV24 getNotificationStep() {
        return (NotificationStepsV24) invioNotificheB2bSteps.getSharedSteps().getMapOfVersionSteps().get(SharedSteps.NotificationVersion.V24);
    }

    @Override
    public void evidenceProduce() {
        NewNotificationResponse newNotificationResponse = getNotificationStep().getNotificationResponse();
        assertThat(newNotificationResponse)
                .as("La risposta della nuova notifica non dovrebbe essere nulla")
                .isNotNull();
        log.info("METADATI: " + '\n' + newNotificationResponse);
        log.info("REQUEST-ID: " + '\n' + newNotificationResponse.getNotificationRequestId());
    }

    @Override
    public void verifyCorrectAcquisition() {
        NewNotificationResponse newNotificationResponse = getNotificationStep().getNotificationResponse();
        assertSoftly(softly -> {
            softly.assertThat(newNotificationResponse)
                    .as("La risposta della nuova notifica non dovrebbe essere nulla")
                    .isNotNull();

            softly.assertThat(newNotificationResponse)
                    .as("L'ID della richiesta di notifica non dovrebbe essere nullo")
                    .isNotNull();

            softly.assertThat(invioNotificheB2bSteps.getB2bClient().getNotificationRequestStatusV24(newNotificationResponse.getNotificationRequestId()))
                    .as("Lo stato della richiesta di notifica non dovrebbe essere nullo.",
                            newNotificationResponse.getNotificationRequestId())
                    .isNotNull();
        });
    }

    @Override
    public void verifyStatus(boolean withNotificationRequestId, boolean withPaProtocolNumber, boolean withIdempotenceToken) {
        NewNotificationResponse newNotificationResponse = getNotificationStep().getNotificationResponse();
        String notificationRequestId = withNotificationRequestId ? newNotificationResponse.getNotificationRequestId() : null;
        String paProtocolNumber = withPaProtocolNumber ? newNotificationResponse.getPaProtocolNumber() : null;
        String idempotenceToken = withIdempotenceToken ? newNotificationResponse.getIdempotenceToken() : null;

        NewNotificationRequestStatusResponseV23 newNotificationRequestStatusResponse = Assertions.assertDoesNotThrow(() ->
                invioNotificheB2bSteps.getB2bClient().getNotificationRequestStatusAllParam(notificationRequestId, paProtocolNumber, idempotenceToken));
        assertThat(newNotificationRequestStatusResponse.getNotificationRequestStatus())
                .as("Lo stato della richiesta di notifica non dovrebbe essere nullo")
                .isNotNull();
        log.debug(newNotificationRequestStatusResponse.getNotificationRequestStatus());
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
