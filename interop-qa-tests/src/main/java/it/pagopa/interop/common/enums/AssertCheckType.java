package it.pagopa.interop.common.enums;

public enum AssertCheckType {
    NONE,                 // Nessun valore atteso
    PRESENT,              // Valore presente
    PRESENT_AND_MATCHING, // Valore presente e corrispondente al valore espettato
    EXPECTED_NOT_PRESENT           // Expected non presente in actual
}
