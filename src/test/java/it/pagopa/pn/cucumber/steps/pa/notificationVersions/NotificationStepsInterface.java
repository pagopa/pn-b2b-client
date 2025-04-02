package it.pagopa.pn.cucumber.steps.pa.notificationVersions;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface NotificationStepsInterface {

    default void throwUnsupportedMethodException(String methodName) {
        methodName = methodName == null ? "" : methodName + " ";
        throw new RuntimeException("Metodo " + methodName + "non previsto per la versione " + getVersionString());
    }

    Object getSentNotificationAnyVersion();

    String getVersionString();

    String getNotificationSentIun();

    void prepareNotificationRequest(Map<String, String> data);

    void addRecipientToNotification(Destinatario destinatario, Map<String, String> data);

    void setSenderTaxId(String senderTaxId);

    String getNotificationRequestGroup();

    void setNotificationRequestGroup(String group);

    Object retrieveNotificationRequest();

    Object retrieveNotificationResponse();

    void sendNotification(int wait, String status, String pollingStrategy);

    Object uploadNotification() throws IOException;

    void setIuvToRecipient(Integer posizione, String iuvGPD);

    void addDocumentItems(int numAllegati);

    void performPriceVerification(String price, String date, Integer destinatario);

    void uploadNotificationAllegatiUgualiPagamento() throws IOException;

    void addIuvGdpToDestinatario(String denominazione, String iuvGdp, Integer posizione);

    List<String> getDatiPagamento(Integer destinatario, Integer pagamento);
}
