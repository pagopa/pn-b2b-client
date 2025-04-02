package it.pagopa.pn.cucumber.steps.pa.invioNotificheVersions;

public interface InvioNotificheStepsInterface {

    void verificaStatoPagamentoNotifica(String status, String errorCode, String creditorTaxId, String noticeCode);
}
