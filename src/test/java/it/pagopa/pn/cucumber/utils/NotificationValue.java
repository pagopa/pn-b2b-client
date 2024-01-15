package it.pagopa.pn.cucumber.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

public enum NotificationValue {

    SUBJECT("subject","invio notifica con cucumber",true),
    CANCELLED_IUN("cancelledIun",null,false),//regex
    GROUP("group",null,false),
    IDEMPOTENCE_TOKEN("idempotenceToken",null,false),
    ABSTRACT("abstract","Abstract della notifica",false),
    SENDER_DENOMINATION("senderDenomination","Comune di milano",false),
    SENDER_TAX_ID("senderTaxId",null,false),
    PA_PROTOCOL_NUMBER("paProtocolNumber","",true),
    NOTIFICATION_FEE_POLICY("feePolicy","FLAT_RATE",false),
    PHYSICAL_COMMUNICATION_TYPE("physicalCommunication","REGISTERED_LETTER_890",false),
    DOCUMENT("document","classpath:/sample.pdf",false),
    DOCUMENT_1("document_1",null,false),
    DOCUMENT_2("document_2",null,false),
    TAXONOMY_CODE("taxonomyCode","010202N",false),
    AMOUNT("amount",null,false),
    PA_FEE("paFee",null,false),
    PAYMENT_EXPIRATION_DATE("paymentExpirationDate", null,false),

    /*RECIPIENT*/
    DENOMINATION("denomination","Mario Cucumber",false),
    TAX_ID("taxId",null,false),
    INTERNAL_ID("internalId","",false),
    DIGITAL_DOMICILE("digitalDomicile","",false),
    DIGITAL_DOMICILE_TYPE("digitalDomicile_type","PEC",false),
    DIGITAL_DOMICILE_ADDRESS("digitalDomicile_address","pectest@pec.pagopa.it",false),
    PHYSICAL_ADDRES("physicalAddress","",false),
    PHYSICAL_ADDRESS_ADDRESS("physicalAddress_address","Via senza nome",false),
    PHYSICAL_ADDRESS_MUNICIPALITY("physicalAddress_municipality","Cosenza",false),
    PHYSICAL_ADDRESS_MUNICIPALITYDETAILS("physicalAddress_municipalityDetails","Cosenza",false),
    PHYSICAL_ADDRESS_AT("at","Presso",false),
    PHYSICAL_ADDRESS_DETAILS("physicalAddress_addressDetails","scala b",false),
    PHYSICAL_ADDRESS_PROVINCE("physicalAddress_province","CS",false),
    PHYSICAL_ADDRESS_STATE("physicalAddress_State","ITALIA",false),
    PHYSICAL_ADDRESS_ZIP("physicalAddress_zip","87100",false),
    RECIPIENT_TYPE("recipientType","PF",false),
    PAYMENT("payment","",false),
    PAYMENT_MULTY("payment_multy","",false),
    PAYMENT_MULTY_NUMBER("payment_multy_number","1",false),
    PAYMENT_CREDITOR_TAX_ID("payment_creditorTaxId","77777777777",false),
    PAYMENT_NOTICE_CODE("payment_noticeCode","",true),
    PAYMENT_NOTICE_CODE_OPTIONAL("payment_noticeCodeOptional","",true),

    PAYMENT_PAGOPA_FORM("payment_pagoPaForm","classpath:/AvvisoPagoPA.pdf",false),
    PAYMENT_PAGOPA_FORM_1("payment_pagoPaForm_1","classpath:/AvvisoPagoPA.pdf",false),
    PAYMENT_PAGOPA_NOTICE_DUPLICATE("notice_duplicate",null,false),

    PAYMENT_F24("payment_f24","classpath:/Metadati_F24.json",false), //NON USATO ??
    PAYMENT_F24_1("payment_f24_1","classpath:/Metadati_F24.json",false), //NON USATO ??


