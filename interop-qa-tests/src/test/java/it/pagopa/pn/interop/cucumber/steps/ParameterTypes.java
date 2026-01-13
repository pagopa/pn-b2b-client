package it.pagopa.pn.interop.cucumber.steps;

import io.cucumber.java.ParameterType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParameterTypes {
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
}
