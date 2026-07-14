package it.pagopa.pn.cucumber.steps.correzioneTimeline;

import it.pagopa.pn.cucumber.steps.utilitySteps.Environment;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Costruisce il payload per la chiamata al consolidatore.
 * Logica pura senza stato, estratta da {@link TimelineReworkSteps} per alleggerire la classe di step:
 * il timestamp e l'ambiente vengono risolti dal chiamante e passati come parametri.
 */
final class ConsolidatoreRequestBuilder {

    static String attachmentUriFor(Environment env) {
        return switch (env) {
            case DEV -> "safestorage://PN_EXTERNAL_LEGAL_FACTS-970c9a266a3e44fa88ff66f4c3f4e5ae.pdf";
            case TEST -> "safestorage://PN_EXTERNAL_LEGAL_FACTS-243648ce692946f987b86fb72b33d98a.pdf";
            case UAT -> "safestorage://PN_EXTERNAL_LEGAL_FACTS-dd7dc6811b024202ac66044671f3e2ad.pdf";
            case HOTFIX -> "safestorage://PN_EXTERNAL_LEGAL_FACTS-31ea166ced054f63952e736f04647f0a.pdf";
        };
    }

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
        mapInfo.put("discoveredAddress", null);
        mapInfo.put("iun", iun);
        mapInfo.put("productType", inputData.getOrDefault("productType", null));
        String registeredLetterCode = inputData.getOrDefault("registeredLetterCode", "QATEST");
        mapInfo.put("registeredLetterCode", registeredLetterCode.equals("<null>") ? null : registeredLetterCode);
        mapInfo.put("statusCode", inputData.getOrDefault("statusCode", null));
        mapInfo.put("statusDateTime", validateTimestamp);
        mapInfo.put("statusDescription", "Quality assurance");
        return mapInfo;
    }

    private static String buildRequestId(String iun, String productType, String recindex, String attempt, String pcRetry) {
        if (productType.equals("RS") || productType.equals("RIS"))
            return String.format("PREPARE_SIMPLE_REGISTERED_LETTER.IUN_%s.%s.%s", iun, recindex, pcRetry);
        else
            return String.format("PREPARE_ANALOG_DOMICILE.IUN_%s.%s.%s.%s",
                    iun, recindex, attempt, pcRetry
            );
    }
}