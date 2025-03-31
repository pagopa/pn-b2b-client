package it.pagopa.pn.cucumber.steps.pa.invioNotificheVersions;

public interface InvioNotificheStepsInterface {

    void evidenceProduce();

    void verifyCorrectAcquisition();

    void verifyStatus(boolean withNotificationRequestId, boolean withPaProtocolNumber, boolean withIdempotenceToken);

    void verificaStatoPagamentoNotifica(String status, String errorCode, String creditorTaxId, String noticeCode);
}