    PAYMENT_F24_FLAT("payment_f24flatRate","classpath:/METADATA_CORRETTO_FLAT.json",false),
    PAYMENT_F24_STANDARD("payment_f24standard","classpath:/METADATA_CORRETTO.json",false),
    PAYMENT_F24_STANDARD_PG("payment_f24standard_pg","classpath:/METADATA_CORRETTO_PG.json",false),
    PAYMENT_F24_STANDARD_EXCISE_PG("payment_f24_standard_excise_pg","classpath:/test_excise_pg.json",false),
    PAYMENT_F24_STANDARD_NO_VALID_FORMAT("payment_f24standard_no_valid_format","classpath:/METADATA_CORRETTO_NO_VALID_FORMAT.json",false),
    PAYMENT_F24_STANDARD_NO_VALID_LENGH("payment_f24standard_no_valid_lengh","classpath:/METADATA_CORRETTO_NO_VALID_LENGH.json",false),
    PAYMENT_F24_STANDARD_VALID_ANAG("payment_f24standard_valid_anag","classpath:/METADATA_CORRETTO_VALID_103.json",false),
    PAYMENT_F24_STANDARD_NO_VALID_ANAG("payment_f24standard_no_valid_anag","classpath:/METADATA_CORRETTO_VALID_104.json",false),
    PAYMENT_F24_STANDARD_NO_VALID_ANAG_1("payment_f24standard_no_valid_anag_1","classpath:/METADATA_CORRETTO_VALID_105.json",false),
    PAYMENT_F24_STANDARD_NO_VALID_ANAG_2("payment_f24standard_no_valid_anag_2","classpath:/METADATA_CORRETTO_VALID_106.json",false),
    PAYMENT_F24_STANDARD_NO_VALID_ANAG_3("payment_f24standard_no_valid_anag_3","classpath:/METADATA_CORRETTO_VALID_107.json",false),
    PAYMENT_F24_STANDARD_NO_VALID_ANAG_4("payment_f24standard_no_valid_anag_4","classpath:/METADATA_CORRETTO_VALID_108.json",false),
    PAYMENT_F24_STANDARD_NO_VALID_ANAG_5("payment_f24standard_no_valid_anag_5","classpath:/test_invalid_metadata.json",false),



    PAYMENT_F24_STANDARD_INPS("payment_f24standard_inps","classpath:/f24_delivery_standard_inps.json",false),
    PAYMENT_F24_STANDARD_INPS_DEBIT_CREDIT("payment_f24standard_inps_debit_credit","classpath:/f24_delivery_standard_inps_debit_credit.json",false),
    PAYMENT_F24_STANDARD_INPS_DEBIT_CREDIT_1("payment_f24standard_inps_debit_credit_1","classpath:/f24_delivery_standard_inps_debit_credit_1.json",false),
    PAYMENT_F24_STANDARD_INPS_ERR("payment_f24standard_inps_err","classpath:/f24_delivery_standard_inps_err.json",false),
    PAYMENT_F24_STANDARD_INPS_ERR1("payment_f24standard_inps_err1","classpath:/f24_delivery_standard_inps_err1.json",false),
    PAYMENT_F24_STANDARD_LOCAL("payment_f24standard_local","classpath:/f24_delivery_standard_local.json",false),
    PAYMENT_F24_STANDARD_LOCAL_TARI("f24_delivery_standard_local_tefa","classpath:/f24_delivery_standard_local_tefa.json",false),

    PAYMENT_F24_STANDARD_REGION("payment_f24standard_region","classpath:/f24_delivery_standard_region.json",false),
    PAYMENT_F24_STANDARD_TREASURY("payment_f24standard_treasury","classpath:/f24_delivery_standard_treasury.json",false),
    PAYMENT_F24_STANDARD_TREASURY_AE("f24_delivery_standard_treasury_ae","classpath:/f24_delivery_standard_treasury_ae.json",false),
    PAYMENT_F24_STANDARD_SOCIAL("payment_f24standard_social","classpath:/f24_delivery_standard_social.json",false),
    PAYMENT_F24_SIMPLIFIED("payment_f24_simplified","classpath:/f24_delivery_simplified.json",false),
    PAYMENT_F24_SIMPLIFIED_PG("payment_f24_simplified_pg","classpath:/test_simplified_pg.json",false),
    PAYMENT_F24_SIMPLIFIED_1("payment_f24_simplified_1","classpath:/f24filenuovo.json",false),
    PAYMENT_F24_SIMPLIFIED_ERR1("payment_f24_simplified_err1","classpath:/test01.json",false),
    PAYMENT_F24_SIMPLIFIED_ERR2("payment_f24_simplified_err2","classpath:/test02.json",false),
    PAYMENT_F24_SIMPLIFIED_ERR3("payment_f24_simplified_err3","classpath:/test03.json",false),
    PAYMENT_F24_SIMPLIFIED_ERR4("payment_f24_simplified_err3","classpath:/test03.json",false),


