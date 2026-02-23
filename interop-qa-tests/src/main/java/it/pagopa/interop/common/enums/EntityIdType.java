package it.pagopa.interop.common.enums;

public enum EntityIdType {
    NULL_ID,
    INVALID_ID,
    VALID_ID,
    NON_EXISTENT_ID;

    public static EntityIdType fromString(String input) {
        return switch (input.toLowerCase()) {
            case "nullo","null" -> NULL_ID;
            case "inesistente" -> NON_EXISTENT_ID;
            case "invalid","invalido" -> INVALID_ID;
            case "valido","valid" -> VALID_ID;
            default -> throw new IllegalStateException("Unexpected value: " + input.toLowerCase());
        };
    }
}
