package it.pagopa.pn.cucumber.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.domain.DynamoTableName;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV29;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.PagoPaPayment;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.notificationcostservice.model.NewNotificationCostRequest;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.notificationcostservice.model.NotificationCostPaymentResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.notificationcostservice.model.NotificationCostRecipientResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.notificationcostservice.model.NotificationFeePolicy;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.notificationcostservice.model.PagoPaIntMode;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.notificationcostservice.model.PaymentData;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.notificationcostservice.model.RecipientCostData;
import it.pagopa.pn.client.b2b.pa.service.DynamoDbService;
import it.pagopa.pn.client.b2b.pa.service.IPnNotificationCostClient;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v26.NotificationProcessCostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.HttpStatusCodeException;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@Slf4j
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class CostiNotificaSteps {
    private final SharedSteps sharedSteps;
    private final DynamoDbService dynamoDbService;
    private final IPnNotificationCostClient notificationCostClient;
    private NotificationCostPaymentResponse notificationCostPaymentResponse;
    private NotificationCostRecipientResponse notificationCostRecipientResponse;


    @And("verifico che per il destinatario {int} il record su Pn-NotificationDeliveryCost sia stato (inserito)(modificato) e correttamente valorizzato")
    public void checkNotificationDeliveryCostRecord(int recIndex, Map<String, String> expectedData) {
        try {
            Map<String, AttributeValue> record = searchNotificationDeliveryCostRecord(recIndex);
            FullSentNotificationV29 fsn = sharedSteps.getSentNotificationLastVersion();
            //verifica che tutte le colonne siano valorizzate in modo coerente
            assertSoftly(softly -> {
                softly.assertThat(record.get("senderTaxId").s()).as("Il senderTaxId del record non coincide con quello della fullSentNotification").isEqualTo(fsn.getSenderTaxId());
                softly.assertThat(record.get("notificationFeePolicy").s()).as("Il notificationFeePolicy del record non coincide con quello della fullSentNotification").isEqualTo(fsn.getNotificationFeePolicy().getValue());
                softly.assertThat(record.get("pagoPaIntMode").s()).as("Il pagoPaIntMode del record non coincide con quello della fullSentNotification").isEqualTo(fsn.getPagoPaIntMode().getValue());
                softly.assertThat(record.get("vat").n()).as("Il campo vat del record non coincide con quello della fullSentNotification").isEqualTo(fsn.getVat().toString());
            });
            //se il record non è eliminato logicamente (isDeleted=true), recupera tramite API l'oggetto corrispondente al record per i check sui campi relativi ai costi;
            //in caso contrario, verifica che la get puntuale produca un errore 404
            try {
                notificationCostRecipientResponse = notificationCostClient.getNotificationCost(sharedSteps.getNotificationIun(), recIndex);
                log.info("NotificationCostRecipientResponse:\n {}", notificationCostRecipientResponse);
                String costoValorizzato = expectedData.get("costoValorizzato");
                if (costoValorizzato != null) {
                    switch (costoValorizzato) {
                        case "firstAnalogCost" ->
                                assertThat(record.get("firstAnalogCost")).as("Il record salvato su Pn-NotificationDeliveryCost dovrebbe avere il campo %s valorizzato", costoValorizzato).isNotNull();
                        case "secondAnalogCost" ->
                                assertThat(record.get("secondAnalogCost")).as("Il record salvato su Pn-NotificationDeliveryCost dovrebbe avere il campo %s valorizzato", costoValorizzato).isNotNull();
                        case "simpleRegisteredLetterCost" ->
                                assertThat(record.get("simpleRegisteredLetterCost")).as("Il record salvato su Pn-NotificationDeliveryCost dovrebbe avere il campo %s valorizzato", costoValorizzato).isNotNull();
                    }
                }
            } catch (HttpStatusCodeException httpStatusCodeException) {
                if (record.get("isDeleted").bool()) {
                    assertSoftly(softly -> {
                        softly.assertThat(httpStatusCodeException.getRawStatusCode()).as("In caso di record eliminato logicamente, la get deve produrre un 404").isEqualTo(404);
                        softly.assertThat(expectedData.get("isDeleted")).as("In caso di errore 404, il flag isDeleted del record dev'essere impostato a true").isEqualToIgnoringCase("true");
                        softly.assertThat(record.get("firstAnalogCost").n()).as("Sulla tabella Pn-NotificationDeliveryCost, post annullamento notifica, il campo firstAnalogCost dovrebbe essere stato riportato a null").isNull();
                        softly.assertThat(record.get("secondAnalogCost").n()).as("Sulla tabella Pn-NotificationDeliveryCost, post annullamento notifica, il campo secondAnalogCost dovrebbe essere stato riportato a null").isNull();
                        softly.assertThat(record.get("simpleRegisteredLetterCost").n()).as("Sulla tabella Pn-NotificationDeliveryCost, post annullamento notifica, il campo simpleRegisteredLetterCost dovrebbe essere stato riportato a null").isNull();
                    });
                } else {
                    throw httpStatusCodeException;
                }
            }
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    private Map<String, AttributeValue> searchNotificationDeliveryCostRecord(int recIndex) {
        QueryResponse queryResponse = dynamoDbService.call(DynamoTableName.NOTIFICATION_DELIVERY_COST, Map.of(
                ":v_pk", AttributeValue.builder().s(sharedSteps.getNotificationIun()).build(),
                ":v_sk", AttributeValue.builder().n(String.valueOf(recIndex)).build()
        ));
        try {
            assertThat(queryResponse.items().size())
                    .as("Pn-NotificationDeliveryCost deve contenere esattamente un record per iun %s e recIndex %s", sharedSteps.getNotificationIun(), recIndex)
                    .isEqualTo(1);
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
        Map<String, AttributeValue> record = queryResponse.items().get(0);

        for (int i = 0; i < queryResponse.items().size(); i++) {
            Map<String, AttributeValue> item = queryResponse.items().get(i);
            log.info("--- Record PnNotificationDeliveryCost {} ---", i + 1);
            item.forEach((key, value) -> {
                Object val = (value.s() != null) ? value.s() :
                        (value.n() != null) ? value.n() :
                                (value.bool() != null) ? value.bool() : value.toString();
                log.info("{}: {}", key, val);
            });
        }
        return record;
    }

    @And("verifico che per l'utente {int} il popolamento dei dati su Pn-PaymentInfo sia avvenuto correttamente")
    public void checkPaymentInfoRecord(Integer recIndex) {
        try {
            FullSentNotificationV29 fsn = sharedSteps.getSentNotificationLastVersion();
            fsn.getRecipients().get(recIndex).getPayments().forEach(payment -> {
                if (payment.getPagoPa() != null) {
                    String creditorTaxId = payment.getPagoPa().getCreditorTaxId();
                    String noticeCode = payment.getPagoPa().getNoticeCode();
                    String paymentInfoPK = creditorTaxId + "##" + noticeCode;
                    Map<String, AttributeValue> record = searchPaymentInfoRecord(paymentInfoPK);
                    assertSoftly(softly -> {
                        softly.assertThat(record.get("iun").s()).as("Lo IUN del record su PaymentInfo non coincide con quanto atteso").isEqualTo(sharedSteps.getNotificationIun());
                        softly.assertThat(record.get("recIndex").n()).as("Il recIndex su PaymentInfo non coincide con quanto atteso").isEqualTo(String.valueOf(recIndex));
                        softly.assertThat(record.get("applyCost").bool()).as("L'applyCost del record su PaymentInfo non coincide con quanto atteso").isEqualTo(payment.getPagoPa().getApplyCost());
                    });
                    notificationCostPaymentResponse = notificationCostClient.getNotificationCostByPayment(creditorTaxId, noticeCode);
                    log.info("PaymentResponse:\n {}", notificationCostPaymentResponse);
                }
            });
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    private Map<String, AttributeValue> searchPaymentInfoRecord(String pk) {
        QueryResponse queryResponse = dynamoDbService.call(DynamoTableName.PAYMENT_INFO, Map.of(
                ":v_pk", AttributeValue.builder().s(pk).build()));
        try {
            assertThat(queryResponse.items().size())
                    .as("Pn-PaymentInfo deve contenere esattamente un record per iun %s", sharedSteps.getNotificationIun())
                    .isEqualTo(1);
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
        Map<String, AttributeValue> record = queryResponse.items().get(0);

        for (int i = 0; i < queryResponse.items().size(); i++) {
            Map<String, AttributeValue> item = queryResponse.items().get(i);
            log.info("--- Record PnPaymentInfo {} ---", i + 1);
            item.forEach((key, value) -> {
                Object val = (value.s() != null) ? value.s() :
                        (value.n() != null) ? value.n() :
                                (value.bool() != null) ? value.bool() : value.toString();
                log.info("{}: {}", key, val);
            });
        }
        return record;
    }

    @Then("verifico che l'API di recupero costi da Pn-PaymentInfo produca un errore quando viene richiamata passando {string}")
    public void checkRobustezzaApiRecuperoCosti(String inputParameterType) {
        AtomicBoolean apiInvocationHasFailed = new AtomicBoolean(false);
        try {
            FullSentNotificationV29 fsn = sharedSteps.getSentNotificationLastVersion();
            fsn.getRecipients().forEach(rec -> rec.getPayments().forEach(payment -> {
                if (payment.getPagoPa() != null) {
                    String creditorTaxId = payment.getPagoPa().getCreditorTaxId();
                    String noticeCode = payment.getPagoPa().getNoticeCode();
                    int expectedError = 404;
                    try {
                        switch (inputParameterType) {
                            case "creditorTaxId errato" -> {
                                creditorTaxId = "invalid";
                                expectedError = 400;
                            }
                            case "noticeCode errato" -> {
                                noticeCode = "invalid";
                                expectedError = 400;
                            }
                            case "creditorTaxId inesistente" -> creditorTaxId = creditorTaxId.replaceFirst("7", "0");
                            case "noticeCode inesistente" ->
                                    noticeCode = noticeCode.startsWith("0") ? "1" + noticeCode.substring(1) : "0" + noticeCode.substring(1);
                        }
                        log.info("Start invocazione API recupero costi con creditorTaxId={} noticeCode={}", creditorTaxId, noticeCode);
                        notificationCostClient.getNotificationCostByPayment(creditorTaxId, noticeCode);
                    } catch (HttpStatusCodeException expectedException) {
                        apiInvocationHasFailed.set(true);
                        log.info(expectedException.getMessage());
                        assertThat(expectedException.getRawStatusCode())
                                .as("L'invocazione dell'api di recupero costi con %s dovrebbe produrre un %s", inputParameterType, expectedError)
                                .isEqualTo(expectedError);
                    }
                }
            }));
            assertThat(apiInvocationHasFailed).as("L'api di recupero costi deve aver prodotto un errore").isTrue();
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @Then("verifico il comportamento dell'API di inserimento costi passando in input {string}")
    public void checkRobustezzaApiInserimentoCosti(String inputParamsType) {
        FullSentNotificationV29 fsn = sharedSteps.getSentNotificationLastVersion();
        String iun = fsn.getIun();
        NewNotificationCostRequest request = initiNewNotificationCostRequest(fsn);

        switch (inputParamsType) {
            case "iun null" -> iun = null;
            case "body null" -> request = null;
            case "body vuoto" -> request = new NewNotificationCostRequest();
            case "recIndex null" -> request.getCostRecipients().get(0).setRecIndex(null);
            case "iuv null" -> request.getCostRecipients().get(0).getPayments().get(0).setIuv(null);
            case "applyCost null" -> request.getCostRecipients().get(0).getPayments().get(0).setApplyCost(null);
            case "iun invalido" -> iun = "INVALID-IUN";
            case "iun inesistente" -> iun = "TEST-INEX-ISTE-123456-Z-1";
            case "pagamenti vuoti" -> request.getCostRecipients().get(0).setPayments(new ArrayList<>());
        }
        try {
            notificationCostClient.initializeNotificationCost(iun, request);
            assertThat(Arrays.asList("iun inesistente", "pagamenti vuoti"))
                    .as("La casistica %s dovrebbe produrre un errore", inputParamsType)
                    .contains(inputParamsType);
        } catch (HttpStatusCodeException httpStatusCodeException) {
            assertThat(httpStatusCodeException.getRawStatusCode()).as("La request con %s dovrebbe generare un errore 400", inputParamsType).isEqualTo(400);
            assertThat(Arrays.asList("iun inesistente", "pagamenti vuoti"))
                    .as("La casistica %s non dovrebbe produrre alcun errore", inputParamsType)
                    .doesNotContain(inputParamsType);
        }
    }

    private NewNotificationCostRequest initiNewNotificationCostRequest(FullSentNotificationV29 fsn) {
        NewNotificationCostRequest request = new NewNotificationCostRequest();
        request.setVat(fsn.getVat());
        request.setPaFee(fsn.getPaFee());
        request.setNotificationFeePolicy(NotificationFeePolicy.valueOf(fsn.getNotificationFeePolicy().getValue()));
        request.setPagoPaIntMode(PagoPaIntMode.valueOf(fsn.getPagoPaIntMode().getValue()));
        request.setSenderTaxId(fsn.getSenderTaxId());
        request.setSenderPaId(fsn.getSenderPaId());
        request.setCostRecipients(new ArrayList<>());
        for (int recIndex = 0; recIndex < fsn.getRecipients().size(); recIndex++) {
            RecipientCostData costData = new RecipientCostData();
            costData.setRecIndex(recIndex);
            costData.setRecipientInternalId(fsn.getRecipients().get(recIndex).getTaxId());
            costData.setPayments(new ArrayList<>());
            for (int paymentIndex = 0; paymentIndex < fsn.getRecipients().get(recIndex).getPayments().size(); paymentIndex++) {
                PagoPaPayment pagoPaPayment = fsn.getRecipients().get(recIndex).getPayments().get(paymentIndex).getPagoPa();
                if (pagoPaPayment != null) {
                    PaymentData paymentData = new PaymentData();
                    paymentData.setApplyCost(fsn.getRecipients().get(recIndex).getPayments().get(paymentIndex).getPagoPa().getApplyCost());
                    String creditorTaxId = pagoPaPayment.getCreditorTaxId();
                    String noticeCode = pagoPaPayment.getNoticeCode();
                    String iuv = creditorTaxId + "##" + noticeCode;
                    paymentData.setIuv(iuv);
                    costData.getPayments().add(paymentData);
                }
            }
            if (!costData.getPayments().isEmpty()) {
                request.getCostRecipients().add(costData);
            }
        }
        return request.getCostRecipients().isEmpty() ? null : request;
    }

    @And("verifico che i valori restituiti dalle nuove api di recupero costi per l'utente {int} coincidano con quelli restituiti da delivery-push")
    public void recuperoIDatiDiCostoNotificaDaDeliveryPush(int recIndex, Map<String, String> expectedData) {
        try {
            checkPaymentInfoRecord(recIndex);

            notificationCostRecipientResponse = notificationCostClient.getNotificationCost(sharedSteps.getNotificationIun(), recIndex);
            log.info("NotificationCostRecipientResponse:\n {}", notificationCostRecipientResponse);

            String feePolicy = expectedData.get("feePolicy");
            boolean applyCost = expectedData.get("applyCost").equalsIgnoreCase("SI");
            int paFee = Integer.parseInt(expectedData.get("paFee"));
            int vat = Integer.parseInt(expectedData.get("vat"));

            NotificationProcessCostResponse notificationProcessCostResponse = sharedSteps.getB2bClient().getNotificationProcessCost(
                    sharedSteps.getNotificationIun(),
                    recIndex,
                    feePolicy,
                    applyCost,
                    paFee,
                    vat
            );
            log.info("NotificationProcessCost response:\n {}", notificationProcessCostResponse);

            int partialCost = notificationProcessCostResponse.getPartialCost();
            int analogCost = notificationProcessCostResponse.getAnalogCost();
            int totalCost = notificationProcessCostResponse.getTotalCost();
            int sendFee = notificationProcessCostResponse.getSendFee();

            assertSoftly(softly -> {
                softly.assertThat(totalCost).as("Il costo totale della response di delivery-push non coincide col costo totale comprensivo di iva").isEqualTo(notificationCostRecipientResponse.getTotalCost().getCostWithVat());
                if (notificationCostPaymentResponse != null) {
                    softly.assertThat(notificationCostPaymentResponse.getTotalCost().getDetails()).as("I costi totali restituiti dalle api di recupero costi non coincidono").isEqualTo(notificationCostRecipientResponse.getTotalCost().getDetails());
                    softly.assertThat(partialCost).as("Il partial cost della response non coincide col costo parziale").isEqualTo(notificationCostPaymentResponse.getPartialCost().getCost());
                }
                if (feePolicy.equals("DELIVERY_MODE")) {
                    softly.assertThat(partialCost).as("In caso di feePolicy=DELIVERY_RATE, il costo parziale della notifica restituito da delivery-push dev'essere pari alla somma di analogCost e sendFee").isEqualTo(analogCost + sendFee);
                } else if (feePolicy.equals("FLAT_RATE")) {
                    softly.assertThat(totalCost).as("In caso di feePolicy=FLAT_RATE, il costo totale della notifica restituito da delivery-push dev'essere pari a 0").isEqualTo(0);
                    softly.assertThat(partialCost).as("In caso di feePolicy=FLAT_RATE, il costo parziale della notifica restituito da delivery-push dev'essere pari a 0").isEqualTo(0);
                }
            });
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }
}
