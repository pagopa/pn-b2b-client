package it.pagopa.pn.interop.cucumber.steps.purposetemplate;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeTemplateState;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionState;

import java.util.Arrays;
import java.util.List;

public class ParameterTypesInterop {
    public enum ResourceState { VISIBLE, NOT_EXISTS, NOT_VISIBLE }

    @ParameterType("true|false|null")
    public static Boolean bool(String value) {
        return switch (value) {
            case "true" -> true;
            case "false" -> false;
            default -> null;
        };
    }

    //direttamente è per quando si tratta di avanzare un qualcosa da uno stato senza passare per gli stati intermedi
    @ParameterType("correttamente|direttamente|erroneamente")
    public static boolean correctly(String value) {
        return value.equals("correttamente");
    }

    @ParameterType("contiene|non contiene")
    public static boolean contains(String value) {
        return value.equals("contiene");
    }

    @ParameterType("creato|creata|esistente|sospeso|archiviato|inesistente")
    public static boolean exists(String value) {
        List<String> valid = Arrays.asList("creato", "creata", "esistente");
        return valid.contains(value);
    }

    @ParameterType("creato|creata|esistente|sospeso|archiviato|inesistente|invisibile")
    public static ResourceState isVisible(String value) {
        // TODO migliorare usando un'istruzione switch OPPURE un costum static method nella classe ResourceState che faccia il parsing
        if(value.equals("inesistente")) {
            return ResourceState.NOT_EXISTS;
        } else if(value.equals("invisibile")) {
            return ResourceState.NOT_VISIBLE;
        } else {
            return ResourceState.VISIBLE;
        }
    }

    /**
     * Blocco di ParameterType utilizzati per PurposeTemplate (finalità agevolata)
     */

    @ParameterType("per la prima volta|nuovamente")
    public static boolean isFirstTime(String value) {
        return !value.equals("nuovamente");
    }

    @ParameterType("entro|oltre")
    public static boolean isInRange(String value) {
        return !value.equals("oltre");
    }

    @ParameterType("DRAFT|PUBLISHED|SUSPENDED|ARCHIVED")
    public static PurposeTemplateState ptState(String state) {
        return PurposeTemplateState.fromValue(state);
    }

    @ParameterType("ACTIVE|DRAFT|SUSPENDED|REJECTED|WAITING_FOR_APPROVAL|ARCHIVED")
    public static PurposeVersionState purposeVersionState(String state) {
        return PurposeVersionState.fromValue(state);
    }

    @ParameterType("ANSWER OVER 250|NO PERSONAL DATA ANSWER|NO PURPOSE ANSWER|PERSONAL DATA CONFLICT|UPDATE WITH EXISTING TITLE")
    public static PurposeTemplateSteps.PurposeTemplateErrorTypes purposeTemplateError(String errorName) {
        return Arrays.stream(PurposeTemplateSteps.PurposeTemplateErrorTypes.values())
                .filter(type -> type.getValue().equals(errorName))
                .findFirst().orElse(null);
    }
}