    PAYMENT_F24_STANDARD_INPS_FLAT("payment_f24standard_inps_flat","classpath:/f24_flat_standard_inps.json",false),
    PAYMENT_F24_STANDARD_LOCAL_FLAT("payment_f24standard_local_flat","classpath:/f24_flat_standard_local.json",false),
    PAYMENT_F24_STANDARD_REGION_FLAT("payment_f24standard_region_flat","classpath:/f24_flat_standard_region.json",false),
    PAYMENT_F24_STANDARD_TREASURY_FLAT("payment_f24standard_treasury_flat","classpath:/f24_flat_standard_treasury.json",false),
    PAYMENT_F24_STANDARD_TREASURY_AE_FLAT("f24_delivery_standard_treasury_ae_flat","classpath:/f24_flat_delivery_standard_treasury_ae.json",false),
    PAYMENT_F24_STANDARD_TREASURY_AE_ERR_FLAT("f24_delivery_standard_treasury_ae_err_flat","classpath:/f24_flat_delivery_standard_treasury_ae_err.json",false),
    PAYMENT_F24_STANDARD_SOCIAL_FLAT("payment_f24standard_social_flat","classpath:/f24_flat_standard_social.json",false),
    PAYMENT_F24_SIMPLIFIED_FLAT("payment_f24_simplified_flat","classpath:/f24_flat_simplified.json",false),


    PAYMENT_APPLY_COST_PAGOPA("apply_cost_pagopa","NO",false),
    PAYMENT_APPLY_COST_F24("apply_cost_f24","NO",false),
    TITLE_PAYMENT("title_payment","F24",false),

    /*TIMELINE DETAILS*/
    PROGRESS_INDEX("progressIndex", "-1", false),
    POLLING_TIME("pollingTime", null, false),
    NUM_CHECK("numCheck", null, false),
    LOAD_TIMELINE("loadTimeline", "false", false),
    IS_FIRST_SEND_RETRY("isFirstSendRetry", "false", false),
    LEGAL_FACT_IDS("legalFactsIds", null, false),
    DETAILS("details", null, false),
    DETAILS_REC_INDEX("details_recIndex",null,false),
    DETAILS_DIGITAL_ADDRESS("details_digitalAddress",null,false),
    DETAILS_REFUSAL_REASONS("details_refusalReasons", null, false),
    DETAILS_GENERATED_AAR_URL("details_generatedAarUrl", null, false),
    DETAILS_RESPONSE_STATUS("details_responseStatus", null, false),
    DETAILS_DIGITAL_ADDRESS_SOURCE("details_digitalAddressSource", null, false),
    DETAILS_RETRY_NUMBER("details_retryNumber", "0", false),
    DETAILS_SENT_ATTEMPT_MADE("details_sentAttemptMade", "0", false),
    DETAILS_SENDING_RECEIPT("details_sendingReceipts", null, false),
    DETAILS_IS_AVAILABLE("details_isAvailable", null, false),
    DETAILS_DELIVERY_DETAIL_CODE("details_deliveryDetailCode", null, false),
    DETAILS_DELIVERY_FAILURE_CAUSE("details_deliveryFailureCause", null, false),
    DETAILS_ATTACHMENTS("details_attachments", null, false),
    DETAILS_PHYSICALADDRESS("details_physicalAddress", null, false),
    DETAILS_ANALOG_COST("details_analogCost", null, false),
    DETAILS_DELEGATE_INFO("details_delegateInfo", null, false),
    PAGOPAINTMODE("pagoPaIntMode","NONE",false);

