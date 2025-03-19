package it.pagopa.pn.cucumber.steps.pa.notificationVersions;

import java.io.IOException;
import java.util.Map;

public interface NotificationStepsInterface {

    default void throwUnsupportedMethodException(String methodName) {
        methodName = methodName == null ? "" : methodName + " ";
        throw new RuntimeException("Metodo " + methodName + "non previsto per la versione " + getVersionString());
    }

    String getVersionString();

    String getNotificationSentIun();

    void setNotificationRequest(Map<String, String> data);

    void addRecipitentToNotification(String recipientName, Map<String, String> data);

    void setSenderTaxId(String senderTaxId);

    String getNotificationRequestGroup();

    void setNotificationRequestGroup(String group);

    void retrieveFullSentNotification(String iun);

    Object retrieveNotificationRequest();

    Object retrieveNotificationResponse();

    void sendNotification(int wait, String status, String pollingStrategy);

    Object uploadNotification() throws IOException;

    void setIuvToRecipient(Integer posizione, String iuvGPD);

    void addDocumentItems(int numAllegati);

    void performPriceVerification(String price, String date, Integer destinatario);

    default void uploadNotificationAllegatiUgualiPagamento() throws IOException {
        throwUnsupportedMethodException("uploadNotificationAllegatiUgualiPagamento");
    }
}
