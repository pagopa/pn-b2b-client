package it.pagopa.interop.eservice.service.enums;

public enum EServiceCheckMode {
    NONE,                // Nessun eService atteso
    PRESENT,             // Lista presente (non vuota)
    PRESENT_AND_MATCHING // Lista presente e contenente gli eService pubblicati
}

