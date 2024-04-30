package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.service.impl.PnGPDClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaymentInfoClientImpl;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.gpd.model.PaymentOptionModel;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.gpd.model.PaymentPositionModel;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.gpd.model.TransferModel;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.payment_info.model.*;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.utils.DataTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Slf4j
public class AsyncSteps {
    @Value("${pn.external.costo_base_notifica}")
    private Integer costoBaseNotifica;
    private final SharedSteps sharedSteps;
    private final PnGPDClientImpl pnGPDClientImpl;
    private final AvanzamentoNotificheB2bSteps avanzamentoNotificheB2bSteps;
    private final PnPaymentInfoClientImpl pnPaymentInfoClientImpl;
    private final List<PaymentPositionModel> paymentPositionModel;
    private List<PaymentInfoV21> paymentInfoResponse;
    private String deleteGDPresponse;
    private Integer amountGPD;
    private final List<Integer> amountNotifica;
    private static final Integer NUM_CHECK_PAYMENT_INFO = 32;
    private static final Integer WAITING_PAYMENT_INFO = 1000;


    @Autowired
    public AsyncSteps(AvanzamentoNotificheB2bSteps avanzamentoNotificheB2bSteps, SharedSteps sharedSteps) {
        this.avanzamentoNotificheB2bSteps = avanzamentoNotificheB2bSteps;
        this.sharedSteps = sharedSteps;
        this.pnGPDClientImpl = sharedSteps.getPnGPDClientImpl();
        this.pnPaymentInfoClientImpl =sharedSteps.getPnPaymentInfoClientImpl();
        this.paymentPositionModel= new ArrayList<>();
        this.amountNotifica= new ArrayList<>();
    }

