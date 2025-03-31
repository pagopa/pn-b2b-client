package it.pagopa.pn.cucumber.steps.pa.notificationVersions;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface NotificationStepsInterface {

    default void throwUnsupportedMethodException(String methodName) {
        methodName = methodName == null ? "" : methodName + " ";
        throw new RuntimeException("Metodo " + methodName + "non previsto per la versione " + getVersionString());
    }

    String getVersionString();

    void prepareNotificationRequest(Map<String, String> data);

    void prepareNotificationRequestSimileAllaPrecedente(boolean isCreditorTaxIdUguale, boolean isCodiceAvvisoUguale, boolean isPaProtocolNumberUguale, String idempotenceToken);

    void resetNotificationRequest();

    void addRecipientToNotification(Destinatario destinatario, Map<String, String> data);

    void addRecipientToNotificationSpecialCondition(Destinatario destinatario, Map<String, String> data, String condition, Integer otherRecipientIndex);

    void setSenderTaxId(String senderTaxId);

    String getNotificationRequestGroup();

    void setNotificationRequestGroup(String group);

    String sendNotification(int wait, String status, String pollingStrategy);

    Object uploadNotification() throws IOException;

    void setIuvToRecipient(Integer posizione, String iuvGPD);

    void addDocumentItems(int numAllegati);

    void performPriceVerification(String price, String date, Integer destinatario);

    void uploadNotificationAllegatiUgualiPagamento() throws IOException;

    void addIuvGdpToDestinatario(String denominazione, String iuvGdp, Integer paymentIndex);

    List<String> getDatiPagamento(String iun, Integer destinatario, Integer pagamento);

    void waitForTimelineElement(String iun, String timelineElementCategory, Integer attempts);

    String getNotificationRequestId();

    void getNotificationRequestStatus(String requestId);

    void checkTaxonomyCode();

    int getRecipientsSize();

    String getRecipientNoticeCode(int recipientIndex, int paymentIndex);

    String getRecipientCreditorTaxId(int recipientIndex, int paymentIndex);
}
