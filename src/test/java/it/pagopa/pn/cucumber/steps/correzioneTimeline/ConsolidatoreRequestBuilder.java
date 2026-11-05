package it.pagopa.pn.cucumber.steps.correzioneTimeline;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Costruisce il payload per la chiamata al consolidatore.
 * Logica pura senza stato, estratta da {@link TimelineReworkSteps} per alleggerire la classe di step:
 * il timestamp e l'ambiente vengono risolti dal chiamante e passati come parametri.
 */
final class ConsolidatoreRequestBuilder {

    static Map<String, Object> buildConsolidatoreMap(String iun,
                                                     Map<String, String> inputData,
                                                     String validateTimestamp,
                                                     String attachmentUri) {
        Map<String, Object> mapInfo = new HashMap<>();
        mapInfo.put("requestId", buildRequestId(
                iun,
                inputData.get("productType"),
                inputData.get("recIndex"),
                inputData.get("attemptId"),
                inputData.get("pcRetry")
        ));
        if (inputData.get("attachment_1") != null) {
            Map<String, Object> attachment = new HashMap<>();
            attachment.put("id", "1");
            attachment.put("documentType", inputData.get("attachment_1"));
            attachment.put("uri", attachmentUri);
            attachment.put("sha256", "UaMdYj7cAVO6EZTC9ddUBD7pbkG6zdEZ0LaL/3cmphU=");
            attachment.put("date", validateTimestamp);
            mapInfo.put("attachments", Collections.singletonList(attachment));
        } else {
            mapInfo.put("attachments", null);
        }
        mapInfo.put("clientRequestTimeStamp", validateTimestamp);
        mapInfo.put("deliveryFailureCause", inputData.getOrDefault("deliveryFailureCause", null));
        mapInfo.put("discoveredAddress", buildDiscoveredAddress(inputData));
        mapInfo.put("iun", iun);
        mapInfo.put("productType", inputData.getOrDefault("productType", null));
        String registeredLetterCode = inputData.getOrDefault("registeredLetterCode", "QATEST");
        mapInfo.put("registeredLetterCode", registeredLetterCode.equals("<null>") ? null : registeredLetterCode);
        mapInfo.put("statusCode", inputData.getOrDefault("statusCode", null));
        mapInfo.put("statusDateTime", validateTimestamp);
        mapInfo.put("statusDescription", "Quality assurance");
        return mapInfo;
    }

    /**
     * Indirizzo individuato dal postino (indagine in loco). La colonna {@code discoveredAddress} contiene la riga
     * dell'indirizzo e può includere una sequence del mock (es. {@code Via@OK_AR}), che verrà usata per il secondo invio.
     * Gli altri campi hanno gli stessi valori di default dell'indirizzo fisico delle notifiche di test
     * e si possono sovrascrivere con le colonne {@code discoveredAddress_<campo>}.
     * Senza la colonna (o con valore vuoto / {@code <null>}) l'evento non riporta l'indirizzo.
     */
    private static Map<String, String> buildDiscoveredAddress(Map<String, String> inputData) {
        String address = inputData.get("discoveredAddress");
        if (address == null || address.isBlank() || address.equals("<null>")) {
            return null;
        }
        Map<String, String> discoveredAddress = new HashMap<>();
        discoveredAddress.put("name", valueOrDefault(inputData, "discoveredAddress_name", "Destinatario Cucumber"));
        discoveredAddress.put("address", address);
        discoveredAddress.put("cap", valueOrDefault(inputData, "discoveredAddress_cap", "87100"));
        discoveredAddress.put("city", valueOrDefault(inputData, "discoveredAddress_city", "Cosenza"));
        discoveredAddress.put("pr", valueOrDefault(inputData, "discoveredAddress_pr", "CS"));
        discoveredAddress.put("country", valueOrDefault(inputData, "discoveredAddress_country", "ITALIA"));
        return discoveredAddress;
    }

    /**
     * Le celle vuote della DataTable arrivano come null: in quel caso si usa il valore di default.
     */
    private static String valueOrDefault(Map<String, String> inputData, String key, String defaultValue) {
        String value = inputData.get(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    /**
     * Le comunicazioni bonarie hanno lo IUN che termina con una lettera (le legali con una cifra) e usano un requestId
     * proprio, es. PREPARE_ANALOG_MESSAGE.IUN_XXXX-XXXX-XXXX-202611-H-A.RECINDEX_0.ATTEMPT_0.DELIVERYTYPE_RS.PCRETRY_0.
     */
    private static String buildRequestId(String iun, String productType, String recindex, String attempt, String pcRetry) {
        if (isInformal(iun))
            return String.format("PREPARE_ANALOG_MESSAGE.IUN_%s.%s.%s.DELIVERYTYPE_%s.%s",
                    iun, recindex, attempt == null || attempt.isBlank() ? "ATTEMPT_0" : attempt, productType, pcRetry);
        if (productType.equals("RS") || productType.equals("RIS"))
            return String.format("PREPARE_SIMPLE_REGISTERED_LETTER.IUN_%s.%s.%s", iun, recindex, pcRetry);
        else
            return String.format("PREPARE_ANALOG_DOMICILE.IUN_%s.%s.%s.%s",
                    iun, recindex, attempt, pcRetry
            );
    }

    private static boolean isInformal(String iun) {
        return iun != null && !iun.isEmpty() && Character.isLetter(iun.charAt(iun.length() - 1));
    }
}