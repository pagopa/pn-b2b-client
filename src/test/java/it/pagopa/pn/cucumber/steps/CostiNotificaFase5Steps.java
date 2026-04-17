package it.pagopa.pn.cucumber.steps;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV28;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.PagoPaPayment;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.notificationcostservice.model.*;
import it.pagopa.pn.client.b2b.pa.service.IPnNotificationCostClient;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.AwsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.HttpStatusCodeException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@Slf4j
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CostiNotificaFase5Steps {

    private final SharedSteps sharedSteps;
    private final IPnNotificationCostClient notificationCostClient;

    @Autowired
    public CostiNotificaFase5Steps(SharedSteps sharedSteps, IPnNotificationCostClient notificationCostClient) {
        this.sharedSteps = sharedSteps;
        this.notificationCostClient = notificationCostClient;
    }

    @ParameterType("la presenza|il mancato inserimento")
    public boolean isInTimeline(String value) {
        return value.equals("la presenza");
    }

    @And("verifico che per il destinatario {int} il record su Pn-NotificationDeliveryCost sia stato (inserito)(modificato) e correttamente valorizzato")
    public void checkNotificationDeliveryCostRecord(int recIndex, Map<String, String> expectedData) {
        Map<String, AttributeValue> record = searchNotificationDeliveryCostRecord(recIndex);
        sharedSteps.setNotificationIun("ZHEJ-XTNL-MUJX-202604-K-1");//TODO REMOVE, example
        FullSentNotificationV28 fsn = sharedSteps.getSentNotificationLastVersion();
        //verifica che tutte le colonne siano valorizzate in modo coerente
        assertSoftly(softly -> {
//            softly.assertThat(record.get("senderInternalId").s()).as("Il senderPaId del record non coincide con quello della fullSentNotification").isEqualTo(fsn.getSenderPaId());
//            softly.assertThat(record.get("senderTaxId").s()).as("Il senderTaxId del record non coincide con quello della fullSentNotification").isEqualTo(fsn.getSenderTaxId());
            softly.assertThat(record.get("notificationFeePolicy").s()).as("Il notificationFeePolicy del record non coincide con quello della fullSentNotification").isEqualTo(fsn.getNotificationFeePolicy().getValue());
            softly.assertThat(record.get("pagoPaIntMode").s()).as("Il pagoPaIntMode del record non coincide con quello della fullSentNotification").isEqualTo(fsn.getPagoPaIntMode().getValue());
            softly.assertThat(record.get("vat").n()).as("Il campo vat del record non coincide con quello della fullSentNotification").isEqualTo(fsn.getVat());
        });
        //se il record non è eliminato logicamente (isDeleted=true), recupera tramite API l'oggetto corrispondente al record per i check sui campi relativi ai costi;
        //in caso contrario, verifica che la get puntuale produca un errore 404
        try {
            NotificationCostRecipientResponse notificationCostRecipientResponse = notificationCostClient.getNotificationCost(sharedSteps.getNotificationIun(), recIndex);
            //TODO: aggiungere controllo sui costi della response
        } catch (HttpStatusCodeException httpStatusCodeException) {
            if (record.get("isDeleted").bool()) {
                assertThat(expectedData.get("isDeleted")).as("").isEqualToIgnoringCase("true");
                assertThat(httpStatusCodeException.getRawStatusCode()).as("In caso di record eliminato logicamente, la get deve produrre un 404").isEqualTo(404);
            }
        }
    }

    private Map<String, AttributeValue> searchNotificationDeliveryCostRecord(int recIndex) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":v_pk", AttributeValue.builder().s(sharedSteps.getNotificationIun()).build());
        expressionAttributeValues.put(":v_sk", AttributeValue.builder().n(String.valueOf(recIndex)).build());

        DynamoDbClient dbClient = sharedSteps.getAwsUtils().getDynamoDbClient();
        QueryRequest queryRequest = AwsUtils.buildPnNotificationDeliveryCostRequest(expressionAttributeValues);
        QueryResponse queryResponse = dbClient.query(queryRequest);

        assertThat(queryResponse.items().size())
                .as("Pn-NotificationDeliveryCost deve contenere esattamente un record per iun %s e recIndex %s", sharedSteps.getNotificationIun(), recIndex)
                .isEqualTo(1);
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

    @And("verifico che il popolamento dei dati su Pn-PaymentInfo sia avvenuto correttamente")
    public void checkPaymentInfoRecord() {
        FullSentNotificationV28 fsn = sharedSteps.getSentNotificationLastVersion();
        Map<Integer, PagoPaPayment> paymentInfoMap = new HashMap<>();
        AtomicInteger recIndex = new AtomicInteger();
        fsn.getRecipients().forEach(rec -> {
            rec.getPayments().forEach(payment -> {
                if (payment.getPagoPa() != null) {
                    String creditorTaxId = payment.getPagoPa().getCreditorTaxId();
                    String noticeCode = payment.getPagoPa().getNoticeCode();
                    String paymentInfoPK = creditorTaxId + "##" + noticeCode;

                    paymentInfoMap.put(recIndex.intValue(), payment.getPagoPa());
                    Map<String, AttributeValue> record = searchPaymentInfoRecord(paymentInfoPK);
                    assertSoftly(softly -> {
                        softly.assertThat(record.get("iun").s()).as("Lo IUN del record su PaymentInfo non coincide con quanto atteso").isEqualTo(sharedSteps.getNotificationIun());
                        softly.assertThat(record.get("recIndex").n()).as("Il recIndex del record su PaymentInfo non coincide con quanto atteso").isEqualTo(recIndex.intValue());
                        softly.assertThat(record.get("applyCost").bool()).as("L'applyCost del record su PaymentInfo non coincide con quanto atteso").isEqualTo(payment.getPagoPa().getApplyCost());
                    });
                    NotificationCostPaymentResponse paymentResponse = notificationCostClient.getNotificationCostByPayment(creditorTaxId, noticeCode);
                }
            });
            recIndex.getAndIncrement();
        });
    }

    private Map<String, AttributeValue> searchPaymentInfoRecord(String pk) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":v_pk", AttributeValue.builder().s(pk).build());

        DynamoDbClient dbClient = sharedSteps.getAwsUtils().getDynamoDbClient();
        QueryRequest queryRequest = AwsUtils.buildPnPaymentInfoRequest(expressionAttributeValues);
        QueryResponse queryResponse = dbClient.query(queryRequest);

        assertThat(queryResponse.items().size())
                .as("Pn-PaymentInfo deve contenere esattamente un record per iun %s", sharedSteps.getNotificationIun())
                .isEqualTo(1);
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
        FullSentNotificationV28 fsn = sharedSteps.getSentNotificationLastVersion();
        fsn.getRecipients().forEach(rec -> rec.getPayments().forEach(payment -> {
            if (payment.getPagoPa() != null) {
                String creditorTaxId = payment.getPagoPa().getCreditorTaxId();
                String noticeCode = payment.getPagoPa().getNoticeCode();
                try {
                    switch (inputParameterType) {
                        case "creditorTaxId errato" -> creditorTaxId = "invalid";
                        case "noticeCode errato" -> noticeCode = "invalid";
                        case "creditorTaxId inesistente" -> creditorTaxId = creditorTaxId.replaceFirst("7", "0");
                        case "noticeCode inesistente" ->
                                noticeCode = noticeCode.startsWith("0") ? "1" + noticeCode.substring(1) : "0" + noticeCode.substring(1);
                    }
                    log.info("Start invocazione API recupero costi con creditorTaxId=%s noticeCode=%s", creditorTaxId, noticeCode);
                    NotificationCostPaymentResponse paymentResponse = notificationCostClient.getNotificationCostByPayment(creditorTaxId, noticeCode);
                } catch (HttpStatusCodeException httpStatusCodeException) {
                    log.info(httpStatusCodeException.getMessage());
                    assertThat(httpStatusCodeException.getRawStatusCode()).as("L'invocazione dell'api di recupero costi con %s dovrebbe produrre un 404").isEqualTo(404);
                }
            }
        }));
    }

    @Then("verifico il comportamento dell'API di inserimento costi passando in input {string}")
    public void verificoIlComportamentoDellAPIDiInserimentoCostiPassandoInInput(String inputParamsType) {
        FullSentNotificationV28 fsn = sharedSteps.getSentNotificationLastVersion();
        String iun = fsn.getIun();
        NewNotificationCostRequest request = initiNewNotificationCostRequest(fsn);

        switch (inputParamsType) {
            case "body null" -> request = null;
            case "body vuoto" -> request = new NewNotificationCostRequest();
            case "pagamenti vuoti" -> request.getCostRecipients().get(0).setPayments(new ArrayList<>());
            case "recIndex null" -> request.getCostRecipients().get(0).setRecIndex(null);
            case "iuv null" -> request.getCostRecipients().get(0).getPayments().get(0).setIuv(null);
            case "applyCost null" -> request.getCostRecipients().get(0).getPayments().get(0).setApplyCost(null);
            case "iun invalido" -> iun = "INVALID IUN";
            case "iun null" -> iun = null;
            case "iun inesistente" -> iun = "TEST-INEX-ISTE-123456-Z-1";
        }
        try {
            notificationCostClient.initializeNotificationCost(iun, request);
            assertThat(inputParamsType).as("").isEqualTo("iun inesistente");
        } catch (HttpStatusCodeException httpStatusCodeException) {
            assertThat(httpStatusCodeException.getRawStatusCode()).as("").isEqualTo(400);
        }
    }

    @And("TODO_REMOVE invoco l'api di inizializzazione dati")
    public void todo_removeInvocoLApiDiInizializzazioneDati() {
        FullSentNotificationV28 fsn = sharedSteps.getSentNotificationLastVersion();
        String iun = fsn.getIun();
        NewNotificationCostRequest request = initiNewNotificationCostRequest(fsn);
        String response = notificationCostClient.initializeNotificationCost(iun, request);
    }

    private NewNotificationCostRequest initiNewNotificationCostRequest(FullSentNotificationV28 fsn) {
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
            request.getCostRecipients().add(costData);
        }
        return request;
    }
}
