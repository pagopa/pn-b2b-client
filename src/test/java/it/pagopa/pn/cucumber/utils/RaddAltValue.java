package it.pagopa.pn.cucumber.utils;


import java.util.Arrays;
import java.util.Map;
import java.util.Random;

public enum RaddAltValue {
//physicalAddress sportello radd
    ADDRESS_RADD("address_radd","SI",false),
    ADDRESS_RADD_ROW("address_radd_row",null,false),
    ADDRESS_RADD_CAP("address_radd_cap",null,false),
    ADDRESS_RADD_CITY("address_radd_city",null,false),
    ADDRESS_RADD_PROVINCE("address_radd_province",null,false),
    ADDRESS_RADD_COUNTRY("address_radd_country","ITALIA",false),

    RADD_DESCRIPTION("radd_description","sportello RADD",false),
    RADD_PHONE_NUMBER("radd_phoneNumber",null,false),
    RADD_GEO_LOCATION("radd_geoLocation","SI",false),
    RADD_GEO_LOCATION_LATITUDINE("radd_geoLocation_latitudine","40.0000",false),
    RADD_GEO_LOCATION_LONGITUDINE("radd_geoLocation_longitudine","20.0221",false),
    RADD_OPENING_TIME("radd_openingTime","tue=8:00-13:00#",false),
    RADD_START_VALIDITY("radd_start_validity",null,false),
    RADD_END_VALIDITY("radd_end_validity",null,false),
    RADD_CAPACITY("radd_capacity",null,false),

    //valori filtro per ricerca sportello
    RADD_FILTER_LIMIT("radd_filter_limit","5",false),
    RADD_FILTER_LASTKEY("radd_filter_lastKey",null,false),
    RADD_EXTERNAL_CODE("radd_externalCode","testRadd",false),

    RADD_REQUESTID("radd_requestId","corretto",false),
    RADD_REGISTRYID("radd_registryId","corretto",false),

    RADD_UID("radd_uid","",true);


    private static final String NULL_VALUE = "NULL";

    public final String key;
    private final String defaultValue;
    private final boolean addCurrentTime;
    private static final Integer ADDRESS_LENGTH = 20;


    RaddAltValue(String key, String defaultValue, boolean addCurrentTime){
        this.key = key;
        this.defaultValue = defaultValue;
        this.addCurrentTime = addCurrentTime;
    }



    public static String getDefaultValue(String key) {
        RaddAltValue notificationValue =
                Arrays.stream(RaddAltValue.values()).filter(value -> value.key.equals(key)).findFirst().orElse(null);

        return (notificationValue == null ? null : (notificationValue.addCurrentTime? (notificationValue.defaultValue + generateRandomNumber() ) : notificationValue.defaultValue));

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

    public static String  generateRandomNumber(){
        String threadNumber = (Thread.currentThread().getId()+"");
        String numberOfThread = threadNumber.length() < 2 ? "0"+threadNumber: threadNumber.substring(0, 2);
        String timeNano = System.nanoTime()+"";
        String randomClassePagamento = new Random().nextInt(20)+"";
        String finalNumber = "" + String.format(randomClassePagamento + numberOfThread + timeNano.substring(0, timeNano.length()-4));
        // String finalNumber = "" + String.format("30210" +randomClassePagamento + numberOfThread + timeNano.substring(0, timeNano.length()-6));
        if(finalNumber.length() > ADDRESS_LENGTH){
            finalNumber = finalNumber.substring(0,ADDRESS_LENGTH);
        }else{
            int remainingLength = ADDRESS_LENGTH - finalNumber.length();
            String paddingString = String.valueOf(new Random().nextInt(9)).repeat(remainingLength);
            finalNumber = finalNumber + paddingString;
        }
        return finalNumber;
    }

}