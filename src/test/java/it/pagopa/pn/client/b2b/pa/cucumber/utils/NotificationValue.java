package it.pagopa.pn.client.b2b.pa.cucumber.utils;

import java.util.Arrays;
import java.util.Map;

public enum NotificationValue {

    SUBJECT("subject","invio notifica con cucumber",true),
    CANCELLED_IUN("cancelledIun",null,false),//regex
    GROUP("group",null,false),
    IDEMPOTENCE_TOKEN("idempotenceToken",null,false),
    ABSTRACT("abstract","Abstract della notifica",false),
    SENDER_DENOMINATION("senderDenomination","Comune di milano",false),
    SENDER_TAX_ID("senderTaxId","01199250158",false),
    PA_PROTOCOL_NUMBER("paProtocolNumber","",true),
    NOTIFICATION_FEE_POLICY("feePolicy","FLAT_RATE",false),
    PHYSICAL_COMMUNICATION_TYPE("physicalCommunication","REGISTERED_LETTER_890",false),
    DOCUMENT("document","classpath:/sample.pdf",false),

    /*RECIPIENT*/
    DENOMINATION("denomination","Mario Cucumber",false),
    TAX_ID("taxId","FRMTTR76M06B715E",false),
    INTERNAL_ID("internalId","",false),
    DIGITAL_DOMICILE_TYPE("digitalDomicile_type","PEC",false),
    DIGITAL_DOMICILE_ADDRESS("digitalDomicile_address","FRMTTR76M06B715E@pnpagopa.postecert.local",false),
    PHYSICAL_ADDRES("physicalAddress","",false),
    PHYSICAL_ADDRESS_ADDRESS("physicalAddress_address","Via senza nome",false),
    PHYSICAL_ADDRESS_MUNICIPALITY("physicalAddress_municipality","Milano",false),
    PHYSICAL_ADDRESS_MUNICIPALITYDETAILS("physicalAddress_municipalityDetails","Milano",false),
    PHYSICAL_ADDRESS_AT("at","Presso",false),
    PHYSICAL_ADDRESS_PROVINCE("physicalAddress_province","MI",false),
    PHYSICAL_ADDRESS_STATE("physicalAddress_State","ITALIA",false),
    PHYSICAL_ADDRESS_ZIP("physicalAddress_zip","40100",false),
    RECIPIENT_TYPE("recipientType","PF",false),
    PAYMENT("payment","",false),
    PAYMENT_CREDITOR_TAX_ID("payment_creditorTaxId","77777777777",false),
    PAYMENT_NOTICE_CODE("payment_noticeCode","",true),
    PAYMENT_NOTICE_CODE_OPTIONAL("payment_noticeCodeOptional","",true),
    PAYMENT_PAGOPA_FORM("payment_pagoPaForm","classpath:/sample.pdf",false),
    PAYMENT_F24_FLAT("payment_f24flatRate","classpath:/sample.pdf",false),
    PAYMENT_F24_STANDARD("payment_f24standard","classpath:/sample.pdf",false);

    private static final String NULL_VALUE = "NULL";

    public final String key;
    private final String defaultValue;
    private final boolean addCurrentTime;

    NotificationValue(String key, String defaultValue, boolean addCurrentTime){
        this.key = key;
        this.defaultValue = defaultValue;
        this.addCurrentTime = addCurrentTime;
    }

    public static String getDefaultValue(String key) {
        NotificationValue notificationValue =
                Arrays.stream(NotificationValue.values()).filter(value -> value.key.equals(key)).findFirst().orElse(null);
        return (notificationValue == null ? null : (notificationValue.addCurrentTime? (notificationValue.defaultValue + (""+String.format("30201%13d",System.currentTimeMillis()))) : notificationValue.defaultValue));
    }

    public static String getValue(Map<String, String> data, String key){
        if(data.containsKey(key)){
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

}
