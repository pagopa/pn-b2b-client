package it.pagopa.pn.interop.cucumber.steps;

import io.cucumber.java.ParameterType;

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
}
