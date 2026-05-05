package it.pagopa.pn.interop.cucumber.steps;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.conf.api_profile.ApiProfile;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.EServiceState;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParameterTypes {
    public enum ApiVersion { V1, V2, V3 }
    public record ApiSpec(ApiProfile.ApiSet set, ApiVersion version) {}

    @ParameterType("positivo|negativo")
    public static boolean booleanResponse(String response) {
        return switch (response)  {
            case "positivo" -> true;
            case "negativo" -> false;
            default -> throw new IllegalStateException("Unexpected value: " + response);
        };
    }

    /* Converte un indice espresso in forma (1,2,3...) in (0,1,2...) */
    @ParameterType("[0-9]+")
    public static int collectionIndex(String value) {
        int naturalIndex = Integer.parseInt(value);
        if(naturalIndex < 1) {
            throw new IllegalArgumentException("L'indicizzazione si intende a partire da 1, ma è stato fornito " + value);
        }

        return naturalIndex - 1;
    }

    /* Converte un insieme di ruoli nel formato stringa "ruolo1|ruolo2|[...]" in una List<String> */
    @ParameterType("(?:admin|api|support|security|api,security)(?:\\|(?:admin|api|support|security|api,security))*")
    public static List<String> bffRoles(String roles) {
        List<String> out = new ArrayList<>();
        String[] split = roles.split("\\|");
        if(split.length == 0) {
            throw new IllegalStateException("Errore durante lo splitting della lista di ruoli " + roles);
        }
        Collections.addAll(out, split);
        return out;
    }

    @ParameterType("DRAFT|PUBLISHED|DEPRECATED|SUSPENDED|ARCHIVED|WAITING_FOR_APPROVAL")
    public static EServiceState eServiceState(String value) {
        return EServiceState.fromValue(value);
    }

    // 1. Definiamo i componenti base come stringhe letterali
    private static final String STATUS_REGEX = "(\\d+)";
    private static final String SET_REGEX = "(BFF|M2M)";
    private static final String VERSION_REGEX = "V(\\d)";

    // 2. Questa è la regex "atomica" con i gruppi di cattura per il parsing interno
    private static final String CAPTURING_REGEX = STATUS_REGEX + " per " + SET_REGEX + " " + VERSION_REGEX;

    // 3. Questa è la regex per l'annotazione (costruita solo con concatenazione +)
    // Usiamo i gruppi non-capturing (?:...) per la validazione di Cucumber
    private static final String SINGLE_NON_CAPTURING = "\\d+ per (?:BFF|M2M) V\\d";

    public static final String FULL_LIST_REGEX = SINGLE_NON_CAPTURING + "(?:,\\s*" + SINGLE_NON_CAPTURING + ")*";

    @ParameterType(FULL_LIST_REGEX)
    public Map<ApiSpec, Integer> apiStatuses(String input) {
        Map<ApiSpec, Integer> resultMap = new HashMap<>();

        // Il compilatore accetta CAPTURING_REGEX perché è risolta tramite concatenazione di static final
        Pattern pattern = Pattern.compile(CAPTURING_REGEX);
        String[] parts = input.split(",\\s*");

        for (String part : parts) {
            Matcher matcher = pattern.matcher(part);
            if (matcher.find()) {
                Integer status = Integer.parseInt(matcher.group(1));
                ApiProfile.ApiSet set = ApiProfile.ApiSet.valueOf(matcher.group(2).toUpperCase());
                ApiVersion version = ApiVersion.valueOf("V" + matcher.group(3));

                resultMap.put(new ApiSpec(set, version), status);
            }
        }
        return resultMap;
    }
}
