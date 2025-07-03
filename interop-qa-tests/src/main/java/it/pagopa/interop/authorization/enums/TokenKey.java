package it.pagopa.interop.authorization.enums;

public record TokenKey(String tenantType, M2MRole role) {
    public static TokenKey of(String tenantType, M2MRole role) {
        return new TokenKey(tenantType, role);
    }
}