    private String generateRandomIuv(){
        int randomSleepToRandomize = new Random().nextInt(100);
        try {
            Thread.sleep(randomSleepToRandomize);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String threadNumber = (Thread.currentThread().getId()+"");
        String numberOfThread = threadNumber.length() < 2 ? "0"+threadNumber: threadNumber.substring(0, 2);
        String timeNano = System.nanoTime()+"";
        String iuv = String.format("47%13s44", (numberOfThread+timeNano).substring(0,13));
        log.info("Iuv generato: " + iuv);
        return iuv;
    }

    @And("viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore {string} e amount {string} per {string} con (CF)(Piva) {string}")
    public void vieneCreataUnaPosizioneDebitoria(String organitationCode,String amount,String name,String taxId) {
        String iuv = generateRandomIuv();
        log.info("IUPD generate: " + organitationCode +"-64c8e41bfec846e04"+ iuv, System.currentTimeMillis());
        sharedSteps.addIuvGPD(iuv);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        PaymentPositionModel paymentPositionModelSend = new PaymentPositionModel()
                .iupd(String.format(organitationCode+"-64c8e41bfec846e04"+  iuv, System.currentTimeMillis()))
                .type(PaymentPositionModel.TypeEnum.F)
                .companyName("Automation")
                .fullName(name)
                .fiscalCode(taxId)
                .addPaymentOptionItem(new PaymentOptionModel()
                        .iuv(iuv)
                        .amount(Long.parseLong(amount))
                        .description("Test Automation")
                        .isPartialPayment(false)
                        .dueDate(new StringBuilder(dateTimeFormatter.format(OffsetDateTime.now().plusDays(2))))
                        .retentionDate(new StringBuilder(dateTimeFormatter.format(OffsetDateTime.now().plusDays(2))))
                        .addTransferItem(new TransferModel()
                                .idTransfer(TransferModel.IdTransferEnum._1)
                                .organizationFiscalCode(organitationCode)
                                .amount(Long.parseLong(amount))
                                .remittanceInformation("Test Automation")
                                .category("9/0301100TS/")
                                .iban("IT30N0103076271000001823603")));

        log.info("Request: " + paymentPositionModelSend.toString());
        amountNotifica.add(Integer.parseInt(amount));
        try {
            Assertions.assertDoesNotThrow(() -> {
                paymentPositionModel.add(pnGPDClientImpl.createPosition(organitationCode, paymentPositionModelSend, null, true));});
            Assertions.assertNotNull(paymentPositionModel);
            Assertions.assertNotNull(amountNotifica);
            log.info("Request: " + paymentPositionModel);
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{la posizione debitoria " + (paymentPositionModel == null ? "NULL" : paymentPositionModel.toString()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @And("lettura amount posizione debitoria per la notifica corrente di {string}")
    public void letturaAmountPosizioneDebitoria(String user) {
        PaymentPositionModel postionUser = new PaymentPositionModel();
        for(PaymentPositionModel position: paymentPositionModel){
            if(position.getFullName().equalsIgnoreCase(user)){
                postionUser=position;
            }
        }

        List<PaymentInfoRequest> paymentInfoRequestList= new ArrayList<PaymentInfoRequest>();
        PaymentInfoRequest paymentInfoRequest = new PaymentInfoRequest()
                .creditorTaxId(Objects.requireNonNull(Objects.requireNonNull(postionUser.getPaymentOption()).get(0).getTransfer()).get(0).getOrganizationFiscalCode())
                .noticeCode("3"+postionUser.getPaymentOption().get(0).getIuv());
        paymentInfoRequestList.add(paymentInfoRequest);

        log.info("User: " + postionUser);
        log.info("Messaggio json da allegare: " + paymentInfoRequest);
        //TODO utilizzare algoritmo di polling
        for(int i=0; i< NUM_CHECK_PAYMENT_INFO ;i++) {
            try {
                Assertions.assertDoesNotThrow(() -> {
                    paymentInfoResponse = pnPaymentInfoClientImpl.getPaymentInfoV21(paymentInfoRequestList);
                    log.info("Risposta recupero posizione debitoria: " + paymentInfoResponse.toString());});
                Assertions.assertNotNull(paymentInfoResponse);
                if(amountGPD != paymentInfoResponse.get(0).getAmount()){
                    amountGPD = paymentInfoResponse.get(0).getAmount();
                    break;
                }
                try {
                    Thread.sleep(WAITING_PAYMENT_INFO);
                } catch (InterruptedException exc) {
                    throw new RuntimeException(exc);
                }
            } catch (AssertionFailedError assertionFailedError) {
                String message = assertionFailedError.getMessage() +
                        "{la posizione debitoria " + (paymentInfoResponse == null ? "NULL" : paymentInfoResponse.toString()) + " }";
                throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
            }
        }
    }

    @And("lettura amount posizione debitoria per pagamento {int}")
    public void letturaAmountPosizioneDebitoria(Integer pagamento) {
        PaymentPositionModel postionUser = paymentPositionModel.get(pagamento);
        List<PaymentInfoRequest> paymentInfoRequestList= new ArrayList<PaymentInfoRequest>();
        PaymentInfoRequest paymentInfoRequest = new PaymentInfoRequest()
                .creditorTaxId(Objects.requireNonNull(Objects.requireNonNull(postionUser.getPaymentOption()).get(0).getTransfer()).get(0).getOrganizationFiscalCode())
                .noticeCode("3"+postionUser.getPaymentOption().get(0).getIuv());

        paymentInfoRequestList.add(paymentInfoRequest);
        log.info("User: " + postionUser);
        log.info("Messaggio json da allegare: " + paymentInfoRequest);

        try {
            Assertions.assertDoesNotThrow(() -> {
                paymentInfoResponse= pnPaymentInfoClientImpl.getPaymentInfoV21(paymentInfoRequestList);
                log.info("Risposta recupero posizione debitoria: " + paymentInfoResponse.toString());});
            Assertions.assertNotNull(paymentInfoResponse);
            amountGPD=paymentInfoResponse.get(0).getAmount();
            Assertions.assertNotNull(amountGPD);
        } catch (AssertionFailedError assertionFailedError) {

            String message = assertionFailedError.getMessage() +
                    "{la posizione debitoria " + (paymentInfoResponse == null ? "NULL" : paymentInfoResponse.toString()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @And("viene cancellata la posizione debitoria di {string}")
    public void vieneCancellataLaPosizioneDebitoria(String user) {
        try {
            for(PaymentPositionModel position: paymentPositionModel){
                if(position.getFullName().equalsIgnoreCase(user)){
                    Assertions.assertDoesNotThrow(() -> {
                        deleteGDPresponse = pnGPDClientImpl.deletePosition(Objects.requireNonNull(Objects.requireNonNull(position.getPaymentOption()).get(0).getTransfer()).get(0).getOrganizationFiscalCode(), position.getIupd(), null);
                    });
                }
            }

            Assertions.assertNotNull(deleteGDPresponse);
            log.info("Risposta evento cancellazione: " + deleteGDPresponse);

        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{la posizione debitoria " + (deleteGDPresponse == null ? "NULL" : deleteGDPresponse) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }


    @And("viene cancellata la posizione debitoria del pagamento {int}")
    public void vieneCancellataLaPosizioneDebitoriaDelPagamento(Integer pagamento) {
        try {
            Assertions.assertDoesNotThrow(() -> {
                deleteGDPresponse = pnGPDClientImpl.deletePosition(Objects.requireNonNull(Objects.requireNonNull(paymentPositionModel.get(pagamento).getPaymentOption()).get(0).getTransfer()).get(0).getOrganizationFiscalCode(), paymentPositionModel.get(pagamento).getIupd(), null);
            });
            Assertions.assertNotNull(deleteGDPresponse);
            log.info("Risposta evento cancellazione: " + deleteGDPresponse);

        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{la posizione debitoria " + (deleteGDPresponse == null ? "NULL" : deleteGDPresponse) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @And("vengono cancellate le posizioni debitorie")
    public void vengonoCancellateLaPosizioniDebitorie() {
        try {
            for(PaymentPositionModel paymentPositionModelUser :paymentPositionModel) {
                Assertions.assertDoesNotThrow(() -> {
                    deleteGDPresponse = pnGPDClientImpl.deletePosition(Objects.requireNonNull(Objects.requireNonNull(paymentPositionModelUser.getPaymentOption()).get(0).getTransfer()).get(0).getOrganizationFiscalCode(), paymentPositionModelUser.getIupd(), null);
                });
            }
            Assertions.assertNotNull(deleteGDPresponse);
            log.info("Risposta evento cancellazione: " + deleteGDPresponse);
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{la posizione debitoria " + (deleteGDPresponse == null ? "NULL" : deleteGDPresponse) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @And("viene effettuato il controllo del amount di GPD = {string}")
    public void vieneEffettuatoIlControlloDelAmountDiGPD(String amount) {
        try {
            log.info("Amount GPD: "+amountGPD);
            Assertions.assertEquals(amountGPD,Integer.parseInt(amount));
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{amountGPD " + (amountGPD == null ? "NULL" : amountGPD.toString()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @And("viene effettuato il controllo del amount di GPD con amount notifica del (utente)(pagamento) {int}")
    public void vieneEffettuatoIlControlloDelAmountDiGPDConAmountNotifica(Integer user) {
        try {
            Assertions.assertEquals(amountGPD,amountNotifica.get(user));
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{Amount GPD " + (amountGPD == null ? "NULL" : amountGPD.toString()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @Then("viene effettuato il controllo del cambiamento del amount nella timeline {string} del (utente)(pagamento) {int}")
    public void vieneEffettuatoIlControlloDelCambiamentoDelAmount(String timelineEventCategory,Integer user) {
        TimelineElementV23 timelineElement = sharedSteps.getTimelineElementByEventId(timelineEventCategory,null);
        int analogCost = Objects.requireNonNull(Objects.requireNonNull(timelineElement.getDetails()).getAnalogCost());
        amountNotifica.set(user,amountNotifica.get(user) + analogCost);
        try {
            Assertions.assertEquals(amountGPD,amountNotifica.get(user));
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{timelineElement  " + timelineElement.toString() + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());

        }
    }

    @Then("viene effettuato il controllo del cambiamento del amount nella timeline {string} del (utente)(pagamento) {int} (al tentativo):")
    public void vieneEffettuatoIlControlloDelCambiamentoDelAmountAlTentativo(String timelineEventCategory,Integer user,@Transpose DataTest dataFromTest ) {
        TimelineElementV23 timelineElement = sharedSteps.getTimelineElementByEventId(timelineEventCategory,dataFromTest);
        int analogCost = Objects.requireNonNull(Objects.requireNonNull(timelineElement.getDetails()).getAnalogCost());
        amountNotifica.set(user, amountNotifica.get(user) + analogCost);
        try {
            Assertions.assertEquals(amountGPD,amountNotifica.get(user));
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{timelineElement " + timelineElement + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @Then("viene effettuato il controllo dell'aggiornamento del costo totale del utente {int}")
    public void vieneEffettuatoIlControlloDelCambiamentoDelCostoTotale(Integer user) {
        try {
            log.info("Costo base presente su Notifica"+amountNotifica.get(user));
            log.info("Costo base presente su GPD"+amountGPD);
            Assertions.assertEquals(amountGPD,amountNotifica.get(user));
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{Amount GPD " + (amountGPD == null ? "NULL" : amountGPD.toString()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    //dopo accettato amount_gpd + 100 (costo base) + pafee
    //Ogni elemento di timeline analogico ha un analog cost per ogni elemento va verificato che aumenti di  + analog_cost.
    //se riufiutata amount_gpd
    @Then("viene verificato il costo finale della notifica amount_gpd + costo_base + pafee + analog_cost per ogni elemento di timeline")
    public void vieneVerificatoIlCostoFinaleDellaNotificaAmount_gpdCosto_basePafeeAnalog_costPerOgniElementoDiTimeline() {
        FullSentNotificationV23 sentNotification = sharedSteps.getSentNotification();
        Integer costoTotale = getCostoTotale(sentNotification);

        String creditorTaxId = Assertions.assertDoesNotThrow(()-> Objects.requireNonNull(Objects.requireNonNull(sentNotification.getRecipients().get(0).getPayments()).get(0).getPagoPa()).getCreditorTaxId());
        String noticeCode = Assertions.assertDoesNotThrow(()-> Objects.requireNonNull(Objects.requireNonNull(sentNotification.getRecipients().get(0).getPayments()).get(0).getPagoPa()).getNoticeCode());
        Assertions.assertNotNull(creditorTaxId);
        Assertions.assertNotNull(noticeCode);

        PaymentInfoRequest paymentInfoRequest = new PaymentInfoRequest()
                .creditorTaxId(creditorTaxId)
                .noticeCode(noticeCode);

        paymentInfoResponse = Assertions.assertDoesNotThrow(() -> pnPaymentInfoClientImpl.getPaymentInfoV21(Collections.singletonList(paymentInfoRequest)));
        Assertions.assertNotNull(paymentInfoResponse);
        System.out.println("Costo totale previsto: {}"+costoTotale);
        System.out.println("Costo attuale su gpd: {}"+paymentInfoResponse.get(0).getAmount());
        Assertions.assertEquals(costoTotale,paymentInfoResponse.get(0).getAmount());
    }

    @And("viene aggiunto il costo della notifica totale del utente {int}")
    public void vieneAggiuntoIlCostoDellaNotificaTotaleAlUtente(Integer user) {
        try {
            for(int i=0;i<amountNotifica.size();i++) {
                Assertions.assertDoesNotThrow(() -> {
                    int amount = Objects.requireNonNull(Objects.requireNonNull(sharedSteps.getSentNotification()).getAmount());
                    int paFee = Objects.requireNonNull(Objects.requireNonNull(sharedSteps.getSentNotification()).getPaFee());
                    amountNotifica.set(user, amount + paFee);
                });
            }
            Assertions.assertNotNull(amountNotifica.get(user));
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{amountNotifica " + amountNotifica + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @And("viene aggiunto il costo della notifica totale")
    public void vieneAggiuntoIlCostoDellaNotificaTotale() {
        try {
            for(int i=0;i<amountNotifica.size();i++) {
                int paFee = Objects.requireNonNull(Objects.requireNonNull(sharedSteps.getSentNotification()).getPaFee());
                int costototale = costoBaseNotifica + paFee;
                log.info("Amount+costo base:"+costototale);
                amountNotifica.set(i, amountNotifica.get(i) + costototale);
            }
            Assertions.assertNotNull(amountNotifica);
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{amountNotifica " + amountNotifica + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @And("viene effettuato il controllo del amount di GPD con il costo {string} della notifica con iva inclusa")
    public void vieneEffettuatoIlControlloDelAmountDiGPDConCostoTotaleConIva(String tipoCosto ) {
        try {
            log.info("Amount GPD: "+amountGPD);
            amountGPD= amountGPD - Integer.parseInt(String.valueOf(Objects.requireNonNull(paymentPositionModel.get(0).getPaymentOption()).get(0).getAmount()));
            avanzamentoNotificheB2bSteps.priceVerificationV23(amountGPD,null,0,tipoCosto);
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{Amount GPD " + (amountGPD == null ? "NULL" : amountGPD.toString()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    private Integer getCostoTotale(FullSentNotificationV23 sentNotification) {
        Integer analogCost = 0;
        for(TimelineElementV23 timelineElem: sentNotification.getTimeline()){
            Integer currentCost = timelineElem.getDetails() == null ? Integer.valueOf(0) : timelineElem.getDetails().getAnalogCost();
            if(currentCost!= null && currentCost > 0)analogCost+=currentCost;
        }
        Integer paFee = sentNotification.getPaFee();
        Integer costoBaseSend = this.costoBaseNotifica;
        Integer amountGpd = amountNotifica.get(0);
        return amountGpd + costoBaseSend + (paFee == null ? 0 : paFee) + analogCost;
    }
}