    private static final String NULL_VALUE = "NULL";
    public static final String EXCLUDE_VALUE = "NO";
    private static final Integer NOTICE_CODE_LENGTH = 18;

    public final String key;
    private final String defaultValue;
    private final boolean addCurrentTime;
    private static final ObjectMapper mapper = new ObjectMapper();



    NotificationValue(String key, String defaultValue, boolean addCurrentTime){
        this.key = key;
        this.defaultValue = defaultValue;
        this.addCurrentTime = addCurrentTime;
    }



    public static String getDefaultValue(String key) {
        NotificationValue notificationValue =
                Arrays.stream(NotificationValue.values()).filter(value -> value.key.equals(key)).findFirst().orElse(null);


        return (notificationValue == null ? null : (notificationValue.addCurrentTime? (notificationValue.defaultValue + generateRandomNumber() ) : notificationValue.defaultValue));

        /*
        String number = threadNumber.length() < 2 ? "0"+threadNumber: threadNumber.substring(0, 2);
        return (notificationValue == null ? null : (notificationValue.addCurrentTime? (notificationValue.defaultValue + (""+String.format("302"+number+"%13d",System.currentTimeMillis()))) : notificationValue.defaultValue));
         */
    }

    public static String  generateRandomNumber(){
        String threadNumber = (Thread.currentThread().getId()+"");
        String numberOfThread = threadNumber.length() < 2 ? "0"+threadNumber: threadNumber.substring(0, 2);
        String timeNano = System.nanoTime()+"";
        String randomClassePagamento = new Random().nextInt(14)+"";
        randomClassePagamento = randomClassePagamento.length() < 2 ? "0"+randomClassePagamento : randomClassePagamento;
        String finalNumber = "" + String.format("302" +randomClassePagamento + numberOfThread + timeNano.substring(0, timeNano.length()-4));
       // String finalNumber = "" + String.format("30210" +randomClassePagamento + numberOfThread + timeNano.substring(0, timeNano.length()-6));
        if(finalNumber.length() > NOTICE_CODE_LENGTH){
            finalNumber = finalNumber.substring(0,NOTICE_CODE_LENGTH);
        }else{
            int remainingLength = NOTICE_CODE_LENGTH - finalNumber.length();
            String paddingString = String.valueOf(new Random().nextInt(9)).repeat(remainingLength);
            finalNumber = finalNumber + paddingString;
        }
        return finalNumber;
    }

    public static String getValue(Map<String, String> data, String key){
        if(data.containsKey(key)){
            /* TEST
            if(data.get(key).equals(EXCLUDE_VALUE)){
                return EXCLUDE_VALUE;
            }
             */
            return data.get(key).equals(NULL_VALUE) ? null : (data.get(key).contains("_CHAR")? getCharSeq(data.get(key)):data.get(key));
        }else{
            return getDefaultValue(key);
        }
    }

    public static String getCharSeq(String request){
        StringBuilder result = new StringBuilder();
        Integer number = Integer.parseInt(request.substring(0,request.indexOf("_")));
        for(int i = 0; i < number; i++){
            result.append("a");
        }
        return result.toString();
    }

    public static <T> T getCastedDefaultValue(String key) {
        NotificationValue notificationValue =
                Arrays.stream(NotificationValue.values()).filter(value -> value.key.equals(key)).findFirst().orElse(null);
        return notificationValue == null ? null : (T) notificationValue.defaultValue;
    }

    public static <T> T getObjValue(Class<T> clazz, Map<String, String> data, String key) throws JsonProcessingException {
        if(data.containsKey(key)){
            T map = mapper.readValue(data.get(key), clazz);
            return data.get(key).equals(NULL_VALUE) ? null : map;
        }else{
            return getCastedDefaultValue(key);
        }
    }

    public static <T> List<T> getListValue(Class<T> clazz, Map<String, String> data, String key) throws JsonProcessingException {
        if(data.containsKey(key)){
            JavaType type = mapper.getTypeFactory().constructParametricType(List.class, clazz);
            List<T> map = mapper.readValue(data.get(key), type);
            return data.get(key).equals(NULL_VALUE) ? null : map;
        }else{
            return getCastedDefaultValue(key);
        }
    }

}