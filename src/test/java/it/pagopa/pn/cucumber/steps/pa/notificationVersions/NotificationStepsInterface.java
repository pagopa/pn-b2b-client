package it.pagopa.pn.cucumber.steps.pa.notificationVersions;

import it.pagopa.pn.client.b2b.pa.domain.Destinatario;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface NotificationStepsInterface {

    /**
     * Ogni classe che implementa NotificationStepsInterface nella propria implementazione dovrà restituire
     * la fullSentNotification relativa alla propria versione ottenuta chiamando il b2b client.
     * Vi dovrà poi essere un metodo PRIVATO getFullSentNotificationVersioned che restituisce l'oggetto
     * castato alla classe corrispondente alla versione in uso.
     */
    Object getFullSentNotification();

    void prepareNotificationRequest(Map<String, String> data);

    void prepareNotificationRequestSimileAllaPrecedente(boolean isCreditorTaxIdUguale, boolean isCodiceAvvisoUguale, boolean isPaProtocolNumberUguale, String idempotenceToken);

    void resetNotificationRequest();

    void addRecipientToNotification(Destinatario destinatario, Map<String, String> data);

    /**
     * Metodo che lascia spazio di manovra per poter creare recipient customizzati a seconda delle esigenze
     */
    void addRecipientToNotificationSpecialCondition(Destinatario destinatario, Map<String, String> data, String condition, Integer otherRecipientIndex);

    void setSenderTaxId(String senderTaxId);

    String getSenderTaxId();

    String getNotificationRequestGroup();

    void setNotificationRequestGroup(String group);

    void sendNotification(int wait, String status, String pollingStrategy);

    /**
     * Metodo chiave, in quanto è qui che viene valorizzato lo IUN della notifica generata che viene poi salvato in SharedSteps
     * su cui poggia la quasi totalità delle logiche dell'applicativo
     *
     * @param errorType da passare sempre a null, tranne quando viene richiamato dal metodo
     * @see #createAndSendNotificationRequestWithError, (che deve portare al REFUSED in fase di validazione asincrona)
     * in modo da modificare la request in accordo al tipo di errore che si intende generare
     */
    Object uploadNotification(String errorType) throws IOException;

    void setIuvToRecipient(Integer posizione, String iuvGPD);

    void addDocumentItems(int numAllegati);

    //TODO MATTEO: inutilizzata, ma serviva a qualcosa, ricordati cosa sostituire
    void performPriceVerification(String price, String date, Integer destinatario);

    void uploadNotificationAllegatiUgualiPagamento() throws IOException;

    void addIuvGpdToDestinatario(String denominazione, String iuvGpd, Integer paymentIndex);

    void addIuvGpdToDestinatario(Integer recIndex, String iuvGpd, Integer recipientPaymentIndex);

    List<String> getDatiPagamento(Integer destinatario, Integer pagamento);

    void waitForTimelineElement(String timelineElementCategory, Integer attempts);

    void getNotificationRequestStatus(String requestId);

    void checkTaxonomyCode();

    int getRecipientsSize();

    String getRecipientNoticeCode(int recipientIndex, int paymentIndex);

    String getRecipientCreditorTaxId(int recipientIndex, int paymentIndex);

    void produceEvidence();

    void verifyCorrectAcquisition();

    void verifyStatus(boolean withNotificationRequestId, boolean withPaProtocolNumber, boolean withIdempotenceToken);

    void verifyNotification(String notificationIun);

    void createAndSendNotificationRequestWithError(String errorType);

    String getCreditorTaxId(int recipientIndex);

    String getNoticeCode(int recipientIndex);

    void setApplyCostFalse(int recipientIndex, int paymentIndex);
}
