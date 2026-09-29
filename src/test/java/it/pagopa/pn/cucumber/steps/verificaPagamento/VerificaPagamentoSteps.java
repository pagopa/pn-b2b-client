package it.pagopa.pn.cucumber.steps.verificaPagamento;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.payment.BffPaymentInfoItem;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.payment.PaymentInfoRequest;
import it.pagopa.pn.client.b2b.pa.service.IPnPaymentInfoClient;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.client.b2b.pa.domain.Destinatario;
import it.pagopa.pn.cucumber.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static it.pagopa.pn.cucumber.utils.NotificationValue.*;

@Slf4j
public class VerificaPagamentoSteps {

    public static final String NOTICE_CODE_COUNT_CSV_PATH = "it/pagopa/pn/cucumber/verificaPagamento/notice_code_counter.csv";

    private final IPnPaymentInfoClient pnPaymentInfoClient;
    private final SharedSteps sharedSteps;
    private String noticeCodeSuffix;
    private String noticeCode;
    private String creditorTaxId;

    @Autowired
    public VerificaPagamentoSteps(SharedSteps sharedSteps) {
        this.sharedSteps = sharedSteps;
        pnPaymentInfoClient = this.sharedSteps.getPnPaymentInfoClientImpl();
    }


    @And("destinatario pagatore {destinatario} e:")
    public void generaDestinatarioNotifica(Destinatario destinatario, DataTable dataTable) {
        Map<String, String> data = new HashMap<>(dataTable.asMap());

        String noticeCodeSuffix = getValue(data, PAYMENT_NOTICE_CODE.key);
        String creditorTaxId = getValue(data, PAYMENT_CREDITOR_TAX_ID.key);

        if (noticeCodeSuffix == null || noticeCodeSuffix.isEmpty()) {
            throw new IllegalArgumentException("La chiave 'payment_noticeCode' è obbligatoria ma non è presente nella tabella.");
        }

        if (creditorTaxId == null || noticeCodeSuffix.isEmpty()) {
            throw new IllegalArgumentException("La chiave 'payment_creditorTaxId' è obbligatoria ma non è presente nella tabella.");
        }

        String noticeCode = generateUniqueRecipentPaymentNoticeCode(noticeCodeSuffix);
        this.noticeCode = noticeCode;
        this.noticeCodeSuffix = noticeCodeSuffix;
        this.creditorTaxId = creditorTaxId;

        data.put(PAYMENT_NOTICE_CODE.key, noticeCode);
        sharedSteps.addDestinatarioWithParams(destinatario, data);
    }

    @When("la notifica viene inviata dal {string} e si attende che lo stato diventi ACCEPTED")
    public void laNotificaVieneInviataOk(String paType) {
        log.info("Invio notifica con noticeCode {} e creditorTaxId {}", noticeCode, creditorTaxId);

        try {
            sharedSteps.sendNotification(paType, "ACCEPTED");
            increaseSuffixCount(this.noticeCodeSuffix);
        } catch (AssertionFailedError e) {
            Throwable cause = e.getCause();
            if (cause instanceof HttpClientErrorException.Conflict) {
                increaseSuffixCount(this.noticeCodeSuffix);
                log.error("Conflict 409: Notifica con noticeCode \"{}\" e creditorTaxId \"{}\" già presente", this.noticeCode, this.creditorTaxId);
            }
            throw e;
        }
    }

