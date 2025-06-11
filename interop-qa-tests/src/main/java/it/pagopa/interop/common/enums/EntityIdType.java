package it.pagopa.interop.common.enums;

public enum EntityIdType {
    NULL_ID,
    DEFAULT_ID,
    NON_EXISTENT_ID;

    public static EntityIdType fromString(String input) {
        return switch (input.toLowerCase()) {
            case "nullo","null","invalid","invalido" -> NULL_ID;
            case "inesistente" -> NON_EXISTENT_ID;
            default -> DEFAULT_ID;
        };
    }
}
