package it.pagopa.interop.attribute.service.enums;

public enum AttributeRequestType {
    VALID,
    INVALID_ID,
    NULL_ID;

    public static AttributeRequestType fromString(String input) {
        return switch (input.toLowerCase()) {
            case "valido" -> VALID;
            case "invalido" -> INVALID_ID;
            case "nullo", "null" -> NULL_ID;
            default -> throw new IllegalArgumentException("Tipo non valido per AttributeRequestType: " + input);
        };
    }
}