    @Then("il servizio di checkout restituisce:")
    public void verificaElaborazioneCheckout(io.cucumber.datatable.DataTable dataTable) throws Exception {
        Map<String, String> data = dataTable.asMap();
        log.info("Inizio verifica elaborazione checkout per noticeCode={} e creditorTaxId={}", this.noticeCode, this.creditorTaxId);

        BffPaymentInfoItem currentStatus = getPaymentStatus(this.noticeCode, creditorTaxId);
        log.info("Stato attuale ricevuto da getPaymentStatus: {}", currentStatus);

        for (Map.Entry<String, String> entry : data.entrySet()) {
            String fieldName = entry.getKey();
            String expectedValue = entry.getValue();

            try {
                Field field = BffPaymentInfoItem.class.getDeclaredField(fieldName);
                field.setAccessible(true);

                Object fieldValue = field.get(currentStatus);
                String actualValue = fieldValue != null ? fieldValue.toString() : null;

                log.info("Verifica campo '{}': atteso='{}', ricevuto='{}'", fieldName, expectedValue, actualValue);

                Assertions.assertEquals(
                        expectedValue,
                        actualValue,
                        String.format(
                                "Campo \"%s\" ricevuto (%s) diverso da quello atteso (%s) per noticeCode %s e creditorTaxId %s",
                                fieldName, actualValue, expectedValue, this.noticeCode, this.creditorTaxId
                        )
                );
            } catch (NoSuchFieldException e) {
                log.error("Campo '{}' non trovato nella classe BffPaymentInfoItem", fieldName, e);
                throw new IllegalArgumentException(String.format("Campo \"%s\" non esiste nella classe BffPaymentInfoItem", fieldName), e);
            } catch (IllegalAccessException e) {
                log.error("Accesso al campo '{}' fallito su BffPaymentInfoItem", fieldName, e);
                throw e;
            }
        }
    }

    private String generateUniqueRecipentPaymentNoticeCode(String suffix) {

        if (suffix == null || !suffix.matches("\\d+")) {
            throw new IllegalArgumentException("La root deve essere una stringa numerica. Ricevuto: " + suffix);
        }

        int lunghezzaResiduaNoticeCode = 18 - suffix.length();

        String noticeCount = getNoticeCountBySuffix(suffix);
        lunghezzaResiduaNoticeCode -= noticeCount.length();

        return suffix + "0".repeat(lunghezzaResiduaNoticeCode) + noticeCount;
    }

    private String getNoticeCountBySuffix(String noticeSuffix) {
        List<List<String>> righe = FileUtils.readCsvSafe(NOTICE_CODE_COUNT_CSV_PATH, ",", true);
        Map<String, String> mappaNotice = new HashMap<>();

        for (List<String> riga : righe) {
            if (riga.size() >= 2) {
                String key = riga.get(0).trim();
                String value = riga.get(1).trim();
                mappaNotice.put(key, value);
            }
        }

        // Stampa la mappa per verifica
        System.out.println("Mappa chiave-valore dal CSV:");
        for (Map.Entry<String, String> entry : mappaNotice.entrySet()) {
            System.out.println("Key: " + entry.getKey() + " | Value: " + entry.getValue());
        }

        if (!mappaNotice.containsKey(noticeSuffix))
            throw new IllegalArgumentException("Suffisso \"" + noticeSuffix + "\" non trovato nel file notice_code_counter.csv");

        return mappaNotice.get(noticeSuffix);

    }

    private BffPaymentInfoItem getPaymentStatus(String noticeCode, String creditorTaxId) {
        List<PaymentInfoRequest> paymentInfoRequests = new ArrayList<>();
        PaymentInfoRequest paymentInfoRequest = new PaymentInfoRequest();

        paymentInfoRequest.creditorTaxId(creditorTaxId);
        paymentInfoRequest.noticeCode(noticeCode);
        paymentInfoRequests.add(paymentInfoRequest);

        List<BffPaymentInfoItem> infoList = pnPaymentInfoClient.getPaymentInfoV21(paymentInfoRequests);

        Assertions.assertNotNull(infoList, "La lista di informazioni di pagamento è nulla");
        Assertions.assertFalse(infoList.isEmpty(), "La lista di informazioni di pagamento è vuota");
        Assertions.assertEquals(1, infoList.size(), "La lista dovrebbe contenere esattamente un elemento");

        return infoList.get(0);
    }

    public void increaseSuffixCount(String noticeCodeSuffix) {
        FileUtils.modifyCsvSafe(NOTICE_CODE_COUNT_CSV_PATH, ",", righe -> {
            for (List<String> riga : righe) {
                if (!riga.isEmpty() && riga.get(0).equals(noticeCodeSuffix)) {
                    while (riga.size() < 2) {
                        riga.add("0");
                    }
                    long count = Long.parseLong(riga.get(1));
                    riga.set(1, String.valueOf(count + 1));
                    return righe;
                }
            }

            throw new IllegalArgumentException("Suffisso \"" + noticeCodeSuffix + "\" non trovato");
        });
    }
}
