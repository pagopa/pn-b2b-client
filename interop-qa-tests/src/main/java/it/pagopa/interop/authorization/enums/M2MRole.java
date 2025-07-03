package it.pagopa.interop.authorization.enums;

import it.pagopa.interop.authorization.service.identity.IllegalM2MRole;

import static java.util.Objects.nonNull;

public enum M2MRole {
    M2M_ADMIN, M2M;

    public static M2MRole fromValue(String m2mRole) throws IllegalM2MRole {
        if (nonNull(m2mRole)) {
            String value = m2mRole.toUpperCase();
            for (M2MRole role : M2MRole.values()) {
                if (role.name().equals(value)) {
                    return role;
                }
            }
            if("M2M-ADMIN".equals(value)) return M2M_ADMIN;
        }
        throw new IllegalM2MRole("Unsupported value '" + m2mRole + "'");
    }

    public static boolean isM2MRole(String role) {
        try {
            M2MRole.fromValue(role);
            return true;
        } catch (IllegalM2MRole ex) {
            return false;
        }
    }
}