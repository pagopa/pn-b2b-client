package it.pagopa.interop.eservice.service.enums;

public enum EserviceRequestType {
    VALID,
    INVALID_ID,
    NON_EXISTENT_ID;

    public static EserviceRequestType fromString(String input) {
        return switch (input.toLowerCase()) {
            case "valido" -> VALID;
            case "invalido" -> INVALID_ID;
            case "inesistente" -> NON_EXISTENT_ID;
            default -> throw new IllegalArgumentException("Tipo non valido per EserviceRequestType: " + input);
        };
    }
}

