package it.pagopa.pn.cucumber.steps.pa.notificationVersions;

import it.pagopa.pn.cucumber.steps.utilitySteps.Destinatario;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface NotificationStepsInterface {

    void prepareNotificationRequest(Map<String, String> data);

    void prepareNotificationRequestSimileAllaPrecedente(boolean isCreditorTaxIdUguale, boolean isCodiceAvvisoUguale, boolean isPaProtocolNumberUguale, String idempotenceToken);

    void resetNotificationRequest();

    void addRecipientToNotification(Destinatario destinatario, Map<String, String> data);

    /**
     * Metodo che lascia spazio di manovra per poter creare recipient customizzati a seconda delle esigenze
     */
    void addRecipientToNotificationSpecialCondition(Destinatario destinatario, Map<String, String> data, String condition, Integer otherRecipientIndex);

    void setSenderTaxId(String senderTaxId);

    String getNotificationRequestGroup();

    void setNotificationRequestGroup(String group);

    String sendNotification(int wait, String status, String pollingStrategy);

    /**
     * Metodo chiave, in quanto è qui che viene valorizzato lo IUN della notifica generata che viene poi salvato in SharedSteps
     * su cui poggia la quasi totalità delle logiche dell'applicativo
     *
     * @param isRegularUpload da passare sempre a true: il solo caso in cui viene passato false, è quando viene richiamato dal metodo
     * @see #createAndSendNotificationRequestWithError, (che deve portare al REFUSED in fase di validazione asincrona)
     * in modo che salti il preload di documenti e altre operazioni che vengono invece regolarmente effettuate quando il parametro è true
     */
    Object uploadNotification(boolean isRegularUpload) throws IOException;

    void setIuvToRecipient(Integer posizione, String iuvGPD);

    void addDocumentItems(int numAllegati);

    void performPriceVerification(String price, String date, Integer destinatario);

    void uploadNotificationAllegatiUgualiPagamento() throws IOException;

    void addIuvGpdToDestinatario(String denominazione, String iuvGpd, Integer paymentIndex);

    List<String> getDatiPagamento(String iun, Integer destinatario, Integer pagamento);

    void waitForTimelineElement(String iun, String timelineElementCategory, Integer attempts);

    void getNotificationRequestStatus(String requestId);

    void checkTaxonomyCode();

    int getRecipientsSize();

    String getRecipientNoticeCode(int recipientIndex, int paymentIndex);

    String getRecipientCreditorTaxId(int recipientIndex, int paymentIndex);

    void produceEvidence();

    void verifyCorrectAcquisition();

    void verifyStatus(boolean withNotificationRequestId, boolean withPaProtocolNumber, boolean withIdempotenceToken);

    void verifyNotification(String notificationIun);

    void createAndSendNotificationRequestWithError(String errorType, Boolean isWithoutUpload);

    String getCreditorTaxId(int recipientIndex);

    String getNoticeCode(int recipientIndex);
}